
package com.t2auth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.t2health.lib1.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

//import com.t2.R;



public class AuthUtils {
    @SuppressWarnings("unused")
    
    public static final String H2_HOST = "ec2-54-245-170-242.us-west-2.compute.amazonaws.com:8081";
    public static final String H2_QUERY = H2_HOST + "/query?dbname=test&colname=h2_test&limit=20";
    public static final String H2_INSERT = H2_HOST + "/write?dbname=test&colname=h2_test";
    
    private static final String TAG = "AuthUtils";

    public static final String APPLICATION_NAME = "https://h2test";
    
    public static final String CAS_HOST = "ec2-54-245-170-242.us-west-2.compute.amazonaws.com:8443";

    private static KeyStore sKey;
    private static SSLContext sSslContext;

    private AuthUtils() {
    }
    
    public static final String getUsername(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getString(ctx.getString(R.string.pref_last_user), "");
    }

    public static final String getTicketGrantingTicket(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getString(ctx.getString(R.string.pref_tgt), null);
    }

    public static final String getServiceTicket(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getString(ctx.getString(R.string.pref_st), null);
    }
    
    public static final void clearServiceTicket(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        prefs.edit().remove(ctx.getString(R.string.pref_st)).commit();
    }
    
    public static final void clearTicketGrantingTicket(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        prefs.edit().remove(ctx.getString(R.string.pref_tgt)).commit();
    }
    
    
    public static String getRequestParams(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static SSLContext getSslContext(Context ctx) {
        InputStream in = null;

        if (sSslContext == null) {
            try {
                sSslContext = SSLContext.getInstance("TLS");
                try {
                    if (sKey == null) {
                        sKey = KeyStore.getInstance("BKS");
                        in = ctx.getResources().openRawResource(R.raw.keystore);
                        sKey.load(in, "itsatrap".toCharArray());
                    }

                    TrustManagerFactory tmf = TrustManagerFactory
                            .getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    tmf.init(sKey);
                    KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
                    kmf.init(sKey, "itsatrap".toCharArray());

                    sSslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                    return sSslContext;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } else {
            return sSslContext;
        }

        return null;
    }

    public static abstract class T2ServiceTicketTask extends AsyncTask<Void, Void, String> {

        private SSLSocketFactory mSocketFactory;

        private String mTgtToken;

        private String mAppName;

        public T2ServiceTicketTask(String appName, String tgtToken, SSLSocketFactory socketFactory) {
            mSocketFactory = socketFactory;
            mAppName = appName;
            mTgtToken = tgtToken;
        }

        @Override
        protected String doInBackground(Void... vals) {

            HttpsURLConnection conn = null;

            try {
                if (mTgtToken == null) {
                    return null;
                }

                URL url = new URL("https://" + CAS_HOST + "/cas/rest/tickets/" + mTgtToken);
                conn = (HttpsURLConnection) url.openConnection();
                conn.setSSLSocketFactory(mSocketFactory);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("service", mAppName));
                String content = AuthUtils.getRequestParams(params);
                conn.setFixedLengthStreamingMode(content.getBytes("UTF-8").length);

                OutputStream out = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(content);
                writer.close();
                out.close();

                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String st = reader.readLine();

                    return st;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String serviceTicket) {
            if (serviceTicket != null) {
                if (serviceTicket.startsWith("ST")) {
                    onTicketRequestSuccess(serviceTicket);
                } else {
                    onTicketRequestFailed();
                }
            } else {
                onTicketRequestFailed();
            }
        }

        protected abstract void onTicketRequestSuccess(String serviceTicket);

        protected abstract void onTicketRequestFailed();
    }

    public static abstract class T2LogoutTask extends AsyncTask<Void, Void, Boolean> {

        private SSLSocketFactory mSocketFactory;
        private String mTgtToken;

        public T2LogoutTask(SSLSocketFactory socketFactory, String tgtToken) {
            super();
            mSocketFactory = socketFactory;
            mTgtToken = tgtToken;
        }

        @Override
        protected Boolean doInBackground(Void... vals) {

            HttpsURLConnection conn = null;

            try {
                URL url = new URL("https://" + CAS_HOST + "/cas/rest/tickets/" + mTgtToken);
                conn = (HttpsURLConnection) url.openConnection();
                conn.setSSLSocketFactory(mSocketFactory);
                conn.setRequestMethod("DELETE");
                conn.setDoInput(true);

                if (conn.getResponseCode() == 200) {
                    return Boolean.TRUE;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return Boolean.FALSE;
        }

        protected abstract void onLogoutSuccess();

        protected abstract void onLogoutFailed();

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                onLogoutSuccess();
            } else {
                onLogoutFailed();
            }
        }
    }

    public static abstract class T2AuthenticateTask extends AsyncTask<Void, Void, String> {

        private SSLSocketFactory mSocketFactory;
        private String mUsername;
        private String mPassword;

        public T2AuthenticateTask(SSLSocketFactory socketFactory, String username, String password) {
            super();
            mSocketFactory = socketFactory;
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... vals) {

            HttpsURLConnection conn = null;

            try {
                URL url = new URL("https://" + CAS_HOST + "/cas/rest/tickets");
                conn = (HttpsURLConnection) url.openConnection();
                conn.setSSLSocketFactory(mSocketFactory);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", mUsername));
                params.add(new BasicNameValuePair("password", mPassword));
                String content = AuthUtils.getRequestParams(params);
                conn.setFixedLengthStreamingMode(content.getBytes("UTF-8").length);

                OutputStream out = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(content);
                writer.close();
                out.close();

                if (conn.getResponseCode() == 201) {
                    String location = conn.getHeaderField("location");
                    String tgt = location.substring(location.lastIndexOf('/') + 1);
                    return tgt;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return null;
        }

        protected abstract void onAuthenticationSuccess(String ticketGrantingTicket);

        protected abstract void onAuthenticationFailed();

        @Override
        protected void onPostExecute(final String ticketGrantingTicket) {
            if (ticketGrantingTicket != null && ticketGrantingTicket.startsWith("TGT")) {
                onAuthenticationSuccess(ticketGrantingTicket);
            } else {
                onAuthenticationFailed();
            }
        }
    }
    
    public static abstract  class H2PostEntryTask extends AsyncTask<Void, Void, String> {

        private static final String TAG = "H2PostTask";

        private String mServiceTicket;
        private String mEntry;

        public H2PostEntryTask(String serviceTicket, String entry) {
            super();
            mServiceTicket = serviceTicket;
            mEntry = entry;
        }

        @Override
        protected String doInBackground(Void... vals) {

            HttpURLConnection conn = null;

            try {
                if (mServiceTicket == null) {
                    Log.d(TAG, "::doInBackground:" + "No Service Ticket");
                    return null;
                }
                
                URL url = new URL("http://" + H2_INSERT + "&st=" + mServiceTicket);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                OutputStream out = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));


                writer.write(mEntry);

                writer.close();
                out.close();

                if (conn.getResponseCode() == 201) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String response = reader.readLine();
                    Log.d(TAG, "::doInBackground SUCCESS:" + response);

                    return response;
                } else {
                    Log.d(TAG, "::doInBackground:" + "Insert Failed - ResponseCode = " + conn.getResponseCode() );
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return null;
        }
        
        protected abstract void onPostSuccess(String response);

        protected abstract void onPostFailed();
        

        @Override
        protected void onPostExecute(final String response) {
//            if (response != null) {
//            	onPostSuccess(response);
//            } else {
//            	onPostFailed();
//            }        	
        }
    }
    
    
    
    
}

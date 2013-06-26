package com.t2.drupalsdk;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import java.io.*;
import java.util.List;

public class ServicesClient {
	
	private static final String TAG = ServicesClient.class.getSimpleName();		

    private String mUrlString;
    public static AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();

    public ServicesClient(String server, String base) {
        this.mUrlString = server + '/' + base + '/';
        mAsyncHttpClient.setTimeout(60000);
    }

    public ServicesClient(String urlString) {
        this.mUrlString = urlString;
        mAsyncHttpClient.setTimeout(60000);
    }

    public void setCookieStore(PersistentCookieStore cookieStore) {
        mAsyncHttpClient.setCookieStore(cookieStore);
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return this.mUrlString + relativeUrl;
    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        mAsyncHttpClient.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        mAsyncHttpClient.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, JSONObject params, AsyncHttpResponseHandler responseHandler) {
        StringEntity se = null;
        try {
            se = new StringEntity(params.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        Log.d(TAG, se.toString());
        Log.d(TAG, "url = " + getAbsoluteUrl(url));
        
//        // TODO: change to debug - it's at error now simply for readability
//        HttpContext context = mAsyncHttpClient.getHttpContext();
//        CookieStore store1 = (CookieStore) context.getAttribute(ClientContext.COOKIE_STORE);
//        Log.e(TAG, "Cookies for AsyncClient = " + store1.getCookies().toString());       
        

        mAsyncHttpClient.post(null, getAbsoluteUrl(url), se, "application/json", responseHandler);
    }

    public void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        mAsyncHttpClient.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public void put(String url, JSONObject params, AsyncHttpResponseHandler responseHandler) {
        StringEntity se = null;
        try {
            se = new StringEntity(params.toString());
        } catch (UnsupportedEncodingException e) {
        	Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        mAsyncHttpClient.put(null, getAbsoluteUrl(url), se, "application/json", responseHandler);
    }
}

package com.t2.drupalsdk;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class UserServices {
	private static final String TAG = UserServices.class.getSimpleName();		

	private ServicesClient mServicesClient;

    public UserServices(ServicesClient c) {
        mServicesClient = c;
    }

    public void RegisterNewUser(String username, String password, String email,
    		AsyncHttpResponseHandler responseHandler) {
    	
        JSONObject params = new JSONObject();
        try {
            params.put("username", username);
            params.put("password", password);
            params.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mServicesClient.post("user/register", params, responseHandler);    	
    }

    public void Login(String username, String password, AsyncHttpResponseHandler responseHandler) {
        JSONObject params = new JSONObject();
        try {
            params.put("username", username);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mServicesClient.post("user/login", params, responseHandler);
    }

    public void Logout(AsyncHttpResponseHandler responseHandler) {
        mServicesClient.post("user/logout", new JSONObject(), responseHandler);
    }

    /**
     * Gets a specific drupal node
     * 
     * @param node Node to retrieve
     * @param responseHandler Handler for response
     */
    public void NodeGet( int node, AsyncHttpResponseHandler responseHandler) {
        mServicesClient.get("node/" + node, new RequestParams(), responseHandler);
    }

    /**
     * Gets all Drupal nodes
     * @param responseHandler Handler for response
     */
    public void NodeGet( AsyncHttpResponseHandler responseHandler) {
        mServicesClient.get("node/", new RequestParams(), responseHandler);
    }

    public void NodePut( String jsonString, AsyncHttpResponseHandler responseHandler) {
        JSONObject params;
		try {
			params = new JSONObject(jsonString);
	        mServicesClient.post("node", params, responseHandler);
		} catch (JSONException e) {
			Log.e(TAG,  e.toString());
			e.printStackTrace();
		}
    }
}

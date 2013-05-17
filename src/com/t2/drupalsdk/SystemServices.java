package com.t2.drupalsdk;

import com.loopj.android.http.AsyncHttpResponseHandler;
import org.json.JSONObject;

public class SystemServices {
    private ServicesClient mUserServicesClient;

    public SystemServices(ServicesClient c) {
        mUserServicesClient = c;
    }

    public void Connect(AsyncHttpResponseHandler responseHandler) {
        mUserServicesClient.post("system/connect", new JSONObject(), responseHandler);
    }
}
package org.t2health.lib1;

import com.janrain.android.engage.JREngageError;
import com.janrain.android.engage.net.async.HttpResponseHeaders;
import com.janrain.android.engage.types.JRDictionary;

public interface T2AuthDelegate {

	void T2AuthSuccess(JRDictionary auth_info, String provider, HttpResponseHeaders responseHeaders,String responsePayload);
	void T2AuthFail(JREngageError error, String provider);
	void T2AuthNotCompleted();
	
	
}

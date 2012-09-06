package org.t2health.lib1;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class T2RestPacket {
	
	private static final String TAG = "BFDemo";	
	public static final int STATUS_PENDING = 0;	// Waiting to be sent to server
	public static final int STATUS_POSTED = 1;		// Posted to server but no response received
	public static final int STATUS_RECEIVED = 2;	// Positive ack received from server
	
	
	public String mId = "nothing";
	public String mJson;
	public int mStatus;
	
	T2RestPacket(String json) {
		mJson = json;
		mStatus = STATUS_PENDING;
		
		 Pattern p = Pattern.compile("\"_id\":\"[0-9a-zA-Z-]*\"");
		 Matcher m = p.matcher(json);	
		 if (m.find()) {
				mId = m.group(0);
		
		 }
	}
	

}

/* T2AndroidLib for Signal Processing
 * 
 * Copyright © 2009-2013 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2013 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: T2AndroidLib1021
 * Government Agency Original Software Title: T2AndroidLib for Signal Processing
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package com.t2.dataouthandler;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.t2.aws.Constants;

import android.util.Log;

/**
 * @author scott.coleman
 * Encapsulates a database entry
 *
 */
public class T2RestPacket {
	
	private static final String TAG = "BFDemo";	
	public static final int STATUS_PENDING = 0;	// Waiting to be sent to server
	public static final int STATUS_POSTED = 1;		// Posted to server but no response received
	public static final int STATUS_RECEIVED = 2;	// Positive ack received from server
	
	public String mId = "nothing";
	public String mJson;
	public int mStatus;
	HashMap<String, AttributeValue> mHashMap = new HashMap<String, AttributeValue>();		
	
	T2RestPacket(String json) {
		mJson = json;
		mStatus = STATUS_PENDING;

		// This is a hokey way to getting the record id!
		// It might present itself differently depending on the database type
		Pattern p = Pattern.compile("\"record_id\":\"[0-9a-zA-Z-]*\"");
		Matcher m = p.matcher(json);	
		if (m.find()) {
			mId = m.group(0);
		}
		
		p = Pattern.compile("\"title\":\"[0-9a-zA-Z-]*\"");
		m = p.matcher(json);	
		if (m.find()) {
			mId = m.group(0);
		}
		
		AttributeValue recordId =  mHashMap.get("record_id");
		
		if (recordId != null) {
			mId = recordId.getS();
		}
	}
	
	T2RestPacket(String json, HashMap<String, AttributeValue> _hashMap) {
		mJson = json;
		mStatus = STATUS_PENDING;
		
		mHashMap = _hashMap;
		
		// This is a hokey way to getting the record id!
		// It might present itself differently depending on the database type
		Pattern p = Pattern.compile("\"record_id\":\"[0-9a-zA-Z-]*\"");
		Matcher m = p.matcher(json);	
		if (m.find()) {
			mId = m.group(0);
		}

		p = Pattern.compile("\"title\":\"[0-9a-zA-Z-]*\"");
		m = p.matcher(json);	
		if (m.find()) {
			mId = m.group(0);
		}
		
		AttributeValue recordId =  mHashMap.get("record_id");
		
		if (recordId != null) {
			mId = recordId.getS();
		}
	}
}

/* T2AndroidLib-SG for Signal Processing
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Vector;

import android.os.Build;

import com.t2.dataouthandler.DataOutHandlerTags;


public class DataOutPacket implements Serializable {

	public HashMap<String, Object> mItemsMap = new HashMap<String, Object>();
	public String mLoggingString;
	public String mId;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	public long mCurrentTime;
	
	public DataOutPacket() {
    	UUID uuid = UUID.randomUUID();
    	Calendar calendar = GregorianCalendar.getInstance();
    	mCurrentTime = calendar.getTimeInMillis();
    	dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentTimeString = dateFormatter.format(calendar.getTime());
    	mId = mCurrentTime + "-" + uuid.toString();

    	add(DataOutHandlerTags.RECORD_ID, mId);
    	add(DataOutHandlerTags.TIME_STAMP, mCurrentTime);
    	add(DataOutHandlerTags.CREATED_AT, currentTimeString);
    	add(DataOutHandlerTags.PLATFORM, "Android");		    	
    	add(DataOutHandlerTags.PLATFORM_VERSION, Build.VERSION.RELEASE);	    	
	}
	
	public void add(String tag, double value) {
		mItemsMap.put(tag, value);
	}

	public void add(String tag, double value, String format) {
		String strVal = String.format("%s:" + format + ",", tag,value);		
		mItemsMap.put(tag, strVal);
	}

	public void add(String tag, long value) {
		mItemsMap.put(tag, value);
	}

	public void add(String tag, int value) {
		mItemsMap.put(tag, value);
	}
	
	public void add(String tag, String value) {
		mItemsMap.put(tag, value);
	}

	public void add(String tag, Vector vector) {
		mItemsMap.put(tag, vector);
	}
	
	public String toString() {
		String result = "";
		   Iterator it = mItemsMap.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        result += pairs.getKey() + " = " + pairs.getValue() + ", ";
		        
		        if (pairs.getValue() instanceof Integer) {
		        	result += "{INTEGER} + ";
		        }
		        if (pairs.getValue() instanceof String) {
		        	result += "{String} + ";
		        }
		        if (pairs.getValue() instanceof Long) {
		        	result += "{Long} + ";
		        }
		        if (pairs.getValue() instanceof Double) {
		        	result += "{Long} + ";
		        }
		        if (pairs.getValue() instanceof Vector) {
		        	result += "{Vector} + ";
		        }
		        
		        
		    }		
		

		return result;
	}

}

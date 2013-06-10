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

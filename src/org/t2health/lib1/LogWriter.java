/* T2AndroidLib for Signal Processing
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
package org.t2health.lib1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

public class LogWriter {
	private static final String TAG = "BFDemo";
	
	
	private BufferedWriter mLogWriter = null;
	private String mFileName = "";
	private File mLogFile;	
	public Context mContext;

	public LogWriter(Context context) {
		mContext = context;
	}
	
	public void open(String fileName, boolean showWarning) {
		
		mFileName = fileName;
		
		try {
		    File root = Environment.getExternalStorageDirectory();
		    if (root.canWrite()){
		        mLogFile = new File(root, fileName);
		        mFileName = mLogFile.getAbsolutePath();
		        
		        FileWriter gpxwriter = new FileWriter(mLogFile, true); // open for append
		        mLogWriter = new BufferedWriter(gpxwriter);

//		        try {
//		        	if (mLogWriter != null) {
//		        		mLogWriter.write(mLogHeader + "\n");
//		        	}
//				} catch (IOException e) {
//					Log.e(TAG, e.toString());
//				}
		        
		        
		    } 
		    else {
    		    Log.e(TAG, "Cannot write to log file" );
    		    
    		    if (showWarning) {
        			AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        			alert.setTitle("ERROR");
        			alert.setMessage("Cannot write to log file");	
        			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int whichButton) {
        				}
       				});    			
        			alert.show();
    		    }
    			
		    }
		} catch (IOException e) {
		    Log.e(TAG, "Cannot write to log file" + e.getMessage());
		    if (showWarning) {
				AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
				alert.setTitle("ERROR");
				alert.setMessage("Cannot write to file");
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
				});    			
			
				alert.show();			
		    }		    
		    
		}		
		
		
	}
	
	public void close() {
    	try {
        	if (mLogWriter != null) {
        		Log.d(TAG, "Closing file");
        		mLogWriter.close();
        		mLogWriter = null;
        	}
		} catch (IOException e) {
			Log.e(TAG, "Exeption closing file " + e.toString());
			e.printStackTrace();
			mLogWriter = null;
		}    		
	}

	public void write(String line) {
        line += "\n";
		try {
        	if (mLogWriter != null)
        		mLogWriter.write(line);
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}			
	}
	
}

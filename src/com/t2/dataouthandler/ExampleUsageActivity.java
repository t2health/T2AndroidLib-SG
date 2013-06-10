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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.t2health.lib1.SharedPref;




import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * @author scott.coleman
 * 
 * This is a set up example of how to use the DataOutHandler classes.
 * It's not meant to be working code.
 *
 */
public class ExampleUsageActivity extends Activity {

	private static final String TAG = "BigBrotherService";	
	private DataOutHandler mDataOutHandler;	

	private static final String mAppId = "BigBrotherService";	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	private String sessionDate = sdf.format(new Date());
	private String userId = SharedPref.getString(this, "SelectedUser", 	"");
	private long sessionId = SharedPref.getLong(this, "bio_session_start_time", 0);
	private String appName = SharedPref.getString(this, "app_name", mAppId);

	/**
	 * Database uri that the service will sync to 
	 */
	private String mRemoteDatabaseUri = 
			"http://ec2-50-112-197-66.us-west-2.compute.amazonaws.com/mongo/json.php";	

	/**
	 * Constructor - Start up a new instance of the data output handler
	 */
	public ExampleUsageActivity() {
		// ----------------------------------------------------
		// Create a data handler to handle outputting data
		//	to files and database
		// ----------------------------------------------------		
		try {
			mDataOutHandler = new DataOutHandler(this, 
					userId,sessionDate, 
					appName, DataOutHandler.DATA_TYPE_INTERNAL_SENSOR, 
					sessionId );
			mDataOutHandler.enableLogging(this);
			mDataOutHandler.enableLogCat();
			Log.d(TAG, "Initializing DataoutHandler");
		} catch (Exception e1) {
			Log.e(TAG, e1.toString());
			e1.printStackTrace();
		}		
		
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// Initialize (and enable) the database
		Log.d(TAG, "Initializing database at " + mRemoteDatabaseUri);
		try {
			mDataOutHandler.initializeDatabase("","","","", mRemoteDatabaseUri);
		} catch (DataOutHandlerException e1) {
			e1.printStackTrace();
		}
		
		// Log the version
		try {
			PackageManager packageManager = getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(getPackageName(), 0);			
			String applicationVersion = info.versionName;
			String versionString = mAppId + 
					" application version: " + applicationVersion;

			
			DataOutPacket packet = new DataOutPacket();			
			packet.add("version", versionString);
			mDataOutHandler.handleDataOut(packet);				

		}
		catch (Exception e) {
		   	Log.e(TAG, e.toString());
		} 			
	}
	
	public void LogSomeData(String model) {

		// Log an arbitrary piece of data
		if (mDataOutHandler != null) {
			DataOutPacket packet = new DataOutPacket();			
			packet.add("MODEL", model);		
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		// We're done with the database
        mDataOutHandler.close();
	}
}
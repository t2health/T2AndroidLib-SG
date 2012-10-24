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

import java.io.File;
import java.io.IOException;

import org.t2health.lib1.dsp.T2FIRFilter;
import org.t2health.lib1.dsp.T2IIRFilter;

import android.os.Environment;
import android.util.Log;

public class Utils {
	private static final String TAG = "Utils";
	/**
	 * Clears the device logcat
	 */
	static void clearLogCat() {
        try {
		    String cmd = "logcat -c ";
		    Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			Log.e(TAG, "Error clearing logcat" + e.toString());
			e.printStackTrace();
		}			
		
	}
	
	/**
	 * Saves the current device logcat to file on external storage
	 * @param fileName - Filename to save to
	 */
	static void SaveLogCatToFile(String fileName) {
		try {
		    File filename = new File(Environment.getExternalStorageDirectory() + "/" + "Logcat_" + fileName+ ".log"); 
		    filename.createNewFile(); 
		    String cmd = "logcat -d -f "+filename.getAbsolutePath();
		    Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}			
	}

	
	void testFIRFilter() {
		// See http://www.eas.asu.edu/~midle/jdsp/jdsp.html for filter coefficient calculators
		double coefs[] = {0.00506,0.02935,0.11074,0.21934,0.27097,0.21934,0.11074,0.02935,0.00506};
		
		int testVector[] = {0,0,0,0,0,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10};
		T2FIRFilter dFilter = new T2FIRFilter(coefs);
		for (int i = 0; i < testVector.length - 1; i++) {
			double result  = dFilter.dfilter(testVector[i]);
	 		Log.i("SensorData", String.format("%d, %f", testVector[i], result));  
		}		
		
		int i = 0;
		int j = i;
		
		
	}
	void testIIRFilter() {

		double zeros[] = {1.0,-0.4747,0.3442};
		double poles[] = {0.4059, -0.8092, 0.4059};  // Scale factor used = 4116 

		int testVector[] = {0,0,0,0,0,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10};
		T2IIRFilter dFilter = new T2IIRFilter(zeros, poles, 1);
		for (int i = 0; i < testVector.length - 1; i++) {
			double result  = dFilter.dfilter(testVector[i]);
	 		Log.i("SensorData", String.format("%d, %f", testVector[i], result));  
		}		
		
		int i = 0;
		int j = i;
		
		
	}
	
	void testFilter() {
		int zeros[] = {1,3,3,1};
		int poles[] = {3846,-11781,12031,0};  // Scale factor used = 4116 

		double fPoles[] = {0.9390989403*10 , -2.8762997235*10,  2.9371707284*10, 0};

		double fZeros[] = {1, 3, 3, 1};

		
		int testVector[] = {0,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10};
		
		// Note that the result of the test must be assessed by looking at the log outputs

		// First test floating point
		T2IIRFilter dFilter = new T2IIRFilter(fZeros, fPoles, 26618129.26);	 // the 266xxx is the gain of the filter
		for (int i = 0; i < testVector.length - 1; i++) {
			double result  = dFilter.dfilter(testVector[i]);
	 		Log.i("SensorData", String.format("%d, %f", testVector[i], result));  
		}		
	}	
	
	
}

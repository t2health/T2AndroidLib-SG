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
package org.t2health.lib1.dsp;



public class T2FIRFilter extends T2Filter {

	int mSmoothingFactor;
	int mCurrentValue;
	
	int[] mCoefs; 
	int[] mDelayLine;
	double[] mdCoefs; 
	double[] mdDelayLine;
	
	int mNumCoefs;
	int mScale;

	public T2FIRFilter(double[] coefs) {
		mdCoefs = coefs;
		mNumCoefs = mdCoefs.length;
		mdDelayLine = new double[mNumCoefs];
	}
	

    public double dfilter(double inputSample) {
    
    	double result = 0;
    	
    	// First take care of the shift register
    	for (int i = mNumCoefs - 1; i > 0; i--) {
			mdDelayLine[i] = mdDelayLine[i - 1];
		}
		mdDelayLine[0] = inputSample; 


//		String t = "";
//		for (int i = 0; i < mNumCoefs; i++) {
//			t = t + mdDelayLine[i] + ", ";
//		}
//		Log.i("SensorData", t);
		
		// Now calculate based on the coefficients
		for (int i = 0; i < mNumCoefs; i++) {
			
			result += mdCoefs[i] * mdDelayLine[i];
		}
		
//		Log.i("SensorData", String.format(",Zeros: %g, %g, %g, %g, Poles: %g, %g, %g, %g", mdDelayLine[0], mdDelayLine[1], mdDelayLine[2], mdDelayLine[3], mdPoleDelayLine[0], mdPoleDelayLine[1], mdPoleDelayLine[2], mdPoleDelayLine[3])); 		
		
		return result;
    }	
	
}

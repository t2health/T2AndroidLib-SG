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

public class T2PeriodAverageFilter extends T2Filter {
	public static final int AVERAGING = Integer.MAX_VALUE;
    private int mAverage;
	private int mIndex;
	private int mSize;
	private int mTotal;
	
	
	public T2PeriodAverageFilter(int size) {
		mSize = size;
	    reset();
	}
	
	@Override
	public    int    filter( int value ) {	
		mTotal += value;

		if (mIndex++ >= mSize - 1) {
			mAverage = mTotal / mSize;
			mTotal = 0;
			mIndex = 0;
			return mAverage;
		}
		else {
			return AVERAGING;
		}
	}       
	   
	public int getAverage() {
		return mAverage;
	}
	
	
	
	public void reset() {
	    mIndex = 0;
	    mAverage = 0;
	    mTotal = 0;
	}

	

	

	
}

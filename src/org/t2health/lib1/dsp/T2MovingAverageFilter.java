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

import java.util.Arrays;

public class T2MovingAverageFilter extends T2Filter {

	private int circularBuffer[];
	
    private int mean;
    private int total;
	private int circularIndex;
	private int count;
	private int size;
	private int min;
	private int max;
	
	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
	
	public T2MovingAverageFilter(int size) {
		circularBuffer = new int[size];
	    reset();
	}
	
	@Override
	public    int    filter( int x ) {
		if (count++ == 0) {
	        primeBuffer(x);
	    }
	    int lastValue = circularBuffer[circularIndex];
	    total -= lastValue;
	    total += x;
	    mean = total / circularBuffer.length;
	    circularBuffer[circularIndex] = x;
	    circularIndex = nextIndex(circularIndex);
	    
	    // Set min and max
	    int[] tmp = circularBuffer.clone();
	    Arrays.sort(tmp);
	    min = tmp[0];
	    max = tmp[tmp.length - 1];
	    
		return mean;
	}       
	   
	public int getValue() {
		return mean;
	}
	
	public double getVariance() {
		
		int sdIndex = circularIndex;		
		long n = 0;
		double mean = 0;
		double s = 0.0;
		
	    for (int i = 0; i < circularBuffer.length; ++i) {
	    	double val = circularBuffer[sdIndex];
			++n;
			double delta = val - mean;
			mean += delta / n;
			s += delta * (val - mean);
			
			sdIndex = nextIndex(sdIndex);
						
	    }		
		return (s / n);		
	}
	
	public double getStdDev() {
		return Math.sqrt(getVariance());
	}	
	
	public void reset() {
	    count = 0;
	    circularIndex = 0;
	    mean = 0;
	    total = 0;
	}
	
	public long getCount() {
	    return count;
	}
	
	private void primeBuffer(int val) {
	    for (int i = 0; i < circularBuffer.length; ++i) {
	        circularBuffer[i] = val;
	        total += val;
	    }
	    mean = val;
	}
	
	private int nextIndex(int curIndex) {
	    if (curIndex + 1 >= circularBuffer.length) {
	        return 0;
	    }
	    return curIndex + 1;
	}	
	
}

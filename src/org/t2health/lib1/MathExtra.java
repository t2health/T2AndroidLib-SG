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

import java.util.Calendar;

public class MathExtra {
	public static double mean(double[] values) {
		double total = 0.0;
		for(double val: values) {
			total += val;
		}
		return total / values.length;
	}
	
	public static double variance(double[] values) {
		long n = 0;
		double mean = 0;
		double s = 0.0;
		
		for(double val: values) {
			++n;
			double delta = val - mean;
			mean += delta / n;
			s += delta * (val - mean);
		}
		
		return (s / n);
	}
	
	public static double stdDev(double[] values) {
		return Math.sqrt(variance(values));
	}
	
	public static void roundTime(Calendar cal, int calendarField) {
		cal.setTimeInMillis(roundTime(cal.getTimeInMillis(), calendarField));
	}
	
	public static long roundTime(long time, int calendarField) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		
		switch(calendarField) {
		case Calendar.YEAR:
			cal.set(Calendar.MONTH, Calendar.JANUARY);
		case Calendar.MONTH:
			cal.set(Calendar.DAY_OF_MONTH, 1);
		case Calendar.DAY_OF_MONTH:
		case Calendar.HOUR_OF_DAY:
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			break;
			
		}
		return cal.getTimeInMillis();
	}
	
	/**
	 * Scales data to 0-outputMax
	 * @param value - Input value to scale
	 * @param inputMax - Max practical value of input
	 * @param inputMin - Min practical value of input
	 * @param outputMax - Max value of scaled output
	 * @return
	 */
	public static float scaleData(float value, float inputMax, float inputMin, int outputMax) {
		float scaledValue = value - inputMin;
		if (scaledValue < 0) scaledValue = 0;
		scaledValue *= (outputMax / (inputMax - inputMin)); //  
		if (scaledValue > outputMax) scaledValue = outputMax;		

		return scaledValue;
	}	
	
	/**
	 * Scales data to 0-outputMax
	 * @param value - Input value to scale
	 * @param inputMax - Max practical value of input
	 * @param inputMin - Min practical value of input
	 * @param outputMax - Max value of scaled output
	 * @param gain - gain to use instead of calculated gain
	 * @return
	 */
	public static float scaleData(float value, float inputMax, float inputMin, int outputMax, float gain) {
		float scaledValue = value - inputMin;
		if (scaledValue < 0) scaledValue = 0;
		scaledValue *= gain; //  
		if (scaledValue > outputMax) scaledValue = outputMax;		

		return scaledValue;
	}	
	
}

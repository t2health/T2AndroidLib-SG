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

public class T2Filter {

	  /**
	   * Method to filter a fixed-length sequence with this filter.
	   *
	   * @param x       double[] containing the input sequence..
	   * @return        double[] containing the resulting filtered sequence.
	   */
	  public    double[]    filter( double[] x ) {
		  return null;
	  }
	
	  /**
	   * Method to filter a single-length sequence with this filter.
	   *
	   * @param x       double containing the input sequence.
	   * @return        double containing the resulting filtered sequence.
	   */
	  public    double    filter( double x ) {
		  return x;
	  }

	  
	  /**
	   * Method to filter a single-length sequence with this filter.
	   *
	   * @param x       int containing the input sequence.
	   * @return        int containing the resulting filtered sequence.
	   */
	  public    int    filter( int sampleValue ) {
		  return sampleValue;
	  }
	
	  /**
	   * Method to filter a single-length sequence with this filter.
	   *
	   * @param x       int containing the input sequence.
	   * @return        int containing the resulting filtered sequence.
	   */
	  public    int    filter( int sampleValue , int sampleTimeStamp) {
		  return sampleValue;
	  }
	
}

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

package com.oregondsp.signalProcessing.filter.iir;


import com.oregondsp.signalProcessing.filter.Polynomial;


/**
 * Designs and implements Thiran allpass filters.
 * 
 * Thiran allpass filters are used to interpolate signals by a fractional sample.  
 * They have unit amplitude response, thus have no amplitude distortion, and approximate
 * a flat group delay specified by D.  The group delay function is maximally flat at 0 Hz.
 * 
 * @author David B. Harris   Deschutes Signal Processing LLC
 *
 */
public class ThiranAllpass extends Allpass {

  
  /**
   * constructs a Thiran allpass filter.
   *
   * @param N     the order of the allpass filter, typically 3 or 4
   * @param D     the delay, in samples, best between N-1 and N
   */
  public ThiranAllpass( int N, double D ) {
    
    super( N );
    
    double[] a  = new double[N+1];
    
    a[0]        = 1.0;
    for ( int i = 1;  i <= N;  i++ ) {
      double prod = 1.0;
      for ( int n = 0;  n <= N;  n++ ) {
        prod *= ( (double)(D - N + n ) ) / ( (double) (D - N + i + n ) );
      }
      a[i] = Math.pow( -1, i ) * ( factorial(N) / ( factorial(N-i) * factorial(i) ) ) * prod;
    }
    
    Polynomial P = new Polynomial( a );
    k            = P.reflectionCoefficients();
    constructRationalRepresentation();
    
  }
    

  /**
   * Factorial function required to evaluate the coefficients of the Thiran allpass filter.
   *
   * @param n       int argument of the factorial function.
   * @return        int n!
   */
  private int factorial( int n ) {

    int retval = 1;
    if ( n > 1 )
      for ( int i = 2;  i <= n;  i++ ) retval *= i;
    
    return retval;
  }

}

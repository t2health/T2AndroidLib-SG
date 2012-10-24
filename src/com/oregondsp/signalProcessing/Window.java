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

package com.oregondsp.signalProcessing;


/**
 * Base class for implementing windows - partial implementation.
 * 
 * This class and its derived classes make it possible to "snip out" a portion of a signal 
 * beginning at a specified index and shape the resulting segment with a specified weighting
 * function.  The length of the resulting segment is the same length as the window.
 * 
 * @author David B. Harris,   Deschutes Signal Processing LLC
 */
public class Window {
  
  /** float[] containing the window coefficients. */
  protected float[] w;
  
  
  /**
   * Instantiates a new Window from a vector of coefficients.
   *
   * @param w     float[] containin the vector of window coefficients.
   */
  public Window( float[] w ) {
    this.w = w.clone();
  }
  
  
  
  /**
   * Instantiates a new length-N window containing zeros.
   *
   * @param N     int specifying the window length in samples.
   */
  public Window( int N ) {
    w = new float[ N ];
  }
  
  
  
  /**
   * Returns the length of the window in samples.
   *
   * @return    int containing the window length in samples.
   */
  public int length() { 
    return w.length;
  }
  
  
  
  /**
   * Allows a window to be modified in-place by multiplication by another window.
   *
   * @param x    float[] containing the coefficients of the second window, which modifies the first (this) window.
   */
  public void timesEquals( float[] x ) {
    if ( x.length != w.length ) throw new IllegalArgumentException( "Argument length does not match window length" );
    for ( int i = 0;  i < w.length;  i++ ) w[i] *= x[i];
  }
  
  
  
  /**
   * Returns a copy of the coefficients of this window.
   *
   * @return     float[] containing window coefficients.
   */
  public float[] getArray() { 
    return w.clone();
  }
  
  
  
  /**
   * Windows a sequence and places the result in a specified array.
   *
   * @param x          float[] containing the sequence to be windowed by this Window.
   * @param index      start point in the input sequence at which this Window is applied.
   * @param y          float[] containing the resulting windowed sequence.
   */
  public void window( float[] x, int index, float[] y ) {
    
    if ( y.length != w.length ) throw new IllegalArgumentException( "Destination array length does not match window length" );
    
    for ( int i = 0;  i < w.length;  i++ ) {
      int j = index + i;
      if ( j >= 0  &&  j < x.length ) 
        y[i] = w[i] * x[j];
      else 
        y[i] = 0.0f;
    }
    
  }

}

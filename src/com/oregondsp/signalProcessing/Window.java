/*
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

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

package com.oregondsp.signalProcessing.filter.fir;

import com.oregondsp.signalProcessing.HammingWindow;
import com.oregondsp.signalProcessing.Sequence;



/**
 * Implements a finite impulse response (FIR) interpolator for resampling sequences up by an integer rate.
 * 
 * <p>This implementation uses a windowed sinc function as the interpolation kernel.  The length of the window
 * is determined by the interpolation rate and a design factor which controls interpolation accuracy.  A
 * Hamming window is used to window the sinc function.</p>  
 * 
 * <p>The window length is N = 2*M + 1, where M = (interpolation rate)*designFactor.  The interpolation kernel
 * is: </p>
 * <p>
 * h[n] = ( 0.54 + 0.46*cos(pi/M * n) ) * sin( n*pi/R ) / ( n*pi/R )
 * </p>
 * <p>R is the interpolation rate.</p>
 * 
 * <p>This implementation assumes that the data will be processed in consecutive, contiguous blocks of uniform
 * size.  The block size is specified in the constructor for the Interpolator, and thereafter calls to interpolate()
 * will interpolate successive blocks of that size.  Very important:  the block size must not change.  State 
 * information required to assure continuous interpolation from block to block is maintained by the class instance, 
 * so that there are no edge effects in the interpolated result sequence (except at the very beginning and end of 
 * the data stream).</p>
 * 
 *  @author David B. Harris,   Deschutes Signal Processing LLC
 * 
 */
public class Interpolator {
  
  /** int containing the interpolation rate. */
  private int         rate;
  
  /** OverlapAdd instance that performs the interpolation by convolution with a windowed sinc function. */
  private OverlapAdd  overlapAdd;
  
  /** float[] that buffers the low-rate sequence to be interpolated. */
  private float[]     buffer;
  
  
  
  /**
   * Instantiates a new Interpolator.
   *
   * @param rate                int containing the interpolation rate.
   * @param designFactor        int containing parameter controlling the accuracy of interpolation
   *                            through the length of the interpolation filter
   * @param blockSize           int controlling the size of blocks in which the data will be processed
   */
  public Interpolator( int rate, int designFactor, int blockSize ) {
    
    this.rate = rate;
    
    int half = rate*designFactor;
    int N    = 2*half + 1;
    float[] kernel = ( new HammingWindow(N) ).getArray();
    for (int i = 1; i <= half; i++) {
      kernel[ half + i ] *= (float) ( Math.sin(Math.PI * i / rate) / ( Math.PI * i / rate ) );
      kernel[ half - i ]  = kernel[ half + i ];
    }

    overlapAdd = new OverlapAdd( kernel, blockSize*rate );
    
    buffer = new float[ blockSize*rate ];
  }
  
  
  
  /**
   * Interpolates blocks of a data stream.
   * 
   * 
   *
   * @param block                 float[] containing a block of the sequence to be interpolated
   * @param interpolatedBlock     float[] containing the interpolated block
   */
  public void interpolate( float[] block, float[] interpolatedBlock ) {
    Sequence.stretch( block, rate, buffer );
    overlapAdd.filter( buffer, 0, interpolatedBlock, 0 );
  }
  
}

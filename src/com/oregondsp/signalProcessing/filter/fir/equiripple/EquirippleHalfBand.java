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

package com.oregondsp.signalProcessing.filter.fir.equiripple;


/**
 * Designs a half-band FIR equiripple filter using the "half-band trick" and the Remez algorithm.
 * 
 * <p>This class designs a half-band filter (a filter suitable for interpolating data by a factor of 2).
 * It uses the "half-band trick" described in </p>
 * 
 * <p>A “TRICK” for the Design of FIR Half-Band Filters, P. P. VAIDYANATHAN AND TRUONG Q. NGUYEN (1987),
 * IEEE TRANSACTIONS ON CIRCUITS AND SYSTEMS, VOL. CAS-34, NO. 3, pp. 297-300.</p>
 * 
 * <p>The filter is obtained as a transformation of a EquirippleHalfBandPrototype, which is designed
 * with the Remez exchange algorithm.  As with other filters, the prototype is specified by a design
 * order parameter (N) and a band edge parameter (OmegaP).  The resulting FIR filter has 2N+1 coefficients
 * and is evenly symmetric about coefficient N, counting from 0.  The band edge parameter should be close
 * to 0.5, though slightly less: 0 < OmegaP < 0.5.  A value of 0.45 is reasonable, and the closer OmegaP
 * is to 0.5, the larger N must be to obtain a reasonable response.</p>
 * 
 *  @author David B. Harris,   Deschutes Signal Processing LLC
 */
public class EquirippleHalfBand {
  
  /** float[] containing the FIR filter coefficients. */
  private float[] coefficients;
  
  
  /**
   * Instantiates a new equiripple half band filter.
   *
   * @param N         int specifying the design order.
   * @param OmegaP    double specifying the upper passband cutoff.
   */
  public EquirippleHalfBand( int N, double OmegaP ) {
    
    EquirippleHalfBandPrototype EHBP = new EquirippleHalfBandPrototype( N, 2*OmegaP );
    
    float[] c = EHBP.getCoefficients();
    
    coefficients = new float[ 2*c.length - 1 ];
    for ( int i = 0;  i < c.length;  i++ ) {
      coefficients[ 2*i ] = 0.5f*c[i];
    }
    coefficients[ c.length - 1 ] = 0.5f;
    
  }
  
  
  
  /**
   * Accessor for the FIR filter coefficients.
   *
   * @return    float[] containing the filter coefficients.
   */
  public float[] getCoefficients() {
    return coefficients.clone();
  }
  
}

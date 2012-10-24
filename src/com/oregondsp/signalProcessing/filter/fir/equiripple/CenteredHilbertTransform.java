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
 * Implements a centered equiripple Hilbert transform operator - the point of symmetry falls on a sample.
 * 
 * <p>This class uses the Remez exchange algorithm to design a Hilbert transformer of length 2N+1.
 * The Nth sample is zero (counting from 0), and the impulse response is an anti-symmetric sequence 
 * about this point.  The filter is linear phase, with group delay a constant equal to N.  The 
 * design parameters are the order (N) specifying the number (N+1) of approximating functions in the 
 * Remez algorithm, and two parameters specifying the band edge frequencies.  The design problem 
 * is performed on a discrete-time frequency axis normalized to range between 0 and 1 (the folding 
 * frequency).  Omega1, the lower band edge of the passband must be greater than 0 and less than
 * Omega2, the upper band edge.  Omega2 must be strictly less than 1.  A tradeoff exists between the 
 * filter order N and band edge frequencies.  As Omega1 approaches 0 or Omega2 approaches 1, the order
 * must be increased to obtain an acceptable design.</p>
 * <p>For details on the design algorithm and characteristics of the filter response, see</p>
 * 
 * <p>A Unified Approach to the Design of Optimum Linear Phase FIR Digital Filters,
 * James H. McClellan and Thomas W. Parks (1973), IEEE Transactions on Circuit Theory, Vol. CT-20, 
 * No. 6, pp. 697-701.</p>
 * 
 * <p>and</p>
 * 
 * <p>FIR Digital Filter Design Techniques Using Weighted Chebyshev Approximation, 
 * Lawrence R. Rabiner, James H. McClellan and Thomas W. Parks (1975) PROCEEDINGS OF THE IEEE,
 * VOL. 63, NO. 4, pp. 595-610.</p>
 * 
 *  @author David B. Harris,   Deschutes Signal Processing LLC
 */
public class CenteredHilbertTransform extends FIRTypeIII  {


  /**
   * Instantiates a new centered Hilbert transform operator.
   *
   * @param N       int specifying the number (N+1) of approximating functions in the Remez design
   *                  algorithm and the resulting number of FIR filter coefficients (2N+1).
   * @param Omega1  double specifying the low passband edge of the filter.  Omega1 > 0
   * @param Omega2  double specifying the high passband edge of the filter. Omega1 < Omega2 < 1.
   * 
   */
  public CenteredHilbertTransform( int N, double Omega1, double Omega2 ) {
      
    super( 1, N );
    
    if ( !( 0 < Omega1  &&  Omega1 < Omega2  &&  Omega2 < 1.0 ) )
      throw new IllegalArgumentException( "Check 0.0 < Omega1 < Omega2 < 1.0" );
        
    bands[0][0] = Omega1;
    bands[0][1] = Omega2;
      
    generateCoefficients();
  }


    
  /* (non-Javadoc)
   * @see com.oregondsp.signalProcessing.filter.fir.equiripple.EquirippleFIRFilter#desiredResponse(double)
   */
  double desiredResponse( double Omega ) {
      
    double retval = 0.0;
    if ( LTE( bands[0][0], Omega)  &&  LTE( Omega, bands[0][1] ) )  retval = 1.0;
        
    return retval;
  }



  /* (non-Javadoc)
   * @see com.oregondsp.signalProcessing.filter.fir.equiripple.EquirippleFIRFilter#weight(double)
   */
  double weight( double Omega ) {
      
    double retval = 0.0;
      
    if (  LTE( bands[0][0], Omega)  &&  LTE( Omega, bands[0][1] ) ) 
      retval = 1.0;
      
    return retval;
  }
    
}

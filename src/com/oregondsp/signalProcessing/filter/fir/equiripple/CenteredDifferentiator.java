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
 * Implements a centered equiripple differentiator - the point of symmetry falls on a sample.
 * 
 * <p>This class uses the Remez exchange algorithm to design a differentiator of length 2N+1.
 * The Nth sample (counting from 0) is zero, and the impulse response is an anti-symmetric 
 * sequence about this point.  The filter is linear phase, with group delay a constant equal 
 * to N.  The design parameters are the order (N) specifying the number (N+1) of approximating 
 * functions in the Remez algorithm, the intended sampling interval of the data and the passband 
 * edge frequency OmegaP.  The design problem is performed on a discrete-time frequency axis 
 * normalized to range between 0 and 1 (the folding frequency).  OmegaP, the upper band edge of 
 * the passband must be strictly less than 1, and should be in the range 0.8 - 0.95, depending 
 * on the specified order.  The larger the order, the closer OmegaP can be to 1.0 and yield an 
 * acceptable approximation.  For details on the design algorithm and characteristics of the filter 
 * response, see</p>
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
public class CenteredDifferentiator extends FIRTypeIII {
  

  /** Intended sampling interval of the data in seconds. */
  private double delta;


  /**
   * Instantiates a new centered differentiator.
   *
   * @param N       int specifying the order of the filter, specifying the number of approximating functions 
   *                (N+1) and the number of resulting FIR filter coefficients (2N+1).
   * @param delta   double specifying the intended sampling interval of the data in seconds.
   * @param OmegaP  double specifying the upper passband cutoff (0 < OmegaP < 1).  It should be in the range
   *                0.8 - 0.95+ with larger values of N required to obtain good approximants when OmegaP
   *                approaches 1.
   */
  public CenteredDifferentiator( int N, double delta, double OmegaP ) {
    
    super( 1, N );
    
    if ( !( 0.0 < OmegaP  &&  OmegaP < 1.0 ) )
      throw new IllegalArgumentException( "Check 0.0 < OmegaP < 1.0" );
      
    bands[0][0] = 1.0/(2*N);
    bands[0][1] = OmegaP;
    
    this.delta  = delta;
    
    generateCoefficients();
  }


  
  /* (non-Javadoc)
   * @see com.oregondsp.signalProcessing.filter.fir.equiripple.EquirippleFIRFilter#desiredResponse(double)
   */
  double desiredResponse( double Omega ) {
    
    double retval = 0.0;
    if ( LTE( bands[0][0], Omega)  &&  LTE( Omega, bands[0][1] ) )  retval = -Math.PI*Omega/delta;
      
    return retval;
  }



  /* (non-Javadoc)
   * @see com.oregondsp.signalProcessing.filter.fir.equiripple.EquirippleFIRFilter#weight(double)
   */
  double weight( double Omega ) {
    
    double retval = 0.0;
    
    if (  LTE( bands[0][0], Omega)  &&  LTE( Omega, bands[0][1] ) ) 
      retval = 1.0/Omega;
    
    return retval;
  }

}

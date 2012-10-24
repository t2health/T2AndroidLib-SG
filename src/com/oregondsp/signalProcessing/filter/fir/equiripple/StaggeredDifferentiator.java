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
 * Implements an even-length differentiator - the point of symmetry falls between samples.
 * 
 * <p>This class implements a differentiator with impulse response that exhibits odd symmetry on a
 * staggered grid, i.e. the point of symmetry falls "between" samples.  This filter has an accurate 
 * differentiator response that extends from 0 to the folding frequency.  This advantage may be
 * offset by a non-integer group delay ( (2N-1)/2 ) which shifts the waveform by a fractional sample.
 * The quality of the design is controlled by a single parameter (order N), which specifies the number
 * of approximating basis functions in the Remez algorithm.</p>
 * 
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
public class StaggeredDifferentiator extends FIRTypeIV {

  /** double representing the sampling interval of the data to be differentiated. */
  private double delta;


  /**
   * Instantiates a new differentiator.
   *
   * @param N       int specifying the filter order design parameter.  The larger this value, the
   *                  more accurate the differentiator response.
   * @param delta   double specifying the sampling interval of the data to be differentiated.
   */
  public StaggeredDifferentiator( int N, double delta ) {
    
    super( 1, N );
      
    bands[0][0] = 1.0/(2*N);
    bands[0][1] = 1.0;
    
    this.delta = delta;
    
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

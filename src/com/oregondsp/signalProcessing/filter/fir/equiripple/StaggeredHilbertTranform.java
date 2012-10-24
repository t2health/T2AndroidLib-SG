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
 * Implements an even-length Hilbert transform operator - the point of symmetry falls "between" samples.
 * 
 * <p>This class implements a Hilbert transformer with impulse response that exhibits odd symmetry on a
 * staggered grid, i.e. the point of symmetry falls "between" samples.  This filter has a response that
 * is accurate over a wider frequency band than the comparable "centered" (odd length) Hilbert transformer. 
 * This advantage may be offset by a non-integer group delay ( (2N-1)/2 ) which shifts the waveform by 
 * a fraction of a sample.  For example, to compute the envelope of a signal, a centered Hilbert transformer
 * may be a better choice.  The envelope is the square root of sum of squares of the original signal and its
 * Hilbert transform.  With this implementation, the signal and its Hilbert transform will be misaligned by
 * a half sample (even after correcting for the bulk delay of the FIR impulse response - roughly half the filter
 * length).</p>
 * 
 * <p>The quality of the design is controlled by two parameters:  the filter design order, N, which specifies 
 * the number of approximating basis functions in the Remez algorithm, and the passband low-frequency cutoff, 
 * OmegaP, which specifies the point near 0 frequency where the filter response begins its transition to zero.
 * The closer OmegaP is to zero, the larger N must be (and the longer the filter impulse response) to obtain 
 * an acceptable approximation to the ideal Hilbert frequency response.</p>
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
public class StaggeredHilbertTranform extends FIRTypeIV  {


  /**
   * Instantiates a new staggered hilbert tranform.
   *
   * @param N the n
   * @param OmegaP the omega p
   */
  public StaggeredHilbertTranform( int N, double OmegaP ) {
      
    super( 1, N );
    
    if ( !( 0.0 < OmegaP  &&  OmegaP < 1.0 ) )
      throw new IllegalArgumentException( "Check 0.0 < OmegaP < 1.0" );
        
    bands[0][0] = OmegaP;
    bands[0][1] = 1.0;
      
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

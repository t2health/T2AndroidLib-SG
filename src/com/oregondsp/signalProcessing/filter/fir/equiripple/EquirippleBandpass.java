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
 * Implements a centered FIR bandpass filter - the point of symmetry falls on a sample.
 * 
 * <p>This class uses the Remez exchange algorithm to design a bandpass filter of length 2N+1.
 * The impulse response is a symmetric sequence about point N (counting from 0). The filter is linear 
 * phase, with group delay a constant equal to N.  The design parameters are the order (N) specifying 
 * the number (N+1) of approximating functions in the Remez algorithm and seven parameters controlling 
 * the cutoffs and design weights of the passband and 2 stopbands.  The design problem is performed on 
 * a discrete-time frequency axis normalized to range between 0 and 1 (the folding frequency).  The
 * pass band is the interval [OmegaP1, OmegaP2].  The lower stop band is the interval [0, OmegaS1].  
 * The upper stop band is the interval [OmegaS2, 1].  Note that 0 < OmegaS1 < OmegaP1 < OmegaP2 < 
 * OmegaS2 < 1.  All bands must have non-zero width, and the open intervals (transition bands) 
 * (OmegaS1, OmegaP1) and (OmegaP2, OmegaS2) also must have non-zero width. The narrower any of these 
 * bands, the larger the order N must be to obtain a reasonable frequency response.  Weights are 
 * specified for each band to control the relative sizes of the maximum errors among bands.
 * For details on the design algorithm and characteristics of the filter response, see</p>
 * 
 * <p>A Unified Approach to the Design of Optimum Linear Phase FIR Digital Filters,
 * James H. McClellan and Thomas W. Parks (1973), IEEE Transactions on Circuit Theory, Vol. CT-20, 
 * No. 6, pp. 697-701.</p>
 * 
 * and</p>
 * 
 * <p>FIR Digital Filter Design Techniques Using Weighted Chebyshev Approximation, 
 * Lawrence R. Rabiner, James H. McClellan and Thomas W. Parks (1975) PROCEEDINGS OF THE IEEE,
 * VOL. 63, NO. 4, pp. 595-610.</p>
 * 
 * <p>and for order selection, consult:</p>
 * 
 * <p>Approximate Design Relationships for Low-Pass FIR Digital Filters, Lawrence R. Rabiner (1973), 
 * IEEE TRANSACTIONS ON AUDIO AND ELECTROACOUSTICS, VOL. AU-21, NO. 5, pp. 456-460.</p>
 * 
 *  @author David B. Harris,   Deschutes Signal Processing LLC
 */
public class EquirippleBandpass extends FIRTypeI {
  
  /** double specifying the passband weight. */
  private double Wp;
  
  /** double specifying the lower stopband weight. */
  private double Ws1;
  
  /** double specifying the upper stopband weight. */
  private double Ws2;

  /**
   * Instantiates a new equiripple bandpass FIR filter.
   *
   * @param N         int specifying the design order of the filter.
   * @param OmegaS1   double specifying the upper cutoff of the low stopband.
   * @param Ws1       double specifying the error weighting of the lower stopband.
   * @param OmegaP1   double specifying the lower cutoff of the passband.
   * @param OmegaP2   double specifying the upper cutoff of the passband.
   * @param Wp        double specifying the error weighting of the passband.
   * @param OmegaS2   double specifying the lower cutoff of the high stopband.
   * @param Ws2       double specifying the error weighting of the upper stopband.
   */
  public EquirippleBandpass( int N, double OmegaS1, double Ws1, double OmegaP1, double OmegaP2, double Wp, double OmegaS2, double Ws2 ) {
    
    super( 3, N );
    
    if ( !( 0.0     < OmegaS1  &&  
            OmegaS1 < OmegaP1  &&
            OmegaP1 < OmegaP2  &&
            OmegaP2 < OmegaS2  &&
            OmegaS2 < 1.0 ) ) throw 
            new IllegalArgumentException( "Band edge specification error, ensure that 0.0 < OmegaS1 < OmegaP1 < OmegaP2 < OmegaS2 < 1.0" );
    
    bands[0][0] = 0.0;
    bands[0][1] = OmegaS1;
    bands[1][0] = OmegaP1;
    bands[1][1] = OmegaP2;
    bands[2][0] = OmegaS2;
    bands[2][1] = 1.0;
    
    this.Wp  = Wp;
    this.Ws1 = Ws1;
    this.Ws2 = Ws2;   
    
    generateCoefficients();
  }


  
  /* (non-Javadoc)
   * @see com.oregondsp.signalProcessing.filter.fir.equiripple.EquirippleFIRFilter#desiredResponse(double)
   */
  double desiredResponse( double Omega ) {
    
    double retval = 0.0;
    if ( LTE( bands[1][0], Omega )  &&  LTE( Omega, bands[1][1] ) )  retval = 1.0;
      
    return retval;
  }



  /* (non-Javadoc)
   * @see com.oregondsp.signalProcessing.filter.fir.equiripple.EquirippleFIRFilter#weight(double)
   */
  double weight( double Omega ) {
    
    double retval = 0.0;
    
    if ( LTE( bands[0][0], Omega )  &&  LTE( Omega, bands[0][1] ) ) 
      retval = Ws1;
    else if ( LTE( bands[1][0], Omega )  &&  LTE( Omega, bands[1][1] ) ) 
      retval = Wp;
    else if ( LTE( bands[2][0], Omega )  &&  LTE( Omega, bands[2][1] ) )
      retval = Ws2;
    
    return retval;
  }

}

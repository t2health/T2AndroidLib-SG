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
 * Half band prototype filter used by EquirippleHalfBand for interpolation by a factor of 2.
 * 
 * <p>This class is intended be used by EquirippleHalfBand to design filters suitable for
 * interpolating sequences by a factor of two.  It is used in conjuction with the half-band "trick"
 * described in:</p>
 * 
 * <p>A “TRICK” for the Design of FIR Half-Band Filters, P. P. VAIDYANATHAN AND TRUONG Q. NGUYEN (1987),
 * IEEE TRANSACTIONS ON CIRCUITS AND SYSTEMS, VOL. CAS-34, NO. 3, pp. 297-300.</p>
 * 
 *  @author David B. Harris,   Deschutes Signal Processing LLC
 */
class EquirippleHalfBandPrototype extends FIRTypeII {


  /**
   * Instantiates a new equiripple half band prototype.
   *
   * @param N         int specifying the design order of the filter.
   * @param OmegaP    double specifying the upper band edge of the single band used in this filter type.
   */
  EquirippleHalfBandPrototype( int N, double OmegaP ) {
    
    super( 1, N );
    
    if ( OmegaP <= 0.0  ||  OmegaP >= 1.0 ) 
      throw new IllegalArgumentException( "OmegaP: " + OmegaP + " out of bounds (0.0 < OmegaP < 1.0)" );

    
    bands[0][0] = 0.0;
    bands[0][1] = OmegaP;
    
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

/* T2AndroidLib for Signal Processing
 * 
 * Copyright � 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright � 2009-2012 Contributors. All Rights Reserved. 
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


import com.oregondsp.signalProcessing.Sequence;


/**
 * Class for designing FIR type I digital filters.  Odd-length filters with even symmetry.
 * 
 * <p>See 
 * A Unified Approach to the Design of Optimum Linear Phase FIR Digital Filters,
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
abstract class FIRTypeI extends EquirippleFIRFilter {
   
  /**
   * Instantiates a new FIR type I filter.
   *
   * @param numBands     int specifying the number of pass and stop bands.
   * @param nHalf        int specifying the half size of the filter - one less than the number of 
   *                       approximating basis functions (cosines).
   */
  FIRTypeI( int numBands, int nHalf ) {
    
    super( numBands, nHalf+1, 2*nHalf+1 );
    
  }
  
  
  
  /* (non-Javadoc)
   * @see com.oregondsp.signalProcessing.filter.fir.equiripple.EquirippleFIRFilter#populateGrid(com.oregondsp.signalProcessing.filter.fir.equiripple.DesignGrid)
   */
  void    populateGrid( DesignGrid G ) {
    
    for ( int i = 0;  i < G.gridSize;  i++ ) {
      G.H[i]    = desiredResponse( G.grid[i] );
      G.W[i]    = weight( G.grid[i] );
    }
    
    G.containsZero = true;
    G.containsPi   = true;
  }
   
  
  
  /* (non-Javadoc)
   * @see com.oregondsp.signalProcessing.filter.fir.equiripple.EquirippleFIRFilter#interpretCoefficients(float[])
   */
  float[] interpretCoefficients( float[] coefficients ) {
    
    float[] retval = new float[ Nc ];
    Sequence.circularShift( coefficients, N-1 );
    System.arraycopy( coefficients, 0, retval, 0, Nc );
    
    return retval;
  }

}

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

import java.io.PrintStream;
import java.text.DecimalFormat;



/**
 * Contains the finite frequency-sampling grid used by the Remez exchange algorithm.
 * 
 *  @author David B. Harris,   Deschutes Signal Processing LLC
 */
class DesignGrid {
  
  /** Constant GRIDDENSITY partly specifies the number of grid points (along with N). */
  static final int    GRIDDENSITY = 20;
  
  /** double[] containing the grid samples. */
  double[]     grid;                 // sample points where designs are evaluated
  
  /** int specifying the grid size. */
  int          gridSize;
  
  /** double array containing the transformed grid points. */
  double[]     X;
  
  /** double[] containing the desired (weighted) response of the filter on the grid. */
  double[]     H;                    // desired response of filter on grid
  
  /** double[] containing the weighting function on grid. */
  double[]     W;                    // weight function on grid

  /** int[] specifying indices of grid points that are band edges. */
  int[]        bandEdgeIndices;
  
  /** int[] specifying indices of grid points that are current extrema in the Remez exchange. */
  int[]        extremaIndices;
  
  /** boolean value specifying whether the grid contains 0 frequency as a sample. */
  boolean      containsZero;
  
  /** boolean value specifying whether the grid contains frequency pi (1.0 normalized) as a sample. */
  boolean      containsPi;
  
  
  /**
   * Prints the grid to a PrintStream instance - useful for debugging.
   *
   * @param ps    PrintStream instance to which the grid is printed.
   */
  void print( PrintStream ps ) {
    
    DecimalFormat F = new DecimalFormat( "0.000000" );
    DecimalFormat I = new DecimalFormat( "000" );
    int extremum = 0;
    int bandEdgeCount = 0;
    for ( int i = 0;  i < gridSize;  i++ ) {
      double Omega = grid[i];
      String line = I.format(i) + "  " + F.format(Omega) + "  " + 
                    F.format( X[i] ) + "  " +
                    F.format( H[i] ) + "  " +
                    F.format( W[i] );
      if ( bandEdgeIndices[ bandEdgeCount ] == i ) {
        line = line + "  band edge";
        bandEdgeCount++;
      }
      if ( Omega == grid[ extremaIndices[ extremum ] ] )  {
        line = line + "  extremum";
        extremum++;
      }
      
      ps.println( line );
    }
    
  }
  
}

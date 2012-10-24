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

package com.oregondsp.signalProcessing.filter.iir;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;


/**
 * Class implementing Chebyshev Type II filters - characterized by zeros in the stop band.
 * 
 * @author David B. Harris,  Deschutes Signal Processing LLC
 */
public class ChebyshevII extends IIRFilter {
	
  /**
   * Instantiates a new Chebyshev type II filter.
   *
   * @param order      int specifying the order (number of poles) of the filter.
   * @param epsilon    double design parameter specifying the passband ripple and stopband attenuation.
   * @param type       PassbandType specifying whether the filter is a lowpass, bandpass or highpass filter.
   * @param f1         double specifying the low cutoff frequency (must always be present, but used only for 
   *                   bandpass and highpass filters).
   * @param f2         double specifying the high cutoff frequency (must always be present, but used only for
   *                   bandpass and lowpass filters).
   * @param delta      double specifying the sampling interval of the data to which this filter will be applied.
   */
  public ChebyshevII( int order, double epsilon, PassbandType type, double f1, double f2, double delta ) {
		    
	super( new AnalogChebyshevII( order, epsilon ), type, f1, f2, delta );
		      
  }	
	
	

  	public static void main( String[] args ) {
		    
		    double epsilon = 0.01;
		    ChebyshevII F = new ChebyshevII( 7, epsilon, PassbandType.LOWPASS, 2.0, 0.0, 0.05 );
		    F.print( System.out );
		    float[] tmp = new float[201];
		    for ( int i = 0;  i < 201;  i++ ) {
		        Complex C = F.evaluate( Math.PI/200.0*i );
		        tmp[i] = (float) Complex.abs( C );
		    }
		    
		    float[] x = new float[ 1001 ];
		    x[200] = 1.0f;
		    float[] y = new float[ 1001 ];
		    F.filter( x, y );
		    
		    PrintStream ps;
		    try {
		        ps = new PrintStream( new FileOutputStream( "C:\\DATA\\Test\\Response.m" ) );
		        ps.print( "R = [ " );
		        for ( int i = 0;  i < 200;  i++ ) {
		            ps.println( tmp[i] + "; ..." );
		        }
		        ps.println( tmp[200] + "];" );
		        ps.close();
		    } catch (FileNotFoundException e) {
		        e.printStackTrace();
		    }
		    
		    try {
		        ps = new PrintStream( new FileOutputStream( "C:\\DATA\\Test\\ImpulseResponse.m" ) );
		        ps.print( "IR = [ " );
		        for ( int i = 0;  i < 1000;  i++ ) {
		            ps.println( y[i] + "; ..." );
		        }
		        ps.println( y[1000] + "];" );
		        ps.close();
		    } catch (FileNotFoundException e) {
		        e.printStackTrace();
		    }

		  }


}

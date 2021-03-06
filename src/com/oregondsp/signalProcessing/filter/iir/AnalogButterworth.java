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


package com.oregondsp.signalProcessing.filter.iir;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.oregondsp.signalProcessing.filter.Polynomial;
import com.oregondsp.signalProcessing.filter.Rational;


/**
 * Class to design analog Butterworth prototype filters.
 * 
 * This analog prototype is a lowpass filter with a cutoff at 1 radian per second.
 * 
 * @author David B. Harris,  Deschutes Signal Processing LLC
 */
public class AnalogButterworth extends AnalogPrototype {

	/**
	 * Instantiates a new analog Butterworth filter design with the indicated number of poles.
	 *
	 * @param order   int specifying the number of poles of the filter.
	 */
	public AnalogButterworth( int order ) {
		
		super();
		
		int nRealPoles        = order - 2*(order/2);
		int nComplexPolePairs = order/2;
		int nPoles            = nRealPoles + 2*nComplexPolePairs;
		
        if ( nRealPoles == 1 ) {
          double[] td = {1.0, 1.0};
          addSection( new Rational( new Polynomial(1.0), new Polynomial(td) ) );
        }
        
    	double dAngle = Math.PI/nPoles;

    	for ( int i = 0;  i < nComplexPolePairs;  i++ ) {
    	    double   angle = -Math.PI/2  +  dAngle/2 *( 1 + nRealPoles )  +  i*dAngle;
    	    double[] td    = {1.0, -2*Math.sin(angle), 1.0 };
    	    addSection( new Rational( new Polynomial(1.0), new Polynomial(td) ) );
    	}
    	
	}
	
	
	
	public static void main( String[] args ) {
	  
	  AnalogButterworth B = new AnalogButterworth( 6 );

	  AnalogPrototype   A = B.lptobp( 2*Math.PI*2.0, 2*Math.PI*3.0 );
		
	  float[] tmp = new float[201];
	  float[] gd  = new float[201];
	  for ( int i = 0;  i < 201;  i++ ) {
	    double omega = i*2.0*Math.PI/20.0;
	    tmp[i] = (float) Complex.abs( A.evaluate( omega ) );
	    gd[i] = (float) A.groupDelay( omega );
	  }
	  
	  PrintStream ps;
	  try {
		ps = new PrintStream( new FileOutputStream( "C:\\DATA\\Test\\AnalogButterworthResponse.m" ) );
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
	     ps = new PrintStream( new FileOutputStream( "C:\\DATA\\Test\\AnalogButterworthGroupDelay.m" ) );
	     ps.print( "gd = [ " );
	     for ( int i = 0;  i < 200;  i++ ) {
	       ps.println( gd[i] + "; ..." );
	     }
	     ps.println( gd[200] + "];" );
	     ps.close();
	     } catch (FileNotFoundException e) {
	     e.printStackTrace();
	     }

	}

}

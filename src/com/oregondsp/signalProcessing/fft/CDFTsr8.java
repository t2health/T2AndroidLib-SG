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

package com.oregondsp.signalProcessing.fft;


/**
 * Package-private class implementing a length-8 complex DFT with a split-radix algorithm.
 * 
 * @author David B. Harris,   Deschutes Signal Processing LLC
 */
class CDFTsr8 extends CDFTsr {

  /** Constant twiddle factor. */
  static final float SQRT2BY2 = (float) (Math.sqrt(2.0) / 2.0);

  /** Input sequence array indices. */
  private int n0, n1, n2, n3, n4, n5, n6, n7;
  
  /** Output transform array indices. */
  private int m0, m1, m2, m3, m4, m5, m6, m7;
  

  /**
   * Instantiates a new CDFTsr8.
   *
   * @param xoffset  int specifying offset into the top-level length-N sequence array.
   * @param xstride  int specifying the stride of butterflies into the top-level length-N sequence array.
   * @param Xoffset  int specifying the offset into the length-N transform array.
   */
  CDFTsr8( int xoffset, int xstride, int Xoffset ) {

     m = 3;
     N = 8;
     this.xoffset = xoffset;
     this.xstride = xstride;
     this.Xoffset = Xoffset;
     
     n0 = xoffset;
     n1 = n0 + xstride;
     n2 = n1 + xstride;
     n3 = n2 + xstride;
     n4 = n3 + xstride;
     n5 = n4 + xstride;
     n6 = n5 + xstride;
     n7 = n6 + xstride;

     m0 = Xoffset;
     m1 = m0 + 1;
     m2 = m1 + 1;
     m3 = m2 + 1;
     m4 = m3 + 1;
     m5 = m4 + 1;
     m6 = m5 + 1;
     m7 = m6 + 1;

  }
  
  
  
  /**
   * Links the user-supplied input sequence and output transform arrays.
   * 
   * @param xr  float[] containing the input sequence real part.
   * @param xi  float[] containing the input sequence imaginary part.
   * @param Xr  float[] containing the output sequence real part.
   * @param Xi  float[] containing the output sequence imaginary part.
   */
  void link( float[] xr, float[] xi, float[] Xr, float[] Xi ) {
    this.xr = xr;
    this.xi = xi;
    this.Xr = Xr;
    this.Xi = Xi;
  }



  /**
   * Evaluates the length-8 complex DFT.
   */
  void evaluate() {
     
    float T1r, T1i, T3r, T3i; 
    float Rr, Ri, Sr, Si;

// Length 2 DFT

    Xr[m0] = xr[n0] + xr[n4];
    Xi[m0] = xi[n0] + xi[n4];
    Xr[m1] = xr[n0] - xr[n4];
    Xi[m1] = xi[n0] - xi[n4];

  // length 4 dft

  // k = 0 butterfly

    Rr = xr[n2]  + xr[n6];
    Ri = xi[n2]  + xi[n6];
    Sr = xi[n6]  - xi[n2];
    Si = xr[n2]  - xr[n6];
  
    Xr[m2] = Xr[m0] - Rr;
    Xi[m2] = Xi[m0] - Ri;
    Xr[m3] = Xr[m1] + Sr;
    Xi[m3] = Xi[m1] + Si;
  
    Xr[m0] += Rr;
    Xi[m0] += Ri;
    Xr[m1] -= Sr;
    Xi[m1] -= Si;

// Length 2 DFT

    Xr[m4] = xr[n1] + xr[n5];
    Xi[m4] = xi[n1] + xi[n5];
    Xr[m5] = xr[n1] - xr[n5];
    Xi[m5] = xi[n1] - xi[n5];

// Length 2 DFT

    Xr[m6] = xr[n3] + xr[n7];
    Xi[m6] = xi[n3] + xi[n7];
    Xr[m7] = xr[n3] - xr[n7];
    Xi[m7] = xi[n3] - xi[n7];



  // length 8 dft


  // k = 0 butterfly

    Rr = Xr[m4]  + Xr[m6];
    Ri = Xi[m4]  + Xi[m6];
    Sr = Xi[m6]  - Xi[m4];
    Si = Xr[m4]  - Xr[m6];
  
    Xr[m4] = Xr[m0] - Rr;
    Xi[m4] = Xi[m0] - Ri;
    Xr[m6] = Xr[m2] + Sr;
    Xi[m6] = Xi[m2] + Si;
  
    Xr[m0] += Rr;
    Xi[m0] += Ri;
    Xr[m2] -= Sr;
    Xi[m2] -= Si;


  // k = 1 butterfly

  // T1 = Wk*O1
  // T3 = W3k*O3

    T1r =  SQRT2BY2 * ( Xr[m5]+ Xi[m5] );
    T1i =  SQRT2BY2 * ( Xi[m5]- Xr[m5] );
    T3r =  SQRT2BY2 * ( Xi[m7] - Xr[m7] );
    T3i = -SQRT2BY2 * ( Xi[m7] + Xr[m7] );

  // R = T1 + T3
  // S = i*(T1 - T3)

    Rr = T1r + T3r;
    Ri = T1i + T3i;
    Sr = T3i - T1i;
    Si = T1r - T3r;

    Xr[m5] = Xr[m1] - Rr;
    Xi[m5] = Xi[m1] - Ri;
    Xr[m7] = Xr[m3] + Sr;
    Xi[m7] = Xi[m3] + Si;

    Xr[m1] += Rr;
    Xi[m1] += Ri;
    Xr[m3] -= Sr;
    Xi[m3] -= Si;

  }

}

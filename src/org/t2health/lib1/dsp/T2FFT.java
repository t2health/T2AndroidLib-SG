/*
* Copyright 2006-2007 Columbia University.
*
*  This file is part of MEAPsoft.
*
*  MEAPsoft is free software; you can redistribute it and/or modify
*  it under the terms of the GNU General Public License version 2 as
*  published by the Free Software Foundation.
*
*  MEAPsoft is distributed in the hope that it will be useful, but
*  WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with MEAPsoft; if not, write to the Free Software
*  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
*  02110-1301 USA
*
*  See the file "COPYING" for the text of the license.
*/

package org.t2health.lib1.dsp;

import android.util.Log;


/**
 * Class that takes a FFT of an input sample
 * 
 *  Currently only Blackman window is supported
 * 
 * @author scott.coleman
 *
 */
public class T2FFT {

	private final static int WINDOW_BLACKMAN = 1;	
	public int mWindowSize; 
	int mWindowBits;

	/**
	 * How often the FFT get's run when using the add() function
	 */
	int mRecomputeFrequency; 
	int mRecomputeCount = 0;
	
	
	// Lookup tables - so we don't have to compute the sin and cosine each real time iteration
	double[] mCos;
	double[] mSin;
	
	double[] mWindow;		
	
	private double mCircularBuffer[];
    private int mCircularIndex;	
    private int mCount;    
    
	private double[] mFftRe;	
	private double[] mFftIm;	
	public double[] mMagnitude;	
    
    
	
 
 public T2FFT(int windowSize, int recomputeFrequency) {
	 mWindowSize = windowSize;
	 mWindowBits = (int)(Math.log(mWindowSize) / Math.log(2));
	 mRecomputeFrequency = recomputeFrequency;
	 
	 
	 // Make sure mWindowSize is a power of 2
	 if(mWindowSize != ( 1 << mWindowBits))
		 throw new RuntimeException("T2FFT length must be power of 2");

	 // precompute tables
	 mCos = new double[windowSize/2];
	 mSin = new double[windowSize/2];
	 
	 mFftRe = new double[windowSize];	 
	 mFftIm = new double[windowSize];	
	 mMagnitude = new double[windowSize];
	 mCircularBuffer = new double[windowSize];
	 

	 for(int i=0; i<windowSize/2; i++) {
		 mCos[i] = Math.cos(-2*Math.PI*i/windowSize);
		 mSin[i] = Math.sin(-2*Math.PI*i/windowSize);
	 }

	 makeWindow();
 }

 protected void makeWindow() {
	 // Make a blackman mWindow:
	 // w(mWindowSize)=0.42-0.5cos{(2*PI*mWindowSize)/(N-1)}+0.08cos{(4*PI*mWindowSize)/(N-1)};
	 mWindow = new double[mWindowSize];
	 for(int i = 0; i < mWindow.length; i++)
		 mWindow[i] = 0.42 - 0.5 * Math.cos(2*Math.PI*i/(mWindowSize-1)) 
		 	+ 0.08 * Math.cos(4*Math.PI*i/(mWindowSize-1));
 }
 
 public double[] getWindow() {
	 return mWindow;
 }


 /***************************************************************
 * fft.c
 * Douglas L. Jones 
 * University of Illinois at Urbana-Champaign 
 * January 19, 1992 
 * http://cnx.rice.edu/content/m12016/latest/
 * 
 *   fft: in-place radix-2 DIT DFT of a complex input 
 * 
 *   input: 
 * mWindowSize: length of T2FFT: must be a power of two 
 * mWindowBits: mWindowSize = 2**mWindowBits 
 *   input/output 
 * x: double array of length mWindowSize with real part of data 
 * y: double array of length mWindowSize with imag part of data 
 * 
 *   Permission to copy and use this program is granted 
 *   as long as this header is included. 
 ****************************************************************/
 public void fft(double[] re, double[] im) {
	 int i,j,k,n1,n2,a;
	 double c,s,e,t1,t2;
 
	 // Bit-reverse
	 j = 0;
	 n2 = mWindowSize/2;
	 for (i=1; i < mWindowSize - 1; i++) {
		 n1 = n2;
		 while ( j >= n1 ) {
			 j = j - n1;
			 n1 = n1/2;
		 }
     
		 j = j + n1;
   
		 if (i < j) {
			 t1 = re[i];
			 re[i] = re[j];
			 re[j] = t1;
			 t1 = im[i];
			 im[i] = im[j];
			 im[j] = t1;
		 }
	 }

	 n1 = 0;
	 n2 = 1;
 
	 for (i=0; i < mWindowBits; i++) {
		 n1 = n2;
		 n2 = n2 + n2;
		 a = 0;
   
		 for (j=0; j < n1; j++) {
			 c = mCos[a];
			 s = mSin[a];
			 a +=  1 << (mWindowBits-i-1);

			 for (k=j; k < mWindowSize; k=k+n2) {
				 t1 = c*re[k+n1] - s*im[k+n1];
				 t2 = s*re[k+n1] + c*im[k+n1];
				 re[k+n1] = re[k] - t1;
				 im[k+n1] = im[k] - t2;
				 re[k] = re[k] + t1;
				 im[k] = im[k] + t2;
			 }
		 }
	 }
 }                          
 
 public double[] getInputBuffer() {
	 double[] buffer = new double[mWindowSize];
	 
	 int ci = mCircularIndex;
	 for (int i = 0; i < mWindowSize; i++) {
		 buffer[i] = mCircularBuffer[ci];
		 ci = nextIndex(ci);
	 }	 
	 return buffer;
 }
 
 /**
 * @param value	Value to add to the circular buffer for the fft
 * @return	True if an FFT has been run and result is computed in mMagnitude[]
 */
public boolean add(double value) {
	 boolean result = false;
	 
	 mCircularBuffer[mCircularIndex] = value;
     mCircularIndex = nextIndex(mCircularIndex);
     mCount++;
	 mRecomputeCount++;
	 //Log.i("BfDemo", "" + mRecomputeCount);
     
     // We must have at least mWindowSize samples in the circular buffer 
     if (mCount >= mWindowSize) {
    	 
    	 
    	 // See if it's time to compute another fft on the circular buffer
    	 if (mRecomputeCount >= mRecomputeFrequency) {
    		 mRecomputeCount = 0;
    		 
    		 int ci = mCircularIndex;
        	 // Fill input re and im arrarys from circular buffer
        	 for (int i = 0; i < mWindowSize; i++) {
        		 mFftRe[i] = mCircularBuffer[ci];
        		 mFftIm[i] = 0;
        		 ci = nextIndex(ci);
        	 }

        	 // Run the fft
        	 fft(mFftRe, mFftIm);
        	 
        	 // Convert result into magnitude
        	 for (int i = 0; i < mWindowSize; i++) {
        		 mMagnitude[i] = Math.sqrt(mFftRe[i] * mFftRe[i] + mFftIm[i] * mFftIm[i]);
        	 }    
        	 result = true;
    	 }
     }
     
     return result;
 }

 public void reset() {
     mCircularIndex = 0;
     mCount = 0;
     mRecomputeCount = 0;     
 }

 private void primeBuffer(float val) {
     for (int i = 0; i < mCircularBuffer.length; ++i) {
         mCircularBuffer[i] = val;
     }
 }

 private int nextIndex(int curIndex) {
     if (curIndex + 1 >= mCircularBuffer.length) {
         return 0;
     }
     return curIndex + 1;
 } 
 
 
 // Test the T2FFT to make sure it's working
 public static void main() {
   int N = 8;

   T2FFT fft = new T2FFT(N,1);

   double[] window = fft.getWindow();
   double[] re = new double[N];
   double[] im = new double[N];

   // Impulse
   re[0] = 1; im[0] = 0;
   for(int i=1; i<N; i++)
     re[i] = im[i] = 0;
   beforeAfter(fft, re, im);

   // Nyquist
   for(int i=0; i<N; i++) {
     re[i] = Math.pow(-1, i);
     im[i] = 0;
   }
   beforeAfter(fft, re, im);

   // Single mSin
   for(int i=0; i<N; i++) {
     re[i] = Math.cos(2*Math.PI*i / N);
     im[i] = 0;
   }
   beforeAfter(fft, re, im);

   // Ramp
   for(int i=0; i<N; i++) {
     re[i] = i;
     im[i] = 0;
   }
   beforeAfter(fft, re, im);

   long time = System.currentTimeMillis();
   double iter = 30000;
   for(int i=0; i<iter; i++)
     fft.fft(re,im);
   time = System.currentTimeMillis() - time;
   System.out.println("Averaged " + (time/iter) + "ms per iteration");
 }

 protected static void beforeAfter(T2FFT fft, double[] re, double[] im) {
   System.out.println("Before: ");
   printReIm(re, im);
   fft.fft(re, im);
   System.out.println("After: ");
   printReIm(re, im);
 }

 protected static void printReIm(double[] re, double[] im) {
   for(int i=0; i<re.length; i++) {
	   double mag =  Math.sqrt(re[i] * re[i] + im[i] * im[i]);
	   Log.i("BFDemo", ((int)(re[i]*1000)/1000.0) + ", " + (int)(im[i]*1000)/1000.0 + ", " + (int)(mag*1000)/1000.0);
   }

 }
}
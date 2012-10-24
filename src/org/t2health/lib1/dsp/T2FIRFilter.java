/*
 * 
 */
package org.t2health.lib1.dsp;



public class T2FIRFilter extends T2Filter {

	int mSmoothingFactor;
	int mCurrentValue;
	
	int[] mCoefs; 
	int[] mDelayLine;
	double[] mdCoefs; 
	double[] mdDelayLine;
	
	int mNumCoefs;
	int mScale;

	public T2FIRFilter(double[] coefs) {
		mdCoefs = coefs;
		mNumCoefs = mdCoefs.length;
		mdDelayLine = new double[mNumCoefs];
	}
	

    public double dfilter(double inputSample) {
    
    	double result = 0;
    	
    	// First take care of the shift register
    	for (int i = mNumCoefs - 1; i > 0; i--) {
			mdDelayLine[i] = mdDelayLine[i - 1];
		}
		mdDelayLine[0] = inputSample; 


//		String t = "";
//		for (int i = 0; i < mNumCoefs; i++) {
//			t = t + mdDelayLine[i] + ", ";
//		}
//		Log.i("SensorData", t);
		
		// Now calculate based on the coefficients
		for (int i = 0; i < mNumCoefs; i++) {
			
			result += mdCoefs[i] * mdDelayLine[i];
		}
		
//		Log.i("SensorData", String.format(",Zeros: %g, %g, %g, %g, Poles: %g, %g, %g, %g", mdDelayLine[0], mdDelayLine[1], mdDelayLine[2], mdDelayLine[3], mdPoleDelayLine[0], mdPoleDelayLine[1], mdPoleDelayLine[2], mdPoleDelayLine[3])); 		
		
		return result;
    }	
	
}

package org.t2health.lib1.dsp;

public class T2SmoothingFilter extends T2Filter {

	int mSmoothingFactor;
	int mCurrentValue;
	
	public T2SmoothingFilter(int smoothingFactor) {
		mSmoothingFactor = smoothingFactor;
		mCurrentValue = 0;
	}
	
	@Override    
    public int filter(int inputSample) {
    
		mCurrentValue += (inputSample - mCurrentValue) / mSmoothingFactor;		
		return mCurrentValue;
    }	
	
	
}

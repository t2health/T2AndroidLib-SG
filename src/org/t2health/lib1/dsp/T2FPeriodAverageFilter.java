package org.t2health.lib1.dsp;

public class T2FPeriodAverageFilter extends T2Filter {
//	public static final double AVERAGING = Double.NaN;
	// For some reason I can't detect NaN on the outside, so for now we'll cheat and use a number highly unlikely to be a good number
	public static final double AVERAGING = 99999999;
    private double mAverage;
	private int mIndex;
	private double mSize;
	private double mTotal;
	
	
	public T2FPeriodAverageFilter(int size) {
		mSize = size;
	    reset();
	}
	
	@Override
	public    double    filter( double value ) {	
		mTotal += value;

		if (mIndex++ >= mSize - 1) {
			mAverage = mTotal / mSize;
			mTotal = 0;
			mIndex = 0;
			return mAverage;
		}
		else {
			return AVERAGING;
		}
	}       
	   
	public double getAverage() {
		return mAverage;
	}
	
	
	
	public void reset() {
	    mIndex = 0;
	    mAverage = 0;
	    mTotal = 0;
	}

	

	

	
}

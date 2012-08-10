package org.t2health.lib1.dsp;

public class T2PeriodAverageFilter extends T2Filter {
	public static final int AVERAGING = Integer.MAX_VALUE;
    private int mAverage;
	private int mIndex;
	private int mSize;
	private int mTotal;
	
	
	public T2PeriodAverageFilter(int size) {
		mSize = size;
	    reset();
	}
	
	@Override
	public    int    filter( int value ) {	
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
	   
	public int getAverage() {
		return mAverage;
	}
	
	
	
	public void reset() {
	    mIndex = 0;
	    mAverage = 0;
	    mTotal = 0;
	}

	

	

	
}

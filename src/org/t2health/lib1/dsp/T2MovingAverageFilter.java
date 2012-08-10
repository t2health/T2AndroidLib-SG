package org.t2health.lib1.dsp;

import java.util.Arrays;

public class T2MovingAverageFilter extends T2Filter {

	private int circularBuffer[];
	
    private int mean;
    private int total;
	private int circularIndex;
	private int count;
	private int size;
	private int min;
	private int max;
	
	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
	
	public T2MovingAverageFilter(int size) {
		circularBuffer = new int[size];
	    reset();
	}
	
	@Override
	public    int    filter( int x ) {
		if (count++ == 0) {
	        primeBuffer(x);
	    }
	    int lastValue = circularBuffer[circularIndex];
	    total -= lastValue;
	    total += x;
	    mean = total / circularBuffer.length;
	    circularBuffer[circularIndex] = x;
	    circularIndex = nextIndex(circularIndex);
	    
	    // Set min and max
	    int[] tmp = circularBuffer.clone();
	    Arrays.sort(tmp);
	    min = tmp[0];
	    max = tmp[tmp.length - 1];
	    
		return mean;
	}       
	   
	public int getValue() {
		return mean;
	}
	
	public double getVariance() {
		
		int sdIndex = circularIndex;		
		long n = 0;
		double mean = 0;
		double s = 0.0;
		
	    for (int i = 0; i < circularBuffer.length; ++i) {
	    	double val = circularBuffer[sdIndex];
			++n;
			double delta = val - mean;
			mean += delta / n;
			s += delta * (val - mean);
			
			sdIndex = nextIndex(sdIndex);
						
	    }		
		return (s / n);		
	}
	
	public double getStdDev() {
		return Math.sqrt(getVariance());
	}	
	
	public void reset() {
	    count = 0;
	    circularIndex = 0;
	    mean = 0;
	    total = 0;
	}
	
	public long getCount() {
	    return count;
	}
	
	private void primeBuffer(int val) {
	    for (int i = 0; i < circularBuffer.length; ++i) {
	        circularBuffer[i] = val;
	        total += val;
	    }
	    mean = val;
	}
	
	private int nextIndex(int curIndex) {
	    if (curIndex + 1 >= circularBuffer.length) {
	        return 0;
	    }
	    return curIndex + 1;
	}	
	
}

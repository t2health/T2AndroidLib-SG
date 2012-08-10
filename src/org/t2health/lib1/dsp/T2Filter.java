package org.t2health.lib1.dsp;

public class T2Filter {

	  /**
	   * Method to filter a fixed-length sequence with this filter.
	   *
	   * @param x       double[] containing the input sequence..
	   * @return        double[] containing the resulting filtered sequence.
	   */
	  public    double[]    filter( double[] x ) {
		  return null;
	  }
	
	  /**
	   * Method to filter a single-length sequence with this filter.
	   *
	   * @param x       double containing the input sequence.
	   * @return        double containing the resulting filtered sequence.
	   */
	  public    double    filter( double x ) {
		  return x;
	  }

	  
	  /**
	   * Method to filter a single-length sequence with this filter.
	   *
	   * @param x       int containing the input sequence.
	   * @return        int containing the resulting filtered sequence.
	   */
	  public    int    filter( int sampleValue ) {
		  return sampleValue;
	  }
	
	  /**
	   * Method to filter a single-length sequence with this filter.
	   *
	   * @param x       int containing the input sequence.
	   * @return        int containing the resulting filtered sequence.
	   */
	  public    int    filter( int sampleValue , int sampleTimeStamp) {
		  return sampleValue;
	  }
	
}

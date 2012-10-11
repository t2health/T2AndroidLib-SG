/*
 * 
 */

package com.oregondsp.signalProcessing.filter.iir;


/**
 * Enum PassbandType used to specify the pass band type (lowpass, highpass, bandpass) of analog and digital filters.
 * 
 * @author David B. Harris,  Deschutes Signal Processing LLC
 */
public enum PassbandType { 
  
 /** Specifies a lowpass filter */
 LOWPASS, 
 /** Specifies a bandpass filter */
 BANDPASS, 
 /** Specifies a highpass filter */
 HIGHPASS }

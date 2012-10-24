/* T2AndroidLib for Signal Processing
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
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
package org.t2health.lib1;
import java.util.HashMap;

import org.t2health.lib1.dsp.T2MovingAverageFilter;
import org.t2health.lib1.dsp.T2RateOfChangeFilter;



public class BioParameter {


	
	private static final int SMOOTHING = 8;	
	
	public long id;
	public String title1;
	public String title2;
	public int color;
	public boolean visible;
	public boolean reverseData = false; 
    public T2MovingAverageFilter mMovingAverage = new T2MovingAverageFilter(10);
    public T2RateOfChangeFilter mRateOfChange = new T2RateOfChangeFilter(6);
//	public XYSeries xySeries;	
    public BioSensor mSensor = null;
     
    public Boolean mEnabled = false;
    public int mParameterOutput;;
    

	

	public int smoothedValue;
	public int rawValue;
	public int scaledValue;
	public int filteredValue;

	private int maxFilteredValue = 0;
	private int minFilteredValue = 9999;
	private int numFilterSamples = 0;
	private long totalOfFilterSamples = 0;



	
	public int getMaxFilteredValue() {
		return maxFilteredValue;
	}


	public void setMaxFilteredValue(int maxFilteredValue) {
		this.maxFilteredValue = maxFilteredValue;
	}


	public int getMinFilteredValue() {
		return minFilteredValue;
	}


	public void setMinFilteredValue(int minFilteredValue) {
		this.minFilteredValue = minFilteredValue;
	}


	public int getAvgFilteredValue() {
		return numFilterSamples != 0 ? (int) (totalOfFilterSamples / numFilterSamples) :0;
	}



	public int getRawValue() {
		return rawValue;
	}


	public void setRawValue(int aRawValue) {
		rawValue = aRawValue;
	    
	}



	public int getScaledValue() {
		return scaledValue;
	}

	public int getFilteredScaledValue() {
		return (int) mMovingAverage.getValue();
	}

	public int getRateOfChangeScaledValue() {
		int filteredLotusValue = (int) (mRateOfChange.getValue() * 10);

		if (filteredLotusValue > 255) filteredLotusValue = 255;
		
		return filteredLotusValue;
	}

	public int updateSmoothedValue() {
	    smoothedValue += (rawValue - smoothedValue) / SMOOTHING;
		return smoothedValue;
	}

	public int getSmoothedValue() {
		return smoothedValue;
	}


	public void updateRateOfChange() {
		mRateOfChange.pushValue(scaledValue);
	}
	
	public void setScaledValue(int scaledValue) {
		this.scaledValue = scaledValue;
		mMovingAverage.filter(scaledValue);
		
		// Now do stats
		int value = (int) mMovingAverage.getValue();
		numFilterSamples++;
		totalOfFilterSamples += value;
		
		if (value >= maxFilteredValue) maxFilteredValue = value;
		if (value < minFilteredValue) minFilteredValue = value;
	}


	
	
	public BioParameter(long id, String title1, String title2, Boolean enabled) {

		this.id = id;
		this.title1 = title1;
		this.title2 = title2;
		this.visible = true;
		this.mEnabled = enabled;

		
//		xySeries = new XYSeries(title1);		
		
	}
	
	
	public HashMap<String,Object> toHashMap() {
		HashMap<String,Object> data = new HashMap<String,Object>();
		data.put("id", id);
		data.put("title1", title1);
		data.put("title2", title2);
		data.put("color", color);
		data.put("visible", visible);
		return data;
	}
}	
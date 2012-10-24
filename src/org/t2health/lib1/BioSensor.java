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


public class BioSensor {
	
	public static final int CONN_ERROR = -1;
	public static final int CONN_IDLE = 0;
	public static final int CONN_PAIRED = 1;
	public static final int CONN_CONNECTING = 2;
	public static final int CONN_CONNECTED = 3;
	
	public String mBTName;
	public String mBTAddress;
	public int mConnectionStatus;
	public Boolean mEnabled;

	/**
	 * A list of names of all of the parameters that this sensor can supply
	 */
	public String mParameterNames = "";
	
	public BioSensor(String btName, String btAddress, Boolean enabled) {
		this.mBTName = btName;
		this.mBTAddress = btAddress;
		this.mEnabled = enabled;
		
		if (btName.startsWith("BH")) {
			this.mParameterNames = "HeartRate, SkinTemp, RespRate";
		}
		if (btName.startsWith("RN42")) {
			this.mParameterNames = "HeartRate, EMG, GSR";
		}
		if (btName.startsWith("TestSensor")) {
			this.mParameterNames = "HeartRate, EMG, GSR, SkinTemp, RespRate";
		}
	}
	

}

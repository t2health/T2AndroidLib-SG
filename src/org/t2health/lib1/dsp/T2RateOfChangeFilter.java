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
 package org.t2health.lib1.dsp;


 public class T2RateOfChangeFilter extends T2Filter {
	 private float circularBuffer[];
	 	private float mean;
	 	private float instantChange;
        private int circularIndex;
        private int count;

        public T2RateOfChangeFilter(int size) {
            circularBuffer = new float[size];
            reset();
        }

        public float getValue() {
        	float total = 0;
        	int len = count < circularBuffer.length  ? count: circularBuffer.length;
        	for (int i = 0; i < len - 1; ++i) {
        		float v1 = circularBuffer[i];
        		float v2 = circularBuffer[nextIndex(i)];
        		float diff = Math.abs(v2 - v1);
        		total += diff;
            }

           float  roc = total / (float) len;
           return roc;
        }


        public void pushValue(float x) {
            if (count++ == 0) {
                primeBuffer(0);
            }
            float lastValue = circularBuffer[circularIndex];
            instantChange = x - lastValue;
            circularBuffer[circularIndex] = x;
            circularIndex = nextIndex(circularIndex);
        }

        public void reset() {
            count = 0;
            circularIndex = 0;
            mean = 0;
        }

        public long getCount() {
            return count;
        }

        private void primeBuffer(float val) {
            for (int i = 0; i < circularBuffer.length; ++i) {
                circularBuffer[i] = val;
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
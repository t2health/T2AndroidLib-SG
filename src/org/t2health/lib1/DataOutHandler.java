/* T2AndroidLib-SG for Signal Processing
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Vector;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONObject;
import org.t2health.lib1.LogWriter;

import com.amazonaws.AmazonServiceException;
import com.t2.aws.Constants;
import com.t2.aws.DynamoDBManager;
import com.t2.aws.PropertyLoader;
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.PutItemRequest;
import com.amazonaws.tvmclient.AmazonClientManager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

//T2 CAS Stuff
//import com.t2auth.AuthUtils;
//import com.t2auth.AuthUtils.H2PostEntryTask;
//import com.t2auth.AuthUtils.T2LogoutTask;
//import com.t2auth.AuthUtils.T2ServiceTicketTask;

//CouchDB Stuff
//import org.ektorp.CouchDbConnector;
//import org.ektorp.CouchDbInstance;
//import org.ektorp.ReplicationCommand;
//import org.ektorp.http.HttpClient;
//import com.couchbase.touchdb.TDServer;
//import com.couchbase.touchdb.router.TDURLStreamHandlerFactory;

/**
 * Handles distribution of processed Biometric data
 *   Using DataOutHandler relieves the calling activity of the burden of knowing
 *   where to sent it's data
 *   
 *   One of the data sinks that will be used in the future in database. This class will 
 *   encapsulate all of the database particulars from the calling activity
 *   
 *   
 *   Currently data is stored in two formats:
 *   Text (mStr) for output to log files
 *   JSON format (mItem) for output to TouchDB
 *   
 *   Potentially these should be merged into one but right now it's 
 *   separate because we don't want as much cluttering up log files.
 * 
 * @author scott.coleman
 *
 */
public class DataOutHandler {
	private final String TAG = getClass().getName();	

	//private static final String DEFAULT_REST_DB_URL = "http://gap.t2health.org/and/phpWebservice/webservice2.php";	 
	// private static final String DEFAULT_REST_DB_URL = "http://gap.t2health.org/and/json.php";	 
	private static final String DEFAULT_REST_DB_URL = "http://ec2-50-112-197-66.us-west-2.compute.amazonaws.com/mongo/json.php";
	private static final String DEFAULT_AWS_DB_URL = "h2tvm.elasticbeanstalk.com";
	
	private static final int LOG_FORMAT_JSON = 1;	
	private static final int LOG_FORMAT_FLAT = 2;	
	
	public static final String TIME_STAMP = "\"TS\"";
	public static final String SENSOR_TIME_STAMP = "STS";
	public static final String RAW_GSR = "GSR";					// Microsiemens
	public static final String AVERAGE_GSR = "GSRAVG";			// Microsiemens 1 sec average
	public static final String USER_ID = "UID";
	public static final String SESSION_ID = "SES";
	public static final String SENSOR_ID = "SID"; 	
	public static final String RAW_HEARTRATE = "HR"; 			// BPM
	public static final String RAW_SKINTEMP = "ST"; 			// Degrees
	public static final String RAW_EMG = "EMG"; 				// 
	public static final String AVERAGE_HEARTRATE = "HRAVG"; 	// BPM 3 sec average
	public static final String RAW_ECG = "ECG";
	public static final String FILTERED_ECG = "FECG";
	public static final String RAW_RESP_RATE = "RR";			// BPM
	public static final String AVERAGE_RESP_RATE = "RRAVG";		// BPM 10 sec average
	public static final String COMPLETION_PERCENT = "COM";
	public static final String DURATION = "DUR";					// seconds
	public static final String NOTATION = "NOT";
	public static final String CATEGORY = "CAT";
	public static final String EEG_SPECTRAL = "EEG";
	public static final String EEG_SIG_STRENGTH = "EEGSIG";
	public static final String EEG_ATTENTION = "EEGATT";
	public static final String EEG_MEDITATION = "EEGMED";
	public static final String HRV_RAW_IBI = "IBI";					// HR inter-beat interval (Ms)
	public static final String HRV_LF_NU = "LFNU";					// HR low frequency content normalized units (0 - 100)
	public static final String HRV_HF_NU = "HFNU";					// HR low frequency content normalized units (0 - 100)
	public static final String HRV_FFT = "HRVFFT";					// FFT of IBI
	public static final String HRV_RAW_SDNN = "SDNN";				// SDNN
	public static final String NOTE = "note";
	public static final String AIRFLOW = "AIRFLOW";
	public static final String SPO2 = "SPO2";

	public static final String DATA_TYPE_RATING = "RatingData";
	public static final String DATA_TYPE_INTERNAL_SENSOR = "InternalSensor";
	public static final String DATA_TYPE_EXTERNAL_SENSOR = "ExternalSensor";
	
	public boolean mLogCatEnabled = false;	
	public boolean mLoggingEnabled = false;	
	private boolean mDatabaseEnabled = false;
	private boolean mSessionIdEnabled = false;
	
	public String mUserId = "";
	public String mSessionDate = "";
	public String mAppName = "";
	public String mDataType = "";
	private LogWriter mLogWriter;	
	private Context mContext;
	private int mLogFormat = LOG_FORMAT_JSON;	// Alternatively LOG_FORMAT_FLAT 	
	private long mSessionId;
	
	/**
	 * URL of the remote database we are saving to
	 */
	String mRemoteDatabase;

	/**
	 * Queue for Rest packets waiting to be sent via HTTP
	 */
	List<T2RestPacket> mPendingQueue;

	/**
	 * Thread used to communicate messages in mPendingQueue to server
	 */
	private DispatchThread mDispatchThread = null;	
	
	
    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	/**
	 * Application version info determined by the package manager
	 */
	private String mApplicationVersion = "";

	// T2 CAS Stuff
	//    private T2ServiceTicketTask mServiceTicketTask = null;
	//    private H2PostEntryTask mPostEntryTask = null;
    
	/**
	 * Database manager when sending data to external Amazon database
	 */
	public static AmazonClientManager clientManager = null;		

	// Right now we're only supporting two database types.
	//	T2 Rest server (goes to Mongo DB)
	//	AWS (Goes to AWS DynamoDB
	private final static int DATABASE_TYPE_AWS = 0;
	private final static int DATABASE_TYPE_T2_REST = 1;
	
	private int mDatabaseType = DATABASE_TYPE_AWS; // Default to AWS
	
	/**
	 * Constructor. Initializes context and user/session parameters
	 * 
	 * @param context	- Context of calling activity
	 * @param userId	- User ID detected by calling activity 
	 * @param sessionDate - Session date created by the calling activity (data/time stamp)
	 */
	public DataOutHandler(Context context, String userId, String sessionDate, String appName) {
		mAppName = appName;
		mContext = context;
		mUserId = userId;
		mSessionDate = sessionDate;
		mSessionIdEnabled = false;
	}
	
	/**
	 * Constructor. Initializes context and user/session parameters
	 * 
	 * @param context	- Context of calling activity
	 * @param userId	- User ID detected by calling activity 
	 * @param sessionDate - Session date created by the calling activity (data/time stamp)
	 * @param sessionId - long session ID to be included in all packets
	 */
	public DataOutHandler(Context context, String userId, String sessionDate, String appName, String dataType, long sessionId) {
		mAppName = appName;
		mDataType = dataType;
		mContext = context;
		mUserId = userId;
		mSessionDate = sessionDate;
		mSessionIdEnabled = true;
		mSessionId = sessionId;
	}
	
	public void disableDatabase() {
		mDatabaseEnabled = false;
	}
	
	public void enableDatabase() {
		mDatabaseEnabled = true;
	}
	
			
	/**
	 * @author scott.coleman
	 * Task to check the status of an AWS database table
	 */
	class CheckTableStatusTask extends AsyncTask<String, Void, String> {

	    private Exception exception;

	    protected String doInBackground(String... urls) {
	        try {
				String tableStatus = DynamoDBManager.getTestTableStatus();
				String status = tableStatus;
	        	
	            return status;
	        } catch (Exception e) {
	            this.exception = e;
	            return "";
	        }
	    }

	    protected void onPostExecute(String status) {
	    	Log.d(TAG, "Database status = " + status);
	    }
	 }	
	
	/**
	 * Initializes the current database
	 * 
	 * Note that all of the parameters (with the exception of remoteDatabase) sent to this routine are for CouchDB only.
	 * Currently they are all N/A
	 * 
	 * @param databaseName		N/A Local SQLITE database name
	 * @param designDocName		N/A Design document name
	 * @param designDocId		N/A Design document ID
	 * @param viewName			N/AView associated with database
	 * @param remoteDatabase	Name of external database
	 */
	public void initializeDatabase(String databaseName, String designDocName, String designDocId, String viewName, String remoteDatabase) {

		mDatabaseEnabled = true;
		
		// Get chosen database from preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		String sDatabaseType = prefs.getString("external_database_type", "AWS");
		if (sDatabaseType.equalsIgnoreCase("AWS"))
			mDatabaseType = DATABASE_TYPE_AWS;
		else
			mDatabaseType = DATABASE_TYPE_T2_REST;

		
		// Set up mRemoteDatabase based on either remoteDatabase if it's not blank,
		// or default values based on database type
		switch (mDatabaseType) {
		default:
		case DATABASE_TYPE_AWS:
			Log.d(TAG, "Using AWS Database type");
			if (remoteDatabase != null ) {
				if (remoteDatabase.equalsIgnoreCase("")) {
					mRemoteDatabase = DEFAULT_AWS_DB_URL;			
				}
				else {
					mRemoteDatabase = remoteDatabase;
				}
			}
			break;
		
		case DATABASE_TYPE_T2_REST:
			Log.d(TAG, "Using T2 Rest Database type");
			if (remoteDatabase != null ) {
				if (remoteDatabase.equalsIgnoreCase("")) {
					mRemoteDatabase = DEFAULT_REST_DB_URL;			
				}
				else {
					mRemoteDatabase = remoteDatabase;
				}
			}
			break;
		}
		
		
		Log.d(TAG, "Initializing T2 database dispatcher");
		Log.d(TAG, "Remote database name = " + mRemoteDatabase);
		mPendingQueue = new ArrayList<T2RestPacket>();		
		mDispatchThread = new DispatchThread();
		mDispatchThread.start();		
		

		if (mDatabaseType == DATABASE_TYPE_AWS) {
			// Create AmazonClientManager with SharedPreference
			clientManager = new AmazonClientManager(mContext.getSharedPreferences(
					"com.amazon.aws.demo.AWSDemo", Context.MODE_PRIVATE), mRemoteDatabase);	
			
			// TBD - we should probably check the table status
			//new CheckTableStatusTask().execute("");
		}
	}		

	/**
	 * @param context	Calling party's context
	 * 
	 *  enables logging to external log file of entries sent to the database
	 */
	public void enableLogging(Context context) {
		try {
			mLogWriter = new LogWriter(context);	
			String logFileName = mUserId + "_" + mSessionDate + ".log";			
			mLogWriter.open(logFileName, true);	
			mLoggingEnabled = true;
			
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSSZ", Locale.US);
			String timeId = sdf.format(new Date());			
			
			PackageManager packageManager = context.getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);			
			mApplicationVersion = info.versionName;
			
			if (mLogFormat == LOG_FORMAT_JSON) {
				String preamble = String.format(
						"{\"userId\" : \"%s\",\n" +
						"\"sessionDate\" : \"%s\",\n" + 
						"\"timeId\" : \"%s\",\n" + 
						"\"versionId\" : \"%s\",\n" + 
						"\"data\":[",  
						mUserId, mSessionDate, timeId, mApplicationVersion);
				mLogWriter.write(preamble);
			}
		} catch (Exception e) {
			Log.e(TAG, "Exception enabling logging: " + e.toString());
		}
	}

	public void enableLogCat() {
		mLogCatEnabled = true;
	}	
	
	
	public void purgeLogFile() {
		if (mLoggingEnabled) {
			if (mLogFormat == LOG_FORMAT_JSON) {
				mLogWriter.write("],}");
			}
			mLogWriter.close();
			
			enableLogging(mContext);			
		}		
	}
	
	/**
	 * Closes out any open log files and data connections
	 */
	public void close() {

		Log.e(TAG, " ***********************************closing ******************************");
		if (mLoggingEnabled) {
			if (mLogFormat == LOG_FORMAT_JSON) {
				mLogWriter.write("],}");
			}
			mLogWriter.close();			
		}
		
		if(mDispatchThread != null) {
			mDispatchThread.cancel();
			mDispatchThread.interrupt();
			mDispatchThread = null;
		}
	}

	/**
	 * Data packet used to accumulate data to be sent using DataOutHandler
	 * 
	 * This class encapsulates one JSON object which holds any number of related data
	 * 
	 * Note: We use this class instead of just building a JSON packet and sending it
	 * because AWS doesn't directly accept JSON, it expects a HashMap.
	 * 
	 * @author scott.coleman
	 *
	 */
	public class DataOutPacket {
		
		public String mStr = "";
		public ObjectNode mItem;		
		public ObjectNode mData;		
		HashMap<String, AttributeValue> hashMap = new HashMap<String, AttributeValue>();		
		
		public DataOutPacket() {
	    	UUID uuid = UUID.randomUUID();
	    	Calendar calendar = GregorianCalendar.getInstance();
	    	long currentTime = calendar.getTimeInMillis();
	    	dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
	        String currentTimeString = dateFormatter.format(calendar.getTime());
	    	String id = currentTime + "-" + uuid.toString();

			if (mLogFormat == LOG_FORMAT_JSON) {
				mStr = "{" + TIME_STAMP + ":" + currentTime + ",";			
			}
			else {
				mStr = TIME_STAMP + ",";			
			}
			
	    	mData = JsonNodeFactory.instance.objectNode();		
	    	mItem = JsonNodeFactory.instance.objectNode();		
	    	mItem.put("record_id", id);
	    	mItem.put("time_stamp", currentTime);
	    	mItem.put("created_at", currentTimeString);
	    	mItem.put("user_id", mUserId);
	    	mItem.put("session_date", mSessionDate);
	    	if (mSessionIdEnabled) {
		    	mItem.put("session_id", mSessionId);
	    	}
	    	mItem.put("app_name", mAppName);
	    	mItem.put("data_type", mDataType);
	    	mItem.put("platform", "Android");		    	
	    	mItem.put("platform_version", Build.VERSION.RELEASE);		    	
	    	
			if (mDatabaseType == DATABASE_TYPE_AWS) {

				HashMap<String, AttributeValue> hashMap = new HashMap<String, AttributeValue>();
		    	
		    	addAttributeWithS("record_id", id);
		    	addAttributeWithS("time_stamp",String.valueOf(currentTime) );
		    	addAttributeWithS("created_at",currentTimeString );
		    	addAttributeWithS("user_id", mUserId);
		    	addAttributeWithS("session_date", mSessionDate);
		    	addAttributeWithS("session_id", String.valueOf(mSessionId));
		    	addAttributeWithS("app_name", mAppName);
		    	addAttributeWithS("data_type", mDataType);
		    	addAttributeWithS("platform", "Android");
		    	addAttributeWithS("platform_version", Build.VERSION.RELEASE);
			}
		}

		void addAttributeWithS(String identifier, String attrVal) {
			
			if (attrVal.equalsIgnoreCase(""))
				return;
				
			AttributeValue attr = new AttributeValue().withS(attrVal);	
			hashMap.put(identifier, attr);			
		}
		
		void addAttributeWithSS(String identifier, Vector attrVal) {
			
			if (attrVal.size()== 0)
				return;
				
			AttributeValue attr = new AttributeValue().withSS(attrVal);	
			hashMap.put(identifier, attr);			
		}		
		
		/**
		 * Adds a tag/data pair to the packet
		 * @param tag
		 * @param value
		 */
		public void add(String tag, double value) {
			
			if (mLogFormat == LOG_FORMAT_JSON) {
				mStr += String.format("%s:%f,", tag,value);
			}
			else {
				mStr += "" + value + ",";			
			}
			
			mItem.put(tag,value);	
			if (mDatabaseType == DATABASE_TYPE_AWS) {
		    	addAttributeWithS(tag, String.valueOf(value));
			}			
		}
		
		/**
		 * Adds a tag/data pair to the packet
		 * @param tag
		 * @param value
		 */
		public void add(String tag, double value, String format) {
			
			if (mLogFormat == LOG_FORMAT_JSON) {
				mStr += String.format("%s:" + format + ",", tag,value);
			}
			else {
				mStr += "" + value + ",";			
			}
			mItem.put(tag,value);	
			if (mDatabaseType == DATABASE_TYPE_AWS) {
		    	addAttributeWithS(tag, String.valueOf(value));
			}			
				
		}
		
		/**
		 * Adds a tag/data pair to the packet
		 * @param tag
		 * @param value
		 */
		public void add(String tag, long value) {
			if (mLogFormat == LOG_FORMAT_JSON) {
				mStr += tag + ":" + value + ",";			
			}
			else {
				mStr += "" + value + ",";			
			}
			mItem.put(tag,value);	
			if (mDatabaseType == DATABASE_TYPE_AWS) {
		    	addAttributeWithS(tag, String.valueOf(value));
			}			
				
		}

		/**
		 * Adds a tag/data pair to the packet
		 * @param tag
		 * @param value
		 */
		public void add(String tag, int value) {
			if (mLogFormat == LOG_FORMAT_JSON) {
				mStr += tag + ":" + value + ",";			
			}
			else {
				mStr += "" + value + ",";			
			}
			mItem.put(tag,value);
			if (mDatabaseType == DATABASE_TYPE_AWS) {
		    	addAttributeWithS(tag, String.valueOf(value));
			}			
					
		}

		/**
		 * Adds a tag/data pair to the packet
		 * @param tag
		 * @param value
		 */
		public void add(String tag, String value) {
			if (mLogFormat == LOG_FORMAT_JSON) {
				mStr += tag + ":\"" + value + "\",";			
			}
			else {
				mStr += "" + value + ",";			
			}
			mItem.put(tag,value);	
			if (mDatabaseType == DATABASE_TYPE_AWS) {
		    	addAttributeWithS(tag, String.valueOf(value));
			}			
				
		}
		
		/**
		 * Adds a tag/data pair to the packet
		 * @param tag
		 * @param value
		 */
		public void add(String tag, Vector values) {
			if (mLogFormat == LOG_FORMAT_JSON) {
				mStr += tag + ":\"" + values.toString() + "\",";			
			}
			else {
				mStr += "" + values.toString() + ",";			
			}
			mItem.put(tag,values.toString());	
			if (mDatabaseType == DATABASE_TYPE_AWS) {
				addAttributeWithSS(tag, values);
			}			
				
		}
		
		public String toString() {
			return mStr;
		}
		
	}

	// Note - the following two routines are Deprecated. Use 
	//	DataOutPacket instead of sending a JSON object or array
	
	public void handleDataOut(final ObjectNode jsonObject) { // This one uses Android Jackson JSON objects
		DataOutPacket packet = new DataOutPacket();
		// To match our format we must remove the starting and ending curly brace
		String tmp = jsonObject.toString();
		tmp = tmp.substring(1,tmp.length() - 1);
		packet.mStr += tmp;
		
		packet.mData.put("data", jsonObject);
		
		handleDataOut(packet);
	}
	
	
	public void handleDataOut(final ArrayNode jsonArray) {
		DataOutPacket packet = new DataOutPacket();
		// To match our format we must remove the starting and ending curly brace
		String tmp = jsonArray.toString();
		tmp = tmp.substring(1,tmp.length() - 1);
		packet.mStr += tmp;
		
		packet.mData.put("data", jsonArray);
		handleDataOut(packet);
	}	
	
	/**
	 * Sends a data packet to all configured output sinks
	 * Actually it just puts it in the mPendingQueue to
	 * be sent out later 
	 * 
	 * @param packet - data Packet to send to output sinks
	 */
	public void handleDataOut(final DataOutPacket packet) {

		//packet.mItem.put("data", packet.mData);		
		
		if (mLogFormat == LOG_FORMAT_JSON) {
			packet.mStr += "},";
		}

		if (mLoggingEnabled) {	
			mLogWriter.write(packet.mStr);
		}

		if (mLogCatEnabled) {
			Log.d(TAG, packet.mStr);			
		}
		
		if (mDatabaseEnabled) {
			String dataPacketString = packet.mItem.toString();
			T2RestPacket pkt = new T2RestPacket(dataPacketString, packet.hashMap);
			
			Log.d(TAG, "Queueing document " + pkt.mId);

			synchronized(mPendingQueue) {
				mPendingQueue.add(0,  pkt);
			}
		}
	}

	/**
	 * Logs a text note to sinks
	 * 
	 * @param note - Text not to log to sinks
	 */
	public void logNote(String note) {
		DataOutPacket packet = new DataOutPacket();
		packet.add(NOTE, note);
		handleDataOut(packet);				
	}
	
	
	/**
	 * @author scott.coleman
	 *
	 * This thread handles maintenance of the mPendingQueue
	 * sending data out if the network is available.
	 * 
	 */
	class DispatchThread extends Thread {
		private boolean isRunning = false;
		private boolean cancelled = false;

		@Override
		public void run() {
			isRunning = true;
			
			while(true) {
				// Break out if this was cancelled.
				if(cancelled) {
					break;
				}
				
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d(TAG, "Http dispatch thread tick");

				// If the network is available post entries from the PendingQueue
				if (isNetworkAvailable()) {
					synchronized(mPendingQueue) {
	
						if (mPendingQueue.size() > 0) {
							Log.d(TAG, "pending queue size =  " + mPendingQueue.size() );

							// Fill the posting Queue with all of the items that need to be posted
							// We need this array so when we get a response we can remove all of these entries from the PendingQueue
							String jsonString  = "[";
							int iteration = 0;
							Iterator<T2RestPacket> iterator = mPendingQueue.iterator();						
							while(iterator.hasNext()) {
								
								T2RestPacket packet = iterator.next();
								Log.d(TAG, "Posting document " + packet.mId);
								if (packet.mStatus == T2RestPacket.STATUS_PENDING) {
									
									 packet.mStatus = T2RestPacket.STATUS_POSTED;
									 
									 if (iteration++ > 0)
										 jsonString += "," + packet.mJson;
									 else
										 jsonString += packet.mJson;
										 
								}
								
								if (mDatabaseType == DATABASE_TYPE_AWS) {
									AmazonDynamoDBClient ddb = DataOutHandler.clientManager
											.ddb();
									try {
										
										PutItemRequest request = new PutItemRequest().withTableName(
												PropertyLoader.getInstance().getTestTableName())
												.withItem(packet.hashMap);

										ddb.putItem(request);
										Log.d(TAG, "AWS Posting Successful: ");
										
									} catch (AmazonServiceException ex) {
										DataOutHandler.clientManager
												.wipeCredentialsOnAuthError(ex);
										Log.d(TAG, "Error posting document " + ex.toString());
									}	
									catch (Exception ex) {
										DataOutHandler.clientManager.clearCredentials();
										Log.d(TAG, "Error posting document " + ex.toString());
									}									
								}
							}
							
							jsonString += "]";
							
							if (mDatabaseType == DATABASE_TYPE_T2_REST) {
								RequestParams params = new RequestParams("json", jsonString);
								Log.d(TAG, "Sending to: " + mRemoteDatabase);
								
						        T2RestClient.post(mRemoteDatabase, params, new AsyncHttpResponseHandler() {
						            @Override
						            public void onSuccess(String response) {
										Log.d(TAG, "T2Rest Posting Successful: " + response);
						                
						            }
						        });	
							}							

//							if (databaseType == DatabaseType.T2_CAS) {
//								sendViaCas(jsonString);
//							}							
					        
							mPendingQueue.clear();
						} // End if (mPendingQueue.size() > 0)
					} // End synchronized(mPendingQueue) 
				}
			} // End while(true)

			isRunning = false;
		} // End public void run() 
		
		public void cancel() {
			this.cancelled = true;
			Log.e(TAG, "Cancelled");
			
		}
		
		public boolean isRunning() {
			return this.isRunning;
		}
	}
	
    /**
     * @return true if network is available
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) 
          mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    } 	
	
//	private void sendViaCas(final String entry) {
//
//		Log.d(TAG, "Sending via cas:  " + entry);
//
//        if (mServiceTicketTask != null) {
//            return;
//        }
//		
//		// First we need to get a service ticket, then do the actual send
//		mServiceTicketTask = new T2ServiceTicketTask(AuthUtils.APPLICATION_NAME,
//                AuthUtils.getTicketGrantingTicket(mContext),
//                AuthUtils.getSslContext(mContext).getSocketFactory()) {
//            @Override
//            protected void onTicketRequestSuccess(String serviceTicket) {
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
//                prefs.edit().putString(mContext.getString(R.string.pref_st), serviceTicket).commit();
//
//                mServiceTicketTask = null;
//				Log.d(TAG, "TicketRequest Success: ");
//
//                mPostEntryTask = new H2PostEntryTask(AuthUtils.getServiceTicket(mContext), entry) {
//
//					@Override
//					protected void onPostSuccess(String response) {
//						Log.d(TAG, "Posting Successful: " + response);
//						
//					}
//
//					@Override
//					protected void onPostFailed() {
//						
//					}
//                	
//                };                
//                mPostEntryTask.execute((Void) null);
//            }
//
//            @Override
//            protected void onTicketRequestFailed() {
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
//                prefs.edit().remove(mContext.getString(R.string.pref_tgt)).commit();
//                
//                mServiceTicketTask = null;
//            }
//
//            @Override
//            protected void onCancelled() {
//                super.onCancelled();
//                mServiceTicketTask = null;
//            }
//        };
//        mServiceTicketTask.execute((Void) null);		
//	}
}
package org.t2health.lib1;



import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.ReplicationCommand;
import org.ektorp.UpdateConflictException;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbInstance;
import org.json.JSONArray;
import org.json.JSONObject;
import org.t2health.lib1.LogWriter;

import com.couchbase.touchdb.TDDatabase;
import com.couchbase.touchdb.TDServer;
import com.couchbase.touchdb.TDView;
import com.couchbase.touchdb.TDViewMapBlock;
import com.couchbase.touchdb.TDViewMapEmitBlock;
import com.couchbase.touchdb.ektorp.TouchDBHttpClient;
import com.couchbase.touchdb.router.TDURLStreamHandlerFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;



/**
 * Handles distribution of processed biometric data
 *   Using DataOutHandler relieves the calling activity of the burdon of knowing
 *   where to sent it's data
 *   
 *   One of the data sinks that will be used in the future in database. Thiss class will 
 *   encapsulate all of the database particuolars from the calling activity
 *   
 *   
 *   Currently data is stored in two formats:
 *   Text (mStr) for output to log files
 *   JSON format (mItem) for output to TouchDB
 *   
 *   Potentially these should be merged into one but right now it's 
 *   seperate because we don't want as much cluttering up log files.
 * 
 * @author scott.coleman
 *
 */
public class DataOutHandler {
	private static final String TAG = "BFDemo";	
	private static final int LOG_FORMAT_JSON = 1;	
	private static final int LOG_FORMAT_FLAT = 2;	
	
	public static final String TIME_STAMP = "TS";
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

	
	public boolean mLogCatEnabled = false;	
	public boolean mLoggingEnabled = false;	
	private boolean mDatabaseEnabled = false;
	
	public String mUserId = "";
	public String mSessionId = "";
	public String mAppName = "";
	private LogWriter mLogWriter;	
	private Context mContext;
	private int mLogFormat = LOG_FORMAT_JSON;	
//	private int mLogFormat = LOG_FORMAT_FLAT;	
	
	//couch internals
	protected static TDServer server;
	protected static HttpClient httpClient;

	//ektorp impl
	protected CouchDbInstance dbInstance;
	protected CouchDbConnector couchDbConnector;
	protected ReplicationCommand pushReplicationCommand;
	protected ReplicationCommand pullReplicationCommand;
	
	String mDatabaseName;	
	String mRemoteDatabase;
	
	
    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	/**
	 * Application version info determined by the package manager
	 */
	private String mApplicationVersion = "";
	
    //static inializer to ensure that touchdb:// URLs are handled properly
    {
        TDURLStreamHandlerFactory.registerSelfIgnoreError();
    }	
	
	
	/**
	 * Constructor. Initializes context and user/session parameters
	 * 
	 * @param context	- Context of calling activity
	 * @param userId	- User ID detected by calling activity 
	 * @param sessionId - Session ID created by the calling activity (data/time stamp)
	 */
	public DataOutHandler(Context context, String userId, String sessionId, String appName) {
		mAppName = appName;
		mContext = context;
		mUserId = userId;
		mSessionId = sessionId;
	}
	
	public void disableDatabase() {
		mDatabaseEnabled = false;
	}
	
	public void enableDatabase() {
		mDatabaseEnabled = true;
	}
	
	/**
	 * Starts up TouchDB database
	 * 
	 * @param databaseName		- Local SQLITE database name
	 * @param designDocName
	 * @param designDocId
	 * @param viewName
	 */
	public void initializeDatabase(String databaseName, String designDocName, String designDocId, String viewName, String remoteDatabase) {

		mDatabaseEnabled = true;
		mRemoteDatabase = remoteDatabase;
		mDatabaseName = databaseName;

		Log.v(TAG, "starting TouchBase");

		// Start TouchDB
		String filesDir = mContext.getFilesDir().getAbsolutePath();
	    try {
            server = new TDServer(filesDir);
        } catch (IOException e) {
            Log.e(TAG, "Error starting TDServer", e);
        }		
		
	    //install a view definition needed by the application
	    TDDatabase db = server.getDatabaseNamed(mDatabaseName);
	    
	    
	    TDView view = db.getViewNamed(String.format("%s/%s", designDocName, viewName));
	    view.setMapReduceBlocks(new TDViewMapBlock() {

            @Override
            public void map(Map<String, Object> document, TDViewMapEmitBlock emitter) {
                Object createdAt = document.get("created_at");
                if(createdAt != null) {
                    emitter.emit(createdAt.toString(), document);
                }
            }
        }, null, "1.0");  
	    
	    // Start ektorp
		Log.v(TAG, "starting TouchBase ektorp");

		if(httpClient != null) {
			httpClient.shutdown();
		}

		httpClient = new TouchDBHttpClient(server);
		dbInstance = new StdCouchDbInstance(httpClient);	    

		T2EktorpAsyncTask startupTask = new T2EktorpAsyncTask() {

			@Override
			protected void doInBackground() {
				couchDbConnector = dbInstance.createConnector(mDatabaseName, true);
				Log.v(TAG, "TouchBase Created");
				
			}

			@Override
			protected void onSuccess() {
				// These need to be started manually now
				//startReplications();
				startPushReplications();				
			}
		};
		startupTask.execute();
	}		

	public void startPushReplications() {
		pushReplicationCommand = new ReplicationCommand.Builder()
		.source(mDatabaseName)
		.target(mRemoteDatabase)
		.createTarget(true)
//		.createTarget(false)
		.continuous(true)
		.build();

		T2EktorpAsyncTask pushReplication = new T2EktorpAsyncTask() {
	
			@Override
			protected void doInBackground() {
				dbInstance.replicate(pushReplicationCommand);
			}
		};
	
		pushReplication.execute();		
	}	
	public void startPullReplications() {
		pullReplicationCommand = new ReplicationCommand.Builder()
		.source(mRemoteDatabase)
		.target(mDatabaseName)
		.continuous(true)
		.build();

		T2EktorpAsyncTask pullReplication = new T2EktorpAsyncTask() {
	
			@Override
			protected void doInBackground() {
				dbInstance.replicate(pullReplicationCommand);
			}
		};
	
		pullReplication.execute();		
	}	

	public void stopPushReplications() {
		pushReplicationCommand = new ReplicationCommand.Builder()
		.source(mDatabaseName)
		.target(mRemoteDatabase)
		.cancel(true)
		.build();

		T2EktorpAsyncTask pushReplication = new T2EktorpAsyncTask() {
	
			@Override
			protected void doInBackground() {
				dbInstance.replicate(pushReplicationCommand);
			}
		};
	
		pushReplication.execute();			
	}	
	
	
	public void stopPullReplications() {
		pullReplicationCommand = new ReplicationCommand.Builder()
		.source(mRemoteDatabase)
		.target(mDatabaseName)
		.cancel(true)
		.build();

		T2EktorpAsyncTask pullReplication = new T2EktorpAsyncTask() {
	
			@Override
			protected void doInBackground() {
				dbInstance.replicate(pullReplicationCommand);
			}
		};
	
		pullReplication.execute();			
	}	
	
	public void startReplications() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
	
		pushReplicationCommand = new ReplicationCommand.Builder()
			.source(mDatabaseName)
			.target(mRemoteDatabase)
//			.createTarget(true)
			
			.continuous(true)
			.build();
	
		T2EktorpAsyncTask pushReplication = new T2EktorpAsyncTask() {
	
			@Override
			protected void doInBackground() {
				dbInstance.replicate(pushReplicationCommand);
			}
		};
	
		pushReplication.execute();
	
		pullReplicationCommand = new ReplicationCommand.Builder()
			.source(mRemoteDatabase)
			.target(mDatabaseName)
//			.createTarget(true)
			
			.continuous(true)
			.build();
	
		T2EktorpAsyncTask pullReplication = new T2EktorpAsyncTask() {
	
			@Override
			protected void doInBackground() {
				dbInstance.replicate(pullReplicationCommand);
			}
		};
	
		pullReplication.execute();
	}
	
	
	
	public void enableLogging(Context context) {
		try {
			mLogWriter = new LogWriter(context);	
			String logFileName = mUserId + "_" + mSessionId + ".log";			
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
						"\"sessionId\" : \"%s\",\n" + 
						"\"timeId\" : \"%s\",\n" + 
						"\"versionId\" : \"%s\",\n" + 
						"\"data\":[",  
						mUserId, mSessionId, timeId, mApplicationVersion);
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
		mDatabaseEnabled = false;
		if (mLoggingEnabled) {
			if (mLogFormat == LOG_FORMAT_JSON) {
				mLogWriter.write("],}");
			}
			mLogWriter.close();			
		}
		
		//clean up our http client connection manager
		if(httpClient != null) {
			httpClient.shutdown();
		}

		if(server != null) {
		    server.close();
		}		
	}

	/**
	 * Data packet used to accumulate data to be sent using DataOutHandler
	 * 
	 * This class encapculates one JSON object which holds any number of related data
	 * 
	 * @author scott.coleman
	 *
	 */
	public class DataOutPacket {
		
		public String mStr = "";
		ObjectNode mItem;		
		
		public DataOutPacket() {
	    	UUID uuid = UUID.randomUUID();
	    	Calendar calendar = GregorianCalendar.getInstance();
	    	long currentTime = calendar.getTimeInMillis();
	        String currentTimeString = dateFormatter.format(calendar.getTime());
	    	String id = currentTime + "-" + uuid.toString();

			if (mLogFormat == LOG_FORMAT_JSON) {
				mStr = "{" + TIME_STAMP + ":" + currentTime + ",";			
			}
			else {
				mStr = TIME_STAMP + ",";			
			}
			
			// mItem is for TouchDB only
	    	mItem = JsonNodeFactory.instance.objectNode();		
	    	mItem.put("_id", id);
	    	mItem.put("created_at", currentTimeString);
	    	mItem.put("user_id", mUserId);
	    	mItem.put("session_id", mSessionId);
	    	
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
		}
	}

	public void handleDataOut(final JSONObject jsonObject) {

		DataOutPacket packet = new DataOutPacket();
		// To match our format we must remove the starting and ending curly brace
		String tmp = jsonObject.toString();
		tmp = tmp.substring(1,tmp.length() - 1);
		packet.mStr += tmp;
		
		//packet.mItem.put("data", jsonObject.toString());
//		packet.mItem.putObject(jsonObject);
		
		handleDataOut(packet);
	}	
	
	public void handleDataOut(final ObjectNode jsonObject) {
		DataOutPacket packet = new DataOutPacket();
		// To match our format we must remove the starting and ending curly brace
		String tmp = jsonObject.toString();
		tmp = tmp.substring(1,tmp.length() - 1);
		packet.mStr += tmp;
		
		packet.mItem.put("data", jsonObject);
		
		handleDataOut(packet);
	}
	
	
	public void handleDataOut(final ArrayNode jsonArray) {

		DataOutPacket packet = new DataOutPacket();
		// To match our format we must remove the starting and ending curly brace
		String tmp = jsonArray.toString();
		tmp = tmp.substring(1,tmp.length() - 1);
		packet.mStr += tmp;
		
		packet.mItem.put("data", jsonArray);
		handleDataOut(packet);
	}	
	
	
	/**
	 * Sends a data packet to all configured output sinks
	 * 
	 * 
	 * @param packet - data Packet to send to output sinks
	 */
	public void handleDataOut(final DataOutPacket packet) {
		if (mLogFormat == LOG_FORMAT_JSON) {
			packet.mStr += "},";
		}

		if (mLoggingEnabled) {	
//			Log.d(TAG, "Writing to log file");		// TODO: remove
			mLogWriter.write(packet.mStr);
		}

		if (mLogCatEnabled) {
			Log.d(TAG, packet.mStr);			
		}
		
		// Now do something with the database if necessary
		if (mDatabaseEnabled && couchDbConnector != null) {
			Log.d(TAG, "Adding document");

			T2EktorpAsyncTask createItemTask = new T2EktorpAsyncTask() {

				@Override
				protected void doInBackground() {
					couchDbConnector.create(packet.mItem);
				}

				@Override
				protected void onSuccess() {
					Log.d(TAG, "Document added to database successfully");
				}

				@Override
				protected void onUpdateConflict(
						UpdateConflictException updateConflictException) {
					Log.d(TAG, "Got an update conflict for: " + packet.mItem.toString());
				}
			};
		    createItemTask.execute();			
		} // End if (mDatabaseEnabled)
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
}
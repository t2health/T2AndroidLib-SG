package com.t2.filechooser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.t2health.lib1.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;



/**
 * List activity that lists all of the files in the external storage directory
 * and allows the user to perform utility functions on the files
 * (View, delete, export (email)) via a context menu
 * 
 * Note that there is a special case, if the caller of this activity wants this module to add a menu item to the 
 * context menu for generic selection it must add a bundle.extra to the intent when calling this activity
 * 
 * bundle.putString(FileChooser.EXTRA_PROMPT_NAME,"Text for menu item");
 * 
 * In that case FileChooser will add a bundle.extra (BioZenConstants.FILECHOOSER_USER_ACTIVITY_RESULT) to the result:
 * 
 * 	  resultIntent.putExtra(BioZenConstants.FILECHOOSER_USER_ACTIVITY_RESULT, itemName);		
 *	  setResult(RESULT_OK, resultIntent);
 * 
 * 
 * 
 * @author scott.coleman
 *
 */
public class FileChooser extends ListActivity {
	private static final String TAG = "FileChooser";
	

	public static String EXTRA_PROMPT_NAME = "extraPromptName";
	public static final int FILECHOOSER_USER_ACTIVITY = 0x303;
	public static final String FILECHOOSER_USER_ACTIVITY_RESULT = "File";	
	
	private static final int FILTER_DATA = 0;
	private static final int FILTER_CAT = 1;
	private static final int FILTER_PDF = 2;
	private static final int FILTER_ALL = 3;
	
    private File currentDir;
    private FileArrayAdapter adapter;
    private int fileFilter = FILTER_DATA;
    private String genericSelectPrompt = "";

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //currentDir = new File("/sdcard/");
        currentDir = Environment.getExternalStorageDirectory();
        
        setContentView(R.layout.file_chooser_activity_layout);   

		Bundle bundle = getIntent().getExtras();
		
		if (bundle != null) {
			genericSelectPrompt = bundle.getString(EXTRA_PROMPT_NAME);        
		}        
        
        
        Spinner spinner = (Spinner) findViewById(R.id.spinnerFilter);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this, R.array.filechooser_spinner_items, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);        
        
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());    

        
        fill(currentDir);
        
        // Set a listener for long click to email file
        ListView lv = getListView();         
        lv.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener () { 
        	@Override 
            public boolean onItemLongClick(AdapterView<?> av, View v, int  pos, long id) { 
        		Option o = adapter.getItem(pos);
        		final String itemName = o.getName();
        		if(o.getData().equalsIgnoreCase("folder")||o.getData().equalsIgnoreCase("parent directory")){
        		}
        		else {
        			optionsForItem(o);        			
        		}        		
        		return true; 
            } 
        });         
    }
    
    void optionsForItem(Option o) {
    	
    	final String itemName = o.getName();
  		CharSequence[] items1 = {"Send", "Delete", "View as text", "View as document"};
  		CharSequence[] items2 = {"Send", "Delete", "View as text", "View as document", genericSelectPrompt};
  		final CharSequence[] items;
		if (genericSelectPrompt.equalsIgnoreCase("")) {
			items = items1;
		}
		else {
			items = items2;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(FileChooser.this);
		builder.setTitle("Choose Action");
		builder.setIcon(R.drawable.icon);
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	switch (item) {
    		    	case 0: // Export
            			Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), itemName));        		
                		
                		Intent i = new Intent(Intent.ACTION_SEND);
                		i.setType("text/plain");
                		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"scott.coleman@tee2.org"});
                		i.putExtra(Intent.EXTRA_SUBJECT, "session results: " + itemName);
//                		i.putExtra(Intent.EXTRA_TEXT   , o.getName());
                		i.putExtra(Intent.EXTRA_STREAM, uri);        		
                		try {
                		    startActivity(Intent.createChooser(i, "Send mail..."));
                		} catch (android.content.ActivityNotFoundException ex) {
                		    Toast.makeText(FileChooser.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                		}        		
    		    		
    		    		break;
    		    	case 1: // Delete
					try {
						File file = new File(Environment.getExternalStorageDirectory(), itemName);
                		boolean deleted = file.delete();
                		if (deleted) {
                		    Toast.makeText(FileChooser.this, "deleted file " + itemName, Toast.LENGTH_SHORT).show();
                		}
                		fill(currentDir);
					} catch (Exception e2) {
						Log.e("BfDemo", "Exception deleting file");
						e2.printStackTrace();
					}	  
                		break;
    		    	case 4: //Generic selection
    		    		Intent resultIntent;
    		    		resultIntent = new Intent();
    		    		resultIntent.putExtra(FILECHOOSER_USER_ACTIVITY_RESULT, itemName);		
    		    		setResult(RESULT_OK, resultIntent);
    		    		finish();
    		    		break;

    		    	case 2:
						try {
							// Some devices have a handler for the MIME type "text"
	        		    	Intent intent = new Intent();
	        		    	intent.setAction(android.content.Intent.ACTION_VIEW);
	        		    	intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), itemName)), "text");
							startActivity(intent);
						} catch (Exception e) {
							// This one doesn't, try text/plain
							Log.e("BfDemo", "Exception trying to open file editor as MIME type plain, trying text/plain");

							try {
								Intent intent = new Intent();
								intent.setAction(android.content.Intent.ACTION_VIEW);
								intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), itemName)), "text/plain");
								startActivity(intent);
							} catch (Exception e1) {
								Log.e("BfDemo", "Exception trying to open file editor");
								e1.printStackTrace();
							}
							
							
							
						}	            		    	
    		    		break;

    		    	case 3:
					try {
						Intent intent = new Intent();
        		    	intent.setAction(android.content.Intent.ACTION_VIEW);
        		    	intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), itemName)), "text/csv");
        		    	startActivity(intent);
					} catch (Exception e) {
						Log.e("BfDemo", "Exception trying to open file editor");
						e.printStackTrace();
					}	            		    	
    		    		break;
   		    		default:
		    			
		    	}
		    }
		});
		AlertDialog alert = builder.create();

		alert.show();            		
	}        		

    
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.menu_filechooser, menu);
		return true;
	}    
    
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.deleteFiles) {
			deleteFiles();
			return true;
		} 
		else if (item.getItemId() == R.id.saveLogCat) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
			String dateTime = sdf.format(new Date());
			try {
			    File filename = new File(Environment.getExternalStorageDirectory() + "/" + "Logcat_" + dateTime+ ".log"); 
			    filename.createNewFile(); 
//			    String cmd = "logcat -d -f "+filename.getAbsolutePath();
//			    String cmd = "logcat -f "+filename.getAbsolutePath();
			   // Runtime.getRuntime().exec(cmd);

			     Process process = Runtime.getRuntime().exec("logcat -d -v time");
			      BufferedReader bufferedReader = new BufferedReader(
			      new InputStreamReader(process.getInputStream()));

			      StringBuilder log=new StringBuilder();
			      String line;
			      while ((line = bufferedReader.readLine()) != null) {
			        log.append(line);
			      }			    
			    
			      
		        FileWriter gpxwriter = new FileWriter(filename, true); // open for append
		        BufferedWriter logWriter = new BufferedWriter(gpxwriter);
		        logWriter.write(log.toString());
		        logWriter.close();
			    
	            Toast.makeText(this, "Saved system LogCat file to " + "Logcat_" + dateTime+ ".log", Toast.LENGTH_SHORT).show();
		        
			    Log.d(TAG, "Saved logcat to file ");
				File[] dirs = currentDir.listFiles();	
        		fill(currentDir);	  				
			    
			    
			} catch (IOException e) {
			    Log.e(TAG, "Exception saving logcat file: " + e.toString());
			    e.printStackTrace();
			}			
			
			
			return true;
		} 
		else if (item.getItemId() == R.id.deleteLogCat) {
	        try {
			    String cmd = "logcat -c ";
			    Runtime.getRuntime().exec(cmd);
	            Toast.makeText(this, "Cleared system LogCat file", Toast.LENGTH_SHORT).show();

			} catch (IOException e) {
				Log.e(TAG, "Error clearing logcat" + e.toString());
				e.printStackTrace();
			}
			return true;
		} 
		else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void deleteFiles() {
		File[] dirs = currentDir.listFiles();
		 for(File ff: dirs)
		 {
			if(!ff.isDirectory()) {
				String itemName = ff.toString();
				if (itemName.contains(".log")) {
					boolean deleted = ff.delete();
            		fill(currentDir);	  
				}
			}
		 }
	}


	
    public class MyOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
            View view, int pos, long id) {
        	Toast.makeText(parent.getContext(), "Showing " +
              parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        	
            fileFilter = pos;
            fill(currentDir);
            

//        	if (parent.getItemAtPosition(pos).toString().contains("cat")) {
//        		showAllFiles = true;
//        	}
//        	else {
//        		showAllFiles = false;
//        		
//        	}
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }    
    private void fill(File f)
    {
    	File[]dirs = f.listFiles();
		 this.setTitle("Current Dir: "+f.getName());
		 List<Option>dir = new ArrayList<Option>();
		 List<Option>fls = new ArrayList<Option>();
		 try{
			 for(File ff: dirs)
			 {
				if(ff.isDirectory()) {
//					dir.add(new Option(ff.getName(),"Folder",ff.getAbsolutePath()));
				}
				else
				{
					String fileName = ff.toString();
					switch (fileFilter) {
					case  FILTER_ALL:
						fls.add(new Option(ff.getName(),"File Size: "+ff.length(),ff.getAbsolutePath()));
						break;
						
					case  FILTER_DATA:
						if (fileName.endsWith(".log") && !fileName.contains("cat"))
							fls.add(new Option(ff.getName(),"File Size: "+ff.length(),ff.getAbsolutePath()));
						break;
						
					case  FILTER_CAT:
						if (fileName.endsWith(".log") && fileName.contains("cat"))
							fls.add(new Option(ff.getName(),"File Size: "+ff.length(),ff.getAbsolutePath()));
						break;
						
					case  FILTER_PDF:
						if (fileName.endsWith(".pdf") )
							fls.add(new Option(ff.getName(),"File Size: "+ff.length(),ff.getAbsolutePath()));
						break;
						

					}
				}
			 }
		 }catch(Exception e)
		 {
			 
		 }
		 Collections.sort(dir);
		 Collections.sort(fls);
		 dir.addAll(fls);
		 if(!f.getName().equalsIgnoreCase("sdcard"))
			 dir.add(0,new Option("..","Parent Directory",f.getParent()));
		 adapter = new FileArrayAdapter(FileChooser.this,R.layout.file_view,dir);
		 this.setListAdapter(adapter);
    }
    
    
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Option o = adapter.getItem(position);
		if(o.getData().equalsIgnoreCase("folder")||o.getData().equalsIgnoreCase("parent directory")){
				currentDir = new File(o.getPath());
				fill(currentDir);
		}
		else
		{
				onFileClick(o);
		}
	}
    private void onFileClick(Option o)
    {
    	optionsForItem(o);
    }
}
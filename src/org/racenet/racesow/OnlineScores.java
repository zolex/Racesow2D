package org.racenet.racesow;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.racenet.racesow.models.MapItem;
import org.racenet.racesow.models.OnlineMapsAdapter;
import org.racenet.racesow.threads.XMLLoaderThread;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Obtain the online scores and display pages of lists of scores
 * 
 * @author soh#zolex
 *
 */
public class OnlineScores extends ListActivity {

	WakeLock wakeLock;
	OnlineMapsAdapter mAdapter;
	
    @Override
    /**
     * Load the scores and initialize the pager and adapter
     * 
     * @param Bundle savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
    	
    	PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	this.wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "racesow");
    	
    	final ProgressDialog pd = new ProgressDialog(OnlineScores.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("Obtaining maplist...");
		pd.setCancelable(false);
		pd.show();
        
		XMLLoaderThread t = new XMLLoaderThread("http://racesow2d.warsow-race.net/map_pages.php", new Handler() {
	    	
	    	@Override
	        public void handleMessage(Message msg) {
	    		
	    		switch (msg.what) {
	    			
	    			// network error
	    			case 0:
	    				new AlertDialog.Builder(OnlineScores.this)
				            .setMessage("Could not obtain the maplist.\nCheck your network connection and try again.")
				            .setNeutralButton("OK", new OnClickListener() {
								
								public void onClick(DialogInterface arg0, int arg1) {
									
									finish();
									overridePendingTransition(0, 0);
								}
							})
				            .show();
	    				break;
	    				
	    			// maplist received
	    			case 1:
	    				pd.dismiss();
						InputStream xmlStream;
						try {
							
							xmlStream = new ByteArrayInputStream(msg.getData().getString("xml").getBytes("UTF-8"));
							mAdapter = new OnlineMapsAdapter(getApplicationContext(), xmlStream);
				    		setListAdapter(mAdapter);
							
						} catch (UnsupportedEncodingException e) {
	
							new AlertDialog.Builder(OnlineScores.this)
					            .setMessage("Internal error: " + e.getMessage())
					            .setNeutralButton("OK", null)
					            .show();
						}
		    			
						break;
	    		}
	    		
	        	pd.dismiss();
	        }
	    });
	    
		t.start();
		
		setContentView(R.layout.listview);
		
		// when clicking a map
        getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, final View view, final int pos, long id) {
				
				final String mapName =  ((MapItem)mAdapter.getItem(pos)).name;
				
				Intent i = new Intent(OnlineScores.this, OnlineScoresDetails.class);
				i.putExtra("map", mapName);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(i, 0);
			}
        });
    }
    
    /**
	 * Acquire the wakelock on resume
	 */
    public void onResume() {
    	
    	super.onResume();
    	this.wakeLock.acquire();
    }
    
    /**
     * Release the wakelock when leaving the activity
     */
    public void onDestroy() {
    	
    	super.onDestroy();
    	this.wakeLock.release();
    }

    /**
     * Disable animations when leaving the activity
     */
	public void onBackPressed() {
    	
    	this.finish();
    	this.overridePendingTransition(0, 0);
    }
}
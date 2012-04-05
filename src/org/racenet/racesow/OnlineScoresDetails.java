package org.racenet.racesow;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.racenet.framework.XMLParser;
import org.racenet.racesow.models.MapItem;
import org.racenet.racesow.models.OnlineMapsAdapter;
import org.racenet.racesow.models.OnlineScoresAdapter;
import org.racenet.racesow.models.ScoreItem;
import org.racenet.racesow.threads.XMLLoaderThread;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Obtain the online scores and display pages of lists of scores
 * 
 * @author soh#zolex
 *
 */
public class OnlineScoresDetails extends ListActivity {

	WakeLock wakeLock;
	OnlineScoresAdapter mAdapter;
	List<ScoreItem> scores = new ArrayList<ScoreItem>();
	boolean isLoading = false;
	int chunkLimit = 50;
	int chunkOffset = 0;
	
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
		
		this.loadData();
		
		setContentView(R.layout.listview);
		getListView().setOnScrollListener(new OnScrollListener() {
			
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

				if (totalItemCount > 0 && visibleItemCount > 0 && firstVisibleItem + visibleItemCount >= totalItemCount) {
					
					if (!isLoading) {
					
						loadData();
					}
				}
			}
		});
    }
    
    public void loadData() {
    	
    	final ProgressDialog pd = new ProgressDialog(OnlineScoresDetails.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("Obtaining scores...");
		pd.setCancelable(false);
		pd.show();
    	
    	isLoading = true;
		String mapName = getIntent().getStringExtra("map");
		XMLLoaderThread t = new XMLLoaderThread("http://racesow2d.warsow-race.net/map_positions.php?name=" + mapName + "&offset=" + this.chunkOffset + "&limit=" + this.chunkLimit, new Handler() {
	    	
	    	@Override
	        public void handleMessage(Message msg) {
	    		
	    		switch (msg.what) {
	    			
	    			// network error
	    			case 0:
	    				new AlertDialog.Builder(OnlineScoresDetails.this)
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
							XMLParser parser = new XMLParser();
							parser.read(xmlStream);

							NodeList positions = parser.doc.getElementsByTagName("position");
							int numPositions = positions.getLength();
							for (int i = 0; i < numPositions; i++) {

								Element position = (Element)positions.item(i);
						
								ScoreItem score = new ScoreItem();
								score.position = Integer.parseInt(parser.getValue(position, "no"));
								score.player = parser.getValue(position, "player");
								score.time = Float.parseFloat(parser.getValue(position, "time"));
								score.created_at = parser.getValue(position, "created_at");
								scores.add(score);
							}
							
							mAdapter = new OnlineScoresAdapter(getApplicationContext(), scores);
				    		setListAdapter(mAdapter);
				    		chunkOffset += chunkLimit;
				    		isLoading = false;
							
						} catch (UnsupportedEncodingException e) {
	
							new AlertDialog.Builder(OnlineScoresDetails.this)
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
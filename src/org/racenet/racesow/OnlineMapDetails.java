package org.racenet.racesow;

import java.io.InputStream;
import org.racenet.framework.XMLParser;
import org.racenet.racesow.models.OnlineScoresAdapter;
import org.racenet.racesow.models.ScoreItem;
import org.racenet.racesow.threads.HttpLoaderTask;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;

/**
 * Obtain and display the online scores
 * 
 * @author soh#zolex
 *
 */
public class OnlineMapDetails extends ListActivity implements HttpCallback {

	WakeLock wakeLock;
	OnlineScoresAdapter adapter;
	boolean isLoading = false;
	int chunkLimit = 50;
	int chunkOffset = 0;
	ProgressDialog pd;
	int count;
	
	/**
	 * For mostly gapless music
	 */
	public OnlineMapDetails() {
		
		super();
		Racesow.resumeMusic();
	}
	
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
		
    	adapter = new OnlineScoresAdapter(this);
		setListAdapter(adapter);
		
		setContentView(R.layout.listview);
		String mapName = getIntent().getStringExtra("map");
		TextView title = (TextView)findViewById(R.id.title);
        title.setText("Map " + mapName);
        title.setTypeface(Racesow.font);
        
		this.loadData();
		
		getListView().setOnScrollListener(new OnScrollListener() {
			
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

				if (totalItemCount > 0 && visibleItemCount > 0 && firstVisibleItem + visibleItemCount >= totalItemCount) {

					if (!isLoading && totalItemCount < count) {
					
						loadData();
					}
				}
			}
		});
    }
    
    /**
     * Show a loading indication and trigger the xml request
     */
    public void loadData() {
    	
    	pd = new ProgressDialog(OnlineMapDetails.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("Obtaining scores...");
		pd.setCancelable(true);
		pd.setOnCancelListener(new OnCancelListener() {
			
			public void onCancel(DialogInterface dialog) {

				OnlineMapDetails.this.onBackPressed();
			}
		});
		pd.show();
    	
    	isLoading = true;
		String mapName = getIntent().getStringExtra("map");
		String url = "http://racesow2d.warsow-race.net/scores.php?name=" + mapName + "&offset=" + this.chunkOffset + "&limit=" + this.chunkLimit;
		new HttpLoaderTask(this).execute(url);
    }
    
    /**
     * Called by XMLLoaderTask when loading has finished
     * 
     * @param InputStream xmlStream
     */
    public void httpCallback(InputStream xmlStream) {
    	
    	if (pd.isShowing()) {
    	
    		pd.dismiss();
    	}
		
		if (xmlStream == null) {
			
			new AlertDialog.Builder(this)
	            .setMessage("Could not load the scores.\nCheck your network connection and try again.")
	            .setNeutralButton("OK", new OnClickListener() {
								
					public void onClick(DialogInterface arg0, int arg1) {
						
						if (chunkOffset == 0) {
							
							finish();
							overridePendingTransition(0, 0);
						}
					}
				})
	            .show();
			
		} else {
		
			XMLParser parser = new XMLParser();
			parser.read(xmlStream);
			
			NodeList counts = parser.doc.getElementsByTagName("count");
			if (counts.getLength() == 1) {
				
				this.count = Integer.parseInt(parser.getNodeValue(counts.item(0)));
			}
			
			NodeList positions = parser.doc.getElementsByTagName("position");
			int numPositions = positions.getLength();
			for (int i = 0; i < numPositions; i++) {

				Element position = (Element)positions.item(i);
		
				ScoreItem score = new ScoreItem();
				score.position = Integer.parseInt(parser.getValue(position, "no"));
				score.player = parser.getValue(position, "player");
				score.races = Integer.parseInt(parser.getValue(position, "races"));
				score.time = Float.parseFloat(parser.getValue(position, "time"));
				score.created_at = parser.getValue(position, "created_at");
				
				adapter.addItem(score);
			}
			
			// adapter.notifyDataSetChanged(); // WHY THE FUCK DOESN'T THIS WORK????
			setListAdapter(adapter);
			getListView().setSelection(chunkOffset - 6);
			
			chunkOffset += chunkLimit;
		}
		
		isLoading = false;
    }
    
    /**
     * Pause the music
     */
    public void onPause() {
		
		super.onPause();
		Racesow.pauseMusic();
	}
    
    /**
	 * Resume music and acquire wakelock
	 */
    public void onResume() {
    	
    	super.onResume();
    	Racesow.resumeMusic();
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
    	
		Racesow.backSound();
    	this.finish();
    	this.overridePendingTransition(0, 0);
    }
}
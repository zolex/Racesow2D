package org.racenet.racesow;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.racenet.framework.XMLParser;
import org.racenet.racesow.models.MapItem;
import org.racenet.racesow.models.OnlineMapsAdapter;
import org.racenet.racesow.models.OnlineScoresAdapter;
import org.racenet.racesow.models.ScoreItem;
import org.racenet.racesow.threads.XMLLoaderTask;
import org.racenet.racesow.threads.XMLLoaderThread;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Obtain and display the online scores
 * 
 * @author soh#zolex
 *
 */
public class OnlineScores extends XMLListActivity {

	WakeLock wakeLock;
	OnlineMapsAdapter adapter;
	boolean isLoading = false;
	int chunkLimit = 50;
	int chunkOffset = 0;
	ProgressDialog pd;
	
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
		
    	adapter = new OnlineMapsAdapter(this);
		setListAdapter(adapter);
		this.loadData();
		
		setContentView(R.layout.listview);
		
		getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, final View view, final int pos, long id) {
				
				final String mapName =  ((MapItem)adapter.getItem(pos)).name;
				
				Intent i = new Intent(OnlineScores.this, OnlineScoresDetails.class);
				i.putExtra("map", mapName);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(i, 0);
			}
        });
    }
    
    /**
     * Load the XML data from the web api
     * 
     */
    public void loadData() {
    	
    	pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("Obtaining maplist...");
		pd.setCancelable(false);
		pd.show();
    	
    	isLoading = true;
		String url = "http://racesow2d.warsow-race.net/map_pages.php";
		new XMLLoaderTask(this).execute(url);
    }
    
    /**
     * Called by the XMLLoaderTask when finished
     * 
     * @param InputStream xmlStream
     */
    public void xmlCallback(InputStream xmlStream) {
    	
    	pd.dismiss();
		
		if (xmlStream == null) {
			
			new AlertDialog.Builder(this)
	            .setMessage("Could not load the maplist.\nCheck your network connection and try again.")
	            .setNeutralButton("OK", new OnClickListener() {
								
					public void onClick(DialogInterface arg0, int arg1) {
						
						finish();
						overridePendingTransition(0, 0);
					}
				})
	            .show();
			
		} else {
		
			XMLParser parser = new XMLParser();
			parser.read(xmlStream);
			NodeList positions = parser.doc.getElementsByTagName("map");
			int numMaps = positions.getLength();
			for (int i = 0; i < numMaps; i++) {

				MapItem item = new MapItem();
				item.name = parser.getNodeValue( (Element)positions.item(i));
				adapter.addItem(item);
			}
			
			// adapter.notifyDataSetChanged(); // WHY THE FUCK DOESN'T THIS WORK????
			setListAdapter(adapter);
		}
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
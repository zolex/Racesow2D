package org.racenet.racesow;

import org.racenet.racesow.models.ScoresMenuAdapter;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Show the online scores menu
 * 
 * @author soh#zolex
 *
 */
public class OnlineScores extends ListActivity {

	WakeLock wakeLock;
	boolean isLoading = false;
	int chunkLimit = 50;
	int chunkOffset = 0;
	
	/**
	 * For mostly gapless music
	 */
	public OnlineScores() {
		
		super();
		Racesow.resumeMusic();
	}
	
    @Override
    /**
     * Initialite the listview
     * 
     * @param Bundle savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
    	
    	PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	this.wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "racesow");
		
		setListAdapter(new ScoresMenuAdapter(this));
		
		setContentView(R.layout.listview);
		
		TextView title = (TextView)findViewById(R.id.title);
        title.setText("Scores");
        title.setTypeface(Racesow.font);
        
		getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				
				Intent i = null;
				switch (pos) {
				
					case 0:
						i = new Intent(OnlineScores.this, LocalScores.class);
						break;
					
					case 1:
						i = new Intent(OnlineScores.this, OnlinePlayers.class);
						break;
						
					case 2:
						i = new Intent(OnlineScores.this, OnlineMaps.class);
						break;
						
					case 3:
						i = new Intent(OnlineScores.this, OnlineUpdates.class);

						break;
				}
				
				if (i != null) {
					
					Racesow.clickSound();
					i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivity(i);
				}
			}
        });
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
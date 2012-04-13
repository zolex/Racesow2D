package org.racenet.racesow;

import org.racenet.framework.FileIO;
import org.racenet.racesow.models.LocalMapPagesAdapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.view.ViewPager;

/**
 * Shows pages of lists of map-highscores
 * 
 * @author soh#zolex
 *
 */
public class LocalScores extends Activity {

	WakeLock wakeLock;
	ViewPager viewPager;
	
    @Override
    /**
     * Acquires wakelock and creates an instance of
     * the View-Pager and corresponding adapter
     */
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
    	
    	PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	this.wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "racesow");
    	
    	FileIO.setupInstance(getAssets());
        setContentView(R.layout.viewpager);
        viewPager = (ViewPager)findViewById(R.id.pager);
    	viewPager.setAdapter(new LocalMapPagesAdapter(getApplicationContext()));
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
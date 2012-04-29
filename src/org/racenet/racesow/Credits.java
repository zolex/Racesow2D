package org.racenet.racesow;

import org.racenet.racesow.models.CreditsAdapter;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.view.ViewPager;

/**
 * Shows the game credits
 * 
 * @author soh#zolex
 *
 */
public class Credits extends ListActivity {

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
    	
        setContentView(R.layout.listview);
        setListAdapter(new CreditsAdapter(getApplicationContext()));
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
    	
		Racesow.backSound();
    	this.finish();
    	this.overridePendingTransition(0, 0);
    }
}
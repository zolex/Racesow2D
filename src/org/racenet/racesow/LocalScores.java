package org.racenet.racesow;

import org.racenet.framework.FileIO;
import org.racenet.racesow.models.LocalMapPagesAdapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

/**
 * Shows pages of lists of map-highscores
 * 
 * @author soh#zolex
 *
 */
public class LocalScores extends Activity {

	WakeLock wakeLock;
	ViewPager viewPager;
	
	/**
	 * For mostly gapless music
	 */
	public LocalScores() {
		
		super();
		Racesow.resumeMusic();
	}
	
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
        TextView title = (TextView)findViewById(R.id.title);
        title.setTypeface(Racesow.font);
        
        viewPager = (ViewPager)findViewById(R.id.pager);
    	viewPager.setAdapter(new LocalMapPagesAdapter(getApplicationContext()));
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
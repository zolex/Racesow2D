package org.racenet.racesow;

import org.racenet.framework.AndroidFileIO;
import org.racenet.racesow.models.LocalScoresAdapter;
import org.racenet.racesow.models.LocalMapPagesAdapter;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.view.ViewPager;

public class LocalScores extends Activity {

	WakeLock wakeLock;
	ViewPager viewPager;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
    	
    	PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	this.wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "racesow");
    	
    	AndroidFileIO fileIO = new AndroidFileIO(getAssets());
        setContentView(R.layout.viewpager);
        viewPager = (ViewPager)findViewById(R.id.pager);
    	viewPager.setAdapter(new LocalMapPagesAdapter(getApplicationContext(), fileIO));
    }
    
    public void onResume() {
    	
    	super.onResume();
    	this.wakeLock.acquire();
    }
    
    public void onDestroy() {
    	
    	super.onDestroy();
    	this.wakeLock.release();
    }
    
    public void onStart() {
    	
    	super.onStart();
    }
    
    public void onStop() {
    	
    	super.onStop();
    }
    
	public void onBackPressed() {
    	
    	this.finish();
    	this.overridePendingTransition(0, 0);
    }
}
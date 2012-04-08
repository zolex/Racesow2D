package org.racenet.racesow;

import org.racenet.framework.AndroidFileIO;
import org.racenet.racesow.models.DemoAdapter;
import org.racenet.racesow.models.LocalMapPagesAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Shows pages of lists of map-highscores
 * 
 * @author soh#zolex
 *
 */
public class DemoList extends ListActivity {

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
    	
    	AndroidFileIO fileIO = new AndroidFileIO(getAssets());
    	setListAdapter(new DemoAdapter(this, fileIO));
        setContentView(R.layout.listview);
        
        getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
				final String demo = (String)getListAdapter().getItem(arg2);
				new AlertDialog.Builder(DemoList.this)
		            .setMessage("Do you want to play the demo '" + demo +"'?")
		            .setPositiveButton("Play", new OnClickListener() {
						
						public void onClick(DialogInterface arg0, int arg1) {
							
							Intent i = new Intent(DemoList.this, Racesow.class);
							i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							i.putExtra("screen", "demo");
							i.putExtra("demo", demo);
							startActivityForResult(i, 0);
						}
					})
					.setNegativeButton("No", null)
		            .show();
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
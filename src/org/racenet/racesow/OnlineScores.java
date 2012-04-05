package org.racenet.racesow;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.racenet.racesow.models.OnlineMapPagesAdapter;
import org.racenet.racesow.models.OnlineScoresAdapter;
import org.racenet.racesow.threads.XMLLoaderThread;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Obtain the online scores and display pages of lists of scores
 * 
 * @author soh#zolex
 *
 */
public class OnlineScores extends Activity {

	WakeLock wakeLock;
	ViewPager viewPager;
	
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
							viewPager = (ViewPager)findViewById(R.id.pager);
					    	viewPager.setAdapter(new OnlineMapPagesAdapter(getApplicationContext(), xmlStream));
							viewPager.setOnPageChangeListener(new OnPageChangeListener() {
								
								public void onPageSelected(int arg0) {
									
									Log.d("DEBUG", "onpageSelceted: " + arg0);
									
									final ProgressDialog pd2 = new ProgressDialog(OnlineScores.this);
									pd2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
									pd2.setMessage("Obtaining scores...");
									pd2.setCancelable(false);
									pd2.show();
									
									View view = viewPager.getChildAt(arg0);
									if (view != null) {
										TextView name = (TextView)view.findViewById(R.id.title);
										XMLLoaderThread t2 = new XMLLoaderThread("http://racesow2d.warsow-race.net/map_positions.php?name=" + name.getText(), new Handler() {
											
											@Override
									        public void handleMessage(Message msg) {
									    		
									    		switch (msg.what) {
									    			
									    			// network error
									    			case 0:
									    				new AlertDialog.Builder(OnlineScores.this)
												            .setMessage("Could not obtain the scores.\nCheck your network connection and try again.")
												            .setNeutralButton("OK", new OnClickListener() {
																
																public void onClick(DialogInterface arg0, int arg1) {
																	
																	finish();
																	overridePendingTransition(0, 0);
																}
															})
												            .show();
									    				break;
									    				
									    			// scores received
									    			case 1:
														InputStream xmlStream;
														try {
															
															xmlStream = new ByteArrayInputStream(msg.getData().getString("xml").getBytes("UTF-8"));
															ListView list = (ListView)view.findViewById(R.id.list);
															list.setAdapter(new OnlineScoresAdapter(getApplicationContext(), xmlStream));
															pd2.dismiss();
															
														} catch (UnsupportedEncodingException e) {
															
															new AlertDialog.Builder(OnlineScores.this)
													            .setMessage("Internal error: " + e.getMessage())
													            .setNeutralButton("OK", null)
													            .show();
														}
														break;
									    		}
											}
										});
										
										t2.start();
									}
								}
								
								public void onPageScrolled(int arg0, float arg1, int arg2) {
									
									//Log.d("DEBUG", "onPageScrolled: " + arg0 + "," + arg1 + "," + arg2);
								}
								
								public void onPageScrollStateChanged(int arg0) {
									
									//Log.d("DEBUG", "onPageScrollStateChanged: " + arg0);
								}
							});
				    		
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
		
        setContentView(R.layout.viewpager);
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
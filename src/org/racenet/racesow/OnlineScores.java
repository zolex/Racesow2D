package org.racenet.racesow;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.racenet.framework.AndroidFileIO;
import org.racenet.framework.XMLParser;
import org.racenet.racesow.models.DownloadMapsAdapter;
import org.racenet.racesow.models.MapItem;
import org.racenet.racesow.models.OnlineMapPagesAdapter;
import org.racenet.racesow.threads.OnlineScoresThread;
import org.racenet.racesow.threads.RefreshMapsThread;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

public class OnlineScores extends Activity {

	WakeLock wakeLock;
	ViewPager viewPager;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
    	
    	PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	this.wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "racesow");
    	
    	
    	final ProgressDialog pd = new ProgressDialog(OnlineScores.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("Obtaining online scores...");
		pd.setCancelable(false);
		pd.show();
        
		final OnlineScoresThread t = new OnlineScoresThread(new Handler() {
	    	
	    	@Override
	        public void handleMessage(Message msg) {
	    		
	    		switch (msg.what) {
	    			
	    			case 0:
	    				new AlertDialog.Builder(OnlineScores.this)
				            .setMessage("Could not obtain the online scores.\nCheck your network connection and try again.")
				            .setNeutralButton("OK", new OnClickListener() {
								
								public void onClick(DialogInterface arg0, int arg1) {
									
									finish();
									overridePendingTransition(0, 0);
								}
							})
				            .show();
	    				break;
	    				
	    			case 1:
						InputStream xmlStream;
						try {
							
							xmlStream = new ByteArrayInputStream(msg.getData().getString("xml").getBytes("UTF-8"));
							viewPager = (ViewPager)findViewById(R.id.pager);
					    	viewPager.setAdapter(new OnlineMapPagesAdapter(getApplicationContext(), xmlStream));
							
				    		
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
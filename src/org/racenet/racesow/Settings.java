package org.racenet.racesow;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.racenet.helpers.InputStreamToString;
import org.racenet.helpers.IsServiceRunning;
import org.racenet.racesow.R;
import org.racenet.racesow.threads.XMLLoaderTask;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;

/**
 * Activity to handle the game settings
 * 
 * @author soh#zolex
 *
 */
public class Settings extends PreferenceActivity implements XMLCallback {
	
	WakeLock wakeLock;
	
    @Override
    /**
     * create the view and add handle changes in the settings
     */
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        setContentView(R.layout.listview);
        
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	this.wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "racesow");
    	this.wakeLock.acquire();
        
    	if (getIntent().getBooleanExtra("setNick", false)) {
    		
    		editNick();
    	}
    	
    	
        OnPreferenceChangeListener listener = new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference pref, Object value) {
				
				SharedPreferences prefs = Settings.this.getSharedPreferences("racesow", Context.MODE_PRIVATE);
				
				if (pref.getKey().equals("sound")) {
					
					prefs.edit().putBoolean("sound", value.toString().equals("true") ? true : false).commit();

				} else if (pref.getKey().equals("ambience")) {
					
					prefs.edit().putBoolean("ambience", value.toString().equals("true") ? true : false).commit();

				} else if (pref.getKey().equals("gfx")) {
					
					prefs.edit().putBoolean("gfx", value.toString().equals("true") ? true : false).commit();
					
				} else if (pref.getKey().equals("blur")) {
					
					prefs.edit().putBoolean("blur", value.toString().equals("true") ? true : false).commit();
					
				} else if (pref.getKey().equals("name")) {
					
					String nick = value.toString().trim();
					if (nick.equals("")) {
				
						askEditNick();
						
					} else {
					
						String url = "http://racesow2d.warsow-race.net/accounts.php";
						List<NameValuePair> values = new ArrayList<NameValuePair>();
						values.add(new BasicNameValuePair("action", "check"));
						values.add(new BasicNameValuePair("name", nick));
						new XMLLoaderTask(Settings.this).execute(url, values);
						
						prefs.edit().putString("name", nick).commit();
					}
				
				} else if (pref.getKey().equals("ups")) {
					
					prefs.edit().putBoolean("ups", value.toString().equals("true") ? true : false).commit();

				} else if (pref.getKey().equals("fps")) {
					
					prefs.edit().putBoolean("fps", value.toString().equals("true") ? true : false).commit();
					
				} else if (pref.getKey().equals("demos")) {
					
					prefs.edit().putBoolean("demos", value.toString().equals("true") ? true : false).commit();
					
				} else if (pref.getKey().equals("notification")) {
					
					prefs.edit().putString("notification", value.toString()).commit();
					
				} else if (pref.getKey().equals("icon")) {
					
					NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
					if (value.toString() == "true") {
						
						if (IsServiceRunning.check("org.racenet.racesow.PullService", getApplicationContext())) {
							
					        manager.notify(PullService.SERVICE_NOTIFICATION,
					        	PullService.getServiceNotification(getApplicationContext(), Settings.this));
						}
						
					} else {
						
						manager.cancel(PullService.SERVICE_NOTIFICATION);
					}
					
					prefs.edit().putBoolean("icon", value.toString().equals("true") ? true : false).commit();
				}
				
				return true;
			}
		};
        
		findPreference("name").setOnPreferenceChangeListener(listener);
		findPreference("demos").setOnPreferenceChangeListener(listener);
		findPreference("sound").setOnPreferenceChangeListener(listener);
		findPreference("ambience").setOnPreferenceChangeListener(listener);
		findPreference("gfx").setOnPreferenceChangeListener(listener);
		findPreference("blur").setOnPreferenceChangeListener(listener);
		findPreference("ups").setOnPreferenceChangeListener(listener);
		findPreference("fps").setOnPreferenceChangeListener(listener);
		findPreference("icon").setOnPreferenceChangeListener(listener);
		findPreference("notification").setOnPreferenceChangeListener(listener);
    }
    
    /**
     * Ask the user to set his nickname
     */
    private void askEditNick() {
    	
    	new AlertDialog.Builder(Settings.this)
        .setMessage("Nickname must not be blank")
        .setPositiveButton("OK", new OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				
				editNick();
			}
		})
        .show();
    }
    
    /**
     * Show the edit nickname preference
     */
    private void editNick() {
    	
    	PreferenceScreen screen = (PreferenceScreen)findPreference("settings");
		int itemPos = findPreference("name").getOrder();
		screen.onItemClick(null, null, itemPos, 0);
    }
    
    /**
     * Acquire the wakelock on resume
     */
    public void onResume() {
    	
    	super.onResume();
    	this.wakeLock.acquire();
    }
    
    /**
     * Release the wakelock on destroy
     */
    public void onDestroy() {
    	
    	super.onDestroy();
    	this.wakeLock.release();
    }
    
    /**
     * Disable animations when leaving the activity
     */
    public void onBackPressed() {
    	
    	if (((EditTextPreference)findPreference("name")).getText().trim().equals("")) {
    	
    		askEditNick();
    		
    	} else {
    	
	    	this.finish();
	    	this.overridePendingTransition(0, 0);
    	}
    }

    /**
     * Called by XMLLoaderTask
     * 
     * @param InputStream xmlStream
     */
	public void xmlCallback(InputStream xmlStream) {
				
		try {
			Log.d("DEBUG", "XML: " + InputStreamToString.convert(xmlStream));
		} catch (IOException e) {
		}
	}
}

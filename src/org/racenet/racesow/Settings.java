package org.racenet.racesow;

import org.racenet.racesow.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

/**
 * Activity to handle the game settings
 * 
 * @author soh#zolex
 *
 */
public class Settings extends PreferenceActivity {
	
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
        
        OnPreferenceChangeListener listener = new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference pref, Object value) {
				
				SharedPreferences prefs = Settings.this.getSharedPreferences("racesow", Context.MODE_PRIVATE);
				
				if (pref.getKey().equals("sound")) {
					
					prefs.edit().putBoolean("sound", value.toString().equals("true") ? true : false).commit();

				} else if (pref.getKey().equals("celshading")) {
					
					prefs.edit().putBoolean("celshading", value.toString().equals("true") ? true : false).commit();
					if (value.toString().equals("true")) {
						new AlertDialog.Builder(Settings.this)
	        		        .setMessage("Toon-Shading reduces the perfomance.")
	        		        .setNeutralButton("OK", null)
	        		        .show();
					}
					
				} else if (pref.getKey().equals("name")) {
					
					prefs.edit().putString("name", value.toString()).commit();
				}
				
				return true;
			}
		};
        
		findPreference("name").setOnPreferenceChangeListener(listener);
		findPreference("sound").setOnPreferenceChangeListener(listener);
		findPreference("celshading").setOnPreferenceChangeListener(listener);
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
    	
    	this.finish();
    	this.overridePendingTransition(0, 0);
    }
}

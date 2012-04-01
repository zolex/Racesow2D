package org.racenet.racesow;

import org.racenet.racesow.R;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity {
	
	private NotificationManager manager;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        
        OnPreferenceChangeListener listener = new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference pref, Object value) {
				
				if (pref.getKey().equals("sound")) {
					

				} else if (pref.getKey().equals("celshading")) {
					
					if (value.toString().equals("true")) {
						new AlertDialog.Builder(Settings.this)
	        		        .setMessage("Toon-Shading reduces the perfomance.")
	        		        .setNeutralButton("OK", null)
	        		        .show();
					}
				}
				
				return true;
			}
		};
        
		findPreference("sound").setOnPreferenceChangeListener(listener);
		findPreference("celshading").setOnPreferenceChangeListener(listener);
    }
}

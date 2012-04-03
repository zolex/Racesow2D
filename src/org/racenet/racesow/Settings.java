package org.racenet.racesow;

import org.racenet.racesow.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        setContentView(R.layout.listview);
        
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
}

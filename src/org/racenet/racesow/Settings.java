package org.racenet.racesow;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.racenet.framework.XMLParser;
import org.racenet.helpers.IsServiceRunning;
import org.racenet.racesow.R;
import org.racenet.racesow.models.Database;
import org.racenet.racesow.threads.XMLLoaderTask;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Activity to handle the game settings
 * 
 * @author soh#zolex
 *
 */
public class Settings extends PreferenceActivity implements XMLCallback {
	
	WakeLock wakeLock;
	String nick;
	ProgressDialog pd;
	SharedPreferences prefs;
	
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
        
    	this.prefs = Settings.this.getSharedPreferences("racesow", Context.MODE_PRIVATE);
    	
    	if (getIntent().getBooleanExtra("setNick", false)) {
    		
    		editNick();
    	}
    	
    	
        OnPreferenceChangeListener listener = new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference pref, Object value) {
				
				if (pref.getKey().equals("sound")) {
					
					prefs.edit().putBoolean("sound", value.toString().equals("true") ? true : false).commit();

				} else if (pref.getKey().equals("ambience")) {
					
					prefs.edit().putBoolean("ambience", value.toString().equals("true") ? true : false).commit();

				} else if (pref.getKey().equals("gfx")) {
					
					prefs.edit().putBoolean("gfx", value.toString().equals("true") ? true : false).commit();
					
				} else if (pref.getKey().equals("blur")) {
					
					prefs.edit().putBoolean("blur", value.toString().equals("true") ? true : false).commit();
					
				} else if (pref.getKey().equals("name")) {
					
					nick = value.toString().trim();
					if (nick.equals("")) {
				
						askEditNick();
						
					} else {
					
						String session = Database.getInstance().getSession(nick);
						if (session != null) {
							
							checkSession(session);
							
						} else {
						
							checkNick();
						}
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
     * Check if the session is valid
     * 
     * @param String session
     */
    private void checkSession(String session) {
    	
    	String url = "http://racesow2d.warsow-race.net/accounts.php";
		List<NameValuePair> values = new ArrayList<NameValuePair>();
		values.add(new BasicNameValuePair("action", "session"));
		values.add(new BasicNameValuePair("id", session));
		final XMLLoaderTask task = new XMLLoaderTask(Settings.this);
		task.execute(url, values);
		
		pd = new ProgressDialog(Settings.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("Checking session...");
		pd.setCancelable(true);
		pd.setOnCancelListener(new OnCancelListener() {
			
			public void onCancel(DialogInterface dialog) {

				task.cancel(true);
				editNick();
			}
		});
		pd.show();
    }
    
    /**
     * Check if the nick is available
     */
    private void checkNick() {
    	
    	String url = "http://racesow2d.warsow-race.net/accounts.php";
		List<NameValuePair> values = new ArrayList<NameValuePair>();
		values.add(new BasicNameValuePair("action", "check"));
		values.add(new BasicNameValuePair("name", nick));
		final XMLLoaderTask task = new XMLLoaderTask(Settings.this);
		task.execute(url, values);
		
		pd = new ProgressDialog(Settings.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("Checking name...");
		pd.setCancelable(true);
		pd.setOnCancelListener(new OnCancelListener() {
			
			public void onCancel(DialogInterface dialog) {

				task.cancel(true);
				editNick();
			}
		});
		pd.show();
    }
    
    /**
     * Called by XMLLoaderTask
     * 
     * @param InputStream xmlStream
     */
	public void xmlCallback(InputStream xmlStream) {
				
		pd.dismiss();
		
		// not online, just use the nick for now without checking it
		if (xmlStream == null) {
			
			prefs.edit().putString("name", nick).commit();
			return;
		}
		
		XMLParser parser = new XMLParser();
		parser.read(xmlStream);
		
		// error response
		NodeList errors = parser.doc.getElementsByTagName("error");
		if (errors.getLength() == 1) {
			
			String message = parser.getNodeValue(errors.item(0));
			showError(message);
			return;
		}
			
		// name availability check response
		NodeList checks = parser.doc.getElementsByTagName("checkname");
		if (checks.getLength() == 1) {
			
			try {
				
				int available = Integer.parseInt(parser.getValue((Element)checks.item(0), "available"));
				if (available == 1) {
					
					askRegister();
					
				} else {
					
					askLogin();
				}
				
			} catch (NumberFormatException e) {}
			return;
		}
		
		// login response
		NodeList auths = parser.doc.getElementsByTagName("auth");
		if (auths.getLength() == 1) {
			
			try {

				Element auth = (Element)auths.item(0);
				int result = Integer.parseInt(parser.getValue(auth, "result"));
				if (result == 1) {
					
					String session = parser.getValue(auth, "session");
					loginSuccessful(session);
					
				} else {
					
					showLogin("Wrong password. Please try again.");
				}
				
			} catch (NumberFormatException e) {}
			return;
		}
		
		// session response
		NodeList sessions = parser.doc.getElementsByTagName("checksession");
		if (sessions.getLength() == 1) {
			
			try {

				Element session = (Element)sessions.item(0);
				int result = Integer.parseInt(parser.getValue(session, "result"));
				if (result == 1) {
					
					Database.getInstance().setSession(nick, parser.getValue(session, "session"));
					prefs.edit().putString("name", nick).commit();
					
				} else {
					
					showLogin("Your session has expired.\nPlease enter your Password.");
				}
				
			} catch (NumberFormatException e) {}
			return;
		}
	}
    
	/**
	 * Show an error message
	 * 
	 * @param String message
	 */
	private void showError(String message) {
		
		new AlertDialog.Builder(Settings.this)
			.setCancelable(false)
	        .setMessage("Error: " + message)
	        .setNeutralButton("OK", null)
	        .show();
	}
	
	/**
	 * On Successful login
	 * 
	 * @param String session
	 */
	private void loginSuccessful(String session) {
		
		new AlertDialog.Builder(Settings.this)
			.setCancelable(false)
	        .setMessage("Login successful")
	        .setNeutralButton("OK", null)
	        .show();
		
		Database.getInstance().setSession(nick, session);
		prefs.edit().putString("name", nick).commit();
	}
	
	/**
	 * Ask the user to login
	 */
	private void askLogin() {
		
		new AlertDialog.Builder(Settings.this)
			.setCancelable(false)
	        .setMessage("The name '"+ this.nick +"' is registered.\nDo you want to login?")
	        .setPositiveButton("Yes", new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
	
					showLogin("Please enter your password.");
				}
			})
			.setNegativeButton("No", new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
	
					editNick();
				}
			})
	        .show();
	}
	
	/**
	 * Ask the user to register the name
	 */
	private void askRegister() {
		
		new AlertDialog.Builder(Settings.this)
			.setCancelable(false)
	        .setMessage("Do you want to register the name '"+ this.nick +"'?")
	        .setPositiveButton("Yes", new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
				
					showRegistration();
				}
			})
			.setNegativeButton("No, thanks", new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					
					prefs.edit().putString("name", nick).commit();
				}
			})
	        .show();
	}
	
	/**
	 * Show the registration form
	 */
	private void showRegistration() {
		
		ScrollView layout = (ScrollView)View.inflate(Settings.this, R.layout.registration, null);
		TextView name = (TextView)layout.findViewById(R.id.field_name);
		name.setText(nick);
		
		new AlertDialog.Builder(Settings.this)
			.setView(layout)
			.setCancelable(true)
	        .setNeutralButton("Register", null)
	        .show();
	}
	
	/**
	 * Show the login window
	 */
	private void showLogin(String message) {
		
		final EditText pass = new EditText(Settings.this);
		pass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
		AlertDialog login = new AlertDialog.Builder(Settings.this)
			.setCancelable(true)
			.setView(pass)
	        .setMessage(message)
	        .setPositiveButton("Login", new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {

					String password = pass.getText().toString();
					
					String url = "http://racesow2d.warsow-race.net/accounts.php";
					List<NameValuePair> values = new ArrayList<NameValuePair>();
					values.add(new BasicNameValuePair("action", "auth"));
					values.add(new BasicNameValuePair("name", nick));
					values.add(new BasicNameValuePair("pass", password));
					new XMLLoaderTask(Settings.this).execute(url, values);
					
					pd = new ProgressDialog(Settings.this);
					pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					pd.setMessage("Loggin in...");
					pd.setCancelable(true);
					pd.setOnCancelListener(new OnCancelListener() {
						
						public void onCancel(DialogInterface dialog) {

							editNick();
						}
					});
					pd.show();
				}
			}).create();
		
		login.setOnCancelListener(new OnCancelListener() {
			
			public void onCancel(DialogInterface dialog) {
				
				editNick();
			}
		});
		
		login.show();
	        
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
}

package org.racenet.racesow;

import java.io.File;

import org.racenet.framework.GLGame;
import org.racenet.framework.interfaces.Screen;
import org.racenet.racesow.GameScreen.GameState;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Main Activity of the game
 * 
 * @author so#zolex
 *
 */
public class Racesow extends GLGame {
	
	public static boolean LOOPER_PREPARED = false;
	
	/**
	 * Create the activity
	 */
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		this.getFileIO().createDirectory("racesow" + File.separator + "downloads");
		
		// when no nickname ist set, ask the user to do so
		SharedPreferences prefs = getSharedPreferences("racesow", Context.MODE_PRIVATE);
		if (prefs.getString("name", "").equals("")) {
			
			new AlertDialog.Builder(this)
		        .setMessage("Please set a nickname for the highscores")
		        .setPositiveButton("OK", new OnClickListener() {
					
					public void onClick(DialogInterface arg0, int arg1) {
						
						Intent i = new Intent((Activity)Racesow.this, Settings.class);
						i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					    ((Activity)Racesow.this).startActivity(i);
					}
				})
		        .show();
		}
	}
	
	/**
	 * Initially show the main menu
	 */
    public Screen getStartScreen() {
    	
    	String screen = getIntent().getStringExtra("screen");
    	if (screen == null) {
    	
    		return new MenuScreen(this);
    		
    	} else if (screen.equals("loading")) {
    		
    		return new LoadingScreen(this, getIntent().getStringExtra("map"), getIntent().getStringExtra("demo"));
    	}
    	
    	return null;
    }
    
    /**
     * Handle the back-button in different situations
     */
    public void onBackPressed() {
    	
    	Screen screen = this.getCurrentScreen();
    	String screenName = screen.getClass().getName();
    	
    	// if we are "inGame"
    	if (screenName.endsWith("GameScreen")) {
    		
    		GameScreen gameScreen = (GameScreen)screen;
    		
    		if (gameScreen.demoMode) {
    			
    			super.onBackPressed();
    		}
    		
    		// restart the race
    		else if (gameScreen.map.inRace() || gameScreen.map.raceFinished()) {
    		
    			gameScreen.state = GameState.Running;
    			gameScreen.map.restartRace(gameScreen.player);
    			
    		// return to maps menu
    		} else {
    			
    			this.glView.queueEvent(new Runnable() {

                    public void run() {
                       
                    	Racesow.this.setScreen(new MapsScreen(Racesow.this));
                    }
                });
    		}
    	
    	// return to main menu
    	} else if (screenName.endsWith("MapsScreen")) {
    		
			this.glView.queueEvent(new Runnable() {

                public void run() {
                   
                	Racesow.this.setScreen(Racesow.this.getStartScreen());
                }
            });
    	
		// quit the application
    	} else {
    		
    		LOOPER_PREPARED = false; // just to be sure...
    		super.onBackPressed();
    	}
    }
}

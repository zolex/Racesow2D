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

public class Racesow extends GLGame {	
	
	public static boolean LOOPER_PREPARED = false;
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		this.getFileIO().createDirectory("racesow" + File.separator + "downloads");
		
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
	
    public Screen getStartScreen() {
    	
        return new MenuScreen(this);
    }
    
    public void onBackPressed() {
    	
    	Screen screen = this.getCurrentScreen();
    	String screenName = screen.getClass().getName();
    	
    	if (screenName.endsWith("GameScreen")) {
    		
    		GameScreen gameScreen = (GameScreen)screen;
    		if (gameScreen.map.inRace() || gameScreen.map.raceFinished()) {
    		
    			gameScreen.state = GameState.Running;
    			gameScreen.map.restartRace(gameScreen.player);
    			
    		} else {
    			
    			this.glView.queueEvent(new Runnable() {

                    public void run() {
                       
                    	Racesow.this.setScreen(new MapsScreen(Racesow.this));
                    }
                });
    		}
    	
    	} else if (screenName.endsWith("MapsScreen")) {
    		
			this.glView.queueEvent(new Runnable() {

                public void run() {
                   
                	Racesow.this.setScreen(Racesow.this.getStartScreen());
                }
            });
    	
    	} else {
    		
    		LOOPER_PREPARED = false; // just to be sure...
    		super.onBackPressed();
    	}
    }
}

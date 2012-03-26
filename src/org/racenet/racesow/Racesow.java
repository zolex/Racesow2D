package org.racenet.racesow;

import org.racenet.framework.GLGame;
import org.racenet.framework.interfaces.Screen;

import android.util.Log;

public class Racesow extends GLGame {	
	
    public Screen getStartScreen() {
    	
        return new GameScreen(this);
    }
    
    public void onBackPressed() {
    	
    	Screen screen = this.getCurrentScreen();
    	String screenName = screen.getClass().getName();
    	
    	if (screenName.endsWith("GameScreen")) {
    		
    		GameScreen gameScreen = (GameScreen)screen;
    		if (gameScreen.map.inRace() || gameScreen.map.raceFinished()) {
    		
    			gameScreen.map.restartRace(gameScreen.player);
    			
    		} else {
    			
    			this.finish();
    		}
    	
    	} else {
    		
    		this.finish();
    	}
    }
}

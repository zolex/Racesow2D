package org.racenet.racesow;

import org.racenet.framework.AndroidGame;
import org.racenet.framework.interfaces.Screen;

public class GameActivity extends AndroidGame {

	public Screen getStartScreen() {
		
		return new MenuLoadingScreen(this);
	}
	
	public void onBackPressed() {
		
		String screen = getCurrentScreen().getClass().getName();
		if (screen.endsWith("TestScreen")) {
			
			setScreen(new MenuScreen(this));
		
		} else {
			
			finish();
		}
	}
}

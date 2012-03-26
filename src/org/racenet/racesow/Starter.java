package org.racenet.racesow;

import org.racenet.framework.AndroidGame;
import org.racenet.framework.interfaces.Screen;

public class Starter extends AndroidGame {

	public Screen getStartScreen() {
		
		return new MenuLoadingScreen(this);
	}
}

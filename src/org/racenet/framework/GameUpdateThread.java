package org.racenet.framework;

import org.racenet.framework.interfaces.Game;
import org.racenet.framework.interfaces.Screen;

import android.os.Looper;
import android.util.Log;

public class GameUpdateThread extends Thread {

	Game game;
	public boolean stop = false;
	public static boolean looperPrepared = false;
	
	public GameUpdateThread(Game game) {
		
		this.game = game;
	}
	
	@Override
    public void run() {
		
		long startTime = System.nanoTime();
		while (!this.stop) {
			
			float delta = (System.nanoTime() - startTime) / 1000000000.0f;
			Screen screen = this.game.getCurrentScreen();
			if (screen != null && delta != 0) {
			
				screen.update(delta);
			}
			
			startTime = System.nanoTime();
		}
	}
}

package org.racenet.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.racenet.framework.interfaces.Game;
import org.racenet.framework.interfaces.Screen;

import android.os.Looper;
import android.util.Log;

public class GameUpdateThread extends Thread {

	GLGame game;
	public boolean stop = false;
	public static HashMap<String, Boolean> loopers = new HashMap<String, Boolean>();
	
	public GameUpdateThread(GLGame game) {
		
		this.game = game;
	}
	
	@Override
    public void run() {
		
		long startTime = System.nanoTime();
		while (!this.stop) {
			
			final float delta = (System.nanoTime() - startTime) / 1000000000.0f;
			startTime = System.nanoTime();
			final Screen screen = this.game.getCurrentScreen();
			if (screen != null && delta != 0) {
			
				//this.game.runOnUiThread(new Runnable() {
					
					//public void run() {
						
						screen.update(delta);
					//}
				//});
			}
				
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
	}
}

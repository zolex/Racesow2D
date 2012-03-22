package org.racenet.racesow;

import org.racenet.framework.Assets;
import org.racenet.framework.interfaces.Game;
import org.racenet.framework.interfaces.Graphics;
import org.racenet.framework.interfaces.Pixmap;
import org.racenet.framework.interfaces.Screen;
import org.racenet.framework.interfaces.Graphics.PixmapFormat;

import android.graphics.Color;

public class TestLoadingScreen extends Screen {
    
	public TestLoadingScreen(final Game game) {
		
		super(game);
		
		final Graphics g = game.getGraphics();
		g.clear(Color.BLACK);
		
		g.drawText("Loading...", 32, Color.WHITE, game.getScreenWidth() / 2 - 75, game.getScreenHeight() / 2 - 16, "verdana.ttf");
		
		// load assets in a thread so we can meanwhile display this loading screen 
		new Thread() {
			
			public void run() {
				
				Assets assets = Assets.getInstance();
				
				for (int n = 0; n < TestScreen.NUM_PIXMAPS; n++) {
					
					Pixmap pixmap = g.newPixmap("test.png", PixmapFormat.ARGB8888);
					pixmap.resizeHeight(pixmap.getHeight() * 2);
					assets.addPixmap("circle_" + n, pixmap);
				}	
			
				game.setScreen(new TestScreen(game));
			}
			
		}.start();
	}

	@Override
	public void update(float deltaTime) {
		
		
	}

	@Override
	public void present(float deltaTime) {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		
	}
}
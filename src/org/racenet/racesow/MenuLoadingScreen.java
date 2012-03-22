package org.racenet.racesow;

import org.racenet.framework.Assets;
import org.racenet.framework.interfaces.Game;
import org.racenet.framework.interfaces.Graphics;
import org.racenet.framework.interfaces.Screen;
import org.racenet.framework.interfaces.Graphics.PixmapFormat;

import android.graphics.Color;

public class MenuLoadingScreen extends Screen {
    
	public MenuLoadingScreen(final Game game) {
		
		super(game);
		
		final Graphics g = game.getGraphics();
		g.clear(Color.BLACK);
		// TODO: render splash screen

		// load assets in a thread so we can meanwhile display this loading screen 
		new Thread() {
			
			public void run() {
				
				Assets assets = Assets.getInstance();
				assets.addPixmap("play_button", g.newPixmap("play.jpg", PixmapFormat.ARGB8888));
				assets.addPixmap("menu_header", g.newPixmap("racesow.jpg", PixmapFormat.ARGB8888));
				
				game.setScreen(new MenuScreen(game));
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
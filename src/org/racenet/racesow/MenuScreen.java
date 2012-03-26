package org.racenet.racesow;

import java.util.List;

import org.racenet.framework.AndroidGame;
import org.racenet.framework.AndroidScreen;
import org.racenet.framework.Assets;
import org.racenet.framework.interfaces.Game;
import org.racenet.framework.interfaces.Graphics;
import org.racenet.framework.interfaces.Pixmap;
import org.racenet.framework.interfaces.Input.TouchEvent;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

public class MenuScreen extends AndroidScreen {

	Pixmap button, button2, button3, button4;
	
	public MenuScreen(Game game) {
		
		super(game);
		AndroidGame aGame = (AndroidGame)game;
		Assets assets = Assets.getInstance();
		Graphics g = game.getGraphics();
		g.clear(Color.BLACK);
		
		Pixmap header = assets.getPixmap("menu_header");
		header.resizeWidth(aGame.getScreenWidth());
		g.drawPixmap(header, 0, 0);
		
		button = assets.getPixmap("play_button");
		button.resizeWidth(aGame.getScreenWidth() / 10);
		g.drawPixmap(button, aGame.getScreenWidth() / 10, aGame.getScreenHeight() / 2);
	}
	
	@Override
	public void update(float deltaTime) {

		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		int length = touchEvents.size();
		for (int i = 0; i < length; i++) {
			
			TouchEvent e = touchEvents.get(i);
			
			if (touchEventInPixmap(e, button)) {
				
				Intent intent = new Intent((AndroidGame)game, Racesow.class);
				((AndroidGame)game).startActivity(intent);
			}
		}
	}
}

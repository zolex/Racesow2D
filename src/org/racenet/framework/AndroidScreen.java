package org.racenet.framework;

import org.racenet.framework.interfaces.Game;
import org.racenet.framework.interfaces.Pixmap;
import org.racenet.framework.interfaces.Screen;
import org.racenet.framework.interfaces.Input.TouchEvent;

public class AndroidScreen extends Screen {

	public AndroidScreen(Game game) {
		
		super(game);
	}
	
	public boolean touchEventInBounds(TouchEvent e, int x, int y, int width, int height) {
		
		if (e.x > x && e.x < x + width - 1 && e.y > y && e.y < y + height - 1) {
			
			return true;
			
		} else {
			
			return false;
		}
	}

	public boolean touchEventInPixmap(TouchEvent e, Pixmap p) {
		
		return touchEventInBounds(e, p.getX(), p.getY(), p.getWidth(), p.getHeight());
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

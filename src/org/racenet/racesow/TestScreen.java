package org.racenet.racesow;

import java.util.List;

import org.racenet.framework.AndroidScreen;
import org.racenet.framework.Assets;
import org.racenet.framework.interfaces.Game;
import org.racenet.framework.interfaces.Graphics;
import org.racenet.framework.interfaces.Input.TouchEvent;
import org.racenet.framework.interfaces.Pixmap;

import android.graphics.Color;

public class TestScreen extends AndroidScreen {

	public static final int NUM_PIXMAPS = 4;
	int draggedByPointer[] = new int[NUM_PIXMAPS];
	Assets assets;
	
	public TestScreen(Game game) {
		
		super(game);
		
		Graphics g = game.getGraphics();
		g.clear(Color.BLACK);
		
		assets = Assets.getInstance();
		
		for (int i = 0; i < NUM_PIXMAPS; i++) {
			
			draggedByPointer[i] = -1;
			g.drawPixmap(assets.getPixmap("circle_" + i), 150 * i, ((GameActivity)game).getScreenHeight() / 2);
		}
	}
	
	public void dispose() {
		
		for (int i = 0; i < NUM_PIXMAPS; i++) {
			
			assets.getPixmap("circle_" + i).dispose();
		}
		
		assets = null;
	}

	@Override
	public void update(float deltaTime) {
		
		Graphics g = game.getGraphics();
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		int length = touchEvents.size();
		boolean wasCleared = false;
		for (int i = 0; i < length; i++) {
			
			TouchEvent e = touchEvents.get(i);
			
			int pointerId = e.pointer;
			
			if (e.type == TouchEvent.TOUCH_DOWN) {
			
				for (int which = 0; which < NUM_PIXMAPS; which++) {
					
					if (touchEventInPixmap(e, assets.getPixmap("circle_" + which))) {
						
						draggedByPointer[which] = pointerId;
					}
				}
			
			} else if (e.type == TouchEvent.TOUCH_UP) {
				
				for (int which = 0; which < NUM_PIXMAPS; which++) {
					
					if (draggedByPointer[which] == pointerId) {
						
						draggedByPointer[which] = -1;
					}
				}
			
			} else if (e.type == TouchEvent.TOUCH_DRAG) {
				
				wasCleared = true;
				g.clear(Color.BLACK);
				
				// find the dragged pixmap and draw it to the new position
				int draggedPixmap = -1;
				for (int which = 0; which < NUM_PIXMAPS; which++) {
				
					if (draggedByPointer[which] == pointerId) {
						
						draggedPixmap = which;
						Pixmap pixmap = assets.getPixmap("circle_" + which);
						g.drawPixmap( pixmap, e.x - pixmap.getWidth() / 2, e.y - pixmap.getHeight() / 2);
						break;
					}
				}
				
				// redraw all other pixmaps
				for (int which = 0; which < NUM_PIXMAPS; which++) {
					
					if (which != draggedPixmap) {
						
						Pixmap pixmap = assets.getPixmap("circle_" + which);
						g.drawPixmap( pixmap, pixmap.getX(), pixmap.getY());
					}
				}
			}
		}
		
		if (wasCleared) {
			
			g.drawText(String.valueOf(new Integer((int)(1 / deltaTime))), 16, Color.GREEN, 100, 100, "verdana.ttf");
		}
	}
}

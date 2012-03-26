package org.racenet.racesow;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.racenet.framework.interfaces.Game;
import org.racenet.framework.interfaces.Input.TouchEvent;
import org.racenet.framework.interfaces.Screen;
import org.racenet.framework.Camera2;
import org.racenet.framework.GLGame;
import org.racenet.framework.GLGraphics;
import org.racenet.framework.Vector2;
import org.racenet.framework.CameraText;

public class Racesow extends GLGame {
	
	Player player;
	CameraText ups, fps;
	Map map;
	
	Vector2 gravity = new Vector2(0, -30);
	
	
	class WorldScreen extends Screen {
		
		Camera2 camera;
		GLGraphics glGraphics;
		
		boolean touchedDown = false;
		float touchedDownTime = 0;
		
		int fpsInterval = 5;
		int frames = 10;
		float sumDelta = 0;
		
		public WorldScreen(Game game) {
			
			super(game);
			glGraphics = ((GLGame)game).getGLGraphics();
			
			float camWidth = (float)game.getScreenWidth() / 10;
			float camHeight = (float)game.getScreenHeight() / 10;
			
			ups = new CameraText(-15, camHeight / 2 - 3, 1, 1);
			ups.setupVertices(glGraphics);
			
			fps = new CameraText(15, camHeight / 2 - 3, 1, 1);
			fps.setupVertices(glGraphics);
			
			camera = new Camera2(glGraphics, camWidth, camHeight);
			camera.addHud(ups);
			camera.addHud(fps);
			
			map = new Map((GLGame)game, "map_testing/testing.xml");
			player = new Player((GLGame)game, map.playerX, map.playerY);
			
			fps.setupText((GLGame)game, "fps");
		}

		public void update(float deltaTime) {
			
			camera.updatePosition(deltaTime);
			
			List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
			int len = touchEvents.size();
			for (int i = 0; i < len; i++) {
				
				TouchEvent e = touchEvents.get(i);
				
				if (e.type == TouchEvent.TOUCH_DOWN) {
					
					touchedDown = true;
					touchedDownTime = 0;
				
				} else if (e.type == TouchEvent.TOUCH_UP) {
					
					touchedDown = false;
				}
			}
			
			if (touchedDown) {
				
				player.jump(map,touchedDownTime);
				touchedDownTime += deltaTime;
			}
			
			player.move(gravity, map, deltaTime);		
			
			camera.setPosition(player.position.x + 20, camera.position.y);		
			
			// draw UPS
			ups.setupText((GLGame)game, "ups " + String.valueOf(new Integer((int)player.virtualSpeed)));
			
			// draw FPS
			frames--;
			sumDelta += deltaTime;
			if (frames == 0) {
			
				fps.setupText((GLGame)game, "fps " + String.valueOf(new Integer((int)(fpsInterval / sumDelta))));
				frames = fpsInterval;
				sumDelta = 0;
				
			}
		}

		public void present(float deltaTime) {
			
			GL10 gl = glGraphics.getGL();
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			
			gl.glFrontFace(GL10.GL_CCW);
			gl.glEnable(GL10.GL_CULL_FACE);
			gl.glCullFace(GL10.GL_BACK);
			
			camera.setViewportAndMatrices();
			
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			
			map.draw();
			player.draw();
			
			ups.draw(gl);
			fps.draw(gl);
		}

		public void pause() {

		}

		public void resume() {

			map.reloadTextures();
			player.reloadTextures();
		}

		public void dispose() {

			map.dispose();
			player.dispose();
		}
	}
	
    public Screen getStartScreen() {
    	
        return new WorldScreen(this);
    }
}

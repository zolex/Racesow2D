package org.racenet.racesow;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.racenet.framework.interfaces.Game;
import org.racenet.framework.interfaces.Input.TouchEvent;
import org.racenet.framework.interfaces.Screen;
import org.racenet.framework.Camera2;
import org.racenet.framework.GLGame;
import org.racenet.framework.GLGraphics;
import org.racenet.framework.GameObject;
import org.racenet.framework.Mesh;
import org.racenet.framework.SpatialHashGrid;
import org.racenet.framework.Vector2;
import org.racenet.framework.CameraText;

public class GLGameTest2 extends GLGame {
	
	Player player;
	CameraText ups, fps;
	Map map = new Map();
	
	Vector2 gravity = new Vector2(0, -30);
	
	
	class WorldScreen extends Screen {
		
		Camera2 camera;
		GLGraphics glGraphics;
		
		boolean touchedDown = false;
		
		int fpsInterval = 5;
		int frames = 10;
		float sumDelta = 0;
		
		public WorldScreen(Game game) {
			
			super(game);
			glGraphics = ((GLGame)game).getGLGraphics();
			
			ups = new CameraText(-15, 17, 1, 1);
			ups.setupVertices(glGraphics);
			
			fps = new CameraText(15, 17, 1, 1);
			fps.setupVertices(glGraphics);
			
			camera = new Camera2(glGraphics, (float)game.getScreenWidth() / 10, (float)game.getScreenHeight() / 10);
			camera.addHud(ups);
			camera.addHud(fps);
			
			player = new Player((GLGame)game, 7, 22, 3.4f, 6.5f, "player.png", "player2.png");
			
			map.addMesh(new Mesh((GLGame)game, -100, 0, 2000, 10, "wood.png"));
			map.addMesh(new Mesh((GLGame)game, 0, 10, 28, 5, "wood.png"));
			map.addMesh(new Mesh((GLGame)game, 60, 10, 150, 2.5f, "wood.png"));
			
			map.addMesh(new Mesh((GLGame)game, 100, 12.5f, 7.5f, 2.5f, "wood.png"));
			map.addMesh(new Mesh((GLGame)game, 102.5f, 15.0f, 5.0f, 2.5f, "wood.png"));
			map.addMesh(new Mesh((GLGame)game, 105, 17.5f, 2.5f, 2.5f, "wood.png"));
			
			map.addMesh(new Mesh((GLGame)game, 107.5f, 12.5f, 60, 7.5f, "wood.png"));
			
			//map.addMesh(new Mesh((GLGame)game, 220, 10, 10, 30, "stone.png"));
			
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
				
				} else if (e.type == TouchEvent.TOUCH_UP) {
					
					touchedDown = false;
				}
			}
			
			if (touchedDown) {
				
				player.jump(map);
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
			
			int length = map.numMeshes();
			for (int i = 0; i < length; i++) {
				
				map.getMesh(i).draw();
			}
			
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			
			player.draw();
			
			ups.draw(gl);
			fps.draw(gl);
		}

		public void pause() {

		}

		public void resume() {

			//textureAtlas = new GLTexture((GLGame)game, "player.png");
			//playerRegion = new TextureRegion(textureAtlas, 0, 0, 64, 128);
			
			//player.setupTexture("player.png");
			
			//map.loadTextures();
		}

		public void dispose() {

		}
	}
	
    public Screen getStartScreen() {
    	
        return new WorldScreen(this);
    }
}

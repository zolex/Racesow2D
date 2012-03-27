package org.racenet.racesow;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.racenet.framework.Camera2;
import org.racenet.framework.GLGame;
import org.racenet.framework.GLGraphics;
import org.racenet.framework.GLTexture;
import org.racenet.framework.Mesh;
import org.racenet.framework.interfaces.Game;
import org.racenet.framework.interfaces.Input.TouchEvent;
import org.racenet.framework.interfaces.Screen;

public class MenuScreen extends Screen {
	
	Mesh header;
	Camera2 camera;
	GLGraphics glGraphics;
	
	public MenuScreen(Game game) {
		
		super(game);
		
		glGraphics = ((GLGame)game).getGLGraphics();
		
		float camWidth = (float)game.getScreenWidth();
		float camHeight = (float)game.getScreenHeight();
		camera = new Camera2(glGraphics, camWidth, camHeight);
		
		GLTexture.APP_FOLDER = "racesow";
		header = new Mesh((GLGame)game, 0, 0, camWidth, -1, "racesow.jpg", Mesh.FUNC_NONE, -1, -1);
		header.position.y = camHeight - header.bounds.height;
		header.texture.setFilters(GL10.GL_LINEAR, GL10.GL_LINEAR);
	}
	
	@Override
	public void update(float deltaTime) {

		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		int length = touchEvents.size();
		for (int i = 0; i < length; i++) {
			
			TouchEvent e = touchEvents.get(i);
			if (e.type == TouchEvent.TOUCH_DOWN) {
				
				game.setScreen(new GameScreen(game));
			}
		}
	}

	@Override
	public void present(float deltaTime) {
		
		GL10 gl = glGraphics.getGL();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		gl.glFrontFace(GL10.GL_CCW);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		
		this.camera.setViewportAndMatrices();
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		//gl.glEnable(GL10.GL_BLEND);
		//gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		this.header.draw();
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
		this.header.reloadTexture();
	}

	@Override
	public void dispose() {
		
		this.header.dispose();
	}
}

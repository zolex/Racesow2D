package org.racenet.racesow;

import org.racenet.framework.Camera2;
import org.racenet.framework.GLGame;
import org.racenet.framework.Screen;
import org.racenet.framework.TexturedBlock;
import org.racenet.framework.Vector2;

import android.opengl.GLES10;

/**
 * The game's main menu
 * 
 * @author soh#zolex
 *
 */
public class SplashScreen extends Screen {
	
	public TexturedBlock logo;
	Camera2 camera;
	float scaleFactor = 80;
	float time;
	boolean wait = true;
	
	/**
	 * Constructor.
	 * 
	 * @param GLGame game
	 */
	public SplashScreen(final GLGame game) {
		
		super(game);
		
		this.camera = new Camera2(this.scaleFactor,  this.scaleFactor * (float)game.getScreenHeight() / (float)game.getScreenWidth());
	
		logo = new TexturedBlock("logo.png", TexturedBlock.FUNC_NONE, -1, -1, 0, 0, new Vector2(0, 0), new Vector2(25.6f, 0));
		logo.vertices[0].x = camera.frustumWidth / 2 - logo.width / 2;
		logo.vertices[0].y = camera.frustumHeight / 2 - logo.height / 2;
		
	}
	
	@Override
	/**
	 * Wait some time before launching the menu
	 */
	public void update(float deltaTime) {

		this.time += deltaTime;
		
		if (this.wait && this.time >= 3f) {
			
			this.wait = false;
			this.game.setScreen(new MenuScreen(this.game));
		}
	}

	@Override
	/**
	 * Draw the slpashscreen
	 */
	public void present(float deltaTime) {
		
		GLES10.glClear(GLES10.GL_COLOR_BUFFER_BIT);
		
		GLES10.glFrontFace(GLES10.GL_CCW);
		GLES10.glEnable(GLES10.GL_CULL_FACE);
		GLES10.glCullFace(GLES10.GL_BACK);
		
		this.camera.setViewportAndMatrices(this.game.glView.getWidth(), this.game.glView.getHeight());
		
		GLES10.glEnable(GLES10.GL_TEXTURE_2D);
		GLES10.glEnable(GLES10.GL_BLEND);
		GLES10.glBlendFunc(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);
		
		GLES10.glClearColor(0.6392156862745098f, 0.1529411764705882f, 0.1764705882352941f, 1);
		
		this.logo.draw();
	}

	@Override
	/**
	 * Nothing to do on GLGame pause
	 */
	public void pause() {
		
	}

	@Override
	/**
	 * Reload all tetxures if
	 * openGL context was lost
	 */
	public void resume() {
		
		this.logo.reloadTexture();
	}

	@Override
	/**
	 * Get rid of all textures when leaving the screen
	 */
	public void dispose() {
		
		this.logo.dispose();
	}
}

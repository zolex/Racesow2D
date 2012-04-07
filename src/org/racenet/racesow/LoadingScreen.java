package org.racenet.racesow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import org.racenet.framework.BitmapFont;
import org.racenet.framework.Camera2;
import org.racenet.framework.GLGame;
import org.racenet.framework.GLGraphics;
import org.racenet.framework.GLTexture;
import org.racenet.framework.SpriteBatcher;
import org.racenet.framework.TexturedBlock;
import org.racenet.framework.Vector2;
import org.racenet.framework.interfaces.Game;
import org.racenet.framework.interfaces.Screen;
import org.racenet.helpers.InputStreamToString;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class beeing invoked when loading a map
 * 
 * @author soh-zolex
 *
 */
class LoadingScreen extends Screen {

	public Camera2 camera;
	GLGraphics glGraphics;
	TexturedBlock header;
	SpriteBatcher batcher;
	String mapName, demoFile;
	BitmapFont loading;
	int frames = 0;
	
	/**
	 * Prepare the header graphics and the loading text
	 * 
	 * @param GLGame game
	 * @param String mapName
	 */
	public LoadingScreen(Game game, String mapName, String demoFile) {
			
		super(game);
		this.glGraphics = ((GLGame)game).getGLGraphics();
		this.mapName = mapName;
		this.demoFile = demoFile;
		
		this.camera = new Camera2(glGraphics, 80,  80 * (float)game.getScreenHeight() / (float)game.getScreenWidth());
		
		this.batcher = new SpriteBatcher(this.glGraphics, 96);
		GLTexture font = new GLTexture((GLGame)game, "font.png");
		this.loading = new BitmapFont(font, 0, 0, 17, 30, 50);
		
		GLTexture.APP_FOLDER = "racesow";
		String texture = "racesow.jpg";
		if ((float)game.getScreenWidth() < 600) {
			
			texture = "racesow_small.jpg";
		}
		
		header = new TexturedBlock((GLGame)game, texture, TexturedBlock.FUNC_NONE, -1, -1,
				new Vector2(0, 0), new Vector2(camera.frustumWidth, 0));
		header.setPosition(new Vector2(0, camera.frustumHeight - header.height));
		header.texture.setFilters(GL10.GL_LINEAR, GL10.GL_LINEAR);
	}

	/**
	 * Be sure to clear the touch event buffer
	 */
	public void update(float deltaTime) {
		
		this.game.getInput().getTouchEvents();
		
		if (this.frames++ == 2) {
			
			SharedPreferences prefs = ((Activity)this.game).getSharedPreferences("racesow", Context.MODE_PRIVATE);
			
			// right after drawing the loading screen load
			// the map and player and pass it to the GameScreen
			Map map = new Map(glGraphics.getGL(), this.camera, prefs.getBoolean("celshading", false));
			map.load((GLGame)game, this.mapName, this.demoFile != null);
			Player player = new Player((GLGame)game, map, this.camera, map.playerX, map.playerY, prefs.getBoolean("sound", true));
			
			// load the demo if not empty
			
			boolean demoMode = false;
			if (this.demoFile != null) {
				
				demoMode = true;
				String folder = "racesow" + File.separator + "demos" + File.separator;
				try {
					InputStream demoStream = this.game.getFileIO().readFile(folder + this.demoFile);
					map.parseDemo(InputStreamToString.convert(demoStream));
					
				} catch (IOException e) {
				}
			}
			
			game.setScreen(new GameScreen(this.game, this.camera, map, player, demoMode));
		}
	}

	/**
	 * Show the loading screen and load the map
	 */
	public void present(float deltaTime) {
		
		GL10 gl = glGraphics.getGL();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		gl.glFrontFace(GL10.GL_CCW);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		
		this.camera.setViewportAndMatrices();
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		this.header.draw();
		this.loading.draw(this.batcher, "LOADING", 0.1f, 0.1f, this.camera.frustumWidth / 2 -10, this.camera.frustumHeight / 2);
	}

	/**
	 * Nothing to do on GLGame pause
	 */
	public void pause() {

	}

	/**
	 * Reload all textures when the
	 * openGL context was lost
	 */
	public void resume() {

		this.header.reloadTexture();
		this.loading.texture.reload();
	}

	/**
	 * Get rid of all textures when
	 * leaving the screen
	 */
	public void dispose() {

		this.header.dispose();
		this.loading.texture.dispose();
	}
}
package org.racenet.racesow;

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

class LoadingScreen extends Screen {

	public Camera2 camera;
	GLGraphics glGraphics;
	TexturedBlock header;
	SpriteBatcher batcher;
	String mapName;
	BitmapFont loading;
	
	public LoadingScreen(Game game, String mapName) {
			
		super(game);
		this.glGraphics = ((GLGame)game).getGLGraphics();
		this.mapName = mapName;
		
		this.camera = new Camera2(glGraphics, 80,  80 * (float)game.getScreenHeight() / (float)game.getScreenWidth());
		this.camera.position.set(0, this.camera.frustumHeight / 2);
		
		this.batcher = new SpriteBatcher(this.glGraphics, 96);
		GLTexture font = new GLTexture((GLGame)game, "font.png");
		this.loading = new BitmapFont(font, 0, 0, 17, 30, 50);
		
		GLTexture.APP_FOLDER = "racesow";
		String texture = "racesow.jpg";
		if ((float)game.getScreenWidth() < 600) {
			
			texture = "racesow_small.jpg";
		}
		
		this.header = new TexturedBlock((GLGame)this.game, texture, TexturedBlock.FUNC_NONE, -1, -1,
			new Vector2(0, 0), new Vector2(this.camera.frustumWidth, 0));
		this.header.setPosition(new Vector2(0, this.camera.frustumHeight - this.header.height));
		this.header.texture.setFilters(GL10.GL_LINEAR, GL10.GL_LINEAR);		
	}

	public void update(float deltaTime) {
		
		this.game.getInput().getTouchEvents();
	}

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
		
		this.loading.draw(this.batcher, "LOADING", 0.1f, 0.1f, -10, this.camera.frustumHeight / 2);
		
		// right after drawing the loading screen
		// load map and player and pass it to the GameScreen
		Map map = new Map(glGraphics.getGL(), this.camera);
		map.load((GLGame)game, this.mapName);
		Player player = new Player((GLGame)game, map, this.camera, map.playerX, map.playerY);
		
		game.setScreen(new GameScreen(this.game, this.camera, map, player));
	}

	public void pause() {

	}

	public void resume() {

		this.header.reloadTexture();
		this.loading.texture.reload();
	}

	public void dispose() {

		this.header.dispose();
		this.loading.texture.dispose();
	}
}
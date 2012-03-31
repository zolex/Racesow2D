package org.racenet.racesow;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.racenet.framework.BitmapFont;
import org.racenet.framework.Camera2;
import org.racenet.framework.CameraText;
import org.racenet.framework.GLGame;
import org.racenet.framework.GLGraphics;
import org.racenet.framework.GLTexture;
import org.racenet.framework.SpriteBatcher;
import org.racenet.framework.Vector2;
import org.racenet.framework.interfaces.Game;
import org.racenet.framework.interfaces.Screen;
import org.racenet.framework.interfaces.Input.TouchEvent;

class GameScreen extends Screen {
		
	public Player player;
	CameraText ups, fps, timer;
	public Map map;
	
	Vector2 gravity = new Vector2(0, -30);
	public Camera2 camera;
	GLGraphics glGraphics;
	
	boolean jumpPressed = false;
	float jumpPressedTime = 0;
	boolean shootPressed = false;
	float shootPressedTime = 0;
	SpriteBatcher batcher;
	
	int fpsInterval = 5;
	int frames = 10;
	float sumDelta = 0;
	
	public GameScreen(Game game, Camera2 camera, Map map, Player player) {
			
		super(game);
		this.glGraphics = ((GLGame)game).getGLGraphics();
		this.camera = camera;
		this.map = map;
		this.player = player;
		
		GLTexture.APP_FOLDER = "racesow";
		
		this.batcher = new SpriteBatcher(this.glGraphics, 96);
		GLTexture texture = new GLTexture((GLGame)game, "font.png");
		BitmapFont font = new BitmapFont(texture, 0, 0, 17, 30, 50);
		
		this.fps = new CameraText(this.batcher, font, this.glGraphics.getGL(),
			this.camera.frustumWidth / 2 - 10, this.camera.frustumHeight / 2 - 3);
		this.camera.addHud(this.fps);
		
		this.ups = new CameraText(this.batcher, font, this.glGraphics.getGL(),
			this.camera.frustumWidth / 2 - 25, this.camera.frustumHeight / 2 - 3);
		this.camera.addHud(this.ups);
		
		this.timer = new CameraText(this.batcher, font, this.glGraphics.getGL(),
			this.camera.frustumWidth / 2 - 40, this.camera.frustumHeight / 2 - 3);
		this.camera.addHud(this.timer);
	}

	public void update(float deltaTime) {
		
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			
			TouchEvent e = touchEvents.get(i);
			
			if (e.type == TouchEvent.TOUCH_DOWN) {
				
				if (e.x / (float)game.getScreenWidth() > 0.5f) {
					
					if (!this.jumpPressed) {
						
						this.jumpPressed = true;
						this.jumpPressedTime = 0;
					}
					
				} else {
					
					if (!this.shootPressed) {
						
						this.shootPressed = true;
						this.shootPressedTime = 0;
					}
				}
				

			} else if (e.type == TouchEvent.TOUCH_UP) {
				
				if (e.x / (float)game.getScreenWidth() > 0.5f) {
					
					this.jumpPressed = false;
					this.jumpPressedTime = 0;
					
				} else {
					
					this.shootPressed = false;
					this.shootPressedTime = 0;
				}
			}
		}
		
		if (this.jumpPressed) {
			
			this.player.jump(this.jumpPressedTime);
			this.jumpPressedTime += deltaTime;
		}
		
		if (this.shootPressed) {
			
			this.player.shoot(this.shootPressedTime);
			this.shootPressedTime += deltaTime;
		}
		
		this.player.move(this.gravity, deltaTime, this.jumpPressed);
		
		float camY = this.camera.frustumHeight / 2;
		if (this.player.getPosition().y + 8 > this.camera.frustumHeight) {
			
			camY = this.player.getPosition().y - this.camera.frustumHeight / 2 + 8;
		}
		
		this.camera.setPosition(this.player.getPosition().x + 20, camY);		
		this.map.update(deltaTime);

		this.frames--;
		this.sumDelta += deltaTime;
		if (frames == 0) {
		
			this.fps.text = "fps " + String.valueOf(new Integer((int)(this.fpsInterval / this.sumDelta)));
			this.frames = fpsInterval;
			this.sumDelta = 0;
			
		}

		this.ups.text = "ups " + String.valueOf(new Integer((int)player.virtualSpeed));
		this.timer.text = "t " + String.format("%.2f", map.getCurrentTime());
	}

	public void present(float deltaTime) {
		
		GL10 gl = this.glGraphics.getGL();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		gl.glFrontFace(GL10.GL_CCW);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		
		this.camera.setViewportAndMatrices();
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		this.map.draw();
		this.player.draw();
		
		synchronized (this.player) {
		
			this.camera.drawHud();
		}
	}

	public void pause() {

	}

	public void resume() {

		this.map.reloadTextures();
		this.player.reloadTextures();
	}

	public void dispose() {

		this.map.dispose();
		this.player.dispose();
	}
}
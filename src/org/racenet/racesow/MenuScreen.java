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

import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class MenuScreen extends Screen implements GestureDetector.OnGestureListener {
	
	public Mesh header, playButton, settingsButton, scoresButton;
	Camera2 camera;
	GLGraphics glGraphics;
	GestureDetector gestures;
	float camWidth;
	float camHeight;
	float menuVelocity = 0;
	
	public MenuScreen(Game game) {
		
		super(game);
		
		glGraphics = ((GLGame)game).getGLGraphics();
		
		if (!Racesow.LOOPER_PREPARED) {
			
			Racesow.LOOPER_PREPARED = true;
			Looper.prepare();
		}
		
		gestures = new GestureDetector(this);  
		
		camWidth = (float)game.getScreenWidth();
		camHeight = (float)game.getScreenHeight();
		camera = new Camera2(glGraphics, camWidth, camHeight);
		
		GLTexture.APP_FOLDER = "racesow";
		header = new Mesh((GLGame)game, 0, 0, camWidth, -1, "racesow.jpg", Mesh.FUNC_NONE, -1, -1);
		header.position.y = camHeight - header.bounds.height;
		header.texture.setFilters(GL10.GL_LINEAR, GL10.GL_LINEAR);
		
		playButton = new Mesh((GLGame)game, 0, 0, camWidth / 3, -1, "menu/play.png", Mesh.FUNC_NONE, -1, -1);
		playButton.position.y = camHeight / 2 - playButton.bounds.height / 2 - camHeight / 8;
		playButton.position.x = camWidth / 2 - playButton.bounds.width / 2;
		
		settingsButton = new Mesh((GLGame)game, 0, 0, camWidth / 3, -1, "menu/settings.png", Mesh.FUNC_NONE, -1, -1);
		settingsButton.position.y = camHeight / 2 - playButton.bounds.height / 2 - camHeight / 8;
		settingsButton.position.x = camWidth / 2 - settingsButton.bounds.width / 2 + settingsButton.bounds.width + camWidth / 15;
		
		scoresButton = new Mesh((GLGame)game, 0, 0, camWidth / 3, -1, "menu/scores.png", Mesh.FUNC_NONE, -1, -1);
		scoresButton.position.y = camHeight / 2 - playButton.bounds.height / 2 - camHeight / 8;
		scoresButton.position.x = camWidth / 2 - scoresButton.bounds.width / 2 - scoresButton.bounds.width - camWidth / 15;
	}
	
	public boolean onDown(MotionEvent event) {
		
		this.menuVelocity = 0;
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				
		if (!this.allowMoveMenu(velocityX)) {
			
			return false;
		} 
		
		this.menuVelocity = velocityX;
		
		return true;
	}

	public void onLongPress(MotionEvent arg0) {
		
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		
		return this.moveMenu(distanceX);
	}

	public void onShowPress(MotionEvent arg0) {
		
	}

	public boolean onSingleTapUp(MotionEvent event) {

		this.buttonPress(event.getX(), this.camHeight - event.getY());
		return false;
	}
	
	public void buttonPress(float x, float y) {
		
		if (x > this.playButton.position.x && x < this.playButton.position.x + this.playButton.bounds.width &&
			y > this.playButton.position.y && y < this.playButton.position.y + this.playButton.bounds.height) {
			
			game.setScreen(new GameScreen(game));
		}
	}
	
	public boolean allowMoveMenu(float distance) {
		
		if ((this.scoresButton.position.x > this.camWidth / 2 - this.scoresButton.bounds.width / 2 && distance < 0) ||
			(this.settingsButton.position.x < this.camWidth / 2 - this.settingsButton.bounds.width / 2 && distance > 0)) {
			
			return false;
		}
		
		return true;
	}
	
	public boolean moveMenu(float distance) {
		
		if (!this.allowMoveMenu(distance)) {
			
			return false;
		}
		
		this.playButton.position.x = this.playButton.position.x - distance;
		this.settingsButton.position.x = this.settingsButton.position.x - distance;
		this.scoresButton.position.x = this.scoresButton.position.x - distance;
		return true;
	}
	
	@Override
	public void update(float deltaTime) {

		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		int length = touchEvents.size();
		
		for (int i = 0; i < length; i++) {
			
			gestures.onTouchEvent(touchEvents.get(i).source);  
		}
		
		if (this.menuVelocity != 0) {
			
			this.moveMenu(-this.menuVelocity  * deltaTime);
			this.menuVelocity = this.menuVelocity / 1.05f;
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
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		this.header.draw();
		this.playButton.draw();
		this.settingsButton.draw();
		this.scoresButton.draw();
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

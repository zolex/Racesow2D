package org.racenet.racesow;

import javax.microedition.khronos.opengles.GL10;

import org.racenet.framework.Camera2;
import org.racenet.framework.GLGame;
import org.racenet.framework.GLGraphics;
import org.racenet.framework.GLTexture;
import org.racenet.framework.Screen;
import org.racenet.framework.TexturedBlock;
import org.racenet.framework.Vector2;

import android.app.Activity;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * The game's main menu
 * 
 * @author soh#zolex
 *
 */
public class MenuScreen extends Screen implements OnTouchListener {
	
	public TexturedBlock header;
	Camera2 camera;
	GLGraphics glGraphics;
	GestureDetector gestures;
	float menuVelocity = 0;
	Menu menu;
	
	/**
	 * Constructor.
	 * 
	 * @param GLGame game
	 */
	public MenuScreen(final GLGame game) {
		
		super(game);
		
		glGraphics = game.getGLGraphics();
		
		camera = new Camera2(glGraphics, (float)game.getScreenWidth(), (float)game.getScreenHeight());
		
		glGraphics.getView().setOnTouchListener(this);
		
		menu = new Menu(game, camera.frustumWidth, camera.frustumHeight);
		gestures = new GestureDetector(menu);
		
		menu.addItem("menu/play.png", menu.new Callback() {
			
			public void handle() {
				
				glGraphics.getView().queueEvent(new Runnable() {

                    public void run() {
                       
                    	game.setScreen(new MapsScreen(game));
                    }
                });
			}
		});
		
		menu.addItem("menu/local_scores.png", menu.new Callback() {
			
			public void handle() {
				
				Intent i = new Intent((Activity)game, LocalScores.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				((Activity)game).startActivity(i);
			}
		});
		
		menu.addItem("menu/online_scores.png", menu.new Callback() {
			
			public void handle() {
				
				Intent i = new Intent((Activity)game, OnlineScores.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				((Activity)game).startActivity(i);
			}
		});
		
		menu.addItem("menu/demos.png", menu.new Callback() {
			
			public void handle() {
				
				Intent i = new Intent((Activity)game, DemoList.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				((Activity)game).startActivity(i);
			}
		});
		
		menu.addItem("menu/settings.png", menu.new Callback() {
			
			public void handle() {
				
				Intent i = new Intent((Activity)game, Settings.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				((Activity)game).startActivity(i);
			}
		});
		
		
		menu.addItem("menu/credits.png", menu.new Callback() {
			
			public void handle() {
				
				Intent i = new Intent((Activity)game, Credits.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			    ((Activity)game).startActivity(i);
			}
		});
		
		GLTexture.APP_FOLDER = "racesow";
		String texture = "racesow.jpg";
		if ((float)game.getScreenWidth() < 600) {
			
			texture = "racesow_small.jpg";
		}
		
		header = new TexturedBlock(game, texture, TexturedBlock.FUNC_NONE, -1, -1, 0, 0,
				new Vector2(0, 0), new Vector2(camera.frustumWidth, 0));
		header.setPosition(new Vector2(0, camera.frustumHeight - header.height));
		header.texture.setFilters(GL10.GL_LINEAR, GL10.GL_LINEAR);
	}

	/**
	 * Handle view touch events
	 */
	public boolean onTouch(View v, MotionEvent event) {
		
		this.gestures.onTouchEvent(event);
		return true;
	}
	
	@Override
	/**
	 * Clear framework touchEvent buffer and update the menu
	 */
	public void update(float deltaTime) {

		this.menu.update(deltaTime);
	}

	@Override
	/**
	 * Draw header and menu
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
		this.menu.draw();
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
		
		this.header.reloadTexture();
		this.menu.reloadTextures();
	}

	@Override
	/**
	 * Get rid of all textures when leaving the screen
	 */
	public void dispose() {
		
		this.header.dispose();
		this.menu.dispose();
	}
}

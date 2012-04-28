package org.racenet.racesow;

import java.util.Timer;
import java.util.TimerTask;

import org.racenet.framework.Camera2;
import org.racenet.framework.GLGame;
import org.racenet.framework.GLTexture;
import org.racenet.framework.Particles;
import org.racenet.framework.Screen;
import org.racenet.framework.TexturedBlock;
import org.racenet.framework.Vector2;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLES10;
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
	
	public TexturedBlock header, test;
	Camera2 camera;
	GestureDetector gestures;
	float menuVelocity = 0;
	Menu menu;
	Particles[] p;
	boolean e = false;
	
	/**
	 * Constructor.
	 * 
	 * @param GLGame game
	 */
	public MenuScreen(final GLGame game) {
		
		super(game);
		
		camera = new Camera2((float)game.getScreenWidth(), (float)game.getScreenHeight());
		
		game.glView.setOnTouchListener(this);
		
		menu = new Menu(game, camera.frustumWidth, camera.frustumHeight);
		menu.setScrolling(false);
		game.runOnUiThread(new Runnable() {
			
			public void run() {
				
				gestures = new GestureDetector(menu);
			}
		});
		
		
		menu.addItem("menu/play.png", menu.new Callback() {
			
			public void handle() {
				
				game.glView.queueEvent(new Runnable() {

                    public void run() {
                       
                    	game.glView.setOnTouchListener(null);
                    	game.setScreen(new MapsScreen(game));
                    }
                });
			}
		});
		
		menu.addItem("menu/scores.png", menu.new Callback() {
			
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
		header = new TexturedBlock("racesow.png", TexturedBlock.FUNC_NONE, -1, -1, 0, 0, new Vector2(0, 0), new Vector2(camera.frustumWidth, 0));
		header.vertices[0].x = 0;
		header.vertices[0].y = camera.frustumHeight - header.height;
		header.texture.setFilters(GLES10.GL_LINEAR, GLES10.GL_LINEAR);
		
		this.p = new Particles[3];
		this.p[0] = new Particles("hud/star.png", 12.8f, new Vector2(100, 100), 0);
		this.p[1] = new Particles("hud/star.png", 12.8f, new Vector2(400, 200), 0.5f);
		this.p[2] = new Particles("hud/star.png", 12.8f, new Vector2(700, 300), 1);
	}

	/**
	 * Handle view touch events
	 */
	public boolean onTouch(View v, MotionEvent event) {
		
		if(!this.e && event.getY() < 20 && event.getX() < 20) {
			
			this.e = true;
			Timer t = new Timer();
			t.schedule(new TimerTask() {
				
				@Override
				public void run() {
					
					final int length = MenuScreen.this.p.length;
					for (int i = 0; i < length; i++) {
						
						MenuScreen.this.p[i].reset();
					}
					
					MenuScreen.this.e = false;
				}
			}, 4000);
		}
		
		if (this.gestures != null) {
		
			this.gestures.onTouchEvent(event);
		}
		
		return true;
	}
	
	@Override
	/**
	 * Clear framework touchEvent buffer and update the menu
	 */
	public void update(float deltaTime) {

		this.menu.update(deltaTime);
		
		if (this.e) {
			
			final int length = this.p.length;
			for (int i = 0; i < length; i++) {
				
				this.p[i].update(deltaTime);
			}
		}
	}

	@Override
	/**
	 * Draw header and menu
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
		
		this.header.draw();
		this.menu.draw();
		
		if (this.e) {
			
			final int length = this.p.length;
			for (int i = 0; i < length; i++) {
				
				this.p[i].draw();
			}
		}
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
		final int length = this.p.length;
		for (int i = 0; i < length; i++) {
			
			this.p[i].reloadTexture();
		}
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

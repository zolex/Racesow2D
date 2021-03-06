package org.racenet.racesow;

import java.util.Locale;

import org.racenet.framework.Camera2;
import org.racenet.framework.GLGame;
import org.racenet.framework.GLTexture;
import org.racenet.framework.GameObject;
import org.racenet.framework.Screen;
import org.racenet.framework.TexturedBlock;
import org.racenet.framework.Vector2;
import org.racenet.racesow.models.DemoKeyFrame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.opengl.GLES10;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Class which represents the racesow game itsself.
 * 
 * @author soh#zolex
 *
 */
public class GameScreen extends Screen implements OnTouchListener {
		
	public Player player;
	public Map map;
	
	Vector2 gravity = new Vector2(0, -30);
	public Camera2 camera;
	TexturedBlock pause, play;
	boolean jumpPressed = false;
	float jumpPressedTime = 0;
	boolean shootPressed = false;
	float shootPressedTime = 0;
	public float frameTime = 0;
	public DemoParser demoParser;
	boolean recordDemos;
	private float currentPlayerOffset = 0;
	private float targetPlayerOffset = 0;
	float playerBlur = 0;
	int lowFpsLimit = 30;
	int minFps = Integer.MAX_VALUE;
	long frameNum = 0;
	boolean showFPS, showUPS;
	int lowFPSCOunt = 0;
	double time;
	public int hudUpdateInterval = 5;
	public int frames = hudUpdateInterval;
	boolean waitForNextFrame = true;
	float sumDelta = 0;
	public GameState state = GameState.Running;
	boolean fpsDialogShown = false;
	private DemoKeyFrame demoFrame = new DemoKeyFrame();
	
	public enum GameState {
		
		Running,
		Paused
	}
	
	/**
	 * Constructor
	 * 
	 * @param Game game
	 * @param Camera2 camera
	 * @param Map map
	 * @param Player player
	 */
	public GameScreen(GLGame game, Camera2 camera, Map map, Player player, DemoParser demoParser, boolean recordDemos) {
		
		super(game);
		this.camera = camera;
		this.map = map;
		this.player = player;
		this.player.setGameScreen(this);
		this.demoParser = demoParser;
		this.recordDemos = recordDemos;
		
		GLTexture.APP_FOLDER = "racesow";
		
		game.glView.setOnTouchListener(this);
		
		// user settings
		SharedPreferences prefs = ((Activity)this.game).getSharedPreferences("racesow", Context.MODE_PRIVATE);
		this.showFPS = prefs.getBoolean("fps", false);
		this.showUPS = prefs.getBoolean("ups", true);
		
		// don't show pause/play button when playing a demo
		if (this.demoParser == null) {
			
			this.pause = new TexturedBlock(
					"hud/pause.png", GameObject.FUNC_NONE, -1, -1, 0, 0,
					new Vector2(-this.camera.frustumWidth / 2 + 1 , this.camera.frustumHeight / 2 - 6),
					new Vector2(-this.camera.frustumWidth / 2 + 6, 0));
			
			this.play = new TexturedBlock(
				"hud/play.png", GameObject.FUNC_NONE, -1, -1, 0, 0,
				new Vector2(-this.camera.frustumWidth / 2 + 1 , this.camera.frustumHeight / 2 - 6),
				new Vector2(-this.camera.frustumWidth / 2 + 6, 0));
			
			// add both, because somehow if play is added
			// later it won't show up in the HUD
			this.camera.addHud(play);
			this.camera.addHud(pause);
		}
	}
	
	/**
	 * Set the game into pause state
	 */
	public void pauseGame() {
		
		this.state = GameState.Paused;
		this.camera.addHud(this.play);
		this.camera.removeHud(this.pause);
	}

	/**
	 * Resume the game from pause state
	 */
	public void resumeGame() {
		
		this.state = GameState.Running;
		this.camera.removeHud(this.play);
		this.camera.addHud(this.pause);
	}
	
	/**
	 * Handle view touch events
	 */
	public boolean onTouch(View v, MotionEvent e) {
		
		if (this.demoParser != null) {
			
			return false;
		}
		
		int action = e.getAction() & MotionEvent.ACTION_MASK;
		int pointerIndex = (e.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
        //int pointerId = e.getPointerId(pointerIndex);
        
		switch (action) {

			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
		
				// when touching the pause-button
				if (e.getY(pointerIndex) / (float)game.getScreenHeight() < 0.1f && e.getX(pointerIndex) / (float)game.getScreenWidth() < 0.1f) {
				
					if (this.state == GameState.Running) {
						
						this.pauseGame();
						
					} else if (this.state == GameState.Paused) {
						
						this.resumeGame();
					} 
				}
				
				// when touching the jump-area on the screen
				else if (e.getX(pointerIndex) / (float)game.getScreenWidth() > 0.5f) {
					
					if (!this.jumpPressed) {
						
						this.jumpPressed = true;
						this.jumpPressedTime = 0;
					}
				
				// when touching the shoot-area on the screen
				} else {
					
					if (!this.shootPressed) {
						
						this.shootPressed = true;
						this.shootPressedTime = 0;
					}
				}
				break;
				
			case MotionEvent.ACTION_UP:
	        case MotionEvent.ACTION_POINTER_UP:
	        case MotionEvent.ACTION_CANCEL:
				// this is only for the tutorial
				if (this.state == GameState.Paused) {
					
					this.player.updateTutorial("release");
				}
				
				// when releasing the jump-button
				if (e.getX(pointerIndex) / (float)game.getScreenWidth() > 0.5f) {
					
					this.jumpPressed = false;
					this.jumpPressedTime = 0;
					
				// when releasing the shoot-button
				} else {
					
					this.shootPressed = false;
					this.shootPressedTime = 0;
				}
				break;
		}
		
		return true;
	}

	/**
	 * Update player, map and camera.
	 * Called each frame from GLGame.
	 */
	public void update(final float deltaTime) {
		
		// always wait for the second frame because the
		// first frame somehow has a too long deltaTime
		if (this.waitForNextFrame) {
		
			this.waitForNextFrame = false;
			return;
				
		}
			
		if (this.demoParser != null) {
			
			final DemoKeyFrame f = demoParser.getKeyFrame(this.frameTime);
			if (f != null) {
				
				if (this.player.lastJumpAnim == Player.ANIM_STAND ||
					this.player.lastJumpAnim == Player.ANIM_ROCKET_STAND ||
					this.player.lastJumpAnim == Player.ANIM_PLASMA_STAND) {
					
					this.player.animTime = 0.15f;
				}
				
				this.player.activeAnimId = f.playerAnimation;
				if (this.player.activeAnimId == Player.ANIM_STAND ||
					this.player.activeAnimId == Player.ANIM_JUMP_1 ||
					this.player.activeAnimId == Player.ANIM_JUMP_2 ||
					this.player.activeAnimId == Player.ANIM_ROCKET_STAND ||
					this.player.activeAnimId == Player.ANIM_ROCKET_JUMP_1 ||
					this.player.activeAnimId == Player.ANIM_ROCKET_JUMP_2 ||
					this.player.activeAnimId == Player.ANIM_PLASMA_STAND ||
					this.player.activeAnimId == Player.ANIM_PLASMA_JUMP_1 ||
					this.player.activeAnimId == Player.ANIM_PLASMA_JUMP_2) {
					
					this.player.lastJumpAnim = this.player.activeAnimId;
				}
				
				this.player.setPosition(f.playerPosition);
				this.player.virtualSpeed = f.playerSpeed;
				this.player.animate(deltaTime);
				this.map.handleAmbience(this.player.vertices[0].x);
				
				if (this.player.soundEnabled && f.playerSound != -1) {
					
					this.player.sounds[f.playerSound].play(player.volume);
				}
				
				game.runOnUiThread(new Runnable() {
					
					public void run() {
						
						Racesow.raceTime.setText(String.format(Locale.US, "%.4f", f.mapTime));
					}
				});
				
				
				if (f.decalType != null) {
					
					if (f.decalType.equals("r")) {
						
						TexturedBlock decal = player.rocketPool.newObject();
						decal.vertices[0].x = f.decalX;
						decal.vertices[0].y = f.decalY;
						map.addDecal(decal, Player.rocketDecalTime);
						
					} else if (f.decalType.equals("p")) {
						
						TexturedBlock decal = player.plasmaPool.newObject();
						decal.vertices[0].x = f.decalX;
						decal.vertices[0].y = f.decalY;
						map.addDecal(decal, Player.plasmaDecalTime);
					}
				}
			}
			
		} else {
			
			// execute shoot if requested
			if (this.shootPressed) {
				
				this.player.shoot(this.shootPressedTime);
				this.shootPressedTime += deltaTime;
			}
			
			// execute jump if requested
			if (this.jumpPressed) {
				
				this.player.jump(this.jumpPressedTime);
				this.jumpPressedTime += deltaTime;
			}
			
			//  nothing more to do here when paused
			if (this.state == GameState.Paused) {
				
				if (map.getCurrentTime() > 0 ) {
				
					this.map.pauseTime += deltaTime;
				}
				
				return;
			}
			
			if (this.recordDemos) {
				
				this.demoFrame.frameTime = this.frameTime;
				this.demoFrame.playerPosition = this.player.vertices[0];
				this.demoFrame.playerAnimation = this.player.activeAnimId;
				this.demoFrame.playerSound = this.player.frameSound;
				this.demoFrame.playerSpeed = (int)this.player.virtualSpeed;
				this.demoFrame.decalType = this.player.frameDecalType;
				this.demoFrame.decalX = this.player.frameDecalX;
				this.demoFrame.decalY = this.player.frameDecalY;
				this.demoFrame.mapTime = this.map.getCurrentTime();
				this.map.demoRecorder.demoParts.add(this.demoFrame);
			}
			
			// update the player
			this.player.move(this.gravity, deltaTime, this.jumpPressed, this.jumpPressedTime, this.shootPressed);
		}
		
		// for demo frames
		this.frameTime += deltaTime;
		
		// move the camera upwards if the player goes to high
		float camY = this.camera.frustumHeight / 2;
		if (this.player.vertices[0].y + 12 > this.camera.frustumHeight) {
			
			camY = this.player.vertices[0].y - this.camera.frustumHeight / 2 + 12;
		}
		
		this.playerBlur = 0;
		this.targetPlayerOffset =  Math.min(5000, Math.max(450, this.player.virtualSpeed)) / 128;
		if (this.currentPlayerOffset < this.targetPlayerOffset) {
			
			float diff = (this.targetPlayerOffset - this.currentPlayerOffset);
			this.currentPlayerOffset += diff / 10;
			this.playerBlur = diff / 1.5f;
			
		} else if (this.currentPlayerOffset > this.targetPlayerOffset) {
			
			this.currentPlayerOffset -= ((this.currentPlayerOffset - this.targetPlayerOffset) / 10);
		}
		
		this.camera.setPosition(this.player.vertices[0].x + 27.5f - this.currentPlayerOffset, camY);		
		this.map.update(deltaTime);		
		
		frames--;
		sumDelta += deltaTime;
		
		// pause updating the HUD when submitting scores
		// because it slows down the submission
		if (frames == 0 && !this.player.submittingScore) {
			
			if (this.demoParser == null) {
				
				this.player.handlePlasma(this.shootPressed);
			}
			
			game.runOnUiThread(new Runnable() {
				
				public void run() {
					
					// update hud for time
					if (demoParser == null) {
						
						Racesow.raceTime.setText("t " + String.format(Locale.US, "%.4f", map.getCurrentTime()));
					}
					
	
					if (showUPS) {
					
						// update HUD for player-speed
						Racesow.ups.setText("ups " + String.valueOf(new Integer((int)player.virtualSpeed)));
					}
					
					if (showFPS) {
						
						// update HUD for frames per second
						Racesow.fps.setText("fps " + String.valueOf(new Integer((int)(hudUpdateInterval / sumDelta))));
					}
					
					sumDelta = 0;
					frames = hudUpdateInterval;
				}				
			});
		}
		
		// low fps detection
		this.time += deltaTime;
		this.frameNum++;
		float fps = 1 / deltaTime;
		if (fps < this.lowFpsLimit) {
			
			this.lowFPSCOunt++;
		}
		
		double lowFpsPerTime = this.lowFPSCOunt / this.time;
		
		if (!this.fpsDialogShown &&
			(this.map.gfxHighlights || this.map.enableAmbience || this.player.blurEnabled) &&
			this.time > 2.0f &&
			lowFpsPerTime > 1.0f) {
			
			this.pauseGame();
			this.fpsDialogShown = true;
			this.game.runOnUiThread(new Runnable() {
				
				public void run() {
				
					new AlertDialog.Builder(GameScreen.this.game)
			        .setMessage("You have less then " + GameScreen.this.lowFpsLimit + " FPS. Do you want to disable some features to increase the performance? (You can disable them permanently in the settings.)")
			        .setPositiveButton("YES", new OnClickListener() {
						
						public void onClick(DialogInterface arg0, int arg1) {
							
							GameScreen.this.map.disableHighlights();
							GameScreen.this.map.disableSounds();
							GameScreen.this.player.blurEnabled = false;
							GameScreen.this.resumeGame();
						}
					})
					.setNegativeButton("No", new OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							
							GameScreen.this.resumeGame();
						}
					})
			        .show();
				}
			});
		}
	}

	/**
	 * Draw player map and HUD. Called by GLGame each frame.
	 */
	public void present(float deltaTime) {
		
		GLES10.glFrontFace(GLES10.GL_CCW);
		GLES10.glEnable(GLES10.GL_CULL_FACE);
		GLES10.glCullFace(GLES10.GL_BACK);
		GLES10.glShadeModel(GLES10.GL_FLAT);
		GLES10.glDisable(GLES10.GL_DEPTH_TEST);
		GLES10.glDisable(GLES10.GL_NORMALIZE);
		GLES10.glDisable(GLES10.GL_SMOOTH);
		GLES10.glDisable(GLES10.GL_DITHER);
		GLES10.glEnable(GLES10.GL_TEXTURE_2D);
		GLES10.glEnable(GLES10.GL_BLEND);
		GLES10.glBlendFunc(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);
		
		this.camera.setViewportAndMatrices(this.game.glView.getWidth(), this.game.glView.getHeight());
		
		this.map.draw();
		this.player.draw(this.playerBlur);
		
		this.map.drawFront();
		
		synchronized (this.player) {
		
			this.camera.draw();
		}
	}
	
	/**
	 * Nothing to do on GLGame pause
	 */
	public void pause() {

	}

	/**
	 * Reload all textures because
	 * the openGL context was lost
	 */
	public void resume() {

		this.camera.reloadTexture();
		this.map.reloadTextures();
		this.player.reloadTexture();
	}

	/**
	 * Get rid of all textures and cancel the
	 * current demo when leaving the screen
	 */
	public void dispose() {
		
		this.game.runOnUiThread(new Runnable() {
			
			public void run() {
				
				Racesow.raceTime.setText("");
				Racesow.ups.setText("");
				Racesow.fps.setText("");
				Racesow.tutorial.setText("");
			}
		});
		
		this.camera.dispose();
		this.map.dispose();
		this.player.dispose();
		Racesow.IN_GAME = false;
	}
}
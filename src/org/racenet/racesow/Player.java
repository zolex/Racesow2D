package org.racenet.racesow;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.racenet.framework.Audio;
import org.racenet.framework.Sound;
import org.racenet.framework.AnimatedBlock;
import org.racenet.framework.AnimationPreset;
import org.racenet.framework.Camera2;
import org.racenet.framework.FifoPool;
import org.racenet.framework.XMLParser;
import org.racenet.framework.FifoPool.PoolObjectFactory;
import org.racenet.framework.GameObject;
import org.racenet.framework.Polygon;
import org.racenet.framework.TexturedBlock;
import org.racenet.framework.Vector2;
import org.racenet.racesow.models.Database;
import org.racenet.racesow.threads.HttpLoaderTask;
import org.racenet.racesow.threads.InternalScoresThread;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.opengl.GLES10;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;

/**
 * Class which represents the player in the game.
 * Executes actions like shoot and jump and moves
 * the player through the world.
 * 
 * @author soh#zolex
 */
public class Player extends AnimatedBlock implements HttpCallback {
	
	// animations
	public static final short ANIM_STAND = 0;
	public static final short ANIM_RUN_1 = 1;
	public static final short ANIM_RUN_2 = 2;
	public static final short ANIM_JUMP_1 = 3;
	public static final short ANIM_JUMP_2 = 4;
	public static final short ANIM_WALLJUMP = 5;
	public static final short ANIM_BURN = 6;
	public static final short ANIM_INVISIBLE = 7;
	public static final short ANIM_ROCKET_SHOOT = 8;
	public static final short ANIM_ROCKET_SHOOT_WALL = 9;
	public static final short ANIM_ROCKET_STAND = 10;
	public static final short ANIM_ROCKET_RUN_1 = 11;
	public static final short ANIM_ROCKET_RUN_2 = 12;
	public static final short ANIM_ROCKET_JUMP_1 = 13;
	public static final short ANIM_ROCKET_JUMP_2 = 14;
	public static final short ANIM_ROCKET_WALLJUMP = 15;
	public static final short ANIM_PLASMA_SHOOT = 16;
	public static final short ANIM_PLASMA_STAND = 17;
	public static final short ANIM_PLASMA_RUN_1 = 18;
	public static final short ANIM_PLASMA_RUN_2 = 19;
	public static final short ANIM_PLASMA_JUMP_1 = 20;
	public static final short ANIM_PLASMA_JUMP_2 = 21;
	public static final short ANIM_PLASMA_WALLJUMP = 22;
	public static final short ANIM_DROWN = 23;
	public static final short ANIM_DRIFTSAND = 24;
	public AnimationPreset[] animPresets = new AnimationPreset[25];
	
	// sounds
	public static final short SOUND_JUMP1 = 0;
	public static final short SOUND_JUMP2 = 1;
	public static final short SOUND_WJ1 = 2;
	public static final short SOUND_WJ2 = 3;
	public static final short SOUND_DIE = 4;
	public static final short SOUND_PICKUP = 5;
	public static final short SOUND_ROCKET = 6;
	public static final short SOUND_PLASMA = 7;
	public Sound sounds[] = new Sound[8];
	
	// firerates
	private static final float FIRERATE_ROCKET = 1.75f;
	private static final float FIRERATE_PLASMA = 0.04f;
	private float lastShot = 0;
	
	public final Vector2 velocity = new Vector2();
	private String name;
	private boolean onFloor = false;
	private float lastWallJumped = 0;
	private float distanceOnJump = -1;
	private boolean distanceRemembered = false;
	public float virtualSpeed = 0;
	private float startSpeed = 450;
	private boolean isDead = false;
	private TexturedBlock attachedItem;
	private Camera2 camera;
	private Random rGen;
	public float volume = 0.1f;
	private String model = "male";
	private Map map;
	public FifoPool<TexturedBlock> plasmaPool;
	public FifoPool<TexturedBlock> rocketPool;
	public static float rocketDecalTime = 0.25f;
	public static float plasmaDecalTime = 0.25f;
	public boolean soundEnabled;
	private GameScreen gameScreen;
	GameObject tutorialActive;
	public String frameDecalType = "";
	public float frameDecalX = 0;
	public float frameDecalY = 0;
	public int frameSound = -1;
	boolean recordDemos;
	public boolean blurEnabled;
	float[] plasmaOffset;
	GameObject plasmaBounds;
	float[] worldOffset;
	GameObject worldBounds;
	public int lastJumpAnim = ANIM_JUMP_2;
	private float lastFinishTime;
	ProgressDialog pd;
	long lastRaceID = 0;
	boolean savedLocally;
	public boolean submittingScore = false;
	private int frames = 0;
	
	/**
	 * Constructor.
	 * 
	 * @param GLGame game
	 * @param Map map
	 * @param Camera2 camera
	 * @param float x
	 * @param float y
	 * @param boolean soundEnabled
	 */
	public Player(String name, Map map, Camera2 camera, boolean soundEnabled, boolean blurEnabled, boolean recordDemos) {
		
		// create the TexturedShape with static width and height
		super(new Vector2(map.playerX, map.playerY),
			new Vector2(map.playerX + 12.8f, map.playerY),
			new Vector2(map.playerX + 12.8f, map.playerY + 12.8f),
			new Vector2(map.playerX, map.playerY + 12.8f));
		
		this.worldOffset = new float[] {0, 0, 8.0f, 9.0f};
		this.worldBounds = new GameObject(
			new Vector2(map.playerX + this.worldOffset[0], map.playerY + this.worldOffset[1]),
			new Vector2(map.playerX + this.worldOffset[2], map.playerY + this.worldOffset[1]),
			new Vector2(map.playerX + this.worldOffset[2], map.playerY + this.worldOffset[3]),
			new Vector2(map.playerX + this.worldOffset[0], map.playerY + this.worldOffset[3])
		);
		
		this.plasmaOffset = new float[] {0, 5.6f, 0, 6.4f};
		this.plasmaBounds = new GameObject(
			new Vector2(map.playerX + this.plasmaOffset[0], map.playerY + this.plasmaOffset[1]),
			new Vector2(map.playerX + this.plasmaOffset[2], map.playerY + this.plasmaOffset[1]),
			new Vector2(map.playerX + this.plasmaOffset[2], map.playerY + this.plasmaOffset[3]),
			new Vector2(map.playerX + this.plasmaOffset[0], map.playerY + this.plasmaOffset[3])
		);
		
		this.texScaleHeight = 0.1f;
		this.texScaleWidth = 0.1f;
		this.name = name;
		this.soundEnabled = soundEnabled;
		this.rGen = new Random();
		this.camera = camera;
		this.map = map;
		this.recordDemos = recordDemos;
		this.blurEnabled = blurEnabled;
		
		// load the sounds
		Audio audio = Audio.getInstance();
		this.sounds[SOUND_JUMP1] = audio.newSound("sounds/player/" + this.model + "/jump_1.ogg");
		this.sounds[SOUND_JUMP2] = audio.newSound("sounds/player/" + this.model + "/jump_2.ogg");
		this.sounds[SOUND_WJ1] = audio.newSound("sounds/player/" + this.model + "/wj_1.ogg");
		this.sounds[SOUND_WJ2] = audio.newSound("sounds/player/" + this.model + "/wj_2.ogg");
		this.sounds[SOUND_DIE] = audio.newSound("sounds/player/" + this.model + "/death.ogg");
		this.sounds[SOUND_PICKUP] = audio.newSound("sounds/weapon_pickup.ogg");
		this.sounds[SOUND_ROCKET] = audio.newSound("sounds/rocket_explosion.ogg");
		this.sounds[SOUND_PLASMA] = audio.newSound("sounds/plasmagun.ogg");
		
		this.loadAnimations();
		this.setupVertices();
		
		// create a first-in-first-out pool for plasma decals
		this.plasmaPool = new FifoPool<TexturedBlock>(new PoolObjectFactory<TexturedBlock>() {
			
			public TexturedBlock createObject() {
				
				return new TexturedBlock(
					"decals/plasma_hit.png",
					GameObject.FUNC_NONE,
					-1,
					-1,
					0,
					0,
					new Vector2(-1, 0),
					new Vector2(2, 0)
				);
			}
		}, 10);
		
		// create a first-in-first-out pool for rocket decals
		this.rocketPool = new FifoPool<TexturedBlock>(new PoolObjectFactory<TexturedBlock>() {
	        	
            public TexturedBlock createObject() {
            	
            	return new TexturedBlock(
    				"decals/rocket_hit.png",
    				GameObject.FUNC_NONE,
    				-1,
    				-1,
    				0,
    				0,
    				new Vector2(-3, 0),
    				new Vector2(5, 0)
    			);
            }
        }, 1);
	}
	
	/**
	 * Get the player position
	 */
	public Vector2 getPhysicalPosition() {
		
		return this.worldBounds.vertices[0];
	}
	
	/**
	 * Change all bounds when setting the position
	 * 
	 * @param Vector2 position
	 */
	public void setPosition(Vector2 position) {
		
		this.vertices[0].x = position.x;
		this.vertices[0].y = position.y;
		
		this.worldBounds.vertices[0].x = position.x + this.worldOffset[0];
		this.worldBounds.vertices[0].y = position.y + this.worldOffset[1];
		
		this.plasmaBounds.vertices[0].x = position.x + this.plasmaOffset[0];
		this.plasmaBounds.vertices[0].y = position.y + this.plasmaOffset[1];
	}
	
	/**
	 * Change all bounds when setting the position
	 * 
	 * @param Vector2 position
	 */
	public void setPosition(float x, float y) {
		
		this.vertices[0].x = x;
		this.vertices[0].y = y;
		
		this.worldBounds.vertices[0].x = x + this.worldOffset[0];
		this.worldBounds.vertices[0].y = y + this.worldOffset[1];
		
		this.plasmaBounds.vertices[0].x = x + this.plasmaOffset[0];
		this.plasmaBounds.vertices[0].y = y + this.plasmaOffset[1];
	}
	
	/**
	 * Change all bounds when adding to position
	 * 
	 * @param float x
	 * @param float y
	 */
	public void addToPosition(float x, float y) {
		
		this.vertices[0].x += x;
		this.vertices[0].y += y;
		
		this.worldBounds.vertices[0].x += x;
		this.worldBounds.vertices[0].y += y;
		
		this.plasmaBounds.vertices[0].x += x;
		this.plasmaBounds.vertices[0].y += y;
	}
	
	/**
	 * Player requires to know the gameScreen
	 * 
	 * @param GameScreen gameScreen
	 */
	public void setGameScreen(GameScreen gameScreen) {
		
		this.gameScreen = gameScreen;
	}
	
	/**
	 * Load all defined animations
	 */
	public void loadAnimations() {
		
		this.animPresets[ANIM_STAND] = new AnimationPreset(0, new String[] {
				"player/" + this.model + "/stand.png"
		});
		
		this.animPresets[ANIM_RUN_1] = new AnimationPreset(0, new String[] {
			"player/" + this.model + "/jump_1_f6.png"
		});
		
		this.animPresets[ANIM_RUN_2] = new AnimationPreset(0, new String[] {
			"player/" + this.model + "/jump_2_f6.png"
		});
		
		this.animPresets[ANIM_JUMP_1] = new AnimationPreset(0.3f, new String[] {
			"player/" + this.model + "/jump_1_f1.png",
			"player/" + this.model + "/jump_1_f2.png",
			"player/" + this.model + "/jump_1_f3.png",
			"player/" + this.model + "/jump_1_f4.png",
			"player/" + this.model + "/jump_1_f5.png",
			"player/" + this.model + "/jump_1_f6.png"
		});
		
		this.animPresets[ANIM_JUMP_2] = new AnimationPreset(0.3f, new String[] {
			"player/" + this.model + "/jump_2_f1.png",
			"player/" + this.model + "/jump_2_f2.png",
			"player/" + this.model + "/jump_2_f3.png",
			"player/" + this.model + "/jump_2_f4.png",
			"player/" + this.model + "/jump_2_f5.png",
			"player/" + this.model + "/jump_2_f6.png"
		});
		
		this.animPresets[ANIM_ROCKET_SHOOT] = new AnimationPreset(0.2f, new String[] {
			"player/" + this.model + "/rocket_shoot_f2.png",
			"player/" + this.model + "/rocket_shoot_f3.png",
			"player/" + this.model + "/rocket_shoot_f2.png"
		});
		
		this.animPresets[ANIM_ROCKET_SHOOT_WALL] = new AnimationPreset(0.2f, new String[] {
			"player/" + this.model + "/rocket_shoot_wall.png"
		});
		
		this.animPresets[ANIM_ROCKET_STAND] = new AnimationPreset(0, new String[] {
				"player/" + this.model + "/rocket_stand.png"
		});
		
		this.animPresets[ANIM_ROCKET_RUN_1] = new AnimationPreset(0, new String[] {
			"player/" + this.model + "/rocket_jump_1_f6.png"
		});
		
		this.animPresets[ANIM_ROCKET_RUN_2] = new AnimationPreset(0, new String[] {
			"player/" + this.model + "/rocket_jump_2_f6.png"
		});
		
		this.animPresets[ANIM_ROCKET_JUMP_1] = new AnimationPreset(0.3f, new String[] {
			"player/" + this.model + "/rocket_jump_1_f1.png",
			"player/" + this.model + "/rocket_jump_1_f2.png",
			"player/" + this.model + "/rocket_jump_1_f3.png",
			"player/" + this.model + "/rocket_jump_1_f4.png",
			"player/" + this.model + "/rocket_jump_1_f5.png",
			"player/" + this.model + "/rocket_jump_1_f6.png"
		});
		
		this.animPresets[ANIM_ROCKET_JUMP_2] = new AnimationPreset(0.3f, new String[] {
			"player/" + this.model + "/rocket_jump_2_f1.png",
			"player/" + this.model + "/rocket_jump_2_f2.png",
			"player/" + this.model + "/rocket_jump_2_f3.png",
			"player/" + this.model + "/rocket_jump_2_f4.png",
			"player/" + this.model + "/rocket_jump_2_f5.png",
			"player/" + this.model + "/rocket_jump_2_f6.png"
		});
		
		this.animPresets[ANIM_ROCKET_WALLJUMP] = new AnimationPreset(0.2f, new String[] {
			"player/" + this.model + "/rocket_walljump_f1.png",
			"player/" + this.model + "/rocket_walljump_f2.png",
			"player/" + this.model + "/rocket_walljump_f3.png"
		});
		
		this.animPresets[ANIM_PLASMA_SHOOT] = new AnimationPreset(0, new String[] {
			"player/" + this.model + "/plasma_shoot.png"
		});
		
		this.animPresets[ANIM_PLASMA_STAND] = new AnimationPreset(0, new String[] {
				"player/" + this.model + "/plasma_stand.png"
		});
		
		this.animPresets[ANIM_PLASMA_RUN_1] = new AnimationPreset(0, new String[] {
			"player/" + this.model + "/plasma_jump_1_f6.png"
		});
		
		this.animPresets[ANIM_PLASMA_RUN_2] = new AnimationPreset(0, new String[] {
			"player/" + this.model + "/plasma_jump_2_f6.png"
		});
		
		this.animPresets[ANIM_PLASMA_JUMP_1] = new AnimationPreset(0.3f, new String[] {
			"player/" + this.model + "/plasma_jump_1_f1.png",
			"player/" + this.model + "/plasma_jump_1_f2.png",
			"player/" + this.model + "/plasma_jump_1_f3.png",
			"player/" + this.model + "/plasma_jump_1_f4.png",
			"player/" + this.model + "/plasma_jump_1_f5.png",
			"player/" + this.model + "/plasma_jump_1_f6.png"
		});
		
		this.animPresets[ANIM_PLASMA_JUMP_2] = new AnimationPreset(0.3f, new String[] {
			"player/" + this.model + "/plasma_jump_2_f1.png",
			"player/" + this.model + "/plasma_jump_2_f2.png",
			"player/" + this.model + "/plasma_jump_2_f3.png",
			"player/" + this.model + "/plasma_jump_2_f4.png",
			"player/" + this.model + "/plasma_jump_2_f5.png",
			"player/" + this.model + "/plasma_jump_2_f6.png"
		});
		
		this.animPresets[ANIM_PLASMA_WALLJUMP] = new AnimationPreset(0.2f, new String[] {
			"player/" + this.model + "/plasma_walljump_f1.png",
			"player/" + this.model + "/plasma_walljump_f2.png",
			"player/" + this.model + "/plasma_walljump_f3.png"
		});
		
		this.animPresets[ANIM_WALLJUMP] = new AnimationPreset(0.2f, new String[] {
			"player/" + this.model + "/walljump_f1.png",
			"player/" + this.model + "/walljump_f2.png",
			"player/" + this.model + "/walljump_f3.png"
		});
		
		this.animPresets[ANIM_BURN] = new AnimationPreset(0.3f, new String[] {
			"player/" + this.model + "/burn_f1.png",
			"player/" + this.model + "/burn_f2.png",
			"player/" + this.model + "/burn_f3.png",
			"player/" + this.model + "/burn_f4.png"
		});
		
		this.animPresets[ANIM_DROWN] = new AnimationPreset(0.3f, new String[] {
			"player/" + this.model + "/drown_f1.png",
			"player/" + this.model + "/drown_f2.png",
			"player/" + this.model + "/drown_f3.png",
			"player/" + this.model + "/drown_f4.png"
		});
		
		this.animPresets[ANIM_DRIFTSAND] = new AnimationPreset(0.3f, new String[] {
			"player/" + this.model + "/sand_f1.png",
			"player/" + this.model + "/sand_f2.png",
			"player/" + this.model + "/sand_f3.png",
			"player/" + this.model + "/sand_f4.png"
		});
		
		this.animPresets[ANIM_INVISIBLE] = new AnimationPreset(0, new String[] {
			"player/" + this.model + "/invisible.png"
		});
	
		this.setAnimations(this.animPresets);
	}
	
	/**
	 * Remove a tutorial message and continue the
	 * game if the proper event is beeing passed
	 *  
	 * @param String event
	 */
	public void updateTutorial(String event) {
		
		if (this.tutorialActive != null) {
			
			if (!event.equals("reset") && !event.equals(this.tutorialActive.event)) return;
			
			this.tutorialActive = null;
			this.gameScreen.resumeGame();
			
			gameScreen.game.runOnUiThread(new Runnable() {
				
				public void run() {
					
					Racesow.tutorial.setText("");
				}
			});
		}
	}
	
	/**
	 * Execute the jump action
	 * 
	 * @param float jumpPressedTime
	 */
	public void jump(float jumpPressedTime) {
		
		if (this.isDead) return;
		
		// only for an initial jump action (not if the player holds jump)
		if (jumpPressedTime == 0) {
		
			this.updateTutorial("jump");
		}
		
		// remember the distance to the ground when falling and pressing jump
		if (!this.distanceRemembered && this.velocity.y < 0) {
			
			GameObject ground = this.map.getGround(this.worldBounds);
			if (ground != null) {
				
				this.distanceOnJump = Math.max(0.32f,
						this.getPhysicalPosition().y - (ground.vertices[0].y + ground.getHeightAt(this.getPhysicalPosition().x)));
				this.distanceRemembered = true;
			}
		}
		
		// only jump when the player is on the floor
		if (this.onFloor) {
		
			this.onFloor = false;
			
			int jumpSound = this.rGen.nextInt(SOUND_JUMP2 - SOUND_JUMP1 + 1) + SOUND_JUMP1;
			this.frameSound = jumpSound;
			if (this.soundEnabled) {
				
				this.sounds[jumpSound].play(this.volume);
			}
			
			// reset to startspeed when no speed left
			if (this.virtualSpeed == 0) {
				
				this.virtualSpeed = this.startSpeed;
			}
			
			// give the player a speed boost according to
			// the distance when initially pressed jump
			if (this.distanceOnJump > 0 && this.velocity.x != 0) {
				
				float boost = (60000 / Math.max(this.virtualSpeed, this.startSpeed) / this.distanceOnJump);
				this.virtualSpeed += boost;
			}
			
			this.velocity.add(0, 20);
			this.distanceRemembered = false;
			this.distanceOnJump = -1;
			
			// choose the proper jump animation
			if (this.attachedItem != null) {
				
				switch (this.attachedItem.func) {
				
					case GameObject.ITEM_ROCKET:
						if (this.lastJumpAnim == ANIM_ROCKET_JUMP_1 && this.lastJumpAnim != ANIM_ROCKET_STAND) {
						
							this.activeAnimId = ANIM_ROCKET_JUMP_2;
							
						} else {
							
							this.activeAnimId = ANIM_ROCKET_JUMP_1;
						}
						break;
					
					case GameObject.ITEM_PLASMA:
						if (this.lastJumpAnim == ANIM_PLASMA_JUMP_1 && this.lastJumpAnim != ANIM_PLASMA_STAND) {
						
							this.activeAnimId = ANIM_PLASMA_JUMP_2;
							
						} else {
							
							this.activeAnimId = ANIM_PLASMA_JUMP_1;
						}
						
						break;
				}
				
			} else {
			
				if (this.lastJumpAnim == ANIM_JUMP_1 && this.lastJumpAnim != ANIM_STAND) {
				
					this.activeAnimId = ANIM_JUMP_2;
					
				} else {
					
					this.activeAnimId = ANIM_JUMP_1;
				}
			}
			
			if (this.lastJumpAnim == ANIM_STAND || this.lastJumpAnim == ANIM_ROCKET_STAND || this.lastJumpAnim == ANIM_PLASMA_STAND) {
				
				this.animTime = 0.15f;
			}
			
			this.lastJumpAnim = this.activeAnimId;
		
		// when in the air check for walls to perform a walljump
		} else {
			
			// allow walljump only in certain intervals
			if (jumpPressedTime == 0 && System.nanoTime() / 1000000000.0f > this.lastWallJumped + 1.5f) {
				
				List<GameObject> colliders = this.map.getPotentialWallColliders(this.worldBounds);
				int length = colliders.size();
				for (int i = 0; i < length; i++) {
				
					GameObject part = colliders.get(i);
					CollisionInfo info = this.worldBounds.intersect(part);
					if (info.collided) {
						
						int wjSound = this.rGen.nextInt(SOUND_WJ2 - SOUND_WJ1 + 1) + SOUND_WJ1;
						this.frameSound = wjSound;
						if (this.soundEnabled) {
						
							this.sounds[wjSound].play(this.volume);
						}
						
						// add some velocity after a walljump
						this.velocity.set(this.velocity.x + 5, 17);
						
						// remember the walljump time for the interval to work
						this.lastWallJumped = System.nanoTime() / 1000000000.0f;
						
						// choose the proper animation
						if (this.attachedItem != null) {
							
							switch (this.attachedItem.func) {
							
								case GameObject.ITEM_ROCKET:
									this.activeAnimId = ANIM_ROCKET_WALLJUMP;
									break;
								
								case GameObject.ITEM_PLASMA:
									this.activeAnimId = ANIM_PLASMA_WALLJUMP;
									break;
									
								default:
									this.activeAnimId = ANIM_WALLJUMP;
									break;
							}
							
						} else {
						
							this.activeAnimId = ANIM_WALLJUMP;
						}
						
						this.animTime = 0;
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Execute the shoot action
	 * 
	 * @param float shootPressedTime
	 */
	public void shoot(float shootPressedTime) {
		
		this.updateTutorial("shoot");
		
		// if there is no attached item or the
		// player is dead we can not shoot
		if (this.attachedItem == null || this.isDead) return;
		
		float currentTime = System.nanoTime() / 1000000000.0f;
		switch (this.attachedItem.func) {
		
			// when shooting with the rocketlauncher
			case GameObject.ITEM_ROCKET:
				boolean hitWall = false;
				if (currentTime >= this.lastShot + FIRERATE_ROCKET) {
					
					// prefer wall-rockets 
					List<GameObject> colliders = this.map.getPotentialWallColliders(this.worldBounds);
					final int length = colliders.size();
					for (int i = 0; i < length; i++) {
					
						GameObject part = colliders.get(i);
						CollisionInfo info = this.worldBounds.intersect(part);
						if (info.collided) {
					
							hitWall = true;
							final float impactX = this.getPhysicalPosition().x;
							final float impactY = this.getPhysicalPosition().y;
							
							// give the player some speed boost for wall-rockets
							this.velocity.set(this.velocity.x, this.velocity.y < 0 ? 30 : this.velocity.y + 20);
							this.virtualSpeed += 200;
							
							this.frameSound = SOUND_ROCKET;
							if (this.soundEnabled) {
							
								this.sounds[SOUND_ROCKET].play(this.volume * 1.5f);
							}
							
							this.animTime = 0;
							this.activeAnimId = ANIM_ROCKET_SHOOT_WALL;
							
							// show the rocket explosion
							TexturedBlock decal = this.rocketPool.newObject();
							decal.vertices[0].x = impactX;
							decal.vertices[0].y = impactY;
							map.addDecal(decal, rocketDecalTime);
							if (this.recordDemos) {
								this.frameDecalType = "r";
								this.frameDecalX = impactX;
								this.frameDecalY = impactY;
							}
							
							this.lastShot = currentTime;
							break;
						}
					}
					
					// if we didn't hit a wall then hit the ground
					if (!hitWall) {
						
						GameObject ground = map.getGround(this.worldBounds);
						if (ground != null) {
							
							float impactY = ground.vertices[0].y + ground.getHeightAt(this.getPhysicalPosition().x) - 4;
							float distance = Math.max(0.75f, this.getPhysicalPosition().y - impactY);
							
							// only allow boost from blocks without functionality (ie. no lava)
							if (ground.func == GameObject.FUNC_NONE) {
								
								this.addToPosition(0, 1);
								this.velocity.add(0, 100 / distance);
								this.onFloor = false;
							}
							
							this.frameSound = SOUND_ROCKET;
							if (this.soundEnabled) {
							
								this.sounds[SOUND_ROCKET].play(this.volume * 1.5f);
							}
							
							this.animTime = 0;
							this.activeAnimId = ANIM_ROCKET_SHOOT;
							
							// show the rocket explosion
							TexturedBlock decal = this.rocketPool.newObject();
							decal.vertices[0].x = this.getPhysicalPosition().x - 2;
							decal.vertices[0].y = impactY;
							map.addDecal(decal, plasmaDecalTime);
							
							if (this.recordDemos) {
								
								this.frameDecalType = "r";
								this.frameDecalX = this.getPhysicalPosition().x;
								this.frameDecalY = impactY;
							}
						}
						
						this.lastShot = currentTime;
					}
				}
				break;
				
			// when shooting with the plasma gun
			case GameObject.ITEM_PLASMA:
				if (currentTime >= this.lastShot + FIRERATE_PLASMA) {
					
					// plasma can only hit walls
					List<GameObject> colliders = this.map.getPotentialWallColliders(this.plasmaBounds);
					final int length = colliders.size();
					for (int i = 0; i < length; i++) {
					
						GameObject part = colliders.get(i);
						CollisionInfo info = this.plasmaBounds.intersect(part);
						if (info.collided) {

							final float impactX = this.getPhysicalPosition().x + 4;
							final float impactY = this.getPhysicalPosition().y + 3;
							
							// give the player some speed boost
							this.velocity.add(0, 2.5f);
							this.virtualSpeed += 15;
							
							this.frameSound = SOUND_PLASMA;
							if (this.soundEnabled) {
							
								this.sounds[SOUND_PLASMA].play(this.volume * 1.2f);
							}
							
							// let the wj anim complete
							if (this.activeAnimId != ANIM_PLASMA_WALLJUMP) {
								
								this.animTime = 0;
								this.activeAnimId = ANIM_PLASMA_SHOOT;
							}
							
							// show the plasma impact
							TexturedBlock decal = (TexturedBlock)this.plasmaPool.newObject();
							decal.vertices[0].x = impactX;
							decal.vertices[0].y = impactY;
							map.addDecal(decal, plasmaDecalTime);
							if (this.recordDemos) {
								
								this.frameDecalType = "p";
								this.frameDecalX = impactX;
								this.frameDecalY = impactY;
							}
							
							this.lastShot = currentTime;
							break;
						}
					}
					
					this.lastShot = currentTime;
				}
				break;
		}
	}
	
	public void animate(float deltaTime) {
		
		// if it's a real animation, not a static image
		if (this.animPresets[this.activeAnimId].duration != 0) {
		
			// increase the internal time of AnimatedBlock
			this.animTime += deltaTime;
			
			// when the animation has reached the end
			if (this.animTime > this.animPresets[this.activeAnimId].duration) {
				
				// reset animation to the beginning
				this.animTime = 0;
				
				// after death animation make the player invisible
				if (this.activeAnimId == ANIM_BURN ||
					this.activeAnimId == ANIM_DROWN ||
					this.activeAnimId == ANIM_DRIFTSAND) {
					
					this.activeAnimId = ANIM_INVISIBLE;
				
				// when the animation is over, choose
				// the proper default animation
				} else {
				
					if (this.attachedItem != null) {
						
						switch (this.attachedItem.func) {
						
							case GameObject.ITEM_ROCKET:
								if (this.lastJumpAnim == ANIM_ROCKET_JUMP_1 && this.lastJumpAnim != ANIM_ROCKET_STAND) {
									
									this.activeAnimId = ANIM_ROCKET_RUN_1;
									
								} else {
									
									this.activeAnimId = ANIM_ROCKET_RUN_2;
								}
								break;
							
							case GameObject.ITEM_PLASMA:
								if (this.lastJumpAnim == ANIM_PLASMA_JUMP_1 && this.lastJumpAnim != ANIM_PLASMA_STAND) {
									
									this.activeAnimId = ANIM_PLASMA_RUN_1;
								
								} else {
									
									this.activeAnimId = ANIM_PLASMA_RUN_2;
								}
								break;
						}
						
					} else {
					
						if (this.lastJumpAnim == ANIM_JUMP_1 && this.lastJumpAnim != ANIM_STAND) {
						
							this.activeAnimId = ANIM_RUN_1;
							
						} else {
							
							this.activeAnimId = ANIM_RUN_2;
						}
					}
				}
			}
		}
	}
	
	/**
	 * See if the player is still plasma-sliding,
	 * otherwise stop the slide-anomation
	 * 
	 * @param boolean pressingShoot
	 */
	public void handlePlasma(boolean pressingShoot) {
		
		if (this.activeAnimId == ANIM_PLASMA_SHOOT) {
			
			// if the player stopped pressing shoot or left a wall
			// return from the plasma shoot anim to the normal one
			boolean stopPlasmaAnim = false;
			if (pressingShoot) {
				
				boolean collided = false;
				List<GameObject> colliders = this.map.getPotentialWallColliders(this.plasmaBounds);
				final int length = colliders.size();
				for (int i = 0; i < length; i++) {
				
					if (this.plasmaBounds.intersect(colliders.get(i)).collided) {
						
						collided = true;
						break;
					}
				}
				
				if (!collided) {
					
					stopPlasmaAnim = true;
				}
				
			} else {
				
				stopPlasmaAnim = true;
			}
			
			if (stopPlasmaAnim) {
			
				this.activeAnimId = this.lastJumpAnim == ANIM_PLASMA_JUMP_1 ? ANIM_PLASMA_RUN_1 : ANIM_PLASMA_RUN_2;
			}
		}
	}
	
	/**
	 * Move the player in the map
	 * 
	 * @param Vector2 gravity
	 * @param float deltaTime
	 * @param boolean pressingJump
	 */
	public void move(Vector2 gravity, float deltaTime, boolean pressingJump, float jumpPressedTime, boolean pressingShoot) {
		
		 // workaround for initial loading
		if (++frames < 3) return;
		
		this.frameDecalType = "";
		this.frameDecalX = 0;
		this.frameDecalY = 0;
		this.frameSound = -1;
		
		this.map.handleAmbience(this.vertices[0].x);
		
		this.animate(deltaTime);
		
		if (this.isDead) return;
		
		// see if the player collides with a map-function
		boolean stop = false;
		List<GameObject> colliders = this.map.getPotentialFuncColliders(this.worldBounds);
		int length = colliders.size();
		for (int i = 0; i < length; i++) {
		
			GameObject part = colliders.get(i);
			CollisionInfo info = this.worldBounds.intersect(part);
			if (info.collided) {
			
				switch (part.func) {
				
					case GameObject.FUNC_START_TIMER:
						this.map.startTimer();
						break;
						
					case GameObject.FUNC_STOP_TIMER:
						this.finishRace();
						break;
						
					case GameObject.FUNC_TUTORIAL:
						this.showTutorialMessage(part);
						break;
				}
			}
			
			if (stop) break;
		}
		
		// see if the player picks up an item (plasmagun, rocketlauncher)
		length = this.map.items.length;
		for (int i = 0; i < length; i++) {
			
			if (this.map.pickedUpItems[i]) {
				
				continue;
			}
			
			GameObject item = this.map.items[i];
			float playerX = this.getPhysicalPosition().x;
			float playerY = this.getPhysicalPosition().y;
			float itemX = item.vertices[0].x;
			float itemY = item.vertices[0].y;
			if (playerX + this.worldBounds.width>= itemX && playerX <= itemX + item.width && playerY + this.worldBounds.height >= itemY) {
				
				String texture = "";
				switch (item.func) {
				
					case GameObject.ITEM_ROCKET:
						texture = "items/rocket.png";
						if (this.activeAnimId == ANIM_RUN_1 || this.activeAnimId == ANIM_PLASMA_RUN_1) {
						
							this.lastJumpAnim = ANIM_ROCKET_JUMP_1;
							this.activeAnimId = ANIM_ROCKET_RUN_1;
							
						} else {
							
							this.lastJumpAnim = ANIM_ROCKET_JUMP_2;
							this.activeAnimId = ANIM_ROCKET_RUN_2;
						}
						
						break;
					
					case GameObject.ITEM_PLASMA:
						texture = "items/plasma.png";
						if (this.activeAnimId == ANIM_RUN_1 || this.activeAnimId == ANIM_ROCKET_RUN_1) {
						
							this.lastJumpAnim = ANIM_PLASMA_JUMP_1;
							this.activeAnimId = ANIM_PLASMA_RUN_1;
							
						} else {
							
							this.lastJumpAnim = ANIM_PLASMA_JUMP_2;
							this.activeAnimId = ANIM_PLASMA_RUN_2;
						}
						
						break;
				}
				
				// show a weapon icon in the HUD
				TexturedBlock hudItem = new TexturedBlock(
					texture,
					item.func,
					-1,
					-1,
					0,
					0,
					new Vector2(-(this.camera.frustumWidth / 2) + 1, -(this.camera.frustumHeight / 2) + 1),
					new Vector2(-(this.camera.frustumWidth / 2) + 10, -(this.camera.frustumHeight / 2) + 10)
				);
				
				camera.addHud(hudItem);
				this.map.pickedUpItems[i] = true;
				if (this.attachedItem != null) {
					
					synchronized (this) {
						
						this.camera.removeHud(this.attachedItem);
						this.attachedItem.texture.dispose();
					}
				}
				
				this.lastShot = 0;
				this.attachedItem = hudItem;
				
				this.frameSound = SOUND_PICKUP;
				if (this.soundEnabled) {
				
					this.sounds[SOUND_PICKUP].play(this.volume);
				}
				break;
			}
		}
		
		// when player is in the air
		if (!this.onFloor) {
			
			// if you're jumping straight up... *1
			boolean straightUp = false;
			if (this.virtualSpeed == 0 && this.velocity.y > 0 ) {
				
				straightUp = true;
			}
			
			// apply gravity
			this.velocity.add(gravity.x * deltaTime, gravity.y * deltaTime);

			// *1 ... and fall down, apply some speed to
			// allow jumping on walls if you're in front of them
			if (straightUp && this.velocity.y <= 0) {
				
				this.virtualSpeed += 10;
			}
			
			// move the player
			this.addToPosition(this.velocity.x * deltaTime, this.velocity.y * deltaTime);
			
			// check for collision with the ground
			colliders = this.map.getPotentialGroundColliders(this.worldBounds);
			length = colliders.size();
			short collisionCount = 0;
			for (int i = 0; i < length; i++) {
				
				GameObject ground = colliders.get(i);
				CollisionInfo info = this.worldBounds.intersect(ground);
				
				// check for functional collisions like water or lava
				if (info.collided) {
					
					collisionCount++;
					
					switch (ground.func) {
					
						case GameObject.FUNC_LAVA:
							this.activeAnimId = ANIM_BURN;
							this.die();
							return;
							
						case GameObject.FUNC_WATER:
							this.activeAnimId = ANIM_DROWN;
							this.die();
							return;
							
						case GameObject.FUNC_DRIFTSAND:
							this.activeAnimId = ANIM_DRIFTSAND;
							this.die();
							return;
					}
					
					// ground
					if (info.type == Polygon.BOTTOM) {
						
						this.setPosition(this.vertices[0].x, this.vertices[0].y - info.distance);
						this.velocity.y = 0;
						
					} else if (info.type == Polygon.TOP) {
					
						this.setPosition(this.vertices[0].x, this.vertices[0].y + info.distance);
						this.velocity.set(this.velocity.x, 0);
						this.onFloor = true;
						
						if (this.map.raceFinished) {
							
							this.map.saveDemo();
						}
					
					// wall
					} else if (info.type == Polygon.LEFT) {
					
						this.setPosition(this.vertices[0].x - info.distance, this.vertices[0].y);
						this.velocity.x = 0;
						this.virtualSpeed = 0;
					
					// ramp up
					} else if (info.type == Polygon.RAMPUP) {
						
						this.addToPosition(0, info.distance);
						if (pressingJump && (jumpPressedTime > 0.5f || this.virtualSpeed >= 900)) {
							
							float m = (ground.vertices[2].y - ground.vertices[0].y) / (ground.vertices[2].x - ground.vertices[0].x);
							this.velocity.set(this.velocity.x, this.velocity.x * m);
							
						} else {
						
							this.velocity.set(this.velocity.x, 0);
							this.onFloor = true;
						}
						
						if (this.map.raceFinished) {
							
							this.map.saveDemo();
						}
						
					// ramp down
					} else if (info.type == Polygon.RAMPDOWN) {
						
						this.addToPosition(0, info.distance);
						float m = (ground.vertices[0].y - ground.vertices[2].y) / (ground.vertices[0].x - ground.vertices[2].x);
						this.velocity.set(this.velocity.x, 0);
						this.virtualSpeed += (-m + 1) * (-m + 1) * (-m + 1) * 128;
						this.onFloor = true;
						
						if (this.map.raceFinished) {
							
							this.map.saveDemo();
						}
					}
					
					if (collisionCount == 2) {
					
						break;
					}
				}
			}
		
		// when on the ground...
		} else {
		
			this.velocity.y = 0;
			// ... lose some speed
			if (this.virtualSpeed > 0) {
				
				this.virtualSpeed = Math.max(0, this.virtualSpeed - 10000 * deltaTime);
			}
		}
		
		if (this.virtualSpeed == 0) {
			
			if (this.attachedItem != null) {
			
				switch (this.attachedItem.func) {
				
					case GameObject.ITEM_ROCKET:
						this.activeAnimId = ANIM_ROCKET_STAND;
						break;
						
					case GameObject.ITEM_PLASMA:
						this.activeAnimId = ANIM_PLASMA_STAND;
						break;
						
					default:
						this.activeAnimId = ANIM_STAND;
						break;
				}
				
			} else {
					
				this.activeAnimId = ANIM_STAND;	
			}
			
			this.lastJumpAnim = this.activeAnimId;
		}
		
		// apply the velocity given by the virtual-speed
		this.velocity.set(this.virtualSpeed / 15, this.velocity.y);
	}
	
	/**
	 * Let the player die
	 */
	public void die() {
		
		this.animTime = 0;
		this.virtualSpeed = 0;
		this.frameSound = SOUND_DIE;
		if (this.soundEnabled) {
		
			this.sounds[SOUND_DIE].play(this.volume);
		}
		
		this.isDead = true;
		
		this.showRestartMessage();
	}
	
	/**
	 * Show a restart message
	 */
	public void showRestartMessage() {
		
		gameScreen.game.runOnUiThread(new Runnable() {
			
			public void run() {
				
				Racesow.centertext2.setText("Press back to restart");
				Racesow.centertext2.setTextColor(Color.RED);
			}
		});
	}
	
	/**
	 * Show "new record" message
	 */
	public void showRatingMessage(final float time) {
		
		gameScreen.game.runOnUiThread(new Runnable() {
			
			public void run() {
				
				String message;
				int color;
				if (time <= map.ratingExcellent) {
					
					message = "Excellent race!";
					color = Color.GREEN;
					
				} else if (time <= map.ratingVeryGood) {
					
					message = "Very good race!";
					color = Color.GREEN;
					
				} else if (time <= map.ratingGood) {
					
					message = "Good race!";
					color = Color.YELLOW;
					
				} else {
					
					message = "Not bad!";
					color = Color.RED;
				}
				
				Racesow.centertext2.setText(message);
				Racesow.centertext2.setTextColor(color);
			}
		});
	}

	/**
	 * Show a tutorial message
	 * 
	 * @param GameObject tutorial
	 */
	public void showTutorialMessage(final GameObject tutorial) {
		
		if (tutorial.finished || this.tutorialActive != null) return;
		
		this.tutorialActive = tutorial;
		tutorial.finished = true;
		this.gameScreen.pauseGame();
		
		gameScreen.game.runOnUiThread(new Runnable() {
			
			public void run() {
				
				Racesow.tutorial.setText(tutorial.text);
			}
		});
	}
	
	/**
	 * Finish the race. Called when stopTimer is touched.
	 */
	public void finishRace() {
		
		if (!this.map.inRace()) return;
		
		this.map.stopTimer();
		
		this.lastFinishTime = this.map.getCurrentTime();
		final int showDialogDelay = 2000;
		
		this.showRatingMessage(Player.this.lastFinishTime);
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
				gameScreen.game.runOnUiThread(new Runnable() {
					
					public void run() {
						
						Racesow.centertext2.setText("");
						savedLocally = false;
						editName();
					}
				});
			}
		}, showDialogDelay);
	}
	
	/**
	 * Show a dialog to edit the nickname
	 */
	private void editName() {
		
		final AlertDialog alert = new AlertDialog.Builder(Player.this.gameScreen.game).create();
		final EditText input = new EditText(Player.this.gameScreen.game);
		input.setText(Player.this.name);
		input.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				if (s.toString().trim().length() == 0) {
				
					alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
					
				} else {
					
					alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
				}
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		alert.setCancelable(false);
		alert.setMessage("Your time is " + String.format(Locale.US, "%.4f", Player.this.lastFinishTime) + "\nEnter a nickname for the highscores.");
        alert.setView(input);
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				Player.this.map.inFinishSequence = false;
				
				if (!savedLocally) {
					// save the time to the local scores
					InternalScoresThread t = new InternalScoresThread(
						Player.this.map.fileName,
						input.getText().toString().trim(),
						Player.this.lastFinishTime,
						new Handler() {
					    	
					    	@Override
					        public void handleMessage(final Message msg) {
					    		
					    		savedLocally = true;
					    		lastRaceID = msg.getData().getLong("id");
					    		
					    		gameScreen.game.runOnUiThread(new Runnable() {
									
									public void run() {
										
							    		if (msg.getData().getBoolean("record")) {
							    			
							    			Racesow.centertext1.setText("New personal record!");
											Racesow.centertext1.setTextColor(Color.GREEN);
							    		
							    		} else {
							    			
							    			Racesow.centertext1.setText("Race finished!");
											Racesow.centertext1.setTextColor(Color.YELLOW);
							    		}
							    		
										Racesow.centertext2.setText("Your time: " + String.format(Locale.US, "%.4f", map.getCurrentTime()));
										Racesow.centertext2.setTextColor(Color.BLUE);
										
										Racesow.centertext3.setText("Press back to restart");
										Racesow.centertext3.setTextColor(Color.RED);	
									}
								});
					    	}
					});
					t.start();
				}
				
				Player.this.name = input.getText().toString().trim();
				Player.this.submitScore();
			}
		});
        
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!Player.this.name.equals(""));
	}
	
	/**
	 * Show the login window
	 */
	private void showLogin(String message) {
		
		final EditText pass = new EditText(this.gameScreen.game);
		pass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
		AlertDialog login = new AlertDialog.Builder(this.gameScreen.game)
			.setCancelable(true)
			.setView(pass)
	        .setMessage(message)
	        .setPositiveButton("Login", new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {

					String password = pass.getText().toString();
					
					String url = "http://racesow2d.warsow-race.net/accounts.php";
					List<NameValuePair> values = new ArrayList<NameValuePair>();
					values.add(new BasicNameValuePair("action", "auth"));
					values.add(new BasicNameValuePair("name", Player.this.name));
					values.add(new BasicNameValuePair("pass", password));
					final HttpLoaderTask task = new HttpLoaderTask(Player.this);
					task.execute(url, values);
					
					pd = new ProgressDialog(Player.this.gameScreen.game);
					pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					pd.setMessage("Logging in...");
					pd.setCancelable(true);
					pd.setOnCancelListener(new OnCancelListener() {
						
						public void onCancel(DialogInterface dialog) {

							task.cancel(true);
						}
					});
					pd.show();
				}
			})
			.setNegativeButton("Change name", new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					
					editName();
				}
			})
			.create();
		
		login.show();
	        
	}
	
	/**
	 * Submit the last time to the API
	 */
	public void submitScore() {
		
		this.submittingScore = true;
		List<NameValuePair> values = new ArrayList<NameValuePair>();
		values.add(new BasicNameValuePair("map", this.map.fileName));
		values.add(new BasicNameValuePair("player", this.name));
		values.add(new BasicNameValuePair("session", Database.getInstance().getSession(Player.this.name)));
		values.add(new BasicNameValuePair("time", String.valueOf(new Float(this.lastFinishTime))));
		values.add(new BasicNameValuePair("key", "alpha-key"));
        
        final HttpLoaderTask task = new HttpLoaderTask(this);
		task.execute("http://racesow2d.warsow-race.net/submit.php", values);
		
		pd = new ProgressDialog(Player.this.gameScreen.game);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("Submitting time...");
		pd.setCancelable(true);
		pd.setOnCancelListener(new OnCancelListener() {
			
			public void onCancel(DialogInterface dialog) {

				task.cancel(true);
			}
		});
		pd.show();
	}
	
	/**
	 * Called by HttpLoaderTask
	 * 
	 * @param InputStream xmlStream
	 */
	public void httpCallback(InputStream xmlStream) {
		
		this.submittingScore = false;
		pd.dismiss();		
		if (xmlStream == null) {
			
			return;
		}
		try {
			
			Database db = Database.getInstance();
			
			XMLParser parser = new XMLParser();
	        parser.read(xmlStream);
			
			// in case of an XML error element sent by the API
			NodeList errors = parser.doc.getElementsByTagName("error");
			if (errors.getLength() > 0) {
				
				int code = -1;
				NodeList codes = parser.doc.getElementsByTagName("code");
				if (codes.getLength() > 0) {
					
					try {
						
						code = Integer.parseInt(parser.getNodeValue((Element)codes.item(0)));
						
					} catch (NumberFormatException e) {
						
						code = -1;
					}
				}
				
				if (code == 2) { // SESSION_INVALID
					
					String message;
					String session = db.getSession(this.name);
					if (session.equals("")) {
						
						message = "This name is registered. Please login or change your name.";
						
					} else {
						
						message = "Your session has expired. Please login.";
					}
					
					showLogin(message);
					return;
					
				} else {
				
					throw new Exception("Server error: " + parser.getNodeValue((Element)errors.item(0)));
				}
			}
			
			// parse the update
			NodeList submissions = parser.doc.getElementsByTagName("submission");
			if (submissions.getLength() == 1) {
				
				Element submissionRoot = (Element)submissions.item(0);
				String session = parser.getValue(submissionRoot, "session");
				if (!session.equals("")) {
					
					db.setSession(this.name, session);
				}
				
				Database.getInstance().flagRaceSubmitted(lastRaceID, this.name);
				
				int points;
				try {
					
					points = Integer.parseInt(parser.getValue(submissionRoot, "points"));
					
				} catch (NumberFormatException e) {
					
					points = 0;
				}
				
				if (points > 0) {
					
					final int earnedPoints = points;
					Player.this.gameScreen.game.runOnUiThread(new Runnable() {
						
						public void run() {
							
							new AlertDialog.Builder(Player.this.gameScreen.game)
								.setCancelable(true)
								.setMessage("You earned " + earnedPoints + " points.")
								.setNeutralButton("OK", null)
								.show();
						}
					});
				}
				
				return;
			}
			
			// login response
			NodeList auths = parser.doc.getElementsByTagName("auth");
			if (auths.getLength() == 1) {
				
				try {

					Element auth = (Element)auths.item(0);
					int result = Integer.parseInt(parser.getValue(auth, "result"));
					if (result == 1) {
						
						db.setSession(this.name, parser.getValue(auth, "session"));
						this.submitScore();
						
					} else {
						
						showLogin("Wrong password. Please try again.");
					}
					
				} catch (NumberFormatException e) {}
				return;
			}
			
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * Reset the player to the initial position
	 * and also reset some variables and messages.
	 * 
	 * @param float x
	 * @param float y
	 */
	public void reset(float x, float y) {
				
		this.updateTutorial("reset");
		
		this.gameScreen.frames = this.gameScreen.hudUpdateInterval;
		this.gameScreen.frameTime = 0;
		
		this.isDead = false;
		this.onFloor = false;
		this.activeAnimId = ANIM_RUN_1;
		this.virtualSpeed = 0;
		this.velocity.set(0, 0);
		this.setPosition(x, y);
		
		if (this.attachedItem != null) {
			
			synchronized (this) {
				
				this.camera.removeHud(this.attachedItem);
				this.attachedItem.texture.dispose();
				this.attachedItem = null;
			}
		}
		
		gameScreen.game.runOnUiThread(new Runnable() {
			
			public void run() {
				
				Racesow.centertext1.setText("");
				Racesow.centertext2.setText("");
				Racesow.centertext3.setText("");
			}
		});
	}
	
	/**
	 * Reload all textures for the player
	 */
	public void reloadTexture() {
		
		super.reloadTexture();
		
		int length = this.plasmaPool.pool.size();
		for (int i = 0; i < length; i++) {
			
			this.plasmaPool.pool.get(i).reloadTexture();
		}
		
		length = this.rocketPool.pool.size();
		for (int i = 0; i < length; i++) {
			
			this.rocketPool.pool.get(i).reloadTexture();
		}
	}
	
	/**
	 * Get rid of loaded resources
	 */
	public void dispose() {
		
		super.dispose();
		
		int length = this.rocketPool.pool.size();
		for (int i = 0; i < length; i++) {
			
			this.rocketPool.pool.get(i).dispose();
		}
		
		length = this.plasmaPool.pool.size();
		for (int i = 0; i < length; i++) {
		
			this.plasmaPool.pool.get(i).dispose();
		}
		
		length = this.sounds.length;
		for (int i = 0; i < length; i++) {
			
			this.sounds[i].dispose();
		}
	}
	
	/**
	 * Draw the Player with a motionBlur effect
	 * 
	 * @param float blur
	 */
	public void draw(float blur) {
		
		super.draw();
		
		if (this.blurEnabled && blur > 0) {
			
			GLES10.glPushMatrix();
			GLES10.glTranslatef(this.vertices[0].x - blur, this.vertices[0].y, 0);
			this.anims[this.activeAnimId].getKeyFrame(this.animTime).bind();
			GLES10.glColor4f(1, 1, 1, 0.2f);
			this.glVertices.bind();
			this.glVertices.draw(GLES10.GL_TRIANGLES, 0, 6);
			this.glVertices.unbind();
			GLES10.glColor4f(1, 1, 1, 1);
			GLES10.glPopMatrix();
		}
	}
}

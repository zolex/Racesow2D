package org.racenet.racesow;

import java.util.List;
import java.util.Random;

import org.racenet.framework.AndroidAudio;
import org.racenet.framework.AndroidSound;
import org.racenet.framework.AnimatedBlock;
import org.racenet.framework.GLGame;
import org.racenet.framework.GameObject;
import org.racenet.framework.TexturedShape;
import org.racenet.framework.TexturedTriangle;
import org.racenet.framework.Vector2;

import android.util.Log;

class Player extends AnimatedBlock {
	
	public final Vector2 velocity = new Vector2();
	public final Vector2 accel = new Vector2();
	
	public static final int ANIM_NONE = 0;
	public static final int ANIM_JUMP = 1;
	public static final int ANIM_WALLJUMP = 2;
	public static final int ANIM_BURN = 3;
	public static final int ANIM_INVISIBLE = 4;
	
	public static final int SOUND_JUMP1 = 0;
	public static final int SOUND_JUMP2 = 1;
	public static final int SOUND_WJ1 = 2;
	public static final int SOUND_WJ2 = 3;
	public static final int SOUND_DIE = 4;
	
	private boolean onFloor = false;
	private float lastWallJumped = 0;
	private float distanceOnJump = -1;
	private boolean distanceRemembered = false;
	public float virtualSpeed = 0;
	private float startSpeed = 450;
	private boolean enableAnimation = false;
	private boolean isDead = false;
	private float animDuration = 0;
	private AndroidSound sounds[] = new AndroidSound[5];
	private Random rGen;
	private float volume = 0.1f;
	private String model = "male";
	
	private int frames = 0;
	
	public Player(GLGame game, float x, float y) {
		
		super(game, new Vector2(x,y), new Vector2(x + 3.4f, y), new Vector2(x + 3.4f, y + 6.5f), new Vector2(x, y + 6.5f));
		
		this.rGen = new Random();
		
		AndroidAudio audio = (AndroidAudio)game.getAudio();
		this.sounds[SOUND_JUMP1] = (AndroidSound)audio.newSound("sounds/player/" + this.model + "/jump_1.ogg");
		this.sounds[SOUND_JUMP2] = (AndroidSound)audio.newSound("sounds/player/" + this.model + "/jump_2.ogg");
		this.sounds[SOUND_WJ1] = (AndroidSound)audio.newSound("sounds/player/" + this.model + "/wj_1.ogg");
		this.sounds[SOUND_WJ2] = (AndroidSound)audio.newSound("sounds/player/" + this.model + "/wj_2.ogg");
		this.sounds[SOUND_DIE] = (AndroidSound)audio.newSound("sounds/player/" + this.model + "/death.ogg");
		
		this.loadAnimations();
		this.setupVertices();
		
	}
	
	public void loadAnimations() {
		
		String[][] animations = new String[5][];
		
		animations[ANIM_NONE] = new String[] {
			"player/" + this.model + "/default.png"
		};
		
		animations[ANIM_JUMP] = new String[] {
			"player/" + this.model + "/jump_f1.png",
			"player/" + this.model + "/jump_f2.png",
			"player/" + this.model + "/jump_f1.png"
		};
		
		animations[ANIM_WALLJUMP] = new String[] {
			"player/" + this.model + "/walljump_f1.png",
			"player/" + this.model + "/walljump_f2.png",
			"player/" + this.model + "/walljump_f1.png"
		};
		
		animations[ANIM_BURN] = new String[] {
			"player/" + this.model + "/burn_f1.png",
			"player/" + this.model + "/burn_f2.png",
			"player/" + this.model + "/burn_f3.png",
			"player/" + this.model + "/burn_f4.png"
		};
		
		animations[ANIM_INVISIBLE] = new String[] {
			"player/" + this.model + "/invisible.png"
		};
	
		this.setAnimations(animations);
	}
	
	public void jump(Map map, float eventTime) {
		
		if (this.isDead) return;
		
		if (!this.distanceRemembered && this.velocity.y < 0) {
			
			TexturedShape ground = map.getGround(this);
			if (ground != null) {
				
				this.distanceOnJump = Math.max(0.32f,
						this.getPosition().y - (ground.getPosition().y + ground.getHeightAt(this.getPosition().x)));
				this.distanceRemembered = true;
			}
		}
		
		if (this.onFloor) {
		
			this.onFloor = false;
			
			int jumpSound = this.rGen.nextInt(SOUND_JUMP2 - SOUND_JUMP1 + 1) + SOUND_JUMP1;
			this.sounds[jumpSound].play(this.volume);
			
			if (this.virtualSpeed == 0) {
				
				this.virtualSpeed = this.startSpeed;
			}
			
			if (this.distanceOnJump > 0) {
				
				float boost = (30000 / (this.virtualSpeed / 2) / this.distanceOnJump);
				this.virtualSpeed += boost;
			}
			
			this.velocity.add(0, 20);
			this.distanceRemembered = false;
			this.distanceOnJump = -1;
			
			this.activeAnimId = Player.ANIM_JUMP;
			this.enableAnimation = true;
			this.animDuration = 0.3f;
		
		} else {
			
			if (eventTime == 0 && System.nanoTime() / 1000000000.0f > this.lastWallJumped + 1.5f) {
				
				List<GameObject> colliders = map.getPotentialWallColliders(this);
				int length = colliders.size();
				for (int i = 0; i < length; i++) {
				
					GameObject part = colliders.get(i);
					CollisionInfo info = this.intersect(part);
					if (info.collided) {
						
						int wjSound = this.rGen.nextInt(SOUND_WJ2 - SOUND_WJ1 + 1) + SOUND_WJ1;

						this.sounds[wjSound].play(this.volume);
						
						this.velocity.set(this.velocity.x + 5, 17);
						this.lastWallJumped = System.nanoTime() / 1000000000.0f;
						this.activeAnimId = Player.ANIM_WALLJUMP;
						this.enableAnimation = true;
						this.animDuration = 0.3f;
					}
				}
			}
		}
	}
	
	public void move(Vector2 gravity, Map map, float deltaTime, boolean pressingJump) {
		
		if (++frames < 3) return; // workaround
		
		if (this.enableAnimation) {
			
			this.animTime += deltaTime;
			if (this.animTime > this.animDuration) {
				
				this.enableAnimation = false;
				this.animTime = 0;
				
				if (this.activeAnimId == Player.ANIM_BURN) {
					
					this.activeAnimId = Player.ANIM_INVISIBLE;
				
				} else {
				
					this.activeAnimId = Player.ANIM_NONE;
				}
			}
		}
		
		if (this.isDead) return;
		
		List<GameObject> colliders = map.getPotentialFuncColliders(this);
		int length = colliders.size();
		for (int i = 0; i < length; i++) {
		
			GameObject part = colliders.get(i);
			CollisionInfo info = this.intersect(part);
			if (info.collided) {
			
				switch (part.func) {
				
					case GameObject.FUNC_START_TIMER:
						map.startTimer();
						break;
						
					case GameObject.FUNC_STOP_TIMER:
						map.stopTimer();
						break;
				}
			}
		}
		
		if (!this.onFloor) {
			
			/*
			boolean straightUp = false;
			if (this.velocity.x == 0 && this.velocity.y > 0) {
				
				straightUp = true;
			}
			*/
			
			this.velocity.add(gravity.x * deltaTime, gravity.y * deltaTime);
			
			/*
			if (straightUp && this.velocity.y <= 0) {
				
				straightUp = false;
				this.velocity.set(10, 1);
			}
			*/
			
			this.addToPosition(this.velocity.x * deltaTime, this.velocity.y * deltaTime);
			
			colliders = map.getPotentialGroundColliders(this);
			length = colliders.size();
			for (int i = 0; i < length; i++) {
				
				GameObject ground = colliders.get(i);
				CollisionInfo info = this.intersect(ground);
				if (info.collided) {

					switch (ground.func) {
					
						case GameObject.FUNC_LAVA:
							this.activeAnimId = Player.ANIM_BURN;
							this.enableAnimation = true;
							this.animDuration = 0.4f;						
							this.die();
							return;
					}

					// ground, ramp down
					if (info.direction.angle() == 90) {
					
						this.setPosition(new Vector2(this.getPosition().x, this.getPosition().y + 0.25f));
						this.velocity.set(this.velocity.x, 0);
						this.onFloor = true;
					
					// ramp up
					} else if (info.direction.angle() == 0 || info.direction.angle() == 180) {
						
						if (pressingJump && this.virtualSpeed >= 900) {
							
							float m = (ground.vertices[2].y - ground.vertices[0].y) / (ground.vertices[2].x - ground.vertices[0].x);
							this.velocity.set(this.velocity.x, this.velocity.x * m);
							
						} else {
						
							this.velocity.set(this.velocity.x, 0);
							this.onFloor = true;
						}
						
					// front wall (and sometimes end of a ramp down)
					} else if (info.direction.angle() == 270) {
						
						// fix for getting stuck at end of ramp down
						if (ground.getClass().getName().endsWith("Triangle")) {
							
							this.setPosition(new Vector2(this.getPosition().x, this.getPosition().y + 0.25f));
							
						// really a front wall
						} else {
						
							float resetX = this.getPosition().x - info.distance / 2000;
							this.setPosition(new Vector2(resetX, this.getPosition().y));
							this.virtualSpeed = 0;
						}
					}

					
					
					break;
				}
			}
			
		} else {
			
			if (this.virtualSpeed > 0) {
				
				this.virtualSpeed = Math.max(0, this.virtualSpeed - 10000 * deltaTime);
			}
		}
		
		this.velocity.set(this.virtualSpeed / 23, this.velocity.y);
	}
	
	public void die() {
		
		this.sounds[SOUND_DIE].play(this.volume);
		this.isDead = true;
	}
	
	public void reset(float x, float y) {
		
		this.isDead = false;
		this.activeAnimId = ANIM_NONE;
		this.virtualSpeed = 0;
		this.setPosition(new Vector2(x, y));
	}
}

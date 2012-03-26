package org.racenet.racesow;

import java.util.List;

import org.racenet.framework.AnimatedMesh;
import org.racenet.framework.CollisionDetecctor;
import org.racenet.framework.GLGame;
import org.racenet.framework.GameObject;
import org.racenet.framework.Mesh;
import org.racenet.framework.Vector2;

class Player extends AnimatedMesh {
	
	public final Vector2 velocity = new Vector2();
	public final Vector2 accel = new Vector2();
	
	public static final int ANIM_NONE = 0;
	public static final int ANIM_JUMP = 1;
	public static final int ANIM_WALLJUMP = 2;
	public static final int ANIM_BURN = 3;
	public static final int ANIM_INVISIBLE = 4;
	
	private boolean onFloor = false;
	private float lastWallJumped = 0;
	private float distanceOnJump = -1;
	private boolean distanceRemembered = false;
	public float virtualSpeed = 0;
	private float startSpeed = 450;
	private boolean enableAnimation = false;
	private boolean isDead = false;
	private float animDuration = 0;
	
	public Player(GLGame game, float x, float y, float width, float height) {
		
		super(game, x, y, width, height);
		
		this.loadAnimations();
		this.setupVertices();
		
	}
	
	public void loadAnimations() {
		
		String[][] animations = new String[5][];
		
		animations[ANIM_NONE] = new String[] {
				"player.png"
			};
		
		animations[ANIM_JUMP] = new String[] {
				"player_jump_f1.png",
				"player_jump_f2.png",
				"player_jump_f1.png"
			};
		
		animations[ANIM_WALLJUMP] = new String[] {
				"player_walljump_f1.png",
				"player_walljump_f2.png",
				"player_walljump_f1.png"
		};
		
		animations[ANIM_BURN] = new String[] {
			"player_burn_f1.png",
			"player_burn_f2.png",
			"player_burn_f3.png",
			"player_burn_f4.png"
		};
		
		animations[ANIM_INVISIBLE] = new String[] {
			"transparent.png"
		};
	
		this.setAnimations(animations);
	}
	
	public void jump(Map map, float eventTime) {
		
		if (this.isDead) return;
		
		if (!this.distanceRemembered && this.velocity.y < 0) {
			
			Mesh ground = map.getGround(this);
			if (ground != null) {
				
				this.distanceOnJump = Math.max(0.1f, this.position.y - (ground.position.y + ground.bounds.height));
				this.distanceRemembered = true;
			}
		}
		
		if (this.onFloor) {
		
			if (this.virtualSpeed == 0) {
				
				this.virtualSpeed = this.startSpeed;
			}
			
			if (this.distanceOnJump > 0) {
				
				float boost = (5000 / (this.virtualSpeed / 2) / this.distanceOnJump);
				this.virtualSpeed += boost;
			}
			
			this.velocity.add(0, 20);
			this.onFloor = false;
			this.distanceRemembered = false;
			this.distanceOnJump = -1;
			
			this.activeAnimId = Player.ANIM_JUMP;
			this.enableAnimation = true;
			this.animDuration = 0.3f;
		
		} else {
			
			if (eventTime == 0 && System.nanoTime() / 1000000000.0f > this.lastWallJumped + 2) {
				
				List<GameObject> colliders = map.getPotentialBackColliders(this);
				int length = colliders.size();
				for (int i = 0; i < length; i++) {
				
					Mesh part = map.getBack(i);
					if (0 != CollisionDetecctor.rectangleCollision(part.bounds, this.bounds)) {
						
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
	
	public void move(Vector2 gravity, Map map, float deltaTime) {
		
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
			
			this.position.add(this.velocity.x * deltaTime, this.velocity.y * deltaTime);
			this.bounds.lowerLeft.set(this.position);
			
			List<GameObject> colliders = map.getPotentialFrontColliders(this);
			int length = colliders.size();
			for (int i = 0; i < length; i++) {
			
				GameObject part = colliders.get(i);
				int collision = CollisionDetecctor.rectangleCollision(part.bounds, this.bounds);
				if (collision != 0) {
				
					switch (((Mesh)part).func) {
					
						case Mesh.FUNC_LAVA:
							this.activeAnimId = Player.ANIM_BURN;
							this.enableAnimation = true;
							this.animDuration = 0.4f;							
							this.die();
							return;
					}
				}
				
				switch (collision) {
				
					case CollisionDetecctor.FROM_LEFT:
						this.position.set(this.position.x - this.virtualSpeed * this.virtualSpeed / 5000000, this.position.y);
						this.bounds.lowerLeft.set(this.position);
						this.virtualSpeed = 0;
						break;
				
					case CollisionDetecctor.FROM_TOP:
						this.velocity.set(this.velocity.x, 0);
						this.onFloor = true;
						break;
				}
			}
			
		} else {
			
			if (this.virtualSpeed > 0) {
				
				this.virtualSpeed = Math.max(0, this.virtualSpeed - 10000 * deltaTime);
			}
		}
		
		this.velocity.set(this.virtualSpeed / 30, this.velocity.y);
	}
	
	public void die() {
		
		this.isDead = true;
	}
}

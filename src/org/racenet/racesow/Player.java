package org.racenet.racesow;

import org.racenet.framework.AnimatedMesh;
import org.racenet.framework.CollisionDetecctor;
import org.racenet.framework.GLGame;
import org.racenet.framework.Mesh;
import org.racenet.framework.Vector2;

class Player extends AnimatedMesh {
	
	public final Vector2 velocity = new Vector2();
	public final Vector2 accel = new Vector2();
	
	private boolean onFloor = false;
	private boolean onWall = false;
	private float distanceOnJump = -1;
	private boolean distanceRemembered = false;
	public float virtualSpeed = 0;
	private float startSpeed = 450;
	private boolean jumpAnimation = false;
	
	public Player(GLGame game, float x, float y, float width, float height, String ... frames) {
		
		super(game, x, y, width, height, 0.1f, frames);
	}
	
	public void jump(Map map) {
		
		if (!this.distanceRemembered && this.velocity.y < 0) {
			
			Mesh ground = map.getGround(this.position);
			this.distanceOnJump = Math.max(0.1f, this.position.y - (ground.position.y + ground.bounds.height));
			this.distanceRemembered = true;
		}
		
		if (this.onFloor) {
		
			if (this.virtualSpeed == 0) {
				
				this.virtualSpeed = this.startSpeed;
			}
			
			if (this.distanceOnJump > 0) {
				
				float boost = (1000 / (this.virtualSpeed / 2) / (this.distanceOnJump * this.distanceOnJump));
				this.virtualSpeed += boost;
			}
			
			this.velocity.set(0, 20);
			this.onFloor = false;
			this.distanceRemembered = false;
			this.distanceOnJump = -1;
			
			this.jumpAnimation = true;
			this.animTime = 0.09f;
		}
	}
	
	public void move(Vector2 gravity, Map map, float deltaTime) {
		
		if (this.jumpAnimation) {
			
			this.animTime += deltaTime;
			if (this.animTime > 0.2f) {
				
				this.jumpAnimation = false;
				this.animTime = 0;
			}
		}
		
		if (!this.onFloor) {
			
			this.velocity.add(gravity.x * deltaTime, gravity.y * deltaTime);			
			this.position.add(this.velocity.x * deltaTime, this.velocity.y * deltaTime);
			this.bounds.lowerLeft.set(this.position);
			
			int length = map.numMeshes();
			for (int i = 0; i < length; i++) {
			
				Mesh part = map.getMesh(i);
				switch (CollisionDetecctor.rectangleCollision(part.bounds, this.bounds)) {
				
					case CollisionDetecctor.FROM_LEFT:
						this.position.set(this.position.x - 0.05f, this.position.y);
						this.bounds.lowerLeft.set(this.position);
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
}

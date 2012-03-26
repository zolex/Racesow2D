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
	private float lastWallJumped = 0;
	private float distanceOnJump = -1;
	private boolean distanceRemembered = false;
	public float virtualSpeed = 0;
	private float startSpeed = 450;
	private boolean jumpAnimation = false;
	
	public Player(GLGame game, float x, float y, float width, float height, String ... frames) {
		
		super(game, x, y, width, height, 0.1f, frames);
	}
	
	public void jump(Map map, float eventTime) {
		
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
			
			this.velocity.add(0, 20);
			this.onFloor = false;
			this.distanceRemembered = false;
			this.distanceOnJump = -1;
			
			this.jumpAnimation = true;
			this.animTime = 0.09f;
		
		} else {
			
			if (eventTime == 0 && System.nanoTime() / 1000000000.0f > this.lastWallJumped + 2) {
				
				int length = map.numBack();
				for (int i = 0; i < length; i++) {
				
					Mesh part = map.getBack(i);
					if (0 != CollisionDetecctor.rectangleCollision(part.bounds, this.bounds)) {
						
						this.velocity.set(this.velocity.x + 5, 17);
						this.lastWallJumped = System.nanoTime() / 1000000000.0f;
						this.jumpAnimation = true;
						this.animTime = 0.09f;
					}
				}
			}
		}
	}
	
	public void move(Vector2 gravity, Map map, float deltaTime) {
		
		if (this.jumpAnimation) {
			
			this.animTime += deltaTime;
			if (this.animTime > 0.4f) {
				
				this.jumpAnimation = false;
				this.animTime = 0;
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
			
			this.position.add(this.velocity.x * deltaTime, this.velocity.y * deltaTime);
			this.bounds.lowerLeft.set(this.position);
			
			int length = map.numFront();
			for (int i = 0; i < length; i++) {
			
				Mesh part = map.getFront(i);
				switch (CollisionDetecctor.rectangleCollision(part.bounds, this.bounds)) {
				
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
}

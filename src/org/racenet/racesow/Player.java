package org.racenet.racesow;


import org.racenet.framework.CollisionDetecctor;
import org.racenet.framework.GLGame;
import org.racenet.framework.GameObject;
import org.racenet.framework.Mesh;
import org.racenet.framework.Vector2;

class Player extends Mesh {
	
	public final Vector2 velocity = new Vector2();
	public final Vector2 accel = new Vector2();
	
	private boolean onFloor = false;
	private float distanceOnJump = -1;
	private boolean distanceRemembered = false;
	public float virtualSpeed = 0;
	private float startSpeed = 450;
	
	public Player(GLGame game, float x, float y, float width, float height, String texture) {
		
		super(game, x, y, width, height, texture);
	}
	
	public void jump(GameObject world) {
		
		if (!this.distanceRemembered && this.velocity.y < 0) {
			
			this.distanceOnJump = Math.max(0.1f, this.position.y - (world.position.y + world.bounds.height));
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
		}
	}
	
	public void applyGravity(Vector2 gravity, GameObject world, float deltaTime) {
		
		if (!this.onFloor) {
			
			this.velocity.add(gravity.x * deltaTime, gravity.y * deltaTime);
			this.position.add(this.velocity.x * deltaTime, this.velocity.y * deltaTime);
			this.bounds.lowerLeft.set(this.position);
			
			if (CollisionDetecctor.rectangleCollision(world.bounds, this.bounds)) {
				
				this.velocity.set(0, 0);
				this.onFloor = true;
			}
			
		} else {
			
			if (this.virtualSpeed > 0) {
				
				this.virtualSpeed = Math.max(0, this.virtualSpeed - 10000 * deltaTime);
			}
		}
	}
}

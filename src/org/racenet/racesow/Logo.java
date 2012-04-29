package org.racenet.racesow;

import org.racenet.framework.Drawable;
import org.racenet.framework.GameObject;
import org.racenet.framework.TexturedBlock;
import org.racenet.framework.Vector2;

/**
 * Animated racesow logo
 * 
 * @author soh#zolex
 */
public class Logo implements Drawable {

	TexturedBlock top, middle, bottom;
	Vector2 velTop, velMiddle, velBottom;
	Vector2 target = new Vector2();
	public float time = 0.0f;
	float delayTop = 0.25f;
	float delayMiddle = 0.45f;
	float delayBottom = 0.65f;
	float easing = 70f;
	
	/**
	 * Constructor
	 */
	public Logo() {
		
		this.top = new TexturedBlock("logo_top.png", GameObject.FUNC_NONE, -1, -1, 0, 0, new Vector2(0, 0), new Vector2(25.6f, 0));
		this.middle = new TexturedBlock("logo_middle.png", GameObject.FUNC_NONE, -1, -1, 0, 0, new Vector2(0, 0), new Vector2(25.6f, 0));
		this.bottom = new TexturedBlock("logo_bottom.png", GameObject.FUNC_NONE, -1, -1, 0, 0, new Vector2(0, 0), new Vector2(25.6f, 0));
		
		this.setVelocities();
	}
	
	/**
	 * Set the final position for the logo parts
	 * 
	 * @param float x
	 * @param float y
	 */
	public void setPosition(float x, float y) {
		
		this.target.x = x;
		this.target.y = y;
		
		this.top.vertices[0].x = -25.6f;
		this.top.vertices[0].y = y;
		
		this.middle.vertices[0].x = 80;
		this.middle.vertices[0].y = y;
		
		this.bottom.vertices[0].x = x;
		this.bottom.vertices[0].y = 60f;
	}
	
	/**
	 * Prepare the velocities for the logo parts
	 */
	private void setVelocities() {
		
		this.velTop = new Vector2(450, 0);
		this.velMiddle = new Vector2(-450, 0);
		this.velBottom = new Vector2(0, -291);
	}
	
	/**
	 * Reset the logo to it's initial state
	 */
	public void reset() {
		
		this.time = 0;
		this.setVelocities();
		this.setPosition(this.target.x, this.target.y);
	}
	
	/**
	 * Update the positions of the logo parts
	 * 
	 * @param float deltaTime
	 */
	public void update(float deltaTime) {
		
		this.time += deltaTime;
		
		if (this.time > this.delayTop) {
			
			if (this.top.vertices[0].x < this.target.x) {
		
				this.top.vertices[0].x += this.velTop.x * deltaTime;
				this.velTop.x /= (this.easing * deltaTime);
				this.velTop.x = Math.max(0.25f, this.velTop.x);
			
			} else {
				
				this.top.vertices[0].x = this.target.x;
			}
		}
		
		if (this.time > this.delayMiddle) {
			
			if (this.middle.vertices[0].x > this.target.x) {
		
				this.middle.vertices[0].x += this.velMiddle.x * deltaTime;
				this.velMiddle.x /= (this.easing * deltaTime);
				this.velMiddle.x = Math.min(-0.25f, this.velMiddle.x);
				
			} else {
				
				this.middle.vertices[0].x = this.target.x;
			}
		}
		
		if (this.time > this.delayBottom) {
			if (this.bottom.vertices[0].y > this.target.y) {
			
				this.bottom.vertices[0].y += this.velBottom.y * deltaTime;
				this.velBottom.y /= (this.easing * deltaTime);
				this.velBottom.y= Math.min(-0.25f, this.velBottom.y);
				
			} else {
				
				this.bottom.vertices[0].y = this.target.y;
			}
		}
	}

	/**
	 * Draw the logo parts
	 */
	public void draw() {
		
		this.top.draw();
		this.middle.draw();
		this.bottom.draw();
	}

	/**
	 * Reload the textures
	 */
	public void reloadTexture() {
		
		this.top.reloadTexture();
		this.middle.reloadTexture();
		this.bottom.reloadTexture();
	}

	/**
	 * Get rid of the textures
	 */
	public void dispose() {
		
		this.top.dispose();
		this.middle.dispose();
		this.bottom.dispose();
	}
}

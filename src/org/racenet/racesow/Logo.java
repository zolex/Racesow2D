package org.racenet.racesow;

import org.racenet.framework.Drawable;
import org.racenet.framework.GameObject;
import org.racenet.framework.TexturedBlock;
import org.racenet.framework.Vector2;

import android.util.Log;

/**
 * Animated racesow logo
 * 
 * @author soh#zolex
 */
public class Logo implements Drawable {

	TexturedBlock top, middle, bottom;
	Vector2 target = new Vector2();
	public float time = 0.0f;
	float delayTop = 0.25f;
	float delayMiddle = 0.45f;
	float delayBottom = 0.65f;
	float topX, middleX, bottomY;
	
	/**
	 * Constructor
	 */
	public Logo() {
		
		this.top = new TexturedBlock("logo_top.png", GameObject.FUNC_NONE, -1, -1, 0, 0, new Vector2(0, 0), new Vector2(25.6f, 0));
		this.middle = new TexturedBlock("logo_middle.png", GameObject.FUNC_NONE, -1, -1, 0, 0, new Vector2(0, 0), new Vector2(25.6f, 0));
		this.bottom = new TexturedBlock("logo_bottom.png", GameObject.FUNC_NONE, -1, -1, 0, 0, new Vector2(0, 0), new Vector2(25.6f, 0));
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
		
		this.topX = -25.6f;
		this.top.vertices[0].x = this.topX;
		this.top.vertices[0].y = y;
		
		this.middleX = 80;
		this.middle.vertices[0].x = this.middleX;
		this.middle.vertices[0].y = y;
		
		this.bottomY = 60;
		this.bottom.vertices[0].x = x;
		this.bottom.vertices[0].y = this.bottomY;
	}
	
	/**
	 * Reset the logo to it's initial state
	 */
	public void reset() {
		
		this.time = 0;
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
			
			float progress = (this.time - this.delayTop) / 0.75f;
			if (progress <= 1.0f) {
			
				this.top.vertices[0].x = (this.target.x - this.topX) * 2 * ( progress - (progress * progress) / 2 ) + this.topX;
			}
		}
		
		if (this.time > this.delayMiddle) {
			
			float progress = (this.time - this.delayMiddle) / 0.75f;
			if (progress <= 1.0f) {
			
				this.middle.vertices[0].x = (this.target.x - this.middleX) * 2 * ( progress - (progress * progress) / 2 ) + this.middleX;
			}
		}
		
		if (this.time > this.delayBottom) {
			
			float progress = (this.time - this.delayBottom) / 0.75f;
			if (progress <= 1.0f) {
			
				this.bottom.vertices[0].y = (this.target.y - this.bottomY) * 2 * ( progress - (progress * progress) / 2 ) + this.bottomY;
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

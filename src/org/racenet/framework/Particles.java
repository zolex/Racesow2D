package org.racenet.framework;

import android.opengl.GLES10;

public class Particles extends TexturedBlock {

	Vector2[] positions;
	Vector2[] velocities;
	Vector2 gravity = new Vector2(0, -200);
	Vector2 origin;
	float time;
	float delay;
	
	public Particles(String texture, float size, Vector2 origin, float delay) {
		
		super(texture, GameObject.FUNC_NONE, -1, -1, 0, 0, new Vector2(0, 0), new Vector2(size, 0));
		
		this.delay = delay;
		this.positions = new Vector2[50];
		this.velocities = new Vector2[50];
		this.origin = origin;
		this.reset();
	}
	
	public void reset() {
		
		this.time = 0;
		final int length = this.positions.length;
		for (int i = 0; i < length; i++) {
		
			this.positions[i] = this.origin.copy();
			this.velocities[i] = new Vector2(this.getRand(-150, 150), this.getRand(-150, 150));
		}
	}
	
	public int getRand(int min, int max) {
		
		return min + (int)(Math.random() * ((max - min) + 1));
	}
	
	public void update(float deltaTime) {
		
		this.time += deltaTime;
		if (this.time < this.delay) {
			
			return;
		}
		
		final int length = this.positions.length;
		for (int i = 0; i < length; i++) {
			
			this.positions[i].x += this.velocities[i].x * deltaTime;
			this.positions[i].y += this.velocities[i].y * deltaTime;
			
			this.velocities[i].add(this.gravity.x * deltaTime, this.gravity.y * deltaTime);
		}
	}
	
	/**
	 * Draw the particles
	 */
	public void draw() {
		
		if (this.time < this.delay) {
			
			return;
		}
		
		this.texture.bind();
		this.glVertices.bind();
		final int length = this.positions.length;
		for (int i = 0; i < length; i++) {
		
			GLES10.glPushMatrix();
			GLES10.glTranslatef(this.positions[i].x, this.positions[i].y, 0);
			this.glVertices.draw(GLES10.GL_TRIANGLES, 0, 6);
			GLES10.glPopMatrix();
		}
		
		this.glVertices.unbind();
	}
}

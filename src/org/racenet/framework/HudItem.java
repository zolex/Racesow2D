package org.racenet.framework;

public abstract class HudItem extends GameObject {

	public float cameraX = 0;
	public float cameraY = 0;
	
	public HudItem(Vector2 ... vertices) {
		
		super(vertices);
		this.cameraX = vertices[0].x;
		this.cameraY = vertices[0].y;
	}
	
	public abstract void draw();
}

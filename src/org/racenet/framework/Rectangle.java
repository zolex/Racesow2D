package org.racenet.framework;

public class Rectangle {

	public final Vector2 lowerLeft = new Vector2();
	public float width, height;
	
	public Rectangle (float x, float y, float width, float height) {
		
		this.lowerLeft.set(x, y);
		this.width = width;
		this.height = height;
	}
}

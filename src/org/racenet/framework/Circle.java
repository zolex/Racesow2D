package org.racenet.framework;

public class Circle {

	public final Vector2 center = new Vector2();
	public float radius;
	
	public Circle (float x, float y, float r) {
		
		center.set(x, y);
		radius = r;
	}
	
	public Circle (Vector2 position, float r) {
		
		center.set(position);
		radius = r;
	}
}

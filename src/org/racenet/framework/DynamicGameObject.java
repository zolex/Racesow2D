package org.racenet.framework;

public class DynamicGameObject extends GameObject {

	public final Vector2 velocity = new Vector2();
	public final Vector2 accel = new Vector2();
	
	public DynamicGameObject(Vector2 ... edges) {
		
		super(edges);
	}
}

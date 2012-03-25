package org.racenet.framework;

public class GameObject {

	public final Vector2 position = new Vector2();
	public final Rectangle bounds;
	
	public GameObject(float x, float y, float width, float height) {
		
		position.set(x, y);
		bounds = new Rectangle(x, y, width, height);
	}
}

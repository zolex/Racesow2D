package org.racenet.framework;

public class GameObject {

	public final Vector2 position = new Vector2();
	public final Polygon bounds;
	
	public GameObject(Vector2 ... edges) {
		
		position.set(edges[0].x, edges[0].y);
		bounds = new Polygon(edges);
	}
}

package org.racenet.framework;

public class GameObject {

	public Polygon bounds;
	
	public GameObject(Vector2 ... edges) {
		
		this.bounds = new Polygon(edges);
	}
	
	public void setPosition(float x, float y) {
		
		this.bounds.setPosition(new Vector2(x, y));
	}
	
	public void setPosition(Vector2 position) {
		
		this.bounds.setPosition(position);
	}
	
	public void addToPosition(float x, float y) {
		
		this.bounds.addToPosition(x, y);
	}
}

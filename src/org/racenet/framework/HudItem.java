package org.racenet.framework;

/**
 * Class which represents an item of the HUD
 * 
 * @author soh#zolex
 *
 */
public abstract class HudItem extends GameObject {

	public float cameraX = 0;
	public float cameraY = 0;
	
	/**
	 * Constructor
	 * 
	 * @param Vector2 ... vertices
	 */
	public HudItem(Vector2 ... vertices) {
		
		super(vertices);
		this.cameraX = vertices[0].x;
		this.cameraY = vertices[0].y;
	}
	
	/**
	 * These methods must be implemented by derivatives
	 */
	public abstract void draw();
	public abstract void reloadTexture();
	public abstract void dispose();
}

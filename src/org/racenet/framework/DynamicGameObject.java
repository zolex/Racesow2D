package org.racenet.framework;

/**
 * A GameObject which can move in the world
 * 
 * @author soh#zolex
 */
public class DynamicGameObject extends GameObject {

	public final Vector2 velocity = new Vector2();
	public final Vector2 accel = new Vector2();
	
	/**
	 * Constructor
	 * 
	 * @param Vector2 ... vertices
	 */
	public DynamicGameObject(Vector2 ... vertices) {
		
		super(vertices);
	}
}

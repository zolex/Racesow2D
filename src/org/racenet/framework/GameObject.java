package org.racenet.framework;

/**
 * Represents a basic object inside of an openGL game
 * 
 * @author soh#zolex
 *
 */
public class GameObject extends Polygon implements Drawable {

	// object functions
	public static final short FUNC_NONE = 0;
	public static final short FUNC_LAVA = 1;
	public static final short FUNC_START_TIMER = 2;
	public static final short FUNC_STOP_TIMER = 3;
	public static final short FUNC_WATER = 4;
	public static final short FUNC_TUTORIAL = 5;
	public static final short FUNC_DRIFTSAND = 6;
	
	// item functions
	public static final short ITEM_NONE = 100;
	public static final short ITEM_ROCKET = 101;
	public static final short ITEM_PLASMA = 102;
	
	// the applied function
	public short func;
	
	// only for tutorial
	public String text = "";
	public boolean finished = false;
	public String event = "";
	
	/**
	 * Constructor
	 * 
	 * @param Vertices ... vertices
	 */
	public GameObject(Vector2 ... vertices) {
		
		super(vertices);
		this.func = FUNC_NONE;
	}
	
	public void draw() {};
	public void reloadTexture() {};
	public void dispose() {};
}

package org.racenet.framework;

public class GameObject extends Polygon {

	public static final short FUNC_NONE = 0;
	public static final short FUNC_LAVA = 1;
	public static final short FUNC_START_TIMER = 2;
	public static final short FUNC_STOP_TIMER = 3;
	
	public short func;
	
	public GameObject(Vector2 ... vertices) {
		
		super(vertices);
		this.func = FUNC_NONE;
	}
}

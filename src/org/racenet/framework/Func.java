package org.racenet.framework;

public class Func extends GameObject {

	public static final short NONE = 0;
	public static final short START_TIMER = 1;
	public static final short STOP_TIMER = 2;
	
	public int type = NONE;
	
	public Func(short type, Vector2 ... edges) {
		
		super(edges);
		this.type = type;
	}
}

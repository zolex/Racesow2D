package org.racenet.racesow.models;

import org.racenet.framework.Vector2;

public class DemoKeyFrame {

	public static final short ACTION_NONE = 0;
	public static final short ACTION_SAVE = 1;
	public static final short ACTION_CANCEL = 2;
	public static final short ACTION_META = 3;
	
	public String meta = null;
	public short action = ACTION_NONE;
	public Vector2 playerPosition = new Vector2();
	public int playerAnimation;
	public int playerSound;
	public int playerSpeed;
	public float mapTime;
	public String decalType;
	public float decalX;
	public float decalY;
	public float frameTime;
}

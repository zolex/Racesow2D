package org.racenet.framework.interfaces;

import java.util.List;

import android.view.MotionEvent;

public interface Input {

	public static class TouchEvent {
		
		public static final int TOUCH_DOWN = 0;
		public static final int TOUCH_UP = 1;
		public static final int TOUCH_DRAG = 2;
		
		public int type;
		public int x;
		public int y;
		public int pointer;
		public MotionEvent source;
	}
	
	public List<TouchEvent> getTouchEvents();
}

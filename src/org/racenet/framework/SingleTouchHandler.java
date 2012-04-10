package org.racenet.framework;

import java.util.ArrayList;
import java.util.List;

import org.racenet.framework.Pool.PoolObjectFactory;
import org.racenet.framework.interfaces.Input.TouchEvent;
import org.racenet.framework.interfaces.TouchHandler;

import android.view.MotionEvent;
import android.view.View;

/**
 * Class to handle single touch events
 * 
 * @author soh#zolex
 *
 */
public class SingleTouchHandler implements TouchHandler {

	private static final int POOL_SIZE = 100;
	
	Pool<TouchEvent> touchEventPool;
	List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
	List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();
	float scaleX;
	float scaleY;
	
	/**
	 * Constructor
	 * 
	 * @param View view
	 * @param float scaleX
	 * @param float scaleY
	 */
	public SingleTouchHandler(View view, float scaleX, float scaleY) {
		
		PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>() {

			public TouchEvent createObject() {
				
				return new TouchEvent();
			}
		};
		
		touchEventPool = new Pool<TouchEvent>(factory, POOL_SIZE);
		view.setOnTouchListener(this);
		
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
	
	/**
	 * Event when the screen is being touched
	 * 
	 * @param View v
	 * @param MotionEvent e
	 * @return boolean
	 */
	public boolean onTouch(View v, MotionEvent e) {
		
		synchronized (this) {
			
			TouchEvent touchEvent = touchEventPool.newObject();
			switch(e.getAction()) {
			
				case MotionEvent.ACTION_DOWN:
					touchEvent.type = TouchEvent.TOUCH_DOWN;
					break;
					
				case MotionEvent.ACTION_MOVE:
					touchEvent.type = TouchEvent.TOUCH_DRAG;
					break;
					
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					touchEvent.type = TouchEvent.TOUCH_UP;
					break;
			}
			
			touchEvent.x = (int)(e.getX() * scaleX);
			touchEvent.y = (int)(e.getY() * scaleY);
			
			touchEventsBuffer.add(touchEvent);
			
			return true;
		}
	}

	/**
	 * Get a list of buffered touch events
	 * 
	 * @return List<TouchEvent>
	 */
	public List<TouchEvent> getTouchEvents() {
		
		synchronized (this) {
			
			int length = touchEvents.size();
			for (int i = 0; i < length; i++) {
				
				touchEventPool.free(touchEvents.get(i));
			}
			
			touchEvents.clear();
			touchEvents.addAll(touchEventsBuffer);
			touchEventsBuffer.clear();
			
			return touchEvents;
		}
	}
}

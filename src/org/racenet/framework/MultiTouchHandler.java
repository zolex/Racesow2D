package org.racenet.framework;

import java.util.ArrayList;
import java.util.List;

import android.view.MotionEvent;
import android.view.View;

import org.racenet.framework.Pool;
import org.racenet.framework.Pool.PoolObjectFactory;
import org.racenet.framework.interfaces.Input.TouchEvent;
import org.racenet.framework.interfaces.TouchHandler;

/**
 * Class to handle multiple touch events
 * 
 * @author soh#zolex
 *
 */
public class MultiTouchHandler implements TouchHandler {
	
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
    public MultiTouchHandler(View view, float scaleX, float scaleY) {
    	
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
    public boolean onTouch(View v, MotionEvent event) {
    	
        synchronized (this) {
        	
            int action = event.getAction() & MotionEvent.ACTION_MASK;
            int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
            int pointerId = event.getPointerId(pointerIndex);
            TouchEvent touchEvent;
            
            switch (action) {
            
	            case MotionEvent.ACTION_DOWN:
	            case MotionEvent.ACTION_POINTER_DOWN:
	                touchEvent = touchEventPool.newObject();
	                touchEvent.type = TouchEvent.TOUCH_DOWN;
	                touchEvent.pointer = pointerId;
	                touchEvent.x = (int)(event.getX(pointerIndex) * scaleX);
	                touchEvent.y = (int)(event.getY(pointerIndex) * scaleY);
	                touchEventsBuffer.add(touchEvent);
	                break;
	
	            case MotionEvent.ACTION_UP:
	            case MotionEvent.ACTION_POINTER_UP:
	            case MotionEvent.ACTION_CANCEL:
	                touchEvent = touchEventPool.newObject();
	                touchEvent.type = TouchEvent.TOUCH_UP;
	                touchEvent.pointer = pointerId;
	                touchEvent.x = (int)(event.getX(pointerIndex) * scaleX);
	                touchEvent.y = (int)(event.getY(pointerIndex) * scaleY);
	                touchEventsBuffer.add(touchEvent);
	                break;
	
	            case MotionEvent.ACTION_MOVE:
	                int pointerCount = event.getPointerCount();
	                for (int i = 0; i < pointerCount; i++) {
	                	
	                    pointerIndex = i;
	                    pointerId = event.getPointerId(pointerIndex);
	                    
	                    touchEvent = touchEventPool.newObject();
	                    touchEvent.type = TouchEvent.TOUCH_DRAG;
	                    touchEvent.pointer = pointerId;
	                    touchEvent.x = (int)(event.getX(pointerIndex) * scaleX);
	                    touchEvent.y = (int)(event.getY(pointerIndex) * scaleY);
	                    touchEventsBuffer.add(touchEvent);
	                }
	                break;
            }

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

package org.racenet.framework;

import java.util.List;

import org.racenet.framework.interfaces.Input;
import org.racenet.framework.interfaces.TouchHandler;

import android.content.Context;
import android.os.Build.VERSION;
import android.view.View;

/**
 * Class to handle android single- or multitouch input
 * 
 * @author soh#zolex
 *
 */
public class AndroidInput implements Input {

	public TouchHandler touchHandler;
	
	/**
	 * Constructor 
	 * 
	 * @param Contextcontext
	 * @param View view
	 * @param float scaleX
	 * @param float scaleY
	 */
	public AndroidInput(Context context, View view, float scaleX, float scaleY) {
		
		if (Integer.parseInt(VERSION.SDK) < 5) {
			
			touchHandler = new SingleTouchHandler(view, scaleX, scaleY);
			
		} else {
			
			touchHandler = new MultiTouchHandler(view, scaleX, scaleY);
		}
	}
	
	/**
	 * Get a list of touch events
	 * 
	 * @return List<TouchEvent>
	 */
	public List<TouchEvent> getTouchEvents() {
		
		return touchHandler.getTouchEvents();
	}
}

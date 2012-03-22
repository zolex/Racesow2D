package org.racenet.framework;

import java.util.List;

import org.racenet.framework.interfaces.Input;
import org.racenet.framework.interfaces.TouchHandler;

import android.content.Context;
import android.os.Build.VERSION;
import android.view.View;

public class AndroidInput implements Input {

	private TouchHandler touchHandler;
	
	public AndroidInput(Context context, View view, float scaleX, float scaleY) {
		
		if (Integer.parseInt(VERSION.SDK) < 5) {
			
			touchHandler = new SingleTouchHandler(view, scaleX, scaleY);
			
		} else {
			
			touchHandler = new MultiTouchHandler(view, scaleX, scaleY);
		}
	}
	
	public List<TouchEvent> getTouchEvents() {
		
		return touchHandler.getTouchEvents();
	}
}

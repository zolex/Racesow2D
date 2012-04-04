package org.racenet.framework.interfaces;

import java.util.List;

import org.racenet.framework.interfaces.Input.TouchEvent;

import android.view.View.OnTouchListener;

/**
 * interface for touch handlers
 * 
 * @author soh#zolex
 *
 */
public interface TouchHandler extends OnTouchListener {

	public List<TouchEvent> getTouchEvents();
}

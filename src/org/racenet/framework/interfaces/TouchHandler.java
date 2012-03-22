package org.racenet.framework.interfaces;

import java.util.List;

import org.racenet.framework.interfaces.Input.TouchEvent;

import android.view.View.OnTouchListener;

public interface TouchHandler extends OnTouchListener {

	public List<TouchEvent> getTouchEvents();
}

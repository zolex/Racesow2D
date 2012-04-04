package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;

/**
 * Class to transport the openGL ES view and base class
 * 
 * @author al
 *
 */
public class GLGraphics {

	GLSurfaceView glView;
	GL10 gl;
	
	/**
	 * Constructor 
	 * 
	 * @param GLSurfaceView v
	 */
	GLGraphics(GLSurfaceView v) {
		
		glView = v;
	}
	
	/**
	 * Get the openGL base class
	 * 
	 * @return GL10
	 */
	public GL10 getGL() {
		
		return gl;
	}
	
	/**
	 * Set the openGL base class
	 * 
	 * @param GL10 g
	 */
	public void setGL(GL10 g) {
		
		gl = g;
	}
	
	/**
	 * Get the view width
	 * 
	 * @return int
	 */
	public int getWidth() {
		
		return glView.getWidth();
	}
	
	/**
	 * Get the view height
	 * 
	 * @return int
	 */
	public int getHeight() {
		
		return glView.getHeight();
	}
}
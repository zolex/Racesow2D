package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;

public class GLGraphics {

	GLSurfaceView glView;
	GL10 gl;
	
	GLGraphics(GLSurfaceView v) {
		
		glView = v;
	}
	
	public GL10 getGL() {
		
		return gl;
	}
	
	public void setGL(GL10 g) {
		
		gl = g;
	}
	
	public int getWidth() {
		
		return glView.getWidth();
	}
	
	public int getHeight() {
		
		return glView.getHeight();
	}
}
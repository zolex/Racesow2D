package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

import org.racenet.framework.GLGame;
import org.racenet.framework.GLGraphics;
import org.racenet.framework.GLString;
import org.racenet.framework.GLVertices;
import org.racenet.framework.GameObject;

public class CameraText extends HudItem {
	
	public GLVertices vertices;
	public GLString string;
	public float cameraX;
	public float cameraY;
	private GL10 gl;
	
	public CameraText(GL10 gl, Vector2 ... vertices) {
		
		super(vertices);
		this.gl = gl;
	}
	
	public void setupVertices(GLGraphics glGraphics) {
		
		vertices = new GLVertices(glGraphics, 4, 6 , false, true);
		vertices.setVertices(new float[] {	 0,  0, 0, 1,
											 8, 0, 1, 1,
											 8, 2, 1, 0,
											 0,  2, 0, 0 }, 0, 16);
		vertices.setIndices(new short[] {0, 1, 2, 2, 3, 0}, 0, 6);
	}
	
	public void setupText(GLGame game, String text) {
		
		if (string != null) {
			string.dispose();
		}
		
		string = new GLString(game, text, 512, 128, 6, 112);
	}
	
	public void draw() {
		
		this.gl.glPushMatrix();
		this.gl.glTranslatef(this.getPosition().x, this.getPosition().y, 0);
		string.bind();
		vertices.bind();
		vertices.draw(GL10.GL_TRIANGLES, 0, 6);
		vertices.unbind();
		this.gl.glPopMatrix();
	}
}
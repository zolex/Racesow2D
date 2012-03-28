package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

import org.racenet.framework.GLGame;
import org.racenet.framework.GLGraphics;
import org.racenet.framework.GLString;
import org.racenet.framework.GLVertices;
import org.racenet.framework.GameObject;

public class CameraText extends GameObject {
	
	public GLVertices vertices;
	public GLString string;
	public float cameraX;
	public float cameraY;
	
	public CameraText(Vector2 ... edges) {
		
		super(edges);
		this.cameraX = edges[0].x;
		this.cameraY = edges[0].y;
	}
	
	public void setupVertices(GLGraphics glGraphics) {
		
		vertices = new GLVertices(glGraphics, 4, 6 , false, true);
		vertices.setVertices(new float[] {	-12, -2, 0, 1,
											 12, -2, 1, 1,
											 12,  2, 1,  0,
											-12,  2, 0,  0 }, 0, 16);
		vertices.setIndices(new short[] {0, 1, 2, 2, 3, 0}, 0, 6);
	}
	
	public void setupText(GLGame game, String text) {
		
		if (string != null) {
			string.dispose();
		}
		
		string = new GLString(game, text, 512, 128, 6, 112);
	}
	
	public void draw(GL10 gl) {
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glLoadIdentity();
		gl.glTranslatef(position.x, position.y, 0);
		string.bind();
		vertices.bind();
		vertices.draw(GL10.GL_TRIANGLES, 0, 6);
		vertices.unbind();
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
}
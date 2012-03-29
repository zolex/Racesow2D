package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

public class TexturedTriangle extends TexturedShape {

	public TexturedTriangle(GLGame game, String texture, short func, float texScaleWidth, float texScaleHeight, Vector2 ... edges) {
		
		super(edges);
		this.game = game;
		this.setupTexture(texture, texScaleWidth, texScaleHeight);
		this.setupVertices();
		this.setFunc(func);
	}

	private void setupVertices() {
		
		float[] vertices;
		
		vertices = new float[] {
				this.bounds.points[0].x, this.bounds.points[0].y,	0, 1,
				this.bounds.points[1].x, this.bounds.points[1].y,	1, 1,
				this.bounds.points[2].x, this.bounds.points[2].y,	1, 0 };
		
		
		this.vertices = new GLVertices(this.game.getGLGraphics(), 3, 0 , false, true);
		this.vertices.setVertices(vertices, 0, 12);
	}
	
	public void draw() {
		
		GL10 gl = this.game.getGLGraphics().getGL();
		
		gl.glPushMatrix();
		//gl.glTranslatef(this.bounds.getPosition().x, this.bounds.getPosition().y, 0);
		this.texture.bind();
		this.vertices.bind();
		this.vertices.draw(GL10.GL_TRIANGLES, 0, 3);
		this.vertices.unbind();
		gl.glPopMatrix();
	}
}

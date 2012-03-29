package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

public class TexturedTriangle extends TexturedShape {

	public TexturedTriangle(GLGame game, String texture, short func, float texScaleWidth, float texScaleHeight, Vector2 ... vertices) {
		
		super(game, texture, func, texScaleWidth, texScaleHeight, vertices);
	}

	protected void setupVertices() {
		
		float[] vertices;
		
		vertices = new float[] {
				this.vertices[0].x, this.vertices[0].y,	0, 1,
				this.vertices[1].x, this.vertices[1].y,	1, 1,
				this.vertices[2].x, this.vertices[2].y,	1, 0 };
		
		
		this.glVertices = new GLVertices(this.game.getGLGraphics(), 3, 0 , false, true);
		this.glVertices.setVertices(vertices, 0, 12);
	}
	
	public void draw() {
		
		GL10 gl = this.game.getGLGraphics().getGL();
		
		gl.glPushMatrix();
		//gl.glTranslatef(this.bounds.getPosition().x, this.bounds.getPosition().y, 0);
		this.texture.bind();
		this.glVertices.bind();
		this.glVertices.draw(GL10.GL_TRIANGLES, 0, 3);
		this.glVertices.unbind();
		gl.glPopMatrix();
	}
}

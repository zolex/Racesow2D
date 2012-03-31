package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class TexturedTriangle extends TexturedShape {

	public TexturedTriangle(GLGame game, String texture, short func, float texScaleWidth, float texScaleHeight, Vector2 ... vertices) {
		
		super(game, texture, func, texScaleWidth, texScaleHeight, vertices);
	}

	protected void setupVertices() {

		float[] glVertices;
		if (this.vertices[1].x == this.vertices[2].x) {
		
			glVertices = new float[] {
				this.vertices[0].x, this.vertices[0].y,	0, this.height / (this.texture.height * this.texScaleHeight),
				this.vertices[1].x, this.vertices[1].y,	this.width / (this.texture.width * this.texScaleWidth), height / (this.texture.height * this.texScaleHeight),
				this.vertices[2].x, this.vertices[2].y,	this.width / (this.texture.width * this.texScaleWidth), 0 };
			
		} else {
			
			glVertices = new float[] {
					this.vertices[0].x, this.vertices[0].y,	this.width / (this.texture.width * this.texScaleWidth), 0,
					this.vertices[1].x, this.vertices[1].y,	this.width / (this.texture.width * this.texScaleWidth), height / (this.texture.height * this.texScaleHeight),
					this.vertices[2].x, this.vertices[2].y,	0, this.height / (this.texture.height * this.texScaleHeight) };
		}
		
		
		this.glVertices = new GLVertices(this.game.getGLGraphics(), 3, 0 , false, true);
		this.glVertices.setVertices(glVertices, 0, 12);
	}
	
	public void draw() {
		
		GL10 gl = this.game.getGLGraphics().getGL();
		
		this.texture.bind();
		this.glVertices.bind();
		this.glVertices.draw(GL10.GL_TRIANGLES, 0, 3);
		this.glVertices.unbind();
	}
	
	public void drawOutline() {
		
		GL10 gl = this.game.getGLGraphics().getGL();
		
		//gl.glDisable(GL10.GL_TEXTURE_2D);
		this.glVertices.bind();
		this.glVertices.draw(GL10.GL_LINE_LOOP, 0, 3);
		this.glVertices.unbind();
		//gl.glEnable(GL10.GL_TEXTURE_2D);
	}
	
	public float getHeightAt(float x, boolean fake) {
		
		// ramp up
		if (this.vertices[1].x == this.vertices[2].x) {
			
			return (this.vertices[2].y - this.vertices[0].y) / (this.vertices[2].x - this.vertices[0].x) * (x - this.vertices[0].x);
		}
		
		// ramp down
		if (!fake && this.vertices[0].x == this.vertices[1].x) {
			
			return (this.vertices[0].y - this.vertices[2].y) / (this.vertices[0].x - this.vertices[2].x) * (x - this.vertices[2].x) - this.height;
		}

		return this.height;
	}
}

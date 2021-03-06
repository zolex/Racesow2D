package org.racenet.framework;

import android.opengl.GLES10;

/**
 * A drawable triangle with a texture applied
 * 
 * @author soh#zolex
 *
 */
public class TexturedTriangle extends TexturedShape implements Drawable {

	/**
	 * Constructor 
	 * 
	 * @param GLGame game
	 * @param String texture
	 * @param short func
	 * @param float texScaleWidth
	 * @param float texScaleHeight
	 * @param Vector2 ... vertices
	 */
	public TexturedTriangle(String texture, short func, float texScaleWidth, float texScaleHeight, float texShiftX, float texShiftY, Vector2 ... vertices) {
		
		super(texture, func, texScaleWidth, texScaleHeight, texShiftX, texShiftY, vertices);
	}
	/**
	 * Constructor 
	 * 
	 * @param GLGame game
	 * @param GLTexture texture
	 * @param short func
	 * @param float texScaleWidth
	 * @param float texScaleHeight
	 * @param Vector2 ... vertices
	 */
	public TexturedTriangle(GLTexture texture, short func, float texScaleWidth, float texScaleHeight, float texShiftX, float texShiftY, Vector2 ... vertices) {
		
		super(texture, func, texScaleWidth, texScaleHeight, texShiftX, texShiftY, vertices);
	}

	/**
	 * Setup the vertices and texture coordinates for the triangle
	 */
	protected void setupVertices() {

		float[] glVertices;
		
		this.texShiftX = 1 / (float)this.texture.width * this.texShiftX;
		this.texShiftY = 1 / (float)this.texture.height * this.texShiftY;
		
		// if the triangle is a "ramp-up"
		if (this.vertices[1].x == this.vertices[2].x) {
		
			glVertices = new float[] {
				this.vertices[0].x, this.vertices[0].y,	-this.texShiftX, this.height / (this.texture.height * this.texScaleHeight) + this.texShiftY,
				this.vertices[1].x, this.vertices[1].y,	this.width / (this.texture.width * this.texScaleWidth) - this.texShiftX, height / (this.texture.height * this.texScaleHeight) + this.texShiftY,
				this.vertices[2].x, this.vertices[2].y,	this.width / (this.texture.width * this.texScaleWidth) - this.texShiftX, this.texShiftY };
			
		// if the trianlge is a "ramp-down"
		} else {
			
			glVertices = new float[] {
					this.vertices[0].x, this.vertices[0].y,	-this.texShiftX, this.texShiftY,
					this.vertices[1].x, this.vertices[1].y,	-this.texShiftX, height / (this.texture.height * this.texScaleHeight) + this.texShiftY,
					this.vertices[2].x, this.vertices[2].y,	this.width / (this.texture.width * this.texScaleWidth) - this.texShiftX, this.height / (this.texture.height * this.texScaleHeight) + this.texShiftY };
		}
		
		
		this.glVertices = new GLVertices(3, 0 , false, true);
		this.glVertices.setVertices(glVertices, 0, 12);
	}
	
	/**
	 * Draw the triangle
	 */
	public void draw() {
		
		this.texture.bind();
		this.glVertices.bind();
		this.glVertices.draw(GLES10.GL_TRIANGLES, 0, 3);
		this.glVertices.unbind();
	}
	
	/**
	 * Draw the outlines of the triangle
	 */
	public void drawOutline() {
		
		this.glVertices.bind();
		this.glVertices.draw(GLES10.GL_LINE_LOOP, 0, 3);
		this.glVertices.unbind();
	}
}

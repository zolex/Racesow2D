package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

/**
 * A drawable rectangle with a texture applied
 * 
 * @author al
 *
 */
public class TexturedBlock extends TexturedShape {

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
	public TexturedBlock(GLGame game, String texture, short func, float texScaleWidth, float texScaleHeight, float texShiftX, float texShiftY, Vector2 ... vertices) {
		
		super(game, texture, func, texScaleWidth, texScaleHeight, texShiftX, texShiftY, vertices);
	}
	
	/**
	 * Setup the vertices and texture coordinates for the rectangle
	 */
	protected void setupVertices() {
		
		float[] vertices;
		
		// create the height according to the texture aspect ratio if only the width is given by two points
		if (texScaleWidth == -1 && texScaleHeight == -1 && this.vertices.length == 2)  {
			
			this.height = this.width / (this.texture.width / this.texture.height);
			
			Vector2 newPoints[] = new Vector2[4];
			newPoints[0] = this.vertices[0];
			newPoints[1] = this.vertices[1];
			newPoints[2] = new Vector2(this.vertices[1].x, this.vertices[1].y + this.height);
			newPoints[3] = new Vector2(this.vertices[0].x, this.vertices[0].y + this.height);
			
			this.vertices = newPoints;
			
			vertices = new float[] {
					0,			0,	  			0, 1,
					this.width,	0,				1, 1,
					this.width,	this.height,	1, 0,
					0,			this.height,	0, 0 };
		
		// default prodecure
		} else {

			vertices = new float[] {
					0,			0,				-this.texShiftX * this.texScaleWidth, this.height / (this.texture.height * this.texScaleHeight) + this.texShiftY * this.texScaleHeight,
					this.width,	0,				this.width / (this.texture.width * this.texScaleWidth) - this.texShiftX * this.texScaleWidth, height / (this.texture.height * this.texScaleHeight) + this.texShiftY * this.texScaleHeight,
					this.width,	this.height,	this.width / (this.texture.width * this.texScaleWidth) - this.texShiftX * this.texScaleWidth, + this.texShiftY * this.texScaleHeight,
					0,			this.height,	-this.texShiftX * this.texScaleWidth, this.texShiftY * this.texScaleHeight };
		}
		
		
		this.glVertices = new GLVertices(this.game.getGLGraphics(), 4, 6 , false, true);
		this.glVertices.setVertices(vertices, 0, 16);
		this.glVertices.setIndices(new short[] {0, 1, 2, 0, 2, 3}, 0, 6);
	}
	
	/**
	 * Draw the rectangle with it's texture
	 */
	public void draw() {
		
		GL10 gl = this.game.getGLGraphics().getGL();
		
		gl.glPushMatrix();
		gl.glTranslatef(this.getPosition().x, this.getPosition().y, 0);
		this.texture.bind();
		this.glVertices.bind();
		this.glVertices.draw(GL10.GL_TRIANGLES, 0, 6);
		this.glVertices.unbind();
		gl.glPopMatrix();
	}
	
	/**
	 * Draw the outlines of the rectangle
	 */
	public void drawOutline() {
		
		GL10 gl = this.game.getGLGraphics().getGL();
		
		gl.glPushMatrix();
		gl.glTranslatef(this.getPosition().x, this.getPosition().y, 0);
		this.glVertices.bind();
		this.glVertices.draw(GL10.GL_LINE_LOOP, 0, 6);
		this.glVertices.unbind();
		gl.glPopMatrix();
	}
}

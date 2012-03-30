package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

public class TexturedBlock extends TexturedShape {

	public TexturedBlock(GLGame game, String texture, short func, float texScaleWidth, float texScaleHeight, Vector2 ... vertices) {
		
		super(game, texture, func, texScaleWidth, texScaleHeight, vertices);
	}
	
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
					0,			0,				0, this.height / (this.texture.height * this.texScaleHeight),
					this.width,	0,				this.width / (this.texture.width * this.texScaleWidth), height / (this.texture.height * this.texScaleHeight),
					this.width,	this.height,	this.width / (this.texture.width * this.texScaleWidth), 0,
					0,			this.height,	0, 0 };
		}
		
		
		this.glVertices = new GLVertices(this.game.getGLGraphics(), 4, 6 , false, true);
		this.glVertices.setVertices(vertices, 0, 16);
		this.glVertices.setIndices(new short[] {0, 1, 2, 0, 2, 3}, 0, 6);
	}
	
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
	
	public void drawOutline() {
		
		GL10 gl = this.game.getGLGraphics().getGL();
		
		gl.glPushMatrix();
		//gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glTranslatef(this.getPosition().x, this.getPosition().y, 0);
		this.glVertices.bind();
		this.glVertices.draw(GL10.GL_LINE_LOOP, 0, 6);
		this.glVertices.unbind();
		//gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glPopMatrix();
	}
	
	public float getHeightAt(float x) {
		
		return this.height;
	}
}

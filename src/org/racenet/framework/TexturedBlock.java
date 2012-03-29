package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

public class TexturedBlock extends TexturedShape {

	public TexturedBlock(GLGame game, String texture, short func, float texScaleWidth, float texScaleHeight, Vector2 ... edges) {
		
		super(edges);
		this.game = game;
		this.setupTexture(texture, texScaleWidth, texScaleHeight);
		this.setupVertices();
		this.func = func;
	}
	
	private void setupVertices() {
		
		float[] vertices;
		
		// create the height according to the texture aspect ratio if only the width is given by two points
		if (texScaleWidth == -1 && texScaleHeight == -1 && this.points.length == 2)  {
			
			float width = this.getWidth();
			float height = width / (this.texture.width / this.texture.height);
			
			Vector2 newPoints[] = new Vector2[4];
			newPoints[0] = this.points[0];
			newPoints[1] = this.points[1];
			newPoints[2] = new Vector2(this.points[1].x, this.points[1].y + height);
			newPoints[3] = new Vector2(this.points[0].x, this.points[0].y + height);
			
			this.points = newPoints;
			
			vertices = new float[] {
					0,		0,	  	0, 1,
					width,	0,		1, 1,
					width,	height,	1, 0,
					0,		height,	0, 0 };
		
		// default prodecure
		} else {

			float height = this.getHeight();
			float width = this.getWidth();
		
			vertices = new float[] {
					0,		0,		0, height / (this.texture.height * this.texScaleHeight),
					width,	0,		width / (this.texture.width * this.texScaleWidth), height / (this.texture.height * this.texScaleHeight),
					width,	height,	width / (this.texture.width * this.texScaleWidth), 0,
					0,		height,	0, 0 };
		}
		
		
		this.vertices = new GLVertices(this.game.getGLGraphics(), 4, 6 , false, true);
		this.vertices.setVertices(vertices, 0, 16);
		this.vertices.setIndices(new short[] {0, 1, 2, 0, 2, 3}, 0, 6);
	}
	
	public void draw() {
		
		GL10 gl = this.game.getGLGraphics().getGL();
		
		gl.glPushMatrix();
		gl.glTranslatef(this.getPosition().x, this.getPosition().y, 0);
		this.texture.bind();
		this.vertices.bind();
		this.vertices.draw(GL10.GL_TRIANGLES, 0, 6);
		this.vertices.unbind();
		gl.glPopMatrix();
	}
}

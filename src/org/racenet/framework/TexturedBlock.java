package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

public class TexturedBlock extends GameObject {

	public static final short FUNC_NONE = 0;
	public static final short FUNC_LAVA = 1;
	
	GLGame game = null;
	public GLVertices vertices = null;
	public GLTexture texture = null;
	float texScaleWidth = 0.05f;
	float texScaleHeight = 0.05f;
	public short func = FUNC_NONE;


	public TexturedBlock(GLGame game, String texture, short func, float texScaleWidth, float texScaleHeight, Vector2 ... edges) {
		
		super(edges);
		this.game = game;
		this.setupTexture(texture, texScaleWidth, texScaleHeight);
		this.setupVertices();
		this.setFunc(func);
	}
	
	public void setFunc(short func) {
		
		this.func = func;
	}
	
	public void setupTexture(String fileName, float scaleWidth, float scaleHeight) {
		
		this.texture = new GLTexture(this.game, fileName);
		this.texScaleWidth = scaleWidth == 0 ? 0.05f : scaleWidth;
		this.texScaleHeight = scaleHeight == 0 ? 0.05f : scaleHeight;
	}
	
	public void reloadTexture() {
		
		if (this.texture != null) {
			
			this.texture.reload();
		}
	}
	
	public void dispose() {
		
		if (this.texture != null) {
			
			this.texture.dispose();
		}
	}
	
	private void setupVertices() {
		
		float[] vertices;
		
		// create the height according to the texture aspect ratio if only the width is given by two points
		if (texScaleWidth == -1 && texScaleHeight == -1 && this.bounds.points.length == 2)  {
			
			float width = this.bounds.getWidth();
			float height = width / (this.texture.width / this.texture.height);
			
			this.bounds = new Polygon(
				this.bounds.points[0],
				this.bounds.points[1],
				new Vector2(this.bounds.points[1].x, this.bounds.points[1].y + height),
				new Vector2(this.bounds.points[0].x, this.bounds.points[0].y + height)
			);
			
			vertices = new float[] {
					0,		0,	  	0, 1,
					width,	0,		1, 1,
					width,	height,	1, 0,
					0,		height,	0, 0 };
		
		// default prodecure
		} else {

			float height = this.bounds.getHeight();
			float width = this.bounds.getWidth();
		
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
		gl.glTranslatef(this.bounds.getPosition().x, this.bounds.getPosition().y, 0);
		this.texture.bind();
		this.vertices.bind();
		this.vertices.draw(GL10.GL_TRIANGLES, 0, 6);
		this.vertices.unbind();
		gl.glPopMatrix();
	}
}

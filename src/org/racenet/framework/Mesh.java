package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

public class Mesh extends GameObject {

	public static final short FUNC_NONE = 0;
	public static final short FUNC_LAVA = 1;
	
	GLGame game = null;
	public GLVertices vertices = null;
	public GLTexture texture = null;
	float texScaleWidth = 0.05f;
	float texScaleHeight = 0.05f;
	public short func = FUNC_NONE;
	
	public Mesh(GLGame game, float x, float y, float width, float height, String texture) {
		
		super(x, y, width, height);
		this.game = game;
		this.setupTexture(texture);
		this.setupVertices();
	}
	
	public Mesh(GLGame game, float x, float y, float width, float height, String texture, short func) {
		
		super(x, y, width, height);
		this.game = game;
		this.setupTexture(texture);
		this.setupVertices();
		this.setFunc(func);
	}
	
	public Mesh(GLGame game, float x, float y, float width, float height, String texture, float texScaleWidth, float texScaleHeight) {
		
		super(x, y, width, height);
		this.game = game;
		this.setupTexture(texture, texScaleWidth, texScaleHeight);
		this.setupVertices();
	}

	public Mesh(GLGame game, float x, float y, float width, float height, String texture, short func, float texScaleWidth, float texScaleHeight) {
		
		super(x, y, width, height);
		this.game = game;
		this.setupTexture(texture, texScaleWidth, texScaleHeight);
		this.setupVertices();
		this.setFunc(func);
	}
	
	public void setFunc(short func) {
		
		this.func = func;
	}

	public void setupTexture(String fileName) {
		
		this.texture = new GLTexture(this.game, fileName);
	}
	
	public void setupTexture(String fileName, float scaleWidth, float scaleHeight) {
		
		this.setupTexture(fileName);
		this.texScaleWidth = scaleWidth;
		this.texScaleHeight = scaleHeight;
	}
	
	private void setupVertices() {
		
		this.vertices = new GLVertices(this.game.getGLGraphics(), 4, 6 , false, true);
		this.vertices.setVertices(new float[] {
				0,					0,	  				0, this.bounds.height / (this.texture.height * this.texScaleHeight),
				this.bounds.width,	0,					this.bounds.width / (this.texture.width * this.texScaleWidth), this.bounds.height / (this.texture.height * this.texScaleHeight),
				this.bounds.width,	this.bounds.height,	this.bounds.width / (this.texture.width * this.texScaleWidth), 0,
				0,					this.bounds.height,	0, 0 }, 0, 16);
		this.vertices.setIndices(new short[] {0, 1, 2, 0, 2, 3}, 0, 6);
	}
	
	public void draw() {
		
		GL10 gl = this.game.getGLGraphics().getGL();
		
		gl.glPushMatrix();
		gl.glTranslatef(this.position.x, this.position.y, 0);
		this.texture.bind();
		this.vertices.bind();
		this.vertices.draw(GL10.GL_TRIANGLES, 0, 6);
		this.vertices.unbind();
		gl.glPopMatrix();
	}
}

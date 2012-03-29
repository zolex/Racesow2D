package org.racenet.framework;

public abstract class TexturedShape extends GameObject {
	
	GLGame game = null;
	public GLVertices glVertices = null;
	public GLTexture texture = null;
	float texScaleWidth = 0.05f;
	float texScaleHeight = 0.05f;
	
	public TexturedShape(GLGame game, String texture, short func, float texScaleWidth, float texScaleHeight, Vector2 ... vertices) {
		
		super(vertices);
		this.game = game;
		this.setupTexture(texture, texScaleWidth, texScaleHeight);
		this.setupVertices();
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
	
	public abstract void draw();
	protected abstract void setupVertices();
}

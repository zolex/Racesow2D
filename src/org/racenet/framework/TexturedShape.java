package org.racenet.framework;

/**
 * Base Class for TexturedTriangle and TexturedRectangle
 * 
 * @author soh#zolex
 *
 */
public abstract class TexturedShape extends HudItem {
	
	GLGame game = null;
	public GLVertices glVertices = null;
	public GLTexture texture = null;
	float texScaleWidth = 0.05f;
	float texScaleHeight = 0.05f;
	float texShiftX;
	float texShiftY;
	
	/**
	 * Constructor 
	 * 
	 * @param GLGame game
	 * @param String texture
	 * @param short func
	 * @param float texScaleWidth
	 * @param float texScaleHeight
	 * @param Vectro2 ... vertices
	 */
	public TexturedShape(GLGame game, String texture, short func, float texScaleWidth, float texScaleHeight, float texShiftX, float texShiftY, Vector2 ... vertices) {
		
		super(vertices);
		this.game = game;
		this.setupTexture(texture, texScaleWidth, texScaleHeight, texShiftX, texShiftY);
		this.setupVertices();
		this.func = func;
	}
	
	/**
	 * Load the texture for the shape 
	 * 
	 * @param String fileName
	 * @param float scaleWidth
	 * @param float scaleHeight
	 */
	public void setupTexture(String fileName, float scaleWidth, float scaleHeight, float texShiftX, float texShiftY) {
		
		this.texture = new GLTexture(this.game, fileName);
		this.texScaleWidth = scaleWidth == 0 ? 0.1f : scaleWidth;
		this.texScaleHeight = scaleHeight == 0 ? 0.1f : scaleHeight;
		this.texShiftX = texShiftX;
		this.texShiftY = texShiftY;
	}
	
	/**
	 * Reload the texture
	 */
	public void reloadTexture() {
		
		if (this.texture != null) {
			
			this.texture.reload();
		}
	}
	
	/**
	 * Dispose the texture
	 */
	public void dispose() {
		
		if (this.texture != null) {
			
			this.texture.dispose();
		}
	}
	
	/**
	 * These methods must be implemented by derivatives
	 */
	public abstract void draw();
	public abstract void drawOutline();
	protected abstract void setupVertices();
}

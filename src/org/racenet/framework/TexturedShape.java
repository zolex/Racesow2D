package org.racenet.framework;

/**
 * Base Class for TexturedTriangle and TexturedRectangle
 * 
 * @author soh#zolex
 *
 */
public abstract class TexturedShape extends HudItem implements Drawable {
	
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
	public TexturedShape(String texture, short func, float texScaleWidth, float texScaleHeight, float texShiftX, float texShiftY, Vector2 ... vertices) {
		
		super(vertices);
		this.func = func;
		this.texScaleWidth = texScaleWidth == 0 ? 0.1f : texScaleWidth;
		this.texScaleHeight = texScaleHeight == 0 ? 0.1f : texScaleHeight;
		this.texShiftX = texShiftX;
		this.texShiftY = texShiftY;
		this.setupTexture(texture);
		this.setupVertices();
	}
	
	/**
	 * Constructor 
	 * 
	 * @param GLGame game
	 * @param GLTexture texture
	 * @param short func
	 * @param float texScaleWidth
	 * @param float texScaleHeight
	 * @param Vectro2 ... vertices
	 */
	public TexturedShape(GLTexture texture, short func, float texScaleWidth, float texScaleHeight, float texShiftX, float texShiftY, Vector2 ... vertices) {
		
		super(vertices);
		this.func = func;
		this.texScaleWidth = texScaleWidth == 0 ? 0.1f : texScaleWidth;
		this.texScaleHeight = texScaleHeight == 0 ? 0.1f : texScaleHeight;
		this.texShiftX = texShiftX;
		this.texShiftY = texShiftY;
		this.texture = texture;
		this.setupVertices();
	}
	
	/**
	 * Load the texture for the shape 
	 * 
	 * @param String fileName
	 * @param float scaleWidth
	 * @param float scaleHeight
	 */
	public void setupTexture(String fileName) {
		
		this.texture = new GLTexture(fileName);
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
	protected abstract void setupVertices();
}

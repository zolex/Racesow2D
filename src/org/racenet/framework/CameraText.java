package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

/**
 * A text assigned to the Camera2 class
 * 
 * @author soh#zolex
 *
 */
public class CameraText extends HudItem {

	SpriteBatcher batcher;
	GL10 gl;
	BitmapFont font;
	public String text = "empty";
	public float red = 0;
	public float green = 1;
	public float blue = 0;
	public float alpha = 1;
	public float scale = 0.075f;
	public float space = 0.06f;
	
	/**
	 * Constructor 
	 * 
	 * @param SpriteBatcher batcher
	 * @param BitmapFont font
	 * @param GL10 gl
	 * @param float cameraX
	 * @param float cameraY
	 */
	public CameraText(SpriteBatcher batcher, BitmapFont font, GL10 gl, float cameraX, float cameraY) {
		
		super(new Vector2(cameraX, cameraY));
		this.batcher = batcher;
		this.gl = gl;
		this.font = font;
	}

	@Override
	/**
	 * Draw the text
	 */
	public void draw() {
		
		this.gl.glColor4f(this.red, this.green, this.blue, this.alpha);
		this.font.draw(batcher, this.text, this.scale, this.space, this.getPosition().x, this.getPosition().y);
		this.gl.glColor4f(1, 1, 1, 1);
	}

	@Override
	/**
	 * Reload the bitmap font
	 */
	public void reloadTexture() {
		
		this.font.texture.reload();
	}
	
	@Override
	/**
	 * Get rid of the bitmap font
	 */
	public void dispose() {
		
		this.font.texture.dispose();
	}
}

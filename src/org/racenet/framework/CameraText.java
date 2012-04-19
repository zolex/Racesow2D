package org.racenet.framework;

import android.opengl.GLES10;

/**
 * A text assigned to the Camera2 class
 * 
 * @author soh#zolex
 *
 */
public class CameraText extends HudItem {

	SpriteBatcher batcher;
	BitmapFont font;
	public String text = "empty";
	public float red = 1;
	public float green = 1;
	public float blue = 1;
	public float alpha = 1;
	public float scale = 0.075f;
	public float space = 0.06f;
	
	/**
	 * Constructor 
	 * 
	 * @param SpriteBatcher batcher
	 * @param BitmapFont font
	 * @param float cameraX
	 * @param float cameraY
	 */
	public CameraText(SpriteBatcher batcher, BitmapFont font, float cameraX, float cameraY) {
		
		super(new Vector2(cameraX, cameraY));
		this.batcher = batcher;
		this.font = font;
	}

	@Override
	/**
	 * Draw the text
	 */
	public void draw() {
		
		GLES10.glColor4f(this.red, this.green, this.blue, this.alpha);
		this.font.draw(batcher, this.text, this.scale, this.space, this.vertices[0].x, this.vertices[0].y);
		GLES10.glColor4f(1, 1, 1, 1);
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

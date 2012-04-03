package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

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
	
	public CameraText(SpriteBatcher batcher, BitmapFont font, GL10 gl, float cameraX, float cameraY) {
		
		super(new Vector2(cameraX, cameraY));
		this.batcher = batcher;
		this.gl = gl;
		this.font = font;
	}

	@Override
	public void draw() {
		
		this.gl.glColor4f(this.red, this.green, this.blue, this.alpha);
		this.font.draw(batcher, this.text, this.scale, this.space, this.getPosition().x, this.getPosition().y);
		this.gl.glColor4f(1, 1, 1, 1);
	}

	@Override
	public void reloadTexture() {
		
		this.font.texture.reload();
	}
	
	@Override
	public void dispose() {
		
		this.font.texture.dispose();
	}
}

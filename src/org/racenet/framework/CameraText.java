package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

public class CameraText extends HudItem {

	SpriteBatcher batcher;
	GL10 gl;
	BitmapFont font;
	public String text = "empty";
	
	public CameraText(SpriteBatcher batcher, BitmapFont font, GL10 gl, float cameraX, float cameraY) {
		
		super(new Vector2(cameraX, cameraY));
		this.batcher = batcher;
		this.gl = gl;
		this.font = font;
	}

	public void draw() {
		
		this.gl.glColor4f(0, 1, 0, 1);
		this.font.draw(batcher, this.text, 0.075f, 0.06f, this.getPosition().x, this.getPosition().y);
		this.gl.glColor4f(1, 1, 1, 1);
	}
}

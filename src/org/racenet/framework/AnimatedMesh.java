package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

public class AnimatedMesh extends GameObject {

	GLGame game = null;
	public float animTime = 0;
	public GLVertices vertices = null;
	public Animation anim;
	float texScaleWidth = 0.05f;
	float texScaleHeight = 0.05f;
	
	public AnimatedMesh(GLGame game, float x, float y, float width, float height, float frameDuration, String ... keyFrames) {
		
		super(x, y, width, height);
		this.game = game;
		this.setupKeyFrames(frameDuration, keyFrames);
		this.setupVertices();
	}
	
	public void setupKeyFrames(float frameDuration, String ... keyFrames) {
		
		GLTexture[] frames = new GLTexture[keyFrames.length];
		for (int i = 0; i < keyFrames.length; i++) {
			
			frames[i] = new GLTexture(this.game, keyFrames[i]);
		}
		
		this.anim = new Animation(frameDuration, frames);
	}
	
	private void setupVertices() {
		
		GLTexture firstFrame = this.anim.getKeyFrame(0);
		this.vertices = new GLVertices(this.game.getGLGraphics(), 4, 6 , false, true);
		this.vertices.setVertices(new float[] {
				0,					0,	  				0, this.bounds.height / (firstFrame.height * this.texScaleHeight),
				this.bounds.width,	0,					this.bounds.width / (firstFrame.width * this.texScaleWidth), this.bounds.height / (firstFrame.height * this.texScaleHeight),
				this.bounds.width,	this.bounds.height,	this.bounds.width / (firstFrame.width * this.texScaleWidth), 0,
				0,					this.bounds.height,	0, 0 }, 0, 16);
		this.vertices.setIndices(new short[] {0, 1, 2, 0, 2, 3}, 0, 6);
	}
	
	public void draw() {
		
		GL10 gl = this.game.getGLGraphics().getGL();
		
		gl.glPushMatrix();
		gl.glTranslatef(this.position.x, this.position.y, 0);
		this.anim.getKeyFrame(animTime).bind();
		this.vertices.bind();
		this.vertices.draw(GL10.GL_TRIANGLES, 0, 6);
		this.vertices.unbind();
		gl.glPopMatrix();
	}
}

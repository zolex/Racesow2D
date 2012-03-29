package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

public class AnimatedBlock extends GameObject {

	GLGame game = null;
	public float animTime = 0;
	public GLVertices vertices = null;
	public Animation anims[];
	public int numAnims;
	public int activeAnimId = 0;
	float texScaleWidth = 0.05f;
	float texScaleHeight = 0.05f;
	
	public AnimatedBlock(GLGame game, Vector2 ... edges) {
		
		super(edges);
		this.game = game;
	}
	
	protected void setAnimations(String[] ... animations) {
		
		this.numAnims = animations.length;
		this.anims = new Animation[this.numAnims];
		for (int i = 0; i < this.numAnims; i++) {
		
			this.setupKeyFrames(0.1f, i, animations[i]);
		}
	}
	
	public void setupKeyFrames(float frameDuration, int animId, String ... keyFrames) {
		
		GLTexture[] frames = new GLTexture[keyFrames.length];
		for (int i = 0; i < keyFrames.length; i++) {
			
			frames[i] = new GLTexture(this.game, keyFrames[i]);
		}
		
		this.anims[animId] = new Animation(frameDuration, frames);
	}
	
	protected void setupVertices() {
		
		float width = this.getWidth();
		float height = this.getHeight();
		GLTexture firstFrame = this.anims[0].getKeyFrame(0); // TODO: choose proper frame
		this.vertices = new GLVertices(this.game.getGLGraphics(), 4, 6 , false, true);
		this.vertices.setVertices(new float[] {
				0,		0,	  	0, height / (firstFrame.height * this.texScaleHeight),
				width,	0,		width / (firstFrame.width * this.texScaleWidth), height / (firstFrame.height * this.texScaleHeight),
				width,	height,	width / (firstFrame.width * this.texScaleWidth), 0,
				0,		height,	0, 0 }, 0, 16);
		this.vertices.setIndices(new short[] {0, 1, 2, 0, 2, 3}, 0, 6);
	}
	
	public void draw() {
		
		GL10 gl = this.game.getGLGraphics().getGL();
		
		gl.glPushMatrix();
		gl.glTranslatef(this.getPosition().x, this.getPosition().y, 0);
		this.anims[this.activeAnimId].getKeyFrame(animTime).bind();
		this.vertices.bind();
		this.vertices.draw(GL10.GL_TRIANGLES, 0, 6);
		this.vertices.unbind();
		gl.glPopMatrix();
	}
	
	public void reloadTextures() {
		
		int length = this.anims.length;
		for (int i = 0; i < length; i++) {
			
			this.anims[i].reloadTextures();
		}
	}
	
	public void dispose() {
		
		int length = this.anims.length;
		for (int i = 0; i < length; i++) {
			
			this.anims[i].dispose();
		}
	}
}

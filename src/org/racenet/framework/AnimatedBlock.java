package org.racenet.framework;

import android.opengl.GLES10;

/**
 * Class which can render an animated rectangle
 * 
 * @author soh#zolex
 *
 */
public class AnimatedBlock extends GameObject implements Drawable {

	public float animTime = 0;
	public GLVertices vertices = null;
	public Animation anims[];
	public int numAnims;
	public int activeAnimId = 0;
	public float texScaleWidth = 0.05f;
	public float texScaleHeight = 0.05f;
	public float texShiftX = 0;
	public float texShiftY = 0;
	
	/**
	 * Constructor 
	 * 
	 * @param GLGame game
	 * @param Vector2 ... vertices
	 */
	public AnimatedBlock(Vector2 ... vertices) {
		
		super(vertices);
	}
	
	/**
	 * Set a list of animations for the ractangle
	 * 
	 * @param String[] ... animations
	 */
	public void setAnimations(AnimationPreset ... animations) {
		
		this.numAnims = animations.length;
		this.anims = new Animation[this.numAnims];
		for (int i = 0; i < this.numAnims; i++) {
			
			this.setupKeyFrames(animations[i].frameDuration, i, animations[i].keyFrames);
		}
	}
	
	/**
	 * Set a list of animations for the ractangle
	 * 
	 * @param String[] ... animations
	 */
	public void setAnimation(float duration, GLTexture ... keyFrames) {
		
		this.numAnims = 1;
		float frameDuration = duration / keyFrames.length;
		this.anims = new Animation[1];
		this.anims[0] = new Animation(frameDuration, keyFrames);
	}
	
	/**
	 * Setup the keyframes by loading the textures for the animations
	 * 
	 * @param float frameDuration
	 * @param int animId
	 * @param String ... keyFrames
	 */
	public void setupKeyFrames(float frameDuration, int animId, String ... keyFrames) {
		
		GLTexture[] frames = new GLTexture[keyFrames.length];
		for (int i = 0; i < keyFrames.length; i++) {
			
			frames[i] = new GLTexture(keyFrames[i]);
		}
		
		this.anims[animId] = new Animation(frameDuration, frames);
	}
	
	/**
	 * Prepare the vertices for the openGL renderer
	 */
	public void setupVertices() {
		
		GLTexture firstFrame = this.anims[0].getKeyFrame(0); // TODO: choose proper frame
		this.vertices = new GLVertices(4, 6 , false, true);
		this.vertices.setVertices(new float[] {
				0,			0,				-this.texShiftX, this.height / (firstFrame.height * this.texScaleHeight) + this.texShiftY,
				this.width,	0,				this.width / (firstFrame.width * this.texScaleWidth) - this.texShiftX, height / (firstFrame.height * this.texScaleHeight) + this.texShiftY,
				this.width,	this.height,	this.width / (firstFrame.width * this.texScaleWidth) - this.texShiftX, this.texShiftY,
				0,			this.height,	-this.texShiftX, this.texShiftY }, 0, 16);
		this.vertices.setIndices(new short[] {0, 1, 2, 0, 2, 3}, 0, 6);
	}
	
	/**
	 * Draw the rectangle with the current keyframe
	 */
	public void draw() {
		
		GLES10.glPushMatrix();
		GLES10.glTranslatef(this.getPosition().x, this.getPosition().y, 0);
		this.anims[this.activeAnimId].getKeyFrame(this.animTime).bind();
		this.vertices.bind();
		this.vertices.draw(GLES10.GL_TRIANGLES, 0, 6);
		this.vertices.unbind();
		GLES10.glPopMatrix();
	}
	
	/**
	 * Get rid of all textures of all animations
	 */
	public void dispose() {
		
		int length = this.anims.length;
		for (int i = 0; i < length; i++) {
			
			this.anims[i].dispose();
		}
	}

	public void reloadTexture() {
		
		int length = this.anims.length;
		for (int i = 0; i < length; i++) {
			
			this.anims[i].reloadTextures();
		}
	}
	
	public void animate(float deltaTime) {
		
		this.animTime += deltaTime;
	}
}

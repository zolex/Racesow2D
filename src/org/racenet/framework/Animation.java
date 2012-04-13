package org.racenet.framework;

/**
 * Simple class to handle GLTextute animations
 * 
 * @author soh#zolex
 *
 */
public class Animation {

	public final GLTexture[] keyFrames;
	final float frameDuration;
	
	/**
	 * Constructor
	 * 
	 * @param float frameDuration
	 * @param GLTexture ... keyFrames
	 */
	public Animation(float frameDuration, GLTexture ... keyFrames) {
		
		this.keyFrames = keyFrames;
		this.frameDuration = frameDuration;
	}
	
	/**
	 * Get the keyframe for the given time
	 * 
	 * @param float stateTime
	 * @return GLtexture
	 */
	public GLTexture getKeyFrame(float stateTime) {
		
		int frameNumber;
		if (frameDuration == 0) {
			
			frameNumber = 0;
			
		} else {
		
			frameNumber = (int)(stateTime / this.frameDuration) % keyFrames.length;
		}
		
		return keyFrames[frameNumber];
	}
	
	/**
	 * Reload all textures of the animation
	 */
	public void reloadTextures() {
		
		int length = this.keyFrames.length;
		for (int i = 0; i < length; i++) {
			
			keyFrames[i].reload();
		}
	}

	/**
	 * Get rid of all textures of the animation
	 */
	public void dispose() {
		
		int length = this.keyFrames.length;
		for (int i = 0; i < length; i++) {
			
			keyFrames[i].dispose();
		}
	}
}

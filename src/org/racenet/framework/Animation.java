package org.racenet.framework;

public class Animation {

	final GLTexture[] keyFrames;
	final float frameDuration;
	
	public Animation(float frameDuration, GLTexture ... keyFrames) {
		
		this.keyFrames = keyFrames;
		this.frameDuration = frameDuration;
	}
	
	public GLTexture getKeyFrame(float stateTime) {
		
		int frameNumber = (int)(stateTime / frameDuration);
		frameNumber = frameNumber % keyFrames.length;
		return keyFrames[frameNumber];
	}
}

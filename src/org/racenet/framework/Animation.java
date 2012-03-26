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
	
	public void reloadTextures() {
		
		int length = this.keyFrames.length;
		for (int i = 0; i < length; i++) {
			
			keyFrames[i].load();
		}
	}

	public void dispose() {
		
		int length = this.keyFrames.length;
		for (int i = 0; i < length; i++) {
			
			keyFrames[i].dispose();
		}
	}
}

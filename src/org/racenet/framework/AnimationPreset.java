package org.racenet.framework;

/**
 * Preset to easily define an annimation by
 * giving the duration and the keyframes
 * 
 * @author soh#zolex
 *
 */
public class AnimationPreset {

	public String[] keyFrames;
	public float frameDuration;
	public float duration;
	
	/**
	 * Constructor
	 * 
	 * @param float duration
	 * @param String[] keyFrames
	 */
	public AnimationPreset(float duration, String[] keyFrames) {
		
		this.duration = duration;
		this.frameDuration = duration / keyFrames.length;
		this.keyFrames = keyFrames;
	}
}

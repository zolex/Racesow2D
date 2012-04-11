package org.racenet.framework;

import android.media.SoundPool;

/**
 * Container for android sounds
 * 
 * @author soh#zolex
 *
 */
public class Sound {

	private int soundId;
	private SoundPool soundPool;
	
	/**
	 * Constructor 
	 * @param SoundPool sp
	 * @param int sid
	 */
	public Sound(SoundPool sp, int sid) {
		
		soundPool = sp;
		soundId = sid;
	}
	
	/**
	 * Play the sound at teh given volume
	 * 
	 * @param float volume
	 */
	public void play(float volume) {
		
		soundPool.play(soundId, volume, volume, 0, 0, 1);
	}

	/**
	 * Get rid of the sound
	 */
	public void dispose() {
		
		soundPool.unload(soundId);
	}	
}

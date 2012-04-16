package org.racenet.framework;

import org.racenet.racesow.models.SoundItem;
import org.racenet.racesow.threads.SoundThread;
/**
 * Container for android sounds
 * 
 * @author soh#zolex
 *
 */
public class Sound {

	private int soundId;
	private SoundThread soundThread;
	
	/**
	 * Constructor 
	 * @param SoundPool sp
	 * @param int sid
	 */
	public Sound(SoundThread soundThread, int soundID) {
		
		this.soundThread = soundThread;
		this.soundId = soundID;
	}
	
	/**
	 * Play the sound at teh given volume
	 * 
	 * @param float volume
	 */
	public void play(float volume) {
		
		try {
			this.soundThread.sounds.put(new SoundItem(this.soundId, volume));
		} catch (InterruptedException e) {}
	}

	/**
	 * Get rid of the sound
	 */
	public void dispose() {
		
		this.soundThread.unloadSound(soundId);
	}	
}

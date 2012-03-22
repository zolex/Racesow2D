package org.racenet.framework;

import org.racenet.framework.interfaces.Sound;

import android.media.SoundPool;

public class AndroidSound implements Sound {

	private int soundId;
	private SoundPool soundPool;
	
	public AndroidSound(SoundPool sp, int sid) {
		
		soundPool = sp;
		soundId = sid;
	}
	
	public void play(float volume) {
		
		soundPool.play(soundId, volume, volume, 0, 0, 1);
	}

	public void dispose() {
		
		soundPool.unload(soundId);
	}

	
}

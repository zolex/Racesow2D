package org.racenet.framework;

import java.io.IOException;

import org.racenet.framework.interfaces.Audio;
import org.racenet.framework.interfaces.Music;
import org.racenet.framework.interfaces.Sound;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

public class AndroidAudio implements Audio {

	public static final int MAX_SIMULTANEOUS_SOUNDS = 20;
	public static final int SOURCE_QUALITY = 0;
	
	private AssetManager assetManager;
	private SoundPool soundPool;
	
	public AndroidAudio(Activity activity) {
		
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		assetManager = activity.getAssets();
		soundPool = new SoundPool(MAX_SIMULTANEOUS_SOUNDS, AudioManager.STREAM_MUSIC, SOURCE_QUALITY);
	}
	
	public Music newMusic(String fileName) {
		
		try {
			
			AssetFileDescriptor assetDescriptor = assetManager.openFd(fileName);
			return new AndroidMusic(assetDescriptor);
			
		} catch (IOException e) {
			
			throw new RuntimeException("Could not load music '" + fileName + "'");
		}
	}

	public Sound newSound(String fileName) {
		
		try {
			
			AssetFileDescriptor assetDescriptor = assetManager.openFd(fileName);
			int soundId = soundPool.load(assetDescriptor, 0);
			return new AndroidSound(soundPool, soundId);
			
		} catch(IOException e) {
			
			throw new RuntimeException("Could not load sound '" + fileName + "'");
		}
	}
}

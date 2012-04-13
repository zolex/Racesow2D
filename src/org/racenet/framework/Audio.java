package org.racenet.framework;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Container to handle android audio (music and sounds)
 * 
 * @author soh#zolex
 *
 */
public class Audio {

	public static final int MAX_SIMULTANEOUS_SOUNDS = 20;
	public static final int SOURCE_QUALITY = 0;
	
	private static Audio __instance;
	private AssetManager assetManager;
	private SoundPool soundPool;
	
	/**
	 * Setup the singleton instance
	 * 
	 * @param Activity activity
	 */
	public static void setupInstance(Activity activity) {
		
		if (__instance == null) {
			
			__instance = new Audio(activity);
		}
	}
	
	/**
	 * Get the singleton instance
	 * 
	 * @return Audio
	 */
	public static Audio getInstance() {
		
		return __instance;
	}
	
	/**
	 * Constructor
	 * 
	 * @param Activity activity
	 */
	private Audio(Activity activity) {
		
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		assetManager = activity.getAssets();
		soundPool = new SoundPool(MAX_SIMULTANEOUS_SOUNDS, AudioManager.STREAM_MUSIC, SOURCE_QUALITY);
	}
	
	/**
	 * Create a new music object
	 * 
	 * @param String fileName
	 * @return Music
	 */
	public Music newMusic(String fileName) {
		
		try {
			
			AssetFileDescriptor assetDescriptor = assetManager.openFd(fileName);
			return new Music(assetDescriptor);
			
		} catch (IOException e) {
			
			throw new RuntimeException("Could not load music '" + fileName + "'");
		}
	}

	/**
	 * Create a new sound
	 * 
	 * @param String fileName
	 * @return Sound
	 */
	public Sound newSound(String fileName) {
		
		try {
			
			AssetFileDescriptor assetDescriptor = assetManager.openFd(fileName);
			int soundId = soundPool.load(assetDescriptor, 0);
			return new Sound(soundPool, soundId);
			
		} catch(IOException e) {
			
			throw new RuntimeException("Could not load sound '" + fileName + "'");
		}
	}
}

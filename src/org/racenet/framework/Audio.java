package org.racenet.framework;

import java.io.IOException;

import org.racenet.racesow.models.SoundItem;
import org.racenet.racesow.threads.SoundThread;

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
	public SoundThread thread;
	
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
	 * Stop the soundThread
	 */
	public void stopThread() {
		
		try {
			this.thread.sounds.put(new SoundItem(true));
		} catch (InterruptedException e) {}
	}
	
	/**
	 * Constructor
	 * 
	 * @param Activity activity
	 */
	private Audio(Activity activity) {
		
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.assetManager = activity.getAssets();
		this.soundPool = new SoundPool(MAX_SIMULTANEOUS_SOUNDS, AudioManager.STREAM_MUSIC, SOURCE_QUALITY);
		this.thread = new SoundThread(this.soundPool);
		this.thread.start();
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
			return new Sound(this.thread, soundId);
			
		} catch(IOException e) {
			
			throw new RuntimeException("Could not load sound '" + fileName + "'");
		}
	}
}

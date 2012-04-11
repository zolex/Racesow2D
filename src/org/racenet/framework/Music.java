package org.racenet.framework;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

/**
 * Container for android music
 * 
 * @author soh#zolex
 *
 */
public class Music implements OnCompletionListener {

	private MediaPlayer mediaPlayer;
	private boolean isPrepared = false;
	
	/**
	 * Constructor 
	 * 
	 * @param AssetFileDescriptor assetDescriptor
	 */
	public Music(AssetFileDescriptor assetDescriptor) {
		
		mediaPlayer = new MediaPlayer();
		try {
			
			mediaPlayer.setDataSource(assetDescriptor.getFileDescriptor(),
					assetDescriptor.getStartOffset(),
					assetDescriptor.getLength());
			mediaPlayer.prepare();
			isPrepared = true;
			mediaPlayer.setOnCompletionListener(this);
			
		} catch(Exception e) {
			
			throw new RuntimeException("Could not load music");
		}
	}
	
	/**
	 * Play the music 
	 */
	public void play() {
		
		if (isPlaying()) {
			
			return;
		}
		
		try {
			
			synchronized(this) {
				
				if (!isPrepared) {
					
					mediaPlayer.prepare();
				}
				
				mediaPlayer.start();
			}
			
		} catch(IllegalStateException e) {
			
			e.printStackTrace();
			
		} catch(IOException e) {
			
			e.printStackTrace();
		}
	}

	/**
	 * Stop the music
	 */
	public void stop() {
		
		mediaPlayer.stop();
		synchronized (this) {
			
			isPrepared = false;
		}
	}

	/**
	 * Pause the music
	 */
	public void pause() {
		
		mediaPlayer.pause();
	}

	/**
	 * Set if we want to repeat the music
	 * 
	 * @param boolean looping
	 */
	public void setLooping(boolean looping) {
		
		mediaPlayer.setLooping(looping);
	}

	/**
	 * The the volume of the music
	 * 
	 * @param float volume
	 */
	public void setVolume(float volume) {
		
		mediaPlayer.setVolume(volume, volume);
	}

	/**
	 * See if the music is playing
	 * 
	 * @return boolean
	 */
	public boolean isPlaying() {
		
		return mediaPlayer.isPlaying();
	}

	/**
	 * See if the music is stopped
	 * 
	 * @return boolean
	 */
	public boolean isStopped() {
		
		return !isPrepared;
	}

	/**
	 * See if the music is repeating
	 * 
	 * @return boolean
	 */
	public boolean isLooping() {

		return mediaPlayer.isLooping();
	}

	/**
	 * Get rid of the music
	 */
	public void dispose() {
		
		if (mediaPlayer.isPlaying()) {
			
			mediaPlayer.stop();
		}
		
		mediaPlayer.release();
	}

	/**
	 * Called internal
	 * 
	 * @param MediaPlayer mediaPlayer
	 */
	public void onCompletion(MediaPlayer mediaPlayer) {
		
		synchronized (this) {
			
			isPrepared = false;
		}
	}
}

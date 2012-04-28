package org.racenet.racesow.threads;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.racenet.framework.Audio;
import org.racenet.framework.Music;
import org.racenet.racesow.models.SoundItem;

import android.media.SoundPool;

/**
 * Thread for playing sounds
 * 
 * @author soh#zolex
 *
 */
public class MusicThread extends Thread {
	
	public Music music = null;
	public boolean stop = false;
	
	/**
	 * Constructor
	 * 
	 * @param FileIO fileIO
	 * @param String map
	 */
	public MusicThread() {

		music = Audio.getInstance().newMusic("sounds/menu_1.ogg");
		music.setLooping(true);
		music.setVolume(0.3f);
	}
	
	@Override
	/**
	 * Wait for sounds to play
	 */
    public void run() {
		
		music.play();
		
		while(!this.stop) {
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		music.dispose();
		music = null;
    }
}

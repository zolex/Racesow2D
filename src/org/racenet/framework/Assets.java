package org.racenet.framework;

import java.util.HashMap;

import org.racenet.framework.interfaces.Pixmap;
import org.racenet.framework.interfaces.Sound;

/**
 * Container for pre-loading assets
 * 
 * @author al
 *
 */
public class Assets {

	private static Assets __instance = null;
	
	private HashMap<String, Pixmap> pixmaps = new HashMap<String, Pixmap>();
	private HashMap<String, Sound> sounds = new HashMap<String, Sound>();
	
	/**
	 * Constructor
	 */
	private Assets() {
		
	}
	
	/**
	 * Singleton getter
	 * 
	 * @return Assets
	 */
	public static Assets getInstance() {
		
		if (__instance == null) {
			
			__instance = new Assets();
		}
		
		return __instance;
	}
	
	/**
	 * Add a Pixmap
	 * 
	 * @param String name
	 * @param Pixmap p
	 */
	public void addPixmap(String name, Pixmap p) {
		
		pixmaps.put(name, p);
	}
	
	/**
	 * Get a Pixmap
	 * 
	 * @param String name
	 * @return Pixmap
	 */
	public Pixmap getPixmap(String name) {
		
		return pixmaps.get(name);
	}
	
	/**
	 * Add a sound
	 * 
	 * @param String name
	 * @param Sound s
	 */
	public void addSound(String name, Sound s) {
		
		sounds.put(name, s);
	}
	
	/**
	 * Get a sound
	 * 
	 * @param String name
	 * @return Sound
	 */
	public Sound getSound(String name) {
		
		return sounds.get(name);
	}
}

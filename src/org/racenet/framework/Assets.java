package org.racenet.framework;

import java.util.HashMap;

import org.racenet.framework.interfaces.Pixmap;
import org.racenet.framework.interfaces.Sound;

public class Assets {

	private static Assets __instance = null;
	
	private HashMap<String, Pixmap> pixmaps = new HashMap<String, Pixmap>();
	private HashMap<String, Sound> sounds = new HashMap<String, Sound>();
	
	private Assets() {
		
	}
	
	public static Assets getInstance() {
		
		if (__instance == null) {
			
			__instance = new Assets();
		}
		
		return __instance;
	}
	
	public void addPixmap(String name, Pixmap p) {
		
		pixmaps.put(name, p);
	}
	
	public Pixmap getPixmap(String name) {
		
		return pixmaps.get(name);
	}
	
	public void addSound(String name, Sound s) {
		
		sounds.put(name, s);
	}
	
	public Sound getSound(String name) {
		
		return sounds.get(name);
	}
}

package org.racenet.racesow;

import java.util.Map.Entry;

import org.racenet.racesow.models.DemoKeyFrame;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentSkipListMap;

/**
 * Class to parse r2d demo files
 * 
 * @author so#zolex
 *
 */
public class DemoParser {

	String map;
	ConcurrentSkipListMap demoParts = new ConcurrentSkipListMap();
	
	/**
	 * Parse the given demo string
	 * 
	 * @param String demo
	 */
	public void parse(String demo) {
		
		String[] meta = demo.split("/");
		this.map = meta[0];
		String[] parts = meta[1].split(";");
		for (int i = 0; i < parts.length - 1; i++) {
			
			String[] part = parts[i].split(":");
			Float time = Float.parseFloat(part[0]);
			String[] info = part[1].split(",");
			
			DemoKeyFrame f = new DemoKeyFrame();
			try { f.playerPosition.x = Float.parseFloat(info[0]); } catch (NumberFormatException e) {}
			try { f.playerPosition.y = Float.parseFloat(info[1]); } catch (NumberFormatException e) {}
			try { f.playerAnimation = Integer.parseInt(info[2]); } catch (NumberFormatException e) {}
			try { f.playerSpeed = Integer.parseInt(info[3]); } catch (NumberFormatException e) {}
			try { f.mapTime = Float.parseFloat(info[4]); } catch (NumberFormatException e) {}
			try { f.playerSound = Integer.parseInt(info[5]); } catch (NumberFormatException e) {}
			
			if (info.length > 6 && !info[6].equals("")) {
				
				String[] decal = info[6].split("#");
				f.decalType = decal[0];
				f.decalX = Float.parseFloat(decal[1]);
				f.decalY = Float.parseFloat(decal[2]);
				f.decalTime = Float.parseFloat(decal[3]);
			}
			
			this.demoParts.put(time, f);
		}
	}
	
	/**
	 * Get the best matching keyframe for the given frameTime
	 * 
	 * @param float time
	 * @return DemoKeyFrame
	 */
	public DemoKeyFrame getKeyFrame(float time) {
		
		@SuppressWarnings("unchecked")
		Entry<Float, DemoKeyFrame> frame = this.demoParts.lowerEntry(time);
		if (frame != null) {
			
			return frame.getValue();
		}
		
		return null;
	}
}

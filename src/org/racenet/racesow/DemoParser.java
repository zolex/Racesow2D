package org.racenet.racesow;

import java.util.HashMap;

import org.racenet.racesow.models.DemoKeyFrame;

public class DemoParser {

	String map;
	HashMap<Float, DemoKeyFrame> demoParts = new HashMap<Float, DemoKeyFrame>();
	
	public void parse(String demo) {
		
		String[] meta = demo.split("/");
		this.map = meta[0];
		String[] parts = meta[1].split(";");
		for (int i = 0; i < parts.length - 1; i++) {
			
			String[] part = parts[i].split(":");
			Float time = Float.parseFloat(part[0]);
			String[] info = part[1].split(",");
			
			DemoKeyFrame f = new DemoKeyFrame();
			f.playerPosition.x = Float.parseFloat(info[0]);
			f.playerPosition.y = Float.parseFloat(info[1]);
			f.playerAnimation = Integer.parseInt(info[2]);
			f.playerAnimDuration = Float.parseFloat(info[3]);
			f.playerSound = Integer.parseInt(info[4]);
			
			if (info.length > 5 && !info[5].equals("")) {
				
				String[] decal = info[5].split("#");
				f.decalType = decal[0];
				f.decalX = Float.parseFloat(decal[1]);
				f.decalY = Float.parseFloat(decal[2]);
				f.decalTime = Float.parseFloat(decal[3]);
			}
			
			this.demoParts.put(time, f);
		}
	}
	
	public DemoKeyFrame getKeyFrame(float time) {
		
		if (this.demoParts.containsKey(time)) {
				
			return this.demoParts.get(time);
		}
		
		return null;
	}
}

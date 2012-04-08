package org.racenet.racesow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.racenet.racesow.models.DemoKeyFrame;

import android.util.Log;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentSkipListMap;

public class DemoParser {

	String map;
	ConcurrentSkipListMap demoParts = new ConcurrentSkipListMap();
	
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
			try { f.playerAnimDuration = Float.parseFloat(info[3]); } catch (NumberFormatException e) {}
			try { f.playerSpeed = Integer.parseInt(info[4]); } catch (NumberFormatException e) {}
			try { f.mapTime = Float.parseFloat(info[5]); } catch (NumberFormatException e) {}
			try { f.playerSound = Integer.parseInt(info[6]); } catch (NumberFormatException e) {}
			
			if (info.length > 7 && !info[7].equals("")) {
				
				String[] decal = info[7].split("#");
				f.decalType = decal[0];
				f.decalX = Float.parseFloat(decal[1]);
				f.decalY = Float.parseFloat(decal[2]);
				f.decalTime = Float.parseFloat(decal[3]);
			}
			
			this.demoParts.put(time, f);
		}
	}
	
	public DemoKeyFrame getKeyFrame(float time) {
		
		Entry<Float, DemoKeyFrame> frame = this.demoParts.lowerEntry(time);
		if (frame != null) {
			
			Log.d("DEBUG", "wanted " + time + " got " + frame.getKey() + " diff " + (time - frame.getKey()));
			return frame.getValue();
		}
		
		return null;
	}
}

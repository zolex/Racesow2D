package org.racenet.racesow.threads;

import org.racenet.racesow.models.Database;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Task to submit scores which were done while
 * the phone was offline to the Racesow2D API
 * 
 * @author soh#zolex
 *
 */
public class InternalScoresThread extends Thread {

	private Handler handler;
	private String map;
	private String player;
	private float time;
	
	/**
	 * Constructor
	 * 
	 * @param String map
	 * @param String player
	 * @param float time
	 * @param Handler handler
	 */
	public InternalScoresThread(String map, String player, float time, Handler handler) {
		
		this.handler = handler;
		this.map = map;
		this.player = player;
		this.time = time;
	}
	
	@Override
	/**
	 * Adds the race to the database
	 */
    public void run() {         

	    Database db = Database.getInstance();
	    float bestTime = db.getBestTime(this.map);
	    long id = db.addRace(this.map, this.player, this.time);
	    
	    Message msg = new Message();
	    Bundle b = new Bundle();
	    b.putLong("id", id);
	    b.putBoolean("record", this.time < bestTime || bestTime == 0);
	    msg.setData(b);
        this.handler.dispatchMessage(msg);
    }
}

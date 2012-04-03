package org.racenet.racesow.threads;

import org.racenet.racesow.models.Database;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class InternalScoresThread extends Thread {

	private Handler handler;
	private String map;
	private String player;
	private float time;
	private Context context;
	
	public InternalScoresThread(Context context, String map, String player, float time, Handler handler) {
		
		this.context = context;
		this.handler = handler;
		this.map = map;
		this.player = player;
		this.time = time;
	}
	
	@Override
    public void run() {         

	    Database db = Database.getInstance(this.context);
	    float bestTime = db.getBestTime(this.map);
	    db.addRace(this.map, this.player, this.time);
	    
	    Message msg = new Message();
	    Bundle b = new Bundle();
	    b.putBoolean("record", this.time < bestTime || bestTime == 0);
	    msg.setData(b);
        this.handler.dispatchMessage(msg);
    }
}

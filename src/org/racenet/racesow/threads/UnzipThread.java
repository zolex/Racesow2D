package org.racenet.racesow.threads;

import org.racenet.framework.Unzipper;

import android.os.Handler;

public class UnzipThread extends Thread {

	private Handler handler;
	private String source;
	private String destination;
	
	public UnzipThread(String source, String destination, Handler handler) {
		
		this.handler = handler;
		this.source = source;
		this.destination = destination;
	}
	
	@Override
    public void run() {         

	    Unzipper.unzip(this.source, this.destination, this.handler);
    }
}

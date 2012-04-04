package org.racenet.racesow.threads;

import org.racenet.framework.Unzipper;

import android.os.Handler;

/**
 * Thread to unzip a file on the background
 * 
 * @author soh#zolex
 *
 */
public class UnzipThread extends Thread {

	private Handler handler;
	private String source;
	private String destination;
	
	/**
	 * Constructor
	 * 
	 * @param String source
	 * @param String destination
	 * @param Handler handler
	 */
	public UnzipThread(String source, String destination, Handler handler) {
		
		this.handler = handler;
		this.source = source;
		this.destination = destination;
	}
	
	@Override
	/**
	 * Unzip the file to the given destination
	 */
    public void run() {         

	    Unzipper.unzip(this.source, this.destination, this.handler);
    }
}

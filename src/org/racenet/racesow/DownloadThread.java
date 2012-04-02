package org.racenet.racesow;

import org.racenet.framework.Downloader;

import android.os.Handler;

public class DownloadThread extends Thread {

	private Handler handler;
	private String source;
	private String destination;
	
	public DownloadThread(String source, String destination, Handler h) {
		
		this.handler = h;
		this.source = source;
		this.destination = destination;
	}
	
	@Override
    public void run() {         

	    Downloader.download(this.source, this.destination, this.handler);
    }
}

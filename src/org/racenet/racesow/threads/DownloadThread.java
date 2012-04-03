package org.racenet.racesow.threads;

import org.racenet.framework.Downloader;

import android.os.Handler;

public class DownloadThread extends Thread {

	private Handler handler;
	private String source;
	private String destination;
	private Downloader downloader;
	
	public DownloadThread(String source, String destination, Handler h) {
		
		this.handler = h;
		this.source = source;
		this.destination = destination;
		this.downloader = new Downloader();
	}
	
	@Override
    public void run() {         

	    this.downloader.download(this.source, this.destination, this.handler);
    }
	
	public void stopDownload() {
		
		this.downloader.cancelDownload();
		this.stop();
	}
}

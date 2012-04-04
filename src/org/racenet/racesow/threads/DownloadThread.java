package org.racenet.racesow.threads;

import org.racenet.framework.Downloader;

import android.os.Handler;

/**
 * Thread for downloading a file from web inthe background
 * 
 * @author soh#zolex
 *
 */
public class DownloadThread extends Thread {

	private Handler handler;
	private String source;
	private String destination;
	private Downloader downloader;
	
	/**
	 * Constructor
	 * 
	 * @param String source
	 * @param String destination
	 * @param Handler h
	 */
	public DownloadThread(String source, String destination, Handler h) {
		
		this.handler = h;
		this.source = source;
		this.destination = destination;
		this.downloader = new Downloader();
	}
	
	@Override
	/**
	 * Start the download
	 */
    public void run() {         

	    this.downloader.download(this.source, this.destination, this.handler);
    }
	
	/**
	 * Stop the download
	 */
	public void stopDownload() {
		
		this.downloader.cancelDownload();
		this.stop();
	}
}

package org.racenet.framework;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
 
import org.apache.http.util.ByteArrayBuffer;
 
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
 
/**
 * Class to download files from web and
 * report the progress to a handler
 * 
 * @author soh#zolex
 *
 */
public class Downloader {
 
	private boolean cancelDownload = false;
	
	/**
	 * Cancel a started download
	 */
	public void cancelDownload() {
		
		this.cancelDownload = true;
	}
	
	/**
	 * Constructor
	 * 
	 * @param String source
	 * @param String destination
	 * @param Handler progress
	 */
	public void download(String source, String destination, Handler progress) {
		
		try {
			
			URL url = new URL(source);
			
			int slashIndex = source.lastIndexOf('/');
			destination += source.substring(slashIndex + 1);
			
			File file = new File(destination);

			// open the web connection
			URLConnection ucon = url.openConnection();
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			float totalBytes = ucon.getContentLength();
			float bytesRead = 0;
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			int updateInterval = 0;
			// download the file
			while (!this.cancelDownload && (current = bis.read()) != -1) {
				
				bytesRead += current;
				// don't report the progress too often...
				if (updateInterval++ == 4096) {
					
					// report the progress
					updateInterval = 0;
					int percent = (int)(((float)bytesRead / 128) / (float)totalBytes * 100);
					if (percent < 100) {
					
						Message msg = new Message();
					    Bundle b = new Bundle();
					    b.putInt("code", 1);
					    b.putInt("percent", percent);
					    msg.setData(b);
				        progress.sendMessage(msg);
					}
				}
				
				baf.append((byte) current);
			}
			
			if (this.cancelDownload) {
				
				this.cancelDownload = false;
				baf.clear();
				
			} else {

				// write the local file
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(baf.toByteArray());
				fos.close();
				
				// report the final progress
				Message msg = new Message();
			    Bundle b = new Bundle();
			    b.putInt("code", 1);
			    b.putInt("percent", 100);
			    msg.setData(b);
		        progress.sendMessage(msg);
			}

		} catch (IOException e) {
			
			// report an error
			Message msg = new Message();
		    Bundle b = new Bundle();
		    b.putInt("code", 2);
		    b.putString("message", "Please try again.");
		    msg.setData(b);
	        progress.sendMessage(msg);
		}
	}
}
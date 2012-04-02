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
 
public class Downloader {
 
	public static void download(String source, String destination, Handler progress) {
		
		try {
			
			URL url = new URL(source);
			
			int slashIndex = source.lastIndexOf('/');
			destination += source.substring(slashIndex + 1);
			
			File file = new File(destination);

			URLConnection ucon = url.openConnection();
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			float totalBytes = ucon.getContentLength();
			float bytesRead = 0;
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			int updateInterval = 0;
			while ((current = bis.read()) != -1) {
				
				bytesRead += current;
				if (updateInterval++ == 4096) {
					
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

			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.close();
			
			Message msg = new Message();
		    Bundle b = new Bundle();
		    b.putInt("code", 1);
		    b.putInt("percent", 100);
		    msg.setData(b);
	        progress.sendMessage(msg);

		} catch (IOException e) {
			
			Message msg = new Message();
		    Bundle b = new Bundle();
		    b.putInt("code", 2);
		    b.putString("message", "Please try again.");
		    msg.setData(b);
	        progress.sendMessage(msg);
		}
	}
}
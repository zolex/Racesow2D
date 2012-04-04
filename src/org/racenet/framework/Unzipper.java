package org.racenet.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Class to unzip a file
 * 
 * @author soh#zolex
 *
 */
public class Unzipper {
	
	/**
	 * Check if a directory exists and create it
	 * if required
	 * 
	 * @param String dir
	 */
	private static void checkDir(String dir) { 
		 
		File f = new File(dir); 
		if (!f.isDirectory()) { 
		
			f.mkdirs(); 
		} 
	} 
	
	/**
	 * Unzip the file to the given destination and
	 * report the progress to the provided handler
	 * 
	 * @param String zipFile
	 * @param String destination
	 * @param handler progress
	 * @return boolean
	 */
	public static boolean unzip(String zipFile, String destination, Handler progress) {
		
		checkDir(destination);
		
		FileInputStream fin;
		try {
			
			// unzip the file
			fin = new FileInputStream(zipFile);
			int totalBytes = fin.available();
			int bytesRead = 0;
			ZipInputStream zin = new ZipInputStream(fin);			
			ZipEntry ze = null; 
			while ((ze = zin.getNextEntry()) != null) {
				
				// report the progress to the handler
				bytesRead = totalBytes - fin.available();
				int percent = (int)((float)bytesRead / (float)totalBytes * 100);
				Message msg = new Message();
			    Bundle b = new Bundle();
			    b.putInt("code", 1);
			    b.putInt("percent", percent);
			    msg.setData(b);
		        progress.sendMessage(msg);
				
				if(ze.isDirectory()) { 
					
					checkDir(destination + ze.getName()); 
					 
				} else { 

					FileOutputStream fout = new FileOutputStream(destination + ze.getName()); 
					for (int c = zin.read(); c != -1; c = zin.read()) { 
						
						fout.write(c); 
					}
					
					zin.closeEntry(); 
					fout.close(); 
				} 
			} 
			
			// report final progress
			Message msg = new Message();
		    Bundle b = new Bundle();
		    b.putInt("code", 1);
		    b.putInt("percent", 100);
		    msg.setData(b);
	        progress.sendMessage(msg);
			
			zin.close();
			
		// send a failure message
		} catch (FileNotFoundException e) {
			
			Message msg = new Message();
		    Bundle b = new Bundle();
		    b.putInt("code", 2);
		    b.putString("message", "Please try again.");
		    msg.setData(b);
	        progress.sendMessage(msg);
			return false;
			
		// send a failure message
		} catch (IOException e) {
			
			Message msg = new Message();
		    Bundle b = new Bundle();
		    b.putInt("code", 2);
		    b.putString("message", "Please try again.");
		    msg.setData(b);
	        progress.sendMessage(msg);
			return false;
		}
		
		return true;
	}
}

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

public class Unzipper {
	
	 private static void checkDir(String dir) { 
		 
		 File f = new File(dir); 
		 if (!f.isDirectory()) { 
			 
			 f.mkdirs(); 
		 } 
	 } 
	
	public static boolean unzip(String zipFile, String destination, Handler progress) {
		
		checkDir(destination);
		
		FileInputStream fin;
		try {
			
			fin = new FileInputStream(zipFile);
			int totalBytes = fin.available();
			int bytesRead = 0;
			ZipInputStream zin = new ZipInputStream(fin);			
			ZipEntry ze = null; 
			while ((ze = zin.getNextEntry()) != null) {
				
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
			
			Message msg = new Message();
		    Bundle b = new Bundle();
		    b.putInt("code", 1);
		    b.putInt("percent", 100);
		    msg.setData(b);
	        progress.sendMessage(msg);
			
			zin.close();
			
		} catch (FileNotFoundException e) {
			
			Message msg = new Message();
		    Bundle b = new Bundle();
		    b.putInt("code", 2);
		    b.putString("message", "Please try again.");
		    msg.setData(b);
	        progress.sendMessage(msg);
			return false;
			
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

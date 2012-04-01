package org.racenet.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;

public class Unzipper {
	
	String zipFile;
	String destination;
	
	public Unzipper(String zipFile, String destination) {
		
		this.zipFile = zipFile;
		this.destination = destination;
	}
	
	 private void checkDir(String dir) { 
		 
		 File f = new File(this.destination + dir); 
		 if (!f.isDirectory()) { 
			 
			 f.mkdirs(); 
		 } 
	 } 
	
	public boolean unzip() {
		
		this.checkDir(this.destination);
		
		FileInputStream fin;
		try {
			
			fin = new FileInputStream(zipFile);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null; 
			while ((ze = zin.getNextEntry()) != null) {
				
				if(ze.isDirectory()) { 
					
					 this.checkDir(ze.getName()); 
				 
				} else { 

					FileOutputStream fout = new FileOutputStream(this.destination + ze.getName()); 
					for (int c = zin.read(); c != -1; c = zin.read()) { 
						
						fout.write(c); 
					}
					
					zin.closeEntry(); 
					fout.close(); 
				} 
			} 
			
			zin.close();
			
		} catch (FileNotFoundException e) {
			
			Log.d("DEBUG", "unzip error: " + e.getMessage());
			return false;
			
		} catch (IOException e) {
			
			Log.d("DEBUG", "unzip error: " + e.getMessage());
			return false;
		}
		
		return true;
	}
}

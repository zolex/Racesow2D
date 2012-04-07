package org.racenet.racesow.threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.racenet.framework.interfaces.FileIO;
import org.racenet.racesow.models.Database;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Thread to save data to the internal scores database
 * 
 * @author soh#zolex
 *
 */
public class WriteDemoThread extends Thread {
	
	private FileIO fileIO;
	private String map;
	private String demo;
	
	/**
	 * Constructor
	 * 
	 * @param Context context
	 * @param String map
	 * @param String player
	 * @param float time
	 * @param Handler handler
	 */
	public WriteDemoThread(FileIO fileIO, String map, String demo) {
		
		this.fileIO = fileIO;
		this.map = map;
		this.demo = demo;
	}
	
	@Override
	/**
	 * Adds the race to the database
	 */
    public void run() {         

		String demoFolder = "racesow" + File.separator + "demos" + File.separator;
		String fileName = demoFolder + this.map + "_" + (int)(System.nanoTime() / 1000000000.0f) + ".r2d";
		try {
			
			this.fileIO.createDirectory(demoFolder);
			FileOutputStream fos = (FileOutputStream)this.fileIO.writeFile(fileName);
			fos.write(this.demo.getBytes());
			fos.close();
			
		} catch (IOException e) {
		}
    }
}

package org.racenet.racesow.threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
public class DemoRecorderThread extends Thread {
	
	private FileIO fileIO;
	private String map;
	public BlockingQueue<String> demoParts = new LinkedBlockingQueue<String>();
	private FileOutputStream fos;
	
	/**
	 * Constructor
	 * 
	 * @param Context context
	 * @param String map
	 * @param String player
	 * @param float time
	 * @param Handler handler
	 */
	public DemoRecorderThread(FileIO fileIO, String map) {
		
		this.fileIO = fileIO;
		this.map = map;
		
		String demoFolder = "racesow" + File.separator + "demos" + File.separator;
		String fileName = demoFolder + this.map + "_" + (int)(System.nanoTime() / 1000000000.0f) + ".r2d";
		this.fileIO.createDirectory(demoFolder);
		try {
			this.fos = (FileOutputStream)this.fileIO.writeFile(fileName);
		} catch (IOException e) {
		}
	}
	
	@Override
	/**
	 * Adds the race to the database
	 */
    public void run() {         
		
		try {
			
			String item;
			while ((item = this.demoParts.take()) != "shutdown") {
				
				fos.write(item.getBytes());
			}
			
			fos.close();
			
		} catch (InterruptedException e) {

		} catch (IOException e) {
			
		}
    }
}

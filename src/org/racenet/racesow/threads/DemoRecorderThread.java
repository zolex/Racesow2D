package org.racenet.racesow.threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.racenet.framework.interfaces.FileIO;

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
	private String demoFolder;
	private String fileName;
	public boolean stop = false;
	
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
		
		this.demoFolder = "racesow" + File.separator + "demos" + File.separator;
		this.fileIO.createDirectory(demoFolder);
		
		this.newDemo();
	}
	
	public void cancelDemo() {
		
		if (this.fos != null) {
			
			try {
				this.fos.close();
				this.fos = null;
			} catch (IOException e) {
			}
		}
		
		if (this.fileName != null) {
			
			try {
				this.fileIO.deleteFile(this.fileName);
			} catch (IOException e) {
			}
		}
	}
	
	public void newDemo() {
		
		this.fileName = this.demoFolder + this.map + "_" + (int)(System.nanoTime() / 1000000000.0f) + ".r2d";
		try {
			this.fos = (FileOutputStream)this.fileIO.writeFile(this.fileName);
		} catch (IOException e) {
		}
		
		try {
			this.demoParts.put(this.map + "/");
		} catch (InterruptedException e) {
		}
	}
	
	@Override
	/**
	 * Adds the race to the database
	 */
    public void run() {         
		
		try {
			
			String item;
			while (!this.stop) {
				
				item = this.demoParts.take();
				if (item == "save-demo") {
					
					if (this.fos != null) {
						
						this.fos.close();
						this.fos = null;
						this.fileName = null;
					}
					
				} else {
					
					if (this.fos != null) {
						
						this.fos.write(item.getBytes());
					}
				}
			}
			
		} catch (InterruptedException e) {
		} catch (IOException e) {
		}
    }
}

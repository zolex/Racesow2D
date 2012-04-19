package org.racenet.racesow.threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.racenet.framework.FileIO;
import org.racenet.racesow.models.DemoKeyFrame;

/**
 * Thread to save the demo-data while playing
 * 
 * @author soh#zolex
 *
 */
public class DemoRecorderThread extends Thread {
	
	private String map;
	public BlockingQueue<DemoKeyFrame> demoParts = new LinkedBlockingQueue<DemoKeyFrame>();
	private FileOutputStream fos;
	private String demoFolder;
	private String fileName;
	public boolean stop = false;
	
	/**
	 * Constructor
	 * 
	 * @param FileIO fileIO
	 * @param String map
	 */
	public DemoRecorderThread(String map) {

		this.map = map;
		
		this.demoFolder = "racesow" + File.separator + "demos" + File.separator;
		FileIO.getInstance().createDirectory(demoFolder);
		
		this.newDemo();
	}
	
	/**
	 * Stop the recording of a demo and delete the file
	 */
	public void cancelDemo() {
		
		if (this.fos != null) {
			
			try {
				this.fos.close();
				this.fos = null;
			} catch (IOException e) {
			}
		}
		
		if (this.fileName != null) {
			
			FileIO.getInstance().deleteFile(this.fileName);
		}
	}
	
	/**
	 * Start recording a new demo
	 */
	public void newDemo() {
		
		String date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
		this.fileName = this.demoFolder + this.map.replace(".xml", "") + "_" + date + ".r2d";
		try {
			this.fos = (FileOutputStream)FileIO.getInstance().writeFile(this.fileName);
		} catch (IOException e) {
		}
		
		try {
			DemoKeyFrame frame = new DemoKeyFrame();
			frame.meta = this.map + "/";
			frame.action = DemoKeyFrame.ACTION_META;
			this.demoParts.put(frame);
		} catch (InterruptedException e) {
		}
	}
	
	@Override
	/**
	 * Keep writing to the demofile
	 */
    public void run() {         
		
		try {
			
			DemoKeyFrame frame;
			while (!this.stop) {
				
				frame = this.demoParts.take();
				if (frame.action == DemoKeyFrame.ACTION_SAVE) {
					
					if (this.fos != null) {
						
						this.fos.close();
						this.fos = null;
						this.fileName = null;
					}
					
				} else if (frame.action == DemoKeyFrame.ACTION_CANCEL) {
					
					this.cancelDemo();
					break;
					
				} else if (frame.action == DemoKeyFrame.ACTION_META) {
					
					if (this.fos != null) {
						
						this.fos.write(frame.meta.getBytes());
					}
					
				} else {
					
					if (this.fos != null) {
						
						String item = frame.frameTime + ":" +
							frame.playerPosition.x + "," +
							frame.playerPosition.y + "," +
							frame.playerAnimation + "," +
							frame.playerSpeed + "," +
							frame.mapTime + 
							(frame.playerSound == -1 ? "" : "," + frame.playerSound) +
							(frame.decalType == null ? "" : "," + frame.decalType + "#" + frame.decalX + "#" + frame.decalY) +
							";";
						
						this.fos.write(item.getBytes());
					}
				}
			}
			
		} catch (InterruptedException e) {
		} catch (IOException e) {
		}
    }
}

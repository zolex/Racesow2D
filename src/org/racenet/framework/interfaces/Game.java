package org.racenet.framework.interfaces;

public interface Game {

	public Input getInput();
	
	public FileIO getFileIO();
	
	public Graphics getGraphics();
	
	public Audio getAudio();
	
	public void setScreen(Screen screen);
	
	public Screen getCurrentScreen();
	
	public Screen getStartScreen();
	
	public int getScreenWidth();
	
	public int getScreenHeight();
}

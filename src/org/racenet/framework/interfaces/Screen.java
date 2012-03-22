package org.racenet.framework.interfaces;

public abstract class Screen {

	protected final Game game;
	
	public Screen(Game g) {
		
		game = g;
	}
	
	public abstract void update(float deltaTime);
	
	public abstract void present(float deltaTime);
	
	public abstract void pause();
	
	public abstract void resume();
	
	public abstract void dispose();
}

package org.racenet.framework;

/**
 * Base class for game screens
 * 
 * @author soh#zolex
 *
 */
public abstract class Screen {

	public final GLGame game;
	
	public Screen(GLGame game) {
		
		this.game = game;
	}
	
	public abstract void update(float deltaTime);
	
	public abstract void present(float deltaTime);
	
	public abstract void pause();
	
	public abstract void resume();
	
	public abstract void dispose();
}

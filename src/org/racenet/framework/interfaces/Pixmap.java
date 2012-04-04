package org.racenet.framework.interfaces;

import org.racenet.framework.interfaces.Graphics.PixmapFormat;

import android.graphics.Bitmap;

/**
 * Interface for Pixmap handling
 * 
 * @author soh#zolex
 *
 */
public interface Pixmap {

	public Bitmap getBitmap();
	
	public int getWidth();
	
	public int getHeight();
	
	public void setX(int x);
	
	public int getX();
	
	public void setY(int y);
	
	public int getY();
	
	public PixmapFormat getFormat();
	
	public void resize(int width, int height);
	
	public void resizeWidth(int width);
	
	public void resizeHeight(int height);
	
	public void dispose();
}

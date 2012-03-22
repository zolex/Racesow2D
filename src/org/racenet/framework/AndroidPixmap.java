package org.racenet.framework;

import org.racenet.framework.interfaces.Graphics.PixmapFormat;
import org.racenet.framework.interfaces.Pixmap;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class AndroidPixmap implements Pixmap {

	private Bitmap bitmap;
	private PixmapFormat format;
	private int lastX;
	private int lastY;
	
	public AndroidPixmap(Bitmap b, PixmapFormat f) {
		
		bitmap = b;
		format = f;
	}
	
	public Bitmap getBitmap() {
		
		return bitmap;
	}
	
	public int getWidth() {

		return bitmap.getWidth();
	}

	public int getHeight() {
		
		return bitmap.getHeight();
	}
	
	public void setX(int x) {
		
		lastX = x;
	}
	
	public int getX() {
		
		return lastX;
	}
	
	public void setY(int y) {
		
		lastY = y;
	}
	
	public int getY() {
		
		return lastY;
	}

	public PixmapFormat getFormat() {
		
		return format;
	}
	
	public void resize(int width, int height) {

		float scaleWidth = ((float) width) / getWidth();
		float scaleHeight = ((float) height) / getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, getWidth(), getHeight(), matrix, false);
	}
	
	public void resizeWidth(int width) {
		
		int height = getHeight() * width / getWidth();
		resize(width, height);
	}
	
	public void resizeHeight(int height) {
		
		int width = getWidth() * height / getHeight();
		resize(width, height);
	}

	public void dispose() {
		
		bitmap.recycle();
	}
}

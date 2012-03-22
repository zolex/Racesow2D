package org.racenet.framework;

import java.io.IOException;
import java.io.InputStream;

import org.racenet.framework.interfaces.Graphics;
import org.racenet.framework.interfaces.Pixmap;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;

public class AndroidGraphics implements Graphics {

	AssetManager assetManager;
	Bitmap frameBuffer;
	Canvas canvas;
	Paint paint;
	Rect srcRect = new Rect();
	Rect dstRect = new Rect();
	Typeface verdana;
	
	public AndroidGraphics(AssetManager am, Bitmap b) {
		
		assetManager = am;
		frameBuffer = b;
		canvas = new Canvas(frameBuffer);
		paint = new Paint();
		verdana = Typeface.createFromAsset(assetManager, "verdana.ttf");
	}
	
	public Pixmap newPixmap(String fileName, PixmapFormat format) {
		
		Config config = null;
		if (format == PixmapFormat.RGB565) {
			
			config = Config.RGB_565;
		
		} else if (format == PixmapFormat.ARGB4444) {
			
			config = Config.ARGB_4444;
		
		} else {
			
			config = Config.ARGB_8888;
		}
		
		Options options = new Options();
		options.inPreferredConfig = config;
		
		InputStream in = null;
		Bitmap bitmap = null;
		try {
			
			in = assetManager.open(fileName);
			bitmap = BitmapFactory.decodeStream(in);
			if (bitmap == null) {
				
				throw new RuntimeException("Could not load bitmap from asset '" + fileName + "'");
			}
			
		} catch (IOException e) {
			
			throw new RuntimeException("Could not load bitmap from asset '" + fileName + "'");
		
		} finally {
			
			if (in != null) {
				
				try {
					
					in.close();
				
				} catch (IOException e) {
					
					// I don't care
				}
			}
		}
		
		if (bitmap.getConfig() == Config.RGB_565) {
			
			format = PixmapFormat.RGB565;
		
		} else if (bitmap.getConfig() == Config.ARGB_4444) {
			
			format = PixmapFormat.ARGB4444;
			
		} else {
			
			format = PixmapFormat.ARGB8888;
		}
		
		return new AndroidPixmap(bitmap, format);
	}
	
	public void drawText(String text, float size, int color, int x, int y, String font) {
		
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setTypeface(verdana);
		paint.setTextSize(size);
		canvas.drawText(text, x, y, paint);
	}

	public void clear(int color) {
		
		canvas.drawRGB((color & 0xff0000) >> 16, (color & 0x00ff) >> 8, (color & 0xff));
	}

	public void drawPixel(int x, int y, int color) {
		
		paint.setColor(color);
		canvas.drawPoint(x, y, paint);
	}

	public void drawLine(int fromX, int fromY, int toX, int toY, int color) {
		
		paint.setColor(color);
		canvas.drawLine(fromX, fromY, toX, toY, paint);
	}

	public void drawRect(int x, int y, int width, int height, int color) {
		
		paint.setColor(color);
		paint.setStyle(Style.FILL);
		canvas.drawRect(x, y, x + width - 1, y + height - 1, paint);
	}

	public void drawPixmap(Pixmap pixmap, int x, int y) {

		canvas.drawBitmap(((AndroidPixmap)pixmap).getBitmap(), x, y, null);
		pixmap.setX(x);
		pixmap.setY(y);
	}

	public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight) {
		
		srcRect.left = srcX;
		srcRect.top = srcY;
		srcRect.right = srcX + srcWidth - 1;
		srcRect.bottom = srcY + srcHeight - 1;
		
		dstRect.left = x;
		dstRect.top = y;
		dstRect.right = x + srcWidth - 1;
		dstRect.bottom = y + srcHeight - 1;
		
		canvas.drawBitmap(((AndroidPixmap)pixmap).getBitmap(), srcRect, dstRect, null);
		
		pixmap.setX(x);
		pixmap.setY(y);
	}

	public int getWidth() {
		
		return frameBuffer.getWidth();
	}

	public int getHeight() {
		
		return frameBuffer.getHeight();
	}
}

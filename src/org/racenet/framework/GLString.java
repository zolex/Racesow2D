package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLUtils;

import org.racenet.framework.GLGame;
import org.racenet.framework.GLGraphics;

public class GLString {
	
    GLGraphics glGraphics;
    String text;
    int textureId;
    int minFilter;
    int magFilter;    
    int width, height, x, y;
    
    public GLString(GLGame glGame, String text, int width, int height, int x, int y) {
    	
        glGraphics = glGame.getGLGraphics();
        this.text = text;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        load();
    }
    
    public int getId() {
    	
    	return textureId;
    }
    
    private void load() {
    	
        GL10 gl = glGraphics.getGL();
        int[] textureIds = new int[1];
        gl.glGenTextures(1, textureIds, 0);
        textureId = textureIds[0];
        
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(bitmap);
		bitmap.eraseColor(Color.TRANSPARENT);
		
		Paint textPaint = new Paint();
		textPaint.setTextSize(128);
		textPaint.setAntiAlias(true);
		textPaint.setARGB(0xff, 0x00, 0xff, 0x00);
		canvas.drawText(this.text, x, y, textPaint);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		setFilters(GL10.GL_LINEAR, GL10.GL_LINEAR);            
		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
    }
    
    public void reload() {
    	
        load();
        bind();
        setFilters(minFilter, magFilter);        
        glGraphics.getGL().glBindTexture(GL10.GL_TEXTURE_2D, 0);
    }
    
    public void setFilters(int min, int mag) {
    	
        minFilter = min;
        magFilter = mag;
        GL10 gl = glGraphics.getGL();
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, minFilter);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, magFilter);
    }    
    
    public void bind() {
    	
        GL10 gl = glGraphics.getGL();
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
    }
    
    public void dispose() {
    	
        GL10 gl = glGraphics.getGL();
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
        int[] textureIds = {textureId};
        gl.glDeleteTextures(1, textureIds, 0);
    }
}
package org.racenet.framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES10;
import android.opengl.GLUtils;
/**
 * Class which represents an openGL ES texture
 * 
 * @author soh#zolex
 *
 */
public class GLTexture {
	
    String fileName;
    int textureId;
    int minFilter;
    int magFilter;
    public int width;
    public int height;
    public static String APP_FOLDER = "";
    
    /**
     * Constructor
     * 
     * @param GLGame glGame
     * @param String fileName
     */
    public GLTexture(String fileName) {
    	
        this.fileName = fileName;
        load();
    }
    
    /**
     * Get the openGL texture ID
     * 
     * @return int
     */
    public int getId() {
    	
    	return textureId;
    }
    
    /**
     * Load the texture from the assets or from the sd-card
     * 
     * @return boolean
     */
    private boolean load() {
    	
        int[] textureIds = new int[1];
        GLES10.glGenTextures(1, textureIds, 0);
        this.textureId = textureIds[0];
        
        InputStream in = null;
        // try loading from assets
        try {
        	
            in = FileIO.getInstance().readAsset("textures" + File.separator + this.fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, this.textureId);
            GLUtils.texImage2D(GLES10.GL_TEXTURE_2D, 0, bitmap, 0);
            this.setFilters(GLES10.GL_NEAREST, GLES10.GL_NEAREST);            
            GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, 0);
            this.width = bitmap.getWidth();
            this.height = bitmap.getHeight();
            bitmap.recycle();
            
        } catch (IOException e) {
        	
        	// if not found in assets try loading from sd-card
            try {
            	
            	in = FileIO.getInstance().readFile(APP_FOLDER + File.separator + "textures" + File.separator + this.fileName);
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, this.textureId);
                GLUtils.texImage2D(GLES10.GL_TEXTURE_2D, 0, bitmap, 0);
                this.setFilters(GLES10.GL_NEAREST, GLES10.GL_NEAREST);            
                GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, 0);
                this.width = bitmap.getWidth();
                this.height = bitmap.getHeight();
                bitmap.recycle();
                
            } catch (IOException e2) {
            	
            	return false;
            	
            } finally {
            	
                if (in != null) {
                
                	try { in.close(); } catch (IOException e2) {}
                }
            }
            
        } finally {
        	
            if (in != null) {
            
            	try { in.close(); } catch (IOException e) {}
            }
        }
        
        return true;
    }
    
    /**
     * Reload the texture
     */
    public void reload() {
    	
        this.load();
        this.bind();
        this.setFilters(minFilter, magFilter);        
        GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, 0);
    }
    
    /**
     * Set the openGL filters for the texture
     * 
     * @param int min
     * @param itn mag
     */
    public void setFilters(int min, int mag) {
    	
    	this.minFilter = min;
    	this.magFilter = mag;
    	GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER, this.minFilter);
    	GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MAG_FILTER, this.magFilter);
    }    
    
    /**
     * Bind the texture
     */
    public void bind() {
    	
    	GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, textureId);
    }
    
    /**
     * Get rid of the tetxure
     */
    public void dispose() {
    	
    	GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, this.textureId);
        int[] textureIds = {textureId};
        GLES10.glDeleteTextures(1, textureIds, 0);
    }
}
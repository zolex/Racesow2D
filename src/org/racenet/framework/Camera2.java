package org.racenet.framework;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * A camera which represents the two dimensional
 * view area of an openGL ES application
 * 
 * @author soh#zolex
 *
 */
public class Camera2 {
	
    public final Vector2 position;
    public float zoom;
    public final float frustumWidth;
    public final float frustumHeight;
    final GLGraphics glGraphics;
    protected List<HudItem> hudItems = new ArrayList<HudItem>();
    
    /**
     * Constructor
     * 
     * @param GLGraphics glGraphics
     * @param float frustumWidth
     * @param float frustumHeight
     */
    public Camera2(GLGraphics glGraphics, float frustumWidth, float frustumHeight) {
    	
        this.glGraphics = glGraphics;
        this.frustumWidth = frustumWidth;
        this.frustumHeight = frustumHeight;
        this.position = new Vector2(frustumWidth / 2, frustumHeight / 2);
        this.zoom = 1.0f;
    }

    /**
     * Set the camera to a new position and
     * care for all HUD items to move with the camera
     * 
     * @param float x
     * @param float y
     */
	public void setPosition(float x, float y) {
		
		this.position.set(x, y);
		
		for (int i = 0; i < hudItems.size(); i++) {
			
			HudItem item = hudItems.get(i);
			
			item.setPosition(new Vector2(x + item.cameraX, y + item.cameraY));
		}
	}
    
	/**
	 * Zoom the camera view and care for all
	 * HUD items to be realigned
	 * 
	 * @param float factor
	 */
	public void setZoom(float factor) {
		
		this.zoom = factor;
		
		for (int i = 0; i < hudItems.size(); i++) {
			
			HudItem item = hudItems.get(i);
			item.setPosition(new Vector2(item.cameraX * factor, item.cameraY * factor));
		}
	}
	
	/**
	 * Add a HUD item to the camrea
	 * 
	 * @param HudItem item
	 */
	public void addHud(HudItem item) {
		
		this.hudItems.add(item);
	}
	
	/**
	 * Remove a HudItem from the camera
	 * 
	 * @param HudItem item
	 */
    public void removeHud(HudItem item) {
    	
    	if (this.hudItems.contains(item)) {
    		
    		this.hudItems.remove(item);
    	}
    }
    
    /**
     * Setup openGL ES internals
     */
    public void setViewportAndMatrices() {
    	
        GL10 gl = glGraphics.getGL();
        gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(position.x - frustumWidth * zoom / 2, 
                    position.x + frustumWidth * zoom/ 2, 
                    position.y - frustumHeight * zoom / 2, 
                    position.y + frustumHeight * zoom/ 2, 
                    1, -1);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    /**
     * Draw all HUD items attached to the camera
     */
    public void drawHud() {
    	
		for (int i = 0; i < hudItems.size(); i++) {
			
			hudItems.get(i).draw();
		}
    }
    
    /**
     * Assign the touch coordinates to the viewed area
     * 
     * @param Vector2 touch
     */
    public void touchToWorld(Vector2 touch) {
    	
    	touch.x = (touch.x / (float) glGraphics.getWidth()) * frustumWidth * zoom;
    	touch.y = (1 - touch.y / (float) glGraphics.getHeight()) * frustumHeight * zoom;
    	touch.add(position).subtract(frustumWidth * zoom / 2, frustumHeight * zoom / 2);
    }
    
    /**
     * Reload the HUD textures
     */
    public void reloadTextures() {
    	
    	for (int i = 0; i < hudItems.size(); i++) {
    		
    		this.hudItems.get(i).reloadTexture();
    	}
    }
    
    /**
     * Get rid of the HUD textures
     */
    public void dispose() {
    	
    	for (int i = 0; i < hudItems.size(); i++) {
    		
    		this.hudItems.get(i).dispose();
    	}
    }
}

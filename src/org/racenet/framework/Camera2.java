package org.racenet.framework;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class Camera2 {
	
    public final Vector2 position;
    public final Vector2 velocity;
    public float zoom;
    public final float frustumWidth;
    public final float frustumHeight;
    final GLGraphics glGraphics;
    protected List<CameraText> hudItems = new ArrayList<CameraText>();
    
    public Camera2(GLGraphics glGraphics, float frustumWidth, float frustumHeight) {
    	
        this.glGraphics = glGraphics;
        this.frustumWidth = frustumWidth;
        this.frustumHeight = frustumHeight;
        this.position = new Vector2(frustumWidth / 2, frustumHeight / 2);
        this.velocity = new Vector2();
        this.zoom = 1.0f;
    }
    
    public void updatePosition(float deltaTime) {
    	
    	if (velocity.x == 0 && velocity.y == 0) {
    		
    		return;
    	}
    	
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
		
		int length = hudItems.size();
		for (int i = 0; i < length; i++) {
			
			hudItems.get(i).position.add(velocity.x * deltaTime, velocity.y * deltaTime);
		}
    }

	public void setPosition(float x, float y) {
		
		this.position.set(x, y);
		
		int length = hudItems.size();
		for (int i = 0; i < length; i++) {
			
			CameraText item = hudItems.get(i);
			
			item.position.set(x - item.cameraX, y + item.cameraY);
		}
	}
    
	public void setZoom(float factor) {
		
		this.zoom = factor;
		
		int length = hudItems.size();
		for (int i = 0; i < length; i++) {
			
			CameraText item = hudItems.get(i);
			item.position.set(item.cameraX * factor, item.cameraY * factor);
		}
	}
	
    public void addHud(CameraText item) {
    	
    	hudItems.add(item);
    }
    
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
    
    public void touchToWorld(Vector2 touch) {
    	
        touch.x = (touch.x / (float) glGraphics.getWidth()) * frustumWidth * zoom;
        touch.y = (1 - touch.y / (float) glGraphics.getHeight()) * frustumHeight * zoom;
        touch.add(position).subtract(frustumWidth * zoom / 2, frustumHeight * zoom / 2);
    }
}

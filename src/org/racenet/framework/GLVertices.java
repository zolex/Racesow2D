package org.racenet.framework;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES10;

/**
 * Class to manage and draw openGL ES vertices
 * 
 * @author soh#zolex
 *
 */
public class GLVertices {
	
    final boolean hasColor;
    final boolean hasTexCoords;
    final int vertexSize;
    final FloatBuffer vertices;
    final ShortBuffer indices;
    
    /**
     * Constructor 
     * 
     * @param GLGraphics glGraphics
     * @param int maxVertices
     * @param int maxIndices
     * @param boolean hasColor
     * @param boolean hasTexCoords
     */
    public GLVertices (int maxVertices, int maxIndices, boolean hasColor, boolean hasTexCoords) {
    	
        this.hasColor = hasColor;
        this.hasTexCoords = hasTexCoords;
        this.vertexSize = (2 + (hasColor?4:0) + (hasTexCoords?2:0)) * 4;
        
        ByteBuffer buffer = ByteBuffer.allocateDirect(maxVertices * vertexSize);
        buffer.order(ByteOrder.nativeOrder());
        vertices = buffer.asFloatBuffer();
        
        if (maxIndices > 0) {
        	
            buffer = ByteBuffer.allocateDirect(maxIndices * Short.SIZE / 8);
            buffer.order(ByteOrder.nativeOrder());
            indices = buffer.asShortBuffer();
            
        } else {
        	
            indices = null;
        }            
    }
    
    /**
     * Prepare the vertices for drawing
     * 
     * @param float[] vertices
     * @param int offset
     * @param int length
     */
    public void setVertices(float[] vertices, int offset, int length) {
    	
        this.vertices.clear();
        this.vertices.put(vertices, offset, length);
        this.vertices.flip();
    }
    
    /**
     * Set the indices for sharing vertices
     * 
     * @param short[] indices
     * @param int offset
     * @param int length
     */
    public void setIndices(short[] indices, int offset, int length) {
    	
        this.indices.clear();
        this.indices.put(indices, offset, length);
        this.indices.flip();
    }
    
    /**
     * Bind the vertices to the openGL renderer
     * according to the provided parameters
     */
    public void bind() {
    	
    	GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
        this.vertices.position(0);
        GLES10.glVertexPointer(2, GLES10.GL_FLOAT, this.vertexSize, this.vertices);
        
        if (this.hasColor) {
        	
        	GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
        	this.vertices.position(2);
        	GLES10.glColorPointer(4, GLES10.GL_FLOAT, this.vertexSize, this.vertices);
        }
        
        if (this.hasTexCoords) {
        	
        	GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
        	this.vertices.position(hasColor?6:2);
        	GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, this.vertexSize, this.vertices);
        }
    }

    /**
     * Draw the provided vertices accoring to the given parameters
     * 
     * @param int primitiveType
     * @param int offset
     * @param int numVertices
     */
    public void draw(int primitiveType, int offset, int numVertices) {  
    	
        if (this.indices != null) {
        	
        	this.indices.position(offset);
        	GLES10.glDrawElements(primitiveType, numVertices, GLES10.GL_UNSIGNED_SHORT, this.indices);
            
        } else {
        	
        	GLES10.glDrawArrays(primitiveType, offset, numVertices);
        }        
    }

    /**
     * Unbind the vertices
     */
    public void unbind() {
    	
        if (this.hasTexCoords) {
            
        	GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
        }

        if (this.hasColor) {
            
        	GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
        }
    }
}

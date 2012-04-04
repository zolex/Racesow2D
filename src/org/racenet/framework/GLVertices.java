package org.racenet.framework;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.racenet.framework.GLGraphics;

/**
 * Class to manage and draw openGL ES vertices
 * 
 * @author soh#zolex
 *
 */
public class GLVertices {
	
    final GLGraphics glGraphics;
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
    public GLVertices (GLGraphics glGraphics, int maxVertices, int maxIndices, boolean hasColor, boolean hasTexCoords) {
    	
        this.glGraphics = glGraphics;
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
    	
        GL10 gl = glGraphics.getGL();
        
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        vertices.position(0);
        gl.glVertexPointer(2, GL10.GL_FLOAT, vertexSize, vertices);
        
        if(hasColor) {
        	
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
            vertices.position(2);
            gl.glColorPointer(4, GL10.GL_FLOAT, vertexSize, vertices);
        }
        
        if(hasTexCoords) {
        	
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            vertices.position(hasColor?6:2);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, vertexSize, vertices);
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
    	
        GL10 gl = glGraphics.getGL();
        
        if (indices != null) {
        	
            indices.position(offset);
            gl.glDrawElements(primitiveType, numVertices, GL10.GL_UNSIGNED_SHORT, indices);
            
        } else {
        	
            gl.glDrawArrays(primitiveType, offset, numVertices);
        }        
    }

    /**
     * Unbind the vertices
     */
    public void unbind() {
    	
        GL10 gl = glGraphics.getGL();
        if (hasTexCoords) {
            
        	gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }

        if (hasColor) {
            
        	gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        }
    }
}

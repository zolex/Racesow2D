package org.racenet.framework;

import javax.microedition.khronos.opengles.GL10;

import android.util.FloatMath;

/**
 * Class to draw sprites on form of TextureRegions
 * 
 * @author soh#zolex
 *
 */
public class SpriteBatcher {
	
    final float[] verticesBuffer;
    int bufferIndex;
    final GLVertices vertices;
    int numSprites;    
    
    /**
     * Constructor
     * 
     * @param GLGraphics glGraphics
     * @param int maxSprites
     */
    public SpriteBatcher(GLGraphics glGraphics, int maxSprites) {   
    	
        this.verticesBuffer = new float[maxSprites*4*4];        
        this.vertices = new GLVertices(glGraphics.getGL(), maxSprites*4, maxSprites*6, false, true);
        this.bufferIndex = 0;
        this.numSprites = 0;
        
        // create the indices for the vertices
        short[] indices = new short[maxSprites*6];
        int len = indices.length;
        short j = 0;
        for (int i = 0; i < len; i += 6, j += 4) {
                indices[i + 0] = (short)(j + 0);
                indices[i + 1] = (short)(j + 1);
                indices[i + 2] = (short)(j + 2);
                indices[i + 3] = (short)(j + 2);
                indices[i + 4] = (short)(j + 3);
                indices[i + 5] = (short)(j + 0);
        }
        vertices.setIndices(indices, 0, indices.length);                
    }       
    
    /**
     * Start drawing sprites using the given tetxure
     * 
     * @param GLTetxure texture
     */
    public void beginBatch(GLTexture texture) {
        texture.bind();
        numSprites = 0;
        bufferIndex = 0;
    }
    
    /**
     * End drawing the sprites
     */
    public void endBatch() {
        vertices.setVertices(verticesBuffer, 0, bufferIndex);
        vertices.bind();
        vertices.draw(GL10.GL_TRIANGLES, 0, numSprites * 6);
        vertices.unbind();
    }
    
    /**
     * Draw a single sprite
     * 
     * @param float x
     * @param float y
     * @param float width
     * @param float height
     * @param TextureRegion region
     */
    public void drawSprite(float x, float y, float width, float height, TextureRegion region) {
    	
        float halfWidth = width / 2;
        float halfHeight = height / 2;
        float x1 = x - halfWidth;
        float y1 = y - halfHeight;
        float x2 = x + halfWidth;
        float y2 = y + halfHeight;
        
        verticesBuffer[bufferIndex++] = x1;
        verticesBuffer[bufferIndex++] = y1;
        verticesBuffer[bufferIndex++] = region.u1;
        verticesBuffer[bufferIndex++] = region.v2;
        
        verticesBuffer[bufferIndex++] = x2;
        verticesBuffer[bufferIndex++] = y1;
        verticesBuffer[bufferIndex++] = region.u2;
        verticesBuffer[bufferIndex++] = region.v2;
        
        verticesBuffer[bufferIndex++] = x2;
        verticesBuffer[bufferIndex++] = y2;
        verticesBuffer[bufferIndex++] = region.u2;
        verticesBuffer[bufferIndex++] = region.v1;
        
        verticesBuffer[bufferIndex++] = x1;
        verticesBuffer[bufferIndex++] = y2;
        verticesBuffer[bufferIndex++] = region.u1;
        verticesBuffer[bufferIndex++] = region.v1;
        
        numSprites++;
    }
    
    /**
     * Draw a rotated sprite
     * 
     * @param float x
     * @param float y
     * @param float width
     * @param float height
     * @param float angle
     * @param TextureRegion region
     */
    public void drawSprite(float x, float y, float width, float height, float angle, TextureRegion region) {
    	
        float halfWidth = width / 2;
        float halfHeight = height / 2;
        
        float rad = angle * Vector2.TO_RADIANS;
        float cos = FloatMath.cos(rad);
        float sin = FloatMath.sin(rad);
                
        float x1 = -halfWidth * cos - (-halfHeight) * sin;
        float y1 = -halfWidth * sin + (-halfHeight) * cos;
        float x2 = halfWidth * cos - (-halfHeight) * sin;
        float y2 = halfWidth * sin + (-halfHeight) * cos;
        float x3 = halfWidth * cos - halfHeight * sin;
        float y3 = halfWidth * sin + halfHeight * cos;
        float x4 = -halfWidth * cos - halfHeight * sin;
        float y4 = -halfWidth * sin + halfHeight * cos;
        
        x1 += x;
        y1 += y;
        x2 += x;
        y2 += y;
        x3 += x;
        y3 += y;
        x4 += x;
        y4 += y;
        
        verticesBuffer[bufferIndex++] = x1;
        verticesBuffer[bufferIndex++] = y1;
        verticesBuffer[bufferIndex++] = region.u1;
        verticesBuffer[bufferIndex++] = region.v2;
        
        verticesBuffer[bufferIndex++] = x2;
        verticesBuffer[bufferIndex++] = y2;
        verticesBuffer[bufferIndex++] = region.u2;
        verticesBuffer[bufferIndex++] = region.v2;
        
        verticesBuffer[bufferIndex++] = x3;
        verticesBuffer[bufferIndex++] = y3;
        verticesBuffer[bufferIndex++] = region.u2;
        verticesBuffer[bufferIndex++] = region.v1;
        
        verticesBuffer[bufferIndex++] = x4;
        verticesBuffer[bufferIndex++] = y4;
        verticesBuffer[bufferIndex++] = region.u1;
        verticesBuffer[bufferIndex++] = region.v1;
        
        numSprites++;
    }
}
package org.racenet.framework;

public class TextureRegion {    
	
    public final float u1, v1;
    public final float u2, v2;
    public final GLTexture texture;
    
    public TextureRegion(GLTexture texture, float x, float y, float width, float height) {
    	
        this.u1 = x / texture.width;
        this.v1 = y / texture.height;
        this.u2 = this.u1 + width / texture.width;
        this.v2 = this.v1 + height / texture.height;        
        this.texture = texture;
    }
}

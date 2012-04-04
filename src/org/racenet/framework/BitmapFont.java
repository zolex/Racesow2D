package org.racenet.framework;

/**
 * Class to render text using a bitmap with ascii characters
 * 
 * @author soh#zolex
 *
 */
public class BitmapFont {

	public GLTexture texture;
	public int glyphWidth;
	public int glyphHeight;
	public TextureRegion[] glyphs = new TextureRegion[96];
	
	/**
	 * Constructor
	 * 
	 * @param GLTexture texture
	 * @param int offsetX
	 * @param int offsetY
	 * @param int glyphsPerRow
	 * @param int glyphWidth
	 * @param itn glyphHeight
	 */
	public BitmapFont(GLTexture texture, int offsetX, int offsetY, int glyphsPerRow, int glyphWidth, int glyphHeight) {
		
		this.texture = texture;
		this.glyphWidth = glyphWidth;
		this.glyphHeight = glyphHeight;
		int x = offsetX;
		int y = offsetY;
		for (int i = 0; i < 96; i++) {
			
			glyphs[i] = new TextureRegion(texture, x, y, glyphWidth, glyphHeight);
			x += glyphWidth;
			if (x == offsetX + glyphsPerRow * glyphWidth) {
				
				x = offsetX;
				y += glyphHeight;
			}
		}
	}
	
	/**
	 * Draw text on the given position using the given
	 * scale and letter spacing using the SpriteBatcher class
	 * 
	 * @param SpriteBatcher batcher
	 * @param String text
	 * @param float scale
	 * @param float spacing
	 * @param float x
	 * @param float y
	 */
	public void draw(SpriteBatcher batcher, String text, float scale, float spacing, float x, float y) {
		
		batcher.beginBatch(texture);
		int lenght = text.length();
		for (int i = 0; i < lenght; i++) {
			
			int c = text.charAt(i) - ' ';
			if (c < 0 || c > glyphs.length - 1) {
				
				continue;
			}
			
			batcher.drawSprite(x, y, glyphWidth * scale, glyphHeight * scale, glyphs[c]);
			x += glyphWidth * spacing;
		}
		
		batcher.endBatch();
	}
}

package org.racenet.framework;

import android.util.Log;

/**
 * Represents a polygon defined by at
 * least three points
 * 
 * @author al
 */
public class Polygon {

	/**
	 * The borders of the polygon represented by
	 * line segments
	 */
	public Line[] borders;
	
	/**
	 * Initialize a new polygon using multiple points
	 * 
	 * @param Vector2 ... points
	 */
	public Polygon(Vector2 ... points) {
		
		int length = points.length;
		this.borders = new Line[length];
		for (int i = 0; i < length; i++) {
			
			this.borders[i] = new Line(points[i], points[i == length - 1 ? 0 : i + 1]);
		}
	}
	
	/**
	 * Calculate if the polygon intersects with
	 * another one by checking the borders
	 * 
	 * @param Polygon other
	 * @return boolean
	 */
	public boolean intersect(Polygon other) {
		
		int tLength = this.borders.length;
		int oLength = other.borders.length;
		for (int t = 0; t < tLength; t++) {
			
			for (int o = 0; o < oLength; o++) {
				
				if (this.borders[t].intersect(other.borders[o])) {
					
					Log.d("DEBUG", "polygon 1 pos x " + String.valueOf(new Float(this.getPosition().x)) + " y " + String.valueOf(new Float(this.getPosition().y)));
					Log.d("DEBUG", "polygon 2 pos x " + String.valueOf(new Float(other.getPosition().x)) + " y " + String.valueOf(new Float(other.getPosition().y)));
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Get the height of the polygon by determining
	 * the minimal and maximal y coordinates
	 * 
	 * @return float
	 */
	public float getHeight() {
		
		float minY = Float.MAX_VALUE;
		float maxY = Float.MIN_VALUE;
		int length = this.borders.length;
		for (int i = 0; i < length; i++) {
			
			if (minY > this.borders[i].p1.y) {
				
				minY = this.borders[i].p1.y;
			}
			
			if (minY > this.borders[i].p2.y) {
				
				minY = this.borders[i].p2.y;
			}
			
			if (maxY < this.borders[i].p1.y) {
				
				maxY = this.borders[i].p1.y;
			}
			
			if (maxY < this.borders[i].p2.y) {
				
				maxY = this.borders[i].p2.y;
			}
		}
		
		return maxY - minY;
	}

	/**
	 * Get the width of the polygon by determining
	 * the minimal and maximal x coordinates
	 * 
	 * @return float
	 */
	public float getWidth() {
		
		float minX = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		int length = this.borders.length;
		for (int i = 0; i < length; i++) {
			
			if (minX > this.borders[i].p1.x) {
				
				minX = this.borders[i].p1.x;
			}
			
			if (minX > this.borders[i].p2.x) {
				
				minX = this.borders[i].p2.x;
			}
			
			if (maxX < this.borders[i].p1.x) {
				
				maxX = this.borders[i].p1.x;
			}
			
			if (maxX < this.borders[i].p2.x) {
				
				maxX = this.borders[i].p2.x;
			}
		}
		
		return maxX - minX;
	}
	
	/**
	 * TODO: for now just use the first given
	 * point as the position
	 * 
	 * @return Vector2
	 */
	public Vector2 getPosition() {
		
		return this.borders[0].p1;
	}
	
	/**
	 * Set the position by moving all borders
	 * of the polygon
	 * 
	 * @param Vector2 position
	 */
	public void setPosition(Vector2 position) {
		
		int length = this.borders.length;
		
		float diffX = position.x - this.getPosition().x;
		float diffY = position.y - this.getPosition().y;
		
		for (int i = 0; i < length; i++) {
			
			this.borders[i].p1.x += diffX;
			this.borders[i].p1.y += diffY;
			this.borders[i].p2.x += diffX;
			this.borders[i].p2.y += diffY;
		}
	}
	
	/**
	 * Set the position by moving all borders
	 * of the polygon
	 * 
	 * @param Vector2 position
	 */
	public void addToPosition(float x, float y) {
		
		int length = this.borders.length;
		for (int i = 0; i < length; i++) {
			
			this.borders[i].p1.x += x;
			this.borders[i].p1.y += y;
			this.borders[i].p2.x += x;
			this.borders[i].p2.y += y;
		}
	}
}

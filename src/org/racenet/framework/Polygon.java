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
	public Vector2[] points;
	
	/**
	 * Initialize a new polygon using multiple points
	 * 
	 * @param Vector2 ... points
	 */
	public Polygon(Vector2 ... points) {
		
		this.points = points;
	}
	
	/**
	 * Check if this polygon intersects with another one
	 * 
	 * @param Polygon other
	 * @return boolean
	 */
	public boolean intersect(Polygon other){
				
		for (int j = this.points.length - 1, i = 0; i < this.points.length; j = i, i++) {
			
			if (separatedByAxis(new Vector2(-(this.points[i].y - this.points[j].y),
					this.points[i].x - this.points[j].x), other)) {
				
				return false;
			}
		}

		for (int j = other.points.length - 1, i = 0; i < other.points.length; j = i, i++) {
			
			if (separatedByAxis(new Vector2(-(other.points[i].y - other.points[j].y),
					other.points[i].x - other.points[j].x), other)) {
				
				return false;
			}
		}
		
		return true;
	}
	
	public boolean separatedByAxis(Vector2 axis, Polygon other) {
		
		float[] resultThis = this.getInterval(axis);
		float[] resultOther = other.getInterval(axis);
		return resultOther[1] - resultThis[0] < 0.0 || resultOther[0] - resultThis[1] > 0.0;
	}
	
	public float[] getInterval(Vector2 axis) {
		
		float[] result = new float[2];
		result[1] = this.points[0].dotProduct(axis);
		result[0] = this.points[0].dotProduct(axis);
			
		for (int i = 1; i < this.points.length; i++) {
			
			float d = this.points[i].dotProduct(axis);
			if (d < result[0]) {
				
				result[0] = d;
				
			} else if (d > result[1]) {
				
				result[1] = d;
			}
		}
		
		return result;
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
		int length = this.points.length;
		for (int i = 0; i < length; i++) {
			
			if (this.points[i].x < minX) minX = this.points[i].x;
			if (this.points[i].x > maxX) maxX = this.points[i].x;
		}
		
		return maxX - minX;
	}

	public float getHeight() {
		
		float minY = Float.MAX_VALUE;
		float maxY = Float.MIN_VALUE;
		int length = this.points.length;
		for (int i = 0; i < length; i++) {
			
			if (this.points[i].y < minY) minY = this.points[i].y;
			if (this.points[i].y > maxY) maxY = this.points[i].y;
		}
		
		return maxY - minY;
	}
	
	/**
	 * TODO: for now just use the first given
	 * point as the position
	 * 
	 * @return Vector2
	 */
	public Vector2 getPosition() {
		
		return this.points[0];
	}
	
	/**
	 * Set the position by moving all borders
	 * of the polygon
	 * 
	 * @param Vector2 position
	 */
	public void setPosition(Vector2 position) {
		
		float diffX = position.x - this.getPosition().x;
		float diffY = position.y - this.getPosition().y;
		
		int length = this.points.length;
		for (int i = 0; i < length; i++) {
			
			this.points[i].x += diffX;
			this.points[i].y += diffY;
		}
	}
	
	/**
	 * Set the position by moving all borders
	 * of the polygon
	 * 
	 * @param Vector2 position
	 */
	public void addToPosition(float x, float y) {
		
		int length = this.points.length;
		for (int i = 0; i < length; i++) {
			
			this.points[i].x += x;
			this.points[i].y += y;
		}
	}
}

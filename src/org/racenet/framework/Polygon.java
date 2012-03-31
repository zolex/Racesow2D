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
	public Vector2[] vertices;
	public float width, height;
	
	/**
	 * Structure which holds information about a collision
	 *
	 */
	public class CollisionInfo {
		
		public boolean collided;
		public Vector2 direction;
		public float distance;
	}
	
	/**
	 * Initialize a new polygon using multiple points
	 * 
	 * @param Vector2 ... points
	 */
	public Polygon(Vector2 ... vertices) {
		
		this.vertices = vertices;
		this.calcWidth();
		this.calcHeight();
	}
	
	/**
	 * Check if this polygon intersects with another one
	 * 
	 * @param Polygon other
	 * @return boolean
	 */
	public CollisionInfo intersect(Polygon other){
				
		CollisionInfo info = new CollisionInfo();
		info.collided = true;
		
		for (int j = this.vertices.length - 1, i = 0; i < this.vertices.length; j = i, i++) {
			
			info = separatedByAxis(new Vector2(-(this.vertices[i].y - this.vertices[j].y),
					this.vertices[i].x - this.vertices[j].x), other);
			
			if (!info.collided) {
				
				return info;
			}
		}

		for (int j = other.vertices.length - 1, i = 0; i < other.vertices.length; j = i, i++) {
			
			info = separatedByAxis(new Vector2(-(other.vertices[i].y - other.vertices[j].y),
					other.vertices[i].x - other.vertices[j].x), other);
			if (!info.collided) {
				
				return info;
			}
		}
		
		return info;
	}
	
	/**
	 * Check if a polygon is separated by an axis
	 * 
	 * @param Vector2 axis
	 * @param Polygon other
	 * @return CollisionInfo
	 */
	public CollisionInfo separatedByAxis(Vector2 axis, Polygon other) {
		
		CollisionInfo info = new CollisionInfo();
		
		float[] resultThis = this.getInterval(axis);
		float[] resultOther = other.getInterval(axis);
		
		float d0 = resultOther[1] - resultThis[0];
		float d1 = resultOther[0] - resultThis[1];
		
		if (d0 < 0.0 || d1 > 0.0) {
			
			info.collided = false;
			return info;
		}
		
		float overlap = d0 < -d1 ? d0 : d1;
		float axis_length_squared = axis.dotProduct(axis);
		assert(axis_length_squared > 0.00001);
		Vector2 sep = new Vector2(axis.x * (overlap / axis_length_squared), axis.y * (overlap / axis_length_squared)); 

		info.distance = sep.dotProduct(sep);
		info.direction = sep;
		info.collided = true;
		
		return info;

	}
	
	/**
	 * Get the interval of the polygon projected on an axis
	 * 
	 * @param Vector2 axis
	 * @return float[minimum, maximum]
	 */
	public float[] getInterval(Vector2 axis) {
		
		float[] result = new float[2];
		result[1] = this.vertices[0].dotProduct(axis);
		result[0] = this.vertices[0].dotProduct(axis);
			
		for (int i = 1; i < this.vertices.length; i++) {
			
			float d = this.vertices[i].dotProduct(axis);
			if (d < result[0]) {
				
				result[0] = d;
				
			} else if (d > result[1]) {
				
				result[1] = d;
			}
		}
		
		return result;
	}

	/**
	 * Calculate the width of the polygon by determining
	 * the minimal and maximal x coordinates
	 * 
	 * @return float
	 */
	public void calcWidth() {
		
		float minX = 320000000;
		float maxX = -320000000;
		int length = this.vertices.length;
		for (int i = 0; i < length; i++) {
			
			if (this.vertices[i].x < minX) minX = this.vertices[i].x;
			if (this.vertices[i].x > maxX) maxX = this.vertices[i].x;
		}
		
		this.width = maxX - minX;
	}

	/**
	 * Calculate the height of the polygon by determining
	 * the minimal and maximal x coordinates
	 * 
	 * @return float
	 */
	public void calcHeight() {
		
		float minY = 320000000;
		float maxY = -320000000;
		int length = this.vertices.length;
		for (int i = 0; i < length; i++) {
			
			if (this.vertices[i].y < minY) minY = this.vertices[i].y;
			if (this.vertices[i].y > maxY) maxY = this.vertices[i].y;
		}
		
		this.height = maxY - minY;
	}
	
	/**
	 * TODO: for now just use the first given
	 * point as the position
	 * 
	 * @return Vector2
	 */
	public Vector2 getPosition() {
		
		return this.vertices[0];
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
		
		int length = this.vertices.length;
		for (int i = 0; i < length; i++) {
			
			this.vertices[i].x += diffX;
			this.vertices[i].y += diffY;
		}
	}
	
	/**
	 * Set the position by moving all borders
	 * of the polygon
	 * 
	 * @param Vector2 position
	 */
	public void addToPosition(float x, float y) {
		
		int length = this.vertices.length;
		for (int i = 0; i < length; i++) {
			
			this.vertices[i].x += x;
			this.vertices[i].y += y;
		}
	}
}

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
	 * Calculate if the polygon intersects with another one
	 * 
	 * @param Polygon other
	 * @return boolean
	 */
	public boolean intersect(Polygon other) {
		
		int edgeCountA = this.points.length;
	    int edgeCountB = other.points.length;
	    Vector2 edge;
	    
	    // Loop through all the edges of both polygons
	    for (int edgeIndex = 0; edgeIndex < edgeCountA + edgeCountB; edgeIndex++) {
	    	
	        if (edgeIndex < edgeCountA) {
	        	
	            edge = this.points[edgeIndex];
	            
	        } else {
	        	
	            edge = other.points[edgeIndex - edgeCountA];
	        }

	        // Find the axis perpendicular to the current edge
	        Vector2 axis = new Vector2(-edge.x, edge.y);
	        axis.normalize();

	        // Find the projection of the polygon on the current axis
	        float minA = 0; float minB = 0; float maxA = 0; float maxB = 0;
	        float[] result;
	        result = this.project(axis, this);
	        minA = result[0];
	        maxA = result[1];
	        
	        result = this.project(axis, other);
	        minB = result[0];
	        maxB = result[1];

	        // Check if the polygon projections are currentlty intersecting
	        if (this.intervalDistance(minA, maxA, minB, maxB) > 0) {
	        
	        	return false;
	        }
	    }
	    
	    return true;
	}
	
	/**
	 * Calculate the distance between [minA, maxA] and [minB, maxB]
	 * The distance will be negative if the intervals overlap
	 * 
	 * @param float minA
	 * @param float maxA
	 * @param float minB
	 * @param float maxB
	 * @return float
	 */
	public float intervalDistance(float minA, float maxA, float minB, float maxB) {
		
	    if (minA < minB) {
	    	
	        return minB - maxA;
	        
	    } else {
	    	
	        return minA - maxB;
	    }
	}
	
	/**
	 * Calculate the projection of a polygon on an axis
	 * and returns it as a [min, max] interval
	 * 
	 * @param Vector2 axis
	 * @param Polygon polygon
	 * @return float[]
	 */
	public float[] project(Vector2 axis, Polygon polygon) {
		
	    float dotProduct = axis.dotProduct(polygon.points[0]);
	    float min = dotProduct;
	    float max = dotProduct;
	    for (int i = 0; i < polygon.points.length; i++) {
			
			dotProduct = polygon.points[i].dotProduct(axis);
			if (dotProduct < min) {
				
				min = dotProduct;
			    
			} else if (dotProduct > max) {
				
				max = dotProduct;
			}
	    }
	    
	    float[] result = new float[2];
	    result[0] = min;
	    result[1] = max;
	    return result;
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
		int length = this.points.length;
		for (int i = 0; i < length; i++) {
			
			if (this.points[i].y < minY) minY = this.points[i].y;
			if (this.points[i].y > maxY) maxY = this.points[i].y;
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
		int length = this.points.length;
		for (int i = 0; i < length; i++) {
			
			if (this.points[i].x < minX) minX = this.points[i].x;
			if (this.points[i].x > maxX) maxX = this.points[i].x;
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

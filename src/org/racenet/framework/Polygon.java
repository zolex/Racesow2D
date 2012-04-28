package org.racenet.framework;

/**
 * Represents a polygon defined by at
 * least three points
 * 
 * @author soh#zolex
 */
public class Polygon {

	public static final short TOP = 0;
	public static final short LEFT = 1;
	public static final short RAMPUP = 2;
	public static final short RAMPDOWN = 3;
	public static final short BOTTOM = 4;
	
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
		public short type;
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
	 * NOTE: This is a very simplified collision detection.
	 * "this" is always the player, an axis aligned rectangle.
	 * "other" my be a rectangular triangle or an axis aligned rectangle
	 * 
	 * @param Polygon other
	 * @return boolean
	 */
	public CollisionInfo intersect(Polygon other){
				
		final CollisionInfo info = new CollisionInfo();
		info.collided = false;
		
		final float thisLeft = this.vertices[0].x;
		final float thisBottom = this.vertices[0].y;
		final float thisRight = thisLeft + this.width;
		final float thisTop = thisBottom + this.height;
		final float otherLeft = other.vertices[0].x;
		final float otherBottom = other.vertices[0].y;
		final float otherHeight = other.getHeightAt(thisLeft);
		final float otherRight = otherLeft + other.width;
		final float otherTop = otherBottom + otherHeight;
		final boolean otherInside = (thisBottom <= otherBottom && thisTop >= otherTop);
		
		if (other.vertices.length == 4) {
			
			if ((thisRight >= otherLeft && thisLeft <= otherRight) &&
				((thisTop >= otherBottom && thisTop <= otherTop) ||
				(thisBottom >= otherBottom && thisBottom <= otherTop) || otherInside)) {
				
				final float distanceLeft = thisRight - otherLeft;
				//final float distanceRight = otherRight - thisLeft;
				final float distanceBottom = thisTop - otherBottom;
				final float distanceTop = otherTop - thisBottom;
				
				if (otherInside ||
					(distanceLeft < Math.min(distanceBottom, distanceTop) &&
					otherHeight > this.height)) {
					
					info.type = LEFT;
					info.distance = distanceLeft;
					
				} else if (distanceTop < distanceBottom) {
					
					info.type = TOP;
					info.distance = distanceTop;
					
				} else {
					
					info.type = BOTTOM;
					info.distance = distanceBottom;
				}
				
				info.collided = true;
				return info;
			}
			
		} else if (other.vertices.length == 3) {
		
			if (thisRight >= otherLeft && thisLeft <= otherRight && thisBottom <= otherTop) {
				
				info.collided = true;
				info.distance = 0;
				if (other.vertices[1].x == other.vertices[2].x) {
				
					info.type = RAMPUP;
					info.distance = otherTop - thisBottom;
					
				} else if (other.vertices[0].x == other.vertices[1].x) {
					
					info.type = RAMPDOWN;
					info.distance = otherTop - thisBottom;
				}
				
				return info;
			}
		}
		
		return info;
	}
	
	/**
	 * Get the height of the polygon at the given position
	 * 
	 * @param float x
	 * @return float
	 */
	public float getHeightAt(float x) {
		
		if (this.vertices.length == 3) {
		
			// ramp up
			if (this.vertices[1].x == this.vertices[2].x) {
				
				return (this.vertices[2].y - this.vertices[0].y) / (this.vertices[2].x - this.vertices[0].x) * (x - this.vertices[0].x);
			}
			
			// ramp down
			if (this.vertices[0].x == this.vertices[1].x) {
				
				return (this.vertices[0].y - this.vertices[2].y) / (this.vertices[0].x - this.vertices[2].x) * (x - this.vertices[2].x) - this.height;
			}
		}
		
		return this.height;
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
}

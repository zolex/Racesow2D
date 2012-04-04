package org.racenet.framework;

import android.util.FloatMath;

/**
 * Represents a vector in a two-dimensional room
 * 
 * @author soh#zolex
 *
 */
public class Vector2 {
	
	public static float TO_RADIANS = (1 / 180.0f) * (float) Math.PI;
	public static float TO_DEGREES = (1 / (float) Math.PI) * 180;
	public float x, y;

	/**
	 * Empty constructor
	 */
	public Vector2() {
	}

	/**
	 * Constructor with coordinates
	 * 
	 * @param float x
	 * @param float y
	 */
	public Vector2(float x, float y) {
		
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructor with other vector
	 * 
	 * @param Vector2 other
	 */
	public Vector2(Vector2 other) {
		
		this.x = other.x;
		this.y = other.y;
	}

	/**
	 * Copy the vector
	 * 
	 * @return Vector2
	 */
	public Vector2 copy() {
		
		return new Vector2(x, y);
	}

	/**
	 * Set new coordinates for the vector
	 * 
	 * @param float x
	 * @param float y
	 * @return Vector2
	 */
	public Vector2 set(float x, float y) {
		
		this.x = x;
		this.y = y;
		return this;
	}

	/**
	 * Set the vector using another vector
	 * 
	 * @param Vector2 other
	 * @return Vector2
	 */
	public Vector2 set(Vector2 other) {
		
		this.x = other.x;
		this.y = other.y;
		return this;
	}

	/**
	 * Add values to the coordinates of the vector
	 * @param float x
	 * @param float y
	 * @return Vector2
	 */
	public Vector2 add(float x, float y) {
		
		this.x += x;
		this.y += y;
		return this;
	}

	/**
	 * Add another vector to this one
	 * 
	 * @param Vector2 other
	 * @return Vector2
	 */
	public Vector2 add(Vector2 other) {
		
		this.x += other.x;
		this.y += other.y;
		return this;
	}

	/**
	 * Subtract coordinates from the vector
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector2 subtract(float x, float y) {
		
		this.x -= x;
		this.y -= y;
		return this;
	}
	
	/**
	 * Subtract another vector from the vector
	 * 
	 * @param Vector2 other
	 * @return Vector2
	 */
	public Vector2 subtract(Vector2 other) {
		
		this.x -= other.x;
		this.y -= other.y;
		return this;
	}
	
	/**
	 * Create the dotproduct of the vector and another one
	 * 
	 * @param Vector2 other
	 * @return Vector2
	 */
	public float dotProduct(Vector2 other) {
		
		return this.x * other.x + this.y * other.y;
	}

	/**
	 * Multiply the vector by a scalar
	 * 
	 * @param float scalar
	 * @return Vector2
	 */
	public Vector2 multiply(float scalar) {
		
		this.x *= scalar;
		this.y *= scalar;
		return this;
	}

	/**
	 * Get the length of the vector
	 * 
	 * @return float
	 */
	public float length() {
		
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * Normalize the vector
	 * 
	 * @return Vector2
	 */
	public Vector2 normalize() {
		
		float len = length();
		if (len != 0) {
			this.x /= len;
			this.y /= len;
		}
		return this;
	}

	/**
	 * Get the angle of the vector
	 * 
	 * @return float
	 */
	public float angle() {
		
		float angle = (float) Math.atan2(y, x) * TO_DEGREES;
		if (angle < 0) {
			
			angle += 360;
		}
		
		return angle;
	}

	/**
	 * Rotate the vector by an angle
	 * 
	 * @param float angle
	 * @return Vector2
	 */
	public Vector2 rotate(float angle) {
		
		float rad = angle * TO_RADIANS;
		float cos = FloatMath.cos(rad);
		float sin = FloatMath.sin(rad);

		float newX = this.x * cos - this.y * sin;
		float newY = this.x * sin + this.y * cos;

		this.x = newX;
		this.y = newY;

		return this;
	}

	/**
	 * Calculate the distance to another verctor
	 * 
	 * @param Vector2 other
	 * @return float
	 */
	public float distance(Vector2 other) {
		
		float distX = this.x - other.x;
		float distY = this.y - other.y;
		return FloatMath.sqrt(distX * distX + distY * distY);
	}

	/**
	 * Calculate the distance to coordinates
	 * 
	 * @param float x
	 * @param float y
	 * @return float
	 */
	public float distance(float x, float y) {
		
		float distX = this.x - x;
		float distY = this.y - y;
		return FloatMath.sqrt(distX * distX + distY * distY);
	}

	/**
	 * Return the squared distance to another vector
	 * 
	 * @param Vector2 other
	 * @return float
	 */
	public float distanceSquared(Vector2 other) {
		
		float distX = this.x - other.x;
		float distY = this.y - other.y;
		return distX * distX + distY * distY;
	}

	/**
	 * Return the squared distance to coordinates
	 * 
	 * @param float x
	 * @param float y
	 * @return float
	 */
	public float distanceSquared(float x, float y) {
		
		float distX = this.x - x;
		float distY = this.y - y;
		return distX * distX + distY * distY;
	}
}
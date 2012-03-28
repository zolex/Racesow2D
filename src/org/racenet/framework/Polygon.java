package org.racenet.framework;

public class Polygon {

	public Line[] borders;
	
	public Polygon(Vector2 ... points) {
		
		int length = points.length;
		this.borders = new Line[length];
		for (int i = 0; i < length; i++) {
			
			this.borders[i] = new Line(points[i], points[i == length - 1 ? 0 : i + 1]);
		}
	}
	
	public boolean intersect(Polygon other) {
		
		int tLength = this.borders.length;
		int oLength = other.borders.length;
		for (int t = 0; t < tLength; t++) {
			
			for (int o = 0; o < oLength; o++) {
				
				if (this.borders[t].intersect(other.borders[o])) {
					
					return true;
				}
			}
		}
		
		return false;
	}
	
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
	
	public void setPosition(Vector2 position) {
		
		int length = this.borders.length;
		
		float diffX = position.x - this.borders[0].p1.x;
		float diffY = position.y - this.borders[0].p1.y;
		
		for (int i = 0; i < length; i++) {
			
			this.borders[i].p1.x += diffX;
			this.borders[i].p1.y += diffY;
			this.borders[i].p2.x += diffX;
			this.borders[i].p2.y += diffY;
		}
	}
}

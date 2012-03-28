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
}

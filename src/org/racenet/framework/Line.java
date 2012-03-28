package org.racenet.framework;

public class Line {

	public Vector2 p1;
	public Vector2 p2;
	
	public Line(Vector2 p1, Vector2 p2) {
		
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public boolean intersect(Line other) {
		
		float l1l = this.p1.x < this.p2.x ? this.p1.x : this.p2.x;
		float l1r = this.p1.x > this.p2.x ? this.p1.x : this.p2.x;
		float l1b = this.p1.y < this.p2.y ? this.p1.y : this.p2.y;
		float l1t = this.p1.y > this.p2.y ? this.p1.y : this.p2.y;
		
		float l2l = other.p1.x < other.p2.x ? other.p1.x : other.p2.x;
		float l2r = other.p1.x > other.p2.x ? other.p1.x : other.p2.x;
		float l2b = other.p1.y < other.p2.y ? other.p1.y : other.p2.y;
		float l2t = other.p1.y > other.p2.y ? other.p1.y : other.p2.y;
		
		float px = ((this.p1.x * this.p2.y - this.p1.y * this.p2.x) * (other.p1.x - other.p2.x) -
					(this.p1.x - this.p2.x) * (other.p1.x * other.p2.y - other.p1.y * other.p2.x)) /
					((this.p1.x - this.p2.x) * (other.p1.y - other.p2.y) - (this.p1.y - this.p2.y) *
					(other.p1.x - other.p2.x));
		
		float py = ((this.p1.x * this.p2.y - this.p1.y * this.p2.x) * (other.p1.y - other.p2.y) -
					(this.p1.y - this.p2.y) * (other.p1.x * other.p2.y - other.p1.y * other.p2.x)) /
					((this.p1.x - this.p2.x) * (other.p1.y - other.p2.y) - (this.p1.y - this.p2.y) *
					(other.p1.x - other.p2.x));
		
		if ((px < l1l || px > l1r || px < l2l || px > l2r || py < l1b || py > l1t || py < l2b || py > l2t)) {
			
			return false;
			
		} else {
			
			return true;
		}
	}
}

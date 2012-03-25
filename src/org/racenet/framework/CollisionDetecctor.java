package org.racenet.framework;

public class CollisionDetecctor {

	public static final short FROM_TOP = 1;
	public static final short FROM_LEFT = 2;
	
	public static boolean circleCollision(Circle c1, Circle c2) {
		
		float distance = c1.center.distanceSquared(c2.center);
		float radiusSum = c1.radius + c2.radius;
		return distance <= radiusSum * radiusSum;
	}
	
	public static int rectangleCollision(Rectangle r1, Rectangle r2) {
		
		if (r1.lowerLeft.x < r2.lowerLeft.x + r2.width &&
			r1.lowerLeft.x + r1.width > r2.lowerLeft.x &&
			r1.lowerLeft.y < r2.lowerLeft.y + r2.height &&
			r1.lowerLeft.y + r1.height > r2.lowerLeft.y) {
			
			if (r1.lowerLeft.x + r1.width > r2.lowerLeft.x &&
				r1.lowerLeft.x > r2.lowerLeft.x && r1.lowerLeft.x < r2.lowerLeft.x + r2.height &&
				r1.height > r2.height / 2) {
				
				return FROM_LEFT;
			
			} else {
			
				return FROM_TOP;
			}
		}
		
		return 0;
	}
	
	public static boolean rectangleCircleCollision(Rectangle r, Circle c) {
		
		float closestX = c.center.x;
		float closestY = c.center.y;
		
		if (c.center.x < r.lowerLeft.x) {
			
			closestX = r.lowerLeft.x;
		
		} else if (c.center.x > r.lowerLeft.x + r.width) {
			
			closestX = r.lowerLeft.x + r.width;
		}
		
		if (c.center.y < r.lowerLeft.y) {
			
			closestY = r.lowerLeft.y;
					
		} else if (c.center.y > r.lowerLeft.y + r.height) {
			
			closestY = r.lowerLeft.y + r.height;
		}
		
		return c.center.distanceSquared(closestX, closestY) < c.radius * c.radius;
	}
	
	public static boolean circlePointCollision(Circle c, Vector2 p) {
		
        return c.center.distanceSquared(p) < c.radius * c.radius;
    }
    
    public static boolean circlePointCollision(Circle c, float x, float y) {
    	
        return c.center.distanceSquared(x, y) < c.radius * c.radius;
    }
    
    public static boolean rectanglePointCollosion(Rectangle r, Vector2 p) {
    	
        return r.lowerLeft.x <= p.x && r.lowerLeft.x + r.width >= p.x &&
               r.lowerLeft.y <= p.y && r.lowerLeft.y + r.height >= p.y;
    }
    
    public static boolean rectanglePointCollosion(Rectangle r, float x, float y) {
    	
        return r.lowerLeft.x <= x && r.lowerLeft.x + r.width >= x &&
               r.lowerLeft.y <= y && r.lowerLeft.y + r.height >= y;
    }
}
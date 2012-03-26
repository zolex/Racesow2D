package org.racenet.racesow;

import java.util.ArrayList;
import java.util.List;

import org.racenet.framework.GameObject;
import org.racenet.framework.Mesh;
import org.racenet.framework.SpatialHashGrid;

public class Map {
	
	List<Mesh> front = new ArrayList<Mesh>();
	List<Mesh> back = new ArrayList<Mesh>();
	SpatialHashGrid frontGrid;
	
	public Map() {
		
		frontGrid = new SpatialHashGrid(1000, 200, 50);
	}
	
	public void addFront(Mesh mesh) {
		
		this.front.add(mesh);
		this.frontGrid.insertStaticObject(mesh);
	}
	
	public void addFront(Mesh mesh, boolean debug) {
		
		this.front.add(mesh);
		this.frontGrid.insertStaticObject(mesh);
	}
	
	public Mesh getFront(int i) {
		
		return this.front.get(i);
	}
	
	public int numFront() {
		
		return this.front.size();
	}
	
	public void addBack(Mesh mesh) {
		
		this.back.add(mesh);
	}
	
	public Mesh getBack(int i) {
		
		return this.back.get(i);
	}
	
	public int numBack() {
		
		return this.back.size();
	}
	
public Mesh getGround(GameObject o) {
		
		int tallestPart = 0;
		float tallestHeight = 0;
		
		List<GameObject> colliders = frontGrid.getPotentialColliders(o);
		int length = colliders.size();
		if (length == 0) return null;
		for (int i = 0; i < length; i++) {
			
			GameObject part = colliders.get(i);
			if (o.position.x >= part.position.x && o.position.x <= part.position.x + part.bounds.width) {
				
				float height = part.position.y + part.bounds.height;
				if (height > tallestHeight) {
					
					tallestHeight = height;
					tallestPart = i;
				}
			}
		}
		
		return (Mesh)colliders.get(tallestPart);
	}

	public List<GameObject> getPotentialColliders(GameObject o) {
		
		return frontGrid.getPotentialColliders(o);
	}
	
	public void draw() {
		
		int length = this.numBack();
		for (int i = 0; i < length; i++) {
			
			this.getBack(i).draw();
		}
		
		length = this.numFront();
		for (int i = 0; i < length; i++) {
			
			this.getFront(i).draw();
		}
	}
}

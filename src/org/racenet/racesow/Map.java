package org.racenet.racesow;

import java.util.ArrayList;
import java.util.List;

import org.racenet.framework.Mesh;
import org.racenet.framework.Vector2;

public class Map {
	
	List<Mesh> front = new ArrayList<Mesh>();
	List<Mesh> back = new ArrayList<Mesh>();
	
	public Map() {
		
	}
	
	public void addFront(Mesh mesh) {
		
		this.front.add(mesh);
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
	
	public Mesh getGround(Vector2 position) {
		
		int tallestPart = 0;
		float tallestHeight = 0;
		int length = this.numFront();
		for (int i = 0; i < length; i++) {
			
			Mesh part = this.getFront(i);
			if (position.x >= part.position.x && position.x <= part.position.x + part.bounds.width) {
				
				float height = part.position.y + part.bounds.height;
				if (height > tallestHeight) {
					
					tallestHeight = height;
					tallestPart = i;
				}
			}
		}
		
		return this.getFront(tallestPart);
	}
	
	public void draw() {
		
		int length = this.numFront();
		for (int i = 0; i < length; i++) {
			
			this.getFront(i).draw();
		}
		
		length = this.numBack();
		for (int i = 0; i < length; i++) {
			
			this.getBack(i).draw();
		}
	}
}

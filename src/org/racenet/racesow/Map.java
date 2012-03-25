package org.racenet.racesow;

import java.util.ArrayList;
import java.util.List;

import org.racenet.framework.Mesh;
import org.racenet.framework.Vector2;

public class Map {
	
	List<Mesh> meshes = new ArrayList<Mesh>();
	
	public Map() {
		
		
	}
	
	public void addMesh(Mesh mesh) {
		
		this.meshes.add(mesh);
	}
	
	public Mesh getGround(Vector2 position) {
		
		int tallestPart = 0;
		float tallestHeight = 0;
		int length = this.numMeshes();
		for (int i = 0; i < length; i++) {
			
			Mesh part = this.getMesh(i);
			if (position.x >= part.position.x && position.x <= part.position.x + part.bounds.width) {
				
				float height = part.position.y + part.bounds.height;
				if (height > tallestHeight) {
					
					tallestHeight = height;
					tallestPart = i;
				}
			}
		}
		
		return this.getMesh(tallestPart);
	}
	
	public Mesh getMesh(int i) {
		
		return this.meshes.get(i);
	}
	
	public int numMeshes() {
		
		return this.meshes.size();
	}
}

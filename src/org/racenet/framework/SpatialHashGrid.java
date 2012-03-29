package org.racenet.framework;

import java.util.ArrayList;
import java.util.List;


import android.util.FloatMath;

public class SpatialHashGrid {
	
    List<GameObject>[] cells;
    int cellsPerRow;
    int cellsPerCol;
    float cellSize;
    int numCells;
    int[] cellIds;
    List<GameObject> foundObjects;
    
    @SuppressWarnings("unchecked")
    public SpatialHashGrid(float worldWidth, float worldHeight, float cellSize) {
    	
        this.cellSize = cellSize;
        this.cellsPerRow = (int)FloatMath.ceil(worldWidth/cellSize);
        this.cellsPerCol = (int)FloatMath.ceil(worldHeight/cellSize);
        this.numCells = cellsPerRow * cellsPerCol;
        this.cellIds = new int[this.numCells];
        this.cells = new List[this.numCells];
        for (int i = 0; i < this.numCells; i++) {
        	
            cells[i] = new ArrayList<GameObject>();
        }
        
        foundObjects = new ArrayList<GameObject>();
    }
    
    public void insertStaticObject(GameObject obj) {
    	
    	int[] cellIds = getCellIds(obj);
    	int i = 0;
    	int cellId = -1;
    	while (i < this.numCells && (cellId = cellIds[i++]) != -1) {
    		
    		cells[cellId].add(obj);
    	}
    }
    
    public void removeObject(GameObject obj) {
    	
        int[] cellIds = getCellIds(obj);
        int i = 0;
        int cellId = -1;
        while (i < this.numCells && (cellId = cellIds[i++]) != -1) {
        	
            cells[cellId].remove(obj);
        }
    }
    
    public List<GameObject> getPotentialColliders(GameObject obj) {
    	
        foundObjects.clear();
        int[] cellIds = getCellIds(obj);
        int i = 0;
        int cellId = -1;
        while (i < this.numCells && (cellId = cellIds[i++]) != -1) {
            
            int len = cells[cellId].size();
            for (int j = 0; j < len; j++) {
            	
                GameObject collider = cells[cellId].get(j);
                if(!foundObjects.contains(collider)) {
                    
                	foundObjects.add(collider);
                }
            }
        }
        
        return foundObjects;
    }
    
    public int[] getCellIds(GameObject obj) {
    	
    	Vector2 position = obj.getPosition();
        int x1 = (int)FloatMath.floor(position.x / cellSize);
        int y1 = (int)FloatMath.floor(position.y / cellSize);
        int x2 = (int)FloatMath.floor((position.x + obj.getWidth()) / cellSize);
        int y2 = (int)FloatMath.floor((position.y + obj.getHeight()) / cellSize);
        
        int i = 0;            
        for (int xn = x1; xn <= x2; xn++) {
        	
			for (int yn = y1; yn <= y2; yn++) {
				
				if (xn >= 0 && xn < cellsPerRow && yn >= 0 && yn < cellsPerCol) {
			     
					cellIds[i++] = xn + yn * cellsPerRow;
				}
			}
        }
        
        while (i < this.numCells) cellIds[i++] = -1;
        
        return cellIds;
    }
}
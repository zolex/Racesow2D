package org.racenet.framework;

import java.util.ArrayList;
import java.util.List;


import android.util.FloatMath;

public class SpatialHashGrid {
	
    List<GameObject>[] dynamicCells;
    List<GameObject>[] staticCells;
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
        dynamicCells = new List[this.numCells];
        staticCells = new List[this.numCells];
        for (int i = 0; i < this.numCells; i++) {
        	
            dynamicCells[i] = new ArrayList<GameObject>(10);
            staticCells[i] = new ArrayList<GameObject>(10);
        }
        
        foundObjects = new ArrayList<GameObject>(10);
    }
    
    public void insertStaticObject(GameObject obj) {
    	
    	int[] cellIds = getCellIds(obj);
    	int i = 0;
    	int cellId = -1;
    	while (i < this.numCells && (cellId = cellIds[i++]) != -1) {
    		
    		staticCells[cellId].add(obj);
    	}
    }
    
    public void insertDynamicObject(GameObject obj) {
    	
        int[] cellIds = getCellIds(obj);
        int i = 0;
        int cellId = -1;
        while (i < this.numCells && (cellId = cellIds[i++]) != -1) {
        	
            dynamicCells[cellId].add(obj);
        }
    }
    
    public void removeObject(GameObject obj) {
    	
        int[] cellIds = getCellIds(obj);
        int i = 0;
        int cellId = -1;
        while (i < this.numCells && (cellId = cellIds[i++]) != -1) {
        	
            dynamicCells[cellId].remove(obj);
            staticCells[cellId].remove(obj);
        }
    }
    
    public void clearDynamicCells(GameObject obj) {
    	
        int len = dynamicCells.length;
        for (int i = 0; i < len; i++) {
            dynamicCells[i].clear();
        }
    }
    
    public List<GameObject> getPotentialColliders(GameObject obj) {
    	
        foundObjects.clear();
        int[] cellIds = getCellIds(obj);
        int i = 0;
        int cellId = -1;
        while (i < this.numCells && (cellId = cellIds[i++]) != -1) {
        	
            int len = dynamicCells[cellId].size();
            for (int j = 0; j < len; j++) {
            	
                GameObject collider = dynamicCells[cellId].get(j);
                if (!foundObjects.contains(collider)) {
                
                	foundObjects.add(collider);
                }
            }
            
            len = staticCells[cellId].size();
            for (int j = 0; j < len; j++) {
            	
                GameObject collider = staticCells[cellId].get(j);
                if(!foundObjects.contains(collider)) {
                    
                	foundObjects.add(collider);
                }
            }
        }
        
        return foundObjects;
    }
    
    public int[] getCellIds(GameObject obj) {
    	
        int x1 = (int)FloatMath.floor(obj.bounds.lowerLeft.x / cellSize);
        int y1 = (int)FloatMath.floor(obj.bounds.lowerLeft.y / cellSize);
        int x2 = (int)FloatMath.floor((obj.bounds.lowerLeft.x + obj.bounds.width) / cellSize);
        int y2 = (int)FloatMath.floor((obj.bounds.lowerLeft.y + obj.bounds.height) / cellSize);
        
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
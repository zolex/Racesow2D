package org.racenet.framework;

import java.util.ArrayList;
import java.util.List;


import android.util.FloatMath;

/**
 * A grid for pre-collision detection.
 * Groups the given objects into cells.
 * 
 * @author soh#zolex
 *
 */
public class SpatialHashGrid {
	
    List<GameObject>[] cells;
    int cellsPerRow;
    int cellsPerCol;
    float cellSize;
    int numCells;
    int[] cellIds;
    List<GameObject> foundObjects;
    
    @SuppressWarnings("unchecked")
    /**
     * Constructor
     * 
     * @param float worldWidth
     * @param float worldHeight
     * @param float cellSize
     */
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
    
    /**
     * Insert an object into the grid
     * 
     * @param GameObject obj
     */
    public void insertStaticObject(GameObject obj) {
    	
    	int[] cellIds = getCellIds(obj);
    	int i = 0;
    	int cellId = -1;
    	while (i < this.numCells && (cellId = cellIds[i++]) != -1) {
    		
    		cells[cellId].add(obj);
    	}
    }
    
    /**
     * Remove an object from the grid
     * 
     * @param GameObject obj
     */
    public void removeObject(GameObject obj) {
    	
        int[] cellIds = getCellIds(obj);
        int i = 0;
        int cellId = -1;
        while (i < this.numCells && (cellId = cellIds[i++]) != -1) {
        	
            cells[cellId].remove(obj);
        }
    }
    
    /**
     * Get a list of objects which are in the
     * same cell as the given object
     * 
     * @param GabeObject obj
     * @return List<GameObject>
     */
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
    
    /**
     * Get the IDs of the cells an object is inside of
     * 
     * @param GameObject obj
     * @return int[]
     */
    public int[] getCellIds(GameObject obj) {
    	
    	Vector2 position = obj.getPosition();
        int x1 = (int)FloatMath.floor(position.x / cellSize);
        int y1 = (int)FloatMath.floor(position.y / cellSize);
        int x2 = (int)FloatMath.floor((position.x + obj.width) / cellSize);
        int y2 = (int)FloatMath.floor((position.y + obj.height) / cellSize);
        
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
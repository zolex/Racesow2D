package org.racenet.framework;

import java.util.ArrayList;
import java.util.List;

/**
 * A pool of reusable objects
 * 
 * @author soh#zolex
 *
 * @param <T>
 */
public class Pool<T> {

	/**
	 * Interface
	 * 
	 * @author soh#zolex
	 *
	 * @param <T>
	 */
	public interface PoolObjectFactory<T> {
		
		public T createObject();
	}
	
	private final List<T> freeObjects;
	private final PoolObjectFactory<T> factory;
	private final int maxSize;
	
	/**
	 * Constructor
	 * 
	 * @param PoolObjectFactory<T> f
	 * @param int s
	 */
	public Pool(PoolObjectFactory<T> f, int s) {
		
		factory = f;
		maxSize = s;
		freeObjects = new ArrayList<T>(maxSize);
	}
	
	/**
	 * Get the number of objects in he pool
	 * 
	 * @return int
	 */
	public int length() {
		
		return freeObjects.size();
	}
	
	/**
	 * Get an object from the pool
	 * 
	 * @param int index
	 * @return T
	 */
	public T get(int index) {
		
		return freeObjects.get(index);
	}
	
	/**
	 * Create an object in the pool
	 * 
	 * @return T
	 */
	public T newObject() {
		
		T object = null;
		if (freeObjects.size() == 0) {
			
			object = factory.createObject();
			
		} else {
			
			object = freeObjects.remove(freeObjects.size() - 1);
		}
		
		return object;
	}
	
	/**
	 * Add an object to the pool
	 * 
	 * @param T object
	 */
	public void free(T object) {
		
		if (freeObjects.size() < maxSize) {
			
			freeObjects.add(object);
		}
	}
}

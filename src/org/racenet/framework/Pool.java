package org.racenet.framework;

import java.util.ArrayList;
import java.util.List;

public class Pool<T> {

	public interface PoolObjectFactory<T> {
		
		public T createObject();
	}
	
	private final List<T> freeObjects;
	private final PoolObjectFactory<T> factory;
	private final int maxSize;
	
	public Pool(PoolObjectFactory<T> f, int s) {
		
		factory = f;
		maxSize = s;
		freeObjects = new ArrayList<T>(maxSize);
	}
	
	public int length() {
		
		return freeObjects.size();
	}
	
	public T get(int index) {
		
		return freeObjects.get(index);
	}
	
	public T newObject() {
		
		T object = null;
		if (freeObjects.size() == 0) {
			
			object = factory.createObject();
			
		} else {
			
			object = freeObjects.remove(freeObjects.size() - 1);
		}
		
		return object;
	}
	
	public void free(T object) {
		
		if (freeObjects.size() < maxSize) {
			
			freeObjects.add(object);
		}
	}
}

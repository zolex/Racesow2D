package org.racenet.framework;

import java.util.ArrayList;
import java.util.List;

public class FifoPool<T> {

	public interface PoolObjectFactory<T> {
		
		public T createObject();
	}
	
	private final List<T> pool;
	private final PoolObjectFactory<T> factory;
	private final int maxSize;
	private int reuseIndex = 0;
	
	public FifoPool(PoolObjectFactory<T> f, int s) {
		
		factory = f;
		maxSize = s;
		pool = new ArrayList<T>(maxSize);
	}

	public int length() {
		
		return pool.size();
	}
	
	public T get(int index) {
		
		return pool.get(index);
	}
	
	public T newObject() {
		
		T object = null;
		if (pool.size() == maxSize) {
			
			object = pool.get(reuseIndex);
			reuseIndex++;
			if (reuseIndex == maxSize) {
				
				reuseIndex = 0;
			}
			
		} else {
			
			object = factory.createObject();
			pool.add(object);
		}
		
		return object;
	}
	
	public void free() {
		
		pool.clear();
	}
}

package org.racenet.racesow;

import java.io.InputStream;

public interface HttpCallback {

	public abstract void httpCallback(InputStream xmlStream);
}

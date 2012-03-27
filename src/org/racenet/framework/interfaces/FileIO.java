package org.racenet.framework.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileIO {

	public InputStream readAsset(String fileName) throws IOException;
	
	public InputStream readFile(String fileName) throws IOException;
	
	public OutputStream writeFile(String fileName) throws IOException;
	
	public boolean createDirectory(String path);
	
	public String[] listAssets(String dir);
	
	public String[] listFiles(String dir);
}

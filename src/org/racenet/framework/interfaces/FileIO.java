package org.racenet.framework.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for File in- and output
 * 
 * @author soh#zolex
 *
 */
public interface FileIO {

	public InputStream readAsset(String fileName) throws IOException;
	
	public InputStream readFile(String fileName) throws IOException;
	
	public OutputStream writeFile(String fileName) throws IOException;
	
	public boolean deleteFile(String fileName) throws IOException;
	
	public boolean renameFile(String fileName, String newName) throws IOException;
	
	public boolean createDirectory(String path);
	
	public String[] listAssets(String dir);
	
	public String[] listFiles(String dir);
}

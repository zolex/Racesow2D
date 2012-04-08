package org.racenet.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.racenet.framework.interfaces.FileIO;

import android.content.res.AssetManager;
import android.os.Environment;

/**
 * Class to handle android file in- and output
 * 
 * @author so#zolex
 *
 */
public class AndroidFileIO implements FileIO {

	private AssetManager assetManager;
	public static String externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	
	/**
	 * Constructor
	 * 
	 * @param AssetManager am
	 */
	public AndroidFileIO(AssetManager am) {
		
		assetManager = am;
	}
	
	/**
	 * Read a file from the assets
	 * 
	 * @param String fileName
	 * @throws IOException
	 * @reutrn InputStream
	 */
	public InputStream readAsset(String fileName) throws IOException {
		
		return assetManager.open(fileName);
	}

	/**
	 * Read a file from the sd-card
	 * 
	 * @param String fileName
	 * @throws IOException
	 * @return InputStream
	 */
	public InputStream readFile(String fileName) throws IOException {

		return new FileInputStream(externalStoragePath + fileName);
	}

	/**
	 * Get the output stream to write a
	 * file to the sd-card
	 * 
	 * @param fileName
	 * @throws IOException
	 * @return OutputStream
	 */
	public OutputStream writeFile(String fileName) throws IOException {

		return new FileOutputStream(externalStoragePath + fileName);
	}
	
	/**
	 * Delete a file
	 * 
	 * @param String fileName
	 * @return boolean
	 */
	public boolean deleteFile(String fileName) {
		
		File file = new File(externalStoragePath + fileName);
		return file.delete();
	}
	
	/**
	 * Create a directory on the sd-card
	 * 
	 * @param String path
	 * @return boolean
	 */
	public boolean createDirectory(String path) {
		
		File directory = new File(externalStoragePath + path);
		return directory.mkdirs();
	}
	
	/**
	 * Get a list of files in the assets
	 * 
	 * @param String dir
	 * @return String[]
	 */
	public String[] listAssets(String dir) {
		
		try {
			
			return this.assetManager.list(dir);
			
		} catch (IOException e) {
			
			return null;
		}
	}

	/**
	 * Get a list of files on the sd-card
	 * 
	 * @param String dir
	 * @return String[]
	 */
	public String[] listFiles(String dir) {
		
		return new File(externalStoragePath + dir).list();
	}
}

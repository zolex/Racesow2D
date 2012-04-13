package org.racenet.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import org.racenet.helpers.FileCreatedComperator;
import edu.emory.mathcs.backport.java.util.Arrays;

import android.content.res.AssetManager;
import android.os.Environment;

/**
 * Class to handle android file in- and output
 * 
 * @author so#zolex
 *
 */
public class FileIO {

	private AssetManager assetManager;
	public static String externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	private static FileIO __instance;
	
	/**
	 * Constructor
	 * 
	 * @param AssetManager am
	 */
	private FileIO(AssetManager am) {
		
		assetManager = am;
	}
	
	/**
	 * Setup the singleton
	 * 
	 * @param AssetManager am
	 */
	public static void setupInstance(AssetManager am) {
		
		if (__instance == null) {
			
			__instance = new FileIO(am);
		}
	}
	
	/**
	 * Singleton getter
	 * 
	 * @return FileIO
	 */
	public static FileIO getInstance() {
		
		return __instance;
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
	 * Rename a file
	 * 
	 * @param String fileName
	 * @return boolean
	 */
	public boolean renameFile(String fileName, String newName) {
		
		File file = new File(externalStoragePath + fileName);
		File newFile = new File(externalStoragePath + newName);
		return file.renameTo(newFile);
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

	public static short ORDER_NAME = 0;
	public static short ORDER_CREATED = 1;
	
	/**
	 * Get a list of files on the sd-card
	 * 
	 * @param String dir
	 * @return String[]
	 */
	public String[] listFiles(String dir, short orderBy) {
		
		if (orderBy == ORDER_NAME) {
			
			String[] files = new File(externalStoragePath + dir).list();
			Arrays.sort(files, String.CASE_INSENSITIVE_ORDER);
			return files;
			
		} else if (orderBy == ORDER_CREATED) {
		
			@SuppressWarnings("unchecked")
			List<File> fileList = Arrays.asList(new File(externalStoragePath + dir).listFiles());
			Collections.sort(fileList, new FileCreatedComperator());
			int numFiles = fileList.size();
			String[] files = new String[numFiles];
			for (int i = 0; i < numFiles; i++) {
				
				files[i] = fileList.get(i).getName();
			}
			
			return files;
			
		} else return new String[0];
	}
}

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

public class AndroidFileIO implements FileIO {

	private AssetManager assetManager;
	private String externalStoragePath;
	
	public AndroidFileIO(AssetManager am) {
		
		assetManager = am;
		externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	}
	
	public InputStream readAsset(String fileName) throws IOException {
		
		return assetManager.open(fileName);
	}

	public InputStream readFile(String fileName) throws IOException {

		return new FileInputStream(externalStoragePath + fileName);
	}

	public OutputStream writeFile(String fileName) throws IOException {

		return new FileOutputStream(externalStoragePath + fileName);
	}

}

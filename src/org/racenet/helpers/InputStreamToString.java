package org.racenet.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Converts an InputStream to a String
 * 
 * @author soh#zolex
 *
 */
public class InputStreamToString {

	/**
	 * Convert it!
	 * 
	 * @param InputStream in
	 * @return String
	 * @throws IOException
	 */
	public static String convert (InputStream in) throws IOException {
	    	
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        while ((line = bufferedReader.readLine()) != null) {
        	stringBuilder.append(line + "\n");
        }

        bufferedReader.close();
        return stringBuilder.toString();
    }
}

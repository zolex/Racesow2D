package org.racenet.helpers;

import java.io.File;
import java.util.Comparator;

import org.racenet.racesow.models.MapItem;

/**
 * Compares two mapItems
 * 
 * @author soh#zolex
 *
 */
public class FileCreatedComperator implements Comparator<File> {
	 
    public int compare(File f1, File f2) {

       long f1m = f1.lastModified();
       long f2m = f2.lastModified();
       
       if (f1m == f2m) return 0;
       else if (f1m < f2m) return -1;
       else return 1;
    }
}

package org.racenet.helpers;

import java.util.Comparator;

import org.racenet.racesow.models.MapItem;

/**
 * Compares two mapItems
 * 
 * @author soh#zolex
 *
 */
public class MapComperator implements Comparator<MapItem> {
	 
    public int compare(MapItem o1, MapItem o2) {

       return o1.name.compareToIgnoreCase(o2.name);
    }
}

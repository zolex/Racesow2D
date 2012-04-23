package org.racenet.helpers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.racenet.framework.FileIO;
import org.racenet.framework.XMLParser;
import org.racenet.racesow.models.MapItem;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MapList {

	public static List<MapItem> load() {
		
		List<MapItem> mapList = new ArrayList<MapItem>();
		
		// load the available maps from the assets
		String[] maps = FileIO.getInstance().listAssets("maps");
		for (int i = 0; i < maps.length; i++) {
			
			final String mapName = maps[i];
			if (!mapName.endsWith(".xml")) continue;
			
			
			XMLParser parser = new XMLParser();
			try {
				
				parser.read(FileIO.getInstance().readAsset("maps" + File.separator + mapName));
				
			} catch (IOException e) {
				
				continue;
			}
			
			String name = "";
			String levelshot = "nolevelshot.png";
			NodeList mapn = parser.doc.getElementsByTagName("map");
			if (mapn.getLength() == 1) {
				
				Element map = (Element)mapn.item(0);
				name = parser.getValue(map, "name");
				levelshot = parser.getValue(map, "levelshot");
			}
			
			MapItem item = new MapItem();
			item.name = name;
			item.filename = mapName;
			item.levelshot = levelshot;
			mapList.add(item);
		}
		
		// lod the available maps from the sd-card
		String[] externalMaps = FileIO.getInstance().listFiles("racesow" + File.separator + "maps", FileIO.ORDER_NAME);
		if (externalMaps != null) {
			for (int i = 0; i < externalMaps.length; i++) {
				
				final String mapName = externalMaps[i];
				if (!mapName.endsWith(".xml")) continue;
				
				
				XMLParser parser = new XMLParser();
				try {
					
					parser.read(FileIO.getInstance().readFile("racesow" + File.separator + "maps" + File.separator + mapName));
					
				} catch (IOException e) {
					
					continue;
				}
				
				String name = "";
				String levelshot = "nolevelshot.png";
				NodeList mapn = parser.doc.getElementsByTagName("map");
				if (mapn.getLength() == 1) {
					
					Element map = (Element)mapn.item(0);
					name = parser.getValue(map, "name");
					levelshot = parser.getValue(map, "levelshot");
				}
				
				MapItem item = new MapItem();
				item.name = name;
				item.filename = mapName;
				item.levelshot = levelshot;
				mapList.add(item);
			}
		}
		
		return mapList;
	}
}

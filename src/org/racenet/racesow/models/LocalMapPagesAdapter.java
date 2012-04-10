package org.racenet.racesow.models;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.racenet.framework.AndroidAudio;
import org.racenet.framework.AndroidFileIO;
import org.racenet.framework.XMLParser;
import org.racenet.framework.interfaces.FileIO;
import org.racenet.helpers.MapComperator;
import org.racenet.racesow.R;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Maps PagerAdapter for local highscores
 * 
 * @author soh#zolex
 *
 */
public class LocalMapPagesAdapter extends PagerAdapter {

	Context context;
	List<MapItem> maps = new ArrayList<MapItem>();
	
	/**
	 * Constructor
	 * 
	 * @param Context context
	 * @param FileIO fileIO
	 */
	public LocalMapPagesAdapter(Context context, FileIO fileIO) {
		
		this.context = context;

		// read all maps from the assets
		String[] maps = fileIO.listAssets("maps");
		for (int i = 0; i < maps.length; i++) {
			
			final String mapName = maps[i];
			
			// exclude non-xml files and the tutorial map
			if (!mapName.endsWith(".xml") | mapName.equals("tutorial.xml")) continue;
			
			XMLParser parser = new XMLParser();
			try {
				
				parser.read(fileIO.readAsset("maps" + File.separator + mapName));
				
			} catch (IOException e) {
				
				continue;
			}
			
			NodeList mapn = parser.doc.getElementsByTagName("map");
			if (mapn.getLength() == 1) {
				
				Element map = (Element)mapn.item(0);
				MapItem mapItem = new MapItem();
				mapItem.name = parser.getValue(map, "name");
				mapItem.filename = mapName;
				this.maps.add(mapItem);
			}
		}
		
		// read all maps from the sd-card
		String[] externalMaps = fileIO.listFiles("racesow" + File.separator + "maps", AndroidFileIO.ORDER_NAME);
		if (externalMaps != null) {
			for (int i = 0; i < externalMaps.length; i++) {
				
				final String mapName = externalMaps[i];
				
				// exclude non-xml files
				if (!mapName.endsWith(".xml")) continue;
				
				XMLParser parser = new XMLParser();
				try {
					
					parser.read(fileIO.readFile("racesow" + File.separator + "maps" + File.separator + mapName));
					
				} catch (IOException e) {
					
					continue;
				}
				
				NodeList mapn = parser.doc.getElementsByTagName("map");
				if (mapn.getLength() == 1) {
					
					Element map = (Element)mapn.item(0);
					MapItem mapItem = new MapItem();
					mapItem.name = parser.getValue(map, "name");
					mapItem.filename = mapName;
					this.maps.add(mapItem);
				}
			}
		}
		
		// order the maps by name
		Collections.sort(this.maps, new MapComperator());
	}
	
	@Override
	/**
	 * Create the view for a single page in the ViewPager
	 * 
	 * @param ViewGroup container
	 * @param int position
	 * @return Object
	 */
	public Object instantiateItem(ViewGroup container, int position) {

		RelativeLayout layout = (RelativeLayout)View.inflate(context, R.layout.mapscores, null);
		ListView list = (ListView)layout.findViewById(R.id.list);
		TextView name = (TextView)layout.findViewById(R.id.title);
		
		if (position < this.getCount() - 1) {
		
			TextView next = (TextView)layout.findViewById(R.id.next);
			next.setText("→");
		}
		
		if (position > 0) {
		
			TextView prev = (TextView)layout.findViewById(R.id.prev);
			prev.setText("←");
		}
		
		name.setText(this.maps.get(position).name);
		LocalScoresAdapter adapter = new LocalScoresAdapter(context, this.maps.get(position).filename);
		list.setAdapter(adapter);
		container.addView(layout);
		return layout;
	}
	
	@Override
	/**
	 * Remove a page from the container
	 */
	public void destroyItem (ViewGroup container, int position, Object object) {
		
		container.removeView((View)object);
	}
	
	@Override
	/**
	 * Get the number of pages
	 * 
	 * @return int
	 */
	public int getCount() {
	
		return this.maps.size();
	}

	@Override
	/**
	 * Check if a page belongs to a view
	 * 
	 * @param View view
	 * @param Object object
	 * @return boolean
	 */
	public boolean isViewFromObject(View view, Object object) {
		
		return view == (View)object;
	}
}

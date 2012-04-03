package org.racenet.racesow.models;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.racenet.framework.XMLParser;
import org.racenet.framework.interfaces.FileIO;
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

public class MapPagesAdapter extends PagerAdapter {

	Context context;
	List<MapItem> maps = new ArrayList<MapItem>();
	
	public MapPagesAdapter(Context context, FileIO fileIO) {
		
		this.context = context;

		String[] maps = fileIO.listAssets("maps");
		for (int i = 0; i < maps.length; i++) {
			
			final String mapName = maps[i];
			if (!mapName.endsWith(".xml")) continue;
			
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
		
		String[] externalMaps = fileIO.listFiles("racesow" + File.separator + "maps");
		if (externalMaps != null) {
			for (int i = 0; i < externalMaps.length; i++) {
				
				final String mapName = externalMaps[i];
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
		
		Collections.sort(this.maps, new Comparator(){
			 
            public int compare(Object o1, Object o2) {

               return ((MapItem)o1).name.compareToIgnoreCase(((MapItem)o2).name);
            }
        });
	}
	
	@Override
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
	public void destroyItem (ViewGroup container, int position, Object object) {
		
		container.removeView((View)object);
	}
	
	@Override
	public int getCount() {
	
		return this.maps.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		
		return view == (View)object;
	}

}

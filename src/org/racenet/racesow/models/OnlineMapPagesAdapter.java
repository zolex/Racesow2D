package org.racenet.racesow.models;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.racenet.framework.XMLParser;
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
 * Maps PagerAdapter for online highscores
 * 
 * @author soh#zolex
 *
 */
public class OnlineMapPagesAdapter extends PagerAdapter {

	Context context;
	List<MapItem> maps = new ArrayList<MapItem>();
	
	/**
	 * Constructor
	 * 
	 * @param Context context
	 * @param InputStream xmlStream
	 */
	public OnlineMapPagesAdapter(Context context, InputStream xmlStream) {
		
		this.context = context;
	    
        XMLParser parser = new XMLParser();
		parser.read(xmlStream);
		
		NodeList maps = parser.doc.getElementsByTagName("map");
		
		// read all maps from the XML stream
		int numMaps = maps.getLength();
		for (int i = 0; i < numMaps; i++) {
			
			MapItem item = new MapItem();
			item.name = parser.getNodeValue((Element)maps.item(i));
			this.maps.add(item);
		}
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

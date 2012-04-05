package org.racenet.racesow.models;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.racenet.framework.XMLParser;
import org.racenet.racesow.R;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Maps PagerAdapter for online highscores
 * 
 * @author soh#zolex
 *
 */
public class OnlineMapsAdapter implements ListAdapter {

	Context context;
	List<MapItem> maps = new ArrayList<MapItem>();
	
	/**
	 * Constructor
	 * 
	 * @param Context context
	 * @param InputStream xmlStream
	 */
	public OnlineMapsAdapter(Context context, InputStream xmlStream) {
		
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
	

	/**
	 * Get the number of scores
	 * 
	 * @return int
	 */
	public int getCount() {

		return this.maps.size();
	}

	/**
	 * Get a score item
	 * 
	 * @param int pos
	 * @return Object
	 */
	public Object getItem(int pos) {
		
		return this.maps.get(pos);
	}

	/**
	 * Get the id of a score
	 * 
	 * @param in pos
	 * @retrn long
	 */
	public long getItemId(int pos) {
		
		return 0;
	}

	/**
	 * Get the type of an item view
	 * 
	 * @param int pos
	 * @return int
	 */
	public int getItemViewType(int arg0) {
		
		return arg0;
	}

	/**
	 * Create the view for a single list item.
	 * Load it from an xml layout.
	 * 
	 * @param int pos
	 * @param View view
	 * @param ViewGroup viewGroup
	 * @return View
	 */
	public View getView(int pos, View view, ViewGroup group) {
		
		LinearLayout layout = (LinearLayout)View.inflate(context, R.layout.scoremapitem, null);
		TextView name = (TextView)layout.findViewById(R.id.name);
		
		MapItem item = (MapItem)getItem(pos);
		name.setText(item.name);

        return layout;
	}

	/**
	 * Get the number of different views
	 * 
	 * @return int
	 */
	public int getViewTypeCount() {
		
		return 1;
	}

	/**
	 * Return wheather the items have stable IDs or not
	 * 
	 * @return boolean
	 */
	public boolean hasStableIds() {
		
		return false;
	}

	/**
	 * Return wheather the list is empty or not
	 * 
	 * @return boolean
	 */
	public boolean isEmpty() {
		
		return this.maps.size() == 0;
	}

	/**
	 * No need of a data observer
	 * 
	 * @param DataSetObserver arg0
	 * @return void
	 */
	public void registerDataSetObserver(DataSetObserver arg0) {
		
	}

	/**
	 * No need of a data observer
	 * 
	 * @param DataSetObserver arg0
	 * @return void
	 */
	public void unregisterDataSetObserver(DataSetObserver arg0) {
		
	}

	/**
	 * All items should be selectable
	 * 
	 * @return boolean
	 */
	public boolean areAllItemsEnabled() {

		return true;
	}

	/**
	 * All items should be selectable
	 * 
	 * @param int pos
	 * @return boolean
	 */
	public boolean isEnabled(int arg0) {

		return true;
	}
}

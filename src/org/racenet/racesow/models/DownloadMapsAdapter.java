package org.racenet.racesow.models;

import java.util.List;

import org.racenet.racesow.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Adapter to provide data for the maps
 * available for download
 * 
 * @author soh#zolex
 *
 */
public class DownloadMapsAdapter implements ListAdapter {
	
	private Context context;
	private List<MapItem> mapList;
	
	/**
	 * Constructor
	 * 
	 * @param Context context
	 * @param List<MapItem> mapList
	 */
	public DownloadMapsAdapter(Context context, List<MapItem> mapList) {
		
		this.context = context;
		this.mapList = mapList;
	}

	/**
	 * Get the number of available maps
	 * 
	 * @return int
	 */
	public int getCount() {

		return this.mapList.size();
	}

	/**
	 * Get a map item
	 * 
	 * @param int pos
	 * @return Object
	 */
	public Object getItem(int pos) {
		
		return this.mapList.get(pos);
	}

	/**
	 * Get the ID of a map
	 * 
	 * @param int pos
	 * @return long
	 */
	public long getItemId(int pos) {
		
		return this.mapList.get(pos).id;
	}

	/**
	 * Get the view type of an item
	 * 
	 * @param int arg0
	 * @return int
	 * 
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
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		
		MapItem item = (MapItem)getItem(arg0);
		LinearLayout layout =  (LinearLayout)View.inflate(context, R.layout.mapitem, null);
		TextView name = (TextView)layout.findViewById(R.id.name);
		TextView skill = (TextView)layout.findViewById(R.id.skill);
		TextView author = (TextView)layout.findViewById(R.id.author);
		TextView status = (TextView)layout.findViewById(R.id.status);
		
		name.setText(item.name);
		skill.setText("(" + item.skill + ")");
		
		if (!item.author.equals("")) {
		
			author.setText("by " + item.author);
		}
		
		if (item.installed) {
		
			status.setText("installed");
			
		} else {
		
			status.setText("download now!");
		}
		
		if (item.skill.equals("hard")) {
			
			skill.setTextColor(Color.RED);
			
		} else if (item.skill.equals("medium")) {
			
			skill.setTextColor(Color.YELLOW);
			
		} else if (item.skill.equals("easy")) {
			
			skill.setTextColor(Color.GREEN);
		}

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
		
		return true;
	}

	/**
	 * Return wheather the list is empty or not
	 * 
	 * @return boolean
	 */
	public boolean isEmpty() {
		
		return this.mapList.size() == 0;
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
	 * @param int arg0
	 * @return boolean
	 */
	public boolean isEnabled(int arg0) {

		return true;
	}
}
package org.racenet.racesow.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.racenet.framework.FileIO;
import org.racenet.racesow.R;

import edu.emory.mathcs.backport.java.util.Collections;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Adapter to provide data for the demo list
 * 
 * @author soh#zolex
 *
 */
public class DemoAdapter extends BaseAdapter {
	
	private Context context;
	public List<String> demos = new ArrayList<String>();
	
	/**
	 * Constructor
	 * 
	 * @param Context context
	 * @param List<MapItem> mapList
	 */
	public DemoAdapter(Context context, short orderBy) {
		
		this.context = context;
		
		String[] demos = FileIO.getInstance().listFiles("racesow" + File.separator + "demos", orderBy);
		if (demos != null) {

			for (int i = 0; i < demos.length; i++) {
				
				this.demos.add(demos[i]);
			}
		}
		
		if (orderBy == FileIO.ORDER_CREATED) {
		
			Collections.reverse(this.demos);
		}
	}
	
	/**
	 * Get the number of available maps
	 * 
	 * @return int
	 */
	public int getCount() {

		return this.demos.size();
	}

	/**
	 * Get a map item
	 * 
	 * @param int pos
	 * @return Object
	 */
	public Object getItem(int pos) {
		
		return this.demos.get(pos);
	}

	/**
	 * Get the ID of a map
	 * 
	 * @param int pos
	 * @return long
	 */
	public long getItemId(int pos) {
		
		return pos;
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
		
		String demo = (String)this.getItem(arg0);
		LinearLayout layout =  (LinearLayout)View.inflate(context, R.layout.demoitem, null);
		TextView name = (TextView)layout.findViewById(R.id.name);
		name.setText(demo);
		
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
		
		return this.demos.size() == 0;
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
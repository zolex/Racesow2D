package org.racenet.racesow.models;

import java.util.ArrayList;
import java.util.List;

import org.racenet.racesow.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Adapter to privide the data for
 * the comments listview
 * 
 * @author soh#zolex
 *
 */
public class CreditsAdapter implements ListAdapter {
	
	private Context context;
	private List<String> credits = new ArrayList<String>();
	
	/**
	 * Add some static data to the list
	 * 
	 * @param Context context
	 */
	public CreditsAdapter(Context context) {
		
		this.context = context;
		this.credits.add("Programming: Andreas Linden");
		this.credits.add("Graphics: Benjamin Schmitt");
	}

	/**
	 * Get the numbers of listitems
	 * 
	 * @return int
	 */
	public int getCount() {

		return this.credits.size();
	}

	/**
	 * Get a listitem
	 * 
	 * @param int pos
	 * @return Object
	 */
	public Object getItem(int pos) {
		
		return this.credits.get(pos);
	}

	/**
	 * The credit items do not have IDs
	 * 
	 * @param int pos
	 * @return long
	 */
	public long getItemId(int pos) {
		
		return 0;
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
	public View getView(int pos, View view, ViewGroup group) {
		
		LinearLayout layout = (LinearLayout)View.inflate(context, R.layout.credititem, null);
		TextView credit = (TextView)layout.findViewById(R.id.credit);
		credit.setText((String)getItem(pos));

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
		
		return this.credits.size() == 0;
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
	 * No item should be selectable
	 * 
	 * @return boolean
	 */
	public boolean areAllItemsEnabled() {

		return false;
	}

	/**
	 * No item should be selectable
	 * 
	 * @return boolean
	 */
	public boolean isEnabled(int arg0) {

		return false;
	}
}
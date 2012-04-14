package org.racenet.racesow.models;

import java.util.ArrayList;
import java.util.List;
import org.racenet.racesow.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Adapter to provide the data for the online players
 * 
 * @author soh#zolex
 *
 */
public class OnlinePlayersAdapter extends BaseAdapter {
	
	private Context context;
	private List<PlayerItem> players = new ArrayList<PlayerItem>();
	
	/**
	 * Constructor
	 * 
	 * @param Context context
	 * @param List<ScoreItem> players
	 */
	public OnlinePlayersAdapter(Context context) {
		
		PlayerItem header = new PlayerItem();
		header.position = 0;
		this.players.add(header);
		this.context = context;
	}
	
	/**
	 * Add an item to the adapter
	 * 
	 * @param item
	 */
	public void addItem(PlayerItem item) {
		
		this.players.add(item);
	}

	/**
	 * Get the number of players
	 * 
	 * @return int
	 */
	public int getCount() {

		return this.players.size();
	}

	/**
	 * Get a score item
	 * 
	 * @param int pos
	 * @return Object
	 */
	public Object getItem(int pos) {
		
		return this.players.get(pos);
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
		
		LinearLayout layout;
		if (view == null) {
			
			layout = (LinearLayout)View.inflate(this.context, R.layout.playeritem, null);
			
		} else {
			
			layout = (LinearLayout)view;
		}
		
		TextView position = (TextView)layout.findViewById(R.id.pos);
		TextView name = (TextView)layout.findViewById(R.id.name);
		TextView points = (TextView)layout.findViewById(R.id.points);
		
		PlayerItem item = (PlayerItem)getItem(pos);
		if (item.position == 0) {
			
			position.setText("Pos.");
			position.setBackgroundColor(Color.DKGRAY);
			name.setText("Nickname");
			name.setBackgroundColor(Color.DKGRAY);
			points.setText("Points");
			points.setBackgroundColor(Color.DKGRAY);
			
		} else {
			
			position.setText(item.position + ".");
			position.setBackgroundColor(Color.BLACK);
			name.setText(item.name);
			name.setBackgroundColor(Color.BLACK);
			points.setText(String.valueOf(item.points));
			points.setBackgroundColor(Color.BLACK);
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
		
		return false;
	}

	/**
	 * Return wheather the list is empty or not
	 * 
	 * @return boolean
	 */
	public boolean isEmpty() {
		
		return this.players.size() == 0;
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
	 * @param int pos
	 * @return boolean
	 */
	public boolean isEnabled(int arg0) {

		return false;
	}
}
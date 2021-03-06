package org.racenet.racesow.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
 * Adapter to provide the data for the online scores
 * 
 * @author soh#zolex
 *
 */
public class OnlineScoresAdapter extends BaseAdapter {
	
	private Context context;
	private List<ScoreItem> scores = new ArrayList<ScoreItem>();
	
	/**
	 * Constructor
	 * 
	 * @param Context context
	 * @param List<ScoreItem> scores
	 */
	public OnlineScoresAdapter(Context context) {
		
		ScoreItem header = new ScoreItem();
		header.position = 0;
		this.scores.add(header);
		this.context = context;
	}
	
	/**
	 * Add an item to the adapter
	 * 
	 * @param item
	 */
	public void addItem(ScoreItem item) {
		
		this.scores.add(item);
	}

	/**
	 * Get the number of scores
	 * 
	 * @return int
	 */
	public int getCount() {

		return this.scores.size();
	}

	/**
	 * Get a score item
	 * 
	 * @param int pos
	 * @return Object
	 */
	public Object getItem(int pos) {
		
		return this.scores.get(pos);
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
			
			layout = (LinearLayout)View.inflate(this.context, R.layout.scoreitem, null);
			
		} else {
			
			layout = (LinearLayout)view;
		}
		
		TextView position = (TextView)layout.findViewById(R.id.pos);
		TextView time = (TextView)layout.findViewById(R.id.time);
		TextView player = (TextView)layout.findViewById(R.id.player);
		TextView races = (TextView)layout.findViewById(R.id.races);
		TextView createdAt = (TextView)layout.findViewById(R.id.created_at);
		
		ScoreItem item = (ScoreItem)getItem(pos);
		if (item.position == 0) {
			
			position.setText("Pos.");
			position.setBackgroundColor(Color.DKGRAY);
			player.setText("Nickname");
			player.setBackgroundColor(Color.DKGRAY);
			time.setText("Time");
			time.setBackgroundColor(Color.DKGRAY);
			races.setText("(Races)");
			races.setBackgroundColor(Color.DKGRAY);
			createdAt.setText("Date");
			createdAt.setBackgroundColor(Color.DKGRAY);
			
		} else {
			
			player.setText(item.player);
			player.setBackgroundColor(Color.BLACK);
			races.setText("(" + String.valueOf(item.races) + ")");
			races.setBackgroundColor(Color.BLACK);
			position.setText(String.valueOf(new Integer(item.position)) + ".");
			position.setBackgroundColor(Color.BLACK);
			time.setText(String.format(Locale.US, "%.4f", item.time));
			time.setBackgroundColor(Color.BLACK);
			createdAt.setText(item.created_at);
			createdAt.setBackgroundColor(Color.BLACK);
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
		
		return this.scores.size() == 0;
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
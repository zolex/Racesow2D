package org.racenet.racesow.models;

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
 * Adapter to provide the data for the local scores
 * 
 * @author soh#zolex
 *
 */
public class LocalScoresAdapter implements ListAdapter {
	
	private Context context;
	private List<ScoreItem> scores;
	
	/**
	 * Constructor
	 * 
	 * @param Context context
	 * @param Map map
	 */
	public LocalScoresAdapter(Context context, String map) {
		
		this.context = context;
		this.scores = Database.getInstance(context).getScores(map);
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
	 * Get teh ID of a score item
	 * 
	 * @param int pos
	 * @return long
	 */
	public long getItemId(int pos) {
		
		return this.scores.get(pos).id;
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
	 * Get the view for a single score item
	 * 
	 * @param int pos
	 * @param View view
	 * @param ViewGroup group
	 * @return View
	 */
	public View getView(int pos, View view, ViewGroup group) {
		
		LinearLayout layout = (LinearLayout)View.inflate(context, R.layout.scoreitem, null);
		TextView position = (TextView)layout.findViewById(R.id.pos);
		TextView time = (TextView)layout.findViewById(R.id.time);
		TextView player = (TextView)layout.findViewById(R.id.player);
		TextView createdAt = (TextView)layout.findViewById(R.id.created_at);
		
		ScoreItem item = (ScoreItem)getItem(pos);
		player.setText(item.player);
		position.setText(String.valueOf(new Integer(item.position)) + ".");
		time.setText(String.format("%.4f", item.time));
		createdAt.setText(item.created_at);

        return layout;
	}

	/**
	 * Get the number of different views
	 * 
	 *  @return int
	 */
	public int getViewTypeCount() {
		
		return 1;
	}

	/**
	 * The local scores have stable IDs
	 */
	public boolean hasStableIds() {
		
		return true;
	}

	/**
	 * Return wheather the list is empty or not
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
	 * All items should be selectable
	 * 
	 * @return boolean
	 */
	public boolean areAllItemsEnabled() {

		return false;
	}

	/**
	 * All items should be selectable
	 * 
	 * @param int arg0
	 * @return boolean
	 */
	public boolean isEnabled(int arg0) {

		return false;
	}
}
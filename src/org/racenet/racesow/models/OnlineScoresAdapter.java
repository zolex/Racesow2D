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

public class OnlineScoresAdapter implements ListAdapter {
	
	private Context context;
	private List<ScoreItem> scores;
	
	public OnlineScoresAdapter(Context context, List<ScoreItem> scores) {
		
		this.context = context;
		this.scores = scores;
	}

	public int getCount() {

		return this.scores.size();
	}

	public Object getItem(int pos) {
		
		return this.scores.get(pos);
	}

	public long getItemId(int pos) {
		
		return this.scores.get(pos).id;
	}

	public int getItemViewType(int arg0) {
		
		return arg0;
	}

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

	public int getViewTypeCount() {
		
		return 1;
	}

	public boolean hasStableIds() {
		
		return true;
	}

	public boolean isEmpty() {
		
		return this.scores.size() == 0;
	}

	public void registerDataSetObserver(DataSetObserver arg0) {
		
	}

	public void unregisterDataSetObserver(DataSetObserver arg0) {
		
	}

	public boolean areAllItemsEnabled() {

		return false;
	}

	public boolean isEnabled(int arg0) {

		return false;
	}
}
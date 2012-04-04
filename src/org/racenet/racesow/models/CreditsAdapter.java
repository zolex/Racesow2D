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

public class CreditsAdapter implements ListAdapter {
	
	private Context context;
	private List<String> credits = new ArrayList<String>();
	
	public CreditsAdapter(Context context) {
		
		this.context = context;
		this.credits.add("Idea: Andreas Linden");
		this.credits.add("Programming: Andreas Linden");
		this.credits.add("GFX & Animation: ???????");
	}

	public int getCount() {

		return this.credits.size();
	}

	public Object getItem(int pos) {
		
		return this.credits.get(pos);
	}

	public long getItemId(int pos) {
		
		return 0;
	}

	public int getItemViewType(int arg0) {
		
		return arg0;
	}

	public View getView(int pos, View view, ViewGroup group) {
		
		LinearLayout layout = (LinearLayout)View.inflate(context, R.layout.credititem, null);
		TextView credit = (TextView)layout.findViewById(R.id.credit);
		credit.setText((String)getItem(pos));

        return layout;
	}

	public int getViewTypeCount() {
		
		return 1;
	}

	public boolean hasStableIds() {
		
		return false;
	}

	public boolean isEmpty() {
		
		return this.credits.size() == 0;
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
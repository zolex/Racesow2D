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

public class DownloadMapsAdapter implements ListAdapter {
	
	private Context context;
	private List<MapItem> mapList;
	
	public DownloadMapsAdapter(Context context, List<MapItem> mapList) {
		
		this.context = context;
		this.mapList = mapList;
	}

	public int getCount() {

		return this.mapList.size();
	}

	public Object getItem(int pos) {
		
		return this.mapList.get(pos);
	}

	public long getItemId(int pos) {
		
		return this.mapList.get(pos).id;
	}

	public int getItemViewType(int arg0) {
		
		return arg0;
	}

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

	public int getViewTypeCount() {
		
		return 1;
	}

	public boolean hasStableIds() {
		
		return true;
	}

	public boolean isEmpty() {
		
		return this.mapList.size() == 0;
	}

	public void registerDataSetObserver(DataSetObserver arg0) {
		
	}

	public void unregisterDataSetObserver(DataSetObserver arg0) {
		
	}

	public boolean areAllItemsEnabled() {

		return true;
	}

	public boolean isEnabled(int arg0) {

		return true;
	}
}
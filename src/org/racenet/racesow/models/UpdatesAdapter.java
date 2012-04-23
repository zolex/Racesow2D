package org.racenet.racesow.models;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class UpdatesAdapter extends BaseExpandableListAdapter {
	
	private Context context;
	private List<UpdateItem> updates;
	
	public UpdatesAdapter(Context c) {
		
		context = c;
		updates = Database.getInstance().getAllUpdates();
	}

    public Object getChild(int groupPosition, int childPosition) {
    	
    	UpdateItem update = updates.get(groupPosition);
    	return "to be implemented...";
    			
    }

    public long getChildId(int groupPosition, int childPosition) {
    	
        return 1;
    }

    public int getChildrenCount(int groupPosition) {
    	
        return 1;
    }
    
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
    	
    	AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(context);
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textView.setPadding(10, 10, 10, 10);
        textView.setText(getChild(groupPosition, childPosition).toString());
        return textView;
    }

    public Object getGroup(int groupPosition) {
    	
    	UpdateItem update = updates.get(groupPosition);
        return update.createdAt;
    }

    public int getGroupCount() {
    	
        return updates.size();
    }

    public long getGroupId(int groupPosition) {
    	
        return updates.get(groupPosition).id;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
    	
    	AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 96);
        TextView textView = new TextView(context);
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textView.setPadding(64, 0, 0, 0);
        textView.setText(getGroup(groupPosition).toString());
        return textView;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
    	
        return false;
    }

    public boolean hasStableIds() {
    	
        return true;
    }
    
    @Override
    public void notifyDataSetChanged() {
    	
    	updates = Database.getInstance().getAllUpdates();
    	super.notifyDataSetChanged();
    }

}
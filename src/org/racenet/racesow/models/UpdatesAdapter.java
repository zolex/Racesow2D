package org.racenet.racesow.models;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * Adapter for reading updates from the database
 * 
 * @author soh#zolex
 *
 */
public class UpdatesAdapter extends BaseExpandableListAdapter {
	
	private Context context;
	private List<UpdateItem> updates;
	
	/**
	 * Constructor
	 * 
	 * @param Context c
	 */
	public UpdatesAdapter(Context c) {
		
		context = c;
		updates = Database.getInstance().getAllUpdates();
	}

	/**
	 * Get a chid of a list item
	 * 
	 * @param int groupPosition
	 * @param int childPosition
	 * @return Object
	 */
    public Object getChild(int groupPosition, int childPosition) {
    	
    	UpdateItem update = updates.get(groupPosition);
    	return "to be implemented...";
    			
    }

    /**
     * Get the ID of a cild
     * 
     * @param int groupPosition
     * @param int childPosition
     * @return long
     */
    public long getChildId(int groupPosition, int childPosition) {
    	
        return 1;
    }

    /**
     * Get the number of children
     * 
     * @param int groupPosition
     * @return int
     */
    public int getChildrenCount(int groupPosition) {
    	
        return 1;
    }
    
    /**
     * Get the view for a child
     * 
     * @param int groupPosition
     * @param int childPosition
     * @param boolean isLastChild
     * @param View convertView
     * @param ViewGroup parent
     * @return View
     */
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
    	
    	AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(context);
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textView.setPadding(10, 10, 10, 10);
        textView.setText(getChild(groupPosition, childPosition).toString());
        return textView;
    }

    /**
     * Get a main list item
     * 
     * @param int groupPosition
     * @return Object
     */
    public Object getGroup(int groupPosition) {
    	
    	UpdateItem update = updates.get(groupPosition);
        return update.createdAt;
    }

    /**
     * Get the number of groups
     * 
     * @return int
     */
    public int getGroupCount() {
    	
        return updates.size();
    }

    /**
     * Get the ID of a group
     * 
     * @param int groupPosition
     * @return long
     */
    public long getGroupId(int groupPosition) {
    	
        return updates.get(groupPosition).id;
    }

    /**
     * Get the view for a group
     * 
     * @param int groupPosition
     * @param boolean isExpanded
     * @param View convertView
     * @param ViewGroup parent
     * @return View
     */
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
    	
    	AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 96);
        TextView textView = new TextView(context);
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textView.setPadding(64, 0, 0, 0);
        textView.setText(getGroup(groupPosition).toString());
        return textView;
    }

    /**
     * Check if a child item is selectable
     * 
     * @param int groupPosition
     * @param int childPosition
     * @return boolean
     */
    public boolean isChildSelectable(int groupPosition, int childPosition) {
    	
        return false;
    }

    /**
     * Check if the list has stable IDs
     * 
     * @return boolean
     */
    public boolean hasStableIds() {
    	
        return true;
    }
    
    @Override
    /**
     * Notify the adapter of new data
     */
    public void notifyDataSetChanged() {
    	
    	updates = Database.getInstance().getAllUpdates();
    	super.notifyDataSetChanged();
    }

}
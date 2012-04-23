package org.racenet.racesow.models;

import java.util.List;

import org.racenet.racesow.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
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
    	
    	return ((UpdateItem)this.updates.get(groupPosition)).maps.get(childPosition);
    }

    /**
     * Get the ID of a cild
     * 
     * @param int groupPosition
     * @param int childPosition
     * @return long
     */
    public long getChildId(int groupPosition, int childPosition) {
    	
    	return ((UpdateItem)this.updates.get(groupPosition)).maps.get(childPosition).id;
    }

    /**
     * Get the number of children
     * 
     * @param int groupPosition
     * @return int
     */
    public int getChildrenCount(int groupPosition) {
    	
        return ((UpdateItem)this.updates.get(groupPosition)).maps.size();
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
    	
    	if (convertView == null) {
    		
    		convertView = (RelativeLayout)View.inflate(context, R.layout.updatemapitem, null);
    	}

    	MapUpdateItem map = (MapUpdateItem)getChild(groupPosition, childPosition);
    	TextView name = (TextView)convertView.findViewById(R.id.name);
    	TextView info = (TextView)convertView.findViewById(R.id.info);
    	
    	String infoText = "";
    	int length = map.beatenBy.size();
    	for (int i = 0; i < length; i++) {
    		
    		BeatenByItem beatenBy = map.beatenBy.get(i);
    		infoText += beatenBy.name + " made " + String.valueOf(beatenBy.time);
    		if (i < length - 1) {
    			
    			infoText += "\n";
    		}
    	}
    	
    	info.setText(infoText);
        name.setText("Your time was beaten on " + map.name.replace(".xml", ""));
        return convertView;
    }

    /**
     * Get a main list item
     * 
     * @param int groupPosition
     * @return Object
     */
    public Object getGroup(int groupPosition) {
    	
    	return updates.get(groupPosition);
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
    	
    	if (convertView == null) {
    		
    		convertView = (RelativeLayout)View.inflate(context, R.layout.updateitem, null);
    	}
        
        UpdateItem update = (UpdateItem)getGroup(groupPosition);
        int points = update.oldPoints - update.newPoints;
        String pointsDiff;
        if (points == 0) {
        	
        	pointsDiff = " time was beaten";
        	
        } else if (points < 0) {
        	
        	pointsDiff = " gained " + (-1 * points) + " point" + (points == -1 ? "" : "s");
        	
        } else {
        	
        	pointsDiff = " lost " + points + " point" + (points == 1 ? "" : "s");
        }
        
        TextView title = (TextView)convertView.findViewById(R.id.title);
        title.setText(update.createdAt + " / " + update.name + pointsDiff);
        return convertView;
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
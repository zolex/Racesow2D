package org.racenet.racesow;

import org.racenet.racesow.models.Database;
import org.racenet.racesow.models.UpdatesAdapter;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.Toast;

public class OnlineUpdates extends ExpandableListActivity {

	WakeLock wakeLock;
	UpdatesAdapter mAdapter;
    private static int MENU_ITEM_DELETE = 0;
    //private BroadcastReceiver broadcastReceiver;
    private Database db;
    
    /**
	 * For mostly gapless music
	 */
    public OnlineUpdates() {
		
		super();
		Racesow.resumeMusic();
	}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
    	
    	PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	this.wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "racesow");
    	
    	setContentView(R.layout.onlineupdates);
    	Database.setupInstance(this.getApplicationContext());
        mAdapter = new UpdatesAdapter(this);
        setListAdapter(mAdapter);
        registerForContextMenu(getExpandableListView());
        /*
        broadcastReceiver = new BroadcastReceiver() {
            
        	@Override
            public void onReceive(Context context, Intent intent) {
            	
            	mAdapter.notifyDataSetChanged();
            }
        };
        */
        
        db = Database.getInstance();
    }

    public void onStart() {
    	
    	super.onStart();
    	//registerReceiver(broadcastReceiver, new IntentFilter(MQTTService.UPDATE_RECORDS_ACTION));
    	if(db.countUpdates() == 0) {
    		
    		Toast.makeText(OnlineUpdates.this, "Update list is empty", Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void onStop() {
    	
    	super.onStop();
    	//unregisterReceiver(broadcastReceiver);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo)menuInfo;       
		final String title = ((TextView)((RelativeLayout)info.targetView).findViewById(R.id.title)).getText().toString();
		menu.setHeaderTitle(title);
		menu.add(0, 0, 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	
        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();       
        final String title = ((TextView)((RelativeLayout)info.targetView).findViewById(R.id.title)).getText().toString();
        final int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);       	
        
        new AlertDialog.Builder(OnlineUpdates.this)
	        .setMessage("Do you really want to delete '" + title + "'?")
	        .setPositiveButton("Yes", new OnClickListener() {
				
				public void onClick(DialogInterface arg0, int arg1) {
					
					db.deleteUpdate((int)mAdapter.getGroupId(groupPos));
					mAdapter.notifyDataSetChanged();
					Toast.makeText(OnlineUpdates.this, "Deleted '" + title + "'", Toast.LENGTH_SHORT).show();
				}
			})
			.setNegativeButton("No", null)
	        .show();

        return true;

    }
    
    @Override
	public boolean onPrepareOptionsMenu (Menu menu) {

		if (db.countUpdates() == 0) {
		
			menu.getItem(MENU_ITEM_DELETE).setEnabled(false);
		
		} else {
			
			menu.getItem(MENU_ITEM_DELETE).setEnabled(true);
		}
		
	    return true;
	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.updates, menu);
	    
	    MenuItem delete = menu.getItem(MENU_ITEM_DELETE);
	    delete.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			public boolean onMenuItemClick(MenuItem arg0) {
				
				new AlertDialog.Builder(OnlineUpdates.this)
			        .setMessage("Do you really want to delete all updates?")
			        .setPositiveButton("Yes", new OnClickListener() {
						
						public void onClick(DialogInterface arg0, int arg1) {
							
							db.deleteAllUpdates();
							mAdapter.notifyDataSetChanged();
							Toast.makeText(OnlineUpdates.this, "Deleted all updates", Toast.LENGTH_SHORT).show();
						}
					})
					.setNegativeButton("No", null)
			        .show();
				
				return true;
			}
		});
	    
		return true;
	}
    
    /**
     * Pause the music
     */
    public void onPause() {
		
		super.onPause();
		Racesow.pauseMusic();
	}
    
    /**
	 * Resume music and acquire wakelock
	 */
    public void onResume() {
    	
    	super.onResume();
    	Racesow.resumeMusic();
    	this.wakeLock.acquire();
    }
    
    /**
     * Release the wakelock when leaving the activity
     */
    public void onDestroy() {
    	
    	super.onDestroy();
    	this.wakeLock.release();
    }
    
    /**
     * Disable animations when leaving the activity
     */
	public void onBackPressed() {
    	
		Racesow.backSound();
    	this.finish();
    	this.overridePendingTransition(0, 0);
    }
}

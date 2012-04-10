package org.racenet.racesow;

import org.racenet.framework.AndroidFileIO;
import org.racenet.racesow.models.DemoAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * Shows pages of lists of map-highscores
 * 
 * @author soh#zolex
 *
 */
public class DemoList extends ListActivity {

	WakeLock wakeLock;
	ViewPager viewPager;
	DemoAdapter adapter;
	
    @Override
    /**
     * Acquires wakelock and creates an instance of
     * the View-Pager and corresponding adapter
     */
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
    	
    	PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	this.wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "racesow");
    	
    	AndroidFileIO fileIO = new AndroidFileIO(getAssets());
    	this.adapter = new DemoAdapter(this, fileIO);
    	setListAdapter(this.adapter);
        setContentView(R.layout.listview);
        
        ListView list = getListView();
        registerForContextMenu(list);
        list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
				final String demo = (String)getListAdapter().getItem(arg2);
				new AlertDialog.Builder(DemoList.this)
		            .setMessage("Do you want to play the demo '" + demo +"'?")
		            .setPositiveButton("Play", new OnClickListener() {
						
						public void onClick(DialogInterface arg0, int arg1) {
							
							Intent i = new Intent(DemoList.this, Racesow.class);
							i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							i.putExtra("screen", "demo");
							i.putExtra("demo", demo);
							startActivityForResult(i, 0);
						}
					})
					.setNegativeButton("No", null)
		            .show();
			}
		});
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle((String)this.adapter.getItem(info.position));
        menu.add("Delete demo");
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	
      final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
      int menuItemIndex = item.getItemId();
     
      if (menuItemIndex == 0) {
    	  
    	  new AlertDialog.Builder(this)
	        .setMessage("Do you really want to delete this demo?")
	        .setPositiveButton("Yes", new OnClickListener() {
				
				public void onClick(DialogInterface arg0, int arg1) {
					
					Toast.makeText(DemoList.this, "Deleted demo " + (String)DemoList.this.adapter.getItem(info.position), Toast.LENGTH_SHORT).show();
				}
			})
			.setNegativeButton("No", null)
	        .show();
      }
      return true;
    }
    
    /**
	 * Acquire the wakelock on resume
	 */
    public void onResume() {
    	
    	super.onResume();
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
    	
    	this.finish();
    	this.overridePendingTransition(0, 0);
    }
}
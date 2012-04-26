package org.racenet.racesow;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.racenet.framework.FileIO;
import org.racenet.framework.XMLParser;
import org.racenet.racesow.models.DownloadMapsAdapter;
import org.racenet.racesow.models.MapItem;
import org.racenet.racesow.threads.DownloadThread;
import org.racenet.racesow.threads.UnzipThread;
import org.racenet.racesow.threads.HttpLoaderTask;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Activity to show a list of available maps
 * for download from a remote server. It guides
 * the user through the download process.
 * 
 * @author soh#zolex
 *
 */
public class DownloadMaps extends ListActivity implements HttpCallback {

	private static int MENU_ITEM_REFRESH = 0;
	DownloadMapsAdapter mAdapter;
	WakeLock wakeLock;
	ProgressDialog pd;
	String racesowPath = FileIO.externalStoragePath + "racesow" + File.separator;
	
    @Override
    /**
     * Set the listview, and initialize the maplist loading
     */
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.listview);
    	PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	this.wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "racesow");
        this.refreshMapList();
    }

    /**
     * Called by the XMLLoaderTask when finished
     * 
     * @param InputStream xmlStream
     */
    public void httpCallback(InputStream xmlStream) {
    	
    	pd.dismiss();
    	
    	if (xmlStream == null) {
    		
    		new AlertDialog.Builder(DownloadMaps.this)
	            .setMessage("Could not obtain the list of available maps.\nCheck your network connection and try again.")
	            .setNeutralButton("OK", new OnClickListener() {
					
					public void onClick(DialogInterface arg0, int arg1) {
						
						finish();
						overridePendingTransition(0, 0);
					}
				})
	            .show();
    		
    	} else {
    		
    		XMLParser parser = new XMLParser();
    		parser.read(xmlStream);
    		
    		List<MapItem> mapList = new ArrayList<MapItem>();
    		NodeList maps = parser.doc.getElementsByTagName("map");
    		int numMaps = maps.getLength();
    		for (int i = 0; i < numMaps; i++) {
    			
    			Element map = (Element)maps.item(i);
    			MapItem mapItem = new MapItem();
    			mapItem.id = Integer.parseInt(parser.getValue(map, "id"));
    			mapItem.name = parser.getValue(map, "name");
    			mapItem.skill = parser.getValue(map, "skill");
    			mapItem.download = parser.getValue(map, "download");
    			mapItem.author = parser.getValue(map, "author");
    			mapItem.filename = parser.getValue(map, "filename");
    			File test = new File(racesowPath + "maps" + File.separator + mapItem.filename);
    			mapItem.installed = test.isFile();
    			mapList.add(mapItem);
    		}
    		
    		mAdapter = new DownloadMapsAdapter(getApplicationContext(), mapList);
    		setListAdapter(mAdapter);
    	}
    	
    }
    
    /**
     * Loads the available maps from a remote
     * server to finally show a listview.
     */
    public void refreshMapList() {
    	
    	final String racesowPath = FileIO.externalStoragePath + "racesow" + File.separator;
    	
    	pd = new ProgressDialog(DownloadMaps.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("Obtaining available maps...");
		pd.setCancelable(true);
		pd.setOnCancelListener(new OnCancelListener() {
			
			public void onCancel(DialogInterface dialog) {

				DownloadMaps.this.onBackPressed();
			}
		});	
		pd.show();
        
		String url = "http://racesow2d.warsow-race.net/downloads.php";
		new HttpLoaderTask(this).execute(url);
		
		// when clicking a map
        getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, final View view, final int pos, long id) {
				
				final String mapName =  ((MapItem)mAdapter.getItem(pos)).name;
				boolean installed = ((MapItem)mAdapter.getItem(pos)).installed;
				
				// when the map is installed we do not need to download it again
				if (installed) {
					
					new AlertDialog.Builder(DownloadMaps.this)
			            .setMessage("The map '" + mapName +"' is already installed.")
			            .setNeutralButton("OK", null)
			            .show();
					
				// otherwise ask before downloading
				} else {
				
					new AlertDialog.Builder(DownloadMaps.this)
			            .setMessage("Download map '" + mapName + "'?")
			            .setPositiveButton("Yes", new OnClickListener() {
					
							public void onClick(DialogInterface arg0, int arg1) {
								
								final ProgressDialog pd2 = new ProgressDialog(DownloadMaps.this);
								pd2.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
								pd2.setMessage("Downloading map '" + mapName + "'");
								pd2.setCancelable(true);
								
								final String source = ((MapItem)mAdapter.getItem(pos)).download;
								final String zipPath = racesowPath + "downloads" + File.separator;
								
								final DownloadThread t = new DownloadThread(source, zipPath, new Handler() {
							    	
								    	@Override
								        public void handleMessage(Message msg) {
								    		
								    		Bundle b = msg.getData();
								    		int code = b.getInt("code");
								    		switch (code) {
								    		
								    			// update the progress-bar
								    			case 1:
								    				int percent = b.getInt("percent");
										    		pd2.setProgress(percent);
										    		if (percent >= 100) {
										    			
										    			pd2.dismiss();
										    			
										    			final ProgressDialog pd3 = new ProgressDialog(DownloadMaps.this);
														pd3.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
														pd3.setMessage("Extracting files...");
														pd3.setCancelable(false);
														pd3.show();
										    			
										    			int slashIndex = source.lastIndexOf('/');
										    			final String zipFile = zipPath + source.substring(slashIndex + 1);
										    			
										    			final UnzipThread t2 = new UnzipThread(zipFile, racesowPath,  new Handler() {
													    	
													    	@Override
													        public void handleMessage(Message msg) {
													    		
													    		Bundle b = msg.getData();
													    		int code = b.getInt("code");
													    		switch (code) {
													    		
													    			// update the progress-bar
													    			case 1:
													    				int percent = msg.getData().getInt("percent");
															    		pd3.setProgress(percent);
															    		if (percent >= 100) {
															    			
															    			new AlertDialog.Builder(DownloadMaps.this)
																	            .setMessage("You can now choose the map '"+ mapName +"' from your maplist.")
																	            .setNeutralButton("OK", null)
																	            .show();
															    			pd3.dismiss();
															    			
															    			TextView status = (TextView)view.findViewById(R.id.status);
															    			status.setText("installed");
															    			
															    			File file = new File(zipFile);
															    			file.delete();
															    		}
															    		break;
															    		
															    	// upzip eror
													    			case 2:
													    				pd3.dismiss();
													    				new AlertDialog.Builder(DownloadMaps.this)
																            .setMessage("Unzip error: " + b.getString("message"))
																            .setNeutralButton("OK", null)
																            .show();
													    				break;
													    		}
													    		
													    	}
										    			});
										    			
										    			t2.start();
										    		}
								    				break;
								    			
								    			// download error
								    			case 2:
								    				pd2.dismiss();
								    				new AlertDialog.Builder(DownloadMaps.this)
											            .setMessage("Download error: " + b.getString("message"))
											            .setNeutralButton("OK", null)
											            .show();
								    				break;
								    		}
								    	}
								});
								
								// stop the download if the user requests it
								pd2.setOnCancelListener(new OnCancelListener() {
									
									public void onCancel(DialogInterface arg0) {
										
										t.stopDownload();
										new AlertDialog.Builder(DownloadMaps.this)
								            .setMessage("Download aborted")
								            .setNeutralButton("OK", null)
								            .show();
									}
								});
								
								pd2.show();
								
								t.start();
							}
						})
						.setNegativeButton("No", null)
			            .show();
				}
			}
		});
    }
    
    /**
     * Acquire the wacklock when reusming the activity
     */
    public void onResume() {
    	
    	super.onResume();
    	this.wakeLock.acquire();
    }
    
    /**
     * Release the wakelock when quitting the activity
     */
    public void onDestroy() {
    	
    	super.onDestroy();
    	this.wakeLock.release();
    }

	@Override
	/**
	 * Show the option to refresh the list manually
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.downloadmaps, menu);
	    
	    MenuItem refresh = menu.getItem(MENU_ITEM_REFRESH);
	    refresh.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			public boolean onMenuItemClick(MenuItem arg0) {
				
				refreshMapList();
				return true;
			}
		});
	    
		return true;
	}
	
	/**
	 * No animations when leaving the activity
	 */
	public void onBackPressed() {
    	
    	this.finish();
    	this.overridePendingTransition(0, 0);
    }
}
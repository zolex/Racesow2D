package org.racenet.racesow;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.racenet.framework.AndroidFileIO;
import org.racenet.framework.XMLParser;
import org.racenet.racesow.models.DownloadMapsAdapter;
import org.racenet.racesow.models.MapItem;
import org.racenet.racesow.threads.DownloadThread;
import org.racenet.racesow.threads.RefreshMapsThread;
import org.racenet.racesow.threads.UnzipThread;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class DownloadMaps extends ListActivity {

	private static int MENU_ITEM_REFRESH = 0;
	DownloadMapsAdapter mAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.downloadmaps);
        
        this.refreshMapList();
    }

    public void refreshMapList() {
    	
    	final ProgressDialog pd = new ProgressDialog(DownloadMaps.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("Refreshing available maps");
		pd.setCancelable(false);
		pd.show();
        
		final RefreshMapsThread t = new RefreshMapsThread(new Handler() {
	    	
	    	@Override
	        public void handleMessage(Message msg) {
	    		
	    		switch (msg.what) {
	    			
	    			case 0:
	    				
	    				new AlertDialog.Builder(DownloadMaps.this)
				            .setMessage("Internal error: " + msg.getData().getString("exception"))
				            .setNeutralButton("OK", null)
				            .show();
	    				break;
	    				
	    			case 1:
	    				
					InputStream xmlStream;
					try {
						xmlStream = new ByteArrayInputStream(msg.getData().getString("xml").getBytes("UTF-8"));
					
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
			    			mapItem.author = parser.getValue(map, "author");
			    			mapItem.file = parser.getValue(map, "file");
			    			mapList.add(mapItem);
			    		}
			    		
			    		mAdapter = new DownloadMapsAdapter(getApplicationContext(), mapList);
			    		setListAdapter(mAdapter);
			    		
					} catch (UnsupportedEncodingException e) {

						new AlertDialog.Builder(DownloadMaps.this)
				            .setMessage("Internal error: " + e.getMessage())
				            .setNeutralButton("OK", null)
				            .show();
					}
	    			
					break;
	    		}
	    		
	        	pd.dismiss();
	        }
	    });
	    
		t.start();
		
        getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
				
				final String mapName =  ((MapItem)mAdapter.getItem(pos)).name;
				new AlertDialog.Builder(DownloadMaps.this)
		            .setMessage("Download map '" + mapName + "'?")
		            .setPositiveButton("Yes", new OnClickListener() {
				
						public void onClick(DialogInterface arg0, int arg1) {
							
							final ProgressDialog pd2 = new ProgressDialog(DownloadMaps.this);
							pd2.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
							pd2.setMessage("Downloading map '" + mapName + "'");
							pd2.setCancelable(false);
							pd2.show();
							
							final String source = ((MapItem)mAdapter.getItem(pos)).file;
							final String racesowPath = AndroidFileIO.externalStoragePath + "racesow" + File.separator;
							final String zipPath = racesowPath + "downloads" + File.separator;
							
							DownloadThread t = new DownloadThread(source, zipPath, new Handler() {
						    	
							    	@Override
							        public void handleMessage(Message msg) {
							    		
							    		Bundle b = msg.getData();
							    		int code = b.getInt("code");
							    		switch (code) {
							    		
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
									    			String zipFile = zipPath + source.substring(slashIndex + 1);
									    			
									    			final UnzipThread t2 = new UnzipThread(zipFile, racesowPath,  new Handler() {
												    	
												    	@Override
												        public void handleMessage(Message msg) {
												    		
												    		Bundle b = msg.getData();
												    		int code = b.getInt("code");
												    		switch (code) {
												    		
												    			case 1:
												    				int percent = msg.getData().getInt("percent");
														    		pd3.setProgress(percent);
														    		if (percent >= 100) {
														    			
														    			new AlertDialog.Builder(DownloadMaps.this)
																            .setMessage("You can now choose the map '"+ mapName +"' from your maplist.")
																            .setNeutralButton("OK", null)
																            .show();
														    			pd3.dismiss();
														    		}
														    		break;
														    		
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
							
							t.start();
						}
					})
					.setNegativeButton("No", null)
		            .show();
			}
		});
    }
    
    public void onStart() {
    	
    	super.onStart();
    }
    
    public void onStop() {
    	
    	super.onStop();
    }
    
    @Override
	public boolean onPrepareOptionsMenu (Menu menu) {

	    return true;
	}

	@Override
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
}
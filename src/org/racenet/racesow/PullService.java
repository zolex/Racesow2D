package org.racenet.racesow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.racenet.framework.XMLParser;
import org.racenet.helpers.MapList;
import org.racenet.racesow.models.BeatenByItem;
import org.racenet.racesow.models.Database;
import org.racenet.racesow.models.MapItem;
import org.racenet.racesow.models.MapUpdateItem;
import org.racenet.racesow.models.UpdateItem;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class PullService extends Service {
	
	private NotificationManager manager;
    private Database db;
    private final HttpClient client = new DefaultHttpClient();
    private final Timer timer = new Timer("PullService", true);
    private final XMLParser parser = new XMLParser();
    
    public static int SERVICE_NOTIFICATION = 1;
    
    @Override
    public void onCreate() {
    	
    	super.onCreate();
    	manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Database.setupInstance(getApplicationContext());
        this.db = Database.getInstance();
        
        this.timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
				try {
					HttpPost post = new HttpPost("http://racesow2d.warsow-race.net/updates.php");
					List<NameValuePair> postValues = new ArrayList<NameValuePair>();
					SharedPreferences prefs = PullService.this.getSharedPreferences("racesow", Context.MODE_PRIVATE);
					String playerName = prefs.getString("name", "player");
					postValues.add(new BasicNameValuePair("name", playerName));
					List<MapItem> mapList = MapList.load(getAssets());
					int length = mapList.size();
					for (int i = 0; i < length; i++) {
						
						MapItem map = mapList.get(i);
						int position = db.getMapPosition(playerName, map.filename);
						postValues.add(new BasicNameValuePair("positions["+ map.filename +"]", String.valueOf(position)));
					}

					UpdateItem update = new UpdateItem();
					
					update.oldPosition = db.getPosition(playerName);
					update.oldPoints = db.getPoints(playerName);
					update.newPosition = 0;
					update.newPoints = 0;
					
					post.setEntity(new UrlEncodedFormEntity(postValues));
					parser.read(client.execute(post).getEntity().getContent());
					NodeList updateN = parser.doc.getElementsByTagName("update");
					if (updateN.getLength() == 1) {
						
						Element updateRoot = (Element)updateN.item(0);
						update.name = parser.getValue(updateRoot, "name");
						update.newPosition = Integer.parseInt(parser.getValue(updateRoot, "position"));
						update.newPoints = Integer.parseInt(parser.getValue(updateRoot, "points"));
						if (update.newPoints != update.oldPoints) {
							
							update.changed = true;
						}
						
						NodeList mapsN = updateRoot.getElementsByTagName("maps");
						if (mapsN.getLength() == 1) {
							
							Element mapsRoot = (Element)mapsN.item(0);
							NodeList maps = mapsRoot.getElementsByTagName("map");
							int numMaps = maps.getLength();
							for (int i = 0; i < numMaps; i++) {
								
								MapUpdateItem mapUpdate = new MapUpdateItem();
								
								Element map = (Element)maps.item(i);
								mapUpdate.name = parser.getValue(map, "name");
								mapUpdate.newPosition = Integer.parseInt(parser.getValue(map, "position"));
								mapUpdate.oldPosition = db.getPosition(mapUpdate.name);

								if (mapUpdate.newPosition != mapUpdate.oldPosition) {
									
									mapUpdate.changed = true;
									update.changed = true;
									
									NodeList beatenByN = map.getElementsByTagName("beaten_by");
									if (beatenByN.getLength() == 1) {
										
										NodeList beatenBy = ((Element)beatenByN.item(0)).getElementsByTagName("player");
										int numBeatenBy = beatenBy.getLength();
										for (int j = 0; j < numBeatenBy; j++) {
											
											BeatenByItem beatenByItem = new BeatenByItem();
											Element player = (Element)beatenBy.item(j);
											beatenByItem.name = parser.getValue(player, "name");
											beatenByItem.time = Float.parseFloat(parser.getValue(player, "time"));
											beatenByItem.position = Integer.parseInt(parser.getValue(player, "position"));
											mapUpdate.beatenBy.add(beatenByItem);
										}
									}
									
									update.maps.add(mapUpdate);
								}
							}
						}
					}
					
					if (update.changed) {
						
						db.addUpdate(update);
					}
					
				} catch (ClientProtocolException e) {
				} catch (IOException e) {
				}
			}
			
		}, 1000, 300000);
  
        manager.notify(SERVICE_NOTIFICATION, getServiceNotification(getApplicationContext(), this));
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	
    	super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
    	
    	super.onDestroy();
    	manager.cancel(SERVICE_NOTIFICATION);
    	this.timer.purge();
    	this.timer.cancel();
    }

	@Override
	public IBinder onBind(Intent arg0) {
		
		return null;
	}
	
	public static Notification getServiceNotification(Context intentContext, Context pendingContext) {
    	
    	Notification notification = new Notification(R.drawable.ic_launcher, null, System.currentTimeMillis());
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        Intent notifyIntent = new Intent(intentContext, Racesow.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(pendingContext, 0, notifyIntent, 0);
        notification.setLatestEventInfo(pendingContext, "Racesow", "Notification Service", contentIntent);
        return notification;
    }
}
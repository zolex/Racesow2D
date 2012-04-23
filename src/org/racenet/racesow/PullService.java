package org.racenet.racesow;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.racenet.helpers.InputStreamToString;
import org.racenet.helpers.MapList;
import org.racenet.racesow.models.Database;
import org.racenet.racesow.models.MapItem;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;


public class PullService extends Service {
	
	private NotificationManager manager;
    private Database db;
    private HttpClient client = new DefaultHttpClient();
    Timer timer = new Timer("PullService", true);
    
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
					postValues.add(new BasicNameValuePair("name", prefs.getString("name", "player")));
					List<MapItem> mapList = MapList.load(getAssets());
					int length = mapList.size();
					for (int i = 0; i < length; i++) {
						
						MapItem map = mapList.get(i);
						int position = db.getPosition(map.filename);
						postValues.add(new BasicNameValuePair("positions["+ map.filename +"]", String.valueOf(position)));
					}
				
					post.setEntity(new UrlEncodedFormEntity(postValues));
					InputStream result = client.execute(post).getEntity().getContent();
					Log.d("DEBUG", "XML: " + InputStreamToString.convert(result));
					
					
				} catch (ClientProtocolException e) {
				} catch (IOException e) {
				}
			}
			
		}, 1000, 10000);
  
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
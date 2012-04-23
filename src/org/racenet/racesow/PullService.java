package org.racenet.racesow;

import org.racenet.racesow.models.Database;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;


class PullService extends Service {
	
	private NotificationManager manager;
    private Database db;
    
    public static int SERVICE_NOTIFICATION = 1;
    
    @Override
    public void onCreate() {
    	
    	super.onCreate();
    	manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Database.setupInstance(getApplicationContext());
        this.db = Database.getInstance();
        
  
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
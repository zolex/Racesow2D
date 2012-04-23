package org.racenet.helpers;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class IsServiceRunning {

	public static boolean check(String serviceClassName, Context context){
		
	    final ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
	    final List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
	    for (RunningServiceInfo runningServiceInfo : services) {
	    	if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
	            return true;
	        }
	    }
	    return false;
	 }
}

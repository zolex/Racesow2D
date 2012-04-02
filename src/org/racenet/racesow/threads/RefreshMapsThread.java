package org.racenet.racesow.threads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import org.racenet.helpers.InputStreamToString;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class RefreshMapsThread extends Thread {

	private Handler handler;
	
	public RefreshMapsThread(Handler h) {
		
		this.handler = h;
	}
	
	@Override
    public void run() {         

		HttpClient client = new DefaultHttpClient();				
	    HttpGet get = new HttpGet("http://www.warsow-race.net/tools/maplist.xml");
	    
	    Message msg = new Message();
	    Bundle b = new Bundle();
	    
	    try {
	    	
	        HttpResponse response = client.execute(get);
	        
	        b.putString("xml", InputStreamToString.convert(response.getEntity().getContent()));
	        msg.what = 1;   
	        
	    } catch (ClientProtocolException e) {
	    	
	    	b.putString("exception", "ClientProtocolException");
	    	msg.setData(b);
	    	msg.what = 0;

	    } catch (IOException e) {

	    	b.putString("exception", "IOException");
	    	msg.setData(b);
	    	msg.what = 0;
    		
	    }
	    
	    msg.setData(b);
        handler.sendMessage(msg);
    }
}

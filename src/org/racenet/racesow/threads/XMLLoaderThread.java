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

/**
 * Thread to load the online scores from the web
 * 
 * @author soh#zolex
 *
 */
public class XMLLoaderThread extends Thread {

	private Handler handler;
	private String url;
	
	/**
	 * Constructor
	 * 
	 * @param Handler h
	 */
	public XMLLoaderThread(String url, Handler h) {
		
		this.handler = h;
		this.url = url;
	}
	
	@Override
	/**
	 * Load the data remotely and pass the XML to the provided handler
	 */
    public void run() {         

		HttpClient client = new DefaultHttpClient();				
		HttpGet get = new HttpGet(this.url);
	    
	    Message msg = new Message();
	    Bundle b = new Bundle();
	    
	    try {
	    	
	        HttpResponse response = client.execute(get);
	        
	        // Bundle can not carry a stream, too bad
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

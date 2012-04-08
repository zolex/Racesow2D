package org.racenet.racesow.threads;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.racenet.racesow.XMLListActivity;

import android.os.AsyncTask;

/**
 * Thread to load the online scores from the web
 * 
 * @author soh#zolex
 *
 */
public class XMLLoaderTask extends AsyncTask<String, Void, InputStream> {

	XMLListActivity activity;
	
	public XMLLoaderTask(XMLListActivity activity) {
		
		this.activity = activity;
	}
	
	@Override
	protected InputStream doInBackground(String... urls) {
		
		HttpClient client = new DefaultHttpClient();				
		HttpGet get = new HttpGet(urls[0]);
	    
	    try {
	    	
	        HttpResponse response = client.execute(get);
	        return response.getEntity().getContent(); 
	        
	    } catch (ClientProtocolException e) {
	    	
	    	return null;

	    } catch (IOException e) {

	    	return null;
	    }
	}
	
	@Override
	protected void onPostExecute(InputStream result) {
		
		this.activity.xmlCallback(result);
	}
}

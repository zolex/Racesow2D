package org.racenet.racesow.threads;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import org.racenet.racesow.HttpCallback;

import android.os.AsyncTask;

/**
 * Thread to load the online scores from the web
 * 
 * @author soh#zolex
 *
 */
public class HttpLoaderTask extends AsyncTask<Object, Void, InputStream> {

	HttpCallback activity;
	
	public HttpLoaderTask(HttpCallback activity) {
		
		this.activity = activity;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected InputStream doInBackground(Object... input) {
		
		if (input.length == 1) {
			
			return this.getRequest((String)input[0]);
		
		} else if (input.length == 2) {
			
			return this.postRequest((String)input[0], (List<NameValuePair>)input[1]);
			
		} else {
			
			return null;
		}
	}
	
	/**
	 * Execute a get request and return the response
	 * 
	 * @param String url
	 * @param List<NameValuePair> values
	 * @return InputStream
	 */
	public InputStream postRequest(String url, List<NameValuePair> values) {
		
		HttpClient client = new DefaultHttpClient();				
		HttpPost post = new HttpPost(url);
		
	    try {
	    	
	    	post.setEntity(new UrlEncodedFormEntity(values));
	        HttpResponse response = client.execute(post);
	        return response.getEntity().getContent(); 
	        
	    } catch (ClientProtocolException e) {
	    	
	    	return null;

	    } catch (IOException e) {

	    	return null;
	    }
	}
	
	/**
	 * Execute a get request and return the response
	 * 
	 * @param String url
	 * @return InputStream
	 */
	public InputStream getRequest(String url) {
		
		HttpClient client = new DefaultHttpClient();				
		HttpGet get = new HttpGet(url);
	    
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
		
		this.activity.httpCallback(result);
	}
}

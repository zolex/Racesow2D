package org.racenet.racesow.threads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.racenet.framework.XMLParser;
import org.racenet.racesow.models.Database;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

/**
 * Thread to submit a race score to the web api
 * 
 * @author soh#zolex
 *
 */
public class SubmitScoreThread extends Thread {

	private float time;
	private String map;
	private String player;
	private short submitTries;
	private Database db;
	
	/**
	 * Constructor
	 * 
	 * @param Stirng map
	 * @param String player
	 * @param float time
	 */
	public SubmitScoreThread(String map, String player, float time) {
		
		this.time = time;
		this.map = map;
		this.player = player;
		this.submitTries = 0;
		this.db = Database.getInstance();
	}
	
	@Override
	/**
	 * Run the thread
	 */
    public void run() {         

		this.submitScores();
    }
	
	/**
	 * Submit the scores to the web api
	 */
	private void submitScores() {
		
		HttpClient client = new DefaultHttpClient();				
	    HttpPost post = new HttpPost("http://racesow2d.warsow-race.net/submit.php");
	    
	    try {
	    	
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	        nameValuePairs.add(new BasicNameValuePair("map", this.map));
	        nameValuePairs.add(new BasicNameValuePair("player", this.player));
	        nameValuePairs.add(new BasicNameValuePair("session", this.db.getSession(this.player)));
	        nameValuePairs.add(new BasicNameValuePair("time", String.valueOf(new Float(this.time))));
	        nameValuePairs.add(new BasicNameValuePair("key", "alpha-key"));
	        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = client.execute(post);
	        XMLParser parser = new XMLParser();
	        parser.read(response.getEntity().getContent());
	        
	        // in case of an XML error element sent by the API
			NodeList errorN = parser.doc.getElementsByTagName("error");
			if (errorN.getLength() > 0) {
				
				throw new Exception("Server error: " + parser.getNodeValue((Element)errorN.item(0)));
			}
			
			// parse the update
			NodeList submissions = parser.doc.getElementsByTagName("submission");
			if (submissions.getLength() == 1) {
				
				Element submissionRoot = (Element)submissions.item(0);
				String session = parser.getValue(submissionRoot, "session");
				if (!session.equals("")) {
					
					this.db.setSession(this.player, session);
				}
				
				/*
				int points;
				try {
					
					points = Integer.parseInt(parser.getValue(submissionRoot, "points"));
					
				} catch (NumberFormatException e) {
					
					points = 0;
				}
				*/
			}
	        
	    // if the submission did not work, retry a few times
	    } catch (ClientProtocolException e) {
	    	
	    	if (this.submitTries++ < 10) this.submitScores();
	    	
	    } catch (IOException e) {
    		
	    	if (this.submitTries++ < 10) this.submitScores();
	    	
	    } catch (Exception e) {
	    	
	    	Log.d("DEBUG", "Error: " + e.getMessage());
	    }
	}
}

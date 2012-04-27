package org.racenet.racesow.threads;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.racenet.framework.XMLParser;
import org.racenet.racesow.HttpCallback;
import org.racenet.racesow.models.Database;
import org.racenet.racesow.models.PlayerItem;
import org.racenet.racesow.models.RaceItem;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

/**
 * Thread to load the online scores from the web
 * 
 * @author soh#zolex
 *
 */
public class SubmitScoresTask extends AsyncTask<Void, Void, Void> implements HttpCallback {
	
	Iterator<Entry<String, PlayerItem>> list;
	BlockingQueue<PlayerItem> players = new LinkedBlockingQueue<PlayerItem>();
	public PlayerItem currentPlayer;
	Handler handler;
	boolean end = false;
	
	/**
	 * Constructor
	 * 
	 * @param Handler handler
	 */
	public SubmitScoresTask(Handler handler) {
		
		this.handler = handler;
	}
	
	@Override
	/**
	 * Run the submission queue
	 * 
	 * @param Void ... unused
	 * @return Void
	 */
	protected Void doInBackground(Void... unused) {
		
		HashMap<String, PlayerItem> players = Database.getInstance().getNewRaces();
		this.list = players.entrySet().iterator();
		this.prepareNext();
		while (!this.end) {
			
			try {
				
				this.currentPlayer = this.players.take();
				if (this.currentPlayer.cancel) {
					
					break;
				}
				
				this.submitCurrent();
				
			} catch (InterruptedException e) {}
		}
		
		return null;
	}

	/**
	 * Prepare the next score submission in the queue.
	 * May be called after enter-password dialog.
	 */
	public void prepareNext() {
		
		if (this.list.hasNext()) {
		
			this.players.add(this.list.next().getValue());
			
		} else {
			
			this.end = true;
			PlayerItem stop = new PlayerItem();
			stop.cancel = true;
			this.players.add(stop);
		}
	}
	
	/**
	 * Set the session for the current player
	 * and submit his scores. Is called after the
	 * enter-password dialog.
	 * 
	 * @param String session
	 */
	public void submitCurrent(String session) {
		
		this.currentPlayer.session = session;
		this.submitCurrent();
	}
	
	/**
	 * Submit the current player's scores.
	 */
	private void submitCurrent() {
		
		String url = "http://racesow2d.warsow-race.net/batchsubmit.php";
		List<NameValuePair> values = new ArrayList<NameValuePair>();
		values.add(new BasicNameValuePair("key", "alpha-key"));
		values.add(new BasicNameValuePair("player", this.currentPlayer.name));
		values.add(new BasicNameValuePair("session", this.currentPlayer.session));
		int length = this.currentPlayer.races.size();
		for (int i = 0; i < length; i++) {
			
			RaceItem race = this.currentPlayer.races.get(i);
			values.add(new BasicNameValuePair("time["+ race.map +"]["+ race.id +"]", String.valueOf(race.time)));
		}
		
		final HttpLoaderTask task = new HttpLoaderTask(this);
		task.execute(url, values);
	}

	/**
	 * Called by HttpLoaderTask
	 */
	public void httpCallback(InputStream xmlStream) {
		
		if (xmlStream == null) {
			
			return;
		}
		
		XMLParser parser = new XMLParser();
        parser.read(xmlStream);
		
		// in case of an XML error element sent by the API
		NodeList errors = parser.doc.getElementsByTagName("error");
		if (errors.getLength() > 0) {
			
			int code = -1;
			NodeList codes = parser.doc.getElementsByTagName("code");
			if (codes.getLength() > 0) {
				
				try {
					
					code = Integer.parseInt(parser.getNodeValue((Element)codes.item(0)));
					
				} catch (NumberFormatException e) {
					
					code = -1;
				}
			}
			
			if (code == 2) { // SESSION_INVALID
				
				// will trigger the enter password dialog
				this.handler.sendEmptyMessage(0);
			}
			
			return;
		}
		
		// parse the update
		NodeList submissions = parser.doc.getElementsByTagName("submission");
		if (submissions.getLength() == 1) {
			
			Database db = Database.getInstance();
			
			Element submissionRoot = (Element)submissions.item(0);
			String session = parser.getValue(submissionRoot, "session");
			if (!session.equals("")) {
				
				db.setSession(this.currentPlayer.name, session);
			}
			
			int length = this.currentPlayer.races.size();
			for (int i = 0; i < length; i++) {
				
				long id = this.currentPlayer.races.get(i).id;
				db.flagRaceSubmitted(id);
			}
			
			int points;
			try {
				
				points = Integer.parseInt(parser.getValue(submissionRoot, "points"));
				
			} catch (NumberFormatException e) {
				
				points = 0;
			}
			
			if (points > 0) {
				
				// triggers the "earned points" dialog
				this.currentPlayer.points = points;
				this.handler.sendEmptyMessage(1);
				
			} else {
				
				this.prepareNext();
			}
			
			return;
		}
	}
}

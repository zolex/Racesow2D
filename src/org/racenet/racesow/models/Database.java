package org.racenet.racesow.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class to store information in an SQLite database
 *
 * @author soh#zolex
 * 
 */
public final class Database extends SQLiteOpenHelper {

	/**
	 * The current version of the database.
	 * Should be increased which each change to the
	 * database structure
	 */
	private static final int DATABASE_VERSION = 1;
	
	/**
	 * Filename for the SQLite database
	 */
    private static final String DATABASE_NAME = "org.racenet.racesow.db";

    /**
     * Instance for singleton access
     */
    private static Database __instance;
    
    /**
     * Setup the singleton instance
     * 
     * @param Context context
     */
    public static void setupInstance(Context context) {
    	
    	if (__instance == null) {
    		
    		__instance = new Database(context);
    	}
    }
    
    /**
     * Singleton getter
     * 
     * @return Database
     */
    public static Database getInstance() {
    	
    	return __instance;
    }
    
    /**
     * Private constructor to only allot singleton usage
     * 
     * @param Context context
     */
    private Database(Context context) {
    	
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    /**
     * Called when the database is initially created
     * 
     * @param SQLiteDatabase db
     */
    public void onCreate(SQLiteDatabase db) {
    	
        db.execSQL("CREATE TABLE races(id INTEGER, map TEXT, player TEXT, time REAL, created_at TEXT, PRIMARY KEY(id))");
        db.execSQL("CREATE TABLE players(name TEXT, position INTEGER, points INTEGER, updated TEXT, PRIMARY KEY(name))");
        db.execSQL("CREATE TABLE updates(id INTEGER, name TEXT, old_points INTEGER, new_points INTEGER, old_position INTEGER, new_position INTEGER, created_at TEXT, done INTEGER, PRIMARY KEY(id))");
		db.execSQL("CREATE TABLE update_maps(id INTEGER, update_id INTEGER, name TEXT, old_position INTEGER, new_position INTEGER, PRIMARY KEY(id))");
		db.execSQL("CREATE TABLE update_beaten_by(update_maps_id INTEGER, name TEXT, time REAL, position INTEGER)");
    }

	@Override
	/**
	 * Called when the database version has changed.
	 * 
	 * @param SQLiteDatabase db
	 * @param int oldVersion
	 * @param int oldVersion
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	/**
	 * Set a value in the settings table.
	 * Be sure to create the value in onCreate or onUpgrade
	 * as this method only updates values.
	 * 
	 * @param String key
	 * @param String value
	 */
	public void set(String key, String value) {
		
		ContentValues values = new ContentValues();
    	values.put("value", value);
    	
    	SQLiteDatabase database = getWritableDatabase();
    	database.update("settings", values, "key = '"+ key + "'", null);
    	database.close();
	}
	
	/**
	 * Get a value from the settings table
	 * 
	 * @param String key
	 * @return String
	 */
	public String get(String key) {
		
		SQLiteDatabase database = getReadableDatabase();
	    Cursor c = database.query("settings", new String[]{"value"},
	        "key = '"+ key + "'", null, null, null, null);
	    c.moveToFirst();
	    String value = c.getString(0);
	    c.close();
	    database.close();
	    return value;
	}
	
	/**
	 * Save a new update to the database
	 * 
	 * @param UpdateItem update
	 */
	public void addUpdate(UpdateItem update) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    	Date date = new Date();
		
		SQLiteDatabase database = getWritableDatabase();
		try {
			
			database.beginTransaction();
			
			// update the player's online points, position and last updated time
			Cursor c = database.query("players", new String[]{"name"},
				"name = '"+ update.name + "'", null, null, null, null);

		    ContentValues playerValues = new ContentValues();
		    playerValues.put("points", update.newPoints);
		    playerValues.put("position", update.newPosition);
		    playerValues.put("updated", update.updated);
		    
		    if (c.getCount() == 0) {
		    	
		    	playerValues.put("name", update.name);
		    	if (-1 == database.insert("players", "", playerValues)) {
		    		
		    		throw new Exception("failed inserting player position and points");
		    	}
		    	
		    } else {
		    	
		    	if (1 != database.update("players", playerValues, "name = '"+ update.name + "'", null)) {
		    		
		    		throw new Exception("invalid update player position and points");
		    	}
		    }
		    c.close();
			
		    // add the update itsself
		    ContentValues values = new ContentValues();
		    values.put("name", update.name);
		    values.put("old_points", update.oldPoints);
		    values.put("new_points", update.newPoints);
		    values.put("old_position", update.oldPosition);
		    values.put("new_position", update.newPosition);
		    values.put("done", 0);
	    	values.put("created_at", dateFormat.format(date));
	    	
	    	long updateID = database.insert("updates", "", values);
	    	if (updateID == -1) {
	    		
	    		throw new Exception("failed inserting 'updates'");
	    	}
	    	
	    	// add maps from the update
	    	int numMaps = update.maps.size();
	    	for (int i = 0; i < numMaps; i++) {
	    		
	    		MapUpdateItem mapUpdate = update.maps.get(i);
	    		ContentValues mapValues = new ContentValues();
	    		mapValues.put("update_id", updateID);
	    		mapValues.put("name", mapUpdate.name);
	    		mapValues.put("old_position", mapUpdate.oldPosition);
	    		mapValues.put("new_position", mapUpdate.newPosition);
	    		
	    		long mapUpdateID = database.insert("update_maps", "", mapValues);
	    		if (mapUpdateID == -1) {
	    			
	    			throw new Exception("failed inserting 'update_maps'");
	    		}
	    		
	    		// add the beaten_bys from the update
	    		int numBeatenBy = mapUpdate.beatenBy.size();
	    		for (int j = 0; j < numBeatenBy; j++) {
	    			
	    			BeatenByItem beatenBy = mapUpdate.beatenBy.get(j);
	    			ContentValues beatenValues = new ContentValues();
	    			beatenValues.put("update_maps_id", mapUpdateID);
	    			beatenValues.put("name", beatenBy.name);
	    			beatenValues.put("time", beatenBy.time);
	    			
	    			long beatenByID = database.insert("update_beaten_by", "", beatenValues);
		    		if (beatenByID == -1) {
		    			
		    			throw new Exception("failed inserting 'update_beaten_by'");
		    		}
	    		}
	    	}
	    	
	    	database.setTransactionSuccessful();
	    	
		} catch (Exception e) {
			
			Log.e("DEBUG", "database update exception: " + e.getMessage());
			
		} finally {
			
			database.endTransaction();
		}
		
		database.close();
	}
	
	/**
	 * Delete all updates
	 */
	public void deleteAllUpdates() {
		
		SQLiteDatabase database = getWritableDatabase();
		database.delete("update_beaten_by", null, null);
		database.delete("update_maps", null, null);
    	database.delete("updates", null, null);
		database.close();
	}
	
	/**
	 * Delete a complete update
	 * 
	 * @param int id
	 */
	public void deleteUpdate(int id) {
		
		SQLiteDatabase database = getWritableDatabase();
		Cursor c = database.query("update_maps", new String[]{"id"},
	    	"update_id = '"+ id +"'", null, null, null, null);
		
    	if (c.getCount() > 0) {
	    	
	    	c.moveToFirst();
		    while (!c.isAfterLast()) {
		    	
		    	database.delete("update_beaten_by", "update_maps_id = " + c.getInt(0), null);
		    	c.moveToNext();
		    }
    	}
    	
    	c.close();
    	database.delete("update_maps", "update_id = " + id, null);
    	database.delete("updates", "id = " + id, null);
    	database.close();
	}
	
	/**
	 * Get a list of all updates
	 * 
	 * @return List<UpdateItem>
	 */
	public List<UpdateItem> getAllUpdates() {
		
		List<UpdateItem> updates = new ArrayList<UpdateItem>();
		SQLiteDatabase database = getReadableDatabase();
	    Cursor c = database.query("updates", new String[]{"id", "name", "old_points", "new_points", "old_position", "new_position", "created_at"},
	        null, null, null, null, "created_at DESC");
	    
	    if (c.getCount() > 0) {
	    	
	    	c.moveToFirst();
		    while (!c.isAfterLast()) {
	    	
		    	UpdateItem update = new UpdateItem();
		    	update.id = c.getInt(0);
		    	update.name = c.getString(1);
		    	update.oldPoints = c.getInt(2);
		    	update.newPoints = c.getInt(3);
		    	update.oldPosition = c.getInt(4);
		    	update.newPosition = c.getInt(5);
		    	update.createdAt = c.getString(6);
		    	
		    	Cursor c2 = database.query("update_maps", new String[]{"id", "name", "old_position", "new_position"},
		    		"update_id = '"+ update.id +"'", null, null, null, null);
		    	if (c2.getCount() > 0) {
			    	
			    	c2.moveToFirst();
				    while (!c2.isAfterLast()) {
				 
				    	MapUpdateItem map = new MapUpdateItem();
				    	map.id = c2.getInt(0);
				    	map.name = c2.getString(1);
				    	map.oldPosition = c2.getInt(2);
				    	map.newPosition = c2.getInt(3);
				    	
				    	Cursor c3 = database.query("update_beaten_by", new String[]{"name", "time", "position"},
				    		"update_maps_id = '"+ map.id +"'", null, null, null, null);
				    	if (c3.getCount() > 0) {
					    	
					    	c3.moveToFirst();
						    while (!c3.isAfterLast()) {
						    	
						    	BeatenByItem player = new BeatenByItem();
						    	player.name = c3.getString(0);
						    	player.time = c3.getFloat(1);
						    	
						    	map.beatenBy.add(player);
						    	c3.moveToNext();
						    }
				    	}
				    	
				    	c3.close();				    	
				    	update.maps.add(map);
				    	c2.moveToNext();
				    }
		    	}
		    	
		    	c2.close();
		    	updates.add(update);		    	
		    	c.moveToNext();
		    }
	    }
	    
	    c.close();
	    database.close();
	    
	    return updates;
	}
	
	/**
	 * Count the number of available updates
	 * 
	 * @return int
	 */
	public int countUpdates() {
		
		SQLiteDatabase database = getReadableDatabase();
	    Cursor c = database.query("updates", new String[]{"COUNT(id)"},
	        null, null, null, null, null);
	    
	    int count;
	    if (c.getCount() == 1) {
	    	
	    	c.moveToFirst();
	    	count = c.getInt(0);
	    	
	    } else {
	    	
	    	count = 0;
	    }
	    
	    c.close();
	    database.close();
	    return count;
	}
	
	/**
	 * Get the last update time for the given player
	 * 
	 * @param String player
	 * @return String
	 */
	public String getLastUpdated(String player) {
		
		SQLiteDatabase database = getReadableDatabase();
	    Cursor c = database.query("players", new String[]{"updated"},
	        "name = '"+ player +"'", null, null, null, null);
	    
	    String updated;
	    if (c.getCount() == 1) {
	    	
	    	c.moveToFirst();
	    	updated = c.getString(0);
	    	
	    } else {
	    	
	    	updated = "";
	    }
	    
	    c.close();
	    database.close();
	    return updated;
	}
	
	/**
	 * Get the remembered online position
	 * 
	 * @param String player
	 * @return int
	 */
	public int getPosition(String player) {
		
		SQLiteDatabase database = getReadableDatabase();
		Cursor c = database.query("players", new String[]{"position"},
				"name = '"+ player +"'", null, null, null, null);
		
		int position;
		if (c.getCount() == 1) {
			
			c.moveToFirst();
			position = c.getInt(0);
			
		} else {
			
			position = 0;
		}
		
		c.close();
		database.close();
		return position;
	}
	
	/**
	 * Get the remembered online points
	 * 
	 * @param String player
	 * @return int
	 */
	public int getPoints(String player) {
		
		SQLiteDatabase database = getReadableDatabase();
	    Cursor c = database.query("players", new String[]{"points"},
	        "name = '"+ player +"'", null, null, null, null);
	    
	    int points;
	    if (c.getCount() == 1) {
	    	
	    	c.moveToFirst();
	    	points = c.getInt(0);
	    	
	    } else {
	    	
	    	points = 0;
	    }
	    
	    c.close();
	    database.close();
	    return points;
	}
	
	/**
	 * Add a race to the local race table
	 * 
	 * @param String map
	 * @param String player
	 * @param float time
	 */
	public void addRace(String map, String player, float time) {
		
		ContentValues values = new ContentValues();
    	values.put("map", map);
    	values.put("time", time);
    	values.put("player", player);
    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    	Date date = new Date();
    	values.put("created_at", dateFormat.format(date));
    	
    	SQLiteDatabase database = getWritableDatabase();
    	database.insert("races", "", values);
    	database.close();
	}
	
	/**
	 * Get the best time for the given map
	 * 
	 * @param String map
	 * @return float
	 */
	public float getBestTime(String map) {
		
		SQLiteDatabase database = getReadableDatabase();
	    Cursor c = database.query("races", new String[]{"time"},
	        "map = '"+ map + "'", null, null, null, "time ASC");
	    float value;
	    if (c.getCount() == 0) {
	    	
	    	value = 0;
	    	
	    } else {
	    	
	    	c.moveToFirst();
		    value = c.getFloat(0);
	    }
	    
	    c.close();
	    database.close();
	    return value;
	}
	
	/**
	 * Get the scores for the given map
	 * 
	 * @param String map
	 * @return List<ScoreItem>
	 */
	public List<ScoreItem> getScores(String map) {
		
		SQLiteDatabase database = getReadableDatabase();
	    Cursor c = database.query("races", new String[]{"id, player, time, created_at"},
	        "map = '"+ map + "'", null, null, null, "time ASC");
	    List<ScoreItem> scores = new ArrayList<ScoreItem>();
	    int position = 1;
	    if (c.getCount() > 0) {
	    	
	    	c.moveToFirst();
		    while(!c.isAfterLast()) {
		    	
		    	ScoreItem item = new ScoreItem();
		    	item.id = c.getInt(0);
		    	item.position = position++;
		    	item.player = c.getString(1);
		    	item.time = c.getFloat(2);
		    	item.created_at = c.getString(3);
		    	scores.add(item);
		    	c.moveToNext();
		    }
	    }
	    
	    c.close();
	    database.close();
	    return scores;
	}
}


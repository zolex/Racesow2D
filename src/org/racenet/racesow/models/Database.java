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
	private static final int DATABASE_VERSION = 6;
	
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
    	
        db.execSQL("CREATE TABLE settings(key TEXT, value TEXT, PRIMARY KEY(key))");
        
        for (String key: new String[]{"points", "position"}) {
        
	        ContentValues values = new ContentValues();
	        values.put("key", key);
	        if (key.equals("points")) {
	        	
	        	values.put("value", "0");
	        	
	        } else if(key.equals("position")) {
	        	
	        	values.put("value", "0");
	        }
	        
	        db.insert("settings", null, values);
        }
        
        db.execSQL("CREATE TABLE races(id INTEGER, map TEXT, player TEXT, time REAL, created_at TEXT, PRIMARY KEY(id))");
        db.execSQL("CREATE TABLE maps(name TEXT, position INTEGER, PRIMARY KEY(name))");
        db.execSQL("CREATE TABLE updates(id INTEGER, old_points INTEGER, new_points INTEGER, old_position INTEGER, new_position INTEGER, created_at TEXT, done INTEGER, PRIMARY KEY(id))");
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
		
		// add player column to races table
		if (oldVersion < 4 && newVersion >= 4) {
			
			db.execSQL("ALTER TABLE races ADD player TEXT");
		}
		
		// create maps table
		if (oldVersion < 5 && newVersion >= 5) {
			
			db.execSQL("CREATE TABLE maps(name TEXT, position INTEGER, PRIMARY KEY(name))");
		}
		
		// add points and position to settings
		if (oldVersion < 6 && newVersion >= 6) {
			
			for (String key: new String[]{"points", "position"}) {
		        
		        ContentValues values = new ContentValues();
		        values.put("key", key);
		        values.put("value", "0");
		        db.insert("settings", null, values);
	        }
			
			db.execSQL("CREATE TABLE updates(id INTEGER, old_points INTEGER, new_points INTEGER, old_position INTEGER, new_position INTEGER, created_at TEXT, done INTEGER, PRIMARY KEY(id))");
			db.execSQL("CREATE TABLE update_maps(id INTEGER, update_id INTEGER, name TEXT, old_position INTEGER, new_position INTEGER, PRIMARY KEY(id))");
			db.execSQL("CREATE TABLE update_beaten_by(update_maps_id INTEGER, name TEXT, time REAL, position INTEGER)");
		}
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
			
			ContentValues posValues = new ContentValues();
			posValues.put("value", String.valueOf(update.newPosition));
	    	database.update("settings", posValues, "key = 'position'", null);
			
	    	ContentValues poiValues = new ContentValues();
	    	poiValues.put("value", String.valueOf(update.newPoints));
	    	database.update("settings", poiValues, "key = 'points'", null);
			
		    ContentValues values = new ContentValues();
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
	    	
	    	int numMaps = update.maps.size();
	    	for (int i = 0; i < numMaps; i++) {
	    		
	    		MapUpdateItem mapUpdate = update.maps.get(i);
	    		ContentValues mapValues = new ContentValues();
	    		mapValues.put("update_id", updateID);
	    		mapValues.put("name", mapUpdate.name);
	    		mapValues.put("old_position", mapUpdate.oldPosition);
	    		mapValues.put("new_position", mapUpdate.newPosition);
	    		
	    		Cursor c = database.query("maps", new String[]{"position"},
    		        "name = '"+ mapUpdate.name + "'", null, null, null, null);
    		    
    		    ContentValues mapValues2 = new ContentValues();
    		    mapValues2.put("position", mapUpdate.newPosition);
    		    
    		    if (c.getCount() == 0) {
    		    	
    		    	values.put("name", mapUpdate.name);
    		    	database.insert("maps", "", mapValues2);
    		    	
    		    } else {
    		    	
    		    	database.update("maps", mapValues2, "map = '"+ mapUpdate.name + "'", null);
    		    }
    		    c.close();
	    		
	    		long mapUpdateID = database.insert("update_maps", "", mapValues);
	    		if (mapUpdateID == -1) {
	    			
	    			throw new Exception("failed inserting 'update_maps'");
	    		}
	    		
	    		int numBeatenBy = mapUpdate.beatenBy.size();
	    		for (int j = 0; j < numBeatenBy; j++) {
	    			
	    			BeatenByItem beatenBy = mapUpdate.beatenBy.get(j);
	    			ContentValues beatenValues = new ContentValues();
	    			beatenValues.put("update_maps_id", mapUpdateID);
	    			beatenValues.put("name", beatenBy.name);
	    			beatenValues.put("time", beatenBy.time);
	    			beatenValues.put("position", beatenBy.position);
	    			
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
	 * Get a list of all updates
	 * 
	 * @return List<UpdateItem>
	 */
	public List<UpdateItem> getAllUpdates() {
		
		List<UpdateItem> updates = new ArrayList<UpdateItem>();
		SQLiteDatabase database = getReadableDatabase();
	    Cursor c = database.query("updates", new String[]{"id", "old_points", "new_points", "old_position", "new_position", "created_at"},
	        null, null, null, null, null);
	    
	    if (c.getCount() > 0) {
	    	
	    	c.moveToFirst();
		    while (!c.isAfterLast()) {
	    	
		    	UpdateItem update = new UpdateItem();
		    	update.id = c.getInt(0);
		    	update.oldPoints = c.getInt(1);
		    	update.newPoints = c.getInt(2);
		    	update.oldPosition = c.getInt(3);
		    	update.newPosition = c.getInt(4);
		    	update.createdAt = c.getString(5);
		    	
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
						    	player.time = c3.getInt(1);
						    	player.position = c3.getInt(2);
						    	
						    	map.beatenBy.add(player);
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
	    
	    if (c.getCount() == 1) {
	    	
	    	c.moveToFirst();
	    	return c.getInt(0);
	    	
	    } else {
	    	
	    	return 0;
	    }
	}
	
	/**
	 * Get the remembered online position for a map
	 * 
	 * @param String map
	 * @return int
	 */
	public int getPosition(String map) {
		
		SQLiteDatabase database = getReadableDatabase();
	    Cursor c = database.query("maps", new String[]{"position"},
	        "name = '"+ map + "'", null, null, null, null);
	    
	    if (c.getCount() == 1) {
	    	
	    	c.moveToFirst();
	    	return c.getInt(0);
	    	
	    } else {
	    	
	    	return 0;
	    }
	}
	
	/**
	 * Insert or update the online player position on a map
	 * 
	 * @param String map
	 * @param int position
	 */
	public void updatePosition(String map, int position) {
		
		SQLiteDatabase database = getReadableDatabase();
	    Cursor c = database.query("maps", new String[]{"position"},
	        "name = '"+ map + "'", null, null, null, null);
	    
	    SQLiteDatabase database2 = getWritableDatabase();
	    ContentValues values = new ContentValues();
	    values.put("position", position);
	    
	    if (c.getCount() == 0) {
	    	
	    	
	    	values.put("name", map);
	    	database2.insert("maps", "", values);
	    	
	    } else {
	    	
	    	database2.update("maps", values, "map = '"+ map + "'", null);
	    }
	    
	    c.close();
	    database.close();
	    database2.close();
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


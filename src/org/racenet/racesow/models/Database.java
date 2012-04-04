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
	private static final int DATABASE_VERSION = 4;
	
	/**
	 * Filename for the SQLite database
	 */
    private static final String DATABASE_NAME = "org.racenet.racesow.db";

    /**
     * Instance for singleton access
     */
    private static Database __instance;
    
    /**
     * Singleton getter
     * 
     * @param Context context
     * @return Database
     */
    public static Database getInstance(Context context) {
    	
    	if (__instance == null) {
    		
    		__instance = new Database(context);
    	}
    	
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
        
        for (String key: new String[]{"name", "sound", "celshading"}) {
        
	        ContentValues values = new ContentValues();
	        values.put("key", key);
	        if (key.equals("sound")) {
	        	values.put("value", "true");
	        } else if(key.equals("celshading")) {
	        	values.put("value", "false");
	        } else if(key.equals("name")) {
	        	values.put("value", "");
	        }
	        
	        db.insert("settings", null, values);
        }
        
        db.execSQL("CREATE TABLE races(id INTEGER, map TEXT, player TEXT, time REAL, created_at TEXT, PRIMARY KEY(id))");
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
		
		if (oldVersion < 4 && newVersion >= 4) {
			
			db.execSQL("ALTER TABLE races ADD player TEXT");
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


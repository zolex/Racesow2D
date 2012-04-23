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
	private static final int DATABASE_VERSION = 5;
	
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
        db.execSQL("CREATE TABLE maps(name TEXT, position INTEGER, PRIMARY KEY(name))");
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
		
		if (oldVersion < 5 && newVersion >= 5) {
			
			db.execSQL("CREATE TABLE maps(name TEXT, position INTEGER, PRIMARY KEY(name))");
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


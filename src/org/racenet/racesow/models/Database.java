package org.racenet.racesow.models;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public final class Database extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "org.racenet.racesow.db";

    private static Database __instance;
    
    public static Database getInstance(Context context) {
    	
    	if (__instance == null) {
    		
    		__instance = new Database(context);
    	}
    	
    	return __instance;
    }
    
    private Database(Context context) {
    	
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
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
    }

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}
	
	public void set(String key, String value) {
		
		ContentValues values = new ContentValues();
    	values.put("value", value);
    	
    	SQLiteDatabase database = getWritableDatabase();
    	database.update("settings", values, "key = '"+ key + "'", null);
    	database.close();
	}
	
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
}


package com.wanghaus.beerlog.service;

import java.text.DateFormat;
import java.util.Date;

import com.wanghaus.beerlog.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Spinner;
import android.widget.TextView;

public class BeerDbService {
	private static final String DB_NAME = "BeerLog";
	private static final String DB_TABLE = "beer_log";
	private static final int DB_VERSION = 2;

	private static final String DB_CREATE = 
		"CREATE TABLE IF NOT EXISTS " + DB_TABLE + "(" +
			"beername VARCHAR(255) NOT NULL, " +
			"container VARCHAR(32) NOT NULL, " +
			"stamp DATETIME NOT NULL" +
			"rating INT NOT NULL DEFAULT 0" +
		")";

    private final Context context; 
	private SQLiteDatabase db;
    private DatabaseHelper DBHelper;

    public BeerDbService(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
        db = DBHelper.getWritableDatabase();
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	if (oldVersion < 2) {
        		db.execSQL("ALTER TABLE " + DB_TABLE + " " +
        				"ADD COLUMN rating INT NOT NULL DEFAULT 0");
        	}
        	
        	// if (oldVersion < 3) etc..
        }
    }    
    
    public void close() {
        DBHelper.close();
    }
    
    /*
     * Write methods
     */
    public long addBeer(String beername, String container, Date stamp) {
		ContentValues newRow = new ContentValues();
		
        newRow.put("beername", beername);
        newRow.put("container", container);
        
        // Get the sqlite format for the stamp
        Cursor stampCursor = db.rawQuery( "SELECT DATETIME(?, 'unixepoch', 'localtime')",
        		new String[] { String.valueOf(stamp.getTime()/1000) } );
        stampCursor.moveToFirst();
        newRow.put("stamp", stampCursor.getString(0));
		
		return db.insert(DB_TABLE, null, newRow);
    }
    
    public void deleteBeer(long id) {
		db.execSQL("DELETE FROM " + DB_TABLE + " WHERE ROWID = " + String.valueOf(id));
    }
    
    /*
     * Read methods 
     */
    public Cursor getBeerHistory() {
    	return getBeerHistory(null);
    }
    public Cursor getBeerHistory(Integer limit) {
    	String strLimit = null;
    	if (limit != null)
    		strLimit = limit.toString();
    	
        return db.query(DB_TABLE,
        		new String[] {"ROWID AS _id", "beername", "container || ' at ' || stamp AS details", "rating", "container"},
        		null, null, null, null, "stamp DESC", strLimit);
    }
    
    public Cursor getBeerNames() {
        return db.query(DB_TABLE,
        		new String[] {"MAX(ROWID) AS _id", "beername"},
        		null, null,
        		"beername",
        		null, null);
    }

    public Cursor getBeerNames(String substr) {
    	if (substr == null)
    		return getBeerNames();
    	
        return db.query(DB_TABLE,
        		new String[] {"MAX(ROWID) AS _id", "beername"},
        		"beername LIKE ?",
        		new String[] { "%" + substr + "%" },
        		"beername",
        		null, null);
    }
    
    public long getBeerCount() {
    	Cursor beercountQuery = db.query(DB_TABLE, new String[] {"ROWID"}, null, null, null, null, null);
    	return beercountQuery.getCount();
    }
    
    public String getFavoriteBeer() {
    	Cursor q = db.query(DB_TABLE,
    			new String[] {"beername", "COUNT(*) AS count"},
    			null, null, "beername",
    			null, "COUNT(*) DESC", "1");
    	
    	if (q.getCount() == 0)
    		return "None yet";
    	
    	q.moveToFirst();
    	String favoriteBeer = q.getString(0);
    	q.close();
    	
    	return favoriteBeer;
    }
    
    public String getFavoriteDrinkingHour() {
    	Cursor q = db.query(DB_TABLE,
    			new String[] {"STRFTIME('%H', stamp) AS hour", "COUNT(*) AS count"},
    			null, null, "STRFTIME('%H', stamp)",
    			null, "COUNT(*) DESC", "1");
    	
    	if (q.getCount() == 0)
    		return "None yet";
    	
    	q.moveToFirst();
    	Integer hr = q.getInt(0);
    	q.close();
    	
    	String favoriteHour;
    	if (hr == 0)    	favoriteHour = "Midnight";
    	else if (hr == 12) 	favoriteHour = "Noon";
    	else if (hr > 12)  favoriteHour = String.valueOf(hr - 12) + " PM";
    	else    			favoriteHour = String.valueOf(hr) + " AM";
    	
    	return favoriteHour;
    }

	public void setBeerRating(long id, int rating) {
		ContentValues newRow = new ContentValues();
        newRow.put("rating", rating);

        db.update(DB_TABLE, newRow, "ROWID = ?", new String[] { String.valueOf(id) });
	}
}
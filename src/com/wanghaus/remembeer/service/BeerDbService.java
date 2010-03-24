package com.wanghaus.remembeer.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;

import com.wanghaus.remembeer.R;

public class BeerDbService {
	private static final String DB_NAME = "BeerLog";
	private static final String DB_TABLE = "beer_log";
	private static final int DB_VERSION = 3;

	private static final String DB_CREATE = 
		"CREATE TABLE IF NOT EXISTS " + DB_TABLE + "(" +
			"beername VARCHAR(255) NOT NULL, " +
			"container VARCHAR(32) NOT NULL, " +
			"stamp DATETIME NOT NULL, " +
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
        	
        	if (oldVersion < 3) {
        		db.execSQL("UPDATE " + DB_TABLE + " " +
        				"SET container='Bottle' WHERE container LIKE 'Bottle%'");
        	}
        	
        	// if (oldVersion < 4) etc..
        	// ABV (float)
        	// Brewery (varchar255)
        	// Location (varchar 255)
        	// Notes (uhh... varchar1024? pointer to some other file?)
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
    
	public void setBeerRating(long id, int rating) {
		ContentValues newRow = new ContentValues();
        newRow.put("rating", rating);

        db.update(DB_TABLE, newRow, "ROWID = ?", new String[] { String.valueOf(id) });
	}

	public void deleteBeer(long id) {
		db.execSQL("DELETE FROM " + DB_TABLE + " WHERE ROWID = " + String.valueOf(id));
    }
    
    /*
     * Read methods 
     */
    public Cursor getBeerHistory() {
    	return getBeerHistory(null, "stamp DESC");
    }
    public Cursor getBeerHistory(Integer limit) {
    	return getBeerHistory(limit, "stamp DESC");
    }
    
    public Cursor getBeerHistoryAlphabetically() {
    	return getBeerHistory(null, "beername ASC");
    }
    public Cursor getBeerHistoryAlphabetically(Integer limit) {
    	return getBeerHistory(limit, "beername ASC");
    }

    public Cursor getBeerHistory(Integer limit, String sortBy) {
    	String strLimit = null;
    	if (limit != null)
    		strLimit = limit.toString();
    	
        return db.query(DB_TABLE,
        		new String[] {"ROWID AS _id", "beername", "container || ' at ' || stamp AS details", "rating", "container"},
        		null, null, null, null, sortBy, strLimit);
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
    
    public String[] getContainers() {
    	LinkedList<String> rv = new LinkedList<String>();
    	
    	// First get the list from the xml
    	Resources res = context.getResources();
    	CharSequence[] containers = res.getTextArray(R.array.containers);
    	for (CharSequence b : containers)
    		rv.add(b.toString());
    	
    	// Then get the most-used containers
    	Cursor containerQuery = db.query(DB_TABLE, new String[] {"container"}, 
    			null, null, "container", null, "COUNT(*) ASC");
    	
    	// Resort as needed
    	while (containerQuery.moveToNext()) {
    		int id;
    		for (id = 0; id < rv.size(); ++id) {
    			if (rv.get(id).equals(containerQuery.getString(0))) {
    				String val = rv.remove(id);
    				rv.addFirst(val);
    				break;
    			}
    		}
    	}
    	containerQuery.close();
    	
    	return rv.toArray(new String[0]);
    }
    
    public long getBeerCount() {
    	Cursor beercountQuery = db.query(DB_TABLE, new String[] {"ROWID"}, null, null, null, null, null);
    	return beercountQuery.getCount();
    }
    
    public long getBeerCount(String querybeer) {
    	if (querybeer == null)
    		return getBeerCount();

    	Cursor beercountQuery = getBeerNames(querybeer);
    	return beercountQuery.getCount();
    }
    
    public long getBeerCountThisYear() {
    	Cursor beercountQuery = db.query(DB_TABLE, new String[] {"ROWID"},
    			"STRFTIME('%Y', stamp) = STRFTIME('%Y', current_date)",
    			null, null, null, null);
    	return beercountQuery.getCount();
    }
    
    public long getBeerCountThisMonth() {
    	Cursor beercountQuery = db.query(DB_TABLE, new String[] {"ROWID"},
    			"STRFTIME('%Y%m', stamp) = STRFTIME('%Y%m', current_date)",
    			null, null, null, null);
    	return beercountQuery.getCount();
    }

    public long getBeerCountLastDays(Integer count) {
    	Cursor beercountQuery = db.query(DB_TABLE, new String[] {"ROWID"},
    			"JULIANDAY(stamp) > JULIANDAY(current_date) - ? AND JULIANDAY(stamp) < JULIANDAY(current_date)", // I'm looking at you, DST
    			new String[] {count.toString()},
    			null, null, null);
    	return beercountQuery.getCount();
    }
    
    public long getBeerTypesCount() {
    	Cursor beercountQuery = db.query(DB_TABLE, new String[] {"DISTINCT beername"},
    			null, null, null, null, null);
    	return beercountQuery.getCount();
    }
    
    public String getFavoriteBeer() {
    	// TODO - There's gotta be a better way to calculate this
    	Cursor q = db.query(DB_TABLE,
    			new String[] {"beername", "AVG(rating) AS rating"},
    			null, null, "beername",
    			null, "AVG(rating) DESC, COUNT(*) DESC", "1");

    	if (q.getCount() == 0)
    		return "-";
    	
    	q.moveToFirst();
    	String favoriteBeer = q.getString(0);
    	q.close();
    	
    	return favoriteBeer;
    }
    
    public String getMostDrunkBeer() {
    	Cursor q = db.query(DB_TABLE,
    			new String[] {"beername", "COUNT(*) AS count"},
    			null, null, "beername",
    			null, "COUNT(*) DESC", "1");
    	
    	if (q.getCount() == 0)
    		return "-";
    	
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
    		return "-";
    	
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

    public Uri exportHistoryToCsvFile() {
    	Cursor q = db.query(DB_TABLE,
    			new String[] {"beername", "container", "stamp", "rating"},
    			null, null, null,
    			null, "stamp ASC");

    	StringBuilder csvData = new StringBuilder();
    	int numCols = q.getColumnCount();
    	
    	// Write the headers
    	for (int col = 0; col < numCols; ++col) {
    		String colname = q.getColumnName(col);
			if (colname != null)
				colname = colname.replaceAll("\"", "\\\""); // escape quotes
			csvData.append("\"");
			csvData.append(colname);
			csvData.append("\"");
			
			if (col < numCols - 1)
				csvData.append(",");
    	}
    	csvData.append("\n");
    	
    	// Write the data
    	q.moveToFirst();
    	do {
    		for (int col = 0; col < numCols; ++col) {
    			String val = q.getString(col);
    			if (val != null)
    				val = val.replaceAll("\"", "\\\""); // escape quotes
    			csvData.append("\"");
    			csvData.append(val);
    			csvData.append("\"");

    			if (col == numCols - 1)
        			csvData.append("\n");
    			else
        			csvData.append(",");
    		}
    	} while (q.moveToNext());
    	
    	q.close();
    	
    	// Write the csv to a file on the external storage 
    	File csvFile = new File(Environment.getExternalStorageDirectory(), "BeerLog_export.csv");
    	try {
			csvFile.createNewFile();
		} catch (IOException e) {
			// If it fails, we probably can't create a file there
			e.printStackTrace();
			return null;
		}
    	
    	try {
			FileOutputStream csvFileStream = new FileOutputStream(csvFile);
			PrintWriter csvWriter = new PrintWriter(csvFileStream);
			csvWriter.write(csvData.toString());
			csvWriter.close();
			csvFileStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
    	return Uri.parse("file://" + csvFile.getAbsolutePath());
    }
    
    public boolean isBeerUnrated(long id) {
    	Cursor q = db.query(DB_TABLE,
    			new String[] {"ROWID"},
    			"ROWID = ? and rating = 0", new String[] {String.valueOf(id)},
    			null, null, null);
    	int isUnrated = q.getCount();
    	q.close();
    	
    	return (isUnrated > 0);
	}

	public double getBeerABV(String beername) {
		// TODO Auto-generated method stub
		return 0.0;
	}

	public String getBeerBrewer(String beername) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBeerBrewerLocation(String beername) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBeerNotes(String beername) {
		// TODO Auto-generated method stub
		return null;
	}

}
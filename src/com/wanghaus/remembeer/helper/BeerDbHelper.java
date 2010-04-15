package com.wanghaus.remembeer.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import android.util.Log;

import com.wanghaus.remembeer.R;

public class BeerDbHelper {
	private static final String DB_NAME = "Remembeer";
	private static final String DB_TABLE_DRINKS = "drinks";
	private static final String DB_TABLE_BEERS = "beers";
	private static final int DB_VERSION = 4;
	private static final String DB_CSV = new String(Environment.getExternalStorageDirectory() +  File.separator + "BeerLog_export.csv");

	private static final String[] DB_CREATE = new String[] {
			"CREATE TABLE IF NOT EXISTS " + DB_TABLE_DRINKS + "("
					+ "beer_id INT NOT NULL, " // foreign key
					+ "container TEXT NOT NULL, "
					+ "stamp DATETIME NOT NULL, "
					+ "rating REAL NOT NULL DEFAULT 0, "
					+ "notes TEXT "
					+ ")",
			"CREATE TABLE IF NOT EXISTS " + DB_TABLE_BEERS + "("
					+ "name TEXT NOT NULL, "
					+ "brewery TEXT, "
					+ "brewery_location TEXT, "
					+ "abv REAL, "
					+ "notes TEXT "
					+ ")", };

    private final Context context; 
	private static SQLiteDatabase db;
    private DatabaseHelper DBHelper;

    public BeerDbHelper(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
        db = DBHelper.getWritableDatabase();

        if (localCsvModifiedDate() > 0 && getDrinkCount() == 0) {
        	importHistoryFromCsvFile();
        	// TODO - Remove csv?
        }
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	for (String sql : DB_CREATE)
        		db.execSQL(sql);
        }

        @Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	Log.e("BeerDbHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        	
			switch (oldVersion) {
			case 1:
			case 2:
				db.execSQL("ALTER TABLE " + DB_TABLE_DRINKS + " "
						+ "ADD COLUMN rating INT NOT NULL DEFAULT 0");
				db.execSQL("UPDATE " + DB_TABLE_DRINKS + " "
						+ "SET container='Bottle' WHERE container LIKE 'Bottle%'");

			case 3:
				// Create beers
				db.execSQL(
						"CREATE TABLE IF NOT EXISTS " + DB_TABLE_BEERS + "("
						+ "name TEXT NOT NULL, "
						+ "brewery TEXT, "
						+ "brewery_location TEXT, "
						+ "abv REAL, "
						+ "notes TEXT "
						+ ")");
				
				// Create temp table
				db.execSQL("CREATE TEMPORARY TABLE " + DB_TABLE_DRINKS + "_temp ("
						+ "beer_id INT NOT NULL, " // foreign key
						+ "container TEXT NOT NULL, "
						+ "stamp DATETIME NOT NULL, "
						+ "rating REAL NOT NULL DEFAULT 0, "
						+ "notes TEXT "
						+ ")");

				// For each drink
				Cursor allDrinks = db.query(DB_TABLE_DRINKS,
						new String[] { "ROWID", "beername", "container", "stamp", "rating" },
						null, null, null, null, null);
	        	Log.e("BeerDbHelper", "Migrating " + allDrinks.getCount() + " beers");

				while (allDrinks.moveToNext()) {
					String beername = allDrinks.getString(1);
					int beerid = 0;
					
					// Try to find a beer with that name
					Cursor thisbeer = db.query(DB_TABLE_BEERS,
							new String[] { "ROWID" },
							"name = ?",
							new String[] { beername },
							null, null, null);

					if (thisbeer.getCount() > 0) {
						thisbeer.moveToFirst();
						beerid = thisbeer.getInt(0);
					} else {
			        	Log.i("BeerDbHelper", "Creating beer '" + beername + "'");

						// If it doesn't exist, create it
						ContentValues newBeer = new ContentValues();
						newBeer.put("name", beername);
						beerid = (int) db.insert(DB_TABLE_BEERS, null, newBeer);
					}
					
					// Add row to temp table
					ContentValues newDrink = new ContentValues();
					newDrink.put("beer_id", beerid);
					newDrink.put("container", allDrinks.getString(2));
					newDrink.put("stamp", allDrinks.getString(3));
					newDrink.put("rating", allDrinks.getFloat(4));
					db.insert(DB_TABLE_DRINKS + "_temp", null, newDrink);
				}
				
				// Replace real table with new table (with content from temp table)
				db.execSQL("DROP TABLE " + DB_TABLE_DRINKS);
				db.execSQL("CREATE TABLE " + DB_TABLE_DRINKS + " ("
						+ "beer_id INT NOT NULL, " // foreign key
						+ "container TEXT NOT NULL, "
						+ "stamp DATETIME NOT NULL, "
						+ "rating REAL NOT NULL DEFAULT 0, "
						+ "notes TEXT "
						+ ")");
				db.execSQL("INSERT INTO " + DB_TABLE_DRINKS + " " + 
						"SELECT beer_id, container, stamp, rating, notes FROM " + DB_TABLE_DRINKS + "_temp");
				db.execSQL("DROP TABLE " + DB_TABLE_DRINKS + "_temp");

			// XXX - future cases go here
			default:
				// TODO - Raise holy hell
			}
		}
    }
    
    public void close() {
        DBHelper.close();
    }
    
    // TODO - Needs to be migrated to the two-table schema
    public int importHistoryFromCsvFile() {
    	BufferedReader inFile;
    	String iLine;
    	Integer count = new Integer(0);
    	
    	try {
    		inFile = new BufferedReader(new FileReader(DB_CSV));
    	} catch(FileNotFoundException e) {
    		return -1;
    	}
    	
    	try {
			if (!inFile.readLine().equalsIgnoreCase("\"beername\",\"container\",\"stamp\",\"rating\""))
				// The header in the file wasn't right, so we're out of here
				return -1;
		} catch (IOException e) {
			Log.e("importHistory", e.toString());
		}

    	try {
    		
    		ContentValues inputvalues = new ContentValues();
    		iLine = inFile.readLine(); // read line from file
    		while(iLine != null){
    			Log.v("importLine", iLine);
    			String[] elements = iLine.split(",");
    			inputvalues.put("beername", elements[0].substring(1, elements[0].length() -1));
    			inputvalues.put("container", elements[1].substring(1, elements[1].length() -1));
    			inputvalues.put("stamp", elements[2].substring(1, elements[2].length() -1));
    			inputvalues.put("rating", elements[3].substring(1, elements[3].length() -1));
    			if (getDrinkCountWhen(elements[2].substring(1, elements[2].length() -1)) == 0) {
    				db.insert(DB_TABLE_DRINKS, null, inputvalues);
    				count++;
    			}
    			iLine = inFile.readLine();
    		}
    		inFile.close();
    	} catch(Exception e){
    		Log.e("importHistory", "Failed to import history csv", e);
    	}
    	Log.d("ImportHistory", "Imported " + count.toString() + " Beers");
    	
		return count; 
	}

	/*
     * Write methods
     */
    // TODO - Needs to be migrated to the two-table schema
    public long addDrink(String beername, String container, Date stamp) {
    	// TODO - Lookup the beer
    	// TODO - Add it if we need to
    	
		ContentValues newRow = new ContentValues();
		
        newRow.put("beername", beername);
        newRow.put("container", container);
        
        // Get the sqlite format for the stamp
        Cursor stampCursor = db.rawQuery( "SELECT DATETIME(?, 'unixepoch', 'localtime')",
        		new String[] { String.valueOf(stamp.getTime()/1000) } );
        stampCursor.moveToFirst();
        newRow.put("stamp", stampCursor.getString(0));
		stampCursor.close();
		
		return db.insert(DB_TABLE_DRINKS, null, newRow);
    }
    
    // TODO - Needs to be migrated to the two-table schema
	public void setDrinkRating(long id, int rating) {
		ContentValues newRow = new ContentValues();
        newRow.put("rating", rating);

        db.update(DB_TABLE_DRINKS, newRow, "ROWID = ?", new String[] { String.valueOf(id) });
	}

    // TODO - Needs to be migrated to the two-table schema
	public void deleteDrink(long id) {
		db.execSQL("DELETE FROM " + DB_TABLE_DRINKS + " WHERE ROWID = " + String.valueOf(id));
    }
    
    /*
     * Read methods 
     */
    public Cursor getDrinkHistory() {
    	return getDrinkHistory(null, "stamp DESC");
    }
    public Cursor getDrinkHistory(Integer limit) {
    	return getDrinkHistory(limit, "stamp DESC");
    }
    
    public Cursor getDrinkHistoryAlphabetically() {
    	return getDrinkHistory(null, "beername ASC");
    }
    public Cursor getDrinkHistoryAlphabetically(Integer limit) {
    	return getDrinkHistory(limit, "beername ASC");
    }

    public Cursor getDrinkHistory(Integer limit, String sortBy) {
    	String strLimit = "";
    	if (limit != null)
    		strLimit = " LIMIT " + limit.toString();
    	
    	return db.rawQuery(
    			"SELECT d.ROWID AS _id, b.name AS beername, container || ' at ' || stamp AS details, rating, container "
    			+ "FROM " + DB_TABLE_DRINKS + " d, " + DB_TABLE_BEERS + " b "
    			+ "WHERE d.beer_id = b.ROWID"
    			+ strLimit
    			+ " ORDER BY " + sortBy,
    			null);
    }
    
    public Cursor getBeerNames() {
        return db.query(DB_TABLE_BEERS,
        		new String[] {"ROWID AS _id", "name"},
        		null, null, null, null, null);
    }

    // TODO - Needs to be migrated to the two-table schema
    public Cursor getBeerNames(String substr) {
    	if (substr == null)
    		return getBeerNames();
    	
        return db.query(DB_TABLE_DRINKS,
        		new String[] {"MAX(ROWID) AS _id", "beername"},
        		"beername LIKE ?",
        		new String[] { "%" + substr + "%" },
        		"beername",
        		null,
        		"COUNT(*) DESC");
    }
    
    public String[] getContainers() {
    	LinkedList<String> rv = new LinkedList<String>();
    	
    	// First get the list from the xml
    	Resources res = context.getResources();
    	CharSequence[] containers = res.getTextArray(R.array.containers);
    	for (CharSequence b : containers)
    		rv.add(b.toString());
    	
    	// Then get the most-used containers
    	Cursor containerQuery = db.query(DB_TABLE_DRINKS, new String[] {"container"}, 
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
    
    public long getDrinkCount() {
    	Cursor beercountQuery = db.query(DB_TABLE_DRINKS, new String[] {"ROWID"}, null, null, null, null, null);
    	long rv = beercountQuery.getCount();
    	beercountQuery.close();
    	return rv;
    }
    
    // TODO - Needs to be migrated to the two-table schema
    public long getDrinkCount(String querybeer) {
    	if (querybeer == null)
    		return getDrinkCount();

    	Cursor beercountQuery = getBeerNames(querybeer);
    	long rv = beercountQuery.getCount();
    	beercountQuery.close();
    	return rv;
    }
    
    public long getDrinkCountThisYear() {
    	Cursor beercountQuery = db.query(DB_TABLE_DRINKS, new String[] {"ROWID"},
    			"STRFTIME('%Y', stamp) = STRFTIME('%Y', current_date)",
    			null, null, null, null);
    	long rv = beercountQuery.getCount();
    	beercountQuery.close();
    	return rv;
    }
    
    public long getDrinkCountThisMonth() {
    	Cursor beercountQuery = db.query(DB_TABLE_DRINKS, new String[] {"ROWID"},
    			"STRFTIME('%Y%m', stamp) = STRFTIME('%Y%m', current_date)",
    			null, null, null, null);
    	long rv = beercountQuery.getCount();
    	beercountQuery.close();
    	return rv;
    }

    public long getDrinkCountLastDays(Integer count) {
    	Cursor beercountQuery = db.query(DB_TABLE_DRINKS, new String[] {"ROWID"},
    			"JULIANDAY(stamp) > JULIANDAY(current_date) - ? AND JULIANDAY(stamp) <= JULIANDAY(current_date) + 1", // I'm looking at you, DST
    			new String[] {count.toString()},
    			null, null, null);
    	long rv = beercountQuery.getCount();
    	beercountQuery.close();
    	return rv;
    }
    
    public long getBeersCount() {
    	Cursor beercountQuery = db.query(DB_TABLE_DRINKS, new String[] {"DISTINCT beername"},
    			null, null, null, null, null);
    	long rv = beercountQuery.getCount();
    	beercountQuery.close();
    	return rv;
    }
    
    // TODO - Needs to be migrated to the two-table schema
    public String getFavoriteBeer() {
    	// TODO - There's gotta be a better way to calculate this
    	Cursor q = db.query(DB_TABLE_DRINKS,
    			new String[] {"beername", "AVG(rating) AS rating"},
    			null, null, "beername",
    			null, "AVG(rating) DESC, COUNT(*) DESC, MAX(stamp) DESC", "1");

    	if (q.getCount() == 0)
    		return "-";
    	
    	q.moveToFirst();
    	String favoriteBeer = q.getString(0);
    	q.close();
    	
    	return favoriteBeer;
    }
    
    // TODO - Needs to be migrated to the two-table schema
    public String getMostDrunkBeer() {
    	Cursor q = db.query(DB_TABLE_DRINKS,
    			new String[] {"beername", "COUNT(*) AS count"},
    			null, null, "beername",
    			null, "COUNT(*) DESC, MAX(stamp) DESC", "1");
    	
    	if (q.getCount() == 0)
    		return "-";
    	
    	q.moveToFirst();
    	String favoriteBeer = q.getString(0);
    	q.close();
    	
    	return favoriteBeer;
    }
    
    public String getFavoriteDrinkingHour() {
    	Cursor q = db.query(DB_TABLE_DRINKS,
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

    // TODO - Needs to be migrated to the two-table schema
    public Uri exportHistoryToCsvFile() {
    	Cursor q = db.query(DB_TABLE_DRINKS,
    			new String[] {"beername", "container", "stamp", "rating"},
    			null, null, null,
    			null, "stamp ASC");
    	
    	if (q.getCount() == 0) {
    		q.close();
    		return null;
    	}

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
    	File csvFile = new File(DB_CSV);
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
    	Cursor q = db.query(DB_TABLE_DRINKS,
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
	
    // TODO - Needs to be migrated to the two-table schema
	public int getDrinkCountWhen(String whenStamp) {
		int count;
		
		Cursor q = db.query(DB_TABLE_DRINKS, new String[] {"beername"},
				"stamp = ?", new String[] {whenStamp}, null, null, null);
 	
		count = q.getCount();
    	q.close();
		
		return count;
	}
	
	public static long localCsvModifiedDate() {
		File localCsv = new File(DB_CSV);
		if (localCsv.exists())
			return localCsv.lastModified();
		
		return 0;
	}

    // TODO - Needs to be migrated to the two-table schema
	public Cursor searchDrinkHistory(String queryString) {
		return db.query(DB_TABLE_DRINKS,
				new String[] {"ROWID AS _id", "beername", "container || ' at ' || stamp AS details", "rating", "container"},
				"beername LIKE ?", new String[] {"%" + queryString + "%"}, null, null, null);
	}
	
}

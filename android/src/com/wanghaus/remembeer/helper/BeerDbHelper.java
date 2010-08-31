package com.wanghaus.remembeer.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.model.Beer;
import com.wanghaus.remembeer.model.Drink;

public class BeerDbHelper {
	private static final String DB_NAME = "Remembeer";
	private static final String TESTDB_NAME = "Remembeer_test";
	private static final String DB_TABLE_DRINKS = "drinks";
	private static final String DB_TABLE_BEERS = "beers";
	private static final int DB_VERSION = 5;

	private static final String[] DB_CREATE = new String[] {
			"CREATE TABLE IF NOT EXISTS " + DB_TABLE_DRINKS + "("
					+ "beer_id INT NOT NULL, " // foreign key
					+ "uuid CHAR(36), "
					+ "container TEXT NOT NULL, "
					+ "stamp DATETIME NOT NULL, "
					+ "rating REAL NOT NULL DEFAULT 0, "
					+ "notes TEXT "
					+ ")",
			"CREATE TABLE IF NOT EXISTS " + DB_TABLE_BEERS + "("
					+ "name TEXT NOT NULL, "
					+ "brewery TEXT, "
					+ "brewery_location TEXT, "
					+ "style TEXT, "
					+ "abv REAL, "
					+ "notes TEXT "
					+ ")", };

    protected final Context context; 
	private SQLiteDatabase db;
    private DatabaseHelper DBHelper;

    public BeerDbHelper(Context ctx) {
    	this(ctx, false);
    }
    public BeerDbHelper(Context ctx, boolean testdb) {
        this.context = ctx;
        
        if (testdb)
        	DBHelper = new DatabaseHelper(context, TESTDB_NAME);
        else
        	DBHelper = new DatabaseHelper(context, DB_NAME);
        
        db = DBHelper.getWritableDatabase();

        if (!testdb) {
        	// Auto-import if this isn't a test and there are no drinks in the db and there's a csv
	    	ImportExportHelper ieh = new ImportExportHelper(this);
	        if (ieh.localCsvModifiedDate() > 0 && getDrinkCount() == 0) {
	        	ieh.importHistoryFromCsvFile();
	        	// Should we remove CSV once we import?
	        }
        }
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context, String dbName) {
            super(context, dbName, null, DB_VERSION);
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
						+ "style TEXT, "
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

			case 4:
				db.execSQL("ALTER TABLE " + DB_TABLE_DRINKS + " "
						+ "ADD COLUMN uuid CHAR(36)");

			// XXX - future cases go here
			default:
				// TODO - Raise holy hell
			}
		}
    }
    
    public void close() {
        DBHelper.close();
    }

    public String datetimeString(Date stamp) {
        Cursor stampCursor = db.rawQuery( "SELECT DATETIME(?, 'unixepoch', 'localtime')",
        		new String[] { String.valueOf(stamp.getTime()/1000) } );
        stampCursor.moveToFirst();
        String stampStr = stampCursor.getString(0);
		stampCursor.close();
		
		return stampStr;
    }
	/*
     * Write methods
     */
    public int updateOrAddDrink(Drink drink) {
    	int beerId = Integer.valueOf(drink.getBeerId());
    	
		ContentValues newDrink = new ContentValues();
        newDrink.put("beer_id", beerId);
        newDrink.put("container", drink.getContainer());
        newDrink.put("stamp", drink.getStamp());
        newDrink.put("notes", drink.getNotes());
        newDrink.put("rating", drink.getRating());
        
		if (drink.getId() > 0) {
			int drinkId = drink.getId();

			// update
			Log.i("addDrink", "Updating drink '" + drinkId + "'");
			db.update(DB_TABLE_DRINKS, newDrink, "ROWID=?", new String[] { String.valueOf(drinkId) });
			
			return drinkId;
		} else {
			Log.i("addDrink", "Creating drink at '" + drink.getStamp() + "");
			
			// insert
			int drinkId = (int) db.insert(DB_TABLE_DRINKS, null, newDrink);
			drink.setId(drinkId);
			
			return drinkId;
		}
    }
	
	public int updateOrAddBeer(Beer beer) {
		ContentValues newBeer = new ContentValues();
		newBeer.put("name", beer.getName());
		newBeer.put("brewery", beer.getBrewery());
		newBeer.put("brewery_location", beer.getLocation());
		newBeer.put("style", beer.getStyle());
		if (beer.getABV() == null || beer.getABV().equals(""))
			newBeer.put("abv", (Float)null);
		else {
			try {
				newBeer.put("abv", Float.valueOf(beer.getABV()));
			} catch (Exception e) { }
		}
		newBeer.put("notes", beer.getNotes());

		if (beer.getId() > 0) {
			int beerId = Integer.valueOf(beer.getId());

			// update
			Log.i("addBeer", "Updating beer '" + beer.getName() + "' (" + beerId + ")");
			
			db.update(DB_TABLE_BEERS, newBeer, "ROWID=?", new String[] { String.valueOf(beerId) });
			beer.setId(beerId);
			
			return beerId;
		} else {
			Log.i("addBeer", "Creating beer '" + beer.getName() + "'");
			
			// insert
			int beerId = (int) db.insert(DB_TABLE_BEERS, null, newBeer);
			beer.setId(beerId);
			
			return beerId;
		}
	}
    
	public void setDrinkRating(Drink drink, float rating) {
		drink.setRating(rating);
		updateOrAddDrink(drink);
	}

	public void setDrinkNotes(Drink drink, String notes) {
		drink.setNotes(notes);
		updateOrAddDrink(drink);
	}
    
	public void deleteDrink(Drink drink) {
		int id = drink.getId();
		db.execSQL("DELETE FROM " + DB_TABLE_DRINKS + " WHERE ROWID = " + String.valueOf(id));
    }
	public void deleteBeer(Beer beer) {
		// Don't do it if there are any drinks for this beer
		List<Drink> drinks = this.getDrinks("beerId = ?", new String[] { String.valueOf(beer.getId()) }, null);
		if (drinks != null && drinks.size() > 0)
			return;
		
		int id = beer.getId();
		db.execSQL("DELETE FROM " + DB_TABLE_BEERS + " WHERE ROWID = " + String.valueOf(id));
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
    	return getDrinkHistory(null, "b.name ASC");
    }
    public Cursor getDrinkHistoryAlphabetically(Integer limit) {
    	return getDrinkHistory(limit, "b.name ASC");
    }
    public Cursor getDrinkHistory(Integer limit, String sortBy) {
    	// This really should return a Cursor, not a list.  Don't change it.
    	String strLimit = "";
    	if (limit != null)
    		strLimit = " LIMIT " + limit.toString();
    	
    	return db.rawQuery(
    			"SELECT d.ROWID AS _id, d.* "
    			+ "FROM " + DB_TABLE_DRINKS + " d, " + DB_TABLE_BEERS + " b "
    			+ "WHERE d.beer_id = b.ROWID "
    			+ strLimit
    			+ " ORDER BY " + sortBy,
    			null);
    }

	public Cursor searchDrinkHistory(String queryString) {
    	return db.rawQuery(
    			"SELECT d.ROWID AS _id, d.* "
    			+ "FROM " + DB_TABLE_DRINKS + " d, " + DB_TABLE_BEERS + " b "
    			+ "WHERE d.beer_id = b.ROWID "
    			+ "AND b.name LIKE ? ",
    			new String[] {"%" + queryString + "%"});
	}	

    public Cursor getBeerNames() {
        return db.query(DB_TABLE_BEERS,
        		new String[] {"ROWID AS _id", "name"},
        		null, null, null, null, null);
    }

    public Cursor getBeerNames(String substr) {
    	if (substr == null)
    		return getBeerNames();
    	
    	return db.rawQuery(
    			"SELECT b.ROWID AS _id, b.name AS beername "
    			+ "FROM " + DB_TABLE_DRINKS + " d, " + DB_TABLE_BEERS + " b "
    			+ "WHERE d.beer_id = b.ROWID "
    			+ "AND b.name LIKE ?"
    			+ "GROUP BY b.name "
    			+ "ORDER BY COUNT(*) DESC",
    			new String[] { "%" + substr + "%"}
    		);
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
    
    public int getDrinkCount() {
    	Cursor beercountQuery = db.query(DB_TABLE_DRINKS, new String[] {"ROWID"}, null, null, null, null, null);
    	int rv = beercountQuery.getCount();
    	beercountQuery.close();
    	return rv;
    }
    
    // XXX - This doesn't do what it thinks it does
    public int getDrinkCount(String querybeer) {
    	if (querybeer == null)
    		return getDrinkCount();

    	Cursor beercountQuery = getBeerNames(querybeer);
    	int rv = beercountQuery.getCount();
    	beercountQuery.close();
    	return rv;
    }
    
    public int getDrinkCountThisYear() {
    	Cursor beercountQuery = db.query(DB_TABLE_DRINKS, new String[] {"ROWID"},
    			"STRFTIME('%Y', stamp) = STRFTIME('%Y', current_date)",
    			null, null, null, null);
    	int rv = beercountQuery.getCount();
    	beercountQuery.close();
    	return rv;
    }
    
    public int getDrinkCountThisMonth() {
    	Cursor beercountQuery = db.query(DB_TABLE_DRINKS, new String[] {"ROWID"},
    			"STRFTIME('%Y%m', stamp) = STRFTIME('%Y%m', current_date)",
    			null, null, null, null);
    	int rv = beercountQuery.getCount();
    	beercountQuery.close();
    	return rv;
    }
    
    public int getDrinkCountLastDays(Integer count) {
    	Cursor beercountQuery = db.query(DB_TABLE_DRINKS, new String[] {"ROWID"},
    			"JULIANDAY(stamp) > JULIANDAY(current_date) - ? AND JULIANDAY(stamp) <= JULIANDAY(current_date) + 1", // I'm looking at you, DST
    			new String[] {count.toString()},
    			null, null, null);
    	int rv = beercountQuery.getCount();
    	beercountQuery.close();
    	return rv;
    }
    
    public int getBeersCount() {
    	Cursor beercountQuery = db.query(DB_TABLE_DRINKS, new String[] {"DISTINCT beer_id"},
    			null, null, null, null, null);
    	int rv = beercountQuery.getCount();
    	beercountQuery.close();
    	return rv;
    }
    
    public Beer getFavoriteBeer() {
    	// This is a kinda clunky implementation of Dirichlet priors and expected value:
    	// <http://all-thing.net/how-to-rank-products-based-on-user-input>
    	
    	Cursor ratings = db.rawQuery("select beer_id, rating, count(*) from drinks group by beer_id, rating;", null);
    	if (ratings == null || ratings.getCount() < 1) {
    		Beer beer = new Beer();
    		beer.setName("-");
    		return beer;
    	}

    	// The prior will have to change if we ever implement half-ratings
    	Map<Double, Integer> prior = new HashMap<Double, Integer>();
    	prior.put(1.0, 2); prior.put(2.0, 2); prior.put(3.0, 2); prior.put(4.0, 2); prior.put(5.0, 2); 

    	int topBeerId=0, lastBeerId=0;
    	Double topRating=0.0;
    	Map<Double, Integer> posterior = new HashMap<Double, Integer>(prior);    	
    	
    	ratings.moveToFirst();
    	do {
    		int beerId = ratings.getInt(0);
    		double rating = ratings.getDouble(1);
    		int count = ratings.getInt(2);
    		
    		if (beerId != lastBeerId) {
    			// Figure out the val for the last beer
    			int totalRatings = 0;
    			Double score = 0.0;
    			for (Double i : posterior.keySet()) {
    				int val = posterior.get(i);
    				score += (i + 1) * val;
    				totalRatings += val;
    			}
    			
    			Log.d("favoriteBeer", "beer " + beerId + " has " + totalRatings + " ratings and a score of " + score + " = " + (score/totalRatings) );
    			
    			// Is this the new biggest one?
    			if (totalRatings > 0 && score/totalRatings > topRating) {
    				topRating = score/totalRatings;
    				topBeerId = lastBeerId;
    			}
    			
    			// Reset
    	    	posterior = new HashMap<Double, Integer>(prior);    	
    			lastBeerId = beerId;
    		}

    		if (rating > 0) {
	    		int v = posterior.get(rating);
	    		posterior.put(rating, v + count);
    		}
    	} while (ratings.moveToNext());

    	// Return the beer
    	if (topBeerId > 0) {
    		Beer beer = getBeer(topBeerId);
    		return beer;
    	} else {
    		Beer beer = new Beer();
    		beer.setName("-");
    		return beer;    		
    	}
    }
    
    public Beer getMostDrunkBeer() {
    	List<Beer> topBeers = getBeers(null, null, "COUNT(*) DESC, MAX(stamp) DESC");

    	if (topBeers == null || topBeers.size() == 0) {
    		Beer beer = new Beer();
    		beer.setName("-");
    		return beer;
    	} else {
    		return topBeers.get(0);
    	}
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

	public List<Beer> getBeers(String whereStr, String[] whereArgs, String orderStr) {
		List<Beer> rv = new ArrayList<Beer>();
		
		if (whereStr != null && !whereStr.equals(""))
			whereStr = " AND " + whereStr + " ";
		else
			whereStr = "";
		
		if (orderStr != null && !orderStr.equals(""))
			orderStr = " ORDER BY " + orderStr + " ";
		else
			orderStr = "";
		
    	Cursor s = db.rawQuery(
    			"SELECT b.ROWID AS _id, b.*, COUNT(*) AS drink_count "
    			+ "FROM " + DB_TABLE_DRINKS + " d, " + DB_TABLE_BEERS + " b "
    			+ "WHERE d.beer_id = b.ROWID "
    			+ whereStr
    			+ "GROUP BY b.ROWID "
    			+ orderStr,
    			whereArgs
    		);
		if (s.getCount() > 0) {
			while (s.moveToNext()) {
				Beer b = new Beer(s);
				rv.add(b);
			}
		}
		s.close();
		
		return rv;
	}

	public List<Drink> getDrinks(String whereStr, String[] whereArgs, String orderStr) {
		List<Drink> rv = new ArrayList<Drink>();
		
		if (whereStr != null && !whereStr.equals(""))
			whereStr = " WHERE " + whereStr + " ";
		else
			whereStr = "";
		
		if (orderStr != null && !orderStr.equals(""))
			orderStr = " ORDER BY " + orderStr + " ";
		else
			orderStr = "";
		
    	Cursor s = db.rawQuery(
    			"SELECT d.ROWID AS _id, d.* "
    			+ "FROM " + DB_TABLE_DRINKS + " d "
    			+ whereStr
    			+ orderStr,
    			whereArgs
    		);
		if (s.getCount() > 0) {
			while (s.moveToNext()) {
				Drink d = new Drink(s);
				rv.add(d);
			}
		}
		s.close();
		
		return rv;
	}

	public Beer findBeerBySubstring(String beername) {
    	if (beername == null)
    		return null;

    	List<Beer> beers = getBeers(
    			"b.name LIKE ?",
    			new String[] { "%" + beername + "%"},
    			"COUNT(*) DESC");

		if (beers.size() > 0)
			return beers.get(0);
		
		Beer newbeer = new Beer();
		newbeer.setName(beername);
		return newbeer;
	}
	public Beer findBeerByName(String beername) {
    	if (beername == null)
    		return null;

    	List<Beer> beers = getBeers(
    			"b.name = ?",
    			new String[] { beername },
    			"COUNT(*) DESC");

		if (beers.size() > 0)
			return beers.get(0);
		
		return null;
	}
	
	public Beer getBeer(int beerId) {
    	List<Beer> beers = getBeers(
    			"b.ROWID = ?",
    			new String[] { String.valueOf(beerId) },
    			"COUNT(*) DESC");

		if (beers.size() > 0)
			return beers.get(0);
		
		return null;
	}
    
	public Beer getBeer(String name) {
    	List<Beer> beers = getBeers(
    			"b.name = ?",
    			new String[] { name },
    			null);

		if (beers.size() > 0)
			return beers.get(0);
		
		return null;
	}
    
	public Drink getDrink(int drinkId) {
    	List<Drink> drinks = getDrinks(
    			"ROWID = ?",
    			new String[] { String.valueOf(drinkId) },
    			null);

		if (drinks.size() > 0)
			return drinks.get(0);
		
		return null;
	}

	public List<Drink> getDrinksWhen(String whenStamp) {
		return getDrinks(
				"stamp = ?",
				new String[] { whenStamp },
				null);
	}
}

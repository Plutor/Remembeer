package com.wanghaus.beerlog.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends Activity {
	protected static final String DB_NAME = "BeerLog";
	protected static final String DB_TABLE = "beer_log";
	protected SQLiteDatabase db = null;
	
	private final int MENU_SETTINGS = 1;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initDb();
	}

	protected void initDb() {
    	try {
    		db = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
    		
    		db.execSQL(
				"CREATE TABLE IF NOT EXISTS " + DB_TABLE + "(" +
					"beername VARCHAR(255) NOT NULL, " +
					"container VARCHAR(32) NOT NULL, " +
					"stamp DATETIME NOT NULL" +
				")"
    		);
    	} catch (Exception e) {
    		// Hmm
    	}
    }

	/* Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!super.onCreateOptionsMenu(menu))
			return false;
		
	    menu.add(0, MENU_SETTINGS, 0, "Settings")
	    	.setIcon(android.R.drawable.ic_menu_preferences);

	    return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case MENU_SETTINGS:
	    	Intent nextIntent = new Intent(this, Config.class);
	    	startActivity(nextIntent);

	        return true;
	    }
	    return false;
	}
}

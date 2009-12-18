package com.wanghaus.beerlog.activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class BaseActivity extends Activity {
	protected static final String DB_NAME = "BeerLog";
	protected static final String DB_TABLE = "beer_log";
	protected SQLiteDatabase db = null;
	
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

}

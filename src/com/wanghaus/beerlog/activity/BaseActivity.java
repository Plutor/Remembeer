package com.wanghaus.beerlog.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends Activity {
	private final int MENU_SETTINGS = 1;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

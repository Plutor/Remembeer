package com.wanghaus.beerlog.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends Activity {
	private final int MENU_ADD_BEER = 1;
	private final int MENU_HISTORY= 2;
	private final int MENU_STATS = 3;
	private final int MENU_SETTINGS = 4;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/* Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!super.onCreateOptionsMenu(menu))
			return false;
		
		if (!(this instanceof AddBeer))
		    menu.add(0, MENU_ADD_BEER, 0, "Add Beer")
	    	.setIcon(android.R.drawable.ic_menu_add);

		if (!(this instanceof History)) {
		    menu.add(0, MENU_HISTORY, 0, "History")
	    	.setIcon(android.R.drawable.ic_menu_recent_history);
		} else {
		    menu.add(0, MENU_HISTORY, 0, "Export")
	    	.setIcon(android.R.drawable.ic_menu_share);
		}
		
		if (!(this instanceof Stats))
		    menu.add(0, MENU_STATS, 0, "Statistics")
	    	.setIcon(android.R.drawable.ic_menu_info_details);

		if (!(this instanceof Config))
			menu.add(0, MENU_SETTINGS, 0, "Settings")
	    	.setIcon(android.R.drawable.ic_menu_preferences);

	    return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent nextIntent;
		
	    switch (item.getItemId()) {
	    case MENU_ADD_BEER:
	    	nextIntent = new Intent(this, AddBeer.class);
	    	startActivity(nextIntent);

	        return true;
	    case MENU_HISTORY:
	    	nextIntent = new Intent(this, History.class);
	    	startActivity(nextIntent);

	        return true;
	    case MENU_STATS:
	    	nextIntent = new Intent(this, Stats.class);
	    	startActivity(nextIntent);

	        return true;
	    case MENU_SETTINGS:
	    	nextIntent = new Intent(this, Config.class);
	    	startActivity(nextIntent);

	        return true;
	    }
	    return false;
	}
}

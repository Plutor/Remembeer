package com.wanghaus.remembeer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.wanghaus.remembeer.R;

public class BaseActivity extends Activity {
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/* Creates the menu items */
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!super.onCreateOptionsMenu(menu))
			return false;
		
		if (this instanceof AddBeerDone)
			return false;
		
		getMenuInflater().inflate(R.layout.optionsmenu, menu);
		return true;  
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		if (this instanceof AddBeer)
			menu.findItem(R.id.optionsmenu_addbeer)
				.setVisible(false);

		if (this instanceof History) {
			menu.findItem(R.id.optionsmenu_history)
				.setVisible(false);
			// History.onPrepareOptionsMenu() will make sure the sort button does the right thing
		} else {
			menu.findItem(R.id.optionsmenu_history_sort)
				.setVisible(false);
			menu.findItem(R.id.optionsmenu_export)
				.setVisible(false);
		}
		
		if (this instanceof Stats || this instanceof ChartsList || this instanceof ViewChart)
			menu.findItem(R.id.optionsmenu_statistics)
				.setVisible(false);

	    return true;
	}

	/* Handles item selections */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent nextIntent;
		
	    switch (item.getItemId()) {
	    case R.id.optionsmenu_addbeer:
	    	nextIntent = new Intent(this, AddBeer.class);
	    	startActivity(nextIntent);

	        return true;
	    case R.id.optionsmenu_history:
	    	nextIntent = new Intent(this, History.class);
	    	startActivity(nextIntent);

	        return true;
	    case R.id.optionsmenu_export:
	    	nextIntent = new Intent(this, ImportExport.class);
		    startActivity(nextIntent);
	    	
	        return true;
	    case R.id.optionsmenu_statistics:
	    	nextIntent = new Intent(this, Stats.class);
	    	startActivity(nextIntent);

	        return true;
	    case R.id.optionsmenu_settings:
	    	nextIntent = new Intent(this, Config.class);
	    	startActivity(nextIntent);

	        return true;
	    }
	    return false;
	}
}

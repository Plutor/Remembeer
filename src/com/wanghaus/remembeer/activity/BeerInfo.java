package com.wanghaus.remembeer.activity;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.helper.BeerDbHelper;

public class BeerInfo extends BaseActivity {
	private String beername;
	private Map<String, String> beer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beerinfo);

		BeerDbHelper dbs = new BeerDbHelper(this);
		int beerId = getIntent().getIntExtra("beerId", -1);
		int drinkId = getIntent().getIntExtra("drinkId", -1);
		beername = getIntent().getStringExtra("beerId");
		
		if (drinkId != -1) {
			try {
				Map<String, String> drink = dbs.getDrinkInfo(drinkId);
				beerId = Integer.valueOf( drink.get("beer_id") );
			} catch (Exception e) {
				beerId = -1;
			}
		}
		
		if (beerId != -1) {
			// Load the beer
			beer = dbs.getBeerInfo(beerId);
			beername = beer.get("name");
			setTitle( "Edit " + beername + " info" );
		} else if (beername != null) {
			beer = new HashMap<String, String>();
			beer.put("name", beername);
			setTitle( "Edit " + beername + " info" );
		}

		// Make sure it's full-width 
        getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        
        // Fill in the known values
        if (beer != null && beer.size() > 0) {
        	try {
        		EditText v = (EditText) findViewById(R.id.beerinfo_abv);
        		v.setText( beer.get("abv") );
        	} catch (Exception e) { /* nothin */ }

        	try {
        		EditText v = (EditText) findViewById(R.id.beerinfo_brewery);
        		v.setText( beer.get("brewery") );
        	} catch (Exception e) { /* nothin */ }

        	try {
        		EditText v = (EditText) findViewById(R.id.beerinfo_location);
        		v.setText( beer.get("brewery_location") );
        	} catch (Exception e) { /* nothin */ }

        	try {
        		EditText v = (EditText) findViewById(R.id.beerinfo_style);
        		v.setText( beer.get("style") );
        	} catch (Exception e) { /* nothin */ }

        	try {
        		EditText v = (EditText) findViewById(R.id.beerinfo_notes);
        		v.setText( beer.get("notes") );
        	} catch (Exception e) { /* nothin */ }
        }
	}
}

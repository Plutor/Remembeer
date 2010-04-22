package com.wanghaus.remembeer.activity;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.helper.BeerDbHelper;
import com.wanghaus.remembeer.model.Beer;

public class BeerInfo extends BaseActivity {
	private String beername;
	private Beer beer;
	private Map<String, String> drink;
	
	private BeerDbHelper dbs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beerinfo);

		dbs = new BeerDbHelper(this);

		int beerId = getIntent().getIntExtra("beerId", -1);
		int drinkId = getIntent().getIntExtra("drinkId", -1);
		beer = (Beer) getIntent().getSerializableExtra("beer");
		beername = getIntent().getStringExtra("beerId");
		
		if (drinkId != -1) {
            Log.i("BeerInfo", "Got drinkId = " + drinkId);

			try {
				drink = dbs.getDrinkInfo(drinkId);
				beerId = Integer.valueOf( drink.get("beer_id") );
			} catch (Exception e) {
				Log.e("BeerInfo", "Can't load drink " + drinkId, e);
				beerId = -1;
			}
			
			if (drink != null) {
				// Show the rating section
	    		View metadataView = findViewById(R.id.metadata);
	    		metadataView.setVisibility( View.VISIBLE );
	
	    		// Init ratingbar
				RatingBar ratingBar = (RatingBar) findViewById(R.id.rating);
				if (ratingBar != null)
					ratingBar.setRating( Float.valueOf(drink.get("rating")) );
			}
			
            Log.i("BeerInfo", "Got beerId = " + beerId);
		}
		
		if (beer != null) {
			beername = beer.getName();
		} else if (beerId != -1) {
			// Load the beer
			beer = dbs.getBeer(beerId);
			beername = beer.get("name");
		} else if (beername != null) {
			beer = new Beer();
			beer.put("name", beername);
		}

		if (beername != null)
			setTitle( getText(R.string.beerinfo_title) + " " + beername );
		
		// Make sure it's full-width 
        getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        
        // Fill in the known values
        if (beer != null && beer.size() > 0) {
        	setViewWithValue( R.id.beerinfo_abv, beer, "abv" );
        	setViewWithValue( R.id.beerinfo_brewery, beer, "brewery" );
        	setViewWithValue( R.id.beerinfo_location, beer, "brewery_location" );
        	setViewWithValue( R.id.beerinfo_style, beer, "style" );
        	setViewWithValue( R.id.beerinfo_notes, beer, "notes" );
        }
        
        // Init save button
        OnClickListener saveClickListener = new OnClickListener() {
			public void onClick(View button) {
				Intent resultData = new Intent();
				
				if (beer == null) {
					beer = new Beer();
					beer.setName(beername);
				}
				beer.setABV( getValueFromView(R.id.beerinfo_abv) );
				beer.setBrewery( getValueFromView(R.id.beerinfo_brewery) );
				beer.setLocation( getValueFromView(R.id.beerinfo_location) );
				beer.setStyle( getValueFromView(R.id.beerinfo_style) );
				beer.setNotes( getValueFromView(R.id.beerinfo_notes) );
				resultData.putExtra("beer", beer);
				
	    		if (drink != null) {
					RatingBar ratingBar = (RatingBar) findViewById(R.id.rating);
					resultData.putExtra("drinkId", Integer.valueOf(drink.get("_id")) );
					resultData.putExtra("rating", ratingBar.getRating());
	    		}
				
        		setResult(RESULT_OK, resultData);
        		dbs.close();
        		finish();
			}
        };
        Button b = (Button) findViewById(R.id.beerinfo_saveInfo);
        b.setOnClickListener(saveClickListener);
        b = (Button) findViewById(R.id.beerinfo_saveRating);
        b.setOnClickListener(saveClickListener);
	}
	
	private void setViewWithValue(int viewId, Beer beer, String key) {
    	try {
    		EditText v = (EditText) findViewById( viewId );
    		v.setText( beer.get(key) );
    	} catch (Exception e) {
    		/* nothin */ 
    	}
	}
	
	private String getValueFromView(int viewId) {
		try {
    		EditText v = (EditText) findViewById(viewId);
    		return v.getText().toString();
    	} catch (Exception e) {
    		/* nothin */
    	}
    	
    	return null;
	}
}

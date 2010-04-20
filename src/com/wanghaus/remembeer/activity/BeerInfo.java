package com.wanghaus.remembeer.activity;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.helper.BeerDbHelper;
import com.wanghaus.remembeer.model.Beer;

public class BeerInfo extends BaseActivity {
	private String beername;
	private Beer beer;
	
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
			beer = dbs.getBeer(beerId);
			beername = beer.get("name");
			setTitle( "Edit " + beername + " info" );
		} else if (beername != null) {
			beer = new Beer();
			beer.put("name", beername);
			setTitle( "Edit " + beername + " info" );
		}

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
        Button b = (Button) findViewById(R.id.beerinfo_save);
        b.setOnClickListener( new OnClickListener() {
			public void onClick(View button) {
				Intent resultData = new Intent();
				
				setIntentExtraFromView( R.id.beerinfo_abv, resultData, "abv" );
				setIntentExtraFromView( R.id.beerinfo_brewery, resultData, "brewery" );
				setIntentExtraFromView( R.id.beerinfo_location, resultData, "brewery_location" );
				setIntentExtraFromView( R.id.beerinfo_style, resultData, "style" );
				setIntentExtraFromView( R.id.beerinfo_notes, resultData, "notes" );

        		setResult(RESULT_OK, resultData);
        		finish();
			}
        });
	}
	
	private void setViewWithValue(int viewId, Beer beer, String key) {
    	try {
    		EditText v = (EditText) findViewById( viewId );
    		v.setText( beer.get(key) );
    	} catch (Exception e) {
    		/* nothin */ 
    	}
	}
	
	private void setIntentExtraFromView(int viewId, Intent intent, String key) {
		try {
    		EditText v = (EditText) findViewById(viewId);
    		intent.putExtra(key, v.getText().toString());
    	} catch (Exception e) {
    		/* nothin */
    	}
	}
}

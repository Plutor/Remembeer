package com.wanghaus.remembeer.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.helper.BeerDbHelper;
import com.wanghaus.remembeer.model.Beer;
import com.wanghaus.remembeer.model.Drink;



public class BeerInfo extends Activity {
	private String beername;
	private Beer beer;
	private Drink drink;
	
	private BeerDbHelper dbs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beerinfo);

		dbs = new BeerDbHelper(this);

		beer = (Beer) getIntent().getSerializableExtra("beer");
		drink = (Drink) getIntent().getSerializableExtra("drink");
		
		if (drink != null) {
            Log.i("BeerInfo", "Got drinkId = " + drink.getId());
    		beer = dbs.getBeer(drink.getBeerId());

			// Show the rating section
    		View metadataView = findViewById(R.id.metadata);
    		metadataView.setVisibility( View.VISIBLE );

    		// Init ratingbar
			RatingBar ratingBar = (RatingBar) findViewById(R.id.rating);
			if (ratingBar != null)
				ratingBar.setRating( drink.getRating() );
			
			// Init notes
            TextView notesView = (TextView) findViewById(R.id.addbeer_notes);
            if (notesView != null)
            	notesView.setText( drink.getNotes() );

			TextView drankWhen = (TextView) findViewById(R.id.drank_when);
	        drankWhen.setText("@ " + drink.getStamp());
		}
		
		if (beer != null)
			beername = beer.getName();

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

            AutoCompleteTextView styleInput = (AutoCompleteTextView) findViewById(R.id.beerinfo_style);
            String[] known_styles = getBeerStylesList();
            ArrayAdapter<String> styleArray = new ArrayAdapter<String>(this, R.layout.list_item, known_styles);
            styleInput.setAdapter(styleArray);
            
            AutoCompleteTextView breweryView = (AutoCompleteTextView) findViewById(R.id.beerinfo_brewery);
            Cursor cursor = dbs.getBreweryNames();
            BreweryNameAutocompleteAdapter list = new BreweryNameAutocompleteAdapter(this, cursor);
            breweryView.setAdapter(list); 
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
	    			drink.setRating(ratingBar.getRating());

					TextView notesView = (TextView) findViewById(R.id.addbeer_notes);
	    			drink.setNotes(notesView.getText().toString());

					resultData.putExtra("drink", drink);
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
	
	private String[] getBeerStylesList() {
		List<String> beerStylesList = new ArrayList<String>();
		
		try {
			XmlResourceParser xrp = getResources().getXml(R.xml.beer_styles);
			while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
				if (xrp.getEventType() == XmlResourceParser.START_TAG) {
					String tagname = xrp.getName();
					if (tagname.equals("beerstyle")) {
						String name = xrp.getAttributeValue(null, "name");
						if (name != null)
							beerStylesList.add(name);
					}
				}
				
				xrp.next();
			}
		} catch (Exception e) {
			Log.e("getBeerStylesList", "Failure to parse beer_styles.xml", e);
		}
		
		return beerStylesList.toArray( new String[]{} );
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
	
    private class BreweryNameAutocompleteAdapter extends CursorAdapter {
        public BreweryNameAutocompleteAdapter(Context context, Cursor c) {
                super(context, c);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
                int columnIndex = cursor.getColumnIndexOrThrow("brewery");
                ((TextView) view).setText(cursor.getString(columnIndex));
        }

        @Override
        public String convertToString(Cursor cursor) {
                int columnIndex = cursor.getColumnIndexOrThrow("brewery");
                return cursor.getString(columnIndex);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
                final LayoutInflater inflater = LayoutInflater.from(context);
                final TextView view = (TextView) inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
                int columnIndex = cursor.getColumnIndexOrThrow("brewery");
                view.setText(cursor.getString(columnIndex));
                return view;
        }

        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        	if (constraint == null)
        		return dbs.getBreweryNames();

            return dbs.getBreweryNames( constraint.toString() );
        }
    }
}

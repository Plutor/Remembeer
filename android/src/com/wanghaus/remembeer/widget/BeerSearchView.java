package com.wanghaus.remembeer.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.activity.BeerInfo;
import com.wanghaus.remembeer.helper.BeerDbHelper;
import com.wanghaus.remembeer.helper.WebServiceHelper;
import com.wanghaus.remembeer.model.Beer;

public class BeerSearchView extends LinearLayout {
	private static final int BEERINFO_DIALOG_ID = 2;

	private final int BEERLOOKUP_WAIT_MSEC = 200;
	private WebServiceHelper wsh;
	private Handler handler;
	private BeerDbHelper dbs;	
	private CharSequence beernameWhenLookupScheduled = null;
	private Context context;
	private Beer beer;
	
	public BeerSearchView(Context context) {
		super(context);
		init(context);
	}
	
	public BeerSearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public void init(Context context) {
		this.context = context;

		// Ugh
		LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.beer_search, this, true);

		dbs = new BeerDbHelper(context);
        wsh = new WebServiceHelper(context);
        handler = new Handler();
        
        initBeerAutocomplete();
        initBeerinfoPreview();
	}
	
	public void setBeer(Beer beer) {
		// TODO
	}
	
	public Beer getBeer() {
		return beer;
	}

	private String getCurrentBeerName() {
		TextView beernameView = (TextView) findViewById(R.id.beername);
		return beernameView.getText().toString();
	}
    
	private void initBeerAutocomplete() {
        // Beer name autocomplete text field
        AutoCompleteTextView beernameView = (AutoCompleteTextView) findViewById(R.id.beername);
        
        Cursor cursor = dbs.getBeerNames();
        
        BeerNameAutocompleteAdapter list = new BeerNameAutocompleteAdapter(context, cursor);
        beernameView.setAdapter(list); 

        // When something is typed, schedule a lookup for 200ms later
        beernameView.addTextChangedListener( new TextWatcher() {
			public void afterTextChanged(Editable s) { }
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void onTextChanged(CharSequence currentBeername, int start, int before, int count) {
				String oldbeername = (beernameWhenLookupScheduled == null) ? null : beernameWhenLookupScheduled.toString();
				String nowbeername = (currentBeername == null) ? null : currentBeername.toString();
				
		        if ( nowbeername != null && !nowbeername.equals(oldbeername) ) {
			        scheduleLookup(currentBeername.toString(), BEERLOOKUP_WAIT_MSEC);
		        }
			}
        });
        
        // When an autocomplete item is chosen, search right away
        beernameView.setOnItemClickListener( new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		        if (v instanceof TextView) {
		        	TextView tv = (TextView) v;
		        	
			        String selectedBeername = tv.getText().toString();
			        scheduleLookup(selectedBeername, 0);
		        }
			}
        });
        
        if (beer != null)
        	// We already have a beer, this is probably a reorientation
        	showBeerPreview(beer);
        else
        	// Lookup at init, in case we're "drinking another"
        	performSearch();
    }

    private void initBeerinfoPreview() {
    	View beerinfoPreview = findViewById(R.id.beerInfoPreview);
    	final Context context = this.context;
    	
    	beerinfoPreview.setOnClickListener( new View.OnClickListener() {
			public void onClick(View arg0) {
		    	Intent beerInfoPopupIntent = new Intent(context, BeerInfo.class);
		    	if (beer != null)
		    		beerInfoPopupIntent.putExtra("beer", beer);
		    	else {
		            AutoCompleteTextView beernameView = (AutoCompleteTextView) findViewById(R.id.beername);
		            beerInfoPopupIntent.putExtra("beername", beernameView.getText().toString());
		    	}
		    	((Activity) context).startActivityForResult(beerInfoPopupIntent, BEERINFO_DIALOG_ID);
			}
    	});
    }

    private class BeerNameAutocompleteAdapter extends CursorAdapter {
        public BeerNameAutocompleteAdapter(Context context, Cursor c) {
                super(context, c);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
                int columnIndex = cursor.getColumnIndexOrThrow("beername");
                ((TextView) view).setText(cursor.getString(columnIndex));
        }

        @Override
        public String convertToString(Cursor cursor) {
                int columnIndex = cursor.getColumnIndexOrThrow("beername");
                return cursor.getString(columnIndex);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
                final LayoutInflater inflater = LayoutInflater.from(context);
                final TextView view = (TextView) inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
                int columnIndex = cursor.getColumnIndexOrThrow("beername");
                view.setText(cursor.getString(columnIndex));
                return view;
        }

        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        	if (constraint == null)
        		return dbs.getBeerNames();

            return dbs.getBeerNames( constraint.toString() );
        }
    } 
	
	private void scheduleLookup(String beername, int msec) {
	    handler.removeCallbacks(beerInfoLookupRunnable);
	    beernameWhenLookupScheduled = beername.toString();
	    handler.postDelayed(beerInfoLookupRunnable, msec);
	}
	
	private Runnable beerInfoLookupRunnable = new Runnable() {
		public void run() {
			TextView beernameView = (TextView) findViewById(R.id.beername);
	        String currentBeername = beernameView.getText().toString();
	        
	        if (currentBeername != null && currentBeername.length() > 0 &&
	    		beernameWhenLookupScheduled != null && beernameWhenLookupScheduled.equals(currentBeername)) {
	    			performSearch(currentBeername);
	        }
		}
	};
	
	private void performSearch() {
		TextView beernameView = (TextView) findViewById(R.id.beername);
	    String beername = beernameView.getText().toString();
		performSearch(beername);
	}
	private void performSearch(final String searchBeerName) {
		if (searchBeerName == null || searchBeerName.equals("")) {
			showBeerPreviewNone();
		} else {
			Log.i("beerInfoLookup", "looking up " + searchBeerName);
	
			// Start looking it up
			showBeerPreviewLoading();
			
			// Actual lookup
	        // This is potentially expensive. Fire off a thread to do it.
	        Thread t = new Thread() {
	            public void run() {
	            	String fieldValue = getCurrentBeerName();
	    			Beer beerFound = wsh.findBeerByName(searchBeerName);
	
	    			if (beerFound != null) {
	                	// We don't want to change anything if the field has changed in the meantime
	    				if (beerFound.getName().equals(fieldValue)) {
	    					beer = beerFound;
	
	    					// Tell the UI thread to update now
	        				handler.post(showBeerPreviewRunnable);
	    				}
	    			}
	            }
	        };
	        t.start();
		}
	}
	private Runnable showBeerPreviewRunnable = new Runnable() {
		public void run() {
			if (beer != null)
				showBeerPreview(beer);
		}
	};
	
	private void showBeerPreviewNone() {
		View beerInfoNoneView = findViewById(R.id.beerInfoNone);
		View beerInfoLoadingView = findViewById(R.id.beerInfoLoading);
		View beerInfoPreviewView = findViewById(R.id.beerInfoPreview);
	
		beerInfoNoneView.setVisibility(View.VISIBLE);
		beerInfoPreviewView.setVisibility(View.INVISIBLE);
		beerInfoLoadingView.setVisibility(View.INVISIBLE);
	}
	
	private void showBeerPreviewLoading() {
		View beerInfoNoneView = findViewById(R.id.beerInfoNone);
		View beerInfoLoadingView = findViewById(R.id.beerInfoLoading);
		View beerInfoPreviewView = findViewById(R.id.beerInfoPreview);
	
		beerInfoNoneView.setVisibility(View.INVISIBLE);
		beerInfoPreviewView.setVisibility(View.INVISIBLE);
		beerInfoLoadingView.setVisibility(View.VISIBLE);
	}
	
	private void showBeerPreview(Beer beer) {
		View beerInfoNoneView = findViewById(R.id.beerInfoNone);
		View beerInfoLoadingView = findViewById(R.id.beerInfoLoading);
		View beerInfoPreviewView = findViewById(R.id.beerInfoPreview);
		
		// Show the returned values
		beerInfoNoneView.setVisibility(View.INVISIBLE);
		beerInfoPreviewView.setVisibility(View.VISIBLE);
		beerInfoLoadingView.setVisibility(View.INVISIBLE);
		
		TextView previewBrewery = (TextView) findViewById(R.id.previewBrewery);
		String previewBreweryVal = context.getText(R.string.beerInfoBrewery).toString();
		if (beer.getBrewery() != null && !beer.getBrewery().equals("")) {
			previewBreweryVal += beer.getBrewery();
	
			if (beer.getLocation() != null && !beer.getLocation().equals(""))
				previewBreweryVal += ", " + beer.getLocation();
		} else
			previewBreweryVal += context.getText(R.string.unknownBeerInfo).toString();
		previewBrewery.setText(previewBreweryVal);
		
		TextView previewStyle = (TextView) findViewById(R.id.previewStyle);
		String previewStyleVal = context.getText(R.string.beerInfoStyle).toString(); 
		if (beer.getStyle() != null && !beer.getStyle().equals(""))
			previewStyleVal += beer.getStyle();
		else
			previewStyleVal += context.getText(R.string.unknownBeerInfo).toString();
		previewStyle.setText(previewStyleVal);
	}
}

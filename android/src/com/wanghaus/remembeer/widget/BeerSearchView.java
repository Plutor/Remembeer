package com.wanghaus.remembeer.widget;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
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
		this.beer = beer;
        
		if (beer != null) {
			AutoCompleteTextView beernameView = (AutoCompleteTextView) findViewById(R.id.beername);
			beernameView.setText(beer.getName());
			
			showBeerPreview(beer);
		}
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
        
        final BeerAutoCompleteAdapter autoCompleter = new BeerAutoCompleteAdapter();
        beernameView.setAdapter(autoCompleter); 
        
        // TODO - When an autocomplete item is chosen, remember it
        beernameView.setOnItemClickListener( new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Beer selected = autoCompleter.getItem(position);
				if (selected != null)
					setBeer(selected);
			}
        });
        
        //if (beer != null)
        	// TODO - We already have a beer, this is probably a reorientation
        	// TODO - Also handle "drink another"
        	//showBeerPreview(beer);
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
	
    private class BeerAutoCompleteAdapter extends BaseAdapter implements Filterable {
    	private List<Beer> beerList;
    	
		public BeerAutoCompleteAdapter() {
			super();
			beerList = new ArrayList<Beer>();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
	            LayoutInflater inflater = LayoutInflater.from(context);
	            view = inflater.inflate(R.layout.beer_search_result, parent, false);
			}
			
            Beer thisBeer = getItem(position);
            if (thisBeer != null) {
	            Log.d("getView", "Trying to show beer autocomplete " + thisBeer.getName());
	            
	            TextView text1 = (TextView) view.findViewById(R.id.text1); 
	            text1.setText(thisBeer.getName());
	            
	            TextView text2 = (TextView) view.findViewById(R.id.text2); 
	            text2.setText(thisBeer.getStyle());
	            
	            ImageView icon = (ImageView) view.findViewById(R.id.icon);
	            icon.setImageDrawable( getResources().getDrawable(R.drawable.beer_half_full) );
            }
            
            return view;
		}

		@Override
		public int getCount() {
			return beerList.size();
		}

		@Override
		public Beer getItem(int position) {
			try {
				return beerList.get(position);
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public Filter getFilter() {
			return new NullFilter();
		}
		
		private class NullFilter extends Filter {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				// clear adapter
				List<Beer> results = new ArrayList<Beer>();
				
				// Add beers that match this text right now
				List<Beer> localBeers = dbs.getBeers( constraint.toString() );
				boolean exactMatch = false;
				for (Beer b : localBeers) {
					if (results.size() >= 5) break;
					if (b.getName().equalsIgnoreCase(constraint.toString())) {
						exactMatch = true;
						results.add(0, b);
					} else {
						results.add(b);
					}
				}

				// If none of them are exactly the text, add it first
				if (!exactMatch) {
					Beer perfectBeer = new Beer();
					perfectBeer.setName(constraint.toString());
					results.add(0, perfectBeer);
				}
				
				// TODO Schedule a future library search
				//String oldbeername = (beernameWhenLookupScheduled == null) ? null : beernameWhenLookupScheduled.toString();
				//String nowbeername = (currentBeername == null) ? null : currentBeername.toString();
				//
		        //if ( nowbeername != null && !nowbeername.equals(oldbeername) ) {
			    //    scheduleLookup(currentBeername.toString(), BEERLOOKUP_WAIT_MSEC);
		        //}

				FilterResults rv = new FilterResults();
				rv.values = results;
				rv.count = results.size();
				return rv;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				beerList = (List<Beer>) results.values;
				notifyDataSetChanged();
			}
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

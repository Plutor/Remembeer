package com.wanghaus.remembeer.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
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
import com.wanghaus.remembeer.helper.BeerDbHelper;
import com.wanghaus.remembeer.helper.WebServiceHelper;
import com.wanghaus.remembeer.model.Beer;

public class BeerSearchView extends LinearLayout {
	//private final int BEERINFO_DIALOG_ID = 2;
	private final int MAX_RESULTS = 10;
	private final int MAX_RESULTS_TO_SEARCH_LIBRARY = 3;
	private final int BEERLOOKUP_WAIT_MSEC = 1000;
	
	private final int START_SEARCH = 7;
	private final int SEARCH_COMPLETE = 8;
	
	private WebServiceHelper wsh;
	private Handler handler;
	private BeerDbHelper dbs;	

    private BeerAutoCompleteAdapter autoCompleter;
    private AutoCompleteTextView beernameView;

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
        handler = getHandler();
        
        initBeerAutocomplete();
	}
	
	public void setBeer(Beer beer) {
		this.beer = beer;

		View beerPreview = findViewById(R.id.beerPreview);

		beerPreview.setVisibility(VISIBLE);
        beernameView.setVisibility(GONE);
        beernameView.setText(""); // So the dropdown doesn't show
        
		TextView beerPreviewName = (TextView)findViewById(R.id.beerPreviewName);
		beerPreviewName.setText(beer.getName());
		TextView beerPreviewDetails = (TextView)findViewById(R.id.beerPreviewDetails);
		beerPreviewDetails.setText(beer.getDetails());
	}
	
	public void unsetBeer() {
		View beerPreview = findViewById(R.id.beerPreview);
        beerPreview.setVisibility(GONE);

        beernameView.setVisibility(VISIBLE);
		beernameView.setText(beer.getName());
		beernameView.selectAll();
	}
	
	public Beer getBeer() {
		return beer;
	}

	private String getCurrentSearchText() {
		return beernameView.getText().toString();
	}
    
	private void initBeerAutocomplete() {
        // Beer name autocomplete text field
	    autoCompleter = new BeerAutoCompleteAdapter();
	    beernameView = (AutoCompleteTextView) findViewById(R.id.beername);
        
        beernameView.setAdapter(autoCompleter); 
        
        // When an autocomplete item is chosen, remember it
        beernameView.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Beer selected = autoCompleter.getItem(position);
				if (selected != null)
					setBeer(selected);
			}
        });
        
        // The beer preview magnifying glass should reopen the search
        View beerPreviewSearch = findViewById(R.id.searchAgainIcon);
        beerPreviewSearch.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				unsetBeer();
			}
        });
	}

    private class BeerAutoCompleteAdapter extends BaseAdapter implements Filterable {
    	private List<Beer> beerList;
    	private BeerSearchFilter filter;
    	private Beer throbber;
    	
		public BeerAutoCompleteAdapter() {
			super();
			beerList = new ArrayList<Beer>();
			filter = new BeerSearchFilter();
			throbber = null;
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
	            TextView text1 = (TextView) view.findViewById(R.id.text1); 
	            text1.setText(thisBeer.getName());
	            
	            TextView text2 = (TextView) view.findViewById(R.id.text2); 
	            text2.setText(thisBeer.getDetails());
	            
            	ImageView icon = (ImageView) view.findViewById(R.id.icon);
	            if (thisBeer == throbber) {
	            	icon.setImageDrawable( getResources().getDrawable(R.drawable.library_loading) );
	            	icon.startAnimation( 
	            		    AnimationUtils.loadAnimation(context, R.anim.rotate_indefinitely) );
	            } else if (beer != null) {
	            	String iconName = beer.getIcon();
	            	int id = getResources().getIdentifier(iconName, "drawable", "com.wanghaus.remembeer");
	            	if (id > 0)
		            	icon.setImageDrawable(getResources().getDrawable(id));
	            	icon.clearAnimation();
	            } else {
	            	// TODO - 'New beer' icon
	            	icon.setImageDrawable(getResources().getDrawable(R.drawable.beer_full));
	            	icon.clearAnimation();
	            }
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

		public void setSearching() {
			if (throbber == null)
				throbber = new Beer();
			
			throbber.setName("Searching library");
			beerList.add(throbber);
			notifyDataSetChanged();
		}
		
		public void doneSearching() {
			if (throbber != null) {
				beerList.remove(throbber);
				notifyDataSetChanged();
			}
		}
		
		public void addAll(Collection<Beer> list) {
			for (Beer libraryBeer : list) {
				if (beerList.size() >= MAX_RESULTS) break;
				
				// Don't add library results that are already on the list
				boolean alreadyInList = false;
				for (int i=0; i<beerList.size(); ++i) {
					Beer b = beerList.get(i);
					String entryname = b.getName();
					if ( entryname.equals(libraryBeer.getName()) ) {
						// If this is the exact beer, replace it
						if ( entryname.equals(getCurrentSearchText()) ) {
							beerList.add(i, libraryBeer);
							beerList.remove(i+1);
						}
						
						alreadyInList = true;
						break;
					}
				}
				
				if (!alreadyInList)
					beerList.add(libraryBeer);
			}
			
			notifyDataSetChanged();
		}
		
		@Override
		public Filter getFilter() {
			return filter;
		}
		
		private class BeerSearchFilter extends Filter {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults rv = new FilterResults();
				List<Beer> results = new ArrayList<Beer>();

				if (constraint != null) {
					// Add beers that match this text right now
					List<Beer> localBeers = dbs.getBeers( constraint.toString() );
					boolean exactMatch = false;
					for (Beer b : localBeers) {
						if (results.size() >= MAX_RESULTS) break;
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
	
						while (results.size() > MAX_RESULTS)
							results.remove( results.size()-1 );					
					}
					
					// Schedule a future library search
					if (results.size() < MAX_RESULTS_TO_SEARCH_LIBRARY) {
				        scheduleLibraryLookup(constraint.toString(), BEERLOOKUP_WAIT_MSEC);
					}
				}
				
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

    /*
     * Type something
     * Wait
     * If text has changed, stop
     * Loading throbber shows up
     * In a new thread:
     *  Search starts
     *  Search returns
     *  Tell the UI thread
     * If text has changed, stop
     * Hide throbber
     * Add results to list
     */
	private void scheduleLibraryLookup(String beername, int msec) {
		Message msg = handler.obtainMessage();
		Bundle msgBundle = new Bundle();
		
		msg.what = START_SEARCH;
		msgBundle.putString("search", beername);
		msg.setData(msgBundle);

		handler.sendMessageAtTime(msg, SystemClock.uptimeMillis() + msec);
	}
	
	public Handler getHandler() {
		return new Handler() {
	    	@SuppressWarnings("unchecked")
			public void handleMessage(Message msg) {
	    		String searchString;

	    		// extract the search
	    		Bundle msgBundle = msg.getData();
	    		if (msgBundle == null) return;
	    		searchString = msgBundle.getString("search");
	    		if (searchString == null) return;
	    		
	    		if (msg.what == START_SEARCH) {
	    			// start the search
	    			performLibraryLookup(searchString);
	    		} else if (msg.what == SEARCH_COMPLETE) {
	    			List<Beer> results = null; 
	    			try {
		    			// extract the results
	    				results = (List<Beer>) msg.obj;
	    			} catch (Exception e) {}
	    			
	    			if (results == null) return;
	    			
	    			showLibraryLookupResults(searchString, results);
	    		}
	    	}
	    };
	}
	
	private void performLibraryLookup(final String search) {
		String curSearchText = getCurrentSearchText();
        
		// Don't search if the text has already changed
        if (curSearchText == null ||
        	curSearchText.equals("") ||
        	search == null ||
        	search.equals("") ||
    		!search.equals(curSearchText))
        	return;

		Log.i("beerInfoLookup", "looking up " + search);

		// Start looking it up
		showLibraryLookupThrobber();
		
		// Actual lookup
        // This is potentially expensive. Fire off a thread to do it.
        Thread t = new Thread() {
            public void run() {
    			Beer beerFound = wsh.findBeerByName(search);

    			// TEMP - Until the web service returns multiple beers
    			List<Beer> results = new ArrayList<Beer>();
    			if (beerFound != null)
    				results.add(beerFound);

    			Message msg = handler.obtainMessage();
				Bundle msgBundle = new Bundle();
				
				msg.what = SEARCH_COMPLETE;
				msgBundle.putString("search", search);
				msg.obj = results;
				msg.setData(msgBundle);

				handler.sendMessage(msg);
            }
        };
        t.start();
	}

	private void showLibraryLookupThrobber() {
		autoCompleter.setSearching();
		beernameView.showDropDown();
	}
	
	private void showLibraryLookupResults(String search, List<Beer> results) {
		String curSearchText = getCurrentSearchText();
        
		// Don't search if the text has already changed
        if (curSearchText == null ||
        	curSearchText.equals("") ||
        	search == null ||
        	search.equals("") ||
    		!search.equals(curSearchText))
        	return;

		autoCompleter.doneSearching();

		if (results.size() > 0)
			autoCompleter.addAll(results);
		
		beernameView.showDropDown();
	}
}

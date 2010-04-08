package com.wanghaus.remembeer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.helper.BeerDbHelper;

public class History extends BaseActivity {
	static final int DRINK_ANOTHER = 1;
	static final int DELETE = 2;
	static final int DO_DELETE = 3;
	
	private ListView historyList;
	private Cursor recentBeers;
	private BeerDbHelper dbs;
	private ListAdapter historyAdapter;
	private boolean isSearch;
	
	private View.OnClickListener clickListener;
	private View.OnCreateContextMenuListener contextMenuListener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        
        final Intent queryIntent = getIntent();
        final String queryAction = queryIntent.getAction();
        
		if (Intent.ACTION_SEARCH.equals(queryAction)) {
			isSearch = true;
			doSearchWithIntent(queryIntent);
		} else {
			isSearch = false;
			initBeerList();
		}
		
        clickListener = new View.OnClickListener() {
        	public void onClick(View itemView) {
        		View smallRatingView = itemView.findViewById(R.id.smallRating);
        		View metadataView = itemView.findViewById(R.id.metadata);
        		boolean isVisible = (metadataView.getVisibility() == View.VISIBLE);
        		metadataView.setVisibility( isVisible ? View.GONE : View.VISIBLE );
        		smallRatingView.setVisibility( isVisible ? View.VISIBLE : View.INVISIBLE );
			}
        };
         
        /* Add Context menu listener to the ListView. */
        final Activity topThis = this;
        contextMenuListener = new View.OnCreateContextMenuListener() {
             public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                 menu.setHeaderTitle("ContextMenu");

                 // Drink another
                 Integer position = (Integer)v.getTag();
                 String beername = getBeerValue(position, "beername");
                 if (beername != null) {
                	 MenuItem item = menu.add(0, DRINK_ANOTHER, 0, getString(R.string.drink_another) + beername);
          			 Intent nextIntent = new Intent(getBaseContext(), AddBeer.class);
          			 nextIntent.putExtra("beername", beername);
          			 nextIntent.putExtra("container", getBeerValue(position, "container"));
           			 item.setIntent(nextIntent);
                 }
                 
                 // Delete
                 final long beerId = Long.valueOf(getBeerValue(position, "_id"));
            	 MenuItem item = menu.add(0, DELETE, 0, "Delete");
            	 item.setOnMenuItemClickListener( new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						new AlertDialog.Builder(topThis).setTitle(getString(R.string.history_delete))
						.setMessage(getString(R.string.history_delete_warn))
						.setPositiveButton((CharSequence) getString(R.string.cancel),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0, int arg1) {
										dbs.deleteBeer(beerId);
										// Update the view
										SimpleCursorAdapter listAdapter = (SimpleCursorAdapter) historyList
												.getAdapter();
										listAdapter.getCursor().requery();
									}
								}).setNegativeButton(getString(R.string.cancel), null).show();

						return true; /* true means: "we handled the event". */
					}
            	 });
             }
        };
    }
    
    private void initBeerList() {
    	if (historyList == null)
    		historyList = (ListView)findViewById(R.id.history_list);
        
        // Get the last ten beers
    	if (dbs == null)
    		dbs = new BeerDbHelper(this);
    	
    	// First check what the sort setting is
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getString("sortBy", "date").equals("alpha")) {
        	recentBeers = dbs.getBeerHistoryAlphabetically();
        } else {
        	recentBeers = dbs.getBeerHistory();
        }
        
        // Map Cursor columns to views defined in simple_list_item_2.xml
        historyAdapter = new HistoryCursorAdapter(this,
                R.layout.history_row, recentBeers, 
                new String[] { "beername", "details" }, 
                new int[] { R.id.beername, R.id.details });

        historyList.setAdapter(historyAdapter);
    }
    
    private String getBeerValue(Integer position, String colName) {
		try {
			recentBeers.moveToPosition(position);
			int column = recentBeers.getColumnIndexOrThrow(colName);
			String rv = recentBeers.getString(column);
			
			return rv;
		} catch (Exception e) {
			Log.e("getBeerValue", "Can't determine beer " + colName, e);
		}
		return null;
	}
    
    public class HistoryCursorAdapter extends SimpleCursorAdapter {
    	private Context context;
    	@SuppressWarnings("unused")
		private int layout;
    	
		public HistoryCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
			super(context, layout, c, from, to);
			
			this.context = context;
			this.layout = layout;
		}

	     public View getView(int position, View convertView, ViewGroup parent) {
	    	 View row;
	    	 
	    	 if (position == 0 && !isSearch) {
	    		 // Show the add button
		         LayoutInflater inflater = LayoutInflater.from(context);
		         row = inflater.inflate(R.layout.history_add, null);
		         
		         View addBeerButton = row.findViewById(R.id.history_addbeer);
		         addBeerButton.setOnClickListener(new View.OnClickListener() {
		        	 public void onClick(View arg0) {
		        		 Intent nextIntent = new Intent(getBaseContext(), AddBeer.class);
		        		 startActivity(nextIntent);
		        	 }
		         });
	    	 } else if (position == 0 && isSearch) {
	    		// Show the add button
		         LayoutInflater inflater = LayoutInflater.from(context);
		         row = inflater.inflate(R.layout.history_clear, null);
		         
		         View clearButton = row.findViewById(R.id.history_clear);
		         clearButton.setOnClickListener(new View.OnClickListener() {
		        	 public void onClick(View arg0) {
		        		 finish();
		        	 }
		         });
	    	 } else {
	    		 // Show a real beer row
	    		 position--;

		         final Cursor cursor = getCursor();
		         cursor.moveToPosition(position);
		         int index;
		         
		         LayoutInflater inflater = LayoutInflater.from(context);
		         row = inflater.inflate(R.layout.history_row, null);
		         row.setTag( cursor.getPosition() );
					
		         TextView beername = (TextView) row.findViewById(R.id.beername);
		         index = cursor.getColumnIndex("beername");
		         beername.setText(cursor.getString(index));
		         
		         TextView details = (TextView) row.findViewById(R.id.details);
		         index = cursor.getColumnIndex("details");
		         details.setText(cursor.getString(index));
	
		         final RatingBar smallRatingBar = (RatingBar) row.findViewById(R.id.smallRating);
		         RatingBar ratingBar = (RatingBar) row.findViewById(R.id.rating);
		         if (ratingBar != null && smallRatingBar != null) {
			         index = cursor.getColumnIndex("rating");
			         ratingBar.setRating( cursor.getFloat(index) );
			         smallRatingBar.setRating( cursor.getFloat(index) );
			         ratingBar.setTag(position);
			         
			         ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
						public void onRatingChanged(RatingBar ratingBar,
								float rating, boolean fromUser) {
							Integer position = (Integer) ratingBar.getTag();
							cursor.moveToPosition(position);
							int index;
	
							index = cursor.getColumnIndex("_id");
							long id = cursor.getLong(index);
	
							Integer intRating = ((Float) rating).intValue();
	
							BeerDbHelper bds = new BeerDbHelper(context);
							bds.setBeerRating(id, intRating);
	
							smallRatingBar.setRating(rating);
							
							SimpleCursorAdapter listAdapter = (SimpleCursorAdapter) historyList.getAdapter();
							listAdapter.getCursor().requery();
						}
					});
		         }
		         
		         row.setOnClickListener( clickListener );
		         row.setOnCreateContextMenuListener( contextMenuListener );
	    	 }
	    	 
	         return row;
	     } 
	     
	     @Override
	     public int getCount() {
	    	 return super.getCount() + 1;
	     }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	MenuItem sortItem = menu.findItem(R.id.optionsmenu_history_sort);
    	
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getString("sortBy", "date").equals("alpha")) {
        	sortItem.setTitle(R.string.optionsmenu_history_sort_date);
        } else {
        	sortItem.setTitle(R.string.optionsmenu_history_sort_alpha);
        }
        
		return super.onPrepareOptionsMenu(menu);
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.optionsmenu_export:
	    	final Intent next = new Intent(this, ImportExport.class);
	    	startActivity(next);
		    return true;
	    case R.id.optionsmenu_history_sort:
	        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        	Editor prefsEditor = settings.edit();
	        if (settings.getString("sortBy", "date").equals("alpha")) {
	        	prefsEditor.putString("sortBy", "date");
	        	prefsEditor.commit();
	        } else {
	        	prefsEditor.putString("sortBy", "alpha");
	        	prefsEditor.commit();
	        }

	        // Resort
	    	initBeerList();
	    	
	    	return true;
	    }

		return super.onOptionsItemSelected(item);
	}

    private void doSearchWithIntent(Intent queryIntent) {
    	final String queryString = queryIntent.getStringExtra(SearchManager.QUERY);
    	
    	if (historyList == null)
    		historyList = (ListView)findViewById(R.id.history_list);
        
    	if (dbs == null)
    		dbs = new BeerDbHelper(this);
    	
        recentBeers = dbs.searchBeerHistory(queryString);
    	
        // Map Cursor columns to views defined in simple_list_item_2.xml
        historyAdapter = new HistoryCursorAdapter(this,
                R.layout.history_row, recentBeers, 
                new String[] { "beername", "details" }, 
                new int[] { R.id.beername, R.id.details });

        historyList.setAdapter(historyAdapter);
	}

	
}

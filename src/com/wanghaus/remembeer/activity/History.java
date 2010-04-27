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
import com.wanghaus.remembeer.model.Beer;

public class History extends BaseActivity {
	private static final int BEERINFO_DIALOG_ID = 0;
	private static final int DRINK_ANOTHER = 1;
	private static final int DELETE = 2;	

	private ListView historyList;
	private Cursor recentDrinks;
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
		
		final Context context = this;
        clickListener = new View.OnClickListener() {
        	public void onClick(View itemView) {
        		Integer position = (Integer)itemView.getTag();
                int drinkId = Integer.valueOf(getDrinkValue(position, "_id"));
                Log.i("History", "Passing drinkId = " + drinkId + " to BeerInfo");
                
		    	Intent beerInfoPopupIntent = new Intent(context, BeerInfo.class);
	    		beerInfoPopupIntent.putExtra("drinkId", drinkId);
		    	startActivityForResult(beerInfoPopupIntent, BEERINFO_DIALOG_ID);
			}
        };
         
        /* Add Context menu listener to the ListView. */
        final Activity topThis = this;
        contextMenuListener = new View.OnCreateContextMenuListener() {
             public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                 Integer position = (Integer)v.getTag();
                 String beername = getDrinkValue(position, "beername");
                 menu.setHeaderTitle(beername);

                 // Drink another
                 if (beername != null) {
                	 MenuItem item = menu.add(0, DRINK_ANOTHER, 0, getString(R.string.drink_another));
          			 Intent nextIntent = new Intent(getBaseContext(), AddBeer.class);
          			 nextIntent.putExtra("beername", beername);
          			 nextIntent.putExtra("container", getDrinkValue(position, "container"));
           			 item.setIntent(nextIntent);
                 }
                 
                 // Delete
                 final long drinkId = Long.valueOf(getDrinkValue(position, "_id"));
            	 MenuItem item = menu.add(0, DELETE, 0, getString(R.string.history_delete));
            	 item.setOnMenuItemClickListener( new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						new AlertDialog.Builder(topThis).setTitle(getString(R.string.history_delete_title))
						.setMessage(getString(R.string.history_delete_warn))
						.setPositiveButton((CharSequence) getString(R.string.history_delete),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0, int arg1) {
										dbs.deleteDrink(drinkId);
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
        	recentDrinks = dbs.getDrinkHistoryAlphabetically();
        } else {
        	recentDrinks = dbs.getDrinkHistory();
        }
        
        // Map Cursor columns to views defined in simple_list_item_2.xml
        historyAdapter = new HistoryCursorAdapter(this,
                R.layout.history_row, recentDrinks, 
                new String[] { "beername", "details" }, 
                new int[] { R.id.beername, R.id.details });

        historyList.setAdapter(historyAdapter);
    }
    
    private String getDrinkValue(Integer position, String colName) {
		try {
			recentDrinks.moveToPosition(position);
			int column = recentDrinks.getColumnIndexOrThrow(colName);
			String rv = recentDrinks.getString(column);
			
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
						Intent nextIntent = new Intent(getBaseContext(),
								AddBeer.class);
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
				row.setTag(cursor.getPosition());

				TextView beername = (TextView) row.findViewById(R.id.beername);
				index = cursor.getColumnIndex("beername");
				beername.setText(cursor.getString(index));

				TextView details = (TextView) row.findViewById(R.id.details);
				index = cursor.getColumnIndex("details");
				details.setText(cursor.getString(index));

				RatingBar smallRatingBar = (RatingBar) row.findViewById(R.id.smallRating);
				if (smallRatingBar != null) {
					index = cursor.getColumnIndex("rating");
					smallRatingBar.setRating(cursor.getFloat(index));
				}
				
				row.setOnClickListener(clickListener);
				row.setOnCreateContextMenuListener(contextMenuListener);
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
    	
        recentDrinks = dbs.searchDrinkHistory(queryString);
    	
        // Map Cursor columns to views defined in simple_list_item_2.xml
        historyAdapter = new HistoryCursorAdapter(this,
                R.layout.history_row, recentDrinks, 
                new String[] { "beername", "details" }, 
                new int[] { R.id.beername, R.id.details });

        historyList.setAdapter(historyAdapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case BEERINFO_DIALOG_ID:
			// We get a beer object back, remember it here for later
			if (resultCode == RESULT_OK && data != null) {
				Beer returnBeer = (Beer) data.getSerializableExtra("beer");
				float rating = data.getFloatExtra("rating", -1);
				int drinkId = data.getIntExtra("drinkId", -1);
				String notes = data.getStringExtra("notes");

				if (returnBeer != null) {
					// Update it
					dbs.updateOrAddBeer(returnBeer);
				}
				
				if (drinkId > 0) {
					// Save it
					if (rating > 0)
						dbs.setDrinkRating(drinkId, rating);
					if (notes != null)
						dbs.setDrinkNotes(drinkId, notes);

					// Update the list
					SimpleCursorAdapter listAdapter = (SimpleCursorAdapter) historyList.getAdapter();
					listAdapter.getCursor().requery();
				}
			}
			
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}

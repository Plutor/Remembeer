package com.wanghaus.remembeer.activity;

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
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.helper.BeerDbHelper;
import com.wanghaus.remembeer.model.Beer;
import com.wanghaus.remembeer.model.Drink;

public class History extends BaseActivity {
	private static final int BEERINFO_DIALOG_ID = 0;
	private static final int CTXMNU_DRINK_ANOTHER = 1;
	private static final int CTXMNU_DELETE = 2;	
	private static final int CTXMNU_EDIT = 3;

	private ListView historyList;
	private Cursor recentDrinks;
	private BeerDbHelper dbs;
	private ListAdapter historyAdapter;
	private boolean isSearch;
	
	private OnItemClickListener clickListener;
	
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
		
        clickListener = new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View itemView, int position, long id) {
				Log.e("onItemClickListener", "onItemClick position = " + position);
				
        		if (position == 0) {
        			if (isSearch) {
        				finish();
        			} else {
						Intent nextIntent = new Intent(getBaseContext(),
								AddBeer.class);
						startActivity(nextIntent);
        			}
        		} else {
        			position--;
	                Drink drink = getDrink(position);
	                Log.i("History", "Passing drinkId = " + drink.getId() + " to BeerInfo");
	                
			    	getBeerInfo(drink);
        		}
			}
        };
        historyList.setOnItemClickListener(clickListener);

        /* Add Context menu listener to the ListView. */
        registerForContextMenu(historyList);
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
                new String[] {}, new int[] {});

        historyList.setAdapter(historyAdapter);
    }
    
    private Drink getDrink(Integer position) {
		try {
			recentDrinks.moveToPosition(position);
			Drink drink = new Drink(recentDrinks);
			return drink;
		} catch (Exception e) {
			Log.e("getBeerValue", "Can't craete drink object", e);
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
			} else if (position == 0 && isSearch) {
				// Show the clear button
				LayoutInflater inflater = LayoutInflater.from(context);
				row = inflater.inflate(R.layout.history_clear, null);
			} else {
				// Show a real beer row
				position--;

				final Cursor cursor = getCursor();
				cursor.moveToPosition(position);

				LayoutInflater inflater = LayoutInflater.from(context);
				row = inflater.inflate(R.layout.history_row, null);
				row.setTag(cursor.getPosition());

				Drink drink = new Drink(cursor);
				Beer beer = dbs.getBeer(drink.getBeerId());
				
				TextView beername = (TextView) row.findViewById(R.id.beername);
				if (beer != null)
					beername.setText(beer.getName());

				TextView details = (TextView) row.findViewById(R.id.details);
				details.setText(drink.getContainer() + " at " + drink.getStamp());

				RatingBar smallRatingBar = (RatingBar) row.findViewById(R.id.smallRating);
				if (smallRatingBar != null) {
					smallRatingBar.setRating( drink.getRating() );
				}
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


    private void getBeerInfo(Drink drink) {
    	try {
			Intent beerInfoPopupIntent = new Intent(this, BeerInfo.class);
			beerInfoPopupIntent.putExtra("drink", drink);
			startActivityForResult(beerInfoPopupIntent, BEERINFO_DIALOG_ID);
    	} catch (Exception e) {
    		Log.e("getBeerInfo", "Unable to start BeerInfo intent" , e);
    	}
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case BEERINFO_DIALOG_ID:
			// We get a beer object back, remember it here for later
			if (resultCode == RESULT_OK && data != null) {
				Drink returnDrink = (Drink) data.getSerializableExtra("drink");
				Beer returnBeer = (Beer) data.getSerializableExtra("beer");

				if (returnBeer != null) {
					// Update it
					dbs.updateOrAddBeer(returnBeer);
				}

				if (returnDrink != null) {
					// Save the drink
					dbs.updateOrAddDrink(returnDrink);

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
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		int position = 0;
		if (menuInfo instanceof AdapterContextMenuInfo) {
			AdapterContextMenuInfo acmi = (AdapterContextMenuInfo)menuInfo;
			position = acmi.position;
		}
		
		if (position <= 0)
			return;
		position--;
		
		final Context thisActivity = this;
        final Drink drink = getDrink(position);
        final Beer beer = dbs.getBeer(drink.getBeerId());
		String beername = beer.getName();
		final int drinkId = Integer.valueOf(drink.getId());
		menu.setHeaderTitle(beername);

		if (beername != null) {
			// Edit
			MenuItem item = menu.add(0, CTXMNU_EDIT, 0, getString(R.string.history_editbeer));
			item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
						public boolean onMenuItemClick(MenuItem arg0) {
							getBeerInfo(drink);
							return true;
						}
					});

			// Drink another
			item = menu.add(0, CTXMNU_DRINK_ANOTHER, 0, getString(R.string.history_drink_another));
			Intent nextIntent = new Intent(getBaseContext(), AddBeer.class);
			nextIntent.putExtra("beername", beername);
			nextIntent.putExtra("container", drink.getContainer());
			item.setIntent(nextIntent);
		}

		// Delete
		MenuItem item = menu.add(0, CTXMNU_DELETE, 0, getString(R.string.history_delete));
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				new AlertDialog.Builder(thisActivity)
						.setTitle(getString(R.string.history_delete_title))
						.setMessage(getString(R.string.history_delete_warn))
						.setPositiveButton(
								(CharSequence) getString(R.string.history_delete),
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface arg0,
											int arg1) {
										dbs
												.deleteDrink(drinkId);
										// Update the view
										SimpleCursorAdapter listAdapter = (SimpleCursorAdapter) historyList
												.getAdapter();
										listAdapter.getCursor()
												.requery();
									}
								}).setNegativeButton(
								getString(R.string.cancel),
								null).show();
				return true;
			}
		});
	}

}

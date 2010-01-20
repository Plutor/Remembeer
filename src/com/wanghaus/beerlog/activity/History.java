package com.wanghaus.beerlog.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.wanghaus.beerlog.R;
import com.wanghaus.beerlog.service.BeerDbService;

public class History extends BaseActivity {
	static final int DRINK_ANOTHER = 1;
	static final int DELETE = 2;
	static final int DO_DELETE = 3;
	
	private ListView historyList;
	private Cursor recentBeers;
	private BeerDbService dbs;
	
	private View.OnClickListener clickListener;
	private View.OnCreateContextMenuListener contextMenuListener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        
        historyList = (ListView)findViewById(R.id.history_list);
        
        // Get the last ten beers
        dbs = new BeerDbService(this);
        recentBeers = dbs.getBeerHistory();
        
        // Map Cursor columns to views defined in simple_list_item_2.xml
        ListAdapter historyAdapter = new HistoryCursorAdapter(this,
                R.layout.history_row, recentBeers, 
                new String[] { "beername", "details" }, 
                new int[] { R.id.beername, R.id.details });

        historyList.setAdapter(historyAdapter);
        
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
                	 MenuItem item = menu.add(0, DRINK_ANOTHER, 0, "Drink another " + beername);
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
						new AlertDialog.Builder(topThis).setTitle("Delete this beer?")
						.setMessage("This action cannot be undone.")
						.setPositiveButton((CharSequence) "Delete",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0, int arg1) {
										dbs.deleteBeer(beerId);
										// Update the view
										SimpleCursorAdapter listAdapter = (SimpleCursorAdapter) historyList
												.getAdapter();
										listAdapter.getCursor().requery();
									}
								}).setNegativeButton("Cancel", null).show();

						return true; /* true means: "we handled the event". */
					}
            	 });
             }
        };
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
	    	 
	    	 if (position == 0) {
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
	
							BeerDbService bds = new BeerDbService(context);
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

    public void exportHistory() {
    	// Build a csv
    	Uri csvFile = dbs.exportHistoryToCsvFile();
    	if (csvFile == null) {
    		// TODO - Show an error message
    		return;
    	}
    	
    	// Create an email
    	final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Beer Log export");
    	
    	// Attach the CSV
    	emailIntent.setType("plain/csv");
    	emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, csvFile);
    	
    	// Send
    	startActivity(Intent.createChooser(emailIntent, "Send export..."));
    }
}

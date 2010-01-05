package com.wanghaus.beerlog.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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
        		smallRatingView.setVisibility( isVisible ? View.VISIBLE : View.GONE );
			}
        };
         
        /* Add Context menu listener to the ListView. */
        contextMenuListener = new View.OnCreateContextMenuListener() {
             public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                 menu.setHeaderTitle("ContextMenu");

                 Integer position = (Integer)v.getTag();
                 String beername = getBeerValue(position, "beername");
                 if (beername != null) {
                	 MenuItem item = menu.add(0, DRINK_ANOTHER, 0, "Drink another " + beername);
          			 Intent nextIntent = new Intent(getBaseContext(), Main.class);
          			 nextIntent.putExtra("selectedTab", "addbeer");
          			 nextIntent.putExtra("beername", beername);
          			 nextIntent.putExtra("container", getBeerValue(position, "container"));
           			 item.setIntent(nextIntent);
                 }
                 
                 menu.add(0, DELETE, 0, "Delete");
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
    
    @Override
    public boolean onContextItemSelected(MenuItem aItem) {
         //AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem.getMenuInfo();
        //final long beerId = menuInfo.id;
        final long beerId = 0;

         /* Switch on the ID of the item, to get what the user selected. */
         switch (aItem.getItemId()) {
         case DELETE:
    	     new AlertDialog.Builder(this)
    	       .setTitle("Delete this beer?")
    	       .setMessage("This action cannot be undone.")
    	       .setPositiveButton((CharSequence)"Delete", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						dbs.deleteBeer(beerId);
						// Update the view
						SimpleCursorAdapter listAdapter = (SimpleCursorAdapter) historyList.getAdapter();
						listAdapter.getCursor().requery();
					}
    	        })  
    	        .setNegativeButton("Cancel", null)
    	        .show();

             return true; /* true means: "we handled the event". */
         }
         return false;
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
	         // Cursor to current item
	         final Cursor cursor = getCursor();
	         cursor.moveToPosition(position);
	         int index;
	         
	         LayoutInflater inflater = LayoutInflater.from(context);
	         View row = inflater.inflate(R.layout.history_row, null);
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
		         
		         ratingBar.setOnRatingBarChangeListener( new RatingBar.OnRatingBarChangeListener() {
		        	 public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
		        		 Integer position = (Integer) ratingBar.getTag();
		        		 cursor.moveToPosition(position);
		        		 int index;
		        		 
		        		 index = cursor.getColumnIndex("_id");
		        		 long id = cursor.getLong(index);
		        		 
		        		 Integer intRating = ((Float)rating).intValue();
		        		 
		        		 BeerDbService bds = new BeerDbService(context);
		        		 bds.setBeerRating(id, intRating);
		        		 
				         smallRatingBar.setRating( rating );
					}
		         });
	         }
	         
	         row.setOnClickListener( clickListener );
	         row.setOnCreateContextMenuListener( contextMenuListener );
	         return row;
	     } 
    }
}

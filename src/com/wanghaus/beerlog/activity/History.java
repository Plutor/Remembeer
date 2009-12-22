package com.wanghaus.beerlog.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.wanghaus.beerlog.R;

public class History extends BaseActivity {
	static final int DRINK_ANOTHER = 1;
	static final int DELETE = 2;
	static final int DO_DELETE = 3;
	
	private ListView historyList;
	private Cursor lastTenBeers;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        
        historyList = (ListView)findViewById(R.id.history_list);
        
        // Get the last ten beers
        lastTenBeers = db.query(DB_TABLE,
        		new String[] {"ROWID AS _id", "beername", "container || ' at ' || stamp AS details", "container"},
        		null, null, null, null, "stamp DESC", "10");
        
        // Map Cursor columns to views defined in simple_list_item_2.xml
        ListAdapter historyAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, lastTenBeers, 
                        new String[] { "beername", "details" }, 
                        new int[] { android.R.id.text1, android.R.id.text2 });

        historyList.setAdapter(historyAdapter);
        
        /* Add Context menu listener to the ListView. */
        historyList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
             public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                 menu.setHeaderTitle("ContextMenu");

                 String beername = getBeerValue(menuInfo, "beername");
                 if (beername != null)
                	 menu.add(0, DRINK_ANOTHER, 0, "Drink another " + beername);
            	 
                 menu.add(0, DELETE, 0, "Delete");
             }
       });
    }
    
    private String getBeerValue(ContextMenuInfo menuInfo, String colName) {
		try {
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
			lastTenBeers.moveToPosition(info.position);
			int column = lastTenBeers.getColumnIndexOrThrow(colName);
			String rv = lastTenBeers.getString(column);
			
			return rv;
		} catch (Exception e) {
			Log.e("getBeerValue", "Can't determine beer " + colName, e);
		}
		return null;
	}
    
    @Override
    public boolean onContextItemSelected(MenuItem aItem) {
         AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem.getMenuInfo();
         final long beerId = menuInfo.id;

         /* Switch on the ID of the item, to get what the user selected. */
         switch (aItem.getItemId()) {
         case DRINK_ANOTHER:
			Intent nextIntent = new Intent(this, Main.class);
			nextIntent.putExtra("selectedTab", "addbeer");
			nextIntent.putExtra("beername", getBeerValue(menuInfo, "beername"));
			nextIntent.putExtra("container", getBeerValue(menuInfo, "container"));
			startActivity(nextIntent);

			return true; /* true means: "we handled the event". */
         case DELETE:
    	     new AlertDialog.Builder(this)
    	       .setTitle("Delete this beer?")
    	       .setMessage("This action cannot be undone.")
    	       .setPositiveButton((CharSequence)"Delete", new OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						db.execSQL("DELETE FROM " + DB_TABLE + " WHERE ROWID = " + String.valueOf(beerId));
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
}

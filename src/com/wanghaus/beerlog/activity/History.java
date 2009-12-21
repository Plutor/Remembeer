package com.wanghaus.beerlog.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.wanghaus.beerlog.R;

public class History extends BaseActivity {
	static final int SHOW_DETAILS = 1;
	static final int DELETE = 2;
	static final int DO_DELETE = 3;
	
	ListView historyList;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        
        historyList = (ListView)findViewById(R.id.history_list);
        
        // Get the last ten beers
        Cursor lastTenBeers = db.query(DB_TABLE,
        		new String[] {"ROWID AS _id", "beername", "container || ' at ' || stamp AS details"},
        		null, null, null, null, "stamp DESC", "10");
        
        // Map Cursor columns to views defined in simple_list_item_2.xml
        ListAdapter historyAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, lastTenBeers, 
                        new String[] { "beername", "details" }, 
                        new int[] { android.R.id.text1, android.R.id.text2 });

        historyList.setAdapter(historyAdapter);
        
        /* Add Context menu listener to the ListView. */
        historyList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
             public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {  
                  menu.setHeaderTitle("ContextMenu");
                  menu.add(0, SHOW_DETAILS, 0, "Details");
                  menu.add(0, DELETE, 0, "Delete");
             }
       });
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem aItem) {
         AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem.getMenuInfo();
         final long beerId = menuInfo.id;

         /* Switch on the ID of the item, to get what the user selected. */
         switch (aItem.getItemId()) {
         case SHOW_DETAILS:
             // TODO
             
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

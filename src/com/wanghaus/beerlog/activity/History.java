package com.wanghaus.beerlog.activity;

import android.database.Cursor;
import android.os.Bundle;
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
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        
        // Get the last ten beers
        Cursor lastTenBeers = db.query(DB_TABLE,
        		new String[] {"ROWID AS _id", "beername", "container || ' at ' || stamp AS details"},
        		null, null, null, null, "stamp DESC", "10");
        
        ListView list = (ListView)findViewById(R.id.history_list);
        
        // Map Cursor columns to views defined in simple_list_item_2.xml
        ListAdapter historyAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, lastTenBeers, 
                        new String[] { "beername", "details" }, 
                        new int[] { android.R.id.text1, android.R.id.text2 });

        list.setAdapter(historyAdapter);
        
        /* Add Context menu listener to the ListView. */
        list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
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

         /* Switch on the ID of the item, to get what the user selected. */
         switch (aItem.getItemId()) {
         case SHOW_DETAILS:
             // TODO
             
             return true; /* true means: "we handled the event". */
         case DELETE:
             // TODO
             
             return true; /* true means: "we handled the event". */
         }
         return false;
    } 
}

package com.wanghaus.beerlog.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.wanghaus.beerlog.R;

public class History extends BaseActivity {
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
        ListAdapter historyCursor = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, lastTenBeers, 
                        new String[] { "beername", "details" }, 
                        new int[] { android.R.id.text1, android.R.id.text2 });

        list.setAdapter(historyCursor);
    }
}

package com.wanghaus.beerlog.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wanghaus.beerlog.R;

public class AddBeerDone extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.addbeerdone);
        
        // Change title bar
        setTitle("Beer added");
        
        // Put in stats
        String beercount = "???";
        try {
        	Cursor beercountQuery = db.query(DB_TABLE, new String[] {"ROWID"}, null, null, null, null, null);
        	beercount = String.valueOf(beercountQuery.getCount());
        } catch (Exception e) {
        	// TODO - Hm
        }
        
        TextView before = (TextView) findViewById(R.id.addbeerdone_before);
        before.setText("You have drunk");
        TextView number = (TextView) findViewById(R.id.addbeerdone_number);
        number.setText(beercount);
        TextView after = (TextView) findViewById(R.id.addbeerdone_after);
        after.setText("beers this year");
        
        // Save button
        Button moreStatsButton = (Button) findViewById(R.id.morestats);
        
        moreStatsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	viewMoreStats();
            }
        });
        
        // Save button
        Button addAnotherButton = (Button) findViewById(R.id.addanother);
        
        addAnotherButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	viewAddAnother();
            }
        });
    }

    private void viewMoreStats() {
    	Intent nextIntent = new Intent(this, Main.class);
    	nextIntent.putExtra("selectedTab", "stats");
    	startActivity(nextIntent);
    	finish();
    }
    
    private void viewAddAnother() {
    	Intent nextIntent = new Intent(this, Main.class); 	
    	nextIntent.putExtra("selectedTab", "addbeer");
    	startActivity(nextIntent);
    	finish();
    }
}

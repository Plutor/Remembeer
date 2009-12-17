package com.wanghaus.beerlog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddBeerDone extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.addbeerdone);
        
        // Change title bar
        setTitle("Beer added");
        
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
    	nextIntent.putExtra("selectedTab", "tab2");
    	startActivity(nextIntent);
    	finish();
    }
    
    private void viewAddAnother() {
    	Intent nextIntent = new Intent(this, Main.class); 	
    	nextIntent.putExtra("selectedTab", "tab1");
    	startActivity(nextIntent);
    	finish();
    }
}

package com.wanghaus.remembeer.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.service.BeerDbService;

public class AddBeerDone extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.addbeerdone);
        
        // Change title bar
        setTitle( getText(R.string.addbeerdone_title) );
        
        // Put in stats
    	List<CharSequence> popupParts = new ArrayList<CharSequence>();
    	popupParts.add(null); popupParts.add(null); popupParts.add(null); // XXX - Because constructor(int) isn't working
    	
        try {
        	BeerDbService dbs = new BeerDbService(this);
        	Random prng = new Random();
        	int statType = prng.nextInt(4);
        	long count = 0;
        	
        	switch (statType) {
        	case 0:
        		count = dbs.getBeerCountThisMonth();
        		if (count <= 2)
        			popupParts.set(2, getText(R.string.addbeerdone_suffix_thisMonth_small));
        		else
        			popupParts.set(2, getText(R.string.addbeerdone_suffix_thisMonth_large));
        		break;
        	case 1:
        		count = dbs.getBeerCountLastDays(7);
        		if (count <= 2)
        			popupParts.set(2, getText(R.string.addbeerdone_suffix_last7days_small));
        		else
        			popupParts.set(2, getText(R.string.addbeerdone_suffix_last7days_large));
        		break;
        	case 2:
        		count = dbs.getBeerTypesCount();
        		if (count <= 2)
        			popupParts.set(2, getText(R.string.addbeerdone_suffix_beerTypes_small));
        		else
        			popupParts.set(2, getText(R.string.addbeerdone_suffix_beerTypes_large));
        		break;
        	case 3:
        	default:
	    		count = dbs.getBeerCountThisYear();
        		if (count <= 2)
        			popupParts.set(2, getText(R.string.addbeerdone_suffix_thisYear_small));
        		else
        			popupParts.set(2, getText(R.string.addbeerdone_suffix_thisYear_large));
	    		break;
        	}
        	
        	if (count == 1) {
        		popupParts.set(0, getText(R.string.addbeerdone_prefix_small));
        		popupParts.set(1, getText(R.string.addbeerdone_num_eq1));
        	} else if (count == 2) {
        		popupParts.set(0, getText(R.string.addbeerdone_prefix_small));
        		popupParts.set(1, getText(R.string.addbeerdone_num_eq2));
        	} else if (count > 2) {
        		popupParts.set(0, getText(R.string.addbeerdone_prefix_large));
        		popupParts.set(1, String.valueOf(count) );
        	} else {
        		 // TODO - Hm
            	Log.e("AddBeerDone", "The count is zero for some reason");
        	}
        } catch (Exception e) {
        	Log.e("AddBeerDone", "Threw exception trying to calculate a stat", e);
        }
        
        TextView before = (TextView) findViewById(R.id.addbeerdone_before);
        before.setText(popupParts.get(0));
        TextView number = (TextView) findViewById(R.id.addbeerdone_number);
        number.setText(popupParts.get(1));
        TextView after = (TextView) findViewById(R.id.addbeerdone_after);
        after.setText(popupParts.get(2));
        
        // Close button
        Button okButton = (Button) findViewById(R.id.okbutton);
        
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	closeAddBeerDone();
            }
        });
    }
    
    private void closeAddBeerDone() {
    	finish();
    }
}
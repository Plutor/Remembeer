package com.wanghaus.beerlog;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

public class BeerLog extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Beer name autocomplete text field
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.beername);
        ArrayAdapter beerNameAdapter = ArrayAdapter.createFromResource(this,
                R.array.beernames, android.R.layout.simple_dropdown_item_1line);
        textView.setAdapter(beerNameAdapter);
     
        // Containers dropdown
        Spinner containerSpinner = (Spinner) findViewById(R.id.container);
        ArrayAdapter containerAdapter = ArrayAdapter.createFromResource(this,
                R.array.containers, android.R.layout.simple_spinner_item);
        containerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        containerSpinner.setAdapter(containerAdapter);
        
        //CompoundButton button1 = (CompoundButton) findViewById(R.id.drink_now);
        //CompoundButton button2 = (CompoundButton) findViewById(R.id.drink_tenminago);
        //CompoundButton button3 = (CompoundButton) findViewById(R.id.drink_specifytime);
        
    }
}
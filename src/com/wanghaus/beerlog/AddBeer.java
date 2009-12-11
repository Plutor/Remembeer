package com.wanghaus.beerlog;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemSelectedListener;

public class AddBeer extends Activity {
	private static final int DATE_DIALOG_ID = 0;
	private static final int TIME_DIALOG_ID = 1;
	
	private Spinner drinkWhenSpinner;
	private Calendar specificDate = Calendar.getInstance();

	private DatePickerDialog.OnDateSetListener dateSetListener =
		new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
				specificDate.set(Calendar.YEAR, arg1);
				specificDate.set(Calendar.MONTH, arg2);
				specificDate.set(Calendar.DAY_OF_MONTH, arg3);
				showDialog(TIME_DIALOG_ID);
			}
	};
	private TimePickerDialog.OnTimeSetListener timeSetListener =
	    new TimePickerDialog.OnTimeSetListener() {
			@SuppressWarnings("unchecked")
			public void onTimeSet(TimePicker view, int hourOfDay, int minuteArg) {
	        	specificDate.set(Calendar.HOUR, hourOfDay);
	        	specificDate.set(Calendar.MINUTE, minuteArg);
	            
	            // Add to the drinkWhenSpinner dropdown and select it
	        	ArrayAdapter<CharSequence> drinkWhenAdapter = (ArrayAdapter<CharSequence>) drinkWhenSpinner.getAdapter();
	        	while (drinkWhenAdapter.getCount() > 4)
	        		drinkWhenAdapter.remove( drinkWhenAdapter.getItem(4) );
	        	
	        	drinkWhenAdapter.add( DateFormat.getDateTimeInstance().format(specificDate.getTime()) );
	        	drinkWhenSpinner.setSelection( drinkWhenAdapter.getCount() - 1 );
	        }
	    };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.addbeer);
        
        // Beer name autocomplete text field
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.beername);
        ArrayAdapter<CharSequence> beerNameAdapter = ArrayAdapter.createFromResource(this,
                R.array.beernames, android.R.layout.simple_dropdown_item_1line);
        textView.setAdapter(beerNameAdapter);
     
        // Containers dropdown
        Spinner containerSpinner = (Spinner) findViewById(R.id.container);
        ArrayAdapter<CharSequence> containerAdapter = ArrayAdapter.createFromResource(this,
                R.array.containers, android.R.layout.simple_spinner_item);
        containerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        containerSpinner.setAdapter(containerAdapter);
        
        // Initialize the specificDate
        specificDate.setTime( new Date() );
        
        // Drinkwhen dropdown
        drinkWhenSpinner = (Spinner) findViewById(R.id.drinkwhen);
    	ArrayAdapter<CharSequence> drinkWhenAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        drinkWhenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drinkWhenSpinner.setAdapter(drinkWhenAdapter);
        
        // Add all the things from R.array.drinkwhens
        String[] drinkWhens = getResources().getStringArray(R.array.drinkwhens);
        for (String s : drinkWhens) {
        	drinkWhenAdapter.add(s);
        }
        
        drinkWhenSpinner.setOnItemSelectedListener( new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int selected, long id) {
				if (selected == 3) {
					// Show the date time popup
					showDialog(DATE_DIALOG_ID);
				} else if (selected < 3) {
					removeSpecificTime();
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				removeSpecificTime(); 
			}
			
			@SuppressWarnings("unchecked")
			public void removeSpecificTime() {
				// Remove the 4th option if it exists
	        	ArrayAdapter<CharSequence> drinkWhenAdapter = (ArrayAdapter<CharSequence>) drinkWhenSpinner.getAdapter();
	        	while (drinkWhenAdapter.getCount() > 4)
	        		drinkWhenAdapter.remove( drinkWhenAdapter.getItem(4) );
			}
        });
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
	        case DATE_DIALOG_ID:
	            return new DatePickerDialog(this,
                    dateSetListener,
                    specificDate.get(Calendar.YEAR),
                    specificDate.get(Calendar.MONTH),
                    specificDate.get(Calendar.DAY_OF_MONTH)
                );
	        case TIME_DIALOG_ID:
	            return new TimePickerDialog(this,
                    timeSetListener,
                    specificDate.get(Calendar.HOUR),
                    specificDate.get(Calendar.MINUTE),
                    false
                );
        }
        return null;
    }
}
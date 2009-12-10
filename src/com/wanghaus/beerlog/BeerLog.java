package com.wanghaus.beerlog;

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

public class BeerLog extends Activity {
	private static final int DATE_DIALOG_ID = 0;
	private static final int TIME_DIALOG_ID = 1;
	
	private Spinner drinkWhenSpinner;
	private int year;
	private int month;
	private int date;
	private int hour;
	private int minute;

	private DatePickerDialog.OnDateSetListener dateSetListener =
		new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
				year = arg1;
				month = arg2;
				date = arg3;
				showDialog(TIME_DIALOG_ID);
			}
	};
	private TimePickerDialog.OnTimeSetListener timeSetListener =
	    new TimePickerDialog.OnTimeSetListener() {
	        public void onTimeSet(TimePicker view, int hourOfDay, int minuteArg) {
	            hour = hourOfDay;
	            minute = minuteArg;
	            
	            // TODO - Add to the drinkWhenSpinner dropdown
	        }
	    };
	
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
        
        // Drinkwhen dropdown
        drinkWhenSpinner = (Spinner) findViewById(R.id.drinkwhen);
        ArrayAdapter drinkWhenAdapter = ArrayAdapter.createFromResource(this,
                R.array.drinkwhens, android.R.layout.simple_spinner_item);
        drinkWhenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drinkWhenSpinner.setAdapter(drinkWhenAdapter);
        drinkWhenSpinner.setOnItemSelectedListener( new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int selected, long id) {
				if (selected == 3) {
					// Show the date time popup
					showDialog(DATE_DIALOG_ID);
				} else {
					removeSpecificTime();
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				removeSpecificTime(); 
			}
			
			public void removeSpecificTime() {
				// TODO - Remove the 4th option if it exists
			}
        });
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
	        case DATE_DIALOG_ID:
	            return new DatePickerDialog(this,
	                    dateSetListener, year, month, date);
	        case TIME_DIALOG_ID:
	            return new TimePickerDialog(this,
	                    timeSetListener, hour, minute, false);
        }
        return null;
    }
}
package com.wanghaus.beerlog.activity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemSelectedListener;

import com.wanghaus.beerlog.R;
import com.wanghaus.beerlog.service.BeerDbService;

public class AddBeer extends BaseActivity {
	private static final int DATE_DIALOG_ID = 0;
	private static final int TIME_DIALOG_ID = 1;
	
	private Spinner drinkWhenSpinner;
	private Calendar specificTime = Calendar.getInstance();
	private BeerDbService dbs;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.addbeer);
        
        dbs = new BeerDbService(this);

        initBeernameAutoComplete();
        initContainerSpinner();
        initDrinkWhenSpinner();
        
        // Save button
        Button saveButton = (Button) findViewById(R.id.save);
        
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	saveBeer();
            }
        });
    }
    
    private void initBeernameAutoComplete() {
        // Beer name autocomplete text field
        AutoCompleteTextView beernameView = (AutoCompleteTextView) findViewById(R.id.beername);
        
        Cursor cursor = dbs.getBeerNames();
        
        BeerNameAutocompleteAdapter list = new BeerNameAutocompleteAdapter(this, cursor);
        beernameView.setAdapter(list); 
        
        // See if we were passed a beer name
        try {
        	String beername = getIntent().getStringExtra("beername");
            if (beername != null)
            	beernameView.setText(beername);
        } catch (Exception e) {
        	// Do nothing
        }
    }

    private class BeerNameAutocompleteAdapter extends CursorAdapter {
            public BeerNameAutocompleteAdapter(Context context, Cursor c) {
                    super(context, c);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                    int columnIndex = cursor.getColumnIndexOrThrow("beername");
                    ((TextView) view).setText(cursor.getString(columnIndex));
            }

            @Override
            public String convertToString(Cursor cursor) {
                    int columnIndex = cursor.getColumnIndexOrThrow("beername");
                    return cursor.getString(columnIndex);
            }

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                    final LayoutInflater inflater = LayoutInflater.from(context);
                    final TextView view = (TextView) inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
                    int columnIndex = cursor.getColumnIndexOrThrow("beername");
                    view.setText(cursor.getString(columnIndex));
                    return view;
            }

            @Override
            public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            	if (constraint == null)
            		return dbs.getBeerNames();

                return dbs.getBeerNames( constraint.toString() );
            }
    } 

    private void initContainerSpinner() {
        // Containers dropdown
        Spinner containerSpinner = (Spinner) findViewById(R.id.container);
        ArrayAdapter<CharSequence> containerAdapter = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item, dbs.getContainers());
        containerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        containerSpinner.setAdapter(containerAdapter);
        
        // See if we were passed a container
        try {
        	String container = getIntent().getStringExtra("container");
            if (container != null)
            	for (int i=0; i<containerSpinner.getCount(); ++i) {
            		String thisContainer = containerSpinner.getItemAtPosition(i).toString();
            		if (thisContainer.equals(container)) {
            			containerSpinner.setSelection(i);
            			break;
            		}
            	}
        } catch (Exception e) {
        	// Do nothing
        	Log.e("getcontainer", "Can't figure out container from intent extras", e);
        }
    }

    private void initDrinkWhenSpinner() {
        // Initialize the specificDate
        specificTime.setTime( new Date() );
        
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
				switch (selected) {
				case 0: // now
					specificTime.setTime( new Date(0) );
					removeSpecificTime();
					break;
				case 1: // ten minutes ago
					specificTime.setTime( new Date(1) );
					removeSpecificTime();
					break;
				case 2: // last night (lets say 9pm)
					specificTime.setTime( new Date() );
					specificTime.add( Calendar.DATE, -1 );
					specificTime.set( Calendar.HOUR_OF_DAY, 21 );
					specificTime.set( Calendar.MINUTE, 0 );
					specificTime.set( Calendar.SECOND, 0 );
					removeSpecificTime();
					break;
				case 3:
					showDialog(DATE_DIALOG_ID);
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
    
	private DatePickerDialog.OnDateSetListener dateSetListener =
		new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
				specificTime.set(Calendar.YEAR, arg1);
				specificTime.set(Calendar.MONTH, arg2);
				specificTime.set(Calendar.DAY_OF_MONTH, arg3);
				showDialog(TIME_DIALOG_ID);
			}
	};
	private TimePickerDialog.OnTimeSetListener timeSetListener =
	    new TimePickerDialog.OnTimeSetListener() {
			@SuppressWarnings("unchecked")
			public void onTimeSet(TimePicker view, int hourOfDay, int minuteArg) {
	        	specificTime.set(Calendar.HOUR, hourOfDay);
	        	specificTime.set(Calendar.MINUTE, minuteArg);
	            
	            // Add to the drinkWhenSpinner dropdown and select it
	        	ArrayAdapter<CharSequence> drinkWhenAdapter = (ArrayAdapter<CharSequence>) drinkWhenSpinner.getAdapter();
	        	while (drinkWhenAdapter.getCount() > 4)
	        		drinkWhenAdapter.remove( drinkWhenAdapter.getItem(4) );
	        	
	        	drinkWhenAdapter.add( DateFormat.getDateTimeInstance().format(specificTime.getTime()) );
	        	drinkWhenSpinner.setSelection( drinkWhenAdapter.getCount() - 1 );
	        }
	    };
	    
	private DialogInterface.OnCancelListener dialogCancelListener =
		new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				drinkWhenSpinner.setSelection(0);
			}
		};
	
    private void saveBeer() {
    	Intent nextIntent = new Intent(this, AddBeerDone.class);

    	// Save
    	if (dbs != null) {
            TextView beernameView = (TextView) findViewById(R.id.beername);
            String beername = beernameView.getText().toString();
    		
            Spinner containerSpinner = (Spinner) findViewById(R.id.container);
            String container = containerSpinner.getSelectedItem().toString();
            
            switch ((int)specificTime.getTimeInMillis()) {
            case 0:
            	specificTime.setTime( new Date());
                break;
            case 1:
            	specificTime.setTime( new Date());
            	specificTime.add( Calendar.MINUTE, -10 );
            	break;
            }
            
            dbs.addBeer(beername, container, specificTime.getTime());
    	} else {
    		// TODO - throw an error?
    	}
    	
    	startActivity(nextIntent);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog = null;
    	
        switch (id) {
	        case DATE_DIALOG_ID:
	            dialog = new DatePickerDialog(this,
                    dateSetListener,
                    specificTime.get(Calendar.YEAR),
                    specificTime.get(Calendar.MONTH),
                    specificTime.get(Calendar.DAY_OF_MONTH)
                );
	            break;
	        case TIME_DIALOG_ID:
	        	dialog = new TimePickerDialog(this,
                    timeSetListener,
                    specificTime.get(Calendar.HOUR),
                    specificTime.get(Calendar.MINUTE),
                    false
                );
	        }
        
        if (dialog != null) {
        	dialog.setOnCancelListener(dialogCancelListener);
        	return dialog;
        }
        
        return null;
    }
}

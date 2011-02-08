package com.wanghaus.remembeer.activity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemSelectedListener;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.app.DatePickerCancellableDialog;
import com.wanghaus.remembeer.app.TimePickerCancellableDialog;
import com.wanghaus.remembeer.helper.BeerDbHelper;
import com.wanghaus.remembeer.helper.TwitterHelper;
import com.wanghaus.remembeer.helper.WebServiceHelper;
import com.wanghaus.remembeer.model.Beer;
import com.wanghaus.remembeer.model.Drink;
import com.wanghaus.remembeer.service.NotifyService;
import com.wanghaus.remembeer.widget.BeerSearchView;

public class AddBeer extends BaseActivity {
	private static final int DATE_DIALOG_ID = 0;
	private static final int TIME_DIALOG_ID = 1;
	private static final int BEERINFO_DIALOG_ID = 2;

	private BeerSearchView beerSearch;
	private Spinner drinkWhenSpinner;
	private Calendar specificTime = Calendar.getInstance();
	private BeerDbHelper dbs;	
	private WebServiceHelper wsh;
	private Button saveButton = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.addbeer);
        
        dbs = new BeerDbHelper(this);
        wsh = new WebServiceHelper(this);
        
        initSaveButton();
        initBeerSearch();
        initContainerSpinner();
        initDrinkWhenSpinner();

        // For rotating
        if (savedInstanceState != null) {
        	Beer beerSaved = (Beer) savedInstanceState.getSerializable("beer");
        	beerSearch.setBeer(beerSaved);
        }
        
    	// If webservice setting is unset, ask
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (!settings.contains("useWebService")) {
			Intent configure = new Intent(this, ConfigureWebService.class);
			startActivity(configure);
        }
    }
    
    private void initSaveButton() {
        // Save button
        saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	saveBeer();
            }
        });
    }
    
	private void initBeerSearch() {
		beerSearch = (BeerSearchView) findViewById(R.id.beersearch);

		// Fill in the search field if we're drinking another
        Beer intentBeer = (Beer)getIntent().getSerializableExtra("beer");
        if (intentBeer != null)
        	beerSearch.setBeer(intentBeer);
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
					removeSpecificTime();
					break;
				case 1: // ten minutes ago
					removeSpecificTime();
					break;
				case 2: // last night (lets say 9pm)
					specificTime.setTime( new Date() );
					specificTime.add( Calendar.DATE, -1 );
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
				DateFormat beerOclock = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
				
	        	specificTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
	        	specificTime.set(Calendar.MINUTE, minuteArg);
	            
	            // Add to the drinkWhenSpinner dropdown and select it
	        	ArrayAdapter<CharSequence> drinkWhenAdapter = (ArrayAdapter<CharSequence>) drinkWhenSpinner.getAdapter();
	        	while (drinkWhenAdapter.getCount() > 4)
	        		drinkWhenAdapter.remove( drinkWhenAdapter.getItem(4) );
	        	
	        	drinkWhenAdapter.add( beerOclock.format(specificTime.getTime()) );
	        	drinkWhenSpinner.setSelection( drinkWhenAdapter.getCount() - 1 );
	        }
	    };
	    
	private OnClickListener dialogCancelListener =
		new OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				drinkWhenSpinner.setSelection(0);
			}
		};
	
    private void saveBeer() {
        final int drinkId;
        Beer beer = beerSearch.getBeer();
        
        if (beer == null) return;
        
    	// Save
    	if (dbs != null) {    		
            Spinner containerSpinner = (Spinner) findViewById(R.id.container);
            String container = containerSpinner.getSelectedItem().toString();
                        
            switch ((int)drinkWhenSpinner.getSelectedItemPosition()) {
            case 0:
            	specificTime.setTime( new Date());
                break;
            case 1:
            	specificTime.setTime( new Date());
            	specificTime.add( Calendar.MINUTE, -10 );
            	break;
            }

            TextView notesView = (TextView) findViewById(R.id.addbeer_notes);
            String notes = notesView.getText().toString();
            
            int beerId = dbs.updateOrAddBeer(beer);
            
            Drink newDrink = new Drink();
            newDrink.setBeerId( beerId );
            newDrink.setContainer( container );
            newDrink.setStamp( dbs.datetimeString(specificTime.getTime()) );
            newDrink.setNotes( notes );
            drinkId = (int)dbs.updateOrAddDrink(newDrink);
            
            wsh.sendWebServiceRequest(newDrink);
            
            if ((int)drinkWhenSpinner.getSelectedItemPosition() == 0) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                        
                if (settings.getBoolean("remindersEnabled", true)) {
                	try {
                		Float reminderDelay = Float.valueOf(settings.getString("remindersDelay", "5"));
                		reminderDelay *= 60000;
                		int timeout = reminderDelay.intValue();
                		
                   		Intent notifyServiceIntent = new Intent(this, NotifyService.class);
                   		notifyServiceIntent.putExtra("drinkId", drinkId);
                   		notifyServiceIntent.putExtra("timeout", timeout);
                   		
                   		startService(notifyServiceIntent);
                 	} catch (Exception e) {
                 		Log.w("remindersDelay", e);
                 	}
                }

                TwitterHelper.sendToTwitter(this, beer.getName());
            }

    	} else {
    		// TODO - throw an error?
    	}
    	
    	showNextScreen();
    }

    private void showNextScreen() {    	
    	Intent nextIntent;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String nextActivity = prefs.getString("addBeerDoneActivity", "");

    	if (nextActivity.equals("Stats"))
    		nextIntent = new Intent(this, Stats.class);
    	else if (nextActivity.equals("AddBeer"))
    		nextIntent = new Intent(this, AddBeer.class);
    	else
    		nextIntent = new Intent(this, History.class);

    	startActivity(nextIntent);

    	Intent popupIntent = new Intent(this, AddBeerDone.class);
    	startActivity(popupIntent);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog = null;
    	
        switch (id) {
	        case DATE_DIALOG_ID:
	        	DatePickerCancellableDialog d = new DatePickerCancellableDialog(this,
                    dateSetListener,
                    specificTime.get(Calendar.YEAR),
                    specificTime.get(Calendar.MONTH),
                    specificTime.get(Calendar.DAY_OF_MONTH)
                );
	        	d.setOnCancelListener(dialogCancelListener);
	        	dialog = (Dialog)d;
	            break;
	        case TIME_DIALOG_ID:
	        	TimePickerCancellableDialog t = new TimePickerCancellableDialog(this,
                    timeSetListener,
                    specificTime.get(Calendar.HOUR_OF_DAY),
                    specificTime.get(Calendar.MINUTE),
                    false
                );
	        	t.setOnCancelListener(dialogCancelListener);
	        	dialog = (Dialog)t;
	        }
        
        if (dialog != null)
        	return dialog;
        
        return null;
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case BEERINFO_DIALOG_ID:
			// We get a beer object back, remember it here for later
			if (resultCode == RESULT_OK && data != null) {
				Beer returnBeer = (Beer) data.getSerializableExtra("beer");
				if (returnBeer != null)
					beerSearch.setBeer(returnBeer);
			}
			
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putSerializable("beer", beerSearch.getBeer());
	}
}

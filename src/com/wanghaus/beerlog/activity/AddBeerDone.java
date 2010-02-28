package com.wanghaus.beerlog.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.lang.Float;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wanghaus.beerlog.R;
import com.wanghaus.beerlog.service.BeerDbService;

public class AddBeerDone extends BaseActivity {
	public final static int BEER_HISTORY_ID = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.addbeerdone);
        
        // Change title bar
        setTitle("Beer added");
        
        // Put in stats
    	List<String> popupParts = new ArrayList<String>();
    	popupParts.add(null); popupParts.add(null); popupParts.add(null); // XXX - Because constructor(int) isn't working
    	
        try {
        	BeerDbService dbs = new BeerDbService(this);
        	Random prng = new Random();
        	long count = 0;
        	
        	switch (prng.nextInt(4)) {
        	case 0:
        		count = dbs.getBeerCountThisMonth();
        		popupParts.set(2, "beers so far this month");
        		break;
        	case 1:
        		count = dbs.getBeerCountLastDays(7);
        		popupParts.set(2, "beers in the last 7 days");
        		break;
        	case 2:
        		count = dbs.getBeerTypesCount();
        		popupParts.set(2, "different types of beer");
        		break;
        	case 3:
        	default:
	    		count = dbs.getBeerCountThisYear();
	    		popupParts.set(2, "beers so far this year");
	    		break;
        	}
        	
        	if (count == 1) {
        		popupParts.set(0, "That was your");
        		popupParts.set(1, "first");
        		popupParts.set(2, popupParts.get(2).replaceAll("^beers", "beer"));
        		popupParts.set(2, popupParts.get(2).replaceAll("^types", "type"));
        	} else if (count == 2) {
        		popupParts.set(0, "That was your");
        		popupParts.set(1, "second");
        		popupParts.set(2, popupParts.get(2).replaceAll("^beers", "beer"));
        		popupParts.set(2, popupParts.get(2).replaceAll("^types", "type"));
        	} else if (count > 2) {
        		popupParts.set(0, "You have drunk");
        		popupParts.set(1, String.valueOf(count) );
        	} else {
        		 // TODO - Hm
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
        
        final Handler handler = new Handler();
        final Runnable rater = new Runnable()
        {
            public void run()
            {
            	ratingsCallback();
            }
        };

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                
        if (settings.getBoolean("remindersEnabled", true)) {
        	try {
        		Float fTimeout = new Float(settings.getString("remindersDelay", "5"));
        		fTimeout *= 60000;
        		int mTimeout = fTimeout.intValue();
        		
        		handler.postDelayed(rater, mTimeout);
         	} catch (Exception e) {
         		Log.w("remindersDelay", e);
         	}
        }
        
    }
    
    private void viewMoreStats() {
    	Intent nextIntent = new Intent(this, Stats.class);
    	startActivity(nextIntent);
    	finish();
    }
    
    private void viewAddAnother() {
    	Intent nextIntent = new Intent(this, AddBeer.class); 	
    	startActivity(nextIntent);
    	finish();
    }

    private void ratingsCallback() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        
        int icon = R.drawable.beer_half_full;
        CharSequence tickerText = "How's that beer you're drinking?";
        long when = System.currentTimeMillis();
        
        Notification ratingsreminder = new Notification(icon, tickerText, when);
        ratingsreminder.flags |= Notification.FLAG_AUTO_CANCEL;
        
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        
        if (settings.getBoolean("remindersVibrate", true)) {
        	ratingsreminder.defaults |= Notification.DEFAULT_VIBRATE;
        }
        
        Context context = getApplicationContext();
        CharSequence contentTitle = "How's that beer?";
        CharSequence contentText = "Take a moment and rate your beer";
        Intent notificationIntent = new Intent(this, History.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        ratingsreminder.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        mNotificationManager.notify(BEER_HISTORY_ID, ratingsreminder);
    }
    
}

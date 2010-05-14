package com.wanghaus.remembeer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.activity.History;
import com.wanghaus.remembeer.helper.BeerDbHelper;
import com.wanghaus.remembeer.model.Drink;

public class NotifyService extends Service {
	private NotificationManager nm;
	
	public final static int RATEBEER_NOTIFY_ID = 1;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		final int drinkId = intent.getIntExtra("drinkId", -1);
		int timeout = intent.getIntExtra("timeout", -1);
		
		if (drinkId != -1 && timeout != -1) {
			// Schedule the notification
			final Handler handler = new Handler();
			final Runnable rater = new Runnable() {
				public void run() {
					showNotification(drinkId);
				}
			};

			handler.postDelayed(rater, timeout);
		}
	}

    /**
     * Show a notification while this service is running.
     */
    private void showNotification(int drinkId) {
    	BeerDbHelper dbs = new BeerDbHelper(this);
    	Drink drink = dbs.getDrink(drinkId);
    	
    	if (drink != null && !drink.isRated()) {
    		int icon = R.drawable.beer_half_full;
    		CharSequence tickerText = getText(R.string.reminder_tickerText);
    		long when = System.currentTimeMillis();
        
    		Notification ratingsreminder = new Notification(icon, tickerText, when);
    		ratingsreminder.flags |= Notification.FLAG_AUTO_CANCEL;
        
    		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        
    		if (settings.getBoolean("remindersVibrate", true)) {
    			ratingsreminder.defaults |= Notification.DEFAULT_VIBRATE;
    		}
        
    		Context context = getApplicationContext();
    		CharSequence contentTitle = getText(R.string.reminder_title);
    		CharSequence contentText = getText(R.string.reminder_text);
    		Intent notificationIntent = new Intent(this, History.class);
    		notificationIntent.putExtra("drinkId", drinkId);
    		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

    		ratingsreminder.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

    		nm.notify(RATEBEER_NOTIFY_ID, ratingsreminder);
    	}
    	
    	dbs.close();
    	stopSelf();
    }
}

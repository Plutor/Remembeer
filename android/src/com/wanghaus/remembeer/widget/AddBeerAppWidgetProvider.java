package com.wanghaus.remembeer.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.activity.AddBeer;
import com.wanghaus.remembeer.helper.BeerDbHelper;

public class AddBeerAppWidgetProvider extends AppWidgetProvider {
	private static BeerDbHelper bdb = null;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		// Connect to the db
		if (bdb == null)
			bdb = new BeerDbHelper(context);

		Log.d("AddBeerAppWidgetProvider", "update");
		
		// Setup the click event
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
	}

	private void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId) {
		
        // Create an Intent to launch ExampleActivity
        Intent intent = new Intent(context, AddBeer.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Get the layout for the App Widget and attach an on-click listener to the button
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.addbeer_appwidget);
        views.setOnClickPendingIntent(R.id.icon, pendingIntent);

        // Set the count to the number of beers in the last week
        // TODO - Configurable duration
        int count = bdb.getDrinkCountLastDays(7);
        if (count > 10)
        	views.setCharSequence(R.id.count, "setText", "10+");
        else
        	views.setCharSequence(R.id.count, "setText", String.valueOf(count));
        
        // Tell the AppWidgetManager to perform an update on the current App Widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
	}

}

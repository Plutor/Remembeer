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

	private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.addbeer_appwidget);

        // Create an Intent to launch AddBeer
        Intent addbeer = new Intent(context, AddBeer.class);
        PendingIntent pendingAddbeer = PendingIntent.getActivity(context, 0, addbeer, 0);
        views.setOnClickPendingIntent(R.id.beersearch, pendingAddbeer);

        // Create an Intent to launch AddBeer
        Intent barcode = new Intent(context, AddBeer.class);
        barcode.putExtra("scanBarcode", true);
        PendingIntent pendingBarcode = PendingIntent.getActivity(context, 0, barcode, 0);
        views.setOnClickPendingIntent(R.id.barcode_icon, pendingBarcode);
        
        // Tell the AppWidgetManager to perform an update on the current App Widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
	}

}

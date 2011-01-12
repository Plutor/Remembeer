package com.wanghaus.remembeer.activity;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.helper.BeerDbHelper;
import com.wanghaus.remembeer.helper.WebServiceHelper;
import com.wanghaus.remembeer.model.Drink;

public class PublishToWebService extends BaseActivity {

	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		final BeerDbHelper dbs;
		final WebServiceHelper wsh;
		final Context cContext;
		
		setTitle(R.string.webService_warning_title);
		setContentView(R.layout.publish_to_webservice);
		
		cContext = this;
		dbs = new BeerDbHelper(this);
		wsh = new WebServiceHelper(this);
		
        // Publish!
        Button yesButton = (Button) findViewById(R.id.webService_warning_yes);
        yesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	// Yes
        		
            	// Use dbs to get a count of unpublished drinks
            	// get a query of unpublished drinks
            	// send them via sendWebServiceRequest(Drink drink)
            	// Do it properly with a throbber in a new context view
            	Integer count = dbs.getDrinkCountUnPublished();
            	Toast.makeText(cContext, "Publishing " + count.toString() + " Beers", Toast.LENGTH_LONG).show();
            	
            	List<Drink> ListOfDrinks = dbs.getDrinks("uuid is null", null, null);
            	for (int i = 0; i < count; i++) {
            		wsh.sendWebServiceRequest(ListOfDrinks.get(i));
            	}
            	
        		setResult(1);
            	finish();
            }
            
        });
		Button noButton = (Button) findViewById(R.id.webService_warning_no);
		noButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// No

        		setResult(0);
				finish();
			}
		});
}}

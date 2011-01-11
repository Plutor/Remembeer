package com.wanghaus.remembeer.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.helper.BeerDbHelper;

public class PublishToWebService extends BaseActivity {

	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		final BeerDbHelper dbs;
		final Context cContext;
		
		setTitle(R.string.webService_warning_title);
		setContentView(R.layout.publish_to_webservice);
		
		cContext = this;
		dbs = new BeerDbHelper(this);
		
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
            	Toast.makeText(cContext, count.toString(), Toast.LENGTH_LONG).show();
            	
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

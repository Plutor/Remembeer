package com.wanghaus.remembeer.activity;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
		final Integer Count;
		
		setTitle(R.string.webService_warning_title);
		setContentView(R.layout.publish_to_webservice);
		
		cContext = this;
		dbs = new BeerDbHelper(this);
		wsh = new WebServiceHelper(this);
		Count = dbs.getDrinkCountUnPublished();

		TextView webServiceUploadText = (TextView) findViewById(R.id.webService_upload_text);
		String webServiceUploadStr = getText(R.string.webService_upload_prefix).toString();
		webServiceUploadStr += Count.toString();
		webServiceUploadStr += getText(R.string.webService_upload_suffix).toString();
		webServiceUploadText.setText(webServiceUploadStr);
		
		TextView webServiceEstimateText = (TextView) findViewById(R.id.webService_estimate_text);
		String webServiceEstimateStr = getText(R.string.webService_publish_prefix).toString();
		webServiceEstimateStr += " ";
		if (Count > 99) {
			webServiceEstimateStr += getText(R.string.webService_publish_5m).toString();
		} else if (Count > 60) {
			webServiceEstimateStr += getText(R.string.webService_publish_3m).toString();
		} else
			webServiceEstimateStr += getText(R.string.webService_publish_1m).toString();
		
		webServiceEstimateText.setText(webServiceEstimateStr);
		
        // Publish!
        Button yesButton = (Button) findViewById(R.id.webService_warning_yes);
        yesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	// Yes
        		
            	// We should really do this
            	// properly with a throbber in a new context view

            	List<Drink> ListOfDrinks = dbs.getDrinks("uuid is null", null, null);
            	for (int i = 0; i < Count; i++) {
            		wsh.sendWebServiceRequest(ListOfDrinks.get(i));
            	}
            	
            	Toast.makeText(cContext, "Published " + Count.toString() + " Beers", Toast.LENGTH_LONG).show();

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

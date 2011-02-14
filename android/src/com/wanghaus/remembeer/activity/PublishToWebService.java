package com.wanghaus.remembeer.activity;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
		final Integer toPublishCount;
		
		setTitle(R.string.webService_warning_title);
		setContentView(R.layout.publish_to_webservice);
		
		cContext = this;
		dbs = new BeerDbHelper(this);
		wsh = new WebServiceHelper(this);
		toPublishCount = dbs.getDrinkCountUnPublished();

		TextView webServiceUploadText = (TextView) findViewById(R.id.webService_upload_text);
		String webServiceUploadStr = getText(R.string.webService_upload_prefix).toString();
		webServiceUploadStr += toPublishCount.toString();
		webServiceUploadStr += getText(R.string.webService_upload_suffix).toString();
		webServiceUploadText.setText(webServiceUploadStr);
		
		TextView webServiceEstimateText = (TextView) findViewById(R.id.webService_estimate_text);
		String webServiceEstimateStr = getText(R.string.webService_publish_prefix).toString();
		webServiceEstimateStr += " ";
		if (toPublishCount > 99) {
			webServiceEstimateStr += getText(R.string.webService_publish_5m).toString();
		} else if (toPublishCount > 60) {
			webServiceEstimateStr += getText(R.string.webService_publish_3m).toString();
		} else
			webServiceEstimateStr += getText(R.string.webService_publish_1m).toString();
		
		webServiceEstimateText.setText(webServiceEstimateStr);
		
        // Publish!
        Button yesButton = (Button) findViewById(R.id.webService_warning_yes);
        yesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	// Yes
            	final List<Drink> listOfDrinks = dbs.getDrinks("uuid is null", null, null);
        		
            	// We should really do this
            	// properly with a throbber in a new context view
            	/// XXX Put in strings
            	final ProgressDialog progress = new ProgressDialog(cContext);
            	progress.setTitle(getText(R.string.webService_publish_dialog_title));
            	progress.setMessage(getText(R.string.webService_publish_dialog_message));
            	progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            	progress.show();
            	
            	final Handler handler = new Handler() {
            		public void handleMessage(Message m) {
            			int i = m.getData().getInt("total");
            			if (progress != null)
            				progress.setProgress(i+1);
            			
            			if (i == toPublishCount-1) {
                        	Toast.makeText(cContext, "Published " + toPublishCount.toString() + " Beers", Toast.LENGTH_LONG).show();

                        	progress.dismiss();
                    		setResult(1);
                        	finish();
            			}
            		}
            	};

            	progress.setMax(toPublishCount);

            	Thread t = new Thread() {
                    public void run() {
                    	for (int i = 0; i < toPublishCount; i++) {
                    		wsh.sendWebServiceRequest(listOfDrinks.get(i));
                    		
                    		// Build a message
                            Message msg = handler.obtainMessage();
                            Bundle b = new Bundle();
                            b.putInt("total", i);
                            msg.setData(b);

                            // Send it back to the thread
                        	handler.sendMessage(msg);
                    	}                    	
                    }
                };
                t.start();
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

package com.wanghaus.remembeer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.wanghaus.remembeer.R;

public class ConfigureWebService extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		final Context context = this;
		
		setTitle(R.string.webService_warning_title);
		setContentView(R.layout.configure_webservice);
		
        // Login
        Button yesButton = (Button) findViewById(R.id.webService_warning_yes);
        yesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	// Yes
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        		Editor editor = settings.edit();
        		editor.putBoolean("useWebService", true);
        		editor.commit();
        		
        		setResult(1);
            	finish();
            }
            
        });
		Button noButton = (Button) findViewById(R.id.webService_warning_no);
		noButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// No
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        		Editor editor = settings.edit();
        		editor.putBoolean("useWebService", false);
        		editor.commit();

        		setResult(0);
				finish();
			}
		});
	}
}

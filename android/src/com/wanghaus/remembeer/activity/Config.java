package com.wanghaus.remembeer.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.helper.TwitterHelper;

public class Config extends PreferenceActivity {
	private int CONFIG_TWITTER_DIALOG = 1;
	private int CONFIG_WEBSERVICE_DIALOG = 2;
	
	private CheckBoxPreference twitterCheckbox;
	private CheckBoxPreference webServiceCheckbox;
	private Context cContext;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	cContext = this;

    	addPreferencesFromResource(R.xml.preferences);

    	// Setup a listener for twitter Enabled to popup ConfigureTwitter
        twitterCheckbox = (CheckBoxPreference) getPreferenceScreen().findPreference("twitterEnabled");
		twitterCheckbox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				if (newValue.equals(false)) {
					// Show an "are you sure" dialog
					
		        	AlertDialog.Builder builder = new AlertDialog.Builder(cContext);
		    		builder.setMessage(getText(R.string.twitter_teardown_title))
		    		       .setCancelable(true)
		    		       .setPositiveButton(getText(android.R.string.ok), new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		                // clear the stored tokens
		    		        	   TwitterHelper.clearTokens(cContext);
		    		        	   twitterCheckbox.setChecked(false);
		    		        	   Log.d("Teardown", "Cleared stored Twitter tokens");
		    		           }
		    		       })
		    		       .setNegativeButton(getText(android.R.string.cancel), new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		                dialog.cancel();
		    		                Log.d("Teardown", "Cancelled");
		    		           }
		    		       });
		    		AlertDialog alert = builder.create();
		    		alert.show();
		        	
		        	// Always return false since the dialog will handle the checkbox
		        	return false;
				}

				if (newValue.equals(true)) {
					// Call ConfigureTwitter
					Intent configure;
					configure = new Intent(cContext,
							ConfigureTwitter.class);
					startActivityForResult(configure, CONFIG_TWITTER_DIALOG);
				}

				return false;
			}
		});
		
		// Setup a listener for twitter Enabled to popup ConfigureTwitter
		webServiceCheckbox = (CheckBoxPreference) getPreferenceScreen().findPreference("useWebService");
		webServiceCheckbox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				if (newValue.equals(false)) {
					// Don't ask to confirm on unchecking
					webServiceCheckbox.setChecked(false);
					return false;
				}

				if (newValue.equals(true)) {
					// Call ConfigureWebService
					Intent configure;
					configure = new Intent(cContext, ConfigureWebService.class);
					startActivityForResult(configure, CONFIG_WEBSERVICE_DIALOG);
				}

				return false;
			}
		});
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CONFIG_TWITTER_DIALOG) {
			Log.d("twitter", "isConfigured = " + TwitterHelper.isConfigured(cContext));
			 
			if (TwitterHelper.isConfigured(cContext))
				twitterCheckbox.setChecked(true);
		} else if (requestCode == CONFIG_WEBSERVICE_DIALOG) {
			if (resultCode == 1)
				webServiceCheckbox.setChecked(true);
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
    
    
}

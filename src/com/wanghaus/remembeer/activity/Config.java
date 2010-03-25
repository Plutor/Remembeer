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
import com.wanghaus.remembeer.service.TwitterService;

public class Config extends PreferenceActivity {
	private int TWITTER_CONFIG_DIALOG = 1;
	
	private CheckBoxPreference twitEnabled;
	private Context cContext;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	cContext = this;

    	addPreferencesFromResource(R.xml.preferences);

    	// Setup a listener for twitter Enabled to popup ConfigureTwitter
        twitEnabled = (CheckBoxPreference) getPreferenceScreen().findPreference("twitterEnabled");
		twitEnabled.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				if (newValue.equals(false)) {
					// Show an "are you sure" dialog
					
		        	AlertDialog.Builder builder = new AlertDialog.Builder(cContext);
		    		builder.setMessage(getText(R.string.twitter_teardown_title))
		    		       .setCancelable(true)
		    		       .setPositiveButton(getText(R.string.twitter_okay), new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		                // clear the stored tokens
		    		        	   TwitterService.clearTokens(cContext);
		    		        	   twitEnabled.setChecked(false);
		    		        	   Log.d("Teardown", "Cleared stored Twitter tokens");
		    		           }
		    		       })
		    		       .setNegativeButton(getText(R.string.twitter_cancel), new DialogInterface.OnClickListener() {
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
					startActivityForResult(configure, TWITTER_CONFIG_DIALOG);
				}

				return false;
			}
		});
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TWITTER_CONFIG_DIALOG) {
			Log.d("twitter", "isConfigured = " + TwitterService.isConfigured(cContext));
			 
			if (TwitterService.isConfigured(cContext))
				twitEnabled.setChecked(true);
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
    
    
}

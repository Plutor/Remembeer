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
	private CheckBoxPreference twitEnabled;
	private CheckBoxPreference twitClear;
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
        public boolean onPreferenceChange(Preference preference, Object newValue) {
        	
        	if (newValue.equals(false))
        		// When going from Enabled to Disabled, return true to let the library handle
        		// flipping the checkbox for us
        		return true;
        	
        	if (newValue.equals(true) && TwitterService.isConfigured() == false) {
        		// Call ConfigureTwitter
        		Intent configure;
        		configure = new Intent(cContext, ConfigureTwitter.class);
        		startActivity(configure);
        		
        		if (TwitterService.isConfigured()) {
        			twitClear.setEnabled(true);
        			twitClear.setChecked(false);
        			return true;
        		}
        	}

        	return false;
        }
    	});
    	
    	// Setup a listener for twitter Clear to popup the confirm dialog
        twitClear = (CheckBoxPreference) getPreferenceScreen().findPreference("twitterClear");
    	twitClear.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {	
        	AlertDialog.Builder builder = new AlertDialog.Builder(cContext);
    		builder.setMessage(getText(R.string.twitter_teardown_title))
    		       .setCancelable(true)
    		       .setPositiveButton(getText(R.string.twitter_okay), new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		                // clear the stored tokens
    		        	   TwitterService.clearTokens(cContext);
    		        	   twitEnabled.setChecked(false);
    		        	   twitClear.setEnabled(false);
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
    	});    	
    }
    
}

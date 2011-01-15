package com.wanghaus.remembeer.activity;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.helper.TwitterHelper;

public class ConfigureTwitter extends Activity {
    final Handler mHandler = new Handler();

    final Runnable mShowError = new Runnable() {
            public void run() {
                showError();
            }
        };

    private void showError() {
        final Context context = this;
        Toast.makeText(context, getText(R.string.twitter_whoops),
                       Toast.LENGTH_LONG).show();
    }

	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		final Context context = this;
		
		setTitle(R.string.twitter_signin);
		setContentView(R.layout.configure_twitter);
		
        // Login
        Button loginButton = (Button) findViewById(R.id.twitLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	TextView loginView = (TextView) findViewById(R.id.Username);
                final String username = loginView.getText().toString();
                TextView passwdView = (TextView) findViewById(R.id.Password);
                final String password = passwdView.getText().toString();
                
            	if (loginView.getText().length() == 0 ||
            		passwdView.getText().length() == 0 )
            		return;
            	
            	// Throw a spinner up while we're doing this
            	final ProgressDialog throbber = ProgressDialog.show(context,
            			getText(R.string.twitter_progress_title),
            			getText(R.string.twitter_progress_message), true);

            	// Authorize out-of-thread so spinner can actually run
        	    Thread authorizeThread = new Thread() {
        	        public void run() {
                        try {
                               Twitter twitter = new TwitterFactory().getInstance(username, password);
                            AccessToken accessToken = twitter.getOAuthAccessToken();
        	                TwitterHelper.setupTwitter(context, accessToken);
        					Log.d("ConfigureTwitter", "Got access token: " + accessToken.toString());
                              finish();
        				} catch (TwitterException e) {
                            Log.d("ConfigureTwitter", e.getMessage());
                            mHandler.post(mShowError);
                        } finally {
                            throbber.dismiss();
                        }

        				Log.d("ConfigureTwitter", "ConfigureTwitter thread ending");
        	        }
        	    };
        	    authorizeThread.start();
            }
        });
		Button cancelButton = (Button) findViewById(R.id.twitCancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}
}

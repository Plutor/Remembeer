package com.wanghaus.remembeer.service;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class TwitterService {
	public TwitterService() {
	}
	
	public static void sendToTwitter(Context context, String beername) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		if (!prefs.getBoolean("twitterEnabled", false))
			return;
		
		final String username = prefs.getString("twitterUsername", null);
		final String password = prefs.getString("twitterPassword", null);
		String template = prefs.getString("twitterTemplate", null);
		
		if (username == null || password == null || template == null)
			return;
		
		// DO IT
		final String status = template.replace("BEERNAME", beername);
		
		Log.d("TwitterService", "Sending '" + status + "' to twitter");

	    // Create runnable for posting
	    Thread updateStatus = new Thread() {
	        public void run() {
				TwitterFactory factory = new TwitterFactory();
				Twitter twitter = factory.getInstance(username, password);

				try {
					twitter.updateStatus(status);
				} catch (TwitterException e) {
					Log.e("TwitterService", "Failure to tweet", e);
				}

				Log.d("TwitterService", "Twitter thread ending");
	        }
	    };
	    updateStatus.start();
	}
}

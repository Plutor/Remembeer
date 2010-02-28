package com.wanghaus.beerlog.service;

import twitter4j.Twitter;
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
		
		String username = prefs.getString("twitterUsername", null);
		String password = prefs.getString("twitterPassword", null);
		String template = prefs.getString("twitterTemplate", null);
		
		Log.d("twitter", "username = " + username);
		Log.d("twitter", "password = " + password);
		Log.d("twitter", "template = " + template);
		
		if (username == null || password == null || template == null)
			return;
		
		// DO IT
		String status = template.replace("BEERNAME", beername);
		
		Log.d("twitter", "Sending '" + status + "' to twitter");

		try {
			TwitterFactory factory = new TwitterFactory();
			Twitter twitter = factory.getInstance(username, password);
			
			twitter.updateStatus(status);
		} catch (Throwable e) {
			Log.e("twitter", "Failure to tweet", e);
		}
	}
}

package com.wanghaus.remembeer.helper;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class TwitterHelper {
	public TwitterHelper() {
	}
	
	public static boolean isConfigured(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		return prefs.getBoolean("twitterEnabled", false) &&
			prefs.getString("twitterUsername", null) != null &&
			prefs.getString("twitterToken", null) != null &&
			prefs.getString("twitterSecret", null) != null;
	}
	
	public static void clearTokens(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		
		editor.remove("twitterUsername");
		editor.remove("twitterToken");
		editor.remove("twitterSecret");
		editor.putBoolean("twitterEnabled", false);
		editor.commit();
	}
	
	public static void setupTwitter(Context context, AccessToken AT) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		
		// We don't *need* the Username, but we'll store it anyway
		editor.putString("twitterUsername", AT.getScreenName());
		editor.putString("twitterToken", AT.getToken());
		editor.putString("twitterSecret", AT.getTokenSecret());
		editor.putBoolean("twitterEnabled", true);
		editor.commit();
	}
	
	public static void sendToTwitter(Context context, String beername) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		if (!prefs.getBoolean("twitterEnabled", false)) {
			Log.v("sendToTwitter", "Twitter not enabled, exiting");
			return;	
		}
		
		final String token = prefs.getString("twitterToken", null);
		final String secret = prefs.getString("twitterSecret", null);
		String template = prefs.getString("twitterTemplate", null);
		
		if (token == null || secret == null || template == null) {
			Log.e("sendToTwitter", "Couldn't get the token or the template from prefs");
			return ;
		}
		
		// DO IT
		final AccessToken accessToken = new AccessToken(token, secret);
		final String status = template.replace("BEERNAME", beername);
		
		Log.v("TwitterService", "Sending '" + status + "' to twitter");

	    // Create runnable for posting
	    Thread updateStatus = new Thread() {
	        public void run() {
				TwitterFactory factory = new TwitterFactory();
				Twitter twitter = factory.getOAuthAuthorizedInstance(accessToken);

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

package com.wanghaus.remembeer.helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.model.Beer;
import com.wanghaus.remembeer.model.Drink;

public class WebServiceHelper {
	//public static String webserviceRoot = "http://api.remembeer.info/";
	public static String webserviceRoot = "http://192.168.0.246:8000/";
	private BeerDbHelper dbs;
	
	public WebServiceHelper(Context context) {
		this(new BeerDbHelper(context));
	}
	public WebServiceHelper(BeerDbHelper dbs) {
    	this.dbs = dbs;
	}

	public List<Beer> findBeersBySubstring(String beername) {
		Beer beer = new Beer();
		beer.setName(beername);

		return sendWebServiceRequest(beer, true);
	}

	public JSONArray sendWebServiceRequest(JSONObject json) {
		// Don't do anything if web service is off
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(dbs.context);
        if (!settings.getBoolean("useWebService", false))
        	return null;

        // Add the user id and client version
		try {
			json.put("user", getUniqueUserId());
			json.put("clientVersion", getClientVersion());
		} catch (JSONException e) {
			Log.w("WebServiceHelper", "Problem building drink JSON", e);
		}

		// Send it
		String response = "";
		try {
			Log.d("WebServiceHelper", "url = " + webserviceRoot);
			URL url = new URL(webserviceRoot);
			String data = json.toString();
			
			Log.d("WebServiceHelper", "Sending data: " + data);

			data = URLEncoder.encode("q", "UTF-8") + "=" + URLEncoder.encode(data, "UTF-8");

			URLConnection conn = url.openConnection(); 
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();
			
			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line;
			while ((line = rd.readLine()) != null) {
				response += line;
			}
			wr.close();
			rd.close();
		} catch (Exception e) {
			Log.e("WebServiceHelper", "Failed to make request", e);
		}
		
		// TODO - This will be a list of objects
		// Extract beer object from response
		if (response != null && !response.equals("")) {
			Log.d("WebServiceHelper", "Got response: " + response);
			try {
				JSONArray responseJSON = new JSONArray(response);
				return responseJSON;
			} catch (Exception e) {
				Log.e("WebServiceHelper", "Unable to parse JSON response", e);
			}
		}
		
		return null;
	}
	
	public List<Beer> sendWebServiceRequest(Beer beer, boolean search) {
		// Build JSON
		JSONObject json = beer.toJSONObject();
		try {
			json.put("search", String.valueOf(search));
		} catch (JSONException e) {}
		
		// Send it
		JSONArray responseJSON = sendWebServiceRequest(json);
		List<Beer> responseBeers = new ArrayList<Beer>();
		
		// Extract beer objects from response
		if (responseJSON != null) {
			try {
				for (int i=0; i<responseJSON.length(); ++i) {
					Beer b = new Beer(responseJSON.getJSONObject(i));
					responseBeers.add(b);
				}
			} catch (Exception e) {
				Log.e("WebServiceHelper", "Unable to parse JSON response", e);
			}
		}
		
		return responseBeers;
	}
	
	@SuppressWarnings("unchecked")
	public Drink sendWebServiceRequest(Drink drink) {
		// Build JSON
		JSONObject json = drink.toJSONObject();
		try {
			json.put("search", "false");
		} catch (JSONException e1) {}

		Beer drinkBeer = dbs.getBeer(drink.getBeerId());
		JSONObject beerJson = drinkBeer.toJSONObject();
		Iterator<String> jsonKeys =  beerJson.keys();
		while( jsonKeys.hasNext() ) {
			String key = jsonKeys.next();
			String value;
			try {
				value = beerJson.getString(key);
				json.put(key, value);
			} catch (JSONException e) {
				Log.w("WebServiceHelper", "Problem building drink JSON", e);
			}
		}
		
		// Send it
		JSONArray responseJSON = sendWebServiceRequest(json);
		
		// Extract drink and beer objects from response
		if (responseJSON != null && responseJSON.length() > 0) {
			try {
				JSONObject firstResponse = responseJSON.getJSONObject(0);
				Beer responseBeer = new Beer(firstResponse);
				Drink responseDrink = new Drink(firstResponse);
				
				// Update the original drink and beer in the db
				if (drink.getId() > 0) {
					responseDrink.setId( drink.getId() );
					responseDrink.setBeerId( drink.getBeerId() );
					dbs.updateOrAddDrink(responseDrink);
					
					responseBeer.setId( drink.getBeerId() );
					dbs.updateOrAddBeer(responseBeer);
				}
				
				return responseDrink;
			} catch (Exception e) {
				Log.e("WebServiceHelper", "Unable to parse JSON response", e);
			}
		}
		
		return null;
	}
	
	private String getUniqueUserId() {
		Log.d("getUniqueUserId", "user = " + Settings.Secure.ANDROID_ID);
		return Settings.Secure.ANDROID_ID;
	}
	
	private String getClientVersion() {
		Context ctx = dbs.context;
		String rv = "?";
		
		try {
            String pkg = ctx.getPackageName();
            String name = ctx.getText(R.string.app_name).toString();
            String version = ctx.getPackageManager().getPackageInfo(pkg, 0).versionName;
            if (name != null && !name.equals("") && version != null && !version.equals(""))
            	rv = name + " " + version;
        } catch (NameNotFoundException e) {
        	Log.e("getClientVersion", "Can't determine client version");
        }
        
        return rv;
	}
}

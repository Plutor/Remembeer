package com.wanghaus.remembeer.helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.wanghaus.remembeer.model.Beer;

public class WebServiceHelper {
	public static String webserviceRoot = "http://remembeer.info:8000/w/";
	private BeerDbHelper dbs;
	
	public WebServiceHelper(Context context) {
		this(new BeerDbHelper(context));
	}
	public WebServiceHelper(BeerDbHelper dbs) {
    	this.dbs = dbs;
	}

	public Beer findBeerByName(String beername) {
		Beer beer = dbs.findBeerByName(beername);
		
		if (beer == null) {
			// Do a lookup
			beer = new Beer();
			beer.setName(beername);
	
			Beer response = sendWebServiceRequest(beer, true);
			if (response != null) { // and other criteria?
				beer = response;
			}
		}
		
		return beer;
	}
	
	public Beer sendWebServiceRequest(Beer beer, boolean search) {
		// Build JSON
		JSONObject json = beer.toJSONObject();
		try {
			json.put("search", String.valueOf(search));
		} catch (JSONException e) {}
		
		// Send it
		String response = "";
		try {
			URL url = new URL(webserviceRoot);
			String data = URLEncoder.encode("q", "UTF-8") + "=" + URLEncoder.encode(json.toString(), "UTF-8");
			
			Log.d("WebServiceHelper", "Sending data: " + data);

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
		
		// Extract beer object from response
		if (response != null && !response.equals("")) {
			Log.d("WebServiceHelper", "Got response: " + response);
			try {
				JSONObject responseJSON = new JSONObject(response);
				Beer responseBeer = new Beer(responseJSON);
				return responseBeer;
			} catch (Exception e) {
				Log.e("WebServiceHelper", "Unable to parse JSON response", e);
			}
		}
		
		return null;
	}
}

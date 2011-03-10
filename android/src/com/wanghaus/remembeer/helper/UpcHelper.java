package com.wanghaus.remembeer.helper;

import java.util.HashMap;
import java.util.Map;

import org.xmlrpc.android.XMLRPCClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class UpcHelper {
	private static String webserviceRoot = "http://www.upcdatabase.com/xmlrpc";
	private static String rpc_key = "f99ddc07a3ecea5b84d8c9bcc71028e19cc1e003";
	private Context context;
	
	public UpcHelper(Context context) {
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	public String getUpcProductName(String upc) {
		// TODO - different pref
		// Don't do anything if web service is off
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        if (!settings.getBoolean("useWebService", false))
        	return null;

        XMLRPCClient client = new XMLRPCClient(webserviceRoot);

    	try {
    		Map<String, String> params = new HashMap<String, String>();
    		params.put("rpc_key", rpc_key);
    		params.put("upc",upc);
    		HashMap result = (HashMap) client.call("lookup", params);

    		String resultDesc = result.get("description").toString();
    		Log.d("getUpcProductName", "Got " + resultDesc);
    		return resultDesc;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        
        return "";
	}
}

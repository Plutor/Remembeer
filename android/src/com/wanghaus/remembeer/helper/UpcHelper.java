package com.wanghaus.remembeer.helper;

import java.util.HashMap;
import java.util.Map;

import org.xmlrpc.android.XMLRPCClient;

import android.util.Log;

public class UpcHelper {
	private static String webserviceRoot = "http://www.upcdatabase.com/xmlrpc";
	private static String rpc_key = "f99ddc07a3ecea5b84d8c9bcc71028e19cc1e003";
	
	@SuppressWarnings("unchecked")
	public String getUpcProductName(String upc) {
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

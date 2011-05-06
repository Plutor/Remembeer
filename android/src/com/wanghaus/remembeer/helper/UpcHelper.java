package com.wanghaus.remembeer.helper;

import java.util.HashMap;
import java.util.Map;

import org.xmlrpc.android.XMLRPCClient;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UpcHelper {
	private static String webserviceRoot = "http://www.upcdatabase.com/xmlrpc";
	private static String rpc_key = "f99ddc07a3ecea5b84d8c9bcc71028e19cc1e003";
	
	@SuppressWarnings("unchecked")
	public void getUpcProductName(final String upc, final Handler resultHandler) {
		Thread t = new Thread() {
			@Override
			public void run() {
		        XMLRPCClient client = new XMLRPCClient(webserviceRoot);

		    	try {
		    		Map<String, String> params = new HashMap<String, String>();
		    		params.put("rpc_key", rpc_key);
		    		params.put("upc", upc);
		    		HashMap result = (HashMap) client.call("lookup", params);

		    		String resultDesc = "";
		    		if (result != null && result.get("description") != null)
		    			resultDesc = result.get("description").toString();
		    		Log.d("getUpcProductName", "Got " + resultDesc);

		    		// Tell the UI thread about the result
		    		Message msg = resultHandler.obtainMessage();
		    		msg.obj = resultDesc;
		    		resultHandler.sendMessage(msg);
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
			}
		};
		
		t.start();
	}
}

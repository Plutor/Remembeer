package com.wanghaus.remembeer.helper;

import java.util.HashMap;
import java.util.Map;

import org.xmlrpc.android.XMLRPCClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.wanghaus.remembeer.R;

public class UpcHelper {
	private static String webserviceRoot = "http://www.upcdatabase.com/xmlrpc";
	private static String rpc_key = "f99ddc07a3ecea5b84d8c9bcc71028e19cc1e003";
	private Activity activity;
	private ProgressDialog progress;

	public UpcHelper(Activity activity) {
		this.activity = activity;
	}
	
	@SuppressWarnings("unchecked")
	public String getUpcProductName(final String upc) {
        // Show a throbber
		progress = new ProgressDialog(activity);
		progress.setTitle(activity.getText(R.string.barcode_inprogress_title));
		progress.setMessage(activity.getText(R.string.barcode_inprogress_message));
		progress.setIndeterminate(true);
		progress.show();

		// TODO - what's the best way to do this in another thread?
		// We probably will have to make it so the result doesn't come straight from this method
        XMLRPCClient client = new XMLRPCClient(webserviceRoot);

    	try {
    		Map<String, String> params = new HashMap<String, String>();
    		params.put("rpc_key", rpc_key);
    		params.put("upc", upc);
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

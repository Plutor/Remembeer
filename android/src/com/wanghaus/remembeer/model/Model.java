package com.wanghaus.remembeer.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

@SuppressWarnings("serial")
public abstract class Model implements Serializable {
	private Map<String, String> stash;
	
	public Model() {
		super();
		stash = new HashMap<String, String>();
	}
	public Model(Cursor c) {
		this();
		putAll(c);
	}
	public Model(JSONObject json) {
		this();
		
		Map<String, String> exportMap = getExportMap();
		Set<String> keys = exportMap.keySet();
		for (String jsonKey : keys) {
			String modelKey = exportMap.get(jsonKey);
			try {
				put(modelKey, json.getString(jsonKey));
			} catch (JSONException e) {	}
		}
	}
	
	public void put(String key, String value) {
		stash.put(key, value);
	}
	
	public String get(String key) {
		return stash.get(key);
	}
	protected int getInt(String key) {
		try {
			if ( get(key) != null )
				return Integer.valueOf( get(key) );
		} catch (NumberFormatException e) {}
			
		return 0; 
	}
	protected float getFloat(String key) {
		try {
			if ( get(key) != null )
				return Float.valueOf( get(key) );
		} catch (NumberFormatException e) {}
		
		return (float)0.0;
	}
	
	public void putAll(Cursor c) {
		for (int col = 0; col < c.getColumnCount(); ++col) {
			String key = c.getColumnName(col);
			if (key == null) continue;
			
			String val = c.getString(col);
			if (val == null) continue;
			
			put(key, val);
		}
	}
	
	public Map<String, String> getAll() {
		return stash;
	}
	
	public JSONObject toJSONObject() {
		JSONObject rv = new JSONObject();
		
		Map<String, String> exportMap = getExportMap();
		Set<String> keys = exportMap.keySet();
		for (String jsonKey : keys) {
			String modelKey = exportMap.get(jsonKey);
			try {
				rv.put(jsonKey, get(modelKey));
			} catch (JSONException e) {	}
		}

		return rv;
	}
	
	public int size() {
		return stash.size();
	}
	
	public abstract Map<String, String> getExportMap(); // export column name => db column name
}

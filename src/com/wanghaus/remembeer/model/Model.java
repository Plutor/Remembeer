package com.wanghaus.remembeer.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
	
	public void put(String key, String value) {
		stash.put(key, value);
	}
	
	public String get(String key) {
		return stash.get(key);
	}
	protected int getInt(String key) {
		if ( get(key) != null )
			return Integer.valueOf( get(key) );
		else
			return 0; 
	}
	protected float getFloat(String key) {
		if ( get(key) != null )
			return Float.valueOf( get(key) );
		else
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
	
	public int size() {
		return stash.size();
	}
}

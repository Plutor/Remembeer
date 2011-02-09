package com.wanghaus.remembeer.model;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.database.Cursor;

public class Beer extends Model {	
	private static final long serialVersionUID = -2080563347089735252L;

	public Beer() {
		super();
	}
	public Beer(Cursor c) {
		super(c);
	}
	public Beer(JSONObject j) {
		super(j);
	}

	public Map<String, String> getExportMap() { // export column name => db column name
		Map<String, String> rv = new HashMap<String, String>();
		
		rv.put("beername", "name");
		rv.put("brewery", "brewery");
		rv.put("location", "brewery_location");
		rv.put("abv", "abv");
		rv.put("style", "style");
		rv.put("about_this_beer", "notes");
		
		return rv;
	}
	
	//
	//** READ-WRITE PROPERTIES
	//
	public int getId()					{ return getInt("_id"); }
	public void setId(int val)			{ put("_id", String.valueOf(val)); }

	public String getName()				{ return get("name"); }
	public void setName(String val)	    { put("name", val); }

	public String getBrewery()			{ return get("brewery"); }
	public void setBrewery(String val)	{ put("brewery", val); }

	public String getLocation()			{ return get("brewery_location"); }
	public void setLocation(String val)	{ put("brewery_location", val); }

	public String getABV()				{ return get("abv"); }
	public void setABV(String val)		{ put("abv", val); }

	public String getStyle()			{ return get("style"); }
	public void setStyle(String val)	{ put("style", val); }

	public String getNotes()			{ return get("notes"); }
	public void setNotes(String val)	{ put("notes", val); }
	
	//
	//** READ ONLY PROPERTIES
	//
	public String getCount()			{ return get("count"); }
	
	public String getDetails() {
		String details = "";
		
		String style = getStyle();
		if (style != null && !style.equals(""))
			details += getStyle();
		
		String brewery = getBrewery();
		if (brewery != null && !brewery.equals("")) {
			if (details.equals(""))
				details = "Brewed by " + brewery;
			else
				details += ", brewed by " + brewery;
			
			String location = getLocation();
			if (location != null)
				details += " in " + location;
		}
		
		return details;
	}
	
	public String getIcon() {
		// TODO - Do this better
		String icon = "beer_full";
		return icon;
	}
}

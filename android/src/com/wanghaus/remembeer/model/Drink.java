package com.wanghaus.remembeer.model;

import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;

public class Drink extends Model {
	private static final long serialVersionUID = 5915137912436398058L;
	
	public Drink() {
		super();
	}
	public Drink(Cursor c) {
		super(c);
	}
	public Drink(Beer beer, String container, String stamp, String notes) {
		this();
		setBeerId(beer.getId());
		setContainer(container);
		setStamp(stamp);
		setNotes(notes);
	}

	public Map<String, String> getExportMap() { // export column name => db column name
		Map<String, String> rv = new HashMap<String, String>();
		
		rv.put("container", "container");
		rv.put("stamp", "stamp");
		rv.put("rating", "rating");
		rv.put("tasting_notes", "notes");
		
		return rv;
	}
	

	//
	//** READ-WRITE PROPERTIES
	//
	public int getId()					{ return getInt("_id"); }
	public void setId(int val)			{ put("_id", String.valueOf(val)); }

	public String getContainer()		{ return get("container"); }
	public void setContainer(String val){ put("container", val); }
	
	public String getStamp()			{ return get("stamp"); }
	public void setStamp(String val)	{ put("stamp", val); }
	
	public float getRating()			{ return getFloat("rating"); }
	public void setRating(float val)	{ put("rating", String.valueOf(val)); }
	
	public String getNotes()			{ return get("notes"); }
	public void setNotes(String val)	{ put("notes", val); }
	
	public int getBeerId()				{ return getInt("beer_id"); }
	public void setBeerId(int val)		{ put("beer_id", String.valueOf(val)); }
	
	/*public Beer getBeer() {
		// TODO - How do we encapsulate this?
		//Giving Drink an Intent is a bad way to do this, because it makes drinks unserializable.
	}
	public void setBeer(Beer beer) {
	}*/
	
	//
	//** READ ONLY PROPERTIES
	//
	public boolean isRated() {
		float rating = getRating();

		return (rating > 0);
	}
}

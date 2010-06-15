package com.wanghaus.remembeer.test;

import java.util.List;

import com.wanghaus.remembeer.model.Drink;

public class ImportTestMinimal extends ImportTest {
	@Override
	protected void setUp() throws Exception {
		// Import known set
		super.setUp( "remembeer_import_minimal.csv" );
	}
	
	/*
	 * Actual test cases
	 */
	public void testBeersCount() throws Throwable {
		int count = dbs.getBeersCount();
		assertTrue(count == 2);
	}	

	public void testDrinksCount() throws Throwable {
		int count = dbs.getDrinkCount();
		assertTrue(count == 3);
	}	
	
	public void testGetDrinksWhen() throws Throwable {
		// There is a drink at this time
		List<Drink> d = dbs.getDrinksWhen("2010-01-01 05:00:00");
		assertTrue(d != null);
		assertTrue(d.size() == 1);
		
		// There is not a drink at this time
		d = dbs.getDrinksWhen("2011-01-01 05:00:00");
		assertTrue(d != null);
		assertTrue(d.size() == 0);
	}
}
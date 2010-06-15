package com.wanghaus.remembeer.test;

import java.util.List;

import com.wanghaus.remembeer.model.Drink;

public class ImportTestIncomplete extends ImportTest {
	@Override
	protected void setUp() throws Exception {
		// Import known set
		super.setUp( "remembeer_import_incomplete.csv" );
	}
	
	/*
	 * Actual test cases
	 */
	public void testBeersCount() throws Throwable {
		int count = dbs.getBeersCount();
		assertTrue(count == 0);
	}	

	public void testDrinksCount() throws Throwable {
		int count = dbs.getDrinkCount();
		assertTrue(count == 0);
	}	
	
	public void testGetDrinksWhen() throws Throwable {
		List<Drink> d = dbs.getDrinksWhen("2010-01-01 05:00:00");
		assertTrue(d != null);
		assertTrue(d.size() == 0);
	}
}
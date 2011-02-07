package com.wanghaus.remembeer.test;

import java.util.List;

import android.util.Log;

import com.wanghaus.remembeer.model.Beer;
import com.wanghaus.remembeer.model.Drink;

public class ImportTestWithFields extends ImportTest {
	@Override
	protected void setUp() throws Exception {
		// Import known set
		super.setUp( "remembeer_import_with_fields.csv" );
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
	
	public void testGetDrinkFields() throws Throwable {
		int row = 0;
		
		// There is a drink at this time
		List<Drink> drinks = dbs.getDrinks(null, null, null);
		assertTrue(drinks != null);
		assertTrue(drinks.size() == 3);

		for (Drink d : drinks) {
			assertTrue( d != null );
			assertTrue( d.getContainer() != null );
			assertTrue( d.getStamp() != null );
			if (row != 2) assertTrue( d.getNotes() != null);

			assertTrue( d.getBeerId() > 0 );
			Beer b = dbs.getBeer(d.getBeerId());
			assertTrue( b != null );
			assertTrue( b.getName() != null );
			if (row != 0) assertTrue( b.getNotes() != null); 
			if (row != 0) assertTrue( b.getStyle() != null);
			if (row != 0) assertTrue( b.getBrewery() != null);
			if (row != 2) assertTrue( b.getLocation() != null);
			if (row != 0) assertTrue( b.getABV() != null);
			
			assertTrue( d.isRated() );
			assertTrue( d.getRating() > 0 );

			row++;
		}
	}
}
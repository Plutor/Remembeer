package com.wanghaus.remembeer.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.wanghaus.remembeer.helper.BeerDbHelper;
import com.wanghaus.remembeer.helper.ImportExportHelper;
import com.wanghaus.remembeer.model.Beer;
import com.wanghaus.remembeer.model.Drink;

import android.test.InstrumentationTestCase;
import android.util.Log;

public abstract class ImportTest extends InstrumentationTestCase {
	protected BeerDbHelper dbs;

	public ImportTest() {
		super();
	}

	protected void setUp(String filename) throws Exception {
		super.setUp();
		
		dbs = new BeerDbHelper(getInstrumentation().getTargetContext(), true);
		
		// Make sure there are no beers or drinks in the db
		removeAllDrinks();
		removeAllBeers();
		
		// Import known set
		importCsvFile( filename );
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		// Remove all beers and drinks
		removeAllDrinks();
		removeAllBeers();
	
		if (dbs != null)
			dbs.close();
	}

	private void removeAllBeers() {
		List<Beer> allBeers = dbs.getBeers(null, null, null);
		for (Beer b : allBeers)
			dbs.deleteBeer(b);
		
		// Make sure we did it right
		int count = dbs.getBeersCount();
		assertTrue(count == 0);
	}

	private void removeAllDrinks() {
		List<Drink> allDrinks = dbs.getDrinks(null, null, null);
		for (Drink d : allDrinks)
			dbs.deleteDrink(d);
		
		// Make sure we did it right
		int count = dbs.getDrinkCount();
		assertTrue(count == 0);
	}

	private void importCsvFile(String filename) {
		ImportExportHelper ieh = new ImportExportHelper(dbs);
		
		InputStream ins = null;
		try {
			ins = getInstrumentation().getContext().getAssets().open( filename );
		} catch (IOException e) {
			Log.e("import", "Importing CSV file " + filename + " failed", e);
			fail();
		}
	
		ieh.importHistoryFromCsvFile(ins);
	}

}
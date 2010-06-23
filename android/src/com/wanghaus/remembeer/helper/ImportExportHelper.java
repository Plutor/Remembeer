package com.wanghaus.remembeer.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import au.com.bytecode.opencsv.CSVReader;

import com.wanghaus.remembeer.model.Beer;
import com.wanghaus.remembeer.model.Drink;

public class ImportExportHelper {
	private String DB_CSV = new String(Environment.getExternalStorageDirectory() +  File.separator + "Remembeer_export.csv");
	private String DB_CSV_LEGACY = new String(Environment.getExternalStorageDirectory() +  File.separator + "BeerLog_export.csv");
	private BeerDbHelper dbs;
	
	public ImportExportHelper(Context context) {
		this(new BeerDbHelper(context));
	}
	public ImportExportHelper(BeerDbHelper dbs) {
    	this.dbs = dbs;
	}
	
    public int importHistoryFromCsvFile() {
    	InputStream ins;
    	
    	try {
    		ins = new FileInputStream(DB_CSV);
    	} catch(FileNotFoundException e) {
        	try {
        		ins = new FileInputStream(DB_CSV_LEGACY);
        	} catch(FileNotFoundException e2) {
        		return -1;
        	}
    	}
    	
		return importHistoryFromCsvFile(ins);
    }
    
    public int importHistoryFromCsvFile( InputStream ins ) {
    	InputStreamReader csvFile = new InputStreamReader(ins);
    	Integer count = new Integer(0);
    
       	List<String> columns = null;
		int stampcol = -1;
		int beernamecol = -1;
		int containercol = -1;
		
	    CSVReader reader = new CSVReader(csvFile);
	    String [] nextLine;
	    
       	try {
        	columns = Arrays.asList( reader.readNext() );
           	
        	for (int i=0; i<columns.size(); ++i) {
        		String c = columns.get(i);
        		c = dequote(c);
        		columns.set(i, c);
        		Log.i("import", "Got column: '" + c + "'");
        	}
        	
    		stampcol = columns.indexOf("stamp");
    		beernamecol = columns.indexOf("beername");
    		containercol = columns.indexOf("container");
    		Log.i("import", "Stamp is column: " + stampcol);
    		Log.i("import", "Beer name is column: " + beernamecol);
    		Log.i("import", "Container is column: " + containercol);
    		
        	if (beernamecol == -1 || stampcol == -1 || containercol == -1) {
				// The header in the file wasn't right, so we're out of here
        		Log.e("import", "Missing at least one of the required columns: beername, container, stamp");
				return -1;
        	}
		} catch (IOException e) {
			Log.e("importHistory", e.toString());
			return -1;
		}

    	try {
    	    while ((nextLine = reader.readNext()) != null) {
    			List<String> elements = Arrays.asList(nextLine);

            	for (int i=0; i<elements.size(); ++i) {
            		String c = elements.get(i);
            		elements.set(i, dequote(c));
            	}
    			
    			Drink drink = new Drink();
    			
    			// See if there's a drink that matches this row
    			List<Drink> drinksAtThisTime = dbs.getDrinksWhen( elements.get(stampcol) );
    			for (Drink d : drinksAtThisTime) {
    				Beer b = dbs.getBeer( d.getBeerId() );
    				if (d.getContainer().equals(elements.get(containercol)) &&
    						b.getName().equals(elements.get(beernamecol))) {
    					drink = d;
    					Log.i("import", "There's already a drink like this");
    					break;
    				}
    			}
    			
    			// Update drink
    			Map<String, String> exportMap = drink.getExportMap();
    			for (String column : exportMap.keySet()) {
    				String attr = exportMap.get(column);
    				int col = columns.indexOf(column);
    				String val = "";
    				if (col != -1)
    					val = elements.get(col);
    				if (val != null && !val.equals(""))
    					drink.put(attr, elements.get(col));
    			}

    			// Build/update beer
    			Beer beer = null;
    			if (drink.getBeerId() > 0)
    				beer = dbs.getBeer(drink.getBeerId());
    			else
    				beer = dbs.getBeer(elements.get(beernamecol));
    			
    			if (beer == null)
    				beer = new Beer();
    			
    			exportMap = beer.getExportMap();
    			for (String column : exportMap.keySet()) {
    				String attr = exportMap.get(column);
    				int col = columns.indexOf(column);
    				String val = "";
    				if (col != -1)
    					val = elements.get(col);
    				if (val != null && !val.equals(""))
    					beer.put(attr, elements.get(col));
    			}

    			// First, write the beer
    			int beerId = dbs.updateOrAddBeer(beer);
    			Log.i("import", "Added or updated beer " + beerId);

    			drink.setBeerId(beerId);
    			int drinkId = dbs.updateOrAddDrink(drink);
    			Log.i("import", "Added or updated drink " + drinkId);

    			count++;
    		}
    		csvFile.close();
    	} catch(Exception e){
    		Log.e("importHistory", "Failed to import history csv", e);
    	}
    	Log.d("ImportHistory", "Imported " + count.toString() + " Beers");
    	
		return count; 
	}

    private String enquote(String str) {
		if (str != null)
			str = str.replaceAll("\"", "\\\""); // escape quotes
		else
			str = "";
		
		return "\"" + str + "\"";
    }

    private String dequote(String str) {
    	// Strip leading and trailing quotes
		str = str.replaceAll("^\"", "").replaceAll("\"$", "");
		
		// convert escaped quotes
		str = str.replaceAll("\\\"", "\"");
		
		return str;
    }
    
    public Uri exportHistoryToCsvFile() {
    	List<Drink> allDrinks = dbs.getDrinks(null, null, null);
    	Map<String, String> beerColumns;
    	Map<String, String> drinkColumns;
    	int numCols = 0;
    	
    	if (allDrinks.size() == 0)
    		return null;

    	StringBuilder csvData = new StringBuilder();
    	
    	Drink someDrink = allDrinks.get(0);
    	Beer someBeer = dbs.getFavoriteBeer();

    	if (someDrink == null || someBeer == null)
    		return null;
    	
    	// Write the headers for drink objects
    	if (someDrink != null) {
	    	drinkColumns = someDrink.getExportMap();
    		numCols += drinkColumns.size();
    		
	    	for (String colname: drinkColumns.keySet()) {
				csvData.append( enquote(colname) );
				csvData.append(",");
			}
    	} else {
    		Log.e("Export", "firstDrink is null");
    		return null;
    	}

    	// Write headers for beer objects
    	if (someBeer != null) {
        	int col = numCols;
	    	beerColumns = someBeer.getExportMap();
    		numCols += beerColumns.size();
    		
	    	for (String colname: beerColumns.keySet()) {
				csvData.append( enquote(colname) );
				
				if (++col < numCols)
					csvData.append(",");
	    	}
	    	
	    	csvData.append("\n");
    	} else {
    		Log.e("Export", "someBeer is null");
    		return null;
    	}
    	
    	// Write the data
    	for (Drink drink : allDrinks) {
        	int col = 0;

        	for (String colname: drinkColumns.keySet()) {
        		String attr = drinkColumns.get(colname);
    			String val = drink.get(attr);

    			csvData.append( enquote(val) );
    			csvData.append(",");
    			
    			++col;
    		}
    		
        	// Get this beer
    		int beerId = drink.getBeerId();
    		Beer beer = dbs.getBeer(beerId);
    		
        	// Write data for beer
        	if (beer != null) {
    	    	for (String colname: beerColumns.keySet()) {
    	    		String attr = beerColumns.get(colname);
    	    		String val = beer.get(attr);

	    			csvData.append( enquote(val) );
	
	    			if (++col < numCols)
	        			csvData.append(",");
    	    	}
    		}    		

			csvData.append("\n");
    	}
    	
    	return writeCsvDataToFile(csvData.toString());
    }

    private Uri writeCsvDataToFile(String csvData) {
    	// Write the csv to a file on the external storage 
    	File csvFile = new File(DB_CSV);
    	try {
			csvFile.createNewFile();
		} catch (IOException e) {
			// If it fails, we probably can't create a file there
			e.printStackTrace();
			return null;
		}
    	
    	try {
			FileOutputStream csvFileStream = new FileOutputStream(csvFile);
			PrintWriter csvWriter = new PrintWriter(csvFileStream);
			csvWriter.write(csvData);
			csvWriter.close();
			csvFileStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
    	return Uri.parse("file://" + csvFile.getAbsolutePath());
    }
    
	public long localCsvModifiedDate() {
		File localCsv = new File(DB_CSV);
		if (localCsv.exists())
			return localCsv.lastModified();
		
		return 0;
	}

	public void setDbs(BeerDbHelper dbs) {
		this.dbs = dbs;
	}
}

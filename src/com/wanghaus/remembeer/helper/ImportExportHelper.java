package com.wanghaus.remembeer.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

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
    	BufferedReader inFile;
    	String iLine;
    	Integer count = new Integer(0);
    	
    	try {
    		inFile = new BufferedReader(new FileReader(DB_CSV));
    	} catch(FileNotFoundException e) {
        	try {
        		inFile = new BufferedReader(new FileReader(DB_CSV_LEGACY));
        	} catch(FileNotFoundException e2) {
        		return -1;
        	}
    	}
    	
    	try {
			if (!inFile.readLine().equalsIgnoreCase("\"beername\",\"container\",\"stamp\",\"rating\""))
				// The header in the file wasn't right, so we're out of here
				return -1;
		} catch (IOException e) {
			Log.e("importHistory", e.toString());
		}

    	try {    		
    		iLine = inFile.readLine(); // read line from file
    		while(iLine != null){
    			Log.v("importLine", iLine);
    			String[] elements = iLine.split(",");
    			
    			for (int i=0; i<elements.length; ++i)
    				elements[i] = elements[i].replaceAll("^\"", "").replaceAll("\"$", "");

    			if (dbs.getDrinkCountWhen(elements[2]) == 0) {
    				Beer beer = dbs.findBeerBySubstring(elements[0]);
    				if (beer != null) {
    					Drink drink = new Drink(beer, elements[1], elements[2], null);
    					drink.setRating(Integer.valueOf(elements[3]));
    					dbs.updateOrAddDrink(drink);
        				count++;
    				}
    			}
    			iLine = inFile.readLine();
    		}
    		inFile.close();
    	} catch(Exception e){
    		Log.e("importHistory", "Failed to import history csv", e);
    	}
    	Log.d("ImportHistory", "Imported " + count.toString() + " Beers");
    	
		return count; 
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
        		if (colname != null)
					colname = colname.replaceAll("\"", "\\\""); // escape quotes
				csvData.append("\"");
				csvData.append(colname);
				csvData.append("\"");
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
        		if (colname != null)
					colname = colname.replaceAll("\"", "\\\""); // escape quotes
				csvData.append("\"");
				csvData.append(colname);
				csvData.append("\"");
				
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
    			if (val != null)
    				val = val.replaceAll("\"", "\\\""); // escape quotes
    			else
    				val = "";
    			csvData.append("\"");
    			csvData.append(val);
    			csvData.append("\"");
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
	    			if (val != null)
	    				val = val.replaceAll("\"", "\\\""); // escape quotes
	    			else
	    				val = "";
	    			csvData.append("\"");
	    			csvData.append(val);
	    			csvData.append("\"");
	
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

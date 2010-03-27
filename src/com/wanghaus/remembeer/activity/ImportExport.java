package com.wanghaus.remembeer.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.service.BeerDbService;

public class ImportExport extends Activity {
	private BeerDbService dbs;
	private Context context;
	
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.importexport);
        
        if (dbs == null)
    		dbs = new BeerDbService(this);

        context = this;
        UpdateLastExported();
        
        setTitle(R.string.import_title);
        
        Button exportEmail = (Button) findViewById(R.id.ExportEmail);
        exportEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Uri csvFile = dbs.exportHistoryToCsvFile();
            	if (csvFile == null) {
            		// TODO - Show an error message
            		Toast.makeText(context, getString(R.string.export_whoops), Toast.LENGTH_LONG);
            		return;
            	}
            	
            	// Create an email
            	final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Beer Log export");
            	
            	// Attach the CSV
            	emailIntent.setType("plain/csv");
            	emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, csvFile);
            	
            	// Send
            	startActivity(Intent.createChooser(emailIntent, "Send export..."));
            	
            	UpdateLastExported();
            }
        });
        Button exportLocal = (Button) findViewById(R.id.ExportLocal);
        exportLocal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Uri destination;
            	String success;
            	
            	//Store it
            	destination = dbs.exportHistoryToCsvFile();
            	if (destination == null)
            		Toast.makeText(context, getString(R.string.export_whoops), Toast.LENGTH_LONG);
            	
            	success = getString(R.string.export_success);
            	success.concat(destination.toString());
            	Toast.makeText(context, success, Toast.LENGTH_LONG);
            	UpdateLastExported();
            }
        });
        Button importLocal = (Button) findViewById(R.id.ImportLocal);
        importLocal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	// Throw a spinner up while we're doing this
            	final ProgressDialog throbber = ProgressDialog.show(context, "",
            			getText(R.string.import_throbber), true);

            	// Authorize out-of-thread so spinner can actually run
        	    Thread importThread = new Thread() {
        	        public void run() {
        	        	dbs.importHistoryFromCsvFile();
        				Log.d("Import", "importThread ending");
        				throbber.dismiss();
        	        }
        	    };
        	    importThread.start();

            }
        });
        Button exportDone = (Button) findViewById(R.id.ExportDone);
        exportDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	finish();
            }
        });
	}

	protected void UpdateLastExported() {
		long when;
		
		when = dbs.localCsvModifiedDate();
		if (when > 0) {
			Date date = new Date(when);
			
			// Format the date to look like "stamp" from the BeerDbService
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			TextView exportDate = (TextView) findViewById(R.id.ExportDate);
			exportDate.setText(sdf.format(date));
		}
        
	};
}

package com.wanghaus.remembeer.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.service.BeerDbService;

public class MoreInfo extends BaseActivity {
		private List<BeerInfo> infos = new ArrayList<BeerInfo>();
		
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	    	super.onCreate(savedInstanceState);
	        setContentView(R.layout.moreinfo);
	        
	        BeerDbService dbs = new BeerDbService(this);
	        // See if we were passed a beer name
        	String beername = getIntent().getStringExtra("beername");
        	// TODO: add a menu option to search for a new beername to display the info on
	        
	        infos.add( new BeerInfo("Count", String.valueOf(dbs.getBeerCount(beername))) );
	        infos.add( new BeerInfo("ABV", String.valueOf(dbs.getBeerABV(beername))));
	        infos.add( new BeerInfo("Brewery", dbs.getBeerBrewer(beername)));
	        infos.add( new BeerInfo("Location", dbs.getBeerBrewerLocation(beername)));
	        infos.add( new BeerInfo("Notes", dbs.getBeerNotes(beername)));
	        
	        dbs.close();
	        
	        ListView statList = (ListView) findViewById(R.id.stats_list);
	        ArrayAdapter<BeerInfo> adapter = new BeerInfoListAdapter(this);
	        statList.setAdapter(adapter);
	    }
	    
	    private class BeerInfo {
	    	public String name;
	    	public String value;
			@SuppressWarnings("unused")
			public BeerInfo() {
	    		super();
	    	}
	    	public BeerInfo(String name, String value) {
	    		this.name = name;
	    		this.value = value;
	    	}
	    }
	    
	    private class BeerInfoListAdapter extends ArrayAdapter<BeerInfo> {
	    	Activity context;
	    	
	    	public BeerInfoListAdapter(Activity context) {
	    		super(context, R.layout.chartslist_row);
	    		
	    		for (BeerInfo e : infos) {
	    			add(e);
	    		}
	    		
	    		this.context = context;
	    	}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row;
				
				LayoutInflater inflater = context.getLayoutInflater();
				row = inflater.inflate(R.layout.stats_row, null);

				BeerInfo e = getItem(position);
				
				TextView label = (TextView)row.findViewById(R.id.label);
				label.setText( e.name );

				TextView value = (TextView)row.findViewById(R.id.value);
				value.setText( e.value );
				
				return row;
			}
	    }
	}

package com.wanghaus.beerlog.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wanghaus.beerlog.R;

public class Stats extends BaseActivity {
	private Map<String, String> stats = new HashMap<String, String>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);
        
        stats.put("Number of beers drunk", "100");
        stats.put("Favorite beer", "Something or other");
        stats.put("Favorite drinking hour", "8pm");
        
        ListView statList = (ListView) findViewById(R.id.stats_list);
        ArrayAdapter<Map.Entry<String, String>> adapter = new BeerStatListAdapter(this);
        statList.setAdapter(adapter);
    }
    
    private class BeerStatListAdapter extends ArrayAdapter<Entry<String, String>> {
    	Activity context;
    	
    	public BeerStatListAdapter(Activity context) {
    		super(context, R.layout.chartslist_row);
    		
    		for (Entry<String, String> e : stats.entrySet()) {
    			add(e);
    		}
    		
    		this.context = context;
    	}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = context.getLayoutInflater();
			View row = inflater.inflate(R.layout.stats_row, null);

			Entry<String, String> e = getItem(position);
			
			TextView label = (TextView)row.findViewById(R.id.label);
			label.setText( e.getKey() );

			TextView value = (TextView)row.findViewById(R.id.value);
			value.setText( e.getValue() );

			return(row);
		}
    }
}

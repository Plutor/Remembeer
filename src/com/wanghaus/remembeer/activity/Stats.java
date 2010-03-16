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

public class Stats extends BaseActivity {
	private List<BeerStat> stats = new ArrayList<BeerStat>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);
        
        BeerDbService dbs = new BeerDbService(this);
        stats.add( new BeerStat( getText(R.string.stat_beerCount), String.valueOf(dbs.getBeerCount())) );
        stats.add( new BeerStat( getText(R.string.stat_favoriteBeer), dbs.getFavoriteBeer()) );
        stats.add( new BeerStat( getText(R.string.stat_mostDrunkBeer), dbs.getMostDrunkBeer()) );
        stats.add( new BeerStat( getText(R.string.stat_favoriteDrinkingHour), dbs.getFavoriteDrinkingHour()) );
        dbs.close();
        
        ListView statList = (ListView) findViewById(R.id.stats_list);
        ArrayAdapter<BeerStat> adapter = new BeerStatListAdapter(this);
        statList.setAdapter(adapter);
    }
    
    private class BeerStat {
    	public CharSequence name;
    	public String value;
    	public BeerStat() {
    		super();
    	}
    	public BeerStat(CharSequence name, String value) {
    		this.name = name;
    		this.value = value;
    	}
    }
    
    private class BeerStatListAdapter extends ArrayAdapter<BeerStat> {
    	Activity context;
    	
    	public BeerStatListAdapter(Activity context) {
    		super(context, R.layout.chartslist_row);
    		
    		for (BeerStat e : stats) {
    			add(e);
    		}
    		
    		this.context = context;
    	}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row;
			
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.stats_row, null);

			BeerStat e = getItem(position);
			
			TextView label = (TextView)row.findViewById(R.id.label);
			label.setText( e.name );

			TextView value = (TextView)row.findViewById(R.id.value);
			value.setText( e.value );
			
			return row;
		}
    }
}

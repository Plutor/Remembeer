package com.wanghaus.beerlog.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wanghaus.beerlog.R;
import com.wanghaus.beerlog.stats.BeerStat;
import com.wanghaus.beerlog.stats.BeerTypes;
import com.wanghaus.beerlog.stats.BeersPerMonth;
import com.wanghaus.beerlog.stats.Containers;
import com.wanghaus.beerlog.stats.FavoriteBrands;

public class BeerStats extends BaseActivity {
	private BeerStat[] stats = new BeerStat[]{
			new BeersPerMonth(),
			new Containers(),
			new BeerTypes(),
			new FavoriteBrands()
		};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);
        
        ListView statList = (ListView) findViewById(R.id.stats_list);
        ArrayAdapter<BeerStat> adapter = new BeerStatListAdapter(this);
        statList.setAdapter(adapter);
    }
    
    private class BeerStatListAdapter extends ArrayAdapter<BeerStat> {
    	Activity context;
    	
    	public BeerStatListAdapter(Activity context) {
    		super(context, R.layout.stats_list_row, stats);
    		this.context = context;
    	}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = context.getLayoutInflater();
			View row = inflater.inflate(R.layout.stats_list_row, null);

			TextView label = (TextView)row.findViewById(R.id.label);
			label.setText( stats[position].getName() );

			ImageView icon = (ImageView)row.findViewById(R.id.icon);
			icon.setImageResource( stats[position].getThumbnailRef() );

			return(row);
		}
    	
    		
    }
}

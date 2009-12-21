package com.wanghaus.beerlog.activity;

import java.text.DateFormat;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.wanghaus.beerlog.R;
import com.wanghaus.beerlog.stats.BaseStat;
import com.wanghaus.beerlog.stats.BeerTypes;
import com.wanghaus.beerlog.stats.BeersPerMonth;
import com.wanghaus.beerlog.stats.Containers;
import com.wanghaus.beerlog.stats.FavoriteBrands;

public class StatsList extends BaseActivity {
	private BaseStat[] stats = new BaseStat[]{
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
        ArrayAdapter<BaseStat> adapter = new BeerStatListAdapter(this);
        statList.setAdapter(adapter);
    }
    
    private class BeerStatListAdapter extends ArrayAdapter<BaseStat> {
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

			row.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					openViewStat(view);
				}
			});
			return(row);
		}
    }

    private void openViewStat(View statToView) {
    	Intent nextIntent = new Intent(this, ViewStat.class);

    	Bundle bundle = new Bundle();
    	// TODO - send the right stat name in the bundle
    	bundle.putString("statToView", statToView.toString());
    	nextIntent.putExtras(bundle);

    	startActivity(nextIntent);
    }
}

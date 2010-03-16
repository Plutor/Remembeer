package com.wanghaus.remembeer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.charts.BaseChart;
import com.wanghaus.remembeer.charts.BeerTypes;
import com.wanghaus.remembeer.charts.BeersPerMonth;
import com.wanghaus.remembeer.charts.Containers;
import com.wanghaus.remembeer.charts.FavoriteBrands;

public class ChartsList extends BaseActivity {
	private BaseChart[] stats = new BaseChart[]{
			new BeersPerMonth(),
			new Containers(),
			new BeerTypes(),
			new FavoriteBrands()
		};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.chartslist);
        
        ListView statList = (ListView) findViewById(R.id.charts_list);
        ArrayAdapter<BaseChart> adapter = new BeerChartListAdapter(this);
        statList.setAdapter(adapter);
    }
    
    private class BeerChartListAdapter extends ArrayAdapter<BaseChart> {
    	Activity context;
    	
    	public BeerChartListAdapter(Activity context) {
    		super(context, R.layout.chartslist_row, stats);
    		this.context = context;
    	}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = context.getLayoutInflater();
			View row = inflater.inflate(R.layout.chartslist_row, null);

			TextView label = (TextView)row.findViewById(R.id.label);
			label.setText( stats[position].getName() );

			ImageView icon = (ImageView)row.findViewById(R.id.icon);
			icon.setImageResource( stats[position].getThumbnailRef() );

			row.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					openViewStat(view);
				}
			});
			return(row);
		}
    }

    private void openViewStat(View statToView) {
    	Intent nextIntent = new Intent(this, ViewChart.class);

    	Bundle bundle = new Bundle();
    	// TODO - send the right stat name in the bundle
    	bundle.putString("statToView", statToView.toString());
    	nextIntent.putExtras(bundle);

    	startActivity(nextIntent);
    }
}

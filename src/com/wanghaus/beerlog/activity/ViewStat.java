package com.wanghaus.beerlog.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.wanghaus.beerlog.R;
import com.wanghaus.beerlog.stats.BaseStat;
import com.wanghaus.beerlog.stats.BeersPerMonth;

public class ViewStat extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.viewstat);
        
        ImageView image = (ImageView) findViewById(R.id.viewstat_image);
        BaseStat statToView = new BeersPerMonth(); // TODO - Get the right object based on the Bundle

        // This represents a poor understanding of drawables and images.
        // TODO - Understand it better
        int width = image.getWidth();
        int height = image.getHeight();
        image.setImageDrawable( statToView.getImage(width, height) );
    }
}

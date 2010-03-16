package com.wanghaus.remembeer.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.charts.BaseChart;
import com.wanghaus.remembeer.charts.BeersPerMonth;

public class ViewChart extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.viewchart);
        
        ImageView image = (ImageView) findViewById(R.id.viewstat_image);
        BaseChart statToView = new BeersPerMonth(); // TODO - Get the right object based on the Bundle

        // This represents a poor understanding of drawables and images.
        // TODO - Understand it better
        int width = image.getWidth();
        int height = image.getHeight();
        image.setImageDrawable( statToView.getImage(width, height) );
    }
}

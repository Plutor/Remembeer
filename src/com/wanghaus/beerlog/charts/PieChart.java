package com.wanghaus.beerlog.charts;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

public abstract class PieChart extends BaseChart {
	public PieChart() {
		super();
		setType("pie");
	}
	
	@Override
	public Drawable getImage(int width, int height) {
        Drawable image = new ShapeDrawable(new OvalShape());

        //image.getPaint().setColor(0xff74AC23);
        image.setBounds(0, 0, width, height);
        //image.setBounds(x, y, x + width, y + height);

        return image;
	}
}

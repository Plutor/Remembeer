package com.wanghaus.beerlog.stats;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

public abstract class BarStat extends BaseStat {
	public BarStat() {
		super();
		setType("bar");
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

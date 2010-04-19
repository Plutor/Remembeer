package com.wanghaus.remembeer.activity;

import android.os.Bundle;

import com.wanghaus.remembeer.R;
import com.wanghaus.remembeer.helper.BeerDbHelper;

public class BeerInfo extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beerinfo);

        setTitle( "Edit beer info" ); // XXX

		BeerDbHelper dbs = new BeerDbHelper(this);

		// TODO - we need to accept a beerId or a drinkId
	}
}

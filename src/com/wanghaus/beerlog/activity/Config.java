package com.wanghaus.beerlog.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.wanghaus.beerlog.R;

public class Config extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	addPreferencesFromResource(R.xml.preferences);
    }
}

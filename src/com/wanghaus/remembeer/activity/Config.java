package com.wanghaus.remembeer.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.wanghaus.remembeer.R;

public class Config extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	addPreferencesFromResource(R.xml.preferences);
    }
}

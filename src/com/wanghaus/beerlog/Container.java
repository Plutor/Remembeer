package com.wanghaus.beerlog;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class Container extends TabActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
        TabHost host = getTabHost();
       
        host.addTab(host.newTabSpec("tab1").setIndicator("Add beer").setContent(new Intent(this, AddBeer.class)));  
        host.addTab(host.newTabSpec("tab2").setIndicator("Stats").setContent(new Intent(this, BeerStats.class)));  
        host.addTab(host.newTabSpec("tab3").setIndicator("Config").setContent(new Intent(this, Config.class)));

        try {
        	String selectedTab = getIntent().getExtras().getString("selectedTab");
        	host.setCurrentTabByTag(selectedTab);
        } catch (Exception e) {
        	// Nothing, default to first tab
        }
    }
}

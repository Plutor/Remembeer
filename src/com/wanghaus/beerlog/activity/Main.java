package com.wanghaus.beerlog.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class Main extends TabActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
        TabHost host = getTabHost();
       
        host.addTab(host.newTabSpec("addbeer").setIndicator("Add beer").setContent(new Intent(this, AddBeer.class)));  
        host.addTab(host.newTabSpec("history").setIndicator("History").setContent(new Intent(this, History.class)));  
        host.addTab(host.newTabSpec("stats").setIndicator("Stats").setContent(new Intent(this, BeerStats.class)));  

        try {
        	String selectedTab = getIntent().getExtras().getString("selectedTab");
        	host.setCurrentTabByTag(selectedTab);
        } catch (Exception e) {
        	// Nothing, default to first tab
        }
    }
}

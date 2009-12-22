package com.wanghaus.beerlog.activity;

import com.wanghaus.beerlog.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class Main extends TabActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
        TabHost host = getTabHost();
       
        host.addTab(host.newTabSpec("addbeer")
        		.setIndicator("Add beer", getResources().getDrawable(R.drawable.addbeer))
        		.setContent(
	        		new Intent(this, AddBeer.class).putExtras(getIntent())
	    		));  
        host.addTab(host.newTabSpec("history")
        		.setIndicator("History", getResources().getDrawable(R.drawable.history))
        		.setContent(
	        		new Intent(this, History.class).putExtras(getIntent())
				));  
        host.addTab(host.newTabSpec("stats")
        		.setIndicator("Stats", getResources().getDrawable(R.drawable.stats))
        		.setContent(
	        		new Intent(this, StatsList.class).putExtras(getIntent())
				));  

        try {
        	String selectedTab = getIntent().getExtras().getString("selectedTab");
        	host.setCurrentTabByTag(selectedTab);
        } catch (Exception e) {
        	// Nothing, default to first tab
        }
    }
}

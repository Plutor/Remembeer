package com.wanghaus.beerlog;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class Container extends TabActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
        TabHost host = getTabHost();  
        host.addTab(host.newTabSpec("tab1").setIndicator("Add beer").setContent(new Intent(this, AddBeer.class)));  
        host.addTab(host.newTabSpec("tab2").setIndicator("Stats").setContent(new Intent(this, BeerStats.class)));  
        host.addTab(host.newTabSpec("tab3").setIndicator("Config").setContent(new Intent(this, Config.class)));  
    }
}

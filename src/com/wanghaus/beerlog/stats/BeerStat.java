package com.wanghaus.beerlog.stats;

import com.wanghaus.beerlog.R;

public abstract class BeerStat {
	private String name;
	private String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public int getThumbnailRef() {
		if (type != null && type.equals("pie"))
			return R.drawable.piechart_thumb;
		
		return R.drawable.barchart_thumb;
	}
}

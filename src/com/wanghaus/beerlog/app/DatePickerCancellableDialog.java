package com.wanghaus.beerlog.app;

import android.app.DatePickerDialog;
import android.content.Context;

public class DatePickerCancellableDialog extends DatePickerDialog {
	private Context context;
	
	public DatePickerCancellableDialog(Context context,
			OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
		
		this.context = context;
	}

	public void setOnCancelListener(OnClickListener listener) {
		setButton2(context.getText(android.R.string.cancel), listener);		
	}
}

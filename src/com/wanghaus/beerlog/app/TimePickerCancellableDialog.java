package com.wanghaus.beerlog.app;

import android.app.TimePickerDialog;
import android.content.Context;

public class TimePickerCancellableDialog extends TimePickerDialog {
	private Context context;
	
	public TimePickerCancellableDialog(Context context,
			OnTimeSetListener callBack, int hourOfDay, int minute,
			boolean is24HourView) {
		super(context, callBack, hourOfDay, minute, is24HourView);
		this.context = context;
	}

	public void setOnCancelListener(OnClickListener listener) {
		setButton2(context.getText(android.R.string.cancel), listener);		
	}
}

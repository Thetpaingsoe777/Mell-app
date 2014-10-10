package com.xavey.app.dialogs;

import java.util.Calendar;

import com.xavey.app.ApplicationValues;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimePickerFragment extends DialogFragment implements
		OnTimeSetListener {

	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int hour = 0;
		int minute = 0;

		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	@Override
	public void onTimeSet(TimePicker arg0, int hour, int min) {
		// TODO Auto-generated method stub
		
//		Toast.makeText(getActivity(), "hour : "+hour+"\nmin : "+min, 1000).show();
//		ApplicationValues.myCalendar.set(Calendar.HOUR, hour);
//		ApplicationValues.myCalendar.set(Calendar.MINUTE, min);
	}
	
	

}

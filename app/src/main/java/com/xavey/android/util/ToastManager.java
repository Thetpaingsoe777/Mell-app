package com.xavey.android.util;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xavey.android.R;

public class ToastManager {
	Activity act_;
	
	public ToastManager(Activity activity) {
		act_ = activity;
	}
	
	public void xaveyToast(TextView textView, String msg){
		LayoutInflater inflater = act_.getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_toast,
				(ViewGroup) act_.findViewById(R.id.custom_toast_layout_id));
		TextView tv = (TextView)layout.findViewById(R.id.tvGreenToastMsg);
		tv.setText(msg);
		try {
			Toast toast = new Toast(act_);
			toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
			toast.setDuration(5000);
			toast.setView(layout);
			toast.show();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}

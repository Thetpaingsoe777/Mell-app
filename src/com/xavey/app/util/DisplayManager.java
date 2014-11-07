package com.xavey.app.util;

import android.app.Activity;
import android.util.DisplayMetrics;

public class DisplayManager {

	DisplayMetrics dm;
	protected int width;
	protected int height;

	public DisplayManager(Activity activity) {
		dm = activity.getResources().getDisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
	}

	public int getWidth(int percent) {
		return (width*percent)/100;
	}

	public int getHeigth(int percent) {
		return (height*percent)/100;
	}
}

package com.xavey.app.util;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {

	
	public static String PREFERENCE_NAME = "XAVEY_PREF";
	public static String PATH = "path";
	public static String Sign_Pathe = "SignPath";
	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	public static void setImage_Path(Context context ,String path) {
		SharedPreferences sharpreference =context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor=sharpreference.edit();
		editor.putString(PATH, path);  
		editor.commit();
	}

	public static String getImage_Path(Context context){
		SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME ,Context.MODE_WORLD_READABLE);

		return pref.getString(PATH, "");
		
	}
	
	public static void setSign_Path(Context context, String Path){
		SharedPreferences pref= context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor ed = pref.edit();
		ed.putString(Sign_Pathe, Path);
		ed.commit();
		
	}
	public static String getSign_Path(Context context){
		SharedPreferences pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_WORLD_READABLE);
		return pref.getString(Sign_Pathe, "");
		
	}
	

	/**
	 * Generate a value suitable for use in {@link #setId(int)}.
	 * This value will not collide with ID values generated at build time by aapt for R.id.
	 *
	 * @return a generated ID value
	 */
	public static int generateViewId() {
	    for (;;) {
	        final int result = sNextGeneratedId.get();
	        // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
	        int newValue = result + 1;
	        if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
	        if (sNextGeneratedId.compareAndSet(result, newValue)) {
	            return result;
	        }
	    }
	}
}


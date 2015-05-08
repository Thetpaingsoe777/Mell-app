package com.xavey.android.util;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.SharedPreferences;

import com.xavey.android.ApplicationValues;
import com.xavey.android.model.FONT;
import com.xavey.android.model.SYNC;

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

    public static void setAppPreference(Context conx){

        SharedPreferences sharedPreferences = conx.getSharedPreferences(ApplicationValues.preferenceKey,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (ApplicationValues.CURRENT_FONT) {
            case DEFAULT_:
                editor.putString(ApplicationValues.prefFontKey, ApplicationValues.prefFontDefaultKey);
                editor.commit();
                break;
            case ZAWGYI:
                editor.putString(ApplicationValues.prefFontKey, ApplicationValues.prefFontZawgyiKey);
                editor.commit();
                break;
            case MYANMAR3:
                editor.putString(ApplicationValues.prefFontKey, ApplicationValues.prefFontMyanmarKey);
                editor.commit();
                break;
            default:
                break;
        }
        switch (ApplicationValues.CURRENT_SYNC){
            case OFF:
                editor.putString(ApplicationValues.prefSyncKey,ApplicationValues.prefSyncOffKey);
                editor.commit();
                break;
            case AUTO_SYNC:
                editor.putString(ApplicationValues.prefSyncKey,ApplicationValues.prefSyncOnKey);
                editor.commit();
                break;
            default:
                break;
        }
    }

    public static void setAppValueFromPreference(Context conx){

        SharedPreferences sharedPreferences = conx.getSharedPreferences(ApplicationValues.preferenceKey,
                Context.MODE_PRIVATE);

        if(sharedPreferences.getString(ApplicationValues.prefFontKey, ApplicationValues.prefFontDefaultKey).equals(ApplicationValues.prefFontDefaultKey)) {
            ApplicationValues.CURRENT_FONT = FONT.DEFAULT_;
        }
        else if(sharedPreferences.getString(ApplicationValues.prefFontKey, ApplicationValues.prefFontDefaultKey).equals(ApplicationValues.prefFontZawgyiKey)) {
            ApplicationValues.CURRENT_FONT = FONT.ZAWGYI;
        }
        else if(sharedPreferences.getString(ApplicationValues.prefFontKey, ApplicationValues.prefFontDefaultKey).equals(ApplicationValues.prefFontMyanmarKey)) {
            ApplicationValues.CURRENT_FONT = FONT.MYANMAR3;
        }
        else{
            ApplicationValues.CURRENT_FONT = FONT.DEFAULT_;
        }

        if(sharedPreferences.getString(ApplicationValues.prefSyncKey, ApplicationValues.prefSyncOffKey).equals(ApplicationValues.prefSyncOffKey)) {
            ApplicationValues.CURRENT_SYNC = SYNC.OFF;
        }
        else if(sharedPreferences.getString(ApplicationValues.prefSyncKey, ApplicationValues.prefSyncOffKey).equals(ApplicationValues.prefSyncOnKey)) {
            ApplicationValues.CURRENT_SYNC = SYNC.AUTO_SYNC;
        }
        else{
            ApplicationValues.CURRENT_SYNC = SYNC.OFF;
        }
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


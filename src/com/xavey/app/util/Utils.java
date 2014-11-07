package com.xavey.app.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {

	
	public static String PREFERENCE_NAME = "XAVEY_PREF";
	public static String PATH = "path";
	public static String Sign_Pathe = "SignPath";

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
}


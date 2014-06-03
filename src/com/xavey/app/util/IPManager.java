package com.xavey.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class IPManager {

	SharedPreferences pref;

	Editor editor;

	Context _context;

	private static final String PREF_NAME = "ipPref";
	
	int PRIVATE_MODE = 0;

	public static final String KEY_IP = "ipaddress";

	public IPManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void saveServerIPAddress(String ipAddress) {
		editor.putString(KEY_IP, ipAddress);
		editor.commit();
	}

	public String getServerIPAddress(){
		return pref.getString(KEY_IP, "none");
	}
}

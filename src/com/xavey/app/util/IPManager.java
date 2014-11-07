package com.xavey.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import com.xavey.app.LoginActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class IPManager {

	SharedPreferences pref;
	Editor editor;
	Context _context;
	private static final String PREF_NAME = "ipPref";
	int PRIVATE_MODE = 0;
	public static final String KEY_IP = "ipaddress";

	// the first IP address will be initialized from xave_properties.properties file
	Properties properties;
	String serverIP = "serverIP";
	
	public IPManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();

		// properties stuffs
		properties = new Properties();
		InputStream in = LoginActivity.class
				.getResourceAsStream("/com/xavey/app/util/xavey_properties.properties");
		try {
			properties.load(new InputStreamReader(in, "UTF-8"));
			serverIP = properties.getProperty(serverIP);
			// serverIP variable is in order to return Server IP as default value
		} catch (UnsupportedEncodingException e) {
			Toast.makeText(_context, "Unspported Encoding", 1000)
					.show();
		} catch (IOException e) {
			Toast.makeText(_context, "IOException", 1000).show();
		}
	}

	public void saveServerIPAddress(String ipAddress) {
		editor.putString(KEY_IP, ipAddress);
		editor.commit();
	}

	public String getServerIPAddress() {
		return pref.getString(KEY_IP, serverIP);
	}
}

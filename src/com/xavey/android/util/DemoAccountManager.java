package com.xavey.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;

public class DemoAccountManager {
	Activity activity;

	public DemoAccountManager(Activity activity) {
		this.activity = activity;
	}

	public String getDataFromAssets(String assetPath) {
		// param should be like book/contents.json
		StringBuilder buf = new StringBuilder();
		String str="";
		InputStream json;
		try {
			json = activity.getAssets().open(assetPath);

			BufferedReader in = new BufferedReader(new InputStreamReader(json,
					"UTF-8"));

			while ((str = in.readLine()) != null) {
				buf.append(str);
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return str;
	}
	
	
	
}

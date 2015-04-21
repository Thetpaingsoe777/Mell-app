package com.xavey.android;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import com.xavey.android.db.XaveyDBHelper;

import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {
	
	String about_text = "";
    String version_name="";
    String version_code="";
//	TextView text;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        Context conx = getActivity().getApplicationContext();
        PackageInfo pInfo = null;
        try {
            pInfo = conx.getPackageManager().getPackageInfo(conx.getPackageName(), 0);
            version_name = pInfo.versionName;
            version_code = String.valueOf(pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        View rootView = inflater.inflate(R.layout.about_fragment, container, false);
		TextView text = (TextView) rootView.findViewById(R.id.about_version);
		text.setText("Version: " + version_name);
		getActivity().getActionBar().setIcon(R.drawable.about);
		getActivity().getActionBar().setTitle("About");
		return rootView;
	}

	private void loadProperties() throws FileNotFoundException {
		Properties properties = new Properties();
		InputStream in = new FileInputStream("about.properties");
		try {
            Context conx = getActivity().getApplicationContext();
            ContextWrapper cw = new ContextWrapper(conx);
            Log.i("", cw.getFilesDir().toString());
            PackageInfo pInfo = conx.getPackageManager().getPackageInfo(conx.getPackageName(), 0);
            version_name = pInfo.versionName;
            version_code = String.valueOf(pInfo.versionCode);
            //properties.load(new InputStreamReader(in, "UTF-8"));
			//about_text = properties.getProperty("about_text");
		} catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}

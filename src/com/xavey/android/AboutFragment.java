package com.xavey.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment extends Fragment {
	
	String about_text = "";
//	TextView text;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		loadProperties();
		View rootView = inflater.inflate(R.layout.about_fragment, container, false);
//		text = (TextView) rootView.findViewById(R.id.tvAboutText);
//		text.setText(about_text);
		getActivity().getActionBar().setIcon(R.drawable.about);
		getActivity().getActionBar().setTitle("About");
		return rootView;
	}

	private void loadProperties(){
		Properties properties = new Properties();
		InputStream in = LoginActivity.class
				.getResourceAsStream("/com/xavey/android/util/about.properties");
		try {
			properties.load(new InputStreamReader(in, "UTF-8"));
			about_text = properties.getProperty("about_text");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

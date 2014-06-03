package com.xavey.app;

import java.util.HashMap;

import com.xavey.app.util.SessionManager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {
	
	public static final String ITEM_NAME = "Home_";
	SessionManager session;
	
	Activity activity;
	TextView form1Icon, form2Icon, welcomeText;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.home_fragment, container,
				false);
		loadUI(view);
		session = new SessionManager(activity.getApplicationContext());
		
		HashMap<String, String> user = session.getUserDetails();
		welcomeText.setText("Welcome "+user.get(SessionManager.KEY_NAME)+"!");
		
		form1Icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(activity,FormDetailActivity.class);
				i.putExtra("form", "form1.json");
				startActivity(i);
			}
		});
		form2Icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(activity,FormDetailActivity.class);
				i.putExtra("form", "form2.json");
				startActivity(i);
			}
		});
		
		return view;
	}
	
	private void loadUI(View v){
		activity = getActivity();
		form1Icon = (TextView) v.findViewById(R.id.form1Icon);
		form2Icon = (TextView) v.findViewById(R.id.form2Icon);
		welcomeText = (TextView) v.findViewById(R.id.tvWelcomeText);
	}
	
	
}

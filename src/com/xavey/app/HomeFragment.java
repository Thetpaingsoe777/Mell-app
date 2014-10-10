package com.xavey.app;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.xavey.app.adapter.FormAdapter;
import com.xavey.app.adapter.FormAdapter.ViewHolder;
import com.xavey.app.db.XaveyDBHelper;
import com.xavey.app.model.Form;
import com.xavey.app.testing.TestActivity;
import com.xavey.app.util.SessionManager;

public class HomeFragment extends Fragment implements OnItemClickListener{

	public static final String ITEM_NAME = "Home_";
	SessionManager session;
	GridView formGridView;
	Activity activity;
	TextView welcomeText;
	FormAdapter formAdapter;
	ArrayList<Form> formList;
	XaveyDBHelper dbHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.home_fragment, container,
				false);

		loadUI(view);
		activity.getActionBar().setIcon(R.drawable.home);
		activity.getActionBar().setTitle("Home");
		refresh();
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refresh();
	}

	private void refresh(){
		//formList = dbHelper.getAllForms(); // dbHelper.getUserRelatedForm
		formList = dbHelper.getAssignedFormsByUserID(MainActivity.LOGIN_USER_ID);
		formAdapter = new FormAdapter(getActivity(), formList);
		formGridView.setAdapter(formAdapter);
		formGridView.setOnItemClickListener(this);
	}

	private void loadUI(View v){
		activity = getActivity();
		welcomeText = (TextView) v.findViewById(R.id.tvWelcomeText);
		formGridView = (GridView) v.findViewById(R.id.gridViewHOME);
		dbHelper = new XaveyDBHelper(getActivity().getApplicationContext());
		if(isTablet(v.getContext()))
			formGridView.setNumColumns(3);
		else
			formGridView.setNumColumns(2);
		
		setWelcomeText();
	}

	public static boolean isTablet(Context context) {
	    return (context.getResources().getConfiguration().screenLayout
	            & Configuration.SCREENLAYOUT_SIZE_MASK)
	            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	private void setWelcomeText(){
		session = new SessionManager(activity.getApplicationContext());
		HashMap<String, String> user = session.getUserDetails();
		welcomeText.setText("Welcome "+user.get(SessionManager.KEY_NAME)+"!");
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		//Intent i = new Intent(activity, DocumentInputActivity.class);
		Intent i = new Intent(activity, OneQuestionOneView.class);
		ViewHolder holder = (ViewHolder)view.getTag();
		String id_from_holder = holder.getFormID();
		i.putExtra("formID", id_from_holder);
		startActivity(i);
	}
	
}

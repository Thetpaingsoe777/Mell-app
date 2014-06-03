package com.xavey.app;

import com.xavey.app.model.Form;
import com.xavey.app.util.ConnectionDetector;
import com.xavey.app.util.JSONReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Toast;

public class FormDetailActivity extends Activity {
	
	LinearLayout lL;
	ScrollView scrollView;
	JSONReader jsonReader;
	Button btnSubmit;
	Intent i;
	String formName;
	
	//Form stuffs (my thinking)
	Form form;
	
	//internet conectivity stuffs
	Boolean isInternetAvailable = false;
	ConnectionDetector connectionDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form_detail_screen);
		loadUI();
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isInternetAvailable = connectionDetector.isConnectingToInternet();
				if(isInternetAvailable){
					// call API
					Toast.makeText(getApplicationContext(), "Internet Available", 1000).show()	;
				}
				else{
					//store in SQLiteDatabase
					Toast.makeText(getApplicationContext(), "Internet is not available", 1000).show()	;
				}
//				String sID = "", sValue = "";
//				if(lL.getChildCount() > 0){
//					for(int i=0; i<lL.getChildCount(); i++){
//						Class<?> c = (Class<?>) lL.getChildAt(i).getClass();
//						if(c.getName().equals("android.widget.TextView")){
//							TextView tv = (TextView) lL.getChildAt(i);
//							sID = sID + tv.getId() + "\n";
//							sValue = sValue + tv.getText().toString() + "\n";
//						}
//						else if (c.getName()
//								.equals("android.widget.EditText")) {
//
//							EditText ed = (EditText) lL.getChildAt(i);
//							sID = sID + ed.getId() + "\n";
//							sValue = sValue + ed.getText().toString() + "\n";
//						}
//						Log.i("Class types", c.getName());
//					}					
//				}
			}
		});
	}
	
	private void loadUI(){
		i = getIntent();
		formName = i.getStringExtra("form"); //formName and fileName are the same
		jsonReader = new JSONReader(this);
		//jsonReader.setActivity(this);
		scrollView = (ScrollView) findViewById(R.id.sv);
		lL = jsonReader.readForm(formName);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lL.setLayoutParams(params);
		lL.setOrientation(LinearLayout.VERTICAL);
		scrollView.addView(lL);
		getActionBar().setTitle(formName);
		btnSubmit = (Button) findViewById(R.id.btnSubmit_form_detail);
		connectionDetector = new ConnectionDetector(getApplicationContext());
		
		//getting form in order to work with DB
		form = jsonReader.getFormFromFile(formName);
	}
}

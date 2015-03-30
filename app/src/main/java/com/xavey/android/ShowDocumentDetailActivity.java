package com.xavey.android;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.xavey.android.db.XaveyDBHelper;
import com.xavey.android.model.Document;
import com.xavey.android.model.Form;
import com.xavey.android.model.RequestMethod;
import com.xavey.android.model.RestClient;
import com.xavey.android.util.ConnectionDetector;
import com.xavey.android.util.JSONReader;
import com.xavey.android.util.JSONWriter;
import com.xavey.android.util.SyncManager;

public class ShowDocumentDetailActivity extends Activity {

	LinearLayout lL;
	ScrollView scrollView;
	Button btnSubmit;
	JSONReader jsonReader;
	JSONWriter jsonWriter;
	Intent intent;

	Form currentForm;
	Document currentDocument;
	String form_id;
	String form_name;
	String form_title;

	ArrayList<HashMap<String, String>> formFieldsList;
	// DM
	DisplayMetrics dm;
	int screenWidth;
	int screenHeight;

	XaveyDBHelper dbHelper;

	//Document screenDocument;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_form_detail_screen);
		loadUI();
		getActionBar().setIcon(R.drawable.history);
		getActionBar().setTitle("History");
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		String documentID = intent.getStringExtra("documentID");
		currentDocument = dbHelper.getDocumentByDocumentID(documentID);
		String formID = currentDocument.getForm_id();
		currentForm = dbHelper.getFormByFormID(formID);

		LinearLayout lL = jsonWriter
				.writeDocument(currentDocument, currentForm);

		/*LayoutParams params = new LayoutParams(screenWidth - 10,
				screenWidth - 20);
		lL.setLayoutParams(params);*/

		//lL.setPadding(10, 10, 10, 10);
		lL.setGravity(Gravity.CENTER_HORIZONTAL);

		scrollView.addView(lL);

		if(currentDocument.getSubmitted().equals("1")){
			// if already subbmitted , no need to submit again
			btnSubmit.setText("Submitted");
			btnSubmit.setEnabled(false);
		}

		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ConnectionDetector conDetector = new ConnectionDetector(getApplicationContext());
				if(conDetector.isConnectingToInternet()){
					try {
						SyncManager syncManager = new SyncManager(ShowDocumentDetailActivity.this);
						syncManager.submitDocument(currentDocument, currentForm);
						finish();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				else{
					Toast.makeText(getApplicationContext(), "Connection error! Check your internet connection and try again", 1000).show();
				}
			}
		});
	}

	private void loadUI() {
		getScreenInfo();
		scrollView = (ScrollView) findViewById(R.id.svShowFormDetail);
		btnSubmit = (Button) findViewById(R.id.documentSubmitButton);
		/*
		 * android.widget.RelativeLayout.LayoutParams layoutParams = new
		 * android.widget.RelativeLayout.LayoutParams(screenWidth-20,
		 * screenHeight/2-100); scrollView.setLayoutParams(layoutParams);
		 */
		jsonReader = new JSONReader(this);
		jsonWriter = new JSONWriter(this);
		dbHelper = new XaveyDBHelper(this);
	}

	private void getScreenInfo() {
		dm = getResources().getDisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/*class URLCheckTask extends AsyncTask<String, Void, String> {

		private ProgressDialog Dialog = new ProgressDialog(
				ShowDocumentDetailActivity.this);

		protected void onPreExecute() {
			Dialog.setMessage("Please wait..");
			Dialog.show();
		}

		@Override
		protected String doInBackground(String... url) {
			boolean reachable = false;
			try {
				reachable = InetAddress.getByName(url[0]).isReachable(3000);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (reachable)
				return "1";
			else
				return "0";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Dialog.dismiss();
			if (result.equals("1")) {
				Toast.makeText(getApplicationContext(), "reachable", 500)
						.show();
				try {
					submitDocument();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				Toast.makeText(getApplicationContext(), "unreachable", 500)
						.show();
				try {
					submitDocument();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}*/

	

	private class JSONSubmitTask extends AsyncTask<JSONArray, Void, String> {

		private ProgressDialog Dialog = new ProgressDialog(
				ShowDocumentDetailActivity.this);

		protected void onPreExecute() {
			Dialog.setMessage("Submitting..");
			Dialog.show();
		}

		@Override
		protected String doInBackground(JSONArray... params) {
			JSONArray jsonArray = params[0];
			String response = "";
			RestClient c = new RestClient(
					"http://dev.xavey.com:3000/collections/data", jsonArray);
			try {
				c.Execute(RequestMethod.POST);
				response = c.getResponse();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			Dialog.setMessage("Done..");
			Dialog.dismiss();
			Toast.makeText(getApplicationContext(),
					"response result :" + result, 4000).show();
		}
	}
}

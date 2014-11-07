package com.xavey.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.xavey.app.R.layout;
import com.xavey.app.db.XaveyDBHelper;
import com.xavey.app.model.Document;
import com.xavey.app.model.Form;
import com.xavey.app.util.ConnectionDetector;
import com.xavey.app.util.ImageSavingManager;
import com.xavey.app.util.JSONReader;
import com.xavey.app.util.JSONWriter;
import com.xavey.app.util.SyncManager;
import com.xavey.app.util.TypeFaceManager;
import com.xavey.app.util.UUIDGenerator;

public class DocumentInputActivity extends Activity {

	LinearLayout lL;
	ScrollView scrollView;
	JSONReader jsonReader;
	Button btnSubmit;
	// Button btnAddSignature, btnRemove;
	EditText documentName;
	TextView errorMsg;
	Intent intent;
	String formName;
	XaveyDBHelper dbHelper;
	// DM
	DisplayMetrics dm;
	int screenWidth;
	int screenHeight;
	JSONWriter jsonWriter;
	// internet conectivity stuffs
	Boolean isInternetAvailable = false;
	ConnectionDetector connectionDetector;
	Form currentForm;
	ArrayList<HashMap<String, Object>> formFieldsList;

	boolean isAllRequiredFieldFilled = true;

	ArrayList<HashMap<String, String>> imagesToSubmit = new ArrayList<HashMap<String, String>>();

	TypeFaceManager typeface;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.document_input_screen);
		loadUI();
		getActionBar().setIcon(R.drawable.home);
		getActionBar().setTitle("Home");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		/*try {
			lL = jsonReader.readForm(currentForm);
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
		lL = null;
		LayoutParams params = new LayoutParams(screenWidth - 100,
				LayoutParams.MATCH_PARENT);
		lL.setLayoutParams(params);
		lL.setPadding(30, 20, 30, 50);
		scrollView.addView(lL);

		// btnAddSignature.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent toSignature = new Intent(DocumentInputActivity.this,
		// SignatureActivity.class);
		// startActivity(toSignature);
		// }
		// });

		// btnRemove.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// mySignature = null;
		// }
		// });

		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (documentName.getText().length() > 0) {
					isInternetAvailable = connectionDetector
							.isConnectingToInternet();
					Document document = new Document();
					HashMap<String, Object> incompleteMap = getValuesFromLayout(lL);
					if (isAllRequiredFieldFilled) {
						JSONObject document_json = new JSONObject();
						JSONArray jsonArray = new JSONArray();
						for (int i = 0; i < formFieldsList.size(); i++) {
							if (formFieldsList.get(i).size() > 0) {
								HashMap<String, Object> map = formFieldsList
										.get(i);
								String fieldLabel = map.get("field_label")
										.toString();
								String fieldName = map.get("field_name")
										.toString();
								String userTypedValue = incompleteMap.get(
										fieldName).toString();
								if (userTypedValue.length() > 0) {
									try {
										JSONObject child = new JSONObject();
										child.put("field_name", fieldName);
										child.put("field_value", userTypedValue);
										child.put("field_label", fieldLabel);
										jsonArray.put(child);
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							}
						}
						try {
							String logginUserName = ApplicationValues.loginUser
									.getUser_name();
							document_json.put("document_json", jsonArray);
							document.setDocument_id(UUIDGenerator
									.getUUIDForDocument());
							document.setDocument_name(documentName.getText()
									.toString());
							document.setCreated_at(getCurrentDate());
							document.setDocument_json(document_json.toString());
							document.setForm_id(currentForm.getForm_id());
							document.setCreated_worker(ApplicationValues.loginUser
									.getUser_id());
							documentName.setText("");
						} catch (JSONException e) {
							e.printStackTrace();
						}

						// ---------
						if (isInternetAvailable) {
							// submit it with API and set true to isSynced
							document.setSubmitted("1");
							try {
								SyncManager syncManager = new SyncManager(
										DocumentInputActivity.this);
								// syncManager.submitDocument(document,
								// currentForm);
								syncManager.submitDocument2(document,
										currentForm, imagesToSubmit);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else { // offline mode
							document.setSubmitted("0");
						}
						dbHelper.addNewDocument(document);
						finish();
					}

				} else {
					errorMsg.setText("Document name is required !");
				}
			}
		});
	}

	private boolean getFieldRequired(String field_required) {
		if (field_required.equals("true"))
			return true;
		return false;
	}

	// to get the input values from editText that user typed
	private HashMap<String, Object> getValuesFromLayout(LinearLayout layout) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (layout.getChildCount() > 0) {
			for (int i = 0; i < layout.getChildCount(); i++) {
				Class<?> c = (Class<?>) layout.getChildAt(i).getClass();
				if (c.getName().equals("android.widget.EditText")) {
					EditText text = (EditText) layout.getChildAt(i);
					String key = text.getTag(R.id.field_name_id).toString();
					String field_required = text.getTag(R.id.field_required_id)
							.toString();
					boolean is_field_required = getFieldRequired(field_required);
					if (text.getText().toString().length() > 0)
						map.put(key, text.getText().toString());
					else if (is_field_required) {
						// Toast

					} else
						map.put(key, "-");
					// field_name : userTyped Value
					// eg. idea_name : great Idea
				}
				/*
				 * if (c.getName().equals("android.gesture.GestureOverlayView"))
				 * { GestureOverlayView signatureView = (GestureOverlayView)
				 * layout .getChildAt(i);
				 * 
				 * }
				 */
				if (c.getName().equals("android.widget.TimePicker")) {
					TimePicker timePicker = (TimePicker) layout.getChildAt(i);
					String hour = timePicker.getCurrentHour() + "";
					String min = timePicker.getCurrentMinute() + "";
					if (timePicker.getCurrentMinute() == 0) {
						min = "00";
					}
					String key = timePicker.getTag(R.id.field_name_id)
							.toString();
					map.put(key, hour + " : " + min);
				}
				if (c.getName().equals("android.widget.DatePicker")) {
					DatePicker datePicker = (DatePicker) layout.getChildAt(i);
					int year = datePicker.getYear();
					int month = datePicker.getMonth();
					int day = datePicker.getDayOfMonth();
					String date = year + "-" + month + "-" + day;
					Toast.makeText(getApplicationContext(), date, 1000).show();
					String key = datePicker.getTag(R.id.field_name_id)
							.toString();
					String field_required = datePicker.getTag(
							R.id.field_required_id).toString();
					boolean is_field_required = getFieldRequired(field_required);
					map.put(key, date);
				}
				if (c.getName().equals("android.widget.LinearLayout")) {
					LinearLayout linearLayout = (LinearLayout) layout
							.getChildAt(i);
					if (linearLayout.getTag().toString().equals("textLayout")) {
						String key = "";
						String value = "";
						TextView errorMsg = null, label = null;
						EditText edt1 = null;

						boolean is_field_required = false;
						String field_label = "";

						for (int y = 0; y < linearLayout.getChildCount(); y++) {
							Class<?> subClass = (Class<?>) linearLayout
									.getChildAt(y).getClass();
							if (subClass.getName().equals(
									"android.widget.TextView")) {
								TextView textView = (TextView) linearLayout
										.getChildAt(y);

								// following if else is just to categorize the
								// textView
								// if that's label or error message
								if (textView.getTag().toString()
										.equals("errorMsg")) {
									// this is error message
									errorMsg = textView;
								} else {
									// this is label
									key = textView.getTag(R.id.field_name_id)
											.toString();
									String field_required = textView.getTag(
											R.id.field_required_id).toString();
									field_label = textView.getTag(
											R.id.field_label_id).toString();
									is_field_required = getFieldRequired(field_required);
								}
							} else if (subClass.getName().equals(
									"android.widget.EditText")) {
								edt1 = (EditText) linearLayout.getChildAt(y);
								value = edt1.getText().toString();
							}
						}

						if (is_field_required
								&& edt1.getText().toString().length() <= 0) {
							// show error
							errorMsg.setText(field_label + " is required.");
							errorMsg.setTextSize(12);
							errorMsg.setTextColor(Color.RED);
							errorMsg.setLayoutParams(new LayoutParams(
									LayoutParams.WRAP_CONTENT, 50));
							isAllRequiredFieldFilled = isAllRequiredFieldFilled && false;
						} else {
							errorMsg.setLayoutParams(new LayoutParams(
									LayoutParams.WRAP_CONTENT, 0)); // dismiss
						}
						map.put(key, value);
					} else if (linearLayout.getTag().toString()
							.equals("numberLayout")) {
						String key = "";
						String value = "";

						TextView errorMsg = null;
						TextView label = null;
						EditText edt1 = null;
						boolean is_field_required = false;
						String field_label = "";

						for (int y = 0; y < linearLayout.getChildCount(); y++) {
							Class<?> subClass = (Class<?>) linearLayout
									.getChildAt(y).getClass();

							if (subClass.getName().equals(
									"android.widget.TextView")) {

								TextView textView = (TextView) linearLayout
										.getChildAt(y);

								// following if else is just to categorize the
								// textView
								// if that's label or error message
								if (textView.getTag().toString()
										.equals("errorMsg")) {
									// this is error message
									errorMsg = textView;
								} else {
									// this is label
									key = textView.getTag(R.id.field_name_id)
											.toString();
									String field_required = textView.getTag(
											R.id.field_required_id).toString();
									field_label = textView.getTag(
											R.id.field_label_id).toString();
									is_field_required = getFieldRequired(field_required);
								}
							} else if (subClass.getName().equals(
									"android.widget.EditText")) {
								edt1 = (EditText) linearLayout.getChildAt(y);
								value = edt1.getText().toString();
							}
						}
						if (is_field_required
								&& edt1.getText().toString().length() <= 0) {
							// show error
							errorMsg.setText(field_label + " is required.");
							errorMsg.setTextSize(12);
							errorMsg.setTextColor(Color.RED);
							errorMsg.setLayoutParams(new LayoutParams(
									LayoutParams.WRAP_CONTENT, 50));
							isAllRequiredFieldFilled = isAllRequiredFieldFilled && false;
						} else {
							errorMsg.setLayoutParams(new LayoutParams(
									LayoutParams.WRAP_CONTENT, 0)); // dismiss
						}
						map.put(key, value);
					} else if (linearLayout.getTag().toString()
							.equals("radioLayout")) {
						String key = "";
						for (int y = 0; y < linearLayout.getChildCount(); y++) {

							Class<?> subClass = (Class<?>) linearLayout
									.getChildAt(y).getClass();

							if (subClass.getName().equals(
									"android.widget.TextView")) {
								TextView label = (TextView) linearLayout
										.getChildAt(y);
								key = label.getTag(R.id.field_name_id)
										.toString();
								String field_required = label.getTag(
										R.id.field_required_id).toString();
								boolean is_field_required = getFieldRequired(field_required);
							} else if (subClass.getName().equals(
									"android.widget.RadioGroup")) {
								// radio
								RadioGroup radioGroup = (RadioGroup) linearLayout
										.getChildAt(y);
								int selectedID = radioGroup
										.getCheckedRadioButtonId();
								RadioButton selectedButton = (RadioButton) findViewById(selectedID);
								String value = selectedButton.getTag()
										.toString();
								map.put(key, value);
							}
						}
					} else if (linearLayout.getTag().toString()
							.equals("checkBoxLayout")) {
						JSONArray checkedValues = new JSONArray();
						String key = "";

						TextView errorMsg = null;
						TextView label = null;
						boolean is_field_required = false;
						String field_label = "";

						for (int z = 0; z < linearLayout.getChildCount(); z++) {
							Class<?> subClass = (Class<?>) linearLayout
									.getChildAt(z).getClass();
							if (subClass.getName().equals(
									"android.widget.TextView")) {
								TextView textView = (TextView) linearLayout
										.getChildAt(z);

								// following if else is just to categorize the
								// textView
								// if that's label or error message
								if (textView.getTag().toString()
										.equals("errorMsg")) {
									// this is error message
									errorMsg = textView;
								} else {
									// this is label
									key = textView.getTag(R.id.field_name_id)
											.toString();
									String field_required = textView.getTag(
											R.id.field_required_id).toString();
									field_label = textView.getTag(
											R.id.field_label_id).toString();
									is_field_required = getFieldRequired(field_required);
								}
							} else if (subClass.getName().equals(
									"android.widget.CheckBox")) {
								CheckBox checkBox = (CheckBox) linearLayout
										.getChildAt(z);
								if (checkBox.isChecked()) {
									String value = checkBox.getTag().toString();
									checkedValues.put(value);
								}
							}
						}
						if (is_field_required && checkedValues.length() <= 0) {
							// show error
							errorMsg.setText(field_label + " is required.");
							errorMsg.setTextSize(12);
							errorMsg.setTextColor(Color.RED);
							errorMsg.setLayoutParams(new LayoutParams(
									LayoutParams.WRAP_CONTENT, 50));
							isAllRequiredFieldFilled = isAllRequiredFieldFilled && false;
						} else {
							errorMsg.setLayoutParams(new LayoutParams(
									LayoutParams.WRAP_CONTENT, 0)); // dismiss
						}
						map.put(key, checkedValues);
					} else if (linearLayout.getTag().toString()
							.equals("locationLayout")) {

						String key = "";
						double latitude = 0.0;
						double longitude = 0.0;

						TextView errorMsg = null;
						TextView label = null;
						boolean is_field_required = false;
						String field_label = "";

						for (int j = 0; j < linearLayout.getChildCount(); j++) {
							Class<?> subClass = (Class<?>) linearLayout
									.getChildAt(j).getClass();
							if (subClass.getName().equals(
									"android.widget.TextView")) {
								TextView textView = (TextView) linearLayout
										.getChildAt(j);

								// following if else is just to categorize the
								// textView
								// if that's label or error message
								if (textView.getTag().toString()
										.equals("errorMsg")) {
									// this is error message
									errorMsg = textView;
								} else {
									// this is label
									key = textView.getTag(R.id.field_name_id)
											.toString();
									String field_required = textView.getTag(
											R.id.field_required_id).toString();
									field_label = textView.getTag(
											R.id.field_label_id).toString();
									is_field_required = getFieldRequired(field_required);
								}
							} else if (subClass.getName().equals(
									"android.widget.EditText")) {
								EditText location = (EditText) linearLayout
										.getChildAt(j);
								if (location.getHint().equals("Latitude")) {
									if (location.getText().toString().length() > 0)
										latitude = Double.parseDouble(location
												.getText().toString());
								} else {
									if (location.getText().toString().length() > 0)
										longitude = Double.parseDouble(location
												.getText().toString());
								}
							}
						}
						String lat = latitude + "";
						String lon = longitude + "";
						if (is_field_required && lat.length() <= 0
								&& lon.length() <= 0) {
							// show error
							errorMsg.setText(field_label + " is required.");
							errorMsg.setTextSize(12);
							errorMsg.setTextColor(Color.RED);
							errorMsg.setLayoutParams(new LayoutParams(
									LayoutParams.WRAP_CONTENT, 50));
							isAllRequiredFieldFilled = isAllRequiredFieldFilled && false;
						}
						map.put(key, latitude + " , " + longitude);
					} else if (linearLayout.getTag().toString()
							.equals("datetimeLayout")) {
						String key = "";
						String time = "";
						String date = "";
						for (int j = 0; j < linearLayout.getChildCount(); j++) {
							Class<?> subClass = (Class<?>) linearLayout
									.getChildAt(j).getClass();
							if (subClass.getName().equals(
									"android.widget.TextView")) {
								TextView label = (TextView) linearLayout
										.getChildAt(j);
								key = label.getTag(R.id.field_name_id)
										.toString();
							} else if (subClass.getName().equals(
									"android.widget.TimePicker")) {
								TimePicker timePicker = (TimePicker) linearLayout
										.getChildAt(j);
								String hour = timePicker.getCurrentHour() + "";
								String min = timePicker.getCurrentMinute() + "";
								if (timePicker.getCurrentMinute() == 0) {
									min = "00";
								}
								time = hour + ":" + min;
							} else if (subClass.getName().equals(
									"android.widget.DatePicker")) {

								DatePicker datePicker = (DatePicker) linearLayout
										.getChildAt(j);
								int year = datePicker.getYear();
								int month = datePicker.getMonth();
								int day = datePicker.getDayOfMonth();
								date = year + "-" + month + "-" + day;
							}
						}
						map.put(key, date + "  " + time);
					} else if (linearLayout.getTag().toString()
							.equals("drawingLayout")) {
						String key = "";
						TextView errorMsg = null;
						ImageView drawingPreview = null;
						TextView label = null;
						boolean is_field_required = false;
						String field_label = "";

						for (int j = 0; j < linearLayout.getChildCount(); j++) {
							Class<?> subClass = (Class<?>) linearLayout
									.getChildAt(j).getClass();
							if (subClass.getName().equals(
									"android.widget.TextView")) {
								TextView textView = (TextView) linearLayout
										.getChildAt(j);

								// following if else is just to categorize the
								// textView
								// if that's label or error message
								if (textView.getTag().toString()
										.equals("errorMsg")) {
									// this is error message
									errorMsg = textView;
								} else {
									// this is label
									key = textView.getTag(R.id.field_name_id)
											.toString();
									String field_required = textView.getTag(
											R.id.field_required_id).toString();
									field_label = textView.getTag(
											R.id.field_label_id).toString();
									is_field_required = getFieldRequired(field_required);
								}
							} else if (subClass.getName().equals(
									"android.widget.ImageView")) {

								// ဒီ method ထဲမှာဘာမှမလုပ်ဘူး... ဒီ small
								// imageview က preview ပဲပြထားတာ... တကယ့် path က
								// သယ်လာပြီးသား....
								// validation ပဲလုပ်တာ..

								drawingPreview = (ImageView) linearLayout
										.getChildAt(j);
								// to check image involve or not
								/*
								 * Drawable d = drawingPreview.getDrawable(); if
								 * (d == null) {
								 * Toast.makeText(getApplicationContext(),
								 * "no image", 1000).show(); } else
								 * Toast.makeText(getApplicationContext(),
								 * "image include", 1000).show();
								 */
							}
						}
						if (is_field_required
								&& drawingPreview.getDrawable() == null) {
							// show error
							errorMsg.setText(field_label + " is required.");
							errorMsg.setTextSize(12);
							errorMsg.setTextColor(Color.RED);
							errorMsg.setLayoutParams(new LayoutParams(
									LayoutParams.WRAP_CONTENT, 50));
							isAllRequiredFieldFilled = isAllRequiredFieldFilled && false;
						} else {
							errorMsg.setLayoutParams(new LayoutParams(
									LayoutParams.WRAP_CONTENT, 0)); // dismiss
						}

						// ဒီနေရာမှာ ImageAsyncTask လာပြီးမှ ရတဲ့ idကို map
						// ထဲထည့်ရမှာ...
						map.put(key, "unavailable");
					} else if (linearLayout.getTag().toString()
							.equals("photoLayout")) {
						String key = "";
						TextView errorMsg = null;
						ImageView drawingPreview = null;
						TextView label = null;
						boolean is_field_required = false;
						String field_label = "";

						for (int j = 0; j < linearLayout.getChildCount(); j++) {
							Class<?> subClass = (Class<?>) linearLayout
									.getChildAt(j).getClass();
							if (subClass.getName().equals(
									"android.widget.TextView")) {
								TextView textView = (TextView) linearLayout
										.getChildAt(j);

								// following if else is just to categorize the
								// textView
								// if that's label or error message
								if (textView.getTag().toString()
										.equals("errorMsg")) {
									// this is error message
									errorMsg = textView;
								} else {
									// this is label
									key = textView.getTag(R.id.field_name_id)
											.toString();
									String field_required = textView.getTag(
											R.id.field_required_id).toString();
									field_label = textView.getTag(
											R.id.field_label_id).toString();
									is_field_required = getFieldRequired(field_required);
								}
							} else if (subClass.getName().equals(
									"android.widget.ImageView")) {

								// ဒီ method ထဲမှာဘာမှမလုပ်ဘူး... ဒီ small
								// imageview က preview ပဲပြထားတာ... တကယ့် path က
								// သယ်လာပြီးသား....
								// validation ပဲလုပ်တာ..

								drawingPreview = (ImageView) linearLayout
										.getChildAt(j);

							}
						}
						if (is_field_required
								&& drawingPreview.getDrawable() == null) {
							// show error
							errorMsg.setText(field_label + " is required.");
							errorMsg.setTextSize(12);
							errorMsg.setTextColor(Color.RED);
							errorMsg.setLayoutParams(new LayoutParams(
									LayoutParams.WRAP_CONTENT, 50));
							isAllRequiredFieldFilled = isAllRequiredFieldFilled && false;
						} else {
							errorMsg.setLayoutParams(new LayoutParams(
									LayoutParams.WRAP_CONTENT, 0)); // dismiss
						}

						// ဒီနေရာမှာ ImageAsyncTask လာပြီးမှ ရတဲ့ idကို map
						// ထဲထည့်ရမှာ...
						map.put(key, "unavailable");
					}

				}
			}
		}
		return map;
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

	private void loadUI() {
		getScreenInfo();
		intent = getIntent();
		// formName = i.getStringExtra("form"); //formName and fileName are the
		// same
		jsonReader = new JSONReader(this);
		scrollView = (ScrollView) findViewById(R.id.sv);
		/*
		 * android.widget.RelativeLayout.LayoutParams layoutParams = new
		 * android.widget.RelativeLayout.LayoutParams( screenWidth - 20,
		 * screenHeight / 2 - 100); scrollView.setLayoutParams(layoutParams);
		 */
		dbHelper = new XaveyDBHelper(this);
		currentForm = dbHelper.getFormByFormID(intent.getStringExtra("formID"));
		btnSubmit = (Button) findViewById(R.id.btnSubmit_form_detail);
		// btnRemove = (Button) findViewById(R.id.btnRemoveSignature);
		// btnAddSignature = (Button) findViewById(R.id.btnAddSignature);

		/*
		 * android.widget.RelativeLayout.LayoutParams layoutParams = new
		 * android.widget.RelativeLayout.LayoutParams(screenWidth-20,
		 * screenHeight/2-100); govSignature.setLayoutParams(layoutParams);
		 */

		documentName = (EditText) findViewById(R.id.edDocumentName);
		errorMsg = (TextView) findViewById(R.id.tvErrorMsgDocumentInput);
		connectionDetector = new ConnectionDetector(getApplicationContext());
		formFieldsList = jsonReader.getFormFields(currentForm.getForm_json());
		jsonWriter = new JSONWriter(this);
		typeface = new TypeFaceManager(this);
	}

	private void getScreenInfo() {
		dm = getResources().getDisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	private String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			if (requestCode == ApplicationValues.REQUEST_DRAWING) {
				Bundle bundle = data.getExtras();
				String field_name = bundle.getString("field_name");
				String field_help = bundle.getString("field_help");
				String field_type = bundle.getString("field_type");
				int view_id = Integer.parseInt(bundle.getString("view_id"));
				String imagePath = bundle.getString("signPath");
				HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("field_name", field_name);
				hashMap.put("imagePath", imagePath);
				imagesToSubmit.add(hashMap);
				int lLchildCount = lL.getChildCount();
				for (int i = 0; i < lLchildCount; i++) {
					Class<?> c = (Class<?>) lL.getChildAt(i).getClass();
					if (c.getName().equals("android.widget.LinearLayout")) {
						LinearLayout childLayout = (LinearLayout) lL
								.getChildAt(i);
						if (childLayout.getTag().toString()
								.equals("drawingLayout")) {
							for (int j = 0; j < childLayout.getChildCount(); j++) {
								if (childLayout.getChildAt(j).getId() == view_id) {
									ImageView imageView = (ImageView) childLayout
											.getChildAt(j);
									ImageSavingManager.loadImageFromLocal(
											imagePath, imageView);
									imageView.setTag(imagePath);
									lL.refreshDrawableState();
								}
							}
						}
					}
				}
			}
			// ------------------------- ~DRAWING END~ ---------------

			else if (requestCode == ApplicationValues.REQUEST_CAMERA
					&& resultCode == RESULT_OK) {
				String field_name = ApplicationValues.FIELD_NAME_TMP;
				String field_help = ApplicationValues.FIELD_HELP_TMP;
				String field_type = ApplicationValues.FIELD_TYPE_TMP;
				int view_id = Integer.parseInt(ApplicationValues.VIEW_ID_TMP);
				String imagePath = ApplicationValues.IMAGE_PATH_TMP;
				// I wrote the following with my own style
				// original code exists at CameraAppGuruz project's
				// MainActivity's onActivityResult
				HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("field_name", field_name);
				hashMap.put("imagePath", imagePath);
				imagesToSubmit.add(hashMap);
				int lLchildCount = lL.getChildCount();
				for (int i = 0; i < lLchildCount; i++) {
					Class<?> c = (Class<?>) lL.getChildAt(i).getClass();
					if (c.getName().equals("android.widget.LinearLayout")) {
						LinearLayout childLayout = (LinearLayout) lL
								.getChildAt(i);
						if (childLayout.getTag().toString()
								.equals("photoLayout")) {
							for (int j = 0; j < childLayout.getChildCount(); j++) {
								if (childLayout.getChildAt(j).getId() == view_id) {
									ImageView imageView = (ImageView) childLayout
											.getChildAt(j);
									ImageSavingManager.loadImageFromLocal(
											imagePath, imageView);
									imageView.setTag(imagePath);
									lL.refreshDrawableState();
								}
							}
						}
					}
				}
			} else if (requestCode == ApplicationValues.SELECT_FILE
					&& resultCode == RESULT_OK) {
				Uri selectedImageUri = data.getData();
				String tempPath = getPath(selectedImageUri,
						DocumentInputActivity.this);
				String field_name = ApplicationValues.FIELD_NAME_TMP;
				String field_help = ApplicationValues.FIELD_HELP_TMP;
				String field_type = ApplicationValues.FIELD_TYPE_TMP;
				int view_id = Integer.parseInt(ApplicationValues.VIEW_ID_TMP);
				String imagePath = tempPath;
				// I wrote the following with my own style
				// original code exists at CameraAppGuruz project's
				// MainActivity's onActivityResult
				HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("field_name", field_name);
				hashMap.put("imagePath", imagePath);
				imagesToSubmit.add(hashMap);
				int lLchildCount = lL.getChildCount();
				for (int i = 0; i < lLchildCount; i++) {
					Class<?> c = (Class<?>) lL.getChildAt(i).getClass();
					if (c.getName().equals("android.widget.LinearLayout")) {
						LinearLayout childLayout = (LinearLayout) lL
								.getChildAt(i);
						if (childLayout.getTag().toString()
								.equals("photoLayout")) {
							for (int j = 0; j < childLayout.getChildCount(); j++) {
								if (childLayout.getChildAt(j).getId() == view_id) {
									ImageView imageView = (ImageView) childLayout
											.getChildAt(j);
									ImageSavingManager.loadImageFromLocal(
											imagePath, imageView);
									imageView.setTag(imagePath);
									lL.refreshDrawableState();
								}
							}
						}
					}
				}
			}
		}
	}

	public String getPath(Uri uri, Activity activity) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = activity
				.managedQuery(uri, projection, null, null, null);
		CursorLoader loader = new CursorLoader(activity, uri, projection, null,
				null, null);
		// following is to test cuz the upper method is deprecated
		// Cursor cursor2 = loader.loadInBackground();
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

}

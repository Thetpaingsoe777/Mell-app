package com.xavey.app.util;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.xavey.app.ApplicationValues;
import com.xavey.app.R;
import com.xavey.app.db.XaveyDBHelper;
import com.xavey.app.model.Audio;
import com.xavey.app.model.Document;
import com.xavey.app.model.Form;
import com.xavey.app.model.MatrixCell;
import com.xavey.app.model.User;

// (1) getFile()
// (2) and getJSON(getFile())
// (3) and that return form
public class JSONReader {

	Activity activity;
	// putting DisplayMetric here didn't work
	XaveyDBHelper dbHelper;
	GPSTracker gps;
	TypeFaceManager typeface;
	DisplayManager displayManager;
	XaveyProperties xaveyProperties;
	String zawGyiFontStatus;
	ToastManager xaveyToast;
	AudioRecordingManager recordingManager;

	private String currentDocumentID;

	public JSONReader(Activity activity) {
		this.activity = activity;
		dbHelper = new XaveyDBHelper(activity);
		gps = new GPSTracker(activity);
		typeface = new TypeFaceManager(activity);
		displayManager = new DisplayManager(this.activity);
		xaveyProperties = new XaveyProperties();
		zawGyiFontStatus = xaveyProperties.getZawgyiFontStatus();
		xaveyToast = new ToastManager(this.activity);
		recordingManager = new AudioRecordingManager(activity);
	}

	// Layout setting here
	// get incoming JSONString and return a linear layout

	public ArrayList<LinearLayout> readForm2(Form form) throws JSONException {

		ArrayList<LinearLayout> layoutList = new ArrayList<LinearLayout>();

		ArrayList<HashMap<String, Object>> formFields = getFormFields(form.getForm_json());

		int upLayoutHeight = displayManager.getHeigth(6);
		int LayoutWidth = displayManager.getWidth(95);
		int LayoutHeight = displayManager.getHeigth(45);
		int editTextLayoutHeight = displayManager.getHeigth(6);

		// params
		LayoutParams labelLayoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// labelLayoutParams.setMargins(10, 10, 10, 60);
		// labelLayoutParams.height = 30;
		labelLayoutParams.setMargins(10, 5, 10, 5);

		LayoutParams descriptionLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// descriptionLayoutParams.height = 30;
		descriptionLayoutParams.setMargins(10, 5, 10, 5);

		LayoutParams innerLayoutParams = new LayoutParams(LayoutWidth, LayoutParams.WRAP_CONTENT);
		LayoutParams editTextLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, editTextLayoutHeight);
		editTextLayoutParams.setMargins(15, 10, 15, 10);

		final LayoutParams extraValueLayoutParamsAppear = new LayoutParams(
				LayoutParams.MATCH_PARENT, editTextLayoutHeight);
		extraValueLayoutParamsAppear.setMargins(25, 0, 25, 5);
		final LayoutParams extraValueLayoutParamsDisappear = new LayoutParams(
				0, 0);
		extraValueLayoutParamsDisappear.setMargins(25, 0, 25, 5);
		editTextLayoutParams.setMargins(15, 10, 15, 10);

		LayoutParams errorMsgLayoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		errorMsgLayoutParams.setMargins(10, 10, 10, 0);

		LayoutParams radioButtonLineLayoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		radioButtonLineLayoutParams.setMargins(15, 10, 15, 5);

		float labelTextSize = 18;
		float descriptionTextSize = 18;
		float radioButtonTextSize = 16;

		String lineColor = "#76C4F5";

		// -------------------------------------------------------------

		for (int i = 0; i < formFields.size(); i++) {
			HashMap<String, Object> fields = formFields.get(i);
			for (Object key : fields.keySet()) {
				if (key.equals("field_type")) {
					if (fields.get(key).equals("text")) {
						ScrollView scroll = new ScrollView(activity);
						scroll.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT));
						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);
						RelativeLayout upLayout = new RelativeLayout(activity);
						upLayout.setPadding(0, 10, 20, 0);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, relative_WRAP_CONTENT);
						upLayout.setLayoutParams(upLayoutParams);
						TextView index = new TextView(activity);
						RelativeLayout.LayoutParams tvLayoutParams = new android.widget.RelativeLayout.LayoutParams(
								relative_WRAP_CONTENT, relative_WRAP_CONTENT);
						tvLayoutParams
								.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						index.setLayoutParams(tvLayoutParams);
						index.setText("index/index");
						index.setTag("index");
						upLayout.addView(index);
						// upLayout.addView(getLine(lineColor));
						parentLayout.addView(upLayout);

						LinearLayout textLayout = new LinearLayout(activity);

						textLayout.setLayoutParams(innerLayoutParams);
						// textLayout
						// .setBackgroundResource(R.drawable.linear_layout_ui);
						textLayout.setOrientation(LinearLayout.VERTICAL);
						textLayout.setTag(R.id.layout_id, "textLayout");
						textLayout
								.setTag(R.id.field_id, fields.get("field_id"));
						textLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						textLayout.setTag(R.id.field_required_id,
								fields.get("field_required"));
						textLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						textLayout.setTag(R.id.field_ref,
								fields.get("field_ref"));
						textLayout.setTag(R.id.next_cond,
								fields.get("next_cond"));
						String textLabel = fields.get("field_label").toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setTag("label");
						tvLabel.setText(textLabel);
						tvLabel.setTextSize(labelTextSize);
						tvLabel.setLayoutParams(labelLayoutParams);
						tvLabel.setPadding(0, 5, 0, 5);
						setTypeFace(tvLabel);
						textLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setPadding(10, 0, 0, 0);
						tvdescription.setText(description);
						tvdescription.setTextSize(descriptionTextSize);
						tvdescription.setLayoutParams(descriptionLayoutParams);
						tvdescription.setPadding(0, 5, 0, 5);
						tvdescription.setGravity(Gravity.CENTER_VERTICAL);
						tvdescription
								.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_START);
						setTypeFace(tvdescription);
						textLayout.addView(tvdescription);

						EditText ed1 = new EditText(activity);
						ed1.setTextSize(16);
						ed1.setTypeface(typeface.getTypeFace());
						String fieldName = fields.get("field_name").toString();
						String fieldHelp = "-";
						if (fields.containsKey("field_help"))
							fieldHelp = fields.get("field_help").toString();

						ed1.setGravity(Gravity.LEFT);
						ed1.setSingleLine(true);
						ed1.setHint(fieldHelp);
						ed1.setBackgroundResource(R.drawable.edittext_style);
						ed1.setLayoutParams(editTextLayoutParams);
						setTypeFace(ed1);
						textLayout.addView(ed1);

						// error msg
						TextView errorMsg = new TextView(activity);
						errorMsg.setLayoutParams(errorMsgLayoutParams);
						errorMsg.setPadding(0, 5, 0, 5);
						setTypeFace(errorMsg);
						errorMsg.setGravity(Gravity.CENTER_VERTICAL);
						errorMsg.setTextSize(12);
						errorMsg.setTag("errorMsg");
						textLayout.addView(errorMsg);

						scroll.addView(textLayout);
						parentLayout.addView(scroll);

						// audio stuff
						boolean isAudioRequired = Boolean.parseBoolean(fields
								.get("field_audio_required").toString());
						if (isAudioRequired) {
							Audio audioinfo = new Audio();
							audioinfo.setAudio_name(fieldName);
							recordingManager = new AudioRecordingManager(
									activity);
							recordingManager.setAudioInfo(audioinfo);
							recordingManager.setFileName(fieldName + " - "
									+ getCurrentDocumentID());
							LinearLayout recordingLayout = recordingManager
									.getRecordingLayout();
							recordingLayout.setTag(R.id.layout_id,
									"recordingLayout");
							parentLayout.addView(recordingLayout);
						}

						layoutList.add(parentLayout);
					}
					if (fields.get(key).equals("number")) {

						ScrollView scroll = new ScrollView(activity);
						scroll.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));

						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);

						RelativeLayout upLayout = new RelativeLayout(activity);
						upLayout.setPadding(0, 10, 20, 0);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, relative_WRAP_CONTENT);
						upLayout.setLayoutParams(upLayoutParams);

						TextView index = new TextView(activity);
						RelativeLayout.LayoutParams tvLayoutParams = new android.widget.RelativeLayout.LayoutParams(
								relative_WRAP_CONTENT, relative_WRAP_CONTENT);
						tvLayoutParams
								.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						index.setLayoutParams(tvLayoutParams);
						index.setText("index/index");
						index.setTag("index");
						upLayout.addView(index);
						// upLayout.addView(getLine(lineColor));
						parentLayout.addView(upLayout);

						LinearLayout numberLayout = new LinearLayout(activity);
						numberLayout.setLayoutParams(innerLayoutParams);
						// numberLayout
						// .setBackgroundResource(R.drawable.linear_layout_ui);
						numberLayout.setOrientation(LinearLayout.VERTICAL);
						numberLayout.setTag(R.id.layout_id, "numberLayout");
						numberLayout.setTag(R.id.field_id,
								fields.get("field_id"));
						numberLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						numberLayout.setTag(R.id.field_required_id,
								fields.get("field_required"));
						numberLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						numberLayout.setTag(R.id.field_max_value,
								fields.get("field_max_value"));
						numberLayout.setTag(R.id.field_min_value,
								fields.get("field_min_value"));
						numberLayout.setTag(R.id.field_default_value,
								fields.get("field_default_value"));
						numberLayout.setTag(R.id.field_err_msg,
								fields.get("field_err_msg"));
						numberLayout.setTag(R.id.field_ref,
								fields.get("field_ref"));
						numberLayout.setTag(R.id.next_cond,
								fields.get("next_cond"));
						String textLabel = fields.get("field_label").toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setText(textLabel);
						tvLabel.setTextSize(labelTextSize);
						tvLabel.setTag("label");
						tvLabel.setTag(R.id.field_name_id,
								fields.get("field_name"));
						tvLabel.setTag(R.id.field_required_id,
								isFieldRequired(fields.get("field_required")));
						tvLabel.setTag(R.id.field_label_id, textLabel);
						tvLabel.setLayoutParams(labelLayoutParams);
						setTypeFace(tvLabel);
						numberLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setText(description);
						tvdescription.setTextSize(18);
						tvdescription.setLayoutParams(labelLayoutParams);
						setTypeFace(tvdescription);
						numberLayout.addView(tvdescription);

						EditText ed1 = new EditText(activity);
						ed1.setTextSize(16);
						String fieldName = fields.get("field_name").toString();
						String fieldHelp = "-";
						if (fields.containsKey("field_help"))
							fieldHelp = fields.get("field_help").toString();
						ed1.setText(fields.get("field_default_value")
								.toString());
						ed1.setGravity(Gravity.LEFT);
						ed1.setSingleLine(true);
						ed1.setHint(fieldHelp);
						ed1.setBackgroundResource(R.drawable.edittext_style);
						ed1.setLayoutParams(editTextLayoutParams);
						setTypeFace(ed1);
						// ed1.setEms(50);
						ed1.setInputType(InputType.TYPE_CLASS_NUMBER);
						ed1.setKeyListener(DigitsKeyListener
								.getInstance("0123456789."));
						numberLayout.addView(ed1);

						// error msg
						TextView errorMsg = new TextView(activity);
						errorMsg.setLayoutParams(errorMsgLayoutParams);
						setTypeFace(errorMsg);
						errorMsg.setTextSize(12);
						errorMsg.setTag("errorMsg");
						numberLayout.addView(errorMsg);

						scroll.addView(numberLayout);
						parentLayout.addView(scroll);

						layoutList.add(parentLayout);
					} else if (fields.get(key).equals("datetime")) {
						ScrollView scroll = new ScrollView(activity);
						scroll.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));

						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);
						RelativeLayout upLayout = new RelativeLayout(activity);
						upLayout.setPadding(0, 10, 20, 0);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, relative_WRAP_CONTENT);
						upLayout.setLayoutParams(upLayoutParams);
						TextView index = new TextView(activity);
						RelativeLayout.LayoutParams tvLayoutParams = new android.widget.RelativeLayout.LayoutParams(
								relative_WRAP_CONTENT, relative_WRAP_CONTENT);
						tvLayoutParams
								.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						index.setLayoutParams(tvLayoutParams);
						index.setText("index/index");
						index.setTag("index");
						upLayout.addView(index);
						// upLayout.addView(getLine(lineColor));
						parentLayout.addView(upLayout);

						LinearLayout datetimeLayout = new LinearLayout(activity);
						// LayoutParams datetimeLayoutParam = new LayoutParams(
						// LayoutParams.WRAP_CONTENT,
						// LayoutParams.WRAP_CONTENT);
						// datetimeLayoutParam.setMargins(0, 30, 0, 0);
						// datetimeLayout.setLayoutParams(datetimeLayoutParam);
						datetimeLayout.setLayoutParams(innerLayoutParams);
						// datetimeLayout.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_blue_light));
						// datetimeLayout.setBackgroundResource(R.drawable.datetime_background);
						datetimeLayout.setOrientation(LinearLayout.VERTICAL);
						datetimeLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						datetimeLayout.setTag(R.id.layout_id, "datetimeLayout");
						datetimeLayout.setTag(R.id.field_id,
								fields.get("field_id"));
						datetimeLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						datetimeLayout.setTag(R.id.field_required_id,
								fields.get("field_required"));
						datetimeLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						String dateTimeLabel = fields.get("field_label")
								.toString();
						datetimeLayout.setTag(R.id.field_ref,
								fields.get("field_ref"));
						datetimeLayout.setTag(R.id.next_cond,
								fields.get("next_cond"));
						TextView tvLabel = new TextView(activity);
						tvLabel.setText(dateTimeLabel);
						tvLabel.setTextSize(labelTextSize);
						String fieldName = fields.get("field_name").toString();
						String fieldRequired = fields.get("field_required")
								.toString();
						tvLabel.setTag(R.id.field_name_id, fieldName);
						tvLabel.setTag(R.id.field_required_id, fieldRequired);
						tvLabel.setLayoutParams(labelLayoutParams);
						setTypeFace(tvLabel);
						datetimeLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setText(description);
						tvdescription.setTextSize(18);
						tvdescription.setLayoutParams(labelLayoutParams);
						setTypeFace(tvdescription);
						datetimeLayout.addView(tvdescription);

						// date
						DatePicker datePicker = new DatePicker(activity);
						datePicker.setCalendarViewShown(false);
						LayoutParams lp = new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT);
						lp.setMargins(0, 0, 0, 0);
						datePicker.setLayoutParams(lp);
						datetimeLayout.addView(datePicker);

						// time
						TimePicker timePicker = new TimePicker(activity);
						timePicker.setIs24HourView(false);
						lp.setMargins(0, 0, 0, 0);
						timePicker.setLayoutParams(lp);
						datetimeLayout.addView(timePicker);

						scroll.addView(datetimeLayout);
						parentLayout.addView(scroll);

						layoutList.add(parentLayout);
					} else if (fields.get(key).equals("option")) {
						ScrollView scroll = new ScrollView(activity);
						scroll.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT));
						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);
						RelativeLayout upLayout = new RelativeLayout(activity);
						upLayout.setPadding(0, 10, 20, 0);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, relative_WRAP_CONTENT);
						upLayout.setLayoutParams(upLayoutParams);
						TextView index = new TextView(activity);
						RelativeLayout.LayoutParams tvLayoutParams = new android.widget.RelativeLayout.LayoutParams(
								relative_WRAP_CONTENT, relative_WRAP_CONTENT);
						tvLayoutParams
								.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						index.setLayoutParams(tvLayoutParams);
						index.setText("index/index");
						index.setTag("index");
						upLayout.addView(index);
						// upLayout.addView(getLine(lineColor));
						parentLayout.addView(upLayout);

						LinearLayout radioLayout = new LinearLayout(activity);
						// LayoutParams radioLayoutParams = new LayoutParams(
						// LayoutParams.MATCH_PARENT,
						// LayoutParams.WRAP_CONTENT);
						// radioLayoutParams.setMargins(0, 25, 0, 25);
						// radioLayout.setLayoutParams(radioLayoutParams);
						radioLayout.setLayoutParams(innerLayoutParams);
						// radioLayout.setBackgroundResource(R.drawable.linear_layout_ui);
						radioLayout.setOrientation(LinearLayout.VERTICAL);
						radioLayout.setTag(R.id.layout_id, "radioLayout");
						radioLayout.setTag(R.id.field_id,
								fields.get("field_id"));
						radioLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						radioLayout.setTag(R.id.field_required_id,
								fields.get("field_required"));
						radioLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						radioLayout.setTag(R.id.field_ref,
								fields.get("field_ref"));
						radioLayout.setTag(R.id.next_cond,
								fields.get("next_cond"));

						if (fields.containsKey("next_cond_type")) {
							radioLayout.setTag(R.id.next_cond_type,
									fields.get("next_cond_type"));
						}
						String field_default_value = fields.get(
								"field_default_value").toString();
						radioLayout.setTag(R.id.field_default_value,
								field_default_value);
						String radioLabel = fields.get("field_label")
								.toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setText(radioLabel);
						tvLabel.setTextSize(labelTextSize);
						String fieldName = fields.get("field_name").toString();
						String fieldRequired = fields.get("field_required")
								.toString();
						String fieldLabel = fields.get("field_label")
								.toString();
						tvLabel.setTag(R.id.field_name_id, fieldName);
						tvLabel.setTag(R.id.field_required_id, fieldRequired);
						tvLabel.setTag(R.id.field_label_id, fieldLabel);
						tvLabel.setGravity(Gravity.CENTER_VERTICAL);
						tvLabel.setLayoutParams(labelLayoutParams);
						setTypeFace(tvLabel);
						radioLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setText(description);
						tvdescription.setTextSize(descriptionTextSize);
						tvdescription.setLayoutParams(descriptionLayoutParams);
						setTypeFace(tvdescription);
						radioLayout.addView(tvdescription);

						final RadioGroup rg = new RadioGroup(activity);
						LayoutParams radioGroupParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						radioGroupParams.setMargins(15, 5, 15, 5);
						rg.setLayoutParams(radioGroupParams);

						JSONArray dataset = (JSONArray) fields
								.get("dataset_values");
						// int default_value = Integer.parseInt(fields.get(
						// "field_default_value").toString());
						int default_value = 1;
						int length = dataset.length();
						for (int j = 0; j < length; j++) {
							JSONObject obj = new JSONObject();
							obj = dataset.getJSONObject(j);
							String text = obj.getString("label");
							String tag = obj.getString("value");
							String skip = obj.getString("field_skip");
							boolean extra = obj.getBoolean("extra"); // thinking
																		// there
																		// is
																		// always
																		// extra
																		// ,
																		// true
																		// and
																		// false

							RadioButton radioButton = new RadioButton(activity);
							radioButton.setId(Utils.generateViewId()); // View.generateViewId();
																		// <=
																		// only
																		// support
																		// for
																		// lvl17
																		// n
																		// above
							radioButton.setText(text);
							radioButton.setTextSize(radioButtonTextSize);
							radioButton.setTag(R.id.radio_value, tag);
							radioButton.setTag(R.id.field_skip, skip);
							radioButton.setTag(R.id.extra, extra);
							radioButton.setTag(R.id.is_radiobutton_selected,
									false);
							radioButton.setSelected(false);

							if (tag.equals(field_default_value)) {
								radioButton.setSelected(true);
								radioButton.setChecked(true);
								radioButton.setTag(R.id.is_radiobutton_selected, true);
							}
							LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
									RadioGroup.LayoutParams.MATCH_PARENT,
									RadioGroup.LayoutParams.WRAP_CONTENT);

							EditText extraValue = new EditText(activity);
							extraValue.setLayoutParams(extraValueLayoutParamsDisappear);
							extraValue.setTag(R.id.extra_status, "off");

							layoutParams.setMargins(0, 10, 0, 10);
							// if (default_value == (j + 1)) {
							// radioButton.setChecked(true);
							// radioButton.setTag(R.id.is_radiobutton_selected,
							// true);
							// }
							setTypeFace(radioButton);

							// rg.addView(radioButton, j, layoutParams);\
							LinearLayout radioButtonLine = new LinearLayout(
									activity);
							radioButtonLine.setTag(R.id.layout_id,
									"radioButtonLine");
							radioButtonLine.setLayoutParams(radioButtonLineLayoutParams);
							radioButtonLine.setOrientation(LinearLayout.VERTICAL);
							radioButton.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											RadioButton clicked_button = (RadioButton) v;
											clicked_button.setTag(R.id.is_radiobutton_selected, true);

											// disable all other
											Random r = new Random();
											int randomValue = r.nextInt(1000);
											clicked_button.setTag(
													R.id.radio_random_value,
													randomValue);

											for (int i = 0; i < rg
													.getChildCount(); i++) {
												LinearLayout radioButtonLine_ = (LinearLayout) rg.getChildAt(i);
												// for loop child count of radio
												// Button line
												// if child == radiobutton ,
												// then assigned to button
												// if child == edit text , than
												// assigned to edittext
												// end loop

												RadioButton singleButtonFromAll = null;
												EditText singleEditTextFromAll = null;
												for (int j = 0; j < radioButtonLine_
														.getChildCount(); j++) {
													View child = radioButtonLine_.getChildAt(j);
													String child_class_name = child
															.getClass()
															.getName()
															.toString();
													if (child_class_name
															.equals("android.widget.RadioButton")) {
														singleButtonFromAll = (RadioButton) child;
													} else if (child_class_name
															.equals("android.widget.EditText")) {
														singleEditTextFromAll = (EditText) child;
													} else {
														// for other views for
														// future
														// just in case
													}
												}

												// now we got editext and radio
												// button
												boolean extra = Boolean
														.parseBoolean(singleButtonFromAll
																.getTag(R.id.extra)
																.toString());

												int randomed_ = 0;
												if (singleButtonFromAll
														.getTag(R.id.radio_random_value) != null) {
													randomed_ = Integer
															.parseInt(singleButtonFromAll
																	.getTag(R.id.radio_random_value)
																	.toString());
												}

												boolean isOtherButton = randomValue == randomed_;

												// radioButtonLine_.setBackgroundColor(Color.BLUE);

												if (!isOtherButton) {
													singleButtonFromAll
															.setChecked(false);
													singleButtonFromAll
															.setTag(R.id.is_radiobutton_selected,
																	false);
													radioButtonLine_
															.setTag(R.id.is_radiobutton_selected,
																	false);
													singleEditTextFromAll
															.setLayoutParams(extraValueLayoutParamsDisappear);
												} else {
													singleButtonFromAll
															.setChecked(true);
													singleButtonFromAll
															.setTag(R.id.is_radiobutton_selected,
																	true);
													radioButtonLine_
															.setTag(R.id.is_radiobutton_selected,
																	true);
													singleEditTextFromAll
															.requestFocus();
													if (extra) { // if extra
																	// value
																	// true;
														singleEditTextFromAll
																.setLayoutParams(extraValueLayoutParamsAppear);
													}
												}
											}

											// if(extraValue.getTag(R.id.extra_status).toString().equals("off")){
											// extraValue.setLayoutParams(extraValueLayoutParamsAppear);
											// extraValue.setTag(R.id.extra_status,"on");
											// }else{
											// extraValue.setLayoutParams(extraValueLayoutParamsDisappear);
											// extraValue.setTag(R.id.extra_status,"off");
											// }
										}
									});

							// extra par yin..
							/*
							 * if(radioButton.getTag(R.id.extra).toString().equals
							 * ("true")){ final EditText extraValue = new
							 * EditText(activity); extraValue.setLayoutParams(
							 * extraValueLayoutParamsAppear);
							 * extraValue.setTag(R.id.extra_status, "on");
							 * radioButtonLine.addView(extraValue); }
							 */
							if (radioButton.isChecked()) {
								radioButton.performClick();
							}
							radioButtonLine.addView(radioButton);
							radioButtonLine.addView(extraValue);
							rg.addView(radioButtonLine);
						}
						// for loop ends here

						// RelativeLayout radiobuttonLayout = new
						// RelativeLayout(activity);
						// int relative_MATCH_PARENT_ =
						// RelativeLayout.LayoutParams.MATCH_PARENT;
						// int relative_WRAP_CONTENT_ =
						// RelativeLayout.LayoutParams.WRAP_CONTENT;
						// RelativeLayout.LayoutParams radiobuttonLayoutParams =
						// new RelativeLayout.LayoutParams(
						// relative_MATCH_PARENT_, relative_WRAP_CONTENT_);
						// radiobuttonLayout.setLayoutParams(radiobuttonLayoutParams);
						// RadioButton radioButton = new RadioButton(activity);
						// radioButton.setId(View.generateViewId());
						// radioButton.setTextSize(13);
						// radiobuttonLayout.addView(radioButton);
						// EditText testText = new EditText(activity);
						// testText.setLayoutParams(labelLayoutParams);
						// radiobuttonLayout.addView(testText);
						// radioLayout.addView(radiobuttonLayout);

						if (getSelectedRadioButtonMyRadioGroup(rg) == null) {
							RadioButton firstButton = getRadioButtonByIndexMyRadioGroup(
									rg, 0);
							firstButton.setChecked(true);
						}

						radioLayout.addView(rg);
						radioLayout.setGravity(Gravity.CENTER);

						scroll.addView(radioLayout);
						parentLayout.addView(scroll);

						// audio stuff
						// String audiorequired =
						// fields.get("field_audio_required").toString();
						boolean isAudioRequired = Boolean.parseBoolean(fields.get("field_audio_required").toString());
						if (isAudioRequired) {
							Audio audioinfo = new Audio();
							audioinfo.setAudio_name(fieldName);
							recordingManager = new AudioRecordingManager(activity);
							recordingManager.setAudioInfo(audioinfo);
							recordingManager.setFileName(fieldName + " - "	+ getCurrentDocumentID());
							LinearLayout recordingLayout = recordingManager
									.getRecordingLayout();
							recordingLayout.setTag(R.id.layout_id, "recordingLayout");
							parentLayout.addView(recordingLayout);
						}

						layoutList.add(parentLayout);
					} else if (fields.get(key).equals("checklist")) {

						ScrollView scroll = new ScrollView(activity);
						scroll.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));

						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);
						RelativeLayout upLayout = new RelativeLayout(activity);
						upLayout.setPadding(0, 10, 20, 0);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, relative_WRAP_CONTENT);

						upLayout.setLayoutParams(upLayoutParams);

						TextView index = new TextView(activity);
						RelativeLayout.LayoutParams tvLayoutParams = new android.widget.RelativeLayout.LayoutParams(
								relative_WRAP_CONTENT, relative_WRAP_CONTENT);
						tvLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						index.setLayoutParams(tvLayoutParams);
						index.setText("index/index");
						index.setTag("index");
						upLayout.addView(index);
						// upLayout.addView(getLine(lineColor));
						parentLayout.addView(upLayout);

						LinearLayout checkBoxLayout = new LinearLayout(activity);
						// LayoutParams cbLayoutParams = new LayoutParams(
						// LayoutParams.MATCH_PARENT,
						// LayoutParams.WRAP_CONTENT);
						// cbLayoutParams.setMargins(0, 25, 0, 25);
						// checkBoxLayout.setLayoutParams(cbLayoutParams);
						checkBoxLayout.setLayoutParams(innerLayoutParams);
						// checkBoxLayout
						// .setBackgroundResource(R.drawable.linear_layout_ui);
						checkBoxLayout.setOrientation(LinearLayout.VERTICAL);
						checkBoxLayout.setTag(R.id.layout_id, "checkBoxLayout");
						checkBoxLayout.setTag(R.id.field_id, fields.get("field_id"));
						checkBoxLayout.setTag(R.id.field_name_id, fields.get("field_name"));
						checkBoxLayout.setTag(R.id.field_required_id, fields.get("field_required"));
						checkBoxLayout.setTag(R.id.field_label_id, fields.get("field_label"));
						checkBoxLayout.setTag(R.id.field_err_msg, fields.get("field_err_msg"));
						checkBoxLayout.setTag(R.id.field_ref, fields.get("field_ref"));
						checkBoxLayout.setTag(R.id.next_cond, fields.get("next_cond"));
						String checkLabel = fields.get("field_label").toString();

						TextView tvLabel = new TextView(activity);
						tvLabel.setText(checkLabel + " ");
						tvLabel.setTextSize(labelTextSize);
						String fieldName = fields.get("field_name").toString();
						String fieldRequired = fields.get("field_required")
								.toString();
						String fieldLabel = fields.get("field_label")
								.toString();
						tvLabel.setTag("label");
						tvLabel.setTag(R.id.field_name_id, fieldName);
						tvLabel.setTag(R.id.field_required_id, fieldRequired);
						tvLabel.setTag(R.id.field_label_id, fieldLabel);
						tvLabel.setLayoutParams(labelLayoutParams);
						setTypeFace(tvLabel);
						checkBoxLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc").toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setText(description);
						tvdescription.setTextSize(descriptionTextSize);
						tvdescription.setLayoutParams(descriptionLayoutParams);
						setTypeFace(tvdescription);
						checkBoxLayout.addView(tvdescription);

						JSONArray dataset = (JSONArray) fields
								.get("dataset_values");
						// int default_value = Integer.parseInt(fields.get(
						// "field_default_value").toString());
						int default_value = 1;
						int length = dataset.length();
						int checkboxCount = 0;
						for (int j = 0; j < length; j++) {
							JSONObject obj = new JSONObject();
							obj = dataset.getJSONObject(j);
							String text = obj.getString("label");
							String tag = obj.getString("value");
							CheckBox cb = new CheckBox(activity);
							cb.setText(text);
							cb.setTextSize(radioButtonTextSize); // same as radio
							cb.setTag(tag);
							cb.setTypeface(typeface.getTypeFace());
							LayoutParams cbParams = new LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT);
							cbParams.setMargins(15, 5, 15, 5);
							cb.setLayoutParams(cbParams);
							setTypeFace(cb);
							if (default_value == (j + 1)) {
								cb.setChecked(true);
							}
							checkBoxLayout.addView(cb);
							checkboxCount++;
						}
						// error msg
						TextView errorMsg = new TextView(activity);
						errorMsg.setLayoutParams(errorMsgLayoutParams);
						setTypeFace(errorMsg);
						errorMsg.setTextSize(12);
						errorMsg.setTag("errorMsg");
						checkBoxLayout.addView(errorMsg);

						scroll.addView(checkBoxLayout);
						parentLayout.addView(scroll);
						layoutList.add(parentLayout);
					} else if (fields.get(key).equals("location")) {

						ScrollView scroll = new ScrollView(activity);
						scroll.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));

						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);
						RelativeLayout upLayout = new RelativeLayout(activity);
						upLayout.setPadding(0, 10, 20, 0);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, relative_MATCH_PARENT);
						upLayout.setLayoutParams(upLayoutParams);
						TextView index = new TextView(activity);
						RelativeLayout.LayoutParams tvLayoutParams = new android.widget.RelativeLayout.LayoutParams(
								relative_WRAP_CONTENT, relative_WRAP_CONTENT);
						tvLayoutParams
								.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						index.setLayoutParams(tvLayoutParams);
						index.setText("index/index");
						index.setTag("index");
						upLayout.addView(index);
						// upLayout.addView(getLine(lineColor));
						parentLayout.addView(upLayout);

						final LinearLayout locationLayout = new LinearLayout(
								activity);
						// LayoutParams locationLayoutParams = new LayoutParams(
						// LayoutParams.MATCH_PARENT,
						// LayoutParams.WRAP_CONTENT);
						// locationLayoutParams.setMargins(0, 20, 0, 0);
						// locationLayout.setLayoutParams(locationLayoutParams);
						locationLayout.setLayoutParams(innerLayoutParams);
						locationLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						// locationLayout.setBackgroundResource(R.drawable.linear_layout_ui);
						locationLayout.setOrientation(LinearLayout.VERTICAL);
						locationLayout.setPadding(0, 10, 0, 0);
						locationLayout.setTag(R.id.layout_id, "locationLayout");
						locationLayout.setTag(R.id.field_id,
								fields.get("field_id"));
						locationLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						locationLayout.setTag(R.id.field_required_id,
								fields.get("field_required"));
						locationLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						locationLayout.setTag(R.id.field_err_msg,
								fields.get("field_err_msg"));
						locationLayout.setTag(R.id.field_ref,
								fields.get("field_ref"));
						locationLayout.setTag(R.id.next_cond,
								fields.get("next_cond"));
						String field_label = fields.get("field_label")
								.toString();
						String field_name = fields.get("field_name").toString();
						String field_required = fields.get("field_required")
								.toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setText(field_label + " : ");
						tvLabel.setTag("label");
						tvLabel.setTag(R.id.field_name_id, field_name);
						tvLabel.setTag(R.id.field_required_id, field_required);
						tvLabel.setTag(R.id.field_label_id, field_label);
						// tvLabel.setTag(fields.get("field_name"));
						tvLabel.setLayoutParams(labelLayoutParams);
						setTypeFace(tvLabel);
						locationLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setPadding(10, 0, 0, 0);
						tvdescription.setText(description);
						tvdescription.setTextSize(18);
						tvdescription.setLayoutParams(labelLayoutParams);
						setTypeFace(tvdescription);
						locationLayout.addView(tvdescription);

						int sevenPercentHeight = displayManager.getHeigth(7);

						LayoutParams editTextLayoutParam = new LayoutParams(
								LayoutParams.MATCH_PARENT, sevenPercentHeight);
						editTextLayoutParam.setMargins(10, 5, 10, 15);
						final EditText edtLat = new EditText(activity);
						edtLat.setEnabled(false);
						edtLat.setHint("Latitude");
						edtLat.setPadding(5, 0, 0, 0);
						edtLat.setLayoutParams(editTextLayoutParam); //
						edtLat.setBackgroundResource(R.drawable.edittext_style);
						final EditText edtLong = new EditText(activity);
						edtLong.setEnabled(false);
						edtLong.setHint("Longitude");
						edtLong.setPadding(5, 0, 0, 0);
						edtLong.setLayoutParams(editTextLayoutParam); //
						setTypeFace(edtLat);
						setTypeFace(edtLong);
						edtLong.setBackgroundResource(R.drawable.edittext_style);
						locationLayout.addView(edtLat);
						locationLayout.addView(edtLong);
						int sevenPerscentHeight = displayManager.getHeigth(7);
						LayoutParams buttonLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT, sevenPerscentHeight);
						buttonLayoutParams.setMargins(30, 20, 30, 20);
						Button btnGPS = new Button(activity);
						btnGPS.setLayoutParams(buttonLayoutParams);
						btnGPS.setText("Get Location");
						btnGPS.setBackgroundResource(R.drawable.button_border);
						btnGPS.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (gps.canGetLocation()) {
									double latitude = gps.getLatitude();
									double longitude = gps.getLongitude();
									edtLat.setTag(latitude);
									edtLong.setTag(longitude);
									edtLat.setText(latitude + "");
									edtLong.setText(longitude + "");
								} else {
									gps.showSettingsAlert();
								}
							}
						});
						setTypeFace(btnGPS);

						// error msg
						TextView errorMsg = new TextView(activity);
						errorMsg.setLayoutParams(errorMsgLayoutParams);
						setTypeFace(errorMsg);
						errorMsg.setTextSize(12);
						errorMsg.setTag("errorMsg");
						locationLayout.addView(errorMsg);
						locationLayout.addView(btnGPS);

						scroll.addView(locationLayout);
						parentLayout.addView(scroll);

						layoutList.add(parentLayout);
					} else if (fields.get(key).equals("drawing")) {

						ScrollView scroll = new ScrollView(activity);
						scroll.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));

						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						int fivePercentWidth = displayManager.getWidth(5);
						parentLayoutParams.setMargins(fivePercentWidth, 0,
								fivePercentWidth, 0);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);
						RelativeLayout upLayout = new RelativeLayout(activity);
						upLayout.setPadding(0, 10, 20, 0);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, relative_WRAP_CONTENT);
						upLayout.setLayoutParams(upLayoutParams);
						TextView index = new TextView(activity);
						RelativeLayout.LayoutParams tvLayoutParams = new android.widget.RelativeLayout.LayoutParams(
								relative_WRAP_CONTENT, relative_WRAP_CONTENT);
						tvLayoutParams
								.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						index.setLayoutParams(tvLayoutParams);
						index.setText("index/index");
						index.setTag("index");
						upLayout.addView(index);
						// upLayout.addView(getLine(lineColor));
						parentLayout.addView(upLayout);

						final LinearLayout drawingLayout = new LinearLayout(
								activity);
						LayoutParams drawingLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						drawingLayoutParams.setMargins(5, 20, 5, 0);
						drawingLayout.setLayoutParams(drawingLayoutParams);
						drawingLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						// drawingLayout
						// .setBackgroundResource(R.drawable.linear_layout_ui);
						drawingLayout.setOrientation(LinearLayout.VERTICAL);
						drawingLayout.setPadding(0, 10, 0, 0);
						drawingLayout.setTag(R.id.layout_id, "drawingLayout");
						drawingLayout.setTag(R.id.field_id,
								fields.get("field_id"));
						drawingLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						drawingLayout.setTag(R.id.field_required_id,
								fields.get("field_required"));
						drawingLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						drawingLayout.setTag(R.id.field_err_msg,
								fields.get("field_err_msg"));
						drawingLayout.setTag(R.id.field_ref,
								fields.get("field_ref"));
						drawingLayout.setTag(R.id.next_cond,
								fields.get("next_cond"));
						String field_label = fields.get("field_label")
								.toString();
						String field_name = fields.get("field_name").toString();
						String field_required = fields.get("field_required")
								.toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setText(field_label + " : ");

						tvLabel.setTag("label");
						tvLabel.setTag(R.id.field_name_id, field_name);
						tvLabel.setTag(R.id.field_required_id, field_required);
						tvLabel.setTag(R.id.field_label_id, field_label);
						// tvLabel.setTag(fields.get("field_name"));
						tvLabel.setLayoutParams(labelLayoutParams);
						setTypeFace(tvLabel);
						drawingLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setPadding(10, 0, 0, 0);
						tvdescription.setText(description);
						tvdescription.setTextSize(18);
						tvdescription.setLayoutParams(labelLayoutParams);
						setTypeFace(tvdescription);
						drawingLayout.addView(tvdescription);

						ImageView imgView = new ImageView(
								activity.getBaseContext());
						final int randomID = randInt(1, 10000);
						imgView.setId(randomID);

						int sixtyPercentOfWidth = displayManager.getWidth(60);

						imgView.setLayoutParams(new LayoutParams(
								sixtyPercentOfWidth, sixtyPercentOfWidth));
						// imgView.setBackgroundColor(Color.parseColor("#abcdef"));
						imgView.setBackgroundResource(R.drawable.imageview_rounded);
						drawingLayout.addView(imgView);
						Button button = new Button(activity);
						int sevenPerscentHeight = displayManager.getHeigth(7);
						LayoutParams buttonLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT, sevenPerscentHeight);
						setTypeFace(button);
						buttonLayoutParams.setMargins(30, 20, 30, 20);
						button.setLayoutParams(buttonLayoutParams);
						button.setText("Add");
						button.setBackgroundResource(R.drawable.button_border);
						final String field_help = fields.get("field_help")
								.toString();
						final String field_name_ = fields.get("field_name")
								.toString();
						final String field_type_ = fields.get("field_type")
								.toString();
						button.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent i = new Intent(activity,
										DrawSignature.class);
								i.putExtra("field_help", field_help);
								i.putExtra("field_name", field_name_);
								i.putExtra("field_type", field_type_);
								i.putExtra("view_id",
										Integer.toString(randomID));
								activity.startActivityForResult(i,
										ApplicationValues.REQUEST_DRAWING);
							}
						});

						LayoutParams errorMsgLayoutParams2 = new LayoutParams(
								LayoutParams.MATCH_PARENT, 60);
						errorMsgLayoutParams2.setMargins(15, 20, 15, 0);

						// error msg
						TextView errorMsg = new TextView(activity);
						errorMsg.setLayoutParams(errorMsgLayoutParams2);
						setTypeFace(errorMsg);
						errorMsg.setTextSize(12);
						errorMsg.setTag("errorMsg");
						drawingLayout.addView(errorMsg);
						drawingLayout.addView(button);

						scroll.addView(drawingLayout);
						parentLayout.addView(scroll);

						layoutList.add(parentLayout);
					} else if (fields.get(key).equals("photo")) {

						ScrollView scroll = new ScrollView(activity);
						scroll.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));

						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						int fivePercentWidth = displayManager.getWidth(5);
						parentLayoutParams.setMargins(fivePercentWidth, 0,
								fivePercentWidth, 0);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);
						RelativeLayout upLayout = new RelativeLayout(activity);
						upLayout.setPadding(0, 10, 20, 0);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, relative_WRAP_CONTENT);
						upLayout.setLayoutParams(upLayoutParams);
						TextView index = new TextView(activity);
						RelativeLayout.LayoutParams tvLayoutParams = new android.widget.RelativeLayout.LayoutParams(
								relative_WRAP_CONTENT, relative_WRAP_CONTENT);
						tvLayoutParams
								.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						index.setLayoutParams(tvLayoutParams);
						index.setText("index/index");
						index.setTag("index");
						upLayout.addView(index);
						// upLayout.addView(getLine(lineColor));
						parentLayout.addView(upLayout);

						final LinearLayout photoLayout = new LinearLayout(
								activity);
						LayoutParams photoLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						photoLayoutParams.setMargins(5, 20, 5, 0);
						photoLayout.setLayoutParams(photoLayoutParams);
						photoLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						// photoLayout
						// .setBackgroundResource(R.drawable.linear_layout_ui);
						photoLayout.setOrientation(LinearLayout.VERTICAL);
						photoLayout.setPadding(0, 10, 0, 10);
						photoLayout.setTag(R.id.layout_id, "photoLayout");
						photoLayout.setTag(R.id.field_id,
								fields.get("field_id"));
						photoLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						photoLayout.setTag(R.id.field_required_id,
								fields.get("field_required"));
						photoLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						photoLayout.setTag(R.id.field_err_msg,
								fields.get("field_err_msg"));
						photoLayout.setTag(R.id.field_ref,
								fields.get("field_ref"));
						photoLayout.setTag(R.id.next_cond,
								fields.get("next_cond"));
						String field_label = fields.get("field_label")
								.toString();
						String field_name = fields.get("field_name").toString();
						String field_required = fields.get("field_required")
								.toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setText(field_label);
						tvLabel.setTag("label");
						tvLabel.setTag(R.id.field_name_id, field_name);
						tvLabel.setTag(R.id.field_required_id, field_required);
						tvLabel.setTag(R.id.field_label_id, field_label);
						// tvLabel.setTag(fields.get("field_name"));
						tvLabel.setLayoutParams(labelLayoutParams);
						setTypeFace(tvLabel);
						photoLayout.addView(tvLabel);

						ImageView photoPreView = new ImageView(
								activity.getBaseContext());
						photoPreView
								.setBackgroundResource(R.drawable.imageview_rounded);
						final int randomID = randInt(1, 10000);
						photoPreView.setId(randomID);
						int sixtyPercentOfWidth = displayManager.getWidth(60);
						photoPreView.setLayoutParams(new LayoutParams(
								sixtyPercentOfWidth, sixtyPercentOfWidth));
						photoLayout.addView(photoPreView);

						Button button = new Button(activity);
						int sevenPerscentHeight = displayManager.getHeigth(7);
						LayoutParams buttonLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT, sevenPerscentHeight);
						buttonLayoutParams.setMargins(30, 20, 30, 10);
						setTypeFace(button);
						button.setLayoutParams(buttonLayoutParams);
						button.setText("Select Photo");
						button.setBackgroundResource(R.drawable.button_border);
						final String field_help_ = fields.get("field_help")
								.toString();
						final String field_name_ = fields.get("field_name")
								.toString();
						final String field_type_ = fields.get("field_type")
								.toString();
						final String view_id_ = Integer.toString(randomID);
						button.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								selectImage(field_name_, field_type_,
										field_help_, view_id_);
							}
						});

						LayoutParams errorMsgLayoutParams2 = new LayoutParams(
								LayoutParams.MATCH_PARENT, 60);
						errorMsgLayoutParams2.setMargins(15, 20, 15, 0);

						// error msg
						TextView errorMsg = new TextView(activity);
						errorMsg.setLayoutParams(errorMsgLayoutParams2);
						setTypeFace(errorMsg);
						errorMsg.setTextSize(12);
						errorMsg.setTag("errorMsg");
						photoLayout.addView(errorMsg);
						photoLayout.addView(button);

						scroll.addView(photoLayout);

						parentLayout.addView(scroll);
						layoutList.add(parentLayout);
					} else if (fields.get(key).equals("note")) {

						ScrollView scroll = new ScrollView(activity);
						scroll.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));

						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);

						RelativeLayout upLayout = new RelativeLayout(activity);
						upLayout.setPadding(0, 10, 20, 0);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, relative_WRAP_CONTENT);
						upLayout.setLayoutParams(upLayoutParams);

						TextView index = new TextView(activity);
						RelativeLayout.LayoutParams tvLayoutParams = new android.widget.RelativeLayout.LayoutParams(
								relative_WRAP_CONTENT, relative_WRAP_CONTENT);
						tvLayoutParams
								.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						index.setLayoutParams(tvLayoutParams);
						index.setText("index/index");
						index.setTag("index");
						upLayout.addView(index);
						// upLayout.addView(getLine(lineColor));
						parentLayout.addView(upLayout);

						LinearLayout noteLayout = new LinearLayout(activity);
						noteLayout.setLayoutParams(innerLayoutParams);
						// numberLayout
						// .setBackgroundResource(R.drawable.linear_layout_ui);
						noteLayout.setOrientation(LinearLayout.VERTICAL);
						noteLayout.setTag(R.id.layout_id, "noteLayout");
						noteLayout
								.setTag(R.id.field_id, fields.get("field_id"));
						noteLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						noteLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						noteLayout.setTag(R.id.field_default_value,
								fields.get("field_default_value"));
						noteLayout.setTag(R.id.field_ref,
								fields.get("field_ref"));
						noteLayout.setTag(R.id.next_cond,
								fields.get("next_cond"));
						String textLabel = fields.get("field_label").toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setText(textLabel);
						tvLabel.setTextSize(labelTextSize);
						tvLabel.setLayoutParams(labelLayoutParams);
						setTypeFace(tvLabel);
						noteLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc").toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setText(description);
						tvdescription.setTextSize(descriptionTextSize);
						tvdescription.setLayoutParams(descriptionLayoutParams);
						// tvdescription.setTextScaleX(1.1f);

						setTypeFace(tvdescription);
						noteLayout.addView(tvdescription);

						scroll.addView(noteLayout);
						parentLayout.addView(scroll);
						layoutList.add(parentLayout);
					} 
					//<matrix_checklist>
					else if (fields.get(key).equals("matrix_checklist")) {
						ScrollView scroll = new ScrollView(activity);
						scroll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);
						RelativeLayout upLayout = new RelativeLayout(activity);
						upLayout.setPadding(0, 10, 20, 0);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, relative_WRAP_CONTENT);
						upLayout.setLayoutParams(upLayoutParams);
						TextView index = new TextView(activity);
						RelativeLayout.LayoutParams tvLayoutParams = new android.widget.RelativeLayout.LayoutParams(
								relative_WRAP_CONTENT, relative_WRAP_CONTENT);
						tvLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						index.setLayoutParams(tvLayoutParams);
						index.setText("index/index");
						index.setTag("index");
						upLayout.addView(index);
						// upLayout.addView(getLine(lineColor));
						parentLayout.addView(upLayout);

						LinearLayout matrixChecklistLayout = new LinearLayout(activity);
						LayoutParams matrixSingleLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						matrixSingleLayoutParams.setMargins(5, 20, 5, 0);
						matrixChecklistLayout.setLayoutParams(matrixSingleLayoutParams);
						matrixChecklistLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						matrixChecklistLayout.setOrientation(LinearLayout.VERTICAL);
						matrixChecklistLayout.setPadding(0, 10, 0, 10);
						matrixChecklistLayout.setTag(R.id.layout_id,"matrixCheckListLayout");
						matrixChecklistLayout.setTag(R.id.field_id, fields.get("field_id"));
						matrixChecklistLayout.setTag(R.id.field_name_id, fields.get("field_name"));
						matrixChecklistLayout.setTag(R.id.field_required_id, fields.get("field_required"));
						matrixChecklistLayout.setTag(R.id.field_label_id, fields.get("field_label"));
						matrixChecklistLayout.setTag(R.id.field_err_msg, "-");
						matrixChecklistLayout.setTag(R.id.field_ref, fields.get("field_ref"));
						matrixChecklistLayout.setTag(R.id.next_cond,
								fields.get("next_cond"));
						String field_label = fields.get("field_label")
								.toString();
						String field_name = fields.get("field_name").toString();
						String field_required = fields.get("field_required")
								.toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setText(field_label);
						tvLabel.setTextSize(labelTextSize);
						// tvLabel.setTag(fields.get("field_name"));
						tvLabel.setLayoutParams(labelLayoutParams);
						setTypeFace(tvLabel);
						matrixChecklistLayout.addView(tvLabel);
						
						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setText(description);
						tvdescription.setTextSize(descriptionTextSize);
						tvdescription.setLayoutParams(descriptionLayoutParams);
						tvdescription.setPadding(0, 5, 0, 5);
						setTypeFace(tvdescription);
						matrixChecklistLayout.addView(tvdescription);
						
						LinearLayout theMatrixLayout = new LinearLayout(activity);
						theMatrixLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
						theMatrixLayout.setOrientation(LinearLayout.HORIZONTAL);
						theMatrixLayout.setTag(R.id.layout_id, "theMatrixLayout");
						
						// <dataset_v stuffs>
						JSONObject field_dataset_v = (JSONObject) fields
								.get("field_dataset_v");
						JSONArray v_values = field_dataset_v
								.getJSONArray("dataset_values");
						String v_name = field_dataset_v
								.getString("dataset_name");

						ArrayList<HashMap<String, String>> v_values_list = new ArrayList<HashMap<String, String>>();
						for (int v = 0; v < v_values.length(); v++) {
							String value = v_values.getJSONObject(v).getString(
									"value");
							String label = v_values.getJSONObject(v).getString(
									"label");
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("value", value);
							map.put("label", label);
							v_values_list.add(map);
						}
						// </dataset_v stuffs>

						// <dataset_h stuffs>
						JSONObject field_dataset_h = (JSONObject) fields
								.get("field_dataset_h");
						JSONArray h_values = field_dataset_h
								.getJSONArray("dataset_values");
						String h_name = field_dataset_h
								.getString("dataset_name");

						ArrayList<HashMap<String, String>> h_values_list = new ArrayList<HashMap<String, String>>();
						for (int v = 0; v < h_values.length(); v++) {
							String value = h_values.getJSONObject(v).getString("value");
							String label = h_values.getJSONObject(v).getString("label");
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("value", value);
							map.put("label", label);
							h_values_list.add(map);
						}
						// </dataset_h stuffs>

						// < matrix stuffs >
						JSONArray matrix_values = (JSONArray) fields.get("matrix_values");

						int v_count = v_values_list.size();
						int h_count = h_values_list.size();

						// grouping all cells by each column
						LinkedList<ArrayList<MatrixCell>> columnSetCollection = new LinkedList<ArrayList<MatrixCell>>();

						for (int h = 0; h < h_count; h++) {
							ArrayList<MatrixCell> columnSet = new ArrayList<MatrixCell>();

							for (int count = 0; count < matrix_values.length(); count++) {
								JSONObject cell_values = matrix_values.getJSONObject(count);
								String fieldSkip = cell_values.getString("field_skip");
								String value = cell_values.getString("value");
								String index_ = cell_values.getString("index");
								String[] parts = index_.split(",");
								int h_ = Integer.parseInt(parts[0]);
								int v_ = Integer.parseInt(parts[1]);

								if (h_ == h) {
									MatrixCell cell = new MatrixCell();
									cell.setFieldSkip(fieldSkip);
									cell.setValue(value);
									cell.setH_index(h_);
									cell.setV_index(v_);
									columnSet.add(cell);
								}
							}
							columnSetCollection.add(columnSet);
						}
						// <producing row_label_set>
						LinearLayout row_label_column_set = new LinearLayout(activity);
						row_label_column_set.setTag(R.id.layout_id, "rowLabelColumn");
						int cell_row_width = 200;
						int cell_column_width = 150;
						int cell_column_height= 70;

						LayoutParams rowLabelLayoutParams = new LayoutParams(
								cell_row_width, LayoutParams.WRAP_CONTENT);
						row_label_column_set.setLayoutParams(rowLabelLayoutParams);
						row_label_column_set.setOrientation(LinearLayout.VERTICAL);
						row_label_column_set.setGravity(Gravity.CENTER);

						LinearLayout blankLayout = new LinearLayout(activity);
						LayoutParams blankLayoutParams = new LayoutParams(
								cell_row_width, cell_column_height);
						blankLayout.setLayoutParams(blankLayoutParams);
						blankLayout.setGravity(Gravity.CENTER);
						TextView blankText = new TextView(activity);
						blankText.setLayoutParams(new LayoutParams(cell_row_width, cell_column_height));
						blankText.setText("     ");
						// row_label_column_set is the left vertical column
						blankLayout.addView(blankText);
						row_label_column_set.addView(blankLayout);
						theMatrixLayout.addView(row_label_column_set);

						for (int r = 0; r < v_values_list.size(); r++) {
							LinearLayout rowLabelCells = new LinearLayout(
									activity);

							LayoutParams rowLabelCellsParams = new LayoutParams(
									cell_row_width,
									cell_column_height);
							rowLabelCells.setLayoutParams(rowLabelCellsParams);

							TextView rowLabel = new TextView(activity);
							LayoutParams rowLabelParams = new LayoutParams(
									cell_row_width,
									cell_column_height);
							rowLabel.setLayoutParams(rowLabelParams);
							rowLabel.setText(v_values_list.get(r).get("label")
									.toString());
							rowLabel.setGravity(Gravity.START);
							//rowLabel.setBackgroundColor(Color.parseColor("#49F2D6"));
							// value may be useless here
							row_label_column_set.addView(rowLabel);
						}
						// </producing row_label_set>

						int maxHeight = 0;

						// <prepare AllColumns
						LinearLayout AllColumns = new LinearLayout(activity);
						AllColumns.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
						AllColumns.setOrientation(LinearLayout.HORIZONTAL);
						AllColumns.setTag(R.id.layout_id, "AllColumns");
						
						// <prepare columnSet>
						for (int column = 0; column < columnSetCollection.size(); column++) {
							ArrayList<MatrixCell> columnSet = columnSetCollection.get(column);
							LinearLayout columnLayout = new LinearLayout(activity);
							LayoutParams columnLayoutParams = new LayoutParams(
									cell_column_width,LayoutParams.WRAP_CONTENT);
							columnLayout.setLayoutParams(columnLayoutParams);
							columnLayout.setOrientation(LinearLayout.VERTICAL);
							columnLayout.setGravity(Gravity.CENTER);
							columnLayout.setTag(R.id.layout_id, "columnLayout");

							// adding columnTitle
							String columnTitle = h_values_list.get(column).get("label");
							TextView tvColumnTitle = new TextView(activity);
							tvColumnTitle.setLayoutParams(new LayoutParams(cell_column_width, cell_column_height));
							tvColumnTitle.setText(columnTitle);
							//tvColumnTitle.setBackgroundColor(Color.parseColor("#aabbcc"));
							tvColumnTitle.setGravity(Gravity.CENTER);
							tvColumnTitle.setTag(R.id.layout_id, "columnTitle");
							// following code dosen't work
//							if(tvColumnTitle.getLayoutParams().height>maxHeight){
//								maxHeight = tvColumnTitle.getHeight();
//							}

							columnLayout.addView(tvColumnTitle);

							for (int cs = 0; cs < columnSet.size(); cs++) {
								MatrixCell cell = columnSet.get(cs);
								LinearLayout singleCheckBoxLayout = new LinearLayout(activity);
								singleCheckBoxLayout.setLayoutParams(new LayoutParams(cell_column_width, cell_column_height));
								singleCheckBoxLayout.setGravity(Gravity.CENTER);
								singleCheckBoxLayout.setTag(R.id.layout_id, "cell");
								CheckBox cb = new CheckBox(activity);
								LayoutParams checkBoxParams = new LayoutParams(
										LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
								cb.setLayoutParams(checkBoxParams);
								cb.setTag(R.id.matrix_cell, cell);
								//cb.setGravity(Gravity.CENTER); // <-- center horizontal didn't work here
								singleCheckBoxLayout.addView(cb);
								//singleCheckBoxLayout.setBackgroundColor(Color.parseColor("#5abde4"));
								columnLayout.addView(singleCheckBoxLayout);
							}
							//here
							AllColumns.addView(columnLayout);
							//theMatrixLayout.addView(columnLayout);
						}
						// </prepare columnSet>

						// </matrix stuffs>

						int aaaaa = maxHeight;
						
						MYHorizontalScrollView horizontalScrollView = new MYHorizontalScrollView(activity);
						horizontalScrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
						horizontalScrollView.addView(AllColumns);
						horizontalScrollView.setTag(R.id.layout_id, "horizontalScrollView");
						horizontalScrollView.requestDisallowInterceptTouchEvent(false);
						theMatrixLayout.addView(horizontalScrollView);
						matrixChecklistLayout.addView(theMatrixLayout);

						// error msg
						TextView errorMsg = new TextView(activity);
						errorMsg.setLayoutParams(errorMsgLayoutParams);
						setTypeFace(errorMsg);
						errorMsg.setTextSize(12);
						errorMsg.setTag("errorMsg");
						matrixChecklistLayout.addView(errorMsg);

						scroll.addView(matrixChecklistLayout);
						parentLayout.addView(scroll);

						layoutList.add(parentLayout);
					}
					//</matrix_checklist>
					
					//<matrix_>
					else if (fields.get(key).equals("matrix_option")) {
						ScrollView scroll = new ScrollView(activity);
						scroll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);
						RelativeLayout upLayout = new RelativeLayout(activity);
						upLayout.setPadding(0, 10, 20, 0);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, relative_WRAP_CONTENT);
						upLayout.setLayoutParams(upLayoutParams);
						TextView index = new TextView(activity);
						RelativeLayout.LayoutParams tvLayoutParams = new android.widget.RelativeLayout.LayoutParams(
								relative_WRAP_CONTENT, relative_WRAP_CONTENT);
						tvLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						index.setLayoutParams(tvLayoutParams);
						index.setText("index/index");
						index.setTag("index");
						upLayout.addView(index);
						// upLayout.addView(getLine(lineColor));
						parentLayout.addView(upLayout);

						ArrayList<RadioButton> radioButtonList = new ArrayList<RadioButton>();
						
						LinearLayout matrixOptionLayout = new LinearLayout(activity);
						LayoutParams matrixRadioLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						matrixRadioLayoutParams.setMargins(5, 20, 5, 0);
						matrixOptionLayout.setLayoutParams(matrixRadioLayoutParams);
						matrixOptionLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						matrixOptionLayout.setOrientation(LinearLayout.VERTICAL);
						matrixOptionLayout.setPadding(0, 10, 0, 10);
						matrixOptionLayout.setTag(R.id.layout_id,"matrixOptionLayout");
						matrixOptionLayout.setTag(R.id.field_id, fields.get("field_id"));
						matrixOptionLayout.setTag(R.id.field_name_id, fields.get("field_name"));
						matrixOptionLayout.setTag(R.id.field_required_id, fields.get("field_required"));
						matrixOptionLayout.setTag(R.id.field_label_id, fields.get("field_label"));
						matrixOptionLayout.setTag(R.id.field_err_msg, "-");
						matrixOptionLayout.setTag(R.id.field_ref, fields.get("field_ref"));
						matrixOptionLayout.setTag(R.id.next_cond,
								fields.get("next_cond"));
						String field_label = fields.get("field_label")
								.toString();
						String field_name = fields.get("field_name").toString();
						String field_required = fields.get("field_required")
								.toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setText(field_label);
						tvLabel.setTextSize(labelTextSize);
						// tvLabel.setTag(fields.get("field_name"));
						tvLabel.setLayoutParams(labelLayoutParams);
						setTypeFace(tvLabel);
						matrixOptionLayout.addView(tvLabel);
						
						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setText(description);
						tvdescription.setTextSize(descriptionTextSize);
						tvdescription.setLayoutParams(descriptionLayoutParams);
						tvdescription.setPadding(0, 5, 0, 5);
						setTypeFace(tvdescription);
						matrixOptionLayout.addView(tvdescription);
						
						LinearLayout theMatrixLayout = new LinearLayout(activity);
						theMatrixLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
						theMatrixLayout.setOrientation(LinearLayout.HORIZONTAL);
						theMatrixLayout.setTag(R.id.layout_id, "theMatrixLayout");
						
						// <dataset_v stuffs>
						JSONObject field_dataset_v = (JSONObject) fields
								.get("field_dataset_v");
						JSONArray v_values = field_dataset_v
								.getJSONArray("dataset_values");
						String v_name = field_dataset_v
								.getString("dataset_name");

						ArrayList<HashMap<String, String>> v_values_list = new ArrayList<HashMap<String, String>>();
						for (int v = 0; v < v_values.length(); v++) {
							String value = v_values.getJSONObject(v).getString(
									"value");
							String label = v_values.getJSONObject(v).getString(
									"label");
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("value", value);
							map.put("label", label);
							v_values_list.add(map);
						}
						// </dataset_v stuffs>

						// <dataset_h stuffs>
						JSONObject field_dataset_h = (JSONObject) fields
								.get("field_dataset_h");
						JSONArray h_values = field_dataset_h
								.getJSONArray("dataset_values");
						String h_name = field_dataset_h
								.getString("dataset_name");

						ArrayList<HashMap<String, String>> h_values_list = new ArrayList<HashMap<String, String>>();
						for (int v = 0; v < h_values.length(); v++) {
							String value = h_values.getJSONObject(v).getString("value");
							String label = h_values.getJSONObject(v).getString("label");
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("value", value);
							map.put("label", label);
							h_values_list.add(map);
						}
						// </dataset_h stuffs>

						// < matrix stuffs >
						JSONArray matrix_values = (JSONArray) fields.get("matrix_values");

						int v_count = v_values_list.size();
						int h_count = h_values_list.size();

						// grouping all cells by each column
						LinkedList<ArrayList<MatrixCell>> columnSetCollection = new LinkedList<ArrayList<MatrixCell>>();

						for (int h = 0; h < h_count; h++) {
							ArrayList<MatrixCell> columnSet = new ArrayList<MatrixCell>();

							for (int count = 0; count < matrix_values.length(); count++) {
								JSONObject cell_values = matrix_values.getJSONObject(count);
								String fieldSkip = cell_values.getString("field_skip");
								String value = cell_values.getString("value");
								String index_ = cell_values.getString("index");
								String[] parts = index_.split(",");
								int h_ = Integer.parseInt(parts[0]);
								int v_ = Integer.parseInt(parts[1]);

								if (h_ == h) {
									MatrixCell cell = new MatrixCell();
									cell.setFieldSkip(fieldSkip);
									cell.setValue(value);
									cell.setH_index(h_);
									cell.setV_index(v_);
									columnSet.add(cell);
								}
							}
							columnSetCollection.add(columnSet);
						}
						// <producing row_label_set>
						LinearLayout row_label_column_set = new LinearLayout(activity);
						row_label_column_set.setTag(R.id.layout_id, "rowLabelColumn");
						int cell_row_width = 200;
						int cell_column_width = 150;
						int cell_column_height= 70;

						LayoutParams rowLabelLayoutParams = new LayoutParams(
								cell_row_width, LayoutParams.WRAP_CONTENT);
						row_label_column_set.setLayoutParams(rowLabelLayoutParams);
						row_label_column_set.setOrientation(LinearLayout.VERTICAL);
						row_label_column_set.setGravity(Gravity.CENTER);

						LinearLayout blankLayout = new LinearLayout(activity);
						LayoutParams blankLayoutParams = new LayoutParams(
								cell_row_width, cell_column_height);
						blankLayout.setLayoutParams(blankLayoutParams);
						blankLayout.setGravity(Gravity.CENTER);
						TextView blankText = new TextView(activity);
						blankText.setLayoutParams(new LayoutParams(cell_row_width, cell_column_height));
						blankText.setText("     ");
						// row_label_column_set is the left vertical column
						blankLayout.addView(blankText);
						row_label_column_set.addView(blankLayout);

						for (int r = 0; r < v_values_list.size(); r++) {
							LinearLayout rowLabelCells = new LinearLayout(
									activity);

							LayoutParams rowLabelCellsParams = new LayoutParams(
									cell_row_width,
									cell_column_height);
							rowLabelCells.setLayoutParams(rowLabelCellsParams);

							TextView rowLabel = new TextView(activity);
							LayoutParams rowLabelParams = new LayoutParams(
									cell_row_width,
									cell_column_height);
							rowLabel.setLayoutParams(rowLabelParams);
							rowLabel.setText(v_values_list.get(r).get("label")
									.toString());
							rowLabel.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
							//rowLabel.setBackgroundColor(Color.parseColor("#49F2D6"));
							// value may be useless here

							row_label_column_set.addView(rowLabel);
							
						}
						
						theMatrixLayout.addView(row_label_column_set);
						theMatrixLayout.setPadding(10, 0, 0, 0);
						// </producing row_label_set>

						int maxHeight = 0;

						// <prepare AllColumns
						LinearLayout AllColumns = new LinearLayout(activity);
						AllColumns.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
						AllColumns.setOrientation(LinearLayout.HORIZONTAL);
						AllColumns.setTag(R.id.layout_id, "AllColumns");
						
						// <prepare columnSet>
						for (int column = 0; column < columnSetCollection.size(); column++) {
							ArrayList<MatrixCell> columnSet = columnSetCollection.get(column);
							LinearLayout columnLayout = new LinearLayout(activity);
							LayoutParams columnLayoutParams = new LayoutParams(
									cell_column_width,LayoutParams.MATCH_PARENT);
							columnLayout.setLayoutParams(columnLayoutParams);
							columnLayout.setOrientation(LinearLayout.VERTICAL);
							columnLayout.setGravity(Gravity.CENTER);
							columnLayout.setTag(R.id.layout_id, "columnLayout");

							// adding columnTitle
							String columnTitle = h_values_list.get(column).get("label");
							TextView tvColumnTitle = new TextView(activity);
							tvColumnTitle.setLayoutParams(new LayoutParams(cell_column_width, cell_column_height));
							tvColumnTitle.setText(columnTitle);
							//tvColumnTitle.setBackgroundColor(Color.parseColor("#aabbcc"));
							tvColumnTitle.setGravity(Gravity.CENTER);
							tvColumnTitle.setTag(R.id.layout_id, "columnTitle");
							// following code dosen't work
//							if(tvColumnTitle.getLayoutParams().height>maxHeight){
//								maxHeight = tvColumnTitle.getHeight();
//							}

							columnLayout.addView(tvColumnTitle);

							for (int cs = 0; cs < columnSet.size(); cs++) {
								MatrixCell cell = columnSet.get(cs);
								
								LinearLayout singleRadioLayout = new LinearLayout(activity);
								singleRadioLayout.setLayoutParams(new LayoutParams(cell_column_width, cell_column_height));
								singleRadioLayout.setGravity(Gravity.CENTER);
								singleRadioLayout.setTag(R.id.layout_id, "cell");
								RadioButton rb = new RadioButton(activity);
								LayoutParams RadioParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
								rb.setLayoutParams(RadioParams);
								rb.setTag(R.id.matrix_cell, cell);
								rb.setTag(R.id.is_radiobutton_selected, false);
								//rb.setGravity(Gravity.CENTER); // <-- center horizontal didn't work here
								singleRadioLayout.addView(rb);
								radioButtonList.add(rb);
								//singleRadioLayout.setBackgroundColor(Color.parseColor("#5abde4"));
								columnLayout.addView(singleRadioLayout);
							}
							//here
							AllColumns.addView(columnLayout);
							//theMatrixLayout.addView(columnLayout);
						}
						// </prepare columnSet>

						// </matrix stuffs>

						int aaaaa = maxHeight;

						MYHorizontalScrollView horizontalScrollView = new MYHorizontalScrollView(activity);
						horizontalScrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
						horizontalScrollView.addView(AllColumns);
						horizontalScrollView.setTag(R.id.layout_id, "horizontalScrollView");
						horizontalScrollView.requestDisallowInterceptTouchEvent(false);
						theMatrixLayout.addView(horizontalScrollView);
						matrixOptionLayout.addView(theMatrixLayout);

						//<setting onClickListener to individual RadioButton>
						for(int v=0; v<v_values_list.size(); v++){ // 4
							final ArrayList<RadioButton> singleRowAtC = new ArrayList<RadioButton>();
							// <collecting cell by v index>
							for(RadioButton rb: radioButtonList){
								MatrixCell cell = (MatrixCell) rb.getTag(R.id.matrix_cell);
								if(cell.getV_index()==v){
									singleRowAtC.add(rb);
								}
							}
							// </collecting cell by v index>

							// <setting Listener>
							for(int s=0; s<singleRowAtC.size(); s++){ //4
								final RadioButton rb = singleRowAtC.get(s);
								rb.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {			
										rb.setTag(R.id.is_radiobutton_selected, true);
										MatrixCell cell = (MatrixCell) rb.getTag(R.id.matrix_cell);	
										String uniqueValue = cell.getValue();
										for(int ss=0; ss<singleRowAtC.size(); ss++){
											RadioButton loopButton = singleRowAtC.get(ss);
											MatrixCell loopCell = (MatrixCell) loopButton.getTag(R.id.matrix_cell);
											if(!uniqueValue.equals(loopCell.getValue())){
												RadioButton otherButton = singleRowAtC.get(ss);
												otherButton.setChecked(false);
												otherButton.setTag(R.id.is_radiobutton_selected, false);
											}
										}
									}
								});
							}
							// </setting Listener>
						}

						//</setting onClickListener to individual RadioButton>
						
						// error msg
						TextView errorMsg = new TextView(activity);
						errorMsg.setLayoutParams(errorMsgLayoutParams);
						setTypeFace(errorMsg);
						errorMsg.setTextSize(12);
						errorMsg.setTag("errorMsg");
						matrixOptionLayout.addView(errorMsg);

						scroll.addView(matrixOptionLayout);
						parentLayout.addView(scroll);
						layoutList.add(parentLayout);
					}
					//</matrix_>
				}
			}
		}
		// lL.addView(signatureView);
		return layoutList;
	}

	private String isFieldRequired(Object field_required) {
		if (field_required.toString().equals("true"))
			return "true";
		return "false";
	}

	public ArrayList<HashMap<String, String>> getDataFromDocumentJSON(
			JSONObject document_json) {
		ArrayList<HashMap<String, String>> dataJson = new ArrayList<HashMap<String, String>>();
		try {
			JSONArray jsonArray = document_json.getJSONArray("document_json");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject child = (JSONObject) jsonArray.get(i);
				String fieldName = child.getString("field_name");
				String fieldValue = child.getString("field_value");
				HashMap<String, String> nameAndValue = new HashMap<String, String>();
				nameAndValue.put(fieldName, fieldValue);
				dataJson.add(nameAndValue);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataJson;
	}

	public String getFileDataAsString(String fileName) {
		String jsonStr = "";
		try {
			InputStream is = activity.getAssets().open(fileName);
			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			is.close();
			jsonStr = new String(buffer, "UTF-8");
			Log.i("JSON from File", jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		Log.i("jsonStr is \n", jsonStr);
		return jsonStr;
	}

	public Form getFormFromJSON(String dataJson) {
		Form form = new Form();
		// form.setCreated_date("00-00-0000");
		// form.setData_json(dataJson);
		// form.setSynced(1);
		return form;
	}

	public ArrayList<HashMap<String, Object>> getFormFields(String form_json) {
		ArrayList<HashMap<String, Object>> fieldList = new ArrayList<HashMap<String, Object>>();
		try {
			JSONObject json = new JSONObject(
					convertStandardJSONString(form_json));
			JSONArray form_fields = json.getJSONArray("form_fields");
			for (int i = 0; i < form_fields.length(); i++) {
				JSONObject jChild = form_fields.getJSONObject(i);
				String field_type = jChild.getString("field_type");
				HashMap<String, Object> fields = new HashMap<String, Object>();
				if (field_type.equals("location")) {
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_help", jChild.getString("field_help"));
					fields.put("field_ref", jChild.getString("field_ref"));
					fields.put("next_cond", jChild.getJSONArray("next_cond"));
					String field_desc = jChild.getString("field_desc");
					if (field_desc.length() > 0)
						fields.put("field_desc", field_desc);
					else
						fields.put("field_desc", "-");
					fields.put("field_required",
							jChild.getString("field_required"));
					fields.put("field_default_value",
							jChild.getString("field_default_value"));
				} else if (field_type.equals("option")) {
					// with dataset (s and checklist)
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_help", jChild.getString("field_help"));
					fields.put("field_ref", jChild.getString("field_ref"));
					fields.put("next_cond", jChild.getJSONArray("next_cond"));
					if (jChild.has("next_cond_type")) {
						fields.put("next_cond_type", jChild.getString("next_cond_type"));
					}
					fields.put("field_audio_required", jChild.getBoolean("field_audio_required"));
					String field_desc = jChild.getString("field_desc");
					if (field_desc.length() > 0)
						fields.put("field_desc", field_desc);
					else
						fields.put("field_desc", "-");
					fields.put("field_required", jChild.getBoolean("field_required"));
					fields.put("field_default_value", jChild.get("field_default_value"));
					HashMap<String, String> field_data_set = new HashMap<String, String>();
					JSONObject field_dataset = jChild.getJSONObject("field_dataset");
					JSONArray dataset_values = field_dataset.getJSONArray("dataset_values");
					String dataset_name = field_dataset.getString("dataset_name");
					fields.put("dataset_values", dataset_values);
					fields.put("dataset_name", dataset_name);
				} else if (field_type.equals("checklist")) {
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_help", jChild.getString("field_help"));
					fields.put("field_ref", jChild.getString("field_ref"));
					fields.put("next_cond", jChild.getJSONArray("next_cond"));
					String field_desc = jChild.getString("field_desc");
					if (field_desc.length() > 0)
						fields.put("field_desc", field_desc);
					else
						fields.put("field_desc", "-");
					fields.put("field_required",
							jChild.getBoolean("field_required"));
					// fields.put("field_default_value",
					// jChild.getInt("field_default_value"));
					fields.put("field_default_value", 1);
					HashMap<String, String> field_data_set = new HashMap<String, String>();
					JSONObject field_dataset = jChild
							.getJSONObject("field_dataset");
					JSONArray dataset_values = field_dataset
							.getJSONArray("dataset_values");
					String dataset_name = field_dataset
							.getString("dataset_name");
					fields.put("dataset_values", dataset_values);
					fields.put("dataset_name", dataset_name);
				} else if (field_type.equals("datetime")) {
					String today = "";
					if (jChild.getString("field_default_value").equals(
							"<<today>>")) {
						Date date = new Date();
						DateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");
						today = dateFormat.format(date);
						fields.put("field_default_value", today);
					} else
						fields.put("field_default_value",
								jChild.getString("field_default_value"));
					String field_desc = jChild.getString("field_desc");
					if (field_desc.length() > 0)
						fields.put("field_desc", field_desc);
					else
						fields.put("field_desc", "-");
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_required",
							jChild.getString("field_required"));
					fields.put("field_help", jChild.getString("field_help"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_ref", jChild.getString("field_ref"));
					fields.put("next_cond", jChild.getJSONArray("next_cond"));
				} else if (field_type.equals("drawing")) {
					if (jChild.getString("field_default_value").length() > 0)
						fields.put("field_default_value",
								jChild.getString("field_default_value"));
					String field_desc = jChild.getString("field_desc");
					if (field_desc.length() > 0)
						fields.put("field_desc", field_desc);
					else
						fields.put("field_desc", "-");
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_required",
							jChild.getString("field_required"));
					fields.put("field_help", jChild.getString("field_help"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_ref", jChild.getString("field_ref"));
					fields.put("next_cond", jChild.getJSONArray("next_cond"));
				} else if (field_type.equals("photo")) {
					if (jChild.getString("field_default_value").length() > 0)
						fields.put("field_default_value",
								jChild.getString("field_default_value"));
					String field_desc = jChild.getString("field_desc");
					if (field_desc.length() > 0)
						fields.put("field_desc", field_desc);
					else
						fields.put("field_desc", "-");
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_required",
							jChild.getString("field_required"));
					fields.put("field_help", jChild.getString("field_help"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_ref", jChild.getString("field_ref"));
					fields.put("next_cond", jChild.getJSONArray("next_cond"));
				} else if (field_type.equals("number")) {
					fields.put("field_name", jChild.getString("field_name"));
					String field_desc = jChild.getString("field_desc");
					if (field_desc.length() > 0)
						fields.put("field_desc", field_desc);
					else
						fields.put("field_desc", "-");
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_min_value",
							jChild.getString("field_min_value"));
					fields.put("field_required",
							jChild.getString("field_required"));
					fields.put("field_help", jChild.getString("field_help"));
					fields.put("field_default_value",
							jChild.getString("field_default_value"));
					fields.put("field_err_msg",
							jChild.getString("field_err_msg"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_max_value",
							jChild.getString("field_max_value"));
					fields.put("field_ref", jChild.getString("field_ref"));
					fields.put("next_cond", jChild.getJSONArray("next_cond"));
				}
				// else if (jChild.isNull("field_dataset")) {
				else if (field_type.equals("text")) {
					fields.put("field_name", jChild.getString("field_name"));
					String field_desc = jChild.getString("field_desc");
					if (field_desc.length() > 0)
						fields.put("field_desc", field_desc);
					else
						fields.put("field_desc", "-");
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_audio_required",
							jChild.getBoolean("field_audio_required"));
					fields.put("field_required",
							jChild.getString("field_required"));
					if (jChild.getString("field_help").length() > 0)
						fields.put("field_help", jChild.getString("field_help"));
					if (jChild.getString("field_default_value").length() > 0)
						fields.put("field_default_value",
								jChild.getString("field_default_value"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_ref", jChild.getString("field_ref"));
					fields.put("next_cond", jChild.getJSONArray("next_cond"));
					if (jChild.has("field_multiline")
							&& jChild.getString("field_multiline").length() > 0)
						fields.put("field_multiline",
								jChild.getString("field_multiline"));
				} else if (field_type.equals("note")) {
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_desc", jChild.getString("field_desc"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_help", jChild.getString("field_help"));
					fields.put("field_ref", jChild.getString("field_ref"));
					fields.put("field_default_value",
							jChild.getString("field_default_value"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("next_cond", jChild.getJSONArray("next_cond"));
				}
				else if (field_type.equals("matrix_option")) {
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_desc", jChild.getString("field_desc"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("matrix_values", jChild.getJSONObject("field_matrix").getJSONArray("matrix_values"));
					fields.put("field_dataset_v", jChild.getJSONObject("field_dataset_v"));
					fields.put("field_required", jChild.getString("field_required"));
					fields.put("field_help", jChild.getString("field_help"));
					fields.put("field_ref", jChild.getString("field_ref"));
					fields.put("field_dataset_h", jChild.getJSONObject("field_dataset_h"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_id", jChild.getString("field_id"));
				} else if (field_type.equals("matrix_checklist")) {
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_desc", jChild.getString("field_desc"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("matrix_values", jChild.getJSONObject("field_matrix").getJSONArray("matrix_values"));
					fields.put("field_dataset_v", jChild.getJSONObject("field_dataset_v"));
					fields.put("field_required", jChild.getString("field_required"));
					fields.put("field_help", jChild.getString("field_help"));
					fields.put("field_ref", jChild.getString("field_ref"));
					fields.put("field_dataset_h", jChild.getJSONObject("field_dataset_h"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_id", jChild.getString("field_id"));
				}

				fieldList.add(fields);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			ToastManager xaveyToast = new ToastManager(activity);
			xaveyToast.xaveyToast(null, e.getMessage());
		}
		return fieldList;
	}

	public ArrayList<HashMap<String, String>> getDocumentFields(
			String document_json, String key_name) {
		ArrayList<HashMap<String, String>> fieldList = new ArrayList<HashMap<String, String>>();
		try {
			JSONObject json = new JSONObject(
					convertStandardJSONString(document_json));

			JSONArray document_fields = json.getJSONArray(key_name);
			for (int i = 0; i < document_fields.length(); i++) {
				JSONObject jChild = document_fields.getJSONObject(i);
				HashMap<String, String> fields = new HashMap<String, String>();
				fields.put("field_id", jChild.getString("field_id"));
				fields.put("field_name", jChild.getString("field_name"));
				fields.put("field_value", jChild.getString("field_value"));
				fields.put("field_label", jChild.getString("field_label"));
				if (jChild.has("field_value_audio")) {
					fields.put("field_value_audio",
							jChild.getString("field_value_audio"));
				}
				fieldList.add(fields);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return fieldList;
	}

	public Object getFormValue(String key, String data_json)
			throws JSONException {

		JSONObject json = new JSONObject(convertStandardJSONString(data_json));
		return json.get(key);
	}

	public static String convertStandardJSONString(String data_json) {
		data_json = data_json.replace("\\", "");
		data_json = data_json.replace("\"{", "{");
		data_json = data_json.replace("}\",", "},");
		data_json = data_json.replace("}\"", "}");
		data_json = data_json.replace("\"[", "[");
		data_json = data_json.replace("]\"", "]");
		return data_json;
	}

	public ArrayList<HashMap<String, String>> getUserInfo(String data_json) {

		return null;
	}

	public Form prepareForm(String form_id, String create_date,
			String data_json, boolean isSynced) throws JSONException {
		JSONObject obj = new JSONObject(data_json);

		return null;
	}

	public static int randInt(int min, int max) {
		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	private void selectImage(final String field_name, final String field_type,
			final String field_help, final String view_id) {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };

		ApplicationValues.FIELD_NAME_TMP = field_name;
		ApplicationValues.FIELD_TYPE_TMP = field_type;
		ApplicationValues.FIELD_HELP_TMP = field_help;
		ApplicationValues.VIEW_ID_TMP = view_id;

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File myDir = new File(ApplicationValues.XAVEY_DIRECTORY,
							"/Photos");
					myDir.mkdirs();
					String photoName = "_photo" + System.currentTimeMillis()
							+ ".jpeg";
					File f = new File(myDir, photoName);

					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					// putting extra like following line is f**king useless
					// intent.putExtra("photo_path", myDir+"/"+photoName);
					activity.startActivityForResult(intent,
							ApplicationValues.REQUEST_CAMERA);
					ApplicationValues.IMAGE_PATH_TMP = myDir + "/" + photoName;
					ApplicationValues.PHOTO_NAME_TMP = photoName;
				} else if (items[item].equals("Choose from Library")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					activity.startActivityForResult(
							Intent.createChooser(intent, "Select File"),
							ApplicationValues.SELECT_FILE);
					// I think it doesn't need to make
					// ApplicationValues.IMAGE_PATH_TMP = myDir+"/"+photoName;
					// because image path can be retrieved from data.getData()
					// in onActivityResult()
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	// following two methods' purpose is just for exporting CSV
	public ArrayList<String> getHeaderList(String form_json) {
		ArrayList<String> fieldNameList = new ArrayList<String>();
		try {
			JSONObject json = new JSONObject(
					convertStandardJSONString(form_json));
			JSONArray form_fields = json.getJSONArray("form_fields");
			for (int i = 0; i < form_fields.length(); i++) {
				JSONObject jChild = form_fields.getJSONObject(i);
				String field_label = jChild.getString("field_label");
				fieldNameList.add(field_label);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fieldNameList;
	}

	public ArrayList<HashMap<String, String>> getDataList(
			ArrayList<Document> documentList) {

		ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
		try {
			for (int z = 0; z < documentList.size(); z++) {
				String document_json = documentList.get(z).getDocument_json();
				JSONObject json = new JSONObject(
						convertStandardJSONString(document_json));
				JSONArray jsonArray = json.getJSONArray("document_json");
				HashMap<String, String> nameAndValue = new HashMap<String, String>();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject child = (JSONObject) jsonArray.get(i);
					String fieldLabel = child.getString("field_label");
					String fieldValue = child.getString("field_value");
					nameAndValue.put(fieldLabel, fieldValue);
				}
				dataList.add(nameAndValue);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataList;
	}

	public JSONArray getJSONArrayToSubmit(Document document, Form form)
			throws JSONException {

		SessionManager session = new SessionManager(activity);
		HashMap<String, String> userInfo = session.getUserDetails();
		String worker_id = userInfo.get(SessionManager.USER_ID);
		User loginUser = dbHelper.getUserByUserID(worker_id);
		// String timestamp = Calendar.getInstance().get(Calendar.MILLISECOND)
		// + "";

		long timestamp = uniqueCurrentTimeMS();

		// ဒီအောက်ကအတိုင်းလုပ်လို့ရရင် ကောင်းတယ်.. document.getID() ကအလု်ဖြစ်ရင်
		// လုပ်လို့ရပြီ....
		// So the form parameter won't be needed anymore
		// form = dbHelper.getFormByFormID(document.getId());

		JSONObject document_json = new JSONObject();
		// (1) worker child node
		JSONObject workerChildNode = new JSONObject();
		workerChildNode.put("id", Integer.parseInt(loginUser.getUser_id()));
		workerChildNode.put("name", loginUser.getUser_name());
		// -------------------------------------------------------------
		// (2) form child node
		JSONObject formChildNode = new JSONObject();
		formChildNode.put("id", Integer.parseInt(form.getForm_id()));
		formChildNode.put("title", form.getForm_title());

		// (3) org child node
		JSONObject orgChildNode = new JSONObject();
		orgChildNode.put("id", Integer.parseInt(form.getOrg_auto_id()));
		orgChildNode.put("org_name", form.getOrg_name());
		// (4) data child node
		String document_JSON = document.getDocument_json();
		ArrayList<HashMap<String, String>> fieldList = getDocumentFields(
				document_JSON, "document_json");
		/*
		 * ArrayList<HashMap<String, String>> fieldList = jsonReader
		 * .getDocumentFields(document_JSON, "data");
		 */
		JSONArray dataArray = new JSONArray();
		for (int i = 0; i < fieldList.size(); i++) {
			HashMap<String, String> map = fieldList.get(i);
			JSONObject fieldNode = new JSONObject();
			fieldNode.put("field_id", map.get("field_id"));
			fieldNode.put("field_label", map.get("field_label"));
			fieldNode.put("field_name", map.get("field_name"));
			fieldNode.put("field_value", map.get("field_value"));
			if (map.containsKey("field_value_audio")) {
				fieldNode
						.put("field_value_audio", map.get("field_value_audio"));
			}
			dataArray.put(fieldNode);
		}

		// location child node
		JSONObject locationChildNode = new JSONObject();
		locationChildNode.put("lat", gps.getLatitude());
		locationChildNode.put("lng", gps.getLongitude());

		document_json.put("data", dataArray);
		document_json.put("timestamp", timestamp);
		document_json.put("location", locationChildNode);
		document_json.put("org", orgChildNode);
		document_json.put("form", formChildNode);
		document_json.put("worker", workerChildNode);
		document_json.put("document_id", document.getDocument_id());

		JSONArray mainArray = new JSONArray();
		mainArray.put(document_json);
		return mainArray;
	}

	private void setTypeFace(View v) {
		String status = zawGyiFontStatus;
		if (v.getClass().getName().toString().equals("android.widget.TextView")
				&& status.equals("on")) {
			TextView tv = (TextView) v;
			if (typeface.getTypeFace() != null)
				tv.setTypeface(typeface.getTypeFace());
		} else if (v.getClass().getName().toString()
				.equals("android.widget.EditText")
				&& status.equals("on")) {
			EditText edt = (EditText) v;
			if (typeface.getTypeFace() != null)
				edt.setTypeface(typeface.getTypeFace());
		} else if (v.getClass().getName().toString()
				.equals("android.widget.RadioButton")
				&& status.equals("on")) {
			RadioButton rdButton = (RadioButton) v;
			if (typeface.getTypeFace() != null)
				rdButton.setTypeface(typeface.getTypeFace());
		} else if (v.getClass().getName().toString()
				.equals("android.widget.CheckBox")
				&& status.equals("on")) {
			CheckBox chkBox = (CheckBox) v;
			if (typeface.getTypeFace() != null)
				chkBox.setTypeface(typeface.getTypeFace());
		} else if (v.getClass().getName().toString()
				.equals("android.widget.Button")
				&& status.equals("on")) {
			Button button = (Button) v;
			if (typeface.getTypeFace() != null)
				button.setTypeface(typeface.getTypeFace());
		}
	}

	public String readValueFromLayout(LinearLayout parentLayout) {
		// -1 don't care the last room cuz the last room is submitLayout
		LinearLayout linearLayout = null;
		for (int p = 0; p < parentLayout.getChildCount(); p++) {
			View child = parentLayout.getChildAt(p);
			if (child.getTag(R.id.layout_id) != null
					&& child.getClass().getName()
							.equals("android.widget.LinearLayout")) {
				linearLayout = (LinearLayout) parentLayout.getChildAt(p);
			}
		}

		if (linearLayout.getTag(R.id.layout_id).toString().equals("textLayout")) {
			String value = "";
			EditText edt1 = null;
			String field_label = linearLayout.getTag(R.id.field_label_id)
					.toString();

			for (int j = 0; j < linearLayout.getChildCount(); j++) {
				Class<?> subClass = (Class<?>) linearLayout.getChildAt(j)
						.getClass();
				if (subClass.getName().equals("android.widget.EditText")) {
					edt1 = (EditText) linearLayout.getChildAt(j);
					value = edt1.getText().toString();
				}
			}
			return value;
		} else if (linearLayout.getTag(R.id.layout_id).toString()
				.equals("numberLayout")) {
			String value = "";
			EditText edt1 = null;
			String field_label;

			for (int j = 0; j < linearLayout.getChildCount(); j++) {
				Class<?> subClass = (Class<?>) linearLayout.getChildAt(j)
						.getClass();
				if (subClass.getName().equals("android.widget.EditText")) {
					edt1 = (EditText) linearLayout.getChildAt(j);
					value = edt1.getText().toString();
				}
			}
			return value;
		} else if (linearLayout.getTag(R.id.layout_id).toString()
				.equals("radioLayout")) {
			for (int y = 0; y < linearLayout.getChildCount(); y++) {

				Class<?> subClass = (Class<?>) linearLayout.getChildAt(y)
						.getClass();

				if (subClass.getName().equals("android.widget.RadioGroup")) {
					// radio
					RadioGroup radioGroup = (RadioGroup) linearLayout
							.getChildAt(y);
					int selectedID = radioGroup.getCheckedRadioButtonId();
					RadioButton selectedButton = getSelectedRadioButtonMyRadioGroup(radioGroup);
					String value = selectedButton.getTag(R.id.radio_value)
							.toString();
					return value;
				}
			}
		} else if (linearLayout.getTag(R.id.layout_id).toString()
				.equals("checkBoxLayout")) {
			// JSONArray checkedValues = new JSONArray();
			String checkedValues = "";
			String key = linearLayout.getTag(R.id.field_name_id).toString();
			String field_label = linearLayout.getTag(R.id.field_label_id)
					.toString();

			for (int z = 0; z < linearLayout.getChildCount(); z++) {
				Class<?> subClass = (Class<?>) linearLayout.getChildAt(z)
						.getClass();
				if (subClass.getName().equals("android.widget.TextView")) {
					TextView textView = (TextView) linearLayout.getChildAt(z);

					// following if else is just to categorize the
					// textView

				} else if (subClass.getName().equals("android.widget.CheckBox")) {
					CheckBox checkBox = (CheckBox) linearLayout.getChildAt(z);
					if (checkBox.isChecked()) {
						String value = checkBox.getTag().toString();
						// checkedValues.put(value);
						checkedValues += "|" + value;
					}
				}
			}
			if (checkedValues.length() > 0)
				checkedValues = checkedValues.substring(1);
			else
				checkedValues = "-";
			return checkedValues;
		} else if (linearLayout.getTag(R.id.layout_id).toString()
				.equals("locationLayout")) {
			double latitude = 0.0;
			double longitude = 0.0;

			String field_label = linearLayout.getTag(R.id.field_label_id)
					.toString();

			for (int j = 0; j < linearLayout.getChildCount(); j++) {
				Class<?> subClass = (Class<?>) linearLayout.getChildAt(j)
						.getClass();
				if (subClass.getName().equals("android.widget.EditText")) {
					EditText location = (EditText) linearLayout.getChildAt(j);
					if (location.getHint().equals("Latitude")) {
						if (location.getText().toString().length() > 0)
							latitude = Double.parseDouble(location.getText()
									.toString());
					} else {
						if (location.getText().toString().length() > 0)
							longitude = Double.parseDouble(location.getText()
									.toString());
					}
				}
			}
			return latitude + "|" + longitude;
		} else if (linearLayout.getTag(R.id.layout_id).toString()
				.equals("datetimeLayout")) {

			String time = "";
			String date = "";
			for (int j = 0; j < linearLayout.getChildCount(); j++) {
				Class<?> subClass = (Class<?>) linearLayout.getChildAt(j)
						.getClass();
				if (subClass.getName().equals("android.widget.TimePicker")) {
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
			return date + "  " + time;
		}
		return "";
	}

	private RadioButton getSelectedRadioButtonMyRadioGroup(RadioGroup radioGroup) {
		RadioButton selectedButton = null;
		for (int i = 0; i < radioGroup.getChildCount(); i++) {
			LinearLayout radioButtonLine = (LinearLayout) radioGroup
					.getChildAt(i);
			for (int z = 0; z < radioButtonLine.getChildCount(); z++) {
				View view = radioButtonLine.getChildAt(z);
				String className = view.getClass().getName().toString();
				if (className.equals("android.widget.RadioButton")) {
					RadioButton radioButton = (RadioButton) view;
					if (radioButton.isChecked()) {
						return radioButton;
					}
				}
			}
		}
		return selectedButton;
	}

	private RadioButton getRadioButtonByIndexMyRadioGroup(
			RadioGroup radioGroup, int index) {

		LinearLayout radioButtonLine = (LinearLayout) radioGroup
				.getChildAt(index);
		for (int z = 0; z < radioButtonLine.getChildCount(); z++) {
			View view = radioButtonLine.getChildAt(z);
			String className = view.getClass().getName().toString();
			if (className.equals("android.widget.RadioButton")) {
				RadioButton radioButton = (RadioButton) view;
				return radioButton;
			}
		}

		return null;
	}

	private static final AtomicLong UNIQUE_TIMESTAMP = new AtomicLong();

	private long uniqueCurrentTimeMS() {
		long now = System.currentTimeMillis();

		while (true) {
			long lastTime = UNIQUE_TIMESTAMP.get();
			if (lastTime >= now)
				now = lastTime + 1;
			if (UNIQUE_TIMESTAMP.compareAndSet(lastTime, now))
				return now;
		}
	}

	public String getCurrentDocumentID() {
		return currentDocumentID;
	}

	public void setCurrentDocumentID(String currentDocumentID) {
		this.currentDocumentID = currentDocumentID;
	}

	private View getLine(String colorCode) {
		int color = Color.parseColor(colorCode);
		android.widget.RelativeLayout.LayoutParams lineParams = new android.widget.RelativeLayout.LayoutParams(
				android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, 1);
		lineParams.setMargins(10, 0, 10, 4);
		lineParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		View line = new View(activity);
		line.setLayoutParams(lineParams);
		line.setBackgroundColor(color);
		return line;
	}

}

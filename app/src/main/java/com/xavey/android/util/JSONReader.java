package com.xavey.android.util;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.xavey.android.ApplicationValues;
import com.xavey.android.OneQuestionOneView;
import com.xavey.android.R;
import com.xavey.android.adapter.ImageAdapter;
import com.xavey.android.adapter.NumberSetAdapter;
import com.xavey.android.adapter.RatingSetAdapter;
import com.xavey.android.adapter.TextSetAdapter;
import com.xavey.android.db.XaveyDBHelper;
import com.xavey.android.layout.CheckboxLayout;
import com.xavey.android.layout.MatrixCheckListLayout;
import com.xavey.android.layout.MatrixNumberLayout;
import com.xavey.android.layout.MatrixOptionLayout;
import com.xavey.android.layout.MatrixTextLayout;
import com.xavey.android.layout.RadioGroupLayout;
import com.xavey.android.model.Audio;
import com.xavey.android.model.Document;
import com.xavey.android.model.Form;
import com.xavey.android.model.User;

// (1) getFile()
// (2) and getJSON(getFile())
// (3) and that return form
public class JSONReader {

	Activity activity;
	// putting DisplayMetric here didn't work
	XaveyDBHelper dbHelper;
//	private GPSTracker gps;
	TypeFaceManager tfManager;
	DisplayManager displayManager;
	ToastManager xaveyToast;
	AudioRecordingManager recordingManager;

	private String currentDocumentID;

	public JSONReader(Activity activity) {
		this.activity = activity;
		dbHelper = new XaveyDBHelper(activity);
        tfManager = new TypeFaceManager(activity);
		//gps = new GPSTracker(activity);
		displayManager = new DisplayManager(this.activity);
		xaveyToast = new ToastManager(this.activity);
		recordingManager = new AudioRecordingManager(activity);
	}

	// Layout setting here
	// get incoming JSONString and return a linear layout

	LayoutParams errorMsgLayoutParams = null;

	public ArrayList<LinearLayout> readForm2(Form form) throws Exception {

		ArrayList<LinearLayout> layoutList = new ArrayList<LinearLayout>();
		ArrayList<HashMap<String, Object>> formFields = getFormFields(form
				.getForm_json());

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

		LayoutParams descriptionLayoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// descriptionLayoutParams.height = 30;
		descriptionLayoutParams.setMargins(10, 5, 10, 5);

		LayoutParams innerLayoutParams = new LayoutParams(LayoutWidth,
				LayoutParams.WRAP_CONTENT);
		LayoutParams editTextLayoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, editTextLayoutHeight);
		editTextLayoutParams.setMargins(15, 10, 15, 10);

		final LayoutParams extraValueLayoutParamsAppear = new LayoutParams(
				LayoutParams.MATCH_PARENT, editTextLayoutHeight);
		extraValueLayoutParamsAppear.setMargins(25, 0, 25, 5);
		final LayoutParams extraValueLayoutParamsDisappear = new LayoutParams(
				0, 0);
		extraValueLayoutParamsDisappear.setMargins(25, 0, 25, 5);
		editTextLayoutParams.setMargins(15, 10, 15, 10);

		errorMsgLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
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
						textLayout
								.setTag(R.id.next_ref, fields.get("next_ref"));
						textLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));
						if (fields.containsKey("next_ref_type"))
							textLayout.setTag(R.id.next_ref_type,
									fields.get("next_ref_type"));

						if (fields.containsKey("render_ref"))
							textLayout.setTag(R.id.render_ref,
									fields.get("render_ref"));
						if (fields.containsKey("render_ref_type"))
							textLayout.setTag(R.id.render_ref_type,
									fields.get("render_ref_type"));

						// tma: tagging ref_setter value
						if (fields.containsKey("ref_setter")) {
							textLayout.setTag(R.id.ref_setter,
									fields.get("ref_setter").toString());
						}

						String textLabel = fields.get("field_label").toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setTag("label");
						tvLabel.setText(textLabel);
						tvLabel.setTextSize(labelTextSize);
						tvLabel.setLayoutParams(labelLayoutParams);
						tvLabel.setPadding(0, 5, 0, 5);
                        tfManager.setTypeFace(tvLabel);
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
                        tfManager.setTypeFace(tvdescription);
						textLayout.addView(tvdescription);

						EditText ed1 = new EditText(activity);
						ed1.setTextSize(16);
						ed1.setTypeface(tfManager.getTypeFace());
						String fieldName = fields.get("field_name").toString();
						String fieldID = fields.get("field_id").toString();
						String fieldHelp = "-";
						if (fields.containsKey("field_help"))
							fieldHelp = fields.get("field_help").toString();

						ed1.setGravity(Gravity.LEFT);
						ed1.setSingleLine(true);
						ed1.setHint(fieldHelp);
						ed1.setBackgroundResource(R.drawable.edittext_style);
						ed1.setLayoutParams(editTextLayoutParams);
						tfManager.setTypeFace(ed1);
						textLayout.addView(ed1);

						scroll.addView(textLayout);
						parentLayout.addView(scroll);
						addErrorMsg(parentLayout);

						// audio stuff
                        boolean isAudioRequired = Boolean.parseBoolean(fields
                                .get("field_audio_required").toString());
                        if (isAudioRequired) {
                            Audio audioinfo = new Audio();
                            audioinfo.setAudio_name(fieldName);
                            recordingManager = new AudioRecordingManager(
                                    activity);
                            recordingManager.setAudioInfo(audioinfo);
                            recordingManager.setFileName(fieldID + "-"
                                    + getCurrentDocumentID());
                            LinearLayout recordingLayout = recordingManager
                                    .getRecordingLayout();
                            recordingLayout.setTag(R.id.layout_id,
                                    "recordingLayout");
                            recordingLayout.setTag(R.id.recording_manager,
                                    recordingManager);
                            //check this tag @ pre-render stage
                            Boolean isRecorderDisplay = Boolean.parseBoolean(fields.get("field_audio_recorder_display").toString());
                            if(isRecorderDisplay == false){
                                recordingLayout.setVisibility(View.INVISIBLE);
                                recordingLayout.setTag(R.id.recorder_auto, !isRecorderDisplay);
                            }
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
						// numberLayout.setTag(R.id.field_default_value,
						// fields.get("field_default_value"));
						numberLayout.setTag(R.id.field_err_msg,
								fields.get("field_err_msg"));
						numberLayout.setTag(R.id.next_ref,
								fields.get("next_ref"));
						numberLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));
						if (fields.containsKey("next_ref_type"))
							numberLayout.setTag(R.id.next_ref_type,
									fields.get("next_ref_type"));
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
                        tfManager.setTypeFace(tvLabel);
						numberLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setText(description);
						tvdescription.setTextSize(18);
						tvdescription.setLayoutParams(labelLayoutParams);
                        tfManager.setTypeFace(tvdescription);
						numberLayout.addView(tvdescription);

						EditText ed1 = new EditText(activity);
						ed1.setTextSize(16);
						String fieldName = fields.get("field_name").toString();
						String fieldHelp = "-";
						if (fields.containsKey("field_help"))
							fieldHelp = fields.get("field_help").toString();
						// ed1.setText(fields.get("field_default_value")
						// .toString());
						ed1.setGravity(Gravity.LEFT);
						ed1.setSingleLine(true);
						ed1.setHint(fieldHelp);
						ed1.setBackgroundResource(R.drawable.edittext_style);
						ed1.setLayoutParams(editTextLayoutParams);
                        tfManager.setTypeFace(ed1);
						// ed1.setEms(50);
						ed1.setInputType(InputType.TYPE_CLASS_NUMBER);
						ed1.setKeyListener(DigitsKeyListener
                                .getInstance("0123456789."));
						numberLayout.addView(ed1);

						scroll.addView(numberLayout);
						parentLayout.addView(scroll);
						addErrorMsg(parentLayout);
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
						datetimeLayout.setTag(R.id.next_ref,
								fields.get("next_ref"));
						datetimeLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));
						TextView tvLabel = new TextView(activity);
						tvLabel.setText(dateTimeLabel);
						tvLabel.setTextSize(labelTextSize);
						String fieldName = fields.get("field_name").toString();
						String fieldRequired = fields.get("field_required")
								.toString();
						tvLabel.setTag(R.id.field_name_id, fieldName);
						tvLabel.setTag(R.id.field_required_id, fieldRequired);
						tvLabel.setLayoutParams(labelLayoutParams);
                        tfManager.setTypeFace(tvLabel);
						datetimeLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setText(description);
						tvdescription.setTextSize(18);
						tvdescription.setLayoutParams(labelLayoutParams);
                        tfManager.setTypeFace(tvdescription);
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

						addErrorMsg(parentLayout);

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

						ArrayList<LinearLayout> buttonLinesToBeRandomed = new ArrayList<LinearLayout>();

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
						radioLayout.setTag(R.id.next_ref,
								fields.get("next_ref"));
						radioLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));

						boolean field_random = false;

						if (fields.containsKey("field_random")) {
							field_random = Boolean.parseBoolean(fields.get(
									"field_random").toString());
							radioLayout.setTag(R.id.field_random, field_random);
						}

						if (fields.containsKey("next_ref_type")) {
							radioLayout.setTag(R.id.next_ref_type,
									fields.get("next_ref_type"));
						}
						// String field_default_value = fields.get(
						// "field_default_value").toString();
						// radioLayout.setTag(R.id.field_default_value,
						// field_default_value);
						if (fields.containsKey("render_ref"))
							radioLayout.setTag(R.id.render_ref,
									fields.get("render_ref"));
						if (fields.containsKey("render_ref_type"))
							radioLayout.setTag(R.id.render_ref_type,
									fields.get("render_ref_type"));

						// tma: tagging ref_setter value
						if (fields.containsKey("ref_setter")) {
							radioLayout.setTag(R.id.ref_setter,
									fields.get("ref_setter").toString());
						}

						if (fields.containsKey("field_random")) {
							field_random = Boolean.getBoolean(fields.get(
									"field_random").toString());
						}

						String radioLabel = fields.get("field_label")
								.toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setText(radioLabel);
						tvLabel.setTextSize(labelTextSize);
						String fieldID = fields.get("field_id").toString();
						String fieldName = fields.get("field_name").toString();
						String fieldRequired = fields.get("field_required")
								.toString();
						String fieldLabel = fields.get("field_label")
								.toString();
						tvLabel.setGravity(Gravity.CENTER_VERTICAL);
						tvLabel.setLayoutParams(labelLayoutParams);
                        tfManager.setTypeFace(tvLabel);
						radioLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setText(description);
						tvdescription.setTextSize(descriptionTextSize);
						tvdescription.setLayoutParams(descriptionLayoutParams);
                        tfManager.setTypeFace(tvdescription);
						radioLayout.addView(tvdescription);

						JSONArray dataset = (JSONArray) fields
								.get("dataset_values");

						RadioGroupLayout radioGroupLayout = new RadioGroupLayout(
								activity, dataset);
						radioGroupLayout
								.setRadioButtonTextSize(radioButtonTextSize);
						radioGroupLayout
								.setRadioButtonLineLayoutParams(radioButtonLineLayoutParams);
						radioGroupLayout
								.setExtraValueLayoutParamsAppear(extraValueLayoutParamsAppear);
						radioGroupLayout
								.setExtraValueLayoutParamsDisappear(extraValueLayoutParamsDisappear);
						radioGroupLayout.initLayout(dataset, field_random);
						dataset = null;

						radioLayout.addView(radioGroupLayout);
						radioLayout.setGravity(Gravity.CENTER);

						scroll.addView(radioLayout);
						parentLayout.addView(scroll);
						dataset = null;

						// audio stuff
						// String audiorequired =
						// fields.get("field_audio_required").toString();
						boolean isAudioRequired = Boolean.parseBoolean(fields
								.get("field_audio_required").toString());
						if (isAudioRequired) {
							Audio audioinfo = new Audio();
							audioinfo.setAudio_name(fieldName);
							recordingManager = new AudioRecordingManager(
									activity);
							recordingManager.setAudioInfo(audioinfo);
							recordingManager.setFileName(fieldID + "-"
									+ getCurrentDocumentID());
                            LinearLayout recordingLayout = recordingManager
									.getRecordingLayout();
							recordingLayout.setTag(R.id.layout_id,
									"recordingLayout");
							recordingLayout.setTag(R.id.recording_manager,
									recordingManager);
                            //check this tag @ pre-render stage
                            Boolean isRecorderDisplay = Boolean.parseBoolean(fields.get("field_audio_recorder_display").toString());
                            if(isRecorderDisplay == false){
                                recordingLayout.setVisibility(View.INVISIBLE);
                                recordingLayout.setTag(R.id.recorder_auto, !isRecorderDisplay);
                            }
//                            recordingLayout.setTag(R.id.recorder_auto,!(Boolean.parseBoolean(fields.get("field_audio_recorder_display").toString())));
							parentLayout.addView(recordingLayout);
						}
						addErrorMsg(parentLayout);
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
						tvLayoutParams
								.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
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
						checkBoxLayout.setTag(R.id.field_id,
								fields.get("field_id"));
						checkBoxLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						checkBoxLayout.setTag(R.id.field_required_id,
								fields.get("field_required"));
						checkBoxLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						checkBoxLayout.setTag(R.id.field_err_msg,
								fields.get("field_err_msg"));
						checkBoxLayout.setTag(R.id.next_ref,
								fields.get("next_ref"));
						checkBoxLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));
						String checkLabel = fields.get("field_label")
								.toString();
						boolean field_random = false;
						if (fields.containsKey("field_random")) {
							field_random = Boolean.parseBoolean(fields.get(
									"field_random").toString());
						}
						if (fields.containsKey("field_err_msg")) {
							checkBoxLayout.setTag(fields.get("field_err_msg")
									.toString());
						}

						if (fields.containsKey("render_ref"))
							checkBoxLayout.setTag(R.id.render_ref,
									fields.get("render_ref"));
						if (fields.containsKey("render_ref_type"))
							checkBoxLayout.setTag(R.id.render_ref_type,
									fields.get("render_ref_type"));
						// tma: tagging ref_setter value
						if (fields.containsKey("ref_setter")) {
							checkBoxLayout.setTag(R.id.ref_setter,
									fields.get("ref_setter").toString());
						}
						TextView tvLabel = new TextView(activity);
						tvLabel.setText(checkLabel + " ");
						tvLabel.setTextSize(labelTextSize);
						String fieldName = fields.get("field_name").toString();
						String fieldRequired = fields.get("field_required")
								.toString();
						String fieldLabel = fields.get("field_label")
								.toString();
						tvLabel.setLayoutParams(labelLayoutParams);
                        tfManager.setTypeFace(tvLabel);
						checkBoxLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setText(description);
						tvdescription.setTextSize(descriptionTextSize);
						tvdescription.setLayoutParams(descriptionLayoutParams);
                        tfManager.setTypeFace(tvdescription);
						checkBoxLayout.addView(tvdescription);

						JSONArray dataset = (JSONArray) fields
								.get("dataset_values");
						// int default_value = Integer.parseInt(fields.get(
						// "field_default_value").toString());

						LayoutParams checkBoxLineLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);

						CheckboxLayout theCheckBoxLayout = new CheckboxLayout(
								activity, dataset);
						theCheckBoxLayout
								.setCheckBoxButtonTextSize(radioButtonTextSize);
						theCheckBoxLayout
								.setCheckBoxLineLayoutParams(checkBoxLineLayoutParams);
						theCheckBoxLayout
								.setExtraValueLayoutParamsAppear(extraValueLayoutParamsAppear);
						theCheckBoxLayout
								.setExtraValueLayoutParamsDisappear(extraValueLayoutParamsDisappear);
						theCheckBoxLayout.initLayout(dataset);
						dataset = null;

						checkBoxLayout.addView(theCheckBoxLayout);
						scroll.addView(checkBoxLayout);
						parentLayout.addView(scroll);
						addErrorMsg(parentLayout);
						layoutList.add(parentLayout);
					}
                    else if (fields.get(key).equals("drawing")) {

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
						drawingLayout.setTag(R.id.next_ref,
								fields.get("next_ref"));
						drawingLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));
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
                        tfManager.setTypeFace(tvLabel);
						drawingLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setPadding(10, 0, 0, 0);
						tvdescription.setText(description);
						tvdescription.setTextSize(18);
						tvdescription.setLayoutParams(labelLayoutParams);
                        tfManager.setTypeFace(tvdescription);
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
                        tfManager.setTypeFace(button);
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

						drawingLayout.addView(button);

						scroll.addView(drawingLayout);
						parentLayout.addView(scroll);
						addErrorMsg(parentLayout);
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
						photoLayout.setTag(R.id.next_ref,
								fields.get("next_ref"));
						photoLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));
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
                        tfManager.setTypeFace(tvLabel);
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
                        tfManager.setTypeFace(button);
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

						photoLayout.addView(button);
						scroll.addView(photoLayout);

						parentLayout.addView(scroll);
						addErrorMsg(parentLayout);
						layoutList.add(parentLayout);
					} else if (fields.get(key).equals("note")) {

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
						// noteLayout.setTag(R.id.field_default_value,
						// fields.get("field_default_value"));
						noteLayout
								.setTag(R.id.next_ref, fields.get("next_ref"));
						noteLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));
						noteLayout.setTag(R.id.render_ref,
								fields.get("render_ref"));
						noteLayout.setTag(R.id.render_ref_type,
								fields.get("render_ref_type"));
						noteLayout.setTag(R.id.isViewAlreadyExisted, false);

						String textLabel = fields.get("field_label").toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setText(textLabel);
						tvLabel.setTextSize(labelTextSize);
						tvLabel.setLayoutParams(labelLayoutParams);
                        tfManager.setTypeFace(tvLabel);
						noteLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setTag(R.id.field_desc, description);
						tvdescription.setText(description);
						tvdescription.setTextSize(descriptionTextSize);
						tvdescription.setLayoutParams(descriptionLayoutParams);
						// tvdescription.setTextScaleX(1.1f);

                        tfManager.setTypeFace(tvdescription);
						noteLayout.addView(tvdescription);

						scroll.addView(noteLayout);
						parentLayout.addView(scroll);
						layoutList.add(parentLayout);
					}
					// <matrix_text>
					else if (fields.get(key).equals("matrix_text")) {
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

						LinearLayout matrixNumberLayout = new LinearLayout(
								activity);
						LayoutParams matrixSingleLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						matrixSingleLayoutParams.setMargins(5, 20, 5, 0);
						matrixNumberLayout
								.setLayoutParams(matrixSingleLayoutParams);
						matrixNumberLayout
								.setGravity(Gravity.CENTER_HORIZONTAL);
						matrixNumberLayout
								.setOrientation(LinearLayout.VERTICAL);
						matrixNumberLayout.setPadding(0, 10, 0, 10);
						matrixNumberLayout.setTag(R.id.layout_id,
								"matrixTextLayout");
						matrixNumberLayout.setTag(R.id.field_id,
								fields.get("field_id"));
						matrixNumberLayout.setTag(R.id.field_name_id,
                                fields.get("field_name"));
						matrixNumberLayout.setTag(R.id.field_required_id,
                                fields.get("field_required"));
						matrixNumberLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						matrixNumberLayout.setTag(R.id.field_err_msg, "-");
						matrixNumberLayout.setTag(R.id.next_ref,
								fields.get("next_ref"));
						matrixNumberLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));
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
                        tfManager.setTypeFace(tvLabel);
						matrixNumberLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setText(description);
						tvdescription.setTextSize(descriptionTextSize);
						tvdescription.setLayoutParams(descriptionLayoutParams);
						tvdescription.setPadding(0, 5, 0, 5);
                        tfManager.setTypeFace(tvdescription);
						matrixNumberLayout.addView(tvdescription);

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
							String max_range = "";
							if (h_values.getJSONObject(v).has("max_range"))
								max_range = h_values.getJSONObject(v)
										.getString("max_range");
							String value = h_values.getJSONObject(v).getString(
									"value");
							String label = h_values.getJSONObject(v).getString(
									"label");
							String field_skip = h_values.getJSONObject(v)
									.getString("field_skip");
							String extra = h_values.getJSONObject(v).getString(
									"extra");
							String error_message = "";
							if (h_values.getJSONObject(v).has("error_message"))
								h_values.getJSONObject(v).getString(
										"error_message");

							HashMap<String, String> map = new HashMap<String, String>();
							map.put("max_range", max_range);
							map.put("value", value);
							map.put("label", label);
							map.put("field_skip", field_skip);
							map.put("extra", extra);
							map.put("error_message", error_message);
							h_values_list.add(map);
						}
						// </dataset_h stuffs>

						// < matrix stuffs >
						JSONArray matrix_values = (JSONArray) fields
								.get("matrix_values");

                        MatrixTextLayout theMatrixLayout = new MatrixTextLayout(
								activity);
						theMatrixLayout.initLayout(h_values_list,
								v_values_list, matrix_values);

						matrixNumberLayout.addView(theMatrixLayout);

						scroll.addView(matrixNumberLayout);
						parentLayout.addView(scroll);
						addErrorMsg(parentLayout);
						layoutList.add(parentLayout);
					}
					// <matrix_number>
					else if (fields.get(key).equals("matrix_number")) {
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

						LinearLayout matrixNumberLayout = new LinearLayout(
								activity);
						LayoutParams matrixSingleLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						matrixSingleLayoutParams.setMargins(5, 20, 5, 0);
						matrixNumberLayout
								.setLayoutParams(matrixSingleLayoutParams);
						matrixNumberLayout
								.setGravity(Gravity.CENTER_HORIZONTAL);
						matrixNumberLayout
								.setOrientation(LinearLayout.VERTICAL);
						matrixNumberLayout.setPadding(0, 10, 0, 10);
						matrixNumberLayout.setTag(R.id.layout_id,
								"matrixNumberLayout");
						matrixNumberLayout.setTag(R.id.field_id,
								fields.get("field_id"));
						matrixNumberLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						matrixNumberLayout.setTag(R.id.field_required_id,
								fields.get("field_required"));
						matrixNumberLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						matrixNumberLayout.setTag(R.id.field_err_msg, "-");
						matrixNumberLayout.setTag(R.id.next_ref,
								fields.get("next_ref"));
						matrixNumberLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));
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
                        tfManager.setTypeFace(tvLabel);
						matrixNumberLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setText(description);
						tvdescription.setTextSize(descriptionTextSize);
						tvdescription.setLayoutParams(descriptionLayoutParams);
						tvdescription.setPadding(0, 5, 0, 5);
                        tfManager.setTypeFace(tvdescription);
						matrixNumberLayout.addView(tvdescription);

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
							String max_range = "";
							if (h_values.getJSONObject(v).has("max_range"))
								max_range = h_values.getJSONObject(v)
										.getString("max_range");
							String value = h_values.getJSONObject(v).getString(
									"value");
							String label = h_values.getJSONObject(v).getString(
									"label");
							String field_skip = h_values.getJSONObject(v)
									.getString("field_skip");
							String extra = h_values.getJSONObject(v).getString(
									"extra");
							String error_message = "";
							if (h_values.getJSONObject(v).has("error_message"))
								h_values.getJSONObject(v).getString(
										"error_message");

							HashMap<String, String> map = new HashMap<String, String>();
							map.put("max_range", max_range);
							map.put("value", value);
							map.put("label", label);
							map.put("field_skip", field_skip);
							map.put("extra", extra);
							map.put("error_message", error_message);
							h_values_list.add(map);
						}
						// </dataset_h stuffs>

						// < matrix stuffs >
						JSONArray matrix_values = (JSONArray) fields
								.get("matrix_values");

						MatrixNumberLayout theMatrixLayout = new MatrixNumberLayout(
								activity);
						theMatrixLayout.initLayout(h_values_list,
								v_values_list, matrix_values);

						matrixNumberLayout.addView(theMatrixLayout);

						scroll.addView(matrixNumberLayout);
						parentLayout.addView(scroll);
						addErrorMsg(parentLayout);
						layoutList.add(parentLayout);
					}
					// </matrix_numberlist>
					// <matrix_checklist>
					else if (fields.get(key).equals("matrix_checklist")) {
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

						LinearLayout matrixChecklistLayout = new LinearLayout(
								activity);
						LayoutParams matrixSingleLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						matrixSingleLayoutParams.setMargins(5, 20, 5, 0);
						matrixChecklistLayout
								.setLayoutParams(matrixSingleLayoutParams);
						matrixChecklistLayout
								.setGravity(Gravity.CENTER_HORIZONTAL);
						matrixChecklistLayout
								.setOrientation(LinearLayout.VERTICAL);
						matrixChecklistLayout.setPadding(0, 10, 0, 10);
						matrixChecklistLayout.setTag(R.id.layout_id,
								"matrixCheckListLayout");
						matrixChecklistLayout.setTag(R.id.field_id,
								fields.get("field_id"));
						matrixChecklistLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						matrixChecklistLayout.setTag(R.id.field_required_id,
								fields.get("field_required"));
						matrixChecklistLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						matrixChecklistLayout.setTag(R.id.field_err_msg, "-");
						matrixChecklistLayout.setTag(R.id.next_ref,
								fields.get("next_ref"));
						matrixChecklistLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));
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
                        tfManager.setTypeFace(tvLabel);
						matrixChecklistLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setText(description);
						tvdescription.setTextSize(descriptionTextSize);
						tvdescription.setLayoutParams(descriptionLayoutParams);
						tvdescription.setPadding(0, 5, 0, 5);
                        tfManager.setTypeFace(tvdescription);
						matrixChecklistLayout.addView(tvdescription);

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
							String max_range = "";
							if (h_values.getJSONObject(v).has("max_range"))
								max_range = h_values.getJSONObject(v)
										.getString("max_range");
							String value = h_values.getJSONObject(v).getString(
									"value");
							String label = h_values.getJSONObject(v).getString(
									"label");
							String field_skip = h_values.getJSONObject(v)
									.getString("field_skip");
							String extra = h_values.getJSONObject(v).getString(
									"extra");
							String error_message = "";
							if (h_values.getJSONObject(v).has("error_message"))
								h_values.getJSONObject(v).getString(
										"error_message");

							HashMap<String, String> map = new HashMap<String, String>();
							map.put("max_range", max_range);
							map.put("value", value);
							map.put("label", label);
							map.put("field_skip", field_skip);
							map.put("extra", extra);
							map.put("error_message", error_message);
							h_values_list.add(map);
						}
						// </dataset_h stuffs>

						// < matrix stuffs >
						JSONArray matrix_values = (JSONArray) fields
								.get("matrix_values");

						MatrixCheckListLayout theMatrixLayout = new MatrixCheckListLayout(
								activity);
						theMatrixLayout.initLayout(h_values_list,
								v_values_list, matrix_values);

						matrixChecklistLayout.addView(theMatrixLayout);

						scroll.addView(matrixChecklistLayout);
						parentLayout.addView(scroll);
						addErrorMsg(parentLayout);
						layoutList.add(parentLayout);
					}
					// </matrix_checklist>

					// <matrix_option>
					else if (fields.get(key).equals("matrix_option")) {
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

						ArrayList<RadioButton> radioButtonList = new ArrayList<RadioButton>();

						LinearLayout matrixOptionLayout = new LinearLayout(
								activity);
						LayoutParams matrixRadioLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						matrixRadioLayoutParams.setMargins(5, 20, 5, 0);
						matrixOptionLayout
								.setLayoutParams(matrixRadioLayoutParams);
						matrixOptionLayout
								.setGravity(Gravity.CENTER_HORIZONTAL);
						matrixOptionLayout
								.setOrientation(LinearLayout.VERTICAL);
						matrixOptionLayout.setPadding(0, 10, 0, 10);
						matrixOptionLayout.setTag(R.id.layout_id,
								"matrixOptionLayout");
						matrixOptionLayout.setTag(R.id.field_id,
								fields.get("field_id"));
						matrixOptionLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						matrixOptionLayout.setTag(R.id.field_required_id,
								fields.get("field_required"));
						matrixOptionLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						matrixOptionLayout.setTag(R.id.field_err_msg, "-");
						matrixOptionLayout.setTag(R.id.next_ref,
								fields.get("next_ref"));
						matrixOptionLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));
						if (fields.containsKey("render_ref"))
							matrixOptionLayout.setTag(R.id.render_ref,
									fields.get("render_ref"));
						if (fields.containsKey("render_ref_type"))
							matrixOptionLayout.setTag(R.id.render_ref_type,
									fields.get("render_ref_type"));

						boolean fieldRandomV = false;

						if (fields.containsKey("field_random_v")){
							matrixOptionLayout.setTag(R.id.field_random_v,
									fields.get("field_random_v"));
							fieldRandomV = Boolean.parseBoolean(fields.get("field_random_v").toString());
						}
						
						boolean fieldRandomH = false;
						/*if(fields.containsKey("field_random_h")){
							
						}*/
							
						String field_label = fields.get("field_label")
								.toString();

						// tma: tagging ref_setter value
						if (fields.containsKey("ref_setter")) {
							matrixOptionLayout.setTag(R.id.ref_setter, fields
									.get("ref_setter").toString());
						}

						String field_name = fields.get("field_name").toString();
						String field_required = fields.get("field_required")
								.toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setText(field_label);
						tvLabel.setTextSize(labelTextSize);
						// tvLabel.setTag(fields.get("field_name"));
						tvLabel.setLayoutParams(labelLayoutParams);
                        tfManager.setTypeFace(tvLabel);
						matrixOptionLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setText(description);
						tvdescription.setTextSize(descriptionTextSize);
						tvdescription.setLayoutParams(descriptionLayoutParams);
						tvdescription.setPadding(0, 5, 0, 5);
                        tfManager.setTypeFace(tvdescription);
						matrixOptionLayout.addView(tvdescription);

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

							String max_range = "";
							if (h_values.getJSONObject(v).has("max_range"))
								max_range = h_values.getJSONObject(v)
										.getString("max_range");
							String field_skip = h_values.getJSONObject(v)
									.getString("field_skip");
							String value = h_values.getJSONObject(v).getString(
									"value");
							String label = h_values.getJSONObject(v).getString(
									"label");
							String extra = h_values.getJSONObject(v).getString(
									"extra");
							String error_message = "";
							if (h_values.getJSONObject(v).has("error_message"))
								error_message = h_values.getJSONObject(v)
										.getString("error_message");
							// String value =
							// h_values.getJSONObject(v).getString("value");
							// String label =
							// h_values.getJSONObject(v).getString("label");
							HashMap<String, String> map = new HashMap<String, String>();
							if (max_range.length() > 0)
								map.put("max_range", max_range);
							map.put("field_skip", field_skip);
							map.put("value", value);
							map.put("label", label);
							map.put("extra", extra);
							map.put("error_message", error_message);
							h_values_list.add(map);
						}
						// </dataset_h stuffs>

						// < matrix stuffs >
						JSONArray matrix_values = (JSONArray) fields
								.get("matrix_values");

						MatrixOptionLayout theMatrixLayout = new MatrixOptionLayout(
								activity);

						theMatrixLayout.setHValueList(h_values_list);
						theMatrixLayout.setVValueList(v_values_list);
						theMatrixLayout.setCellValueList(matrix_values);
						theMatrixLayout.setWillBeRandomizedV(fieldRandomV);
						theMatrixLayout.setWillBeRandomizedH(fieldRandomH);
						// setWillBeRandomizedH
						theMatrixLayout.initLayout(); // above three params are
														// needed to init this

						matrixOptionLayout.addView(theMatrixLayout);

						scroll.addView(matrixOptionLayout);
						parentLayout.addView(scroll);
						addErrorMsg(parentLayout);
						// int height = parentLayout.getChildAt(0).getHeight() +
						// parentLayout.getChildAt(1).getHeight();
						// parentLayout.getChildAt(2).setTop(900);
						layoutList.add(parentLayout);
					}
					// </matrix_option>
					// image_checklist
					else if (fields.get(key).equals("image_checklist")) {

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

						final LinearLayout imagechecklistLayout = new LinearLayout(
								activity);
						LayoutParams imageChecklistParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						imageChecklistParams.setMargins(5, 20, 5, 0);
						imagechecklistLayout
								.setLayoutParams(imageChecklistParams);
						imagechecklistLayout
								.setGravity(Gravity.CENTER_HORIZONTAL);
						// drawingLayout
						// .setBackgroundResource(R.drawable.linear_layout_ui);
						imagechecklistLayout
								.setOrientation(LinearLayout.VERTICAL);
						imagechecklistLayout.setPadding(0, 10, 0, 0);
						imagechecklistLayout.setTag(R.id.layout_id,
								"imageChecklistLayout");
						imagechecklistLayout.setTag(R.id.field_id,
								fields.get("field_id"));
						imagechecklistLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						imagechecklistLayout.setTag(R.id.field_required_id,
								fields.get("field_required"));
						imagechecklistLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						imagechecklistLayout.setTag(R.id.field_err_msg,
								fields.get("field_err_msg"));
						imagechecklistLayout.setTag(R.id.next_ref,
								fields.get("next_ref"));
						imagechecklistLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));
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
                        tfManager.setTypeFace(tvLabel);
						imagechecklistLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setPadding(10, 0, 0, 0);
						tvdescription.setText(description);
						tvdescription.setTextSize(18);
						tvdescription.setLayoutParams(labelLayoutParams);
                        tfManager.setTypeFace(tvdescription);
						imagechecklistLayout.addView(tvdescription);

						GridView imageGridView = new GridView(activity);
						imageGridView.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));
						// imageGridView.setBackgroundColor(Color.parseColor("#FFFFFF"));
						imageGridView.setNumColumns(2);
						imageGridView.setColumnWidth(GridView.AUTO_FIT);
						imageGridView.setVerticalSpacing(5);
						imageGridView.setHorizontalSpacing(5);
						imageGridView
								.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
						ArrayList<HashMap<String, String>> imageFieldList = new ArrayList<HashMap<String, String>>();
						JSONArray dataset_values = (JSONArray) fields
								.get("dataset_values");
						for (int dv = 0; dv < dataset_values.length(); dv++) {
							String value = dataset_values.getJSONObject(dv)
									.getString("value");
							String field_skip = dataset_values
									.getJSONObject(dv).getString("field_skip");
							String image = dataset_values.getJSONObject(dv)
									.getString("image");
							String label = dataset_values.getJSONObject(dv)
									.getString("label");
							String extra = dataset_values.getJSONObject(dv)
									.getString("extra");
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("value", value);
							map.put("field_skip", field_skip);
							map.put("image", image);
							map.put("label", label);
							map.put("extra", extra);
							imageFieldList.add(map);
						}
						imageGridView
								.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
						ImageAdapter imageAdapter = new ImageAdapter(activity,
								imageFieldList);
						imageGridView.setAdapter(imageAdapter);
						ArrayList<String> valuesList = new ArrayList<String>();
						imageGridView.setTag(R.id.selected_grid_values,
								valuesList);
						imageGridView.setTag(R.id.grid_view_type,
								"multipleSelection");

						// LayoutParams errorMsgLayoutParams2 = new
						// LayoutParams(
						// LayoutParams.MATCH_PARENT, 60);
						// errorMsgLayoutParams2.setMargins(15, 20, 15, 0);
						imagechecklistLayout.addView(imageGridView);

						// scroll.addView(imagechecklistLayout);
						parentLayout.addView(imagechecklistLayout);
						addErrorMsg(parentLayout);
						layoutList.add(parentLayout);
					}
					// </image checklist>

					// <image option>
					else if (fields.get(key).equals("image_option")) {
						// ScrollView scroll = new ScrollView(activity);
						// scroll.setLayoutParams(new LayoutParams(
						// LayoutParams.MATCH_PARENT,
						// LayoutParams.MATCH_PARENT));

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

						final LinearLayout imagechecklistLayout = new LinearLayout(
								activity);
						LayoutParams imageChecklistParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						imageChecklistParams.setMargins(5, 20, 5, 0);
						imagechecklistLayout
								.setLayoutParams(imageChecklistParams);
						imagechecklistLayout
								.setGravity(Gravity.CENTER_HORIZONTAL);
						// drawingLayout
						// .setBackgroundResource(R.drawable.linear_layout_ui);
						imagechecklistLayout
								.setOrientation(LinearLayout.VERTICAL);
						imagechecklistLayout.setPadding(0, 10, 0, 0);
						imagechecklistLayout.setTag(R.id.layout_id,
								"imageOptionLayout");
						imagechecklistLayout.setTag(R.id.field_id,
								fields.get("field_id"));
						imagechecklistLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						imagechecklistLayout.setTag(R.id.field_required_id,
								fields.get("field_required"));
						imagechecklistLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						imagechecklistLayout.setTag(R.id.field_err_msg,
								fields.get("field_err_msg"));
						imagechecklistLayout.setTag(R.id.next_ref,
								fields.get("next_ref"));
						imagechecklistLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));
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
                        tfManager.setTypeFace(tvLabel);
						imagechecklistLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setPadding(10, 0, 0, 0);
						tvdescription.setText(description);
						tvdescription.setTextSize(18);
						tvdescription.setLayoutParams(labelLayoutParams);
                        tfManager.setTypeFace(tvdescription);
						imagechecklistLayout.addView(tvdescription);

						GridView imageGridView = new GridView(activity);
						imageGridView.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));
						// imageGridView.setBackgroundColor(Color.parseColor("#F7F7CD"));
						imageGridView.setNumColumns(2);
						imageGridView.setColumnWidth(GridView.AUTO_FIT);
						imageGridView.setVerticalSpacing(5);
						imageGridView.setHorizontalSpacing(5);
						imageGridView
								.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
						ArrayList<HashMap<String, String>> imageFieldList = new ArrayList<HashMap<String, String>>();
						JSONArray dataset_values = (JSONArray) fields
								.get("dataset_values");
						for (int dv = 0; dv < dataset_values.length(); dv++) {
							String value = dataset_values.getJSONObject(dv)
									.getString("value");
							String field_skip = dataset_values
									.getJSONObject(dv).getString("field_skip");
							String image = dataset_values.getJSONObject(dv)
									.getString("image");
							String label = dataset_values.getJSONObject(dv)
									.getString("label");
							String extra = dataset_values.getJSONObject(dv)
									.getString("extra");
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("value", value);
							map.put("field_skip", field_skip);
							map.put("image", image);
							map.put("label", label);
							map.put("extra", extra);
							imageFieldList.add(map);
						}
						imageGridView
								.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
						ImageAdapter imageAdapter = new ImageAdapter(activity,
								imageFieldList);
						imageGridView.setAdapter(imageAdapter);
						ArrayList<String> valuesList = new ArrayList<String>();
						imageGridView.setTag(R.id.selected_grid_values,
								valuesList);
						imageGridView.setTag(R.id.grid_view_type,
								"singleSelection");

						LayoutParams errorMsgLayoutParams2 = new LayoutParams(
								LayoutParams.MATCH_PARENT, 60);
						errorMsgLayoutParams2.setMargins(15, 20, 15, 0);

						imagechecklistLayout.addView(imageGridView);
						// error msg
						TextView errorMsg = new TextView(activity);
						errorMsg.setLayoutParams(errorMsgLayoutParams2);
                        tfManager.setTypeFace(errorMsg);
						errorMsg.setTextSize(12);
						errorMsg.setTag("errorMsg");
						imagechecklistLayout.addView(errorMsg);

						// scroll.addView(imagechecklistLayout);
						parentLayout.addView(imagechecklistLayout);
						layoutList.add(parentLayout);
					}
					// </image option>

					// <Rating>
					else if (fields.get(key).equals("rating")) {

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

						final LinearLayout ratingLayout = new LinearLayout(
								activity);
						LayoutParams ratingLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);

						ratingLayout.setLayoutParams(ratingLayoutParams);
						ratingLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						// drawingLayout
						// .setBackgroundResource(R.drawable.linear_layout_ui);
						ratingLayout.setOrientation(LinearLayout.VERTICAL);
						ratingLayout.setPadding(0, 10, 0, 0);
						ratingLayout.setTag(R.id.layout_id, "ratingLayout");
						ratingLayout.setTag(R.id.field_id,
								fields.get("field_id"));
						ratingLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						ratingLayout.setTag(R.id.field_required_id,
								fields.get("field_required"));
						ratingLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						ratingLayout.setTag(R.id.field_err_msg,
								fields.get("field_err_msg"));
						ratingLayout.setTag(R.id.next_ref,
								fields.get("next_ref"));
						ratingLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));
						String field_label = fields.get("field_label")
								.toString();
						String field_name = fields.get("field_name").toString();
						String field_required = fields.get("field_required")
								.toString();
						String minLabel = fields.get("field_min_label")
								.toString();
						String maxLabel = fields.get("field_max_label")
								.toString();
						String minValue = fields.get("field_min_value")
								.toString();
						String maxValue = fields.get("field_max_value")
								.toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setText(field_label + " : ");
						tvLabel.setTag("label");
						// tvLabel.setTag(fields.get("field_name"));
						tvLabel.setLayoutParams(labelLayoutParams);
                        tfManager.setTypeFace(tvLabel);
						ratingLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setPadding(10, 0, 0, 0);
						tvdescription.setText(description);
						tvdescription.setTextSize(18);
						tvdescription.setLayoutParams(labelLayoutParams);
                        tfManager.setTypeFace(tvdescription);
						ratingLayout.addView(tvdescription);

						RatingBar ratingBar = new RatingBar(activity);
						RelativeLayout.LayoutParams ratingBarLayoutParams = new RelativeLayout.LayoutParams(
								relative_WRAP_CONTENT, relative_WRAP_CONTENT);
						ratingBarLayoutParams
								.addRule(RelativeLayout.ALIGN_PARENT_TOP);
						ratingBar.setLayoutParams(ratingBarLayoutParams);
						// ratingBar.setRating(1);
						int maxValue_ = Integer.parseInt(maxValue);
						if (maxValue_ > 10)
							maxValue_ = 10;
						ratingBar.setNumStars(maxValue_);
						ratingBar.setStepSize(1);
						// etc....
						// ratingLayout.addView(ratingBar);
						ratingBar.setBackgroundColor(Color
								.parseColor("#ffffff"));

						RelativeLayout minMaxLabelLayout = new RelativeLayout(
								activity);
						minMaxLabelLayout.setBackgroundColor(Color
								.parseColor("#ffffff"));
						minMaxLabelLayout.setTag(R.id.layout_id,
								"ratingAndMinMaxLabelLayout");
						minMaxLabelLayout.setPadding(5, 8, 5, 10);
						RelativeLayout.LayoutParams ratingAndMinMaxLabelLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, relative_MATCH_PARENT);
						minMaxLabelLayout
								.setLayoutParams(ratingAndMinMaxLabelLayoutParams);

						TextView tvMinLabel = new TextView(activity);
                        tfManager.setTypeFace(tvMinLabel);
						RelativeLayout.LayoutParams tvMinLabelParams = new RelativeLayout.LayoutParams(
								relative_WRAP_CONTENT, relative_WRAP_CONTENT);
						tvMinLabelParams
								.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
						tvMinLabel.setLayoutParams(tvMinLabelParams);
						tvMinLabel.setText(minLabel);
						tvMinLabel.setTextSize(13);
						minMaxLabelLayout.addView(tvMinLabel);

						TextView tvMaxLabel = new TextView(activity);
                        tfManager.setTypeFace(tvMaxLabel);
						RelativeLayout.LayoutParams tvMaxLabelParams = new RelativeLayout.LayoutParams(
								relative_WRAP_CONTENT, relative_WRAP_CONTENT);
						tvMaxLabelParams
								.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						tvMaxLabel.setLayoutParams(tvMaxLabelParams);
						tvMaxLabel.setText(maxLabel);
						tvMaxLabel.setTextSize(13);
						minMaxLabelLayout.addView(tvMaxLabel);

						LinearLayout ratingAndLabelLayout = new LinearLayout(
								activity);
						LayoutParams ratingAndLabelLayoutParams = new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT);

						ratingAndLabelLayout
								.setLayoutParams(ratingAndLabelLayoutParams);
						ratingAndLabelLayout
								.setOrientation(LinearLayout.VERTICAL);
						ratingAndLabelLayout.setTag(R.id.layout_id,
								"ratingAndLabelLayout");
						ratingAndLabelLayout.addView(ratingBar);
						ratingAndLabelLayout.addView(minMaxLabelLayout);

						ratingLayout.addView(ratingAndLabelLayout);
						LayoutParams errorMsgLayoutParams2 = new LayoutParams(
								LayoutParams.MATCH_PARENT, 60);
						errorMsgLayoutParams2.setMargins(15, 20, 15, 0);

						scroll.addView(ratingLayout);
						parentLayout.addView(scroll);
						addErrorMsg(parentLayout);
						layoutList.add(parentLayout);
					}
					// </Rating>

					// <rating set>
					else if (fields.get(key).equals("rating_set")) {
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

						final LinearLayout ratingSetLayout = new LinearLayout(
								activity);
						LayoutParams ratingSetLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						ratingSetLayoutParams.setMargins(5, 20, 5, 0);
						ratingSetLayout.setLayoutParams(ratingSetLayoutParams);
						ratingSetLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						// drawingLayout
						// .setBackgroundResource(R.drawable.linear_layout_ui);
						ratingSetLayout.setOrientation(LinearLayout.VERTICAL);
						ratingSetLayout.setPadding(0, 10, 0, 0);
						ratingSetLayout.setTag(R.id.layout_id,
								"ratingSetLayout");
						ratingSetLayout.setTag(R.id.field_id,
								fields.get("field_id"));
						ratingSetLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						ratingSetLayout.setTag(R.id.field_required_id,
								fields.get("field_required"));
						ratingSetLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						ratingSetLayout.setTag(R.id.field_err_msg,
								fields.get("field_err_msg"));
						ratingSetLayout.setTag(R.id.next_ref,
								fields.get("next_ref"));
						ratingSetLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));
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
                        tfManager.setTypeFace(tvLabel);
						ratingSetLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setPadding(10, 0, 0, 0);
						tvdescription.setText(description);
						tvdescription.setTextSize(18);
						tvdescription.setLayoutParams(labelLayoutParams);
                        tfManager.setTypeFace(tvdescription);
						ratingSetLayout.addView(tvdescription);

						// list view here
						ListView listView = new ListView(activity);
						listView.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));

						ArrayList<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();
						String minLabel = fields.get("field_min_label")
								.toString();
						String maxLabel = fields.get("field_max_label")
								.toString();
						String maxValue = fields.get("field_max_value")
								.toString();
						JSONArray datasetValues = (JSONArray) fields
								.get("dataset_values");
						for (int dv = 0; dv < datasetValues.length(); dv++) {
							String value = datasetValues.getJSONObject(dv)
									.getString("value");
							String field_skip = datasetValues.getJSONObject(dv)
									.getString("field_skip");
							// String image =
							// datasetValues.getJSONObject(dv).getString("image");
							String label = datasetValues.getJSONObject(dv)
									.getString("label");
							String extra = datasetValues.getJSONObject(dv)
									.getString("extra");
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("minLabel", minLabel);
							map.put("maxLabel", maxLabel);
							map.put("maxValue", maxValue);
							map.put("value", value);
							map.put("field_skip", field_skip);
							map.put("image", "#no_value#");
							map.put("label", label);
							map.put("extra", extra);
							listData.add(map);
						}
						RatingSetAdapter ratingSetAdapter = new RatingSetAdapter(
								activity, listData);
						listView.setAdapter(ratingSetAdapter);

						ratingSetLayout.addView(listView);
						parentLayout.addView(ratingSetLayout);
						addErrorMsg(parentLayout);
						layoutList.add(parentLayout);
					}
					// </rating set>

					// <rating_set_image>
					else if (fields.get(key).equals("rating_set_image")) {
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

						final LinearLayout ratingSetImageLayout = new LinearLayout(
								activity);
						LayoutParams ratingSetLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						ratingSetLayoutParams.setMargins(5, 20, 5, 0);
						ratingSetImageLayout
								.setLayoutParams(ratingSetLayoutParams);
						ratingSetImageLayout
								.setGravity(Gravity.CENTER_HORIZONTAL);
						// drawingLayout
						// .setBackgroundResource(R.drawable.linear_layout_ui);
						ratingSetImageLayout
								.setOrientation(LinearLayout.VERTICAL);
						ratingSetImageLayout.setPadding(0, 10, 0, 0);
						ratingSetImageLayout.setTag(R.id.layout_id,
								"ratingSetImageLayout");
						ratingSetImageLayout.setTag(R.id.field_id,
								fields.get("field_id"));
						ratingSetImageLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						ratingSetImageLayout.setTag(R.id.field_required_id,
								fields.get("field_required"));
						ratingSetImageLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						ratingSetImageLayout.setTag(R.id.field_err_msg,
								fields.get("field_err_msg"));
						ratingSetImageLayout.setTag(R.id.next_ref,
								fields.get("next_ref"));
						ratingSetImageLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));
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
                        tfManager.setTypeFace(tvLabel);
						ratingSetImageLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setPadding(10, 0, 0, 0);
						tvdescription.setText(description);
						tvdescription.setTextSize(18);
						tvdescription.setLayoutParams(labelLayoutParams);
                        tfManager.setTypeFace(tvdescription);
						ratingSetImageLayout.addView(tvdescription);

						// list view here
						ListView listView = new ListView(activity);
						listView.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));

						ArrayList<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();
						String minLabel = fields.get("field_min_label")
								.toString();
						String maxLabel = fields.get("field_max_label")
								.toString();
						String maxValue = fields.get("field_max_value")
								.toString();
						JSONArray datasetValues = (JSONArray) fields
								.get("dataset_values");
						for (int dv = 0; dv < datasetValues.length(); dv++) {
							String value = datasetValues.getJSONObject(dv)
									.getString("value");
							String field_skip = datasetValues.getJSONObject(dv)
									.getString("field_skip");
							String image = datasetValues.getJSONObject(dv)
									.getString("image");
							String label = datasetValues.getJSONObject(dv)
									.getString("label");
							String extra = datasetValues.getJSONObject(dv)
									.getString("extra");
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("minLabel", minLabel);
							map.put("maxLabel", maxLabel);
							map.put("maxValue", maxValue);
							map.put("value", value);
							map.put("field_skip", field_skip);
							map.put("image", image);
							map.put("label", label);
							map.put("extra", extra);
							listData.add(map);
						}
						RatingSetAdapter ratingSetAdapter = new RatingSetAdapter(
								activity, listData);
						listView.setAdapter(ratingSetAdapter);
						ratingSetImageLayout.addView(listView);

						parentLayout.addView(ratingSetImageLayout);
						addErrorMsg(parentLayout);
						layoutList.add(parentLayout);
					}
					// </rating_set_image>

					// <text_set>
					else if (fields.get(key).equals("text_set")) {

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

						LinearLayout textSetLayout = new LinearLayout(activity);
						// LayoutParams cbLayoutParams = new LayoutParams(
						// LayoutParams.MATCH_PARENT,
						// LayoutParams.WRAP_CONTENT);
						// cbLayoutParams.setMargins(0, 25, 0, 25);
						// checkBoxLayout.setLayoutParams(cbLayoutParams);
						textSetLayout.setLayoutParams(innerLayoutParams);
						// checkBoxLayout
						// .setBackgroundResource(R.drawable.linear_layout_ui);
						textSetLayout.setOrientation(LinearLayout.VERTICAL);
						textSetLayout.setTag(R.id.layout_id, "textSetLayout");
						textSetLayout.setTag(R.id.field_id,
								fields.get("field_id"));
						textSetLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						textSetLayout.setTag(R.id.field_required_id,
								fields.get("field_required"));
						textSetLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						textSetLayout.setTag(R.id.field_err_msg,
								fields.get("field_err_msg"));
						textSetLayout.setTag(R.id.next_ref,
								fields.get("next_ref"));
						textSetLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));
						textSetLayout.setTag(R.id.field_min_value,
								fields.get("field_min_value"));
						JSONArray dataset_values = (JSONArray) fields
								.get("dataset_values");
						textSetLayout.setTag(R.id.dataset_values,
								dataset_values);
						String checkLabel = fields.get("field_label")
								.toString();

						if (fields.containsKey("render_ref"))
							textSetLayout.setTag(R.id.render_ref,
									fields.get("render_ref"));
						if (fields.containsKey("render_ref_type"))
							textSetLayout.setTag(R.id.render_ref_type,
									fields.get("render_ref_type"));

						// tma: tagging ref_setter value
						if (fields.containsKey("ref_setter")) {
							textSetLayout.setTag(R.id.ref_setter,
									fields.get("ref_setter").toString());
						}

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
                        tfManager.setTypeFace(tvLabel);
						textSetLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setText(description);
						tvdescription.setTextSize(descriptionTextSize);
						tvdescription.setLayoutParams(descriptionLayoutParams);
                        tfManager.setTypeFace(tvdescription);
						textSetLayout.addView(tvdescription);

						// int default_value = Integer.parseInt(fields.get(
						// "field_default_value").toString());

						ListView listView = new ListView(activity);
						listView.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));

						ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

						JSONArray datasetValues = (JSONArray) fields
								.get("dataset_values");
						for (int dv = 0; dv < datasetValues.length(); dv++) {
							String value = datasetValues.getJSONObject(dv)
									.getString("value");
							String field_skip = datasetValues.getJSONObject(dv)
									.getString("field_skip");
							String image = datasetValues.getJSONObject(dv)
									.getString("image");
							String label = datasetValues.getJSONObject(dv)
									.getString("label");
							String extra = datasetValues.getJSONObject(dv)
									.getString("extra");
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("value", value);
							map.put("field_skip", field_skip);
							map.put("image", image);
							map.put("label", label);
							map.put("extra", extra);
							data.add(map);
						}

						TextSetAdapter textSetAdapter = new TextSetAdapter(
								activity, data);
						listView.setAdapter(textSetAdapter);
						listView.setFocusable(false);
						listView.setFocusableInTouchMode(true);
						listView.setItemsCanFocus(true);
						textSetLayout.addView(listView);

						parentLayout.addView(textSetLayout);
						addErrorMsg(parentLayout);
						layoutList.add(parentLayout);
					}
					// </text_set>

					// <number_set>
					else if (fields.get(key).equals("number_set")) {

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

						LinearLayout numberSetLayout = new LinearLayout(
								activity);
						// LayoutParams cbLayoutParams = new LayoutParams(
						// LayoutParams.MATCH_PARENT,
						// LayoutParams.WRAP_CONTENT);
						// cbLayoutParams.setMargins(0, 25, 0, 25);
						// checkBoxLayout.setLayoutParams(cbLayoutParams);
						numberSetLayout.setLayoutParams(innerLayoutParams);
						// checkBoxLayout
						// .setBackgroundResource(R.drawable.linear_layout_ui);
						numberSetLayout.setOrientation(LinearLayout.VERTICAL);
						numberSetLayout.setTag(R.id.layout_id,
								"numberSetLayout");
						numberSetLayout.setTag(R.id.field_id,
								fields.get("field_id"));
						numberSetLayout.setTag(R.id.field_name_id,
								fields.get("field_name"));
						numberSetLayout.setTag(R.id.field_required_id,
								fields.get("field_required"));
						numberSetLayout.setTag(R.id.field_label_id,
								fields.get("field_label"));
						numberSetLayout.setTag(R.id.field_err_msg,
								fields.get("field_err_msg"));
						numberSetLayout.setTag(R.id.next_ref,
								fields.get("next_ref"));
						numberSetLayout.setTag(R.id.next_ref_cond,
								fields.get("next_ref_cond"));
						numberSetLayout.setTag(R.id.next_ref_type,
								fields.get("next_ref_type"));
						numberSetLayout.setTag(R.id.render_ref,
								fields.get("render_ref"));
						numberSetLayout.setTag(R.id.render_ref_type,
								fields.get("render_ref_type"));
						if (fields.containsKey("field_min_value"))
							numberSetLayout.setTag(R.id.field_min_value,
									fields.get("field_min_value"));
						if (fields.containsKey("field_max_value"))
							numberSetLayout.setTag(R.id.field_max_value,
									fields.get("field_max_value"));
						JSONArray dataset_values = (JSONArray) fields
								.get("dataset_values");
						numberSetLayout.setTag(R.id.dataset_values,
								dataset_values);
						String checkLabel = fields.get("field_label")
								.toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setText(checkLabel + " ");
						tvLabel.setTextSize(labelTextSize);
						String fieldName = fields.get("field_name").toString();
						String fieldRequired = fields.get("field_required")
								.toString();
						String fieldLabel = fields.get("field_label")
								.toString();
						tvLabel.setLayoutParams(labelLayoutParams);
                        tfManager.setTypeFace(tvLabel);
						numberSetLayout.addView(tvLabel);

						// adding description
						String description = fields.get("field_desc")
								.toString();
						TextView tvdescription = new TextView(activity);
						tvdescription.setText(description);
						tvdescription.setTextSize(descriptionTextSize);
						tvdescription.setLayoutParams(descriptionLayoutParams);
                        tfManager.setTypeFace(tvdescription);
						numberSetLayout.addView(tvdescription);

						ListView listView = new ListView(activity);
						listView.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));

						ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

						JSONArray datasetValues = (JSONArray) fields
								.get("dataset_values");
						for (int dv = 0; dv < datasetValues.length(); dv++) {
							String value = datasetValues.getJSONObject(dv)
									.getString("value");
							String field_skip = datasetValues.getJSONObject(dv)
									.getString("field_skip");
							String image = datasetValues.getJSONObject(dv)
									.getString("image");
							String label = datasetValues.getJSONObject(dv)
									.getString("label");
							String extra = datasetValues.getJSONObject(dv)
									.getString("extra");
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("value", value);
							map.put("field_skip", field_skip);
							map.put("image", image);
							map.put("label", label);
							map.put("extra", extra);
							data.add(map);
						}

						NumberSetAdapter numberSetAdapter = new NumberSetAdapter(
								activity, data);
						listView.setAdapter(numberSetAdapter);
						listView.setFocusable(false);
						listView.setFocusableInTouchMode(false);
						listView.setItemsCanFocus(true);
						numberSetLayout.addView(listView);

						parentLayout.addView(numberSetLayout);
						addErrorMsg(parentLayout);
						layoutList.add(parentLayout);
					}
					// </number_set>
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
					fields.put("next_ref", jChild.getString("next_ref"));
					fields.put("next_ref_cond",
							jChild.getJSONArray("next_ref_cond"));
					String field_desc = jChild.getString("field_desc");
					if (field_desc.length() > 0)
						fields.put("field_desc", field_desc);
					else
						fields.put("field_desc", "-");
					fields.put("field_required",
							jChild.getString("field_required"));
					// fields.put("field_default_value",
					// jChild.getString("field_default_value"));
				} else if (field_type.equals("option")) {
					// with dataset (s and checklist)
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_help", jChild.getString("field_help"));
					if (jChild.has("field_random")) {
						fields.put("field_random",
								jChild.getString("field_random"));
					}
					if (jChild.has("next_ref"))
						fields.put("next_ref", jChild.getString("next_ref"));
					if (jChild.has("next_ref_cond"))
						fields.put("next_ref_cond",
								jChild.getJSONArray("next_ref_cond"));
					if (jChild.has("next_ref_type")) {
						fields.put("next_ref_type",
								jChild.getString("next_ref_type"));
					}
					if (jChild.has("next_ref"))
						fields.put("next_ref", jChild.getString("next_ref"));
					if (jChild.has("next_ref_type"))
						fields.put("next_ref_type",
								jChild.getString("next_ref_type"));
                    Boolean audio = null;
                    audio = jChild.getBoolean("field_audio_required");
                    if(jChild.has("field_audio_required")){
                        fields.put("field_audio_required",audio);
                    }
                    if((audio) && jChild.has("field_audio_recorder_display")){
                        fields.put("field_audio_recorder_display",jChild.getBoolean("field_audio_recorder_display"));
                    }
                    else if(audio){
                        //default to true
                        fields.put("field_audio_recorder_display",true);
                    }

					String field_desc = jChild.getString("field_desc");
					if (field_desc.length() > 0)
						fields.put("field_desc", field_desc);
					else
						fields.put("field_desc", "-");
					fields.put("field_required",
							jChild.getBoolean("field_required"));
					// if(jChild.has("field_default_value"))
					// fields.put("field_default_value",
					// jChild.get("field_default_value"));
					HashMap<String, String> field_data_set = new HashMap<String, String>();
					JSONObject field_dataset = jChild
							.getJSONObject("field_dataset");
					JSONArray dataset_values = field_dataset
							.getJSONArray("dataset_values");
					String dataset_name = field_dataset
							.getString("dataset_name");
					fields.put("dataset_values", dataset_values);
					fields.put("dataset_name", dataset_name);
				} else if (field_type.equals("checklist")) {
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_help", jChild.getString("field_help"));
					/*
					 * if(jChild.has("ignore_other")) fields.put("ignore_other",
					 * jChild.getBoolean("ignore_other"));
					 */
					if (jChild.has("next_ref"))
						fields.put("next_ref", jChild.getString("next_ref"));
					if (jChild.has("next_ref_cond"))
						fields.put("next_ref_cond",
								jChild.getJSONArray("next_ref_cond"));
					String field_desc = jChild.getString("field_desc");
					if (field_desc.length() > 0)
						fields.put("field_desc", field_desc);
					else
						fields.put("field_desc", "-");
					fields.put("field_required",
							jChild.getBoolean("field_required"));

					if (jChild.has("field_err_msg"))
						fields.put("field_err_msg",
								jChild.getString("field_err_msg"));

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
					} else {
					}
					// fields.put("field_default_value",
					// jChild.getString("field_default_value"));
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
					if (jChild.has("next_ref"))
						fields.put("next_ref", jChild.getString("next_ref"));
					if (jChild.has("next_ref_cond"))
						fields.put("next_ref_cond",
								jChild.getJSONArray("next_ref_cond"));
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
					if (jChild.has("next_ref"))
						fields.put("next_ref", jChild.getString("next_ref"));
					if (jChild.has("next_ref_cond"))
						fields.put("next_ref_cond",
								jChild.getJSONArray("next_ref_cond"));
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
					if (jChild.has("next_ref"))
						fields.put("next_ref", jChild.getString("next_ref"));
					if (jChild.has("next_ref_cond"))
						fields.put("next_ref_cond",
								jChild.getJSONArray("next_ref_cond"));
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
					// fields.put("field_default_value",
					// jChild.getString("field_default_value"));
					fields.put("field_err_msg",
							jChild.getString("field_err_msg"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_max_value",
							jChild.getString("field_max_value"));
					if (jChild.has("next_ref"))
						fields.put("next_ref", jChild.getString("next_ref"));
					if (jChild.has("next_ref_cond"))
						fields.put("next_ref_cond",
								jChild.getJSONArray("next_ref_cond"));
					if (jChild.has("next_ref_type"))
						fields.put("next_ref_type",
								jChild.getString("next_ref_type"));
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
                    Boolean audio = null;
                    audio = jChild.getBoolean("field_audio_required");
                    if(jChild.has("field_audio_required")){
                        fields.put("field_audio_required",audio);
                    }
                    if((audio) && jChild.has("field_audio_recorder_display")){
                        fields.put("field_audio_recorder_display",jChild.getBoolean("field_audio_recorder_display"));
                    }
                    else if(audio){
                        fields.put("field_audio_recorder_display",true);
                    }

					fields.put("field_required",
							jChild.getString("field_required"));
					if (jChild.getString("field_help").length() > 0)
						fields.put("field_help", jChild.getString("field_help"));
					// if (jChild.getString("field_default_value").length() > 0)
					// fields.put("field_default_value",
					// jChild.getString("field_default_value"));
					fields.put("field_label", jChild.getString("field_label"));
					if (jChild.has("next_ref"))
						fields.put("next_ref", jChild.getString("next_ref"));
					if (jChild.has("next_ref_cond"))
						fields.put("next_ref_cond",
								jChild.getJSONArray("next_ref_cond"));
					if (jChild.has("field_multiline")
							&& jChild.getString("field_multiline").length() > 0)
						fields.put("field_multiline",
								jChild.getString("field_multiline"));
					if (jChild.has("next_ref_type"))
						fields.put("next_ref_type",
								jChild.getString("next_ref_type"));
				} else if (field_type.equals("note")) {
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_desc", jChild.getString("field_desc"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_help", jChild.getString("field_help"));
					if (jChild.has("next_ref"))
						fields.put("next_ref", jChild.getString("next_ref"));

					// if (jChild.has("field_default_value"))
					// fields.put("field_default_value",
					// jChild.getString("field_default_value"));
					fields.put("field_label", jChild.getString("field_label"));
					if (jChild.has("next_ref_cond"))
						fields.put("next_ref_cond",
								jChild.getJSONArray("next_ref_cond"));
				} else if (field_type.equals("matrix_number")) {
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_desc", jChild.getString("field_desc"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put(
							"matrix_values",
							jChild.getJSONObject("field_matrix").getJSONArray(
									"matrix_values"));
					fields.put("field_dataset_v",
							jChild.getJSONObject("field_dataset_v"));
					fields.put("field_required",
							jChild.getString("field_required"));
					fields.put("field_help", jChild.getString("field_help"));
					if (jChild.has("next_ref"))
						fields.put("next_ref", jChild.getString("next_ref"));
					fields.put("field_dataset_h",
							jChild.getJSONObject("field_dataset_h"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_id", jChild.getString("field_id"));
				} else if (field_type.equals("matrix_text")) {
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_desc", jChild.getString("field_desc"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put(
							"matrix_values",
							jChild.getJSONObject("field_matrix").getJSONArray(
									"matrix_values"));
					fields.put("field_dataset_v",
							jChild.getJSONObject("field_dataset_v"));
					fields.put("field_required",
							jChild.getString("field_required"));
					fields.put("field_help", jChild.getString("field_help"));
					if (jChild.has("next_ref"))
						fields.put("next_ref", jChild.getString("next_ref"));
					fields.put("field_dataset_h",
							jChild.getJSONObject("field_dataset_h"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_id", jChild.getString("field_id"));
				} else if (field_type.equals("matrix_option")) {
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_desc", jChild.getString("field_desc"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put(
							"matrix_values",
							jChild.getJSONObject("field_matrix").getJSONArray(
									"matrix_values"));
					fields.put("field_dataset_v",
							jChild.getJSONObject("field_dataset_v"));
					fields.put("field_required",
							jChild.getString("field_required"));
					fields.put("field_help", jChild.getString("field_help"));
					if (jChild.has("next_ref"))
						fields.put("next_ref", jChild.getString("next_ref"));
					fields.put("field_dataset_h",
							jChild.getJSONObject("field_dataset_h"));
					if (jChild.has("render_ref"))
						fields.put("render_ref", jChild.getString("render_ref"));
					if (jChild.has("render_ref_type"))
						fields.put("render_ref_type",
								jChild.getString("render_ref_type"));
					if (jChild.has("field_random_v"))
						fields.put("field_random_v",
								jChild.getString("field_random_v"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_id", jChild.getString("field_id"));
				} else if (field_type.equals("matrix_checklist")) {
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_desc", jChild.getString("field_desc"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put(
							"matrix_values",
							jChild.getJSONObject("field_matrix").getJSONArray(
									"matrix_values"));
					fields.put("field_dataset_v",
							jChild.getJSONObject("field_dataset_v"));
					fields.put("field_required",
							jChild.getString("field_required"));
					fields.put("field_help", jChild.getString("field_help"));
					if (jChild.has("next_ref"))
						fields.put("next_ref", jChild.getString("next_ref"));
					fields.put("field_dataset_h",
							jChild.getJSONObject("field_dataset_h"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_id", jChild.getString("field_id"));
				} else if (field_type.equals("image_checklist")) {
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_help", jChild.getString("field_help"));
					if (jChild.has("next_ref"))
						fields.put("next_ref", jChild.getString("next_ref"));
					if (jChild.has("next_ref_cond"))
						fields.put("next_ref_cond",
								jChild.getJSONArray("next_ref_cond"));
					String field_desc = jChild.getString("field_desc");
					if (field_desc.length() > 0)
						fields.put("field_desc", field_desc);
					else
						fields.put("field_desc", "-");
					fields.put("field_required",
							jChild.getString("field_required"));
					// fields.put("field_default_value",
					// jChild.getString("field_default_value"));
					JSONArray dataset_values = jChild.getJSONObject(
							"field_dataset").getJSONArray("dataset_values");
					fields.put("dataset_values", dataset_values);
				} else if (field_type.equals("image_option")) {
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_help", jChild.getString("field_help"));
					if (jChild.has("next_ref"))
						fields.put("next_ref", jChild.getString("next_ref"));
					if (jChild.has("next_ref_cond"))
						fields.put("next_ref_cond",
								jChild.getJSONArray("next_ref_cond"));
					String field_desc = jChild.getString("field_desc");
					if (field_desc.length() > 0)
						fields.put("field_desc", field_desc);
					else
						fields.put("field_desc", "-");
					fields.put("field_required",
							jChild.getString("field_required"));
					// fields.put("field_default_value",
					// jChild.getString("field_default_value"));
					JSONArray dataset_values = jChild.getJSONObject(
							"field_dataset").getJSONArray("dataset_values");
					fields.put("dataset_values", dataset_values);
				} else if (field_type.equals("rating")) {
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_help", jChild.getString("field_help"));
					if (jChild.has("next_ref"))
						fields.put("next_ref", jChild.getString("next_ref"));
					if (jChild.has("next_ref_cond"))
						fields.put("next_ref_cond",
								jChild.getJSONArray("next_ref_cond"));
					fields.put("field_desc", jChild.getString("field_desc"));
					fields.put("field_required",
							jChild.getString("field_required"));
					fields.put("field_max_label",
							jChild.getString("field_max_label"));
					fields.put("field_max_value",
							jChild.getString("field_max_value"));
					fields.put("field_min_label",
							jChild.getString("field_min_label"));
					fields.put("field_min_value",
							jChild.getString("field_min_value"));
					// fields.put("field_default_value",
					// jChild.getString("field_default_value"));
				} else if (field_type.equals("rating_set")
						|| field_type.equals("rating_set_image")) {
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_help", jChild.getString("field_help"));
					if (jChild.has("next_ref"))
						fields.put("next_ref", jChild.getString("next_ref"));
					if (jChild.has("next_ref_cond"))
						fields.put("next_ref_cond",
								jChild.getJSONArray("next_ref_cond"));
					fields.put("field_desc", jChild.getString("field_desc"));
					fields.put("field_required",
							jChild.getString("field_required"));
					fields.put("field_max_label",
							jChild.getString("field_max_label"));
					fields.put("field_max_value",
							jChild.getString("field_max_value"));
					fields.put("field_min_label",
							jChild.getString("field_min_label"));
					fields.put("field_min_value",
							jChild.getString("field_min_value"));
					// fields.put("field_default_value",
					// jChild.getString("field_default_value"));
					fields.put("field_audio_required",
							jChild.getString("field_audio_required"));
					JSONArray dataset_values = jChild.getJSONObject(
							"field_dataset").getJSONArray("dataset_values");
					fields.put("dataset_values", dataset_values);
				} else if (field_type.equals("text_set")
						|| field_type.equals("number_set")) {
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_help", jChild.getString("field_help"));
					if (jChild.has("field_min_value"))
						fields.put("field_min_value",
								jChild.getString("field_min_value"));
					if (jChild.has("field_max_value"))
						fields.put("field_max_value",
								jChild.getString("field_max_value"));
					if (jChild.has("next_ref"))
						fields.put("next_ref", jChild.getString("next_ref"));
					if (jChild.has("next_ref_type"))
						fields.put("next_ref_type",
								jChild.getString("next_ref_type"));
					if (jChild.has("next_ref_cond"))
						fields.put("next_ref_cond",
								jChild.getJSONArray("next_ref_cond"));
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
				}

				// tma: general fields go here
				if (jChild.has("ref_setter"))
					fields.put("ref_setter", jChild.getString("ref_setter"));
				if (jChild.has("render_ref"))
					fields.put("render_ref", jChild.getString("render_ref"));
				if (jChild.has("render_ref_type"))
					fields.put("render_ref_type",
							jChild.getString("render_ref_type"));

				if (!fields.isEmpty())
					fieldList.add(fields);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			ToastManager xaveyToast = new ToastManager(activity);
			xaveyToast.xaveyToast(null, e.getMessage());
		}
		return fieldList;
	}

	public ArrayList<HashMap<String, Object>> getFormRefs(String form_json) {
		ArrayList<HashMap<String, Object>> refList = new ArrayList<HashMap<String, Object>>();
		try {
			JSONObject json = new JSONObject(
					convertStandardJSONString(form_json));
            if(json.has("form_refs")){
                JSONArray form_refs = json.getJSONArray("form_refs");
                for (int i = 0; i < form_refs.length(); i++) {
                    JSONObject jChild = form_refs.getJSONObject(i);
                    HashMap<String, Object> fields = new HashMap<String, Object>();
                    fields.put("ref_id", jChild.getString("ref_id"));
                    fields.put("ref_name", jChild.getString("ref_name"));
                    fields.put("ref_type", jChild.getString("ref_type"));
                    fields.put("ref_setter", jChild.getJSONArray("ref_setter"));
                    if (jChild.has("ref_match")) {
                        fields.put("ref_match",jChild.getJSONArray("ref_match"));
                    }
                    if (!fields.isEmpty()) {
                        refList.add(fields);
                    }
                }
            }
		} catch (JSONException e) {
			e.printStackTrace();
			ToastManager xaveyToast = new ToastManager(activity);
			xaveyToast.xaveyToast(null, e.getMessage());
		}
		return refList;
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
                if (jChild.has("field_id")) {
                    fields.put("field_id", jChild.getString("field_id"));
                    fields.put("field_name", jChild.getString("field_name"));
                    fields.put("field_value", jChild.getString("field_value"));
                    fields.put("field_label", jChild.getString("field_label"));
                    if (jChild.has("field_audio")) {
                        fields.put("field_audio", jChild.getString("field_audio"));
                    }
                    fieldList.add(fields);
                }
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
		if (data_json != null) {
			data_json = data_json.replace("\\", "");
			data_json = data_json.replace("\"{", "{");
			data_json = data_json.replace("}\",", "},");
			data_json = data_json.replace("}\"", "}");
			data_json = data_json.replace("\"[", "[");
			data_json = data_json.replace("]\"", "]");
		}
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
            if (map.containsKey("field_id")) {
                fieldNode.put("field_id", map.get("field_id"));
                fieldNode.put("field_label", map.get("field_label"));
                fieldNode.put("field_name", map.get("field_name"));
                fieldNode.put("field_value", map.get("field_value"));
                if (map.containsKey("field_audio")) {
                    fieldNode.put("field_audio", map.get("field_audio"));
                }
            }
			dataArray.put(fieldNode);
		}

        JSONObject tempJSONLoc = new JSONObject(
                convertStandardJSONString(document_JSON));
        JSONArray tempJSONLocArray = tempJSONLoc.getJSONArray("document_json");
        for(int i=0; i<tempJSONLocArray.length(); i++){
            if(tempJSONLocArray.getJSONObject(i).has("location")){
                document_json.put("location", tempJSONLocArray.getJSONObject(i).get("location"));
            }
        }

        //tempJSONLoc.

		// location child node
		JSONObject locationChildNode = new JSONObject();

		//locationChildNode.put("lat", document.getGpsInfo().get("lat"));
		//locationChildNode.put("lng", document.getGpsInfo().get("lng"));

		document_json.put("data", dataArray);
		document_json.put("timestamp", timestamp);
        document_json.put("submit_datetime", document.getCreated_at());
		//document_json.put("location", locationChildNode);
		document_json.put("org", orgChildNode);
		document_json.put("form", formChildNode);
		document_json.put("worker", workerChildNode);
		document_json.put("document_id", document.getDocument_id());

		JSONArray mainArray = new JSONArray();
		mainArray.put(document_json);
		return mainArray;
	}

	public String readValueFromLayout(LinearLayout parentLayout) {
		// -1 don't care the last room cuz the last room is submitLayout
		LinearLayout linearLayout = getInnerLayout(parentLayout);
		for (int p = 0; p < parentLayout.getChildCount(); p++) {
			View child = parentLayout.getChildAt(p);
			if (child.getClass().getName()
					.equals("android.widget.LinearLayout")
					&& !child.getTag(R.id.layout_id).toString()
							.equals("recordingLayout")
					&& child.getTag(R.id.layout_id) != null) {
				linearLayout = (LinearLayout) parentLayout.getChildAt(p);
				break;
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
				if (subClass.getName().equals("com.xavey.android.layout.RadioGroupLayout")) {
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
				} else if (subClass.getName().equals("com.xavey.android.layout.CheckboxLayout")) {
					CheckboxLayout checkBoxWrapper = (CheckboxLayout) linearLayout.getChildAt(z);
                    for (int d = 0; d < checkBoxWrapper.getChildCount(); d++) {
                        LinearLayout checkBoxLine = null;
                        View cbLineLayoutChild = checkBoxWrapper.getChildAt(d);
                        if (cbLineLayoutChild.getClass().getName()
                                .equals("android.widget.LinearLayout")) {
                            checkBoxLine = (LinearLayout) checkBoxWrapper
                                    .getChildAt(d);
                            CheckBox cb = getCheckBoxFromCheckBoxLine(checkBoxLine);
                            if (cb.isChecked()) {
                                String value = cb.getText().toString();
                                checkedValues += "|" + value;
                            }
                        }
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
                .equals("imageChecklistLayout")) {
            // JSONArray checkedValues = new JSONArray();
            String checkedValues = "";
            String key = linearLayout.getTag(R.id.field_id).toString();
            String field_label = linearLayout.getTag(R.id.field_label_id)
                    .toString();

            for (int l = 0; l < linearLayout.getChildCount(); l++) {
                Class<?> subClass = (Class<?>) linearLayout.getChildAt(l)
                        .getClass();
                if (subClass.getName().equals("android.widget.GridView")) {
                    GridView gridView = (GridView) linearLayout
                            .getChildAt(l);
                    ArrayList<String> selectedValueList = (ArrayList<String>) gridView
                            .getTag(R.id.selected_grid_values);
                    for (String selectedValue : selectedValueList) {
                        checkedValues += "|" + selectedValue;
                    }
                }
            }
            if (checkedValues.length() > 0){
                checkedValues = checkedValues.substring(1); // <- it deletes
            }
            else {
                checkedValues = "-";
            }
            return checkedValues;
        }else if (linearLayout.getTag(R.id.layout_id).toString()
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

    private CheckBox getCheckBoxFromCheckBoxLine(LinearLayout checkBoxLine) {
        View view = null;
        for (int i = 0; i < checkBoxLine.getChildCount(); i++) {
            if (checkBoxLine.getChildAt(i).getClass().getName().toString()
                    .equals("android.widget.CheckBox")) {
                view = checkBoxLine.getChildAt(i);
                break;
            }
        }
        return (CheckBox) view;
    }

	private LinearLayout getInnerLayout(LinearLayout parrentLayout) {
		LinearLayout innerLayout = null;
		LinearLayout innerLayout2 = null;
		for (int i = 0; i < parrentLayout.getChildCount(); i++) {
			String className = parrentLayout.getChildAt(i).getClass().getName();
			View v = parrentLayout.getChildAt(i);

			if (className.equals("android.widget.ScrollView")) {
				ScrollView scroll = (ScrollView) v;
				innerLayout = (LinearLayout) scroll.getChildAt(0);
				String layoutID = innerLayout.getTag(R.id.layout_id).toString();
				if (innerLayout.getTag(R.id.layout_id) != null
						&& !layoutID.equals("recordingLayout")) {
					return innerLayout;
				}
				// else{
				// return null;
				// }
			}
			// following else is for layouts without ScrollView
			else if (className.equals("android.widget.LinearLayout")) {
				innerLayout2 = (LinearLayout) parrentLayout.getChildAt(i);
				if (innerLayout2.getTag(R.id.layout_id) != null
						&& innerLayout2.getTag(R.id.layout_id).toString() != "recordingLayout") {
					Log.i("child count", innerLayout2.getChildCount() + "");
					return innerLayout2;
				}
			}
		}
		if (innerLayout != null)
			return innerLayout;
		else
			return innerLayout2;
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

	public void addErrorMsg(LinearLayout parentLayout) {
		TextView errorMsg = new TextView(activity);
		errorMsg.setLayoutParams(errorMsgLayoutParams);
        tfManager.setTypeFace(errorMsg);
		errorMsg.setTextSize(12);
		errorMsg.setTag("errorMsg");
		parentLayout.addView(errorMsg);
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

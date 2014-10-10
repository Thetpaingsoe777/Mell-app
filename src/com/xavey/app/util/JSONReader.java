package com.xavey.app.util;

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
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.Environment;
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
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.xavey.app.ApplicationValues;
import com.xavey.app.MainActivity;
import com.xavey.app.R;
import com.xavey.app.R.drawable;
import com.xavey.app.R.layout;
import com.xavey.app.db.XaveyDBHelper;
import com.xavey.app.model.Document;
import com.xavey.app.model.Field;
import com.xavey.app.model.Form;
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

	public JSONReader(Activity activity) {
		this.activity = activity;
		dbHelper = new XaveyDBHelper(activity);
		gps = new GPSTracker(activity);
		typeface = new TypeFaceManager(activity);
		displayManager = new DisplayManager(this.activity);
	}

	// Layout setting here
	// get incoming JSONString and return a linear layout
	/*
	 * public LinearLayout readForm(Form form) throws JSONException {
	 * LinearLayout lL = new LinearLayout(activity);
	 * lL.setBackgroundColor(Color.WHITE);
	 * lL.setOrientation(LinearLayout.VERTICAL);
	 * 
	 * TextView tv1 = new TextView(activity); tv1.setText("Form Title : " +
	 * form.getForm_title()); tv1.setTextSize(15); tv1.setTypeface(null,
	 * Typeface.BOLD_ITALIC); tv1.setId(View.getDefaultSize(0, 2000));
	 * LayoutParams llp = new LayoutParams(LayoutParams.WRAP_CONTENT,
	 * LayoutParams.WRAP_CONTENT); llp.setMargins(0, 10, 0, 10);
	 * tv1.setLayoutParams(llp); lL.addView(tv1);
	 * 
	 * TextView tv2 = new TextView(activity); tv2.setText("Form Subtile : " +
	 * form.getForm_subtitle()); tv2.setTextSize(15); tv2.setTypeface(null,
	 * Typeface.BOLD_ITALIC); llp = new LayoutParams(LayoutParams.WRAP_CONTENT,
	 * LayoutParams.WRAP_CONTENT); llp.setMargins(0, 10, 0, 30);
	 * tv2.setLayoutParams(llp); lL.addView(tv2);
	 * 
	 * // TypeFaceManager tfManager = new TypeFaceManager(activity); // Typeface
	 * zawGyiTypeFace = tfManager.getZawGyiTypeFace();
	 * 
	 * ArrayList<HashMap<String, Object>> formFields = getFormFields(form
	 * .getForm_json());
	 * 
	 * for (int i = 0; i < formFields.size(); i++) { HashMap<String, Object>
	 * fields = formFields.get(i); for (Object key : fields.keySet()) { if
	 * (key.equals("field_type")) { if (fields.get(key).equals("text")) {
	 * LinearLayout textLayout = new LinearLayout(activity); LayoutParams
	 * textLayoutParams = new LayoutParams( LayoutParams.MATCH_PARENT,
	 * LayoutParams.WRAP_CONTENT); textLayoutParams.setMargins(0, 20, 0, 0);
	 * textLayout.setLayoutParams(textLayoutParams);
	 * textLayout.setOrientation(LinearLayout.VERTICAL);
	 * textLayout.setTag("textLayout"); String textLabel =
	 * fields.get("field_label") .toString(); TextView tvLabel = new
	 * TextView(activity); tvLabel.setText(textLabel); tvLabel.setTag("label");
	 * tvLabel.setTag(R.id.field_name_id,fields.get("field_name"));
	 * tvLabel.setTag(R.id.field_required_id,
	 * isFieldRequired(fields.get("field_required")));
	 * tvLabel.setTag(R.id.field_label_id, textLabel);
	 * tvLabel.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,
	 * 50)); textLayout.addView(tvLabel);
	 * 
	 * EditText ed1 = new EditText(activity); ed1.setTextSize(12);
	 * ed1.setTypeface(typeface.getMyanmar3TypeFace()); String fieldName =
	 * fields.get("field_name").toString(); String fieldHelp="-";
	 * if(fields.containsKey("field_help")) fieldHelp =
	 * fields.get("field_help").toString();
	 * 
	 * ed1.setGravity(Gravity.LEFT); ed1.setSingleLine(true);
	 * ed1.setHint(fieldHelp);
	 * 
	 * LayoutParams lp = new LayoutParams( LayoutParams.MATCH_PARENT, 80); //
	 * lp.setMargins(0, 20, 0, 20); ed1.setLayoutParams(lp);
	 * textLayout.addView(ed1);
	 * 
	 * // error msg TextView errorMsg = new TextView(activity);
	 * errorMsg.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,
	 * 0)); errorMsg.setTextSize(12); errorMsg.setTag("errorMsg");
	 * textLayout.addView(errorMsg);
	 * 
	 * lL.addView(textLayout); }if (fields.get(key).equals("number")) {
	 * LinearLayout numberLayout = new LinearLayout(activity); LayoutParams
	 * numberLayoutParams = new LayoutParams( LayoutParams.MATCH_PARENT,
	 * LayoutParams.WRAP_CONTENT); numberLayoutParams.setMargins(0, 20, 0, 0);
	 * numberLayout.setLayoutParams(numberLayoutParams);
	 * numberLayout.setOrientation(LinearLayout.VERTICAL);
	 * numberLayout.setTag("numberLayout"); String textLabel =
	 * fields.get("field_label") .toString(); TextView tvLabel = new
	 * TextView(activity); tvLabel.setText(textLabel); tvLabel.setTag("label");
	 * tvLabel.setTag(R.id.field_name_id,fields.get("field_name"));
	 * tvLabel.setTag(R.id.field_required_id,
	 * isFieldRequired(fields.get("field_required")));
	 * tvLabel.setTag(R.id.field_label_id, textLabel);
	 * tvLabel.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,
	 * 50)); numberLayout.addView(tvLabel);
	 * 
	 * EditText ed1 = new EditText(activity); ed1.setTextSize(12);
	 * ed1.setTypeface(typeface.getMyanmar3TypeFace()); String fieldName =
	 * fields.get("field_name").toString(); String fieldHelp="-";
	 * if(fields.containsKey("field_help")) fieldHelp =
	 * fields.get("field_help").toString();
	 * 
	 * ed1.setGravity(Gravity.LEFT); ed1.setSingleLine(true);
	 * ed1.setHint(fieldHelp);
	 * 
	 * LayoutParams lp = new LayoutParams( LayoutParams.MATCH_PARENT, 80); //
	 * lp.setMargins(0, 20, 0, 20); ed1.setLayoutParams(lp); ed1.setEms(50);
	 * ed1.setInputType(InputType.TYPE_CLASS_NUMBER);
	 * ed1.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
	 * numberLayout.addView(ed1);
	 * 
	 * // error msg TextView errorMsg = new TextView(activity);
	 * errorMsg.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,
	 * 0)); errorMsg.setTextSize(12); errorMsg.setTag("errorMsg");
	 * numberLayout.addView(errorMsg); lL.addView(numberLayout); }else if
	 * (fields.get(key).equals("date")) { DatePicker datePicker = new
	 * DatePicker(activity); datePicker.setCalendarViewShown(false); String
	 * fieldName = fields.get("field_name").toString(); String fieldRequried =
	 * fields.get("field_required").toString();
	 * datePicker.setTag(R.id.field_name_id, fieldName); // followig is meaning
	 * less for date datePicker.setTag(R.id.field_required_id, fieldRequried);
	 * 
	 * LayoutParams lp = new LayoutParams( LayoutParams.WRAP_CONTENT,
	 * LayoutParams.WRAP_CONTENT); lp.setMargins(0, 15, 0, 0);
	 * datePicker.setLayoutParams(lp); TextView tvFieldName = new
	 * TextView(activity); tvFieldName.setText(fieldName); lp = new
	 * LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); //
	 * lp.setMargin tvFieldName.setLayoutParams(lp); lL.addView(tvFieldName);
	 * lL.addView(datePicker); } else if (fields.get(key).equals("time")) {
	 * TimePicker timePicker = new TimePicker(activity);
	 * timePicker.setIs24HourView(false); String fieldName =
	 * fields.get("field_name").toString(); String fieldRequired =
	 * fields.get("field_required").toString();
	 * timePicker.setTag(R.id.field_name_id, fieldName);
	 * timePicker.setTag(R.id.field_required_id, fieldRequired); LayoutParams lp
	 * = new LayoutParams( LayoutParams.WRAP_CONTENT,
	 * LayoutParams.WRAP_CONTENT); lp.setMargins(10, 5, 10, 10);
	 * timePicker.setLayoutParams(lp); TextView tvFieldName = new
	 * TextView(activity); tvFieldName.setText(fieldName); lp = new
	 * LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	 * tvFieldName.setLayoutParams(lp); lL.addView(tvFieldName);
	 * lL.addView(timePicker); }else if(fields.get(key).equals("datetime")){
	 * LinearLayout datetimeLayout = new LinearLayout(activity); LayoutParams
	 * datetimeLayoutParam = new LayoutParams( LayoutParams.WRAP_CONTENT,
	 * LayoutParams.WRAP_CONTENT); datetimeLayoutParam.setMargins(0, 30, 0, 0);
	 * datetimeLayout.setLayoutParams(datetimeLayoutParam);
	 * datetimeLayout.setOrientation(LinearLayout.VERTICAL);
	 * datetimeLayout.setGravity(Gravity.CENTER_HORIZONTAL);
	 * datetimeLayout.setTag("datetimeLayout"); String dateTimeLabel =
	 * fields.get("field_label").toString(); TextView tvLabel = new
	 * TextView(activity); tvLabel.setText(dateTimeLabel); String fieldName =
	 * fields.get("field_name").toString(); String fieldRequired =
	 * fields.get("field_required").toString();
	 * tvLabel.setTag(R.id.field_name_id, fieldName);
	 * tvLabel.setTag(R.id.field_required_id, fieldRequired);
	 * tvLabel.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,
	 * LayoutParams.WRAP_CONTENT)); datetimeLayout.addView(tvLabel);
	 * 
	 * //date DatePicker datePicker = new DatePicker(activity);
	 * datePicker.setCalendarViewShown(false); LayoutParams lp = new
	 * LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	 * lp.setMargins(0, 0, 0, 0); datePicker.setLayoutParams(lp);
	 * datetimeLayout.addView(datePicker);
	 * 
	 * //time TimePicker timePicker = new TimePicker(activity);
	 * timePicker.setIs24HourView(false); lp.setMargins(0, 0, 0, 0);
	 * timePicker.setLayoutParams(lp); datetimeLayout.addView(timePicker);
	 * lL.addView(datetimeLayout); } else if (fields.get(key).equals("options"))
	 * { LinearLayout radioLayout = new LinearLayout(activity); LayoutParams
	 * radioLayoutParams = new LayoutParams( LayoutParams.MATCH_PARENT,
	 * LayoutParams.WRAP_CONTENT); radioLayoutParams.setMargins(0, 25, 0, 25);
	 * radioLayout.setLayoutParams(radioLayoutParams);
	 * radioLayout.setOrientation(LinearLayout.VERTICAL);
	 * radioLayout.setTag("radioLayout"); String radioLabel =
	 * fields.get("field_label") .toString(); TextView tvLabel = new
	 * TextView(activity); tvLabel.setPadding(0, 0, 0, 10);
	 * tvLabel.setText(radioLabel); tvLabel.setTextSize(15); String fieldName =
	 * fields.get("field_name").toString(); String fieldRequired =
	 * fields.get("field_required").toString(); String fieldLabel =
	 * fields.get("field_required").toString();
	 * 
	 * tvLabel.setTag(R.id.field_name_id, fieldName);
	 * tvLabel.setTag(R.id.field_required_id, fieldRequired);
	 * tvLabel.setTag(R.id.field_label_id, fieldLabel);
	 * 
	 * LayoutParams tvLabelParams = new LayoutParams( LayoutParams.WRAP_CONTENT,
	 * LayoutParams.WRAP_CONTENT); tvLabelParams.setMargins(0, 0, 0, 15);
	 * tvLabel.setGravity(Gravity.CENTER_VERTICAL);
	 * tvLabel.setLayoutParams(tvLabelParams); radioLayout.addView(tvLabel);
	 * 
	 * RadioGroup rg = new RadioGroup(activity); JSONArray dataset = (JSONArray)
	 * fields .get("field_dataset"); int default_value =
	 * Integer.parseInt(fields.get( "field_default_value").toString()); int
	 * length = dataset.length(); for (int j = 0; j < length; j++) { JSONObject
	 * obj = new JSONObject(); obj = dataset.getJSONObject(j); String text =
	 * obj.getString("label"); String tag = obj.getString("value"); RadioButton
	 * radioButton = new RadioButton(activity);
	 * radioButton.setId(View.generateViewId()); radioButton.setText(text);
	 * radioButton.setTextSize(13); radioButton.setTag(tag);
	 * LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
	 * RadioGroup.LayoutParams.WRAP_CONTENT,
	 * RadioGroup.LayoutParams.WRAP_CONTENT); layoutParams.setMargins(0, 15, 0,
	 * 15); if (default_value == (j + 1)) { radioButton.setChecked(true); }
	 * rg.addView(radioButton, j, layoutParams); }
	 * radioLayout.setBackgroundColor(Color.parseColor("#000000"));
	 * radioLayout.addView(rg); lL.addView(radioLayout); } else if
	 * (fields.get(key).equals("checklist")) { LinearLayout checkBoxLayout = new
	 * LinearLayout(activity); LayoutParams cbLayoutParams = new LayoutParams(
	 * LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	 * cbLayoutParams.setMargins(0, 25, 0, 25);
	 * checkBoxLayout.setLayoutParams(cbLayoutParams);
	 * checkBoxLayout.setOrientation(LinearLayout.VERTICAL);
	 * checkBoxLayout.setTag("checkBoxLayout"); String checkLabel =
	 * fields.get("field_label") .toString(); TextView tvLabel = new
	 * TextView(activity); tvLabel.setPadding(0, 0, 0, 10);
	 * tvLabel.setText(checkLabel + " "); tvLabel.setTextSize(15); String
	 * fieldName = fields.get("field_name").toString(); String fieldRequired =
	 * fields.get("field_required").toString(); String fieldLabel =
	 * fields.get("field_label").toString(); tvLabel.setTag("label");
	 * tvLabel.setTag(R.id.field_name_id, fieldName);
	 * tvLabel.setTag(R.id.field_required_id, fieldRequired);
	 * tvLabel.setTag(R.id.field_label_id, fieldLabel); LayoutParams
	 * tvLabelParams = new LayoutParams( LayoutParams.WRAP_CONTENT,
	 * LayoutParams.WRAP_CONTENT); tvLabelParams.setMargins(0, 0, 0, 15);
	 * tvLabel.setGravity(Gravity.CENTER_VERTICAL);
	 * tvLabel.setLayoutParams(tvLabelParams); checkBoxLayout.addView(tvLabel);
	 * 
	 * JSONArray dataset = (JSONArray) fields .get("field_dataset"); int
	 * default_value = Integer.parseInt(fields.get(
	 * "field_default_value").toString()); int length = dataset.length(); for
	 * (int j = 0; j < length; j++) { JSONObject obj = new JSONObject(); obj =
	 * dataset.getJSONObject(j); String text = obj.getString("label"); String
	 * tag = obj.getString("value"); CheckBox cb = new CheckBox(activity);
	 * cb.setText(text); cb.setTextSize(13); cb.setTag(tag); LayoutParams
	 * cbParams = new LayoutParams( LayoutParams.WRAP_CONTENT,
	 * LayoutParams.WRAP_CONTENT); cbParams.setMargins(0, 15, 0, 15);
	 * cb.setLayoutParams(cbParams); if (default_value == (j + 1)) {
	 * cb.setChecked(true); } checkBoxLayout.addView(cb); } // error msg
	 * TextView errorMsg = new TextView(activity); errorMsg.setLayoutParams(new
	 * LayoutParams( LayoutParams.WRAP_CONTENT, 0)); errorMsg.setTextSize(12);
	 * errorMsg.setTag("errorMsg"); checkBoxLayout.addView(errorMsg);
	 * checkBoxLayout.setBackgroundColor(Color.BLACK);
	 * lL.addView(checkBoxLayout); } else if
	 * (fields.get(key).equals("location")) { final LinearLayout locationLayout
	 * = new LinearLayout(activity); LayoutParams locationLayoutParams = new
	 * LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	 * locationLayout.setGravity(Gravity.CENTER_HORIZONTAL);
	 * locationLayoutParams.setMargins(0, 20, 0, 0);
	 * locationLayout.setLayoutParams(locationLayoutParams);
	 * locationLayout.setOrientation(LinearLayout.VERTICAL);
	 * locationLayout.setPadding(0, 10, 0, 0);
	 * locationLayout.setTag("locationLayout"); String field_label =
	 * fields.get("field_label") .toString(); String field_name =
	 * fields.get("field_name").toString(); String field_required =
	 * fields.get("field_required").toString(); TextView tvLabel = new
	 * TextView(activity); tvLabel.setText(field_label + " : ");
	 * tvLabel.setTag("label"); tvLabel.setTag(R.id.field_name_id, field_name);
	 * tvLabel.setTag(R.id.field_required_id, field_required);
	 * tvLabel.setTag(R.id.field_label_id, field_label); tvLabel.setPadding(0,
	 * 0, 0, 10); // tvLabel.setTag(fields.get("field_name"));
	 * tvLabel.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,
	 * LayoutParams.WRAP_CONTENT)); locationLayout.addView(tvLabel);
	 * 
	 * LayoutParams editTextLayoutParam = new
	 * LayoutParams(LayoutParams.MATCH_PARENT, 50);
	 * editTextLayoutParam.setMargins(0, 5, 0, 5); final EditText edtLat = new
	 * EditText(activity); edtLat.setEnabled(false); edtLat.setHint("Latitude");
	 * edtLat.setPadding(5, 0, 0, 0);
	 * edtLat.setLayoutParams(editTextLayoutParam); // final EditText edtLong =
	 * new EditText(activity); edtLong.setEnabled(false);
	 * edtLong.setHint("Longitude"); edtLong.setPadding(5, 0, 0, 0);
	 * edtLat.setLayoutParams(editTextLayoutParam); //
	 * locationLayout.addView(edtLat); locationLayout.addView(edtLong);
	 * 
	 * // error msg final TextView errorMsg = new TextView(activity);
	 * errorMsg.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,
	 * 0)); errorMsg.setTextSize(12); errorMsg.setTag("errorMsg");
	 * locationLayout.addView(errorMsg);
	 * 
	 * Button btnGPS = new Button(activity); btnGPS.setLayoutParams(new
	 * LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	 * btnGPS.setText("Get Location");
	 * btnGPS.setBackgroundResource(R.drawable.button_border);
	 * 
	 * btnGPS.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { if (gps.canGetLocation()) { //
	 * double latitude = gps.getLatitude(); // double longitude =
	 * gps.getLongitude(); double[] latAndlong = gps.getGPS(); double latitude =
	 * latAndlong[0]; double longitude = latAndlong[1]; edtLat.setTag(latitude);
	 * edtLong.setTag(longitude); edtLat.setText(latitude+"");
	 * edtLong.setText(longitude+""); } else { errorMsg.setText("No GPS"); }
	 * 
	 * } }); locationLayout.addView(btnGPS);
	 * 
	 * lL.addView(locationLayout); } else if (fields.get(key).equals("drawing"))
	 * { final LinearLayout drawingLayout = new LinearLayout(activity);
	 * LayoutParams locationLayoutParams = new LayoutParams(
	 * LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	 * drawingLayout.setGravity(Gravity.CENTER_HORIZONTAL);
	 * locationLayoutParams.setMargins(0, 20, 0, 0);
	 * drawingLayout.setLayoutParams(locationLayoutParams);
	 * drawingLayout.setOrientation(LinearLayout.VERTICAL);
	 * drawingLayout.setPadding(0, 10, 0, 0);
	 * drawingLayout.setTag("drawingLayout"); String field_label =
	 * fields.get("field_label") .toString(); String field_name =
	 * fields.get("field_name").toString(); String field_required =
	 * fields.get("field_required").toString(); TextView tvLabel = new
	 * TextView(activity); tvLabel.setText(field_label + " : ");
	 * 
	 * tvLabel.setTag("label"); tvLabel.setTag(R.id.field_name_id, field_name);
	 * tvLabel.setTag(R.id.field_required_id, field_required);
	 * tvLabel.setTag(R.id.field_label_id, field_label); tvLabel.setPadding(0,
	 * 0, 0, 10); // tvLabel.setTag(fields.get("field_name"));
	 * tvLabel.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,
	 * LayoutParams.WRAP_CONTENT)); drawingLayout.addView(tvLabel);
	 * 
	 * ImageView imgView = new ImageView(activity.getBaseContext()); final int
	 * randomID = randInt(1, 10000); imgView.setId(randomID);
	 * imgView.setLayoutParams(new LayoutParams(100,100));
	 * imgView.setBackgroundColor(Color.parseColor("#abcdef"));
	 * drawingLayout.addView(imgView);
	 * 
	 * Button button = new Button(activity); button.setLayoutParams(new
	 * LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	 * button.setText("Add"); final String field_help =
	 * fields.get("field_help").toString(); final String field_name_ =
	 * fields.get("field_name").toString(); final String field_type_ =
	 * fields.get("field_type").toString(); button.setOnClickListener(new
	 * OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { Intent i = new Intent(activity,
	 * DrawSignature.class); i.putExtra("field_help", field_help);
	 * i.putExtra("field_name", field_name_); i.putExtra("field_type",
	 * field_type_); i.putExtra("view_id", Integer.toString(randomID));
	 * activity.startActivityForResult(i, ApplicationValues.REQUEST_DRAWING); }
	 * }); drawingLayout.addView(button);
	 * 
	 * // error msg TextView errorMsg = new TextView(activity);
	 * errorMsg.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,
	 * 0)); errorMsg.setTextSize(12); errorMsg.setTag("errorMsg");
	 * drawingLayout.addView(errorMsg); lL.addView(drawingLayout); } else if
	 * (fields.get(key).equals("photo")) { final LinearLayout photoLayout = new
	 * LinearLayout(activity); LayoutParams locationLayoutParams = new
	 * LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	 * photoLayout.setGravity(Gravity.CENTER_HORIZONTAL);
	 * locationLayoutParams.setMargins(0, 20, 0, 0);
	 * photoLayout.setLayoutParams(locationLayoutParams);
	 * photoLayout.setOrientation(LinearLayout.VERTICAL);
	 * photoLayout.setPadding(0, 10, 0, 0); photoLayout.setTag("photoLayout");
	 * String field_label = fields.get("field_label") .toString(); String
	 * field_name = fields.get("field_name").toString(); String field_required =
	 * fields.get("field_required").toString(); TextView tvLabel = new
	 * TextView(activity); tvLabel.setText(field_label);
	 * 
	 * tvLabel.setTag("label"); tvLabel.setTag(R.id.field_name_id, field_name);
	 * tvLabel.setTag(R.id.field_required_id, field_required);
	 * tvLabel.setTag(R.id.field_label_id, field_label); tvLabel.setPadding(0,
	 * 0, 0, 10); // tvLabel.setTag(fields.get("field_name"));
	 * tvLabel.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,
	 * LayoutParams.WRAP_CONTENT)); photoLayout.addView(tvLabel);
	 * 
	 * ImageView photoPreView = new ImageView(activity.getBaseContext()); final
	 * int randomID = randInt(1, 10000); photoPreView.setId(randomID);
	 * photoPreView.setLayoutParams(new LayoutParams(200,200));
	 * photoLayout.addView(photoPreView);
	 * 
	 * Button button = new Button(activity); button.setLayoutParams(new
	 * LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	 * button.setText("Select Photo"); final String field_help_ =
	 * fields.get("field_help").toString(); final String field_name_ =
	 * fields.get("field_name").toString(); final String field_type_ =
	 * fields.get("field_type").toString(); final String view_id_ =
	 * Integer.toString(randomID); button.setOnClickListener(new
	 * OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { selectImage(field_name_,
	 * field_type_, field_help_, view_id_); } }); photoLayout.addView(button);
	 * 
	 * // error msg TextView errorMsg = new TextView(activity);
	 * errorMsg.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,
	 * 0)); errorMsg.setTextSize(12); errorMsg.setTag("errorMsg");
	 * photoLayout.addView(errorMsg); lL.addView(photoLayout); } } } }
	 * 
	 * lL.setGravity(Gravity.CENTER_HORIZONTAL); // lL.addView(signatureView);
	 * return lL; }
	 */

	public ArrayList<LinearLayout> readForm2(Form form) throws JSONException {

		ArrayList<LinearLayout> layoutList = new ArrayList<LinearLayout>();

		ArrayList<HashMap<String, Object>> formFields = getFormFields(form
				.getForm_json());

		int upLayoutHeight = displayManager.getHeigth(10);
		int LayoutWidth = displayManager.getWidth(95);
		int LayoutHeight = displayManager.getHeigth(45);
		int editTextLayoutHeight = displayManager.getHeigth(6);

		// params
		LayoutParams labelLayoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		labelLayoutParams.setMargins(10, 30, 10, 30);

		LayoutParams innerLayoutParams = new LayoutParams(LayoutWidth,
				LayoutParams.WRAP_CONTENT);

		LayoutParams editTextLayoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, editTextLayoutHeight);
		editTextLayoutParams.setMargins(15, 10, 15, 10);

		LayoutParams errorMsgLayoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, 150);
		errorMsgLayoutParams.setMargins(10, 10, 10, 0);
		// -------------------------------------------------------------

		for (int i = 0; i < formFields.size(); i++) {
			HashMap<String, Object> fields = formFields.get(i);
			for (Object key : fields.keySet()) {
				if (key.equals("field_type")) {
					if (fields.get(key).equals("text")) {
						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);
						RelativeLayout upLayout = new RelativeLayout(activity);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, upLayoutHeight);
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
						parentLayout.addView(upLayout);

						LinearLayout textLayout = new LinearLayout(activity);
						textLayout.setLayoutParams(innerLayoutParams);
						textLayout
								.setBackgroundResource(R.drawable.linear_layout_ui);
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
						String textLabel = fields.get("field_label").toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setPadding(10, 0, 0, 0);
						tvLabel.setTag("label");
						tvLabel.setText(textLabel);
						tvLabel.setLayoutParams(labelLayoutParams);
						textLayout.addView(tvLabel);

						EditText ed1 = new EditText(activity);
						ed1.setTextSize(12);
						ed1.setTypeface(typeface.getMyanmar3TypeFace());
						String fieldName = fields.get("field_name").toString();
						String fieldHelp = "-";
						if (fields.containsKey("field_help"))
							fieldHelp = fields.get("field_help").toString();

						ed1.setGravity(Gravity.LEFT);
						ed1.setSingleLine(true);
						ed1.setHint(fieldHelp);
						ed1.setBackgroundResource(R.drawable.edittext_style);
						ed1.setLayoutParams(editTextLayoutParams);
						textLayout.addView(ed1);

						// error msg
						TextView errorMsg = new TextView(activity);
						errorMsg.setLayoutParams(errorMsgLayoutParams);
						errorMsg.setGravity(Gravity.CENTER_VERTICAL);
						errorMsg.setTextSize(12);
						errorMsg.setTag("errorMsg");
						textLayout.addView(errorMsg);
						parentLayout.addView(textLayout);
						layoutList.add(parentLayout);
					}
					if (fields.get(key).equals("number")) {
						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);

						RelativeLayout upLayout = new RelativeLayout(activity);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, upLayoutHeight);
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
						parentLayout.addView(upLayout);

						LinearLayout numberLayout = new LinearLayout(activity);
						numberLayout.setLayoutParams(innerLayoutParams);
						numberLayout
								.setBackgroundResource(R.drawable.linear_layout_ui);
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
						String textLabel = fields.get("field_label").toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setText(textLabel);
						tvLabel.setTag("label");
						tvLabel.setTag(R.id.field_name_id,
								fields.get("field_name"));
						tvLabel.setTag(R.id.field_required_id,
								isFieldRequired(fields.get("field_required")));
						tvLabel.setTag(R.id.field_label_id, textLabel);
						tvLabel.setLayoutParams(labelLayoutParams);
						numberLayout.addView(tvLabel);

						EditText ed1 = new EditText(activity);
						ed1.setTextSize(12);
						ed1.setTypeface(typeface.getMyanmar3TypeFace());
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
						// ed1.setEms(50);
						ed1.setInputType(InputType.TYPE_CLASS_NUMBER);
						ed1.setKeyListener(DigitsKeyListener
								.getInstance("0123456789."));
						numberLayout.addView(ed1);

						// error msg
						TextView errorMsg = new TextView(activity);
						errorMsg.setLayoutParams(errorMsgLayoutParams);
						errorMsg.setTextSize(12);
						errorMsg.setTag("errorMsg");
						numberLayout.addView(errorMsg);
						parentLayout.addView(numberLayout);
						layoutList.add(parentLayout);
					} else if (fields.get(key).equals("datetime")) {
						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);
						RelativeLayout upLayout = new RelativeLayout(activity);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, upLayoutHeight);
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
						parentLayout.addView(upLayout);

						LinearLayout datetimeLayout = new LinearLayout(activity);
						// LayoutParams datetimeLayoutParam = new LayoutParams(
						// LayoutParams.WRAP_CONTENT,
						// LayoutParams.WRAP_CONTENT);
						// datetimeLayoutParam.setMargins(0, 30, 0, 0);
						// datetimeLayout.setLayoutParams(datetimeLayoutParam);
						datetimeLayout.setLayoutParams(innerLayoutParams);
						datetimeLayout
								.setBackgroundResource(R.drawable.linear_layout_ui);
						datetimeLayout.setBackgroundColor(Color
								.parseColor("#5E5C5E"));
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
						TextView tvLabel = new TextView(activity);
						tvLabel.setText(dateTimeLabel);
						String fieldName = fields.get("field_name").toString();
						String fieldRequired = fields.get("field_required")
								.toString();
						tvLabel.setTag(R.id.field_name_id, fieldName);
						tvLabel.setTag(R.id.field_required_id, fieldRequired);
						tvLabel.setLayoutParams(labelLayoutParams);
						datetimeLayout.addView(tvLabel);

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
						parentLayout.addView(datetimeLayout);
						layoutList.add(parentLayout);
					} else if (fields.get(key).equals("options")) {
						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);
						RelativeLayout upLayout = new RelativeLayout(activity);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, upLayoutHeight);
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
						parentLayout.addView(upLayout);

						LinearLayout radioLayout = new LinearLayout(activity);
						// LayoutParams radioLayoutParams = new LayoutParams(
						// LayoutParams.MATCH_PARENT,
						// LayoutParams.WRAP_CONTENT);
						// radioLayoutParams.setMargins(0, 25, 0, 25);
						// radioLayout.setLayoutParams(radioLayoutParams);
						radioLayout.setLayoutParams(innerLayoutParams);
						radioLayout
								.setBackgroundResource(R.drawable.linear_layout_ui);
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
						String radioLabel = fields.get("field_label")
								.toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setPadding(0, 0, 0, 10);
						tvLabel.setText(radioLabel);
						tvLabel.setTextSize(15);
						String fieldName = fields.get("field_name").toString();
						String fieldRequired = fields.get("field_required")
								.toString();
						String fieldLabel = fields.get("field_required")
								.toString();

						tvLabel.setTag(R.id.field_name_id, fieldName);
						tvLabel.setTag(R.id.field_required_id, fieldRequired);
						tvLabel.setTag(R.id.field_label_id, fieldLabel);
						tvLabel.setGravity(Gravity.CENTER_VERTICAL);
						tvLabel.setLayoutParams(labelLayoutParams);
						radioLayout.addView(tvLabel);

						RadioGroup rg = new RadioGroup(activity);
						LayoutParams radioGroupParams = new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT);
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
							RadioButton radioButton = new RadioButton(activity);
							radioButton.setId(View.generateViewId());
							radioButton.setText(text);
							radioButton.setTextSize(13);
							radioButton.setTag(R.id.radio_value, tag);
							radioButton.setTag(R.id.field_skip, skip);
							LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
									RadioGroup.LayoutParams.MATCH_PARENT,
									RadioGroup.LayoutParams.WRAP_CONTENT);
							layoutParams.setMargins(0, 15, 0, 15);
							if (default_value == (j + 1)) {
								radioButton.setChecked(true);
							}
							rg.addView(radioButton, j, layoutParams);
						}
						radioLayout.addView(rg);
						radioLayout.setGravity(Gravity.CENTER);
						parentLayout.addView(radioLayout);
						layoutList.add(parentLayout);
					} else if (fields.get(key).equals("checklist")) {
						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);
						RelativeLayout upLayout = new RelativeLayout(activity);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, upLayoutHeight);
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
						parentLayout.addView(upLayout);

						LinearLayout checkBoxLayout = new LinearLayout(activity);
						// LayoutParams cbLayoutParams = new LayoutParams(
						// LayoutParams.MATCH_PARENT,
						// LayoutParams.WRAP_CONTENT);
						// cbLayoutParams.setMargins(0, 25, 0, 25);
						// checkBoxLayout.setLayoutParams(cbLayoutParams);
						checkBoxLayout.setLayoutParams(innerLayoutParams);
						checkBoxLayout
								.setBackgroundResource(R.drawable.linear_layout_ui);
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
						String checkLabel = fields.get("field_label")
								.toString();
						TextView tvLabel = new TextView(activity);
						tvLabel.setPadding(0, 0, 0, 10);
						tvLabel.setText(checkLabel + " ");
						tvLabel.setTextSize(15);
						String fieldName = fields.get("field_name").toString();
						String fieldRequired = fields.get("field_required")
								.toString();
						String fieldLabel = fields.get("field_label")
								.toString();
						tvLabel.setTag("label");
						tvLabel.setTag(R.id.field_name_id, fieldName);
						tvLabel.setTag(R.id.field_required_id, fieldRequired);
						tvLabel.setTag(R.id.field_label_id, fieldLabel);
						tvLabel.setGravity(Gravity.CENTER_VERTICAL);
						tvLabel.setLayoutParams(labelLayoutParams);
						checkBoxLayout.addView(tvLabel);

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
							CheckBox cb = new CheckBox(activity);
							cb.setText(text);
							cb.setTextSize(13);
							cb.setTag(tag);
							LayoutParams cbParams = new LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT);
							cbParams.setMargins(0, 15, 0, 15);
							cb.setLayoutParams(cbParams);
							if (default_value == (j + 1)) {
								cb.setChecked(true);
							}
							checkBoxLayout.addView(cb);
						}
						// error msg
						TextView errorMsg = new TextView(activity);
						errorMsg.setLayoutParams(errorMsgLayoutParams);
						errorMsg.setTextSize(12);
						errorMsg.setTag("errorMsg");
						checkBoxLayout.setGravity(Gravity.CENTER);
						checkBoxLayout.addView(errorMsg);
						parentLayout.addView(checkBoxLayout);
						layoutList.add(parentLayout);
					} else if (fields.get(key).equals("location")) {
						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);
						RelativeLayout upLayout = new RelativeLayout(activity);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, upLayoutHeight);
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
						locationLayout
								.setBackgroundResource(R.drawable.linear_layout_ui);
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
						tvLabel.setPadding(0, 0, 0, 10);
						// tvLabel.setTag(fields.get("field_name"));
						tvLabel.setLayoutParams(labelLayoutParams);
						locationLayout.addView(tvLabel);

						LayoutParams editTextLayoutParam = new LayoutParams(
								LayoutParams.MATCH_PARENT, 50);
						editTextLayoutParam.setMargins(0, 5, 0, 5);
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
						edtLong.setBackgroundResource(R.drawable.edittext_style);
						locationLayout.addView(edtLat);
						locationLayout.addView(edtLong);
						Button btnGPS = new Button(activity);
						btnGPS.setLayoutParams(new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
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
						locationLayout.addView(btnGPS);
						// error msg
						TextView errorMsg = new TextView(activity);
						errorMsg.setLayoutParams(labelLayoutParams);
						errorMsg.setTextSize(12);
						errorMsg.setTag("errorMsg");
						locationLayout.addView(errorMsg);
						parentLayout.addView(locationLayout);
						layoutList.add(parentLayout);
					} else if (fields.get(key).equals("drawing")) {
						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						int fivePercentWidth = displayManager.getWidth(5);
						parentLayoutParams.setMargins(fivePercentWidth, 0, fivePercentWidth, 0);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);
						RelativeLayout upLayout = new RelativeLayout(activity);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, upLayoutHeight);
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
						parentLayout.addView(upLayout);

						final LinearLayout drawingLayout = new LinearLayout(
								activity);
						LayoutParams drawingLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						drawingLayoutParams.setMargins(5, 20, 5, 0);
						drawingLayout.setLayoutParams(drawingLayoutParams);
						drawingLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						drawingLayout
								.setBackgroundResource(R.drawable.linear_layout_ui);
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
						tvLabel.setPadding(0, 0, 0, 10);
						// tvLabel.setTag(fields.get("field_name"));
						tvLabel.setLayoutParams(labelLayoutParams);
						drawingLayout.addView(tvLabel);

						ImageView imgView = new ImageView(
								activity.getBaseContext());
						final int randomID = randInt(1, 10000);
						imgView.setId(randomID);
						
						int sixtyPercentOfWidth = displayManager.getWidth(60);
						
						imgView.setLayoutParams(new LayoutParams(sixtyPercentOfWidth, sixtyPercentOfWidth));
						//imgView.setBackgroundColor(Color.parseColor("#abcdef"));
						imgView.setBackgroundResource(R.drawable.imageview_rounded);
						drawingLayout.addView(imgView);
						Button button = new Button(activity);
						int sevenPerscentHeight = displayManager.getHeigth(7);
						LayoutParams buttonLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT, sevenPerscentHeight);
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
						errorMsg.setTextSize(12);
						errorMsg.setTag("errorMsg");
						drawingLayout.addView(errorMsg);
						drawingLayout.addView(button);
						parentLayout.addView(drawingLayout);
						layoutList.add(parentLayout);
					} else if (fields.get(key).equals("photo")) {
						LinearLayout parentLayout = new LinearLayout(activity);
						parentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						LayoutParams parentLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						int fivePercentWidth = displayManager.getWidth(5);
						parentLayoutParams.setMargins(fivePercentWidth, 0, fivePercentWidth, 0);
						parentLayout.setLayoutParams(parentLayoutParams);
						parentLayout.setOrientation(LinearLayout.VERTICAL);
						RelativeLayout upLayout = new RelativeLayout(activity);
						int relative_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
						int relative_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
						RelativeLayout.LayoutParams upLayoutParams = new RelativeLayout.LayoutParams(
								relative_MATCH_PARENT, upLayoutHeight);
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
						parentLayout.addView(upLayout);

						final LinearLayout photoLayout = new LinearLayout(
								activity);
						LayoutParams photoLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT);
						photoLayoutParams.setMargins(5, 20, 5, 0);
						photoLayout.setLayoutParams(photoLayoutParams);
						photoLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						photoLayout
								.setBackgroundResource(R.drawable.linear_layout_ui);
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
						tvLabel.setPadding(0, 0, 0, 10);
						// tvLabel.setTag(fields.get("field_name"));
						tvLabel.setLayoutParams(labelLayoutParams);
						photoLayout.addView(tvLabel);

						ImageView photoPreView = new ImageView(
								activity.getBaseContext());
						photoPreView.setBackgroundResource(R.drawable.imageview_rounded);
						final int randomID = randInt(1, 10000);
						photoPreView.setId(randomID);
						int sixtyPercentOfWidth = displayManager.getWidth(60);
						photoPreView
								.setLayoutParams(new LayoutParams(sixtyPercentOfWidth, sixtyPercentOfWidth));
						photoLayout.addView(photoPreView);

						Button button = new Button(activity);
						int sevenPerscentHeight = displayManager.getHeigth(7);
						LayoutParams buttonLayoutParams = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								sevenPerscentHeight);
						buttonLayoutParams.setMargins(30, 20, 30, 10);
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
						errorMsg.setTextSize(12);
						errorMsg.setTag("errorMsg");
						photoLayout.addView(errorMsg);
						photoLayout.addView(button);
						parentLayout.addView(photoLayout);
						layoutList.add(parentLayout);
					}
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
					String field_desc = jChild.getString("field_desc");
					if (field_desc.length() > 0)
						fields.put("field_desc", field_desc);
					else
						fields.put("field_desc", "-");
					fields.put("field_required",
							jChild.getString("field_required"));
					fields.put("field_default_value",
							jChild.getString("field_default_value"));
				} else if (field_type.equals("options")) {
					// with dataset (options and checklist)
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_help", jChild.getString("field_help"));
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
				} else if (field_type.equals("checklist")) {
					fields.put("field_id", jChild.getString("field_id"));
					fields.put("field_name", jChild.getString("field_name"));
					fields.put("field_type", jChild.getString("field_type"));
					fields.put("field_label", jChild.getString("field_label"));
					fields.put("field_help", jChild.getString("field_help"));
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
					fields.put("field_required",
							jChild.getString("field_required"));
					if (jChild.getString("field_help").length() > 0)
						fields.put("field_help", jChild.getString("field_help"));
					if (jChild.getString("field_default_value").length() > 0)
						fields.put("field_default_value",
								jChild.getString("field_default_value"));
					fields.put("field_label", jChild.getString("field_label"));
					if (jChild.has("field_multiline")
							&& jChild.getString("field_multiline").length() > 0)
						fields.put("field_multiline",
								jChild.getString("field_multiline"));
				}
				fieldList.add(fields);
			}
		} catch (JSONException e) {
			e.printStackTrace();
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
				fields.put("field_name", jChild.getString("field_name"));
				fields.put("field_value", jChild.getString("field_value"));
				fields.put("field_label", jChild.getString("field_label"));
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
					String root = Environment.getExternalStorageDirectory()
							.toString();
					File myDir = new File(root, "/temp");
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
			fieldNode.put("field_label", map.get("field_label"));
			fieldNode.put("field_name", map.get("field_name"));
			fieldNode.put("field_value", map.get("field_value"));
			dataArray.put(fieldNode);
		}

		document_json.put("data", dataArray);
		document_json.put("timestamp", timestamp);
		document_json.put("org", orgChildNode);
		document_json.put("form", formChildNode);
		document_json.put("worker", workerChildNode);

		JSONArray mainArray = new JSONArray();
		mainArray.put(document_json);
		return mainArray;
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

}

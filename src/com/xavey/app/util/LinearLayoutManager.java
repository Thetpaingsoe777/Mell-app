package com.xavey.app.util;

import java.util.HashMap;

import org.json.JSONArray;

import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
<<<<<<< HEAD
=======
import android.widget.LinearLayout.LayoutParams;
>>>>>>> ce4c53483e36d66116a944fa419f4f5c31caf09c
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.xavey.app.R;

public class LinearLayoutManager {

	public TextView getErrorMsgTextView(LinearLayout linearLayout){
		TextView errMsg = null;
		// tag errorMsg
		for(int i=0; i<linearLayout.getChildCount(); i++){
			if(isViewErrorMsg(linearLayout.getChildAt(i))){
				errMsg = (TextView) linearLayout.getChildAt(i);
				
			}
		}
		return errMsg;
	}
	
	public String getFieldNameFromLayout(LinearLayout linearLayout){
		LinearLayout targetLayout = null;
		for(int i=0; i<linearLayout.getChildCount();i++){
			View view = linearLayout.getChildAt(i);
			if(view.getClass().getName().equals("android.widget.LinearLayout") && view.getTag(R.id.layout_id)!=null){
				targetLayout = (LinearLayout) view;
			}
		}
		if(targetLayout!=null){
			if(targetLayout.getTag(R.id.field_name_id)!=null)
				return targetLayout.getTag(R.id.field_name_id).toString();
			else
				return "noTag";
		}
		else
			return "noTag";
			
	}

	public boolean isViewErrorMsg(View view){
		if(view.getClass().getName().equals("android.widget.TextView")){
			Object tag = view.getTag();
			if(view.getTag() != null && view.getTag().toString().equals("errorMsg"))
				return true;
			else
				return false;
		}
		return false;
	}

	public HashMap<String, String> test(LinearLayout linearLayout){
		HashMap<String, String> map = new HashMap<String, String>();
		String layoutID = linearLayout.getTag(R.id.layout_id).toString();
		String fieldName = linearLayout.getTag(R.id.field_name_id).toString();
		String fieldRequired = linearLayout.getTag(R.id.field_required_id).toString();
		String fieldLabel = linearLayout.getTag(R.id.field_label_id).toString();

		String userTypedValue = "";
		if(layoutID.equals("textLayout")){
			for(int i=0; i<linearLayout.getChildCount(); i++){
				String className = linearLayout.getChildAt(i).getClass().getName().toString();
				if(className.equals("android.widget.EditText")){
					EditText textOrNumber = (EditText) linearLayout.getChildAt(i);
					userTypedValue = textOrNumber.getText().toString();
					if(userTypedValue.length()>0)
						map.put("value", userTypedValue);
					else
						map.put("value", "#no_value#");
				}
			}
		}
		else if(layoutID.equals("numberLayout")){
			for(int i=0; i<linearLayout.getChildCount(); i++){
				String className = linearLayout.getChildAt(i).getClass().getName().toString();
				if(className.equals("android.widget.EditText")){
					EditText textOrNumber = (EditText) linearLayout.getChildAt(i);
					userTypedValue = textOrNumber.getText().toString();
					if(userTypedValue.length()>0)
						map.put("value", userTypedValue);
					else
						map.put("value", "#no_value#");
				}
			}
			String maxValue = linearLayout.getTag(R.id.field_max_value).toString();
			String minValue = linearLayout.getTag(R.id.field_min_value).toString();
			String errorMsg = linearLayout.getTag(R.id.field_err_msg).toString();
			map.put("field_max_value", maxValue);
			map.put("field_min_value", minValue);
			map.put("field_err_msg", errorMsg);
		}
		else if(layoutID.equals("checkBoxLayout")){
			boolean isChecked = false;
			for(int i=0; i<linearLayout.getChildCount(); i++){
				String className = linearLayout.getChildAt(i).getClass().getName();
				if(className.equals("android.widget.CheckBox")){
					CheckBox checkBox = (CheckBox) linearLayout.getChildAt(i);
					if(checkBox.isChecked())
						isChecked = isChecked || true;
				}
			}
			if(isChecked)
				map.put("value", "checked");
			else
				map.put("value", "#no_value#");
		}
		else if(layoutID.equals("drawingLayout")||layoutID.equals("photoLayout")){
			for(int i=0; i<linearLayout.getChildCount(); i++){
				String className = linearLayout.getChildAt(i).getClass().getName();
				if(className.equals("android.widget.ImageView")){
					ImageView imgPreview = (ImageView) linearLayout.getChildAt(i);
					if(imgPreview.getDrawable()!=null){
						map.put("value", "hasImage");
					}
					else{
						map.put("value", "#no_value#");
					}
				}
			}
		}
		else if(layoutID.equals("locationLayout")){
			String latitude = "";
			String longitude = "";
			for(int i=0; i<linearLayout.getChildCount(); i++){
				String className = linearLayout.getChildAt(i).getClass().getName();
				if (className.equals(
						"android.widget.EditText")) {
					EditText location = (EditText) linearLayout
							.getChildAt(i);
					if (location.getHint().equals("Latitude")) {
						if (location.getText().toString().length() > 0)
							latitude = location.getText().toString();
					} else {
						if (location.getText().toString().length() > 0)
							longitude = location.getText().toString();
					}
				}
			}
			if(latitude.length()>0 || longitude.length()>0)
				map.put("value", "value");
			else
				map.put("value", "#no_value#");
		}
		else{
			//for others field rather number and text
		}
		map.put("field_name", fieldName);
		map.put("field_label", fieldLabel);
		map.put("field_required", fieldRequired);
		return map;
	}
	
	public HashMap<String, Object> getValueFromLinearLayout(
			LinearLayout linearLayout) {
		HashMap<String, Object> map = new HashMap<String, Object>();
			if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("textLayout")) {
				String key = "";
				String value = "";
				EditText edt1 = null;
				String field_label="";

				for (int j = 0; j < linearLayout.getChildCount(); j++) {
					Class<?> subClass = (Class<?>) linearLayout.getChildAt(j)
							.getClass();
					if (subClass.getName().equals("android.widget.TextView")) {
						TextView textView = (TextView) linearLayout
								.getChildAt(j);
						// following if else is just to categorize the
						// textView
						// if that's label or error message
						if (!textView.getTag().toString().equals("errorMsg")) {
							// this is label
							key = textView.getTag(R.id.field_name_id)
									.toString();
							field_label = textView.getTag(R.id.field_label_id)
									.toString();
						}
					} else if (subClass.getName().equals(
							"android.widget.EditText")) {
						edt1 = (EditText) linearLayout.getChildAt(j);
						value = edt1.getText().toString();
					}
				}
				map.put(key, value);
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("numberLayout")) {
				String key = "";
				String value = "";
				EditText edt1 = null;
				String field_label;

				for (int j = 0; j < linearLayout.getChildCount(); j++) {
					Class<?> subClass = (Class<?>) linearLayout.getChildAt(j)
							.getClass();
					if (subClass.getName().equals("android.widget.TextView")) {
						TextView textView = (TextView) linearLayout
								.getChildAt(j);
						// following if else is just to categorize the
						// textView
						// if that's label or error message
						if (!textView.getTag().toString().equals("errorMsg")) {
							// this is label
							key = textView.getTag(R.id.field_name_id)
									.toString();
							field_label = textView.getTag(R.id.field_label_id)
									.toString();
						}
					} else if (subClass.getName().equals(
							"android.widget.EditText")) {
						edt1 = (EditText) linearLayout.getChildAt(j);
						value = edt1.getText().toString();
					}
				}
				map.put(key, value);
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("radioLayout")) {
				String key = "";
				for (int y = 0; y < linearLayout.getChildCount(); y++) {

					Class<?> subClass = (Class<?>) linearLayout.getChildAt(y)
							.getClass();

					if (subClass.getName().equals("android.widget.TextView")) {
						TextView label = (TextView) linearLayout.getChildAt(y);
						key = label.getTag(R.id.field_name_id).toString();
					} else if (subClass.getName().equals(
							"android.widget.RadioGroup")) {
						// radio
						RadioGroup radioGroup = (RadioGroup) linearLayout
								.getChildAt(y);
						int selectedID = radioGroup.getCheckedRadioButtonId();
						RadioButton selectedButton = (RadioButton) radioGroup
								.findViewById(selectedID);
						String value = selectedButton.getTag().toString();
						map.put(key, value);
					}
				}
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("checkBoxLayout")) {
				JSONArray checkedValues = new JSONArray();
				String key = "";
				TextView label = null;
				String field_label = "";

				for (int z = 0; z < linearLayout.getChildCount(); z++) {
					Class<?> subClass = (Class<?>) linearLayout.getChildAt(z)
							.getClass();
					if (subClass.getName().equals("android.widget.TextView")) {
						TextView textView = (TextView) linearLayout
								.getChildAt(z);

						// following if else is just to categorize the
						// textView
						// if that's label or error message
						if (!textView.getTag().toString().equals("errorMsg")) {
							key = textView.getTag(R.id.field_name_id)
									.toString();
							field_label = textView.getTag(R.id.field_label_id)
									.toString();
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
				map.put(key, checkedValues);
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("locationLayout")) {

				String key = "";
				double latitude = 0.0;
				double longitude = 0.0;

				TextView label = null;
				String field_label = "";

				for (int j = 0; j < linearLayout.getChildCount(); j++) {
					Class<?> subClass = (Class<?>) linearLayout.getChildAt(j)
							.getClass();
					if (subClass.getName().equals("android.widget.TextView")) {
						TextView textView = (TextView) linearLayout
								.getChildAt(j);
						if (!textView.getTag().toString().equals("errorMsg")) {
							key = textView.getTag(R.id.field_name_id)
									.toString();
							field_label = textView.getTag(R.id.field_label_id)
									.toString();
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
				map.put(key, latitude + " , " + longitude);
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("datetimeLayout")) {
				String key = "";
				String time = "";
				String date = "";
				for (int j = 0; j < linearLayout.getChildCount(); j++) {
					Class<?> subClass = (Class<?>) linearLayout.getChildAt(j)
							.getClass();
					if (subClass.getName().equals("android.widget.TextView")) {
						TextView label = (TextView) linearLayout.getChildAt(j);
						key = label.getTag(R.id.field_name_id).toString();
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
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("drawingLayout")) {
				String key = "";
				ImageView drawingPreview = null;
				TextView label = null;
				String field_label = "";

				for (int j = 0; j < linearLayout.getChildCount(); j++) {
					Class<?> subClass = (Class<?>) linearLayout.getChildAt(j)
							.getClass();
					if (subClass.getName().equals("android.widget.TextView")) {
						TextView textView = (TextView) linearLayout
								.getChildAt(j);

						if (!textView.getTag().toString().equals("errorMsg")) {
							key = textView.getTag(R.id.field_name_id)
									.toString();
							field_label = textView.getTag(R.id.field_label_id)
									.toString();
						}
					} else if (subClass.getName().equals(
							"android.widget.ImageView")) {

						// ဒီ method ထဲမှာဘာမှမလုပ်ဘူး... ဒီ small imageview က
						// preview ပဲပြထားတာ... တကယ့် path က သယ်လာပြီးသား....
						// validation ပဲလုပ်တာ..

						drawingPreview = (ImageView) linearLayout.getChildAt(j);
						// to check image involve or not
						/*
						 * Drawable d = drawingPreview.getDrawable(); if (d ==
						 * null) { Toast.makeText(getApplicationContext(),
						 * "no image", 1000).show(); } else
						 * Toast.makeText(getApplicationContext(),
						 * "image include", 1000).show();
						 */
					}
				}
				map.put(key, "unavailable");
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("photoLayout")) {
				String key = "";
				ImageView drawingPreview = null;
				TextView label = null;
				String field_label = "";

				for (int j = 0; j < linearLayout.getChildCount(); j++) {
					Class<?> subClass = (Class<?>) linearLayout.getChildAt(j)
							.getClass();
					if (subClass.getName().equals("android.widget.TextView")) {
						TextView textView = (TextView) linearLayout
								.getChildAt(j);

						// following if else is just to categorize the
						// textView
						// if that's label or error message
						if (!textView.getTag().toString().equals("errorMsg")) {
							// this is label
							key = textView.getTag(R.id.field_name_id)
									.toString();
							field_label = textView.getTag(R.id.field_label_id)
									.toString();
						}
					} else if (subClass.getName().equals(
							"android.widget.ImageView")) {
						drawingPreview = (ImageView) linearLayout.getChildAt(j);
					}
				}
				map.put(key, "unavailable");
			}
		return map;
	}

}

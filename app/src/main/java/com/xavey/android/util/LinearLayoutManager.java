package com.xavey.android.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.xavey.android.R;
import com.xavey.android.layout.CheckboxLayout;
import com.xavey.android.layout.RadioGroupLayout;

public class LinearLayoutManager {

	XaveyUtils xaveyUtils = new XaveyUtils(null);

	public TextView getErrorMsgTextView(LinearLayout linearLayout) {
		TextView errMsg = null;
		// tag errorMsg
		for (int i = 0; i < linearLayout.getChildCount(); i++) {
			if (isViewErrorMsg(linearLayout.getChildAt(i))) {
				errMsg = (TextView) linearLayout.getChildAt(i);
			}
		}
		return errMsg;
	}

	public static CheckBox getCheckBoxFromCheckBoxLine(LinearLayout checkBoxLine) {
		CheckBox cb = null;
		for (int i = 0; i < checkBoxLine.getChildCount(); i++) {
			if (checkBoxLine.getChildAt(i).getClass().getName()
					.equals("android.widget.CheckBox")) {
				cb = (CheckBox) checkBoxLine.getChildAt(i);
				break;
			}
		}
		return cb;
	}

	public static boolean isViewInvolvedID(View v, int id) { // R.id.
		if (v.getTag(id) != null)
			return true;

		return false;
	}

	public String getFieldIDFromLayout(LinearLayout linearLayout) {
		LinearLayout targetLayout = null;
		for (int i = 0; i < linearLayout.getChildCount(); i++) {
			View view = linearLayout.getChildAt(i);

			String className = view.getClass().getName();
			if (className.equals("android.widget.ScrollView")) {
				ScrollView scrollView = (ScrollView) view;
				for (int z = 0; z < scrollView.getChildCount(); z++) {
					View scrollViewChild = scrollView.getChildAt(z);
					String svChildClassName = scrollViewChild.getClass()
							.getName();
					if (svChildClassName.equals("android.widget.LinearLayout")
							&& scrollViewChild.getTag(R.id.layout_id) != null) {
						if (!scrollViewChild.getTag(R.id.layout_id).toString()
								.equals("recordingLayout"))
							targetLayout = (LinearLayout) scrollViewChild;
					}
				}
			} else if (className.equals("android.widget.LinearLayout")) {
				LinearLayout linearLayout_ = (LinearLayout) view;
				if (view.getTag(R.id.layout_id) != null
						&& !view.getTag(R.id.layout_id).toString()
								.equals("recordingLayout")) {
					targetLayout = linearLayout_;
				}
			}

		}
		if (targetLayout != null) {
			if (targetLayout.getTag(R.id.field_id) != null)
				if (!targetLayout.getTag(R.id.layout_id).toString()
						.equals("noteLayout"))
					return targetLayout.getTag(R.id.field_id).toString();
				else
					return "noTag";
			else
				return "noTag";
		} else
			return "noTag";
	}

	public boolean isViewErrorMsg(View view) {
		if (view.getClass().getName().equals("android.widget.TextView")) {
			Object tag = view.getTag();
			if (view.getTag() != null
					&& view.getTag().toString().equals("errorMsg"))
				return true;
			else
				return false;
		}
		return false;
	}

	public HashMap<String, Object> test(LinearLayout linearLayout)
			throws JSONException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		String layoutID = linearLayout.getTag(R.id.layout_id).toString();
		String fieldName = linearLayout.getTag(R.id.field_name_id).toString();
		String fieldLabel = linearLayout.getTag(R.id.field_label_id).toString();
		String fieldErrorMsg = "";
		if (linearLayout.getTag(R.id.field_err_msg) != null)
			fieldErrorMsg = linearLayout.getTag(R.id.field_err_msg).toString();

		String userTypedValue = "";
		if (layoutID.equals("textLayout")) {
			for (int i = 0; i < linearLayout.getChildCount(); i++) {
				String className = linearLayout.getChildAt(i).getClass()
						.getName().toString();
				if (className.equals("android.widget.EditText")) {
					EditText textOrNumber = (EditText) linearLayout
							.getChildAt(i);
					userTypedValue = textOrNumber.getText().toString();
					if (userTypedValue.length() > 0)
						map.put("value", userTypedValue);
					else
						map.put("value", "#no_value#");
				}
			}
		} else if (layoutID.equals("numberLayout")) {
			for (int i = 0; i < linearLayout.getChildCount(); i++) {
				String className = linearLayout.getChildAt(i).getClass()
						.getName().toString();
				if (className.equals("android.widget.EditText")) {
					EditText textOrNumber = (EditText) linearLayout
							.getChildAt(i);
					userTypedValue = textOrNumber.getText().toString();
					if (userTypedValue.length() > 0)
						map.put("value", userTypedValue);
					else
						map.put("value", "#no_value#");
				}
			}
			String maxValue = linearLayout.getTag(R.id.field_max_value)
					.toString();
			String minValue = linearLayout.getTag(R.id.field_min_value)
					.toString();
			String errorMsg = linearLayout.getTag(R.id.field_err_msg)
					.toString();
			map.put("field_max_value", maxValue);
			map.put("field_min_value", minValue);
			map.put("field_err_msg", errorMsg);
		} else if (layoutID.equals("radioLayout")) {
			boolean isChecked = false;
			for (int i = 0; i < linearLayout.getChildCount(); i++) {
				String className = linearLayout.getChildAt(i).getClass()
						.getName();
				if (className
						.equals("com.xavey.android.layout.RadioGroupLayout")) {
					RadioGroup rg = (RadioGroup) linearLayout.getChildAt(i);
					RadioButton selectedButton = getSelectedRadioButtonMyRadioGroup(rg);
					if (selectedButton != null)
						isChecked = true;
					else
						isChecked = false;
				}
			}
			if (isChecked)
				map.put("value", "checked");
			else
				map.put("value", "#no_value#");
		}

		else if (layoutID.equals("checkBoxLayout")) {
			boolean isChecked = false;
			for (int i = 0; i < linearLayout.getChildCount(); i++) {
				String className = linearLayout.getChildAt(i).getClass()
						.getName();
				if (className.equals("com.xavey.android.layout.CheckboxLayout")) {
					CheckboxLayout checkBoxWrapper = (CheckboxLayout) linearLayout
							.getChildAt(i);
					for (int j = 0; j < checkBoxWrapper.getChildCount(); j++) {
						if (checkBoxWrapper.getChildAt(j).getClass().getName()
								.equals("android.widget.LinearLayout")) {
							LinearLayout llCB = (LinearLayout) checkBoxWrapper
									.getChildAt(j);
							for (int k = 0; k < llCB.getChildCount(); k++) {
								if (llCB.getChildAt(k).getClass().getName()
										.equals("android.widget.CheckBox")) {
									CheckBox checkBox = (CheckBox) llCB.getChildAt(k);
									if (checkBox.isChecked())
										isChecked = isChecked || true;
								}
							}
						}
					}

				}
			}
			if (isChecked)
				map.put("value", "checked");
			else
				map.put("value", "#no_value#");
		} else if (layoutID.equals("drawingLayout")
				|| layoutID.equals("photoLayout")) {
			for (int i = 0; i < linearLayout.getChildCount(); i++) {
				String className = linearLayout.getChildAt(i).getClass()
						.getName();
				if (className.equals("android.widget.ImageView")) {
					ImageView imgPreview = (ImageView) linearLayout
							.getChildAt(i);
					if (imgPreview.getDrawable() != null) {
						map.put("value", "hasImage");
					} else {
						map.put("value", "#no_value#");
					}
				}
			}
		} else if (layoutID.equals("locationLayout")) {
			String latitude = "";
			String longitude = "";
			for (int i = 0; i < linearLayout.getChildCount(); i++) {
				String className = linearLayout.getChildAt(i).getClass()
						.getName();
				if (className.equals("android.widget.EditText")) {
					EditText location = (EditText) linearLayout.getChildAt(i);
					if (location.getHint().equals("Latitude")) {
						if (location.getText().toString().length() > 0)
							latitude = location.getText().toString();
					} else {
						if (location.getText().toString().length() > 0)
							longitude = location.getText().toString();
					}
				}
			}
			if (latitude.length() > 0 || longitude.length() > 0)
				map.put("value", "value");
			else
				map.put("value", "#no_value#");
		} else if (layoutID.equals("textSetLayout")
				|| layoutID.equals("numberSetLayout")) {
			String field_min_value = "";
			String field_max_value = "";
			String field_err_msg = "";
			if (linearLayout.getTag(R.id.field_min_value) != null)
				field_min_value = linearLayout.getTag(R.id.field_min_value)
						.toString();
			if (linearLayout.getTag(R.id.field_max_value) != null)
				field_max_value = linearLayout.getTag(R.id.field_max_value)
						.toString();
			if (linearLayout.getTag(R.id.field_err_msg) != null)
				field_err_msg = linearLayout.getTag(R.id.field_err_msg)
						.toString();
			if (field_min_value.length() > 0) {
				map.put("field_min_value", field_min_value);
			}
			if (field_max_value.length() > 0) {
				map.put("field_max_value", field_max_value);
			}
			if (field_err_msg.length() > 0) {
				map.put("field_err_msg", field_err_msg);
			}

			JSONArray dataValues = (JSONArray) linearLayout
					.getTag(R.id.dataset_values);

			// data is only for validation
			ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

			ArrayList<Integer> userTypedNumberList = new ArrayList<Integer>();

			ArrayList<String> missingLabels = new ArrayList<String>();

			for (int i = 0; i < linearLayout.getChildCount(); i++) {
				String className = linearLayout.getChildAt(i).getClass()
						.getName();
				if (className.equals("android.widget.ListView")) {
					ListView listView = (ListView) linearLayout.getChildAt(i);

					for (int j = 0; j < listView.getChildCount(); j++) {
						LinearLayout lL = (LinearLayout) listView.getChildAt(j);

						for (int k = 0; k < lL.getChildCount(); k++) {
							View kChild = lL.getChildAt(k);
							if (kChild.getClass().getName()
									.equals("android.widget.EditText")) {
								EditText editText = (EditText) kChild;
								String userTypedValue_ = editText.getText()
										.toString();
								String field_value = dataValues
										.getJSONObject(j).getString("value");
								String field_label = dataValues
										.getJSONObject(j).getString("label");
								HashMap<String, String> map_ = new HashMap<String, String>();
								if (userTypedValue_.length() == 0) {
									userTypedValue_ = "#noValue#";
									missingLabels.add(field_label);
								} else {
									map_.put(field_value, userTypedValue_);
									data.add(map_);
								}

								if (!userTypedValue_.equals("#noValue#")) {
									if (layoutID.equals("numberSetLayout")) {
										int inte = Integer
												.parseInt(userTypedValue_);
										userTypedNumberList.add(inte);
									}
								}
							}
						}
						if (data.size() > 0)
							map.put("value", "value");
						else
							map.put("value", "#no_value#");
					}
				}
			}
			int total = 0;
			for (int number : userTypedNumberList) {
				total += number;
			}
			map.put("data", data);
			map.put("total", total);
			map.put("missing_labels", missingLabels);
			map.put("layout_id", layoutID);

			String maxValue = "";
			String minValue = "";
			String errorMsg = "";

			if (linearLayout.getTag(R.id.field_max_value) != null) {
				maxValue = linearLayout.getTag(R.id.field_max_value).toString();
				map.put("field_max_value", maxValue);
			}

			if (linearLayout.getTag(R.id.field_min_value) != null) {
				minValue = linearLayout.getTag(R.id.field_min_value).toString();
				map.put("field_min_value", minValue);
			}

			if (linearLayout.getTag(R.id.field_err_msg) != null) {
				errorMsg = linearLayout.getTag(R.id.field_err_msg).toString();
				map.put("field_err_msg", errorMsg);
			}
		}
		// else if(layoutID.equals("matrixCheckListLayout") ||
		// layoutID.equals("matrixOptionLayout")){
		//
		// }

		else {
			// for others field rather number and text
		}
		map.put("field_name", fieldName);
		map.put("field_label", fieldLabel);
		if (fieldErrorMsg.length() > 0)
			map.put("field_err_msg", fieldErrorMsg);

		if (linearLayout.getTag(R.id.field_required_id) != null) {
			map.put("field_required",
					linearLayout.getTag(R.id.field_required_id).toString());
		}

		return map;
	}

	public HashMap<String, Object> getValueFromLinearLayout(
			LinearLayout linearLayout) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (linearLayout.getTag(R.id.layout_id).toString().equals("textLayout")) {
			String key = "";
			String value = "";
			EditText edt1 = null;
			String field_label = "";

			for (int j = 0; j < linearLayout.getChildCount(); j++) {
				Class<?> subClass = (Class<?>) linearLayout.getChildAt(j)
						.getClass();
				if (subClass.getName().equals("android.widget.TextView")) {
					TextView textView = (TextView) linearLayout.getChildAt(j);
					// following if else is just to categorize the
					// textView
					// if that's label or error message
					if (!textView.getTag().toString().equals("errorMsg")) {
						// this is label
						key = textView.getTag(R.id.field_name_id).toString();
						field_label = textView.getTag(R.id.field_label_id)
								.toString();
					}
				} else if (subClass.getName().equals("android.widget.EditText")) {
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
					TextView textView = (TextView) linearLayout.getChildAt(j);
					// following if else is just to categorize the
					// textView
					// if that's label or error message
					if (!textView.getTag().toString().equals("errorMsg")) {
						// this is label
						key = textView.getTag(R.id.field_name_id).toString();
						field_label = textView.getTag(R.id.field_label_id)
								.toString();
					}
				} else if (subClass.getName().equals("android.widget.EditText")) {
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
					TextView textView = (TextView) linearLayout.getChildAt(z);

					// following if else is just to categorize the
					// textView
					// if that's label or error message
					if (!textView.getTag().toString().equals("errorMsg")) {
						key = textView.getTag(R.id.field_name_id).toString();
						field_label = textView.getTag(R.id.field_label_id)
								.toString();
					}
				} else if (subClass.getName().equals("android.widget.CheckBox")) {
					CheckBox checkBox = (CheckBox) linearLayout.getChildAt(z);
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
					TextView textView = (TextView) linearLayout.getChildAt(j);
					if (!textView.getTag().toString().equals("errorMsg")) {
						key = textView.getTag(R.id.field_name_id).toString();
						field_label = textView.getTag(R.id.field_label_id)
								.toString();
					}
				} else if (subClass.getName().equals("android.widget.EditText")) {
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
					TextView textView = (TextView) linearLayout.getChildAt(j);

					if (!textView.getTag().toString().equals("errorMsg")) {
						key = textView.getTag(R.id.field_name_id).toString();
						field_label = textView.getTag(R.id.field_label_id)
								.toString();
					}
				} else if (subClass.getName()
						.equals("android.widget.ImageView")) {

					// ဒီ method ထဲမှာဘာမှမလုပ်ဘူး... ဒီ small imageview က
					// preview ပဲပြထားတာ... တကယ့် path က သယ်လာပြီးသား....
					// validation ပဲလုပ်တာ..

					drawingPreview = (ImageView) linearLayout.getChildAt(j);
					// to check image involve or not
					/*
					 * Drawable d = drawingPreview.getDrawable(); if (d == null)
					 * { Toast.makeText(getApplicationContext(), "no image",
					 * 1000).show(); } else
					 * Toast.makeText(getApplicationContext(), "image include",
					 * 1000).show();
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
					TextView textView = (TextView) linearLayout.getChildAt(j);

					// following if else is just to categorize the
					// textView
					// if that's label or error message
					if (!textView.getTag().toString().equals("errorMsg")) {
						// this is label
						key = textView.getTag(R.id.field_name_id).toString();
						field_label = textView.getTag(R.id.field_label_id)
								.toString();
					}
				} else if (subClass.getName()
						.equals("android.widget.ImageView")) {
					drawingPreview = (ImageView) linearLayout.getChildAt(j);
				}
			}
			map.put(key, "unavailable");
		}
		return map;
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

	public LinearLayout getInnerLayout(LinearLayout parrentLayout) {
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

}

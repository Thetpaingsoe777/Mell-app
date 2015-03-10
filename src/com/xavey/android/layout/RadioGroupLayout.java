package com.xavey.android.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xavey.android.R;
import com.xavey.android.util.Utils;

public class RadioGroupLayout extends RadioGroup {

	private JSONArray _baseValueList;
	float _radioButtonTextSize = 16;
	 android.widget.LinearLayout.LayoutParams _extraValueLayoutParamsDisappear = new LayoutParams(0, 0);
	 android.widget.LinearLayout.LayoutParams _extraValueLayoutParamsAppear = new LayoutParams(0, 0);
	 android.widget.LinearLayout.LayoutParams _radioButtonLineLayoutParams = new LayoutParams(0, 0);

	public RadioGroupLayout(Context context, JSONArray baseValue) {
		super(context);
		this._baseValueList= baseValue;
		baseValue = null;
	}
	
	public void setRadioButtonTextSize(float radioButtonTextSize){
		_radioButtonTextSize=radioButtonTextSize;
	}
	
	public void setExtraValueLayoutParamsDisappear(android.widget.LinearLayout.LayoutParams extraValueLayoutParamsDisappear){
		_extraValueLayoutParamsDisappear=extraValueLayoutParamsDisappear;
	}
	
	public void setExtraValueLayoutParamsAppear(android.widget.LinearLayout.LayoutParams extraValueLayoutParamsAppear){
		_extraValueLayoutParamsAppear=extraValueLayoutParamsAppear;
	}
	
	public void setRadioButtonLineLayoutParams(android.widget.LinearLayout.LayoutParams radioButtonLineLayoutParams){
		_radioButtonLineLayoutParams=radioButtonLineLayoutParams;
	}
	
	public JSONArray getFinalBaseValueList(){
		return this._baseValueList;
	}
	
	public void setFinalBaseValueList(JSONArray val){
		this._baseValueList=val;
		val=null;
	}
	
	public void initLayout(JSONArray valueList, boolean field_random) throws Exception{
		this.removeAllViews();
		final RadioGroup that = this;

		LayoutParams radioGroupParams = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		radioGroupParams.setMargins(15, 5, 15, 5);
		this.setLayoutParams(radioGroupParams);

		ArrayList<LinearLayout> buttonLinesToBeRandomed = new ArrayList<LinearLayout>();

		// int default_value = Integer.parseInt(fields.get(
		// "field_default_value").toString());
		int default_value = 1;
		int length = valueList.length();
		for (int j = 0; j < length; j++) {
			JSONObject obj = new JSONObject();
			obj = valueList.getJSONObject(j);
			String text = obj.getString("label");
			String tag = obj.getString("value");
			String skip = obj.getString("field_skip");
			boolean extra = obj.getBoolean("extra");
			boolean extra_required = false;
			if (obj.has("extra_required"))
				extra_required = obj
						.getBoolean("extra_required");

			RadioButton radioButton = new RadioButton(this.getContext());
			radioButton.setId(Utils.generateViewId()); // View.generateViewId();
														// <=
														// only
														// support
														// for
														// lvl17
														// n
														// above
			radioButton.setText(text);
			radioButton.setTextSize(_radioButtonTextSize);
			radioButton.setTag(R.id.radio_value, tag);
			radioButton.setTag(R.id.field_skip, skip);
			radioButton.setTag(R.id.extra, extra);
			radioButton.setTag(R.id.is_radiobutton_selected,
					false);
			if (extra_required)
				radioButton.setTag(R.id.extra_required,
						extra_required);
			radioButton.setSelected(false);

			// if (tag.equals(field_default_value)) {
			// radioButton.setSelected(true);
			// radioButton.setChecked(true);
			// radioButton.setTag(R.id.is_radiobutton_selected,
			// true);
			// }

			LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
					RadioGroup.LayoutParams.MATCH_PARENT,
					RadioGroup.LayoutParams.WRAP_CONTENT);

			EditText extraValue = new EditText(this.getContext());
			extraValue
					.setLayoutParams(_extraValueLayoutParamsDisappear);
			extraValue.setTag(R.id.extra_status, "off");

			layoutParams.setMargins(0, 10, 0, 10);
			// if (default_value == (j + 1)) {
			// radioButton.setChecked(true);
			// radioButton.setTag(R.id.is_radiobutton_selected,
			// true);
			// }
			//setTypeFace(radioButton);

			// rg.addView(radioButton, j, layoutParams);\
			LinearLayout radioButtonLine = new LinearLayout(
					this.getContext());
			radioButtonLine.setTag(R.id.layout_id,
					"radioButtonLine");
			radioButtonLine
					.setLayoutParams(_radioButtonLineLayoutParams);
			radioButtonLine
					.setOrientation(LinearLayout.VERTICAL);
			radioButton
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							RadioButton clicked_button = (RadioButton) v;
							clicked_button
									.setTag(R.id.is_radiobutton_selected,
											true);

							// disable all other
							Random r = new Random();
							int randomValue = r.nextInt(1000);
							clicked_button.setTag(
									R.id.radio_random_value,
									randomValue);

							for (int i = 0; i <  that.getChildCount(); i++) {
								LinearLayout radioButtonLine_ = (LinearLayout) that.getChildAt(i);
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
									View child = radioButtonLine_
											.getChildAt(j);
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
											.setLayoutParams(_extraValueLayoutParamsDisappear);
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
												.setLayoutParams(_extraValueLayoutParamsAppear);
									}
								}
							}
						}
					});

			// extra par yin..
			/*
			 * if(radioButton.getTag(R.id.extra).toString().equals
			 * ("true")){ final EditText extraValue = new
			 * EditText(activity); extraValue.setLayoutParams(
			 * extraValueLayoutParamsAppear);
			 * extraValue.setTag(R.id.extra_status, "on");
			 * radioButtonLine.addView(); }
			 */
			if (radioButton.isChecked()) {
				radioButton.performClick();
			}
			radioButtonLine.addView(radioButton);
			radioButtonLine.addView(extraValue);

			// random here everything...
			// a condition should be checked here
			// cuz not every question should be randomed

			buttonLinesToBeRandomed.add(radioButtonLine); // <--
															// collect
															// the
															// lines
															// first

			// rg.addView(radioButtonLine);
		}
		// for loop ends here


		// random.... valid by some json in future
		// example.. -> field_random : true

		if (field_random) {
			Collections.shuffle(buttonLinesToBeRandomed);
		 }

		for (LinearLayout eachButtonLine : buttonLinesToBeRandomed) {
			this.addView(eachButtonLine);
		}

		// the clients don't want a default selected value
		// so nothing will be selected here
		// if (getSelectedRadioButtonMyRadioGroup(rg) == null) {
		// RadioButton firstButton =
		// getRadioButtonByIndexMyRadioGroup(
		// rg, 0);
		// firstButton.setChecked(true);
		// }
	}
}

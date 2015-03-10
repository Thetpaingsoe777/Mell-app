package com.xavey.android.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xavey.android.R;
import com.xavey.android.model.MatrixCell;
import com.xavey.android.util.LinearLayoutManager;
import com.xavey.android.util.MYHorizontalScrollView;

import android.content.Context;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class CheckboxLayout extends LinearLayout {

	private JSONArray _baseValueList;
	float _checkBoxButtonTextSize = 16;
	android.widget.LinearLayout.LayoutParams _extraValueLayoutParamsDisappear = new LayoutParams(0, 0);
	android.widget.LinearLayout.LayoutParams _extraValueLayoutParamsAppear = new LayoutParams(0, 0);
	android.widget.LinearLayout.LayoutParams _checkBoxLineLayoutParams = new LayoutParams(0, 0);
	
	public CheckboxLayout(Context context, JSONArray baseValue) {
		super(context);
		this._baseValueList= baseValue;
		baseValue = null;
	}

	public void setCheckBoxButtonTextSize(float checkBoxButtonTextSize){
		_checkBoxButtonTextSize=checkBoxButtonTextSize;
	}
	
	public void setExtraValueLayoutParamsDisappear(android.widget.LinearLayout.LayoutParams extraValueLayoutParamsDisappear){
		_extraValueLayoutParamsDisappear=extraValueLayoutParamsDisappear;
	}
	
	public void setExtraValueLayoutParamsAppear(android.widget.LinearLayout.LayoutParams extraValueLayoutParamsAppear){
		_extraValueLayoutParamsAppear=extraValueLayoutParamsAppear;
	}
	
	public void setCheckBoxLineLayoutParams(android.widget.LinearLayout.LayoutParams checkBoxLineLayoutParams){
		_checkBoxLineLayoutParams=checkBoxLineLayoutParams;
	}
	
	public JSONArray getFinalBaseValueList(){
		return this._baseValueList;
	}
	
	public void setFinalBaseValueList(JSONArray val){
		this._baseValueList=val;
		val=null;
	}
	
	
	public void initLayout(JSONArray valueList) throws Exception{
		this.removeAllViews();
		
		int default_value = 1;
		int length = valueList.length();
		
		ArrayList<CheckBox> checkBoxList = new ArrayList<CheckBox>();
		
		this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		this.setOrientation(LinearLayout.VERTICAL);
		
		int checkboxCount = 0;
		for (int j = 0; j < length; j++) {
			JSONObject obj = new JSONObject();
			obj = valueList.getJSONObject(j);
			String text = obj.getString("label");
			String tag = obj.getString("value");
                        boolean ignore_other = false;
			if(obj.has("ignore_other")){
				ignore_other = obj.getBoolean("ignore_other");
			}
			String skip = obj.getString("field_skip");
			boolean extra = obj.getBoolean("extra");
			boolean extra_required = false;
			if (obj.has("extra_required"))
				extra_required = obj
						.getBoolean("extra_required");
			CheckBox cb = new CheckBox(this.getContext());
			cb.setText(text);
			cb.setTextSize(_checkBoxButtonTextSize); // same as
													// radio
			cb.setTag(R.id.checkbox_value, tag);
			cb.setTag(R.id.field_skip, skip);
			cb.setTag(R.id.extra, extra);
			cb.setTag(R.id.extra_required, extra_required);
                        cb.setTag(R.id.ignore_other, ignore_other);
			//TODO : settypeface
			//cb.setTypeface(typeface.getTypeFace());
			LayoutParams cbParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			cbParams.setMargins(15, 5, 15, 5);
			cb.setLayoutParams(cbParams);
			//TODO set typeface
			//setTypeFace(cb);
			// if (default_value == (j + 1)) {
			// cb.setChecked(true);
			// }
			checkBoxList.add(cb);
			// checkBoxLayout.addView(cb);
			checkboxCount++;
		}

		//if (field_random) {
			// Collections.shuffle(checkBoxList);
		//}

		for (final CheckBox cb : checkBoxList) {
			LinearLayout checkBoxLine = new LinearLayout(this.getContext());
			checkBoxLine.setTag(R.id.layout_id, "checkBoxLine");
			checkBoxLine
					.setLayoutParams(_checkBoxLineLayoutParams);
			checkBoxLine.setOrientation(LinearLayout.VERTICAL);

			Boolean extraRequired = Boolean.parseBoolean(cb
					.getTag(R.id.extra).toString());

			EditText extraValue = new EditText(this.getContext());
			extraValue.setLayoutParams(_extraValueLayoutParamsDisappear);
							cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
								@Override
								public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
									
								}
							});
							
							cb.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View view___) {
									boolean isChecked = cb.isChecked();
									LinearLayout checkBoxLine = (LinearLayout) cb.getParent();
									CheckBox selectedCB = (CheckBox) checkBoxLine.getChildAt(0); // <- making static so far
									EditText selectedExtra = (EditText) checkBoxLine.getChildAt(1); // <- making static so far..
									if(selectedExtra!=null)
									if(!isChecked){
										selectedExtra.setLayoutParams(_extraValueLayoutParamsDisappear);
									}else{
										selectedExtra.setLayoutParams(_extraValueLayoutParamsAppear);
									}
									// ignore stuffs\
									LinearLayout checkBoxLayout = (LinearLayout) checkBoxLine.getParent();
									if(selectedCB.getTag(R.id.ignore_other)!=null){
										boolean ignore_other = Boolean.parseBoolean(selectedCB.getTag(R.id.ignore_other).toString());
										if(ignore_other){
											
											for(int c=0; c<checkBoxLayout.getChildCount(); c++){
												View v = checkBoxLayout.getChildAt(c);
												if(v.getTag(R.id.layout_id)!=null && v.getTag(R.id.layout_id).toString().equals("checkBoxLine")){
													LinearLayout singleCheckBoxLine = (LinearLayout) v;
													CheckBox singleCheckBox = LinearLayoutManager.getCheckBoxFromCheckBoxLine(singleCheckBoxLine);

													String a = singleCheckBox.toString(); //<-- debug it
													String b = selectedCB.toString(); // debug it if they are the same or not
													a.length();
													b.length();
													if(singleCheckBox!=selectedCB){
														singleCheckBox.setChecked(false);
													}
												}
											}
											
										} else{ // those checkboxes which involved ignore_other but false
											for(int c=0; c<checkBoxLayout.getChildCount(); c++){
												View v = checkBoxLayout.getChildAt(c);
												if(v.getTag(R.id.layout_id)!=null && v.getTag(R.id.layout_id).toString().equals("checkBoxLine")){
													LinearLayout singleCheckBoxLine = (LinearLayout) v;
													CheckBox singleCheckBox = LinearLayoutManager.getCheckBoxFromCheckBoxLine(singleCheckBoxLine);
													boolean single_ignore_other = Boolean.parseBoolean(singleCheckBox.getTag(R.id.ignore_other).toString());
													if(single_ignore_other){ // juz find the ignore_other checkbox and discheck it
														singleCheckBox.setChecked(false);
													}
												}
											}
										}
									}
									else{ // those checkboxes which doesn't even involved ignore_other 
										for(int c=0; c<checkBoxLayout.getChildCount(); c++){
											View v = checkBoxLayout.getChildAt(c);
											if(v.getTag(R.id.layout_id)!=null && v.getTag(R.id.layout_id).toString().equals("checkBoxLine")){
												LinearLayout singleCheckBoxLine = (LinearLayout) v;
												CheckBox singleCheckBox = LinearLayoutManager.getCheckBoxFromCheckBoxLine(singleCheckBoxLine);
												boolean single_ignore_other = Boolean.parseBoolean(singleCheckBox.getTag(R.id.ignore_other).toString());
												if(single_ignore_other){ // juz find the ignore_other checkbox and discheck it
													singleCheckBox.setChecked(false);
												}
											}
										}
									}
								}
							});
			checkBoxLine.addView(cb);
			if (extraRequired)
				checkBoxLine.addView(extraValue);
			this.addView(checkBoxLine);
		}
	}
}


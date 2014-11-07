package com.xavey.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
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
import android.widget.TextView;
import android.widget.TimePicker;

import com.xavey.app.adapter.QuestionPagerAdapter;
import com.xavey.app.db.XaveyDBHelper;
import com.xavey.app.model.Document;
import com.xavey.app.model.Form;
import com.xavey.app.model.Image;
import com.xavey.app.util.ConnectionDetector;
import com.xavey.app.util.ImageSavingManager;
import com.xavey.app.util.JSONReader;
import com.xavey.app.util.JSONWriter;
import com.xavey.app.util.LinearLayoutManager;
import com.xavey.app.util.SyncManager;
import com.xavey.app.util.ToastManager;
import com.xavey.app.util.TypeFaceManager;
import com.xavey.app.util.UUIDGenerator;

public class OneQuestionOneView extends FragmentActivity {

	private QuestionPagerAdapter qAdapter; // 19-9-2014

	ViewPager vPager;
	int pageTotal;

	Typeface zawGyiTypeFace;
	Typeface myanmarTypeFace;

	Intent intent;
	JSONReader jsonReader;
	JSONWriter jsonWriter;
	XaveyDBHelper dbHelper;
	Boolean isInternetAvailable = false;
	ConnectionDetector connectionDetector;
	Form currentForm;
	ArrayList<HashMap<String, Object>> formFieldsList;
	ArrayList<LinearLayout> layoutList;

	TypeFaceManager typeface;

	boolean isAllRequiredFieldFilled = true;
	ArrayList<HashMap<String, String>> imagesToSubmit = new ArrayList<HashMap<String, String>>();

	// DM
	DisplayMetrics dm;
	int screenWidth;
	int screenHeight;

	int previousIndex = 0;

	int jump = 0;
	LinkedList<Integer> navigator = new LinkedList<Integer>();
	LinkedList<String> used_field_names = new LinkedList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_slide);
		loadUI();

		vPager = (ViewPager) findViewById(R.id.pager);
		try {
			layoutList = jsonReader.readForm2(currentForm);
			layoutList.add(produceSubmitLayout());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * adapterViewPager = new QuestionPagerAdapter(
		 * getSupportFragmentManager(), layoutList);
		 * vPager.setAdapter(adapterViewPager);
		 */

		FragmentManager supportFragmentMagaer = getSupportFragmentManager();
		qAdapter = new QuestionPagerAdapter(getSupportFragmentManager(),
				layoutList);
		vPager.setAdapter(qAdapter);

		vPager.setOnPageChangeListener(new OnPageChangeListener() {

			String fieldRequired;
			String layoutID;
			int index = 0;

			LinearLayout currentLayout = null;
			LinearLayout nextLayout = null;
			RelativeLayout currentUpperLayout = null;
			int currentPosition = 0;

			// private int field_id;
			int skipID = 0;
			String direction = "";

			private final String LEFT_TO_RIGHT = "L_R";
			private final String RIGHT_TO_LEFT = "R_L";

			// int previousIndex = 0;

			// just for testing
			String currentLayoutID = "";
			String currentFieldName = "";
			String currentFieldRef = "";
			JSONArray currentNextCond;

			ToastManager toast = new ToastManager(OneQuestionOneView.this);

			@Override
			public void onPageSelected(int newPosition) {

				if (newPosition > currentPosition) {
					// left to right
					direction = LEFT_TO_RIGHT;
				} else {
					// right to left
					direction = RIGHT_TO_LEFT;
				}
				int lastJump = -1;
				if (navigator.size() != 0) {
					lastJump = navigator.getLast();
				}
				LinearLayout currentParentLayout = layoutList
						.get(currentPosition);
				for (int i = 0; i < currentParentLayout.getChildCount(); i++) {
					View view = currentParentLayout.getChildAt(i);
					Object obj = currentParentLayout.getChildAt(i);
					if (obj.getClass().getName()
							.equals("android.widget.LinearLayout")
							&& view.getTag(R.id.layout_id) != null && !view.getTag(R.id.layout_id).toString().equals("recordingLayout")) {
						currentLayout = (LinearLayout) view;
					}
					if (obj.getClass().getName()
							.equals("android.widget.RelativeLayout")) {
						currentUpperLayout = (RelativeLayout) view;
					}
				}

				nextLayout = layoutList.get(currentPosition);
				LinearLayout nextInnerLayout = getInnerLayout(nextLayout);
				// if next layout is submit or not
				boolean isSubmitLayout = isSubmitLayout(nextLayout);
				if(!isSubmitLayout)
				if (nextInnerLayout.getTag(R.id.layout_id) != null) {
					currentLayoutID = nextInnerLayout.getTag(R.id.layout_id)
							.toString();
					currentFieldName = nextInnerLayout.getTag(R.id.field_name_id)
							.toString();
					currentFieldRef = nextInnerLayout.getTag(R.id.field_ref).toString();
					currentNextCond = (JSONArray)nextInnerLayout.getTag(R.id.next_cond);
				}else{
					for(int i=0; i<currentNextCond.length(); i++){
						currentNextCond.remove(i);
					}
				}
				

				/*
				 * String toast_text = "Current Position : " + currentPosition +
				 * "\n" + "New Position : " + newPosition + "\n" +
				 * "CurrentLayout ID : " + currentLayoutID + "\n" +
				 * "Last Jump Range : " + lastJump;
				 * Toast.makeText(getApplicationContext(), toast_text, 4000)
				 * .show();
				 */

				boolean isNeedToValid = currentLayout.getTag(R.id.layout_id)
						.toString().equals("radioLayout");
				isNeedToValid = isNeedToValid
						|| currentLayout.getTag(R.id.layout_id).toString()
								.equals("datetimeLayout");
				isNeedToValid = isNeedToValid
						|| currentLayout.getTag(R.id.layout_id).toString()
								.equals("submitLayout");
				LinearLayoutManager lLManager = new LinearLayoutManager();

				TextView errorMsg = lLManager
						.getErrorMsgTextView(currentLayout);

				 LayoutParams errorMsgLayoutOpen = new LayoutParams(
				 LayoutParams.MATCH_PARENT,
				 30);
				 errorMsgLayoutOpen.setMargins(10, 20, 10, 20);

				 LayoutParams errorMsgLayoutHide = new LayoutParams(
				 LayoutParams.MATCH_PARENT, 0);
				 errorMsgLayoutHide.setMargins(10, 20, 10, 20);

				if (!isNeedToValid) {
					HashMap<String, String> test = lLManager
							.test(currentLayout);
					String value = test.get("value");
					String field_required = test.get("field_required");
					String field_label = test.get("field_label");
					boolean isNotTyped = field_required.equals("true")
							&& value.equals("#no_value#");
					if (!isNotTyped) {
						// user typed values
						String tagID = currentLayout.getTag(R.id.layout_id)
								.toString();
						if (tagID.equals("numberLayout")) {
							String value_ = test.get("value").toString();
							String field_max_value = test
									.get("field_max_value");
							String field_min_value = test
									.get("field_min_value");
							// String field_default_value =
							// test.get("field_default_value"); // no need yet
							String field_err_msg = test.get("field_err_msg");
							int userTypedValue = Integer.parseInt(value_);
							int maxValue = Integer.parseInt(field_max_value);
							int minValue = Integer.parseInt(field_min_value);
							if (userTypedValue > maxValue
									|| userTypedValue < minValue) {
								// out of range ..
								// block
								// block for only left to right cuz invalid
								if (direction.equals(LEFT_TO_RIGHT)) {
									// int range = newPosition-currentPosition;
									navigator.addLast(0);
									used_field_names.addLast(currentFieldName);
									vPager.setCurrentItem(currentPosition);
									errorMsg.setText("" + field_err_msg);
									errorMsg.setTextColor(Color.RED);
									// errorMsg.setLayoutParams(errorMsgLayoutOpen);
									currentPosition = previousIndex;
								} else { // RIGHT_TO_LEFT
									int last_range = navigator.getLast();
									newPosition = currentPosition - last_range;
									vPager.setCurrentItem(newPosition);
									currentPosition = newPosition;
									navigator.removeLast();
									used_field_names.removeLast();
								}
							} else {
								// in range
								// pass
								if (direction.equals(LEFT_TO_RIGHT)) {
									
									// logic testing
									// reverse
									/*LinearLayout nextLayout_ = layoutList.get(newPosition);
									boolean isInvolvedRef = isFieldInvolvedReference(nextLayout_);
									if(isInvolvedRef){
										String field_ref = getReferenceFromParrentLayout(nextLayout_);
										LinearLayout ref_layout = getRefLayout(field_ref, layoutList);
										JSONArray next_cond = getNextConditionFromParrentLayout(nextLayout_);
										String value_from_ref_layout = jsonReader.readValueFromLayout(ref_layout);
										boolean isNeedToSkip = isNeedToSkip(next_cond, value_from_ref_layout);
										newPosition++;
									}*/
									//------------------------------------------------------
									newPosition = getNextRoute(newPosition);
									int range = newPosition - currentPosition;
									if (range != 0)
										navigator.addLast(range);
									used_field_names.addLast(currentFieldName);
									vPager.setCurrentItem(newPosition);
									currentPosition = newPosition;
									previousIndex = currentPosition;
									// hide keyboard
									LinearLayout nextLayout_ = layoutList.get(newPosition);
									if(!isSubmitLayout(nextLayout_))
										hideKeyboard(nextLayout_);
								} else { // RIGHT_TO_LEFT
									int last_range = navigator.getLast();
									newPosition = currentPosition - last_range;
									vPager.setCurrentItem(newPosition);
									currentPosition = newPosition;
									navigator.removeLast();
									used_field_names.removeLast();
									// hide keyboard
									LinearLayout nextLayout_ = layoutList.get(newPosition);
									if(!isSubmitLayout(nextLayout_))
										hideKeyboard(nextLayout_);
								}
								if (errorMsg != null)
									errorMsg.setText("");
							}
						} else {
							// user typed and field type is not number... 
							// so no need validation let them go

							if (direction.equals(LEFT_TO_RIGHT)) {
								
								// logic testing
								// reverse
								/*LinearLayout nextLayout_ = layoutList.get(newPosition);
								boolean isInvolvedRef = isFieldInvolvedReference(nextLayout_);
								if(isInvolvedRef){
									String field_ref = getReferenceFromParrentLayout(nextLayout_);
									LinearLayout ref_layout = getRefLayout(field_ref, layoutList);
									JSONArray next_cond = getNextConditionFromParrentLayout(nextLayout_);
									String value_from_ref_layout = jsonReader.readValueFromLayout(ref_layout);
									boolean isNeedToSkip = isNeedToSkip(next_cond, value_from_ref_layout);
									newPosition++;
								}*/
								//----------------------------------------------------------
								newPosition = getNextRoute(newPosition);
								int range = newPosition - currentPosition;
								if (range != 0)
									navigator.addLast(range);
								used_field_names.addLast(currentFieldName);
								vPager.setCurrentItem(newPosition);
								currentPosition = newPosition;
								previousIndex = currentPosition;
								// hide keyboard
								LinearLayout nextLayout_ = layoutList.get(newPosition);
								if(!isSubmitLayout(nextLayout_))
									hideKeyboard(nextLayout_);
							} else { // RIGHT_TO_LEFT
								int last_range = navigator.getLast();
								newPosition = currentPosition - last_range;
								vPager.setCurrentItem(newPosition);
								currentPosition = newPosition;
								navigator.removeLast();
								used_field_names.removeLast();
								// hide keyboard
								LinearLayout nextLayout_ = layoutList.get(newPosition);
								if(!isSubmitLayout(nextLayout_))
									hideKeyboard(nextLayout_);
							}
							if (errorMsg != null)
								errorMsg.setText("");
						}
					} else {
						// user didn't type anything
						// block

						if (direction.equals(LEFT_TO_RIGHT)) {
							navigator.addLast(0);
							used_field_names.addLast(currentFieldName);
							vPager.setCurrentItem(currentPosition);
							errorMsg.setText(field_label + " is required !");
							errorMsg.setTextColor(Color.RED);
							currentPosition = previousIndex;
						} else { // RIGHT_TO_LEFT
							int last_range = navigator.getLast();
							newPosition = currentPosition - last_range;
							vPager.setCurrentItem(newPosition);
							currentPosition = newPosition;
							navigator.removeLast();
							used_field_names.removeLast();
						}
					}
				} else {
					// here will be fragments they are not concerned with any
					// validation
					// pass
					// another question is where to go if radio layout
					// (skip logic)
					if (currentLayout.getTag(R.id.layout_id).toString()
							.equals("radioLayout")) {
						for (int i = 0; i < currentLayout.getChildCount(); i++) {
							String className = currentLayout.getChildAt(i)
									.getClass().getName().toString();
							if (className.equals("android.widget.RadioGroup")) {
								// radio
								RadioGroup radioGroup = (RadioGroup) currentLayout
										.getChildAt(i);
								
								RadioButton selectedButton = getSelectedRadioButtonMyRadioGroup(radioGroup);

								if (direction.equals(LEFT_TO_RIGHT)) {
									String field_skip = selectedButton.getTag(
											R.id.field_skip).toString();
									if (field_skip.length() > 0) {
										if(field_skip.equals("submit")){
											//go to submit
											ToastManager tm = new ToastManager(OneQuestionOneView.this);
											//tm.xaveyToast(null, "here should be go to submit");
											newPosition = layoutList.size()-1;
											int range = newPosition - currentPosition;
											navigator.addLast(range);
											used_field_names.addLast(currentFieldName);
											vPager.setCurrentItem(newPosition);
											currentPosition = newPosition;
											previousIndex = currentPosition;
											// hide keyboard
											LinearLayout nextLayout_ = layoutList.get(newPosition);
											if(!isSubmitLayout(nextLayout_))
												hideKeyboard(nextLayout_);
										}
										else{
											skipID = Integer.parseInt(field_skip);
											newPosition = skipID - 1;
											newPosition = getNextRoute(newPosition);
											previousIndex = currentPosition;
											int range = newPosition
													- currentPosition;
											if (range != 0) // <-- don't know why
															// but a
															// zero came sometimes,
															// so i
															// filtered
												navigator.addLast(range);

											used_field_names
													.addLast(currentFieldName);
											vPager.setCurrentItem(newPosition);
											currentPosition = newPosition;	
											// hide keyboard
											LinearLayout nextLayout_ = layoutList.get(newPosition);
											if(!isSubmitLayout(nextLayout_))
												hideKeyboard(nextLayout_);
										}
									}else{
										// logic testing
										// reverse
										/*LinearLayout nextLayout_ = layoutList.get(newPosition);
										boolean isInvolvedRef = isFieldInvolvedReference(nextLayout_);
										if(isInvolvedRef){
											String field_ref = getReferenceFromParrentLayout(nextLayout_);
											LinearLayout ref_layout = getRefLayout(field_ref, layoutList);
											JSONArray next_cond = getNextConditionFromParrentLayout(nextLayout_);
											String value_from_ref_layout = jsonReader.readValueFromLayout(ref_layout);
											boolean isNeedToSkip = isNeedToSkip(next_cond, value_from_ref_layout);
											newPosition++;
										}*/
										newPosition = getNextRoute(newPosition);
										int range = newPosition-currentPosition;
										navigator.addLast(range);
										used_field_names.addLast(currentFieldName);
										vPager.setCurrentItem(newPosition);
										currentPosition = newPosition;
										previousIndex = currentPosition;
										// hide keyboard
										LinearLayout nextLayout_ = layoutList.get(newPosition);
										if(!isSubmitLayout(nextLayout_))
											hideKeyboard(nextLayout_);
									}
								} else { // RIGHT_TO_LEFT
									int last_range = navigator.getLast();
									newPosition = currentPosition - last_range;
									vPager.setCurrentItem(newPosition);
									currentPosition = newPosition;
									navigator.removeLast();
									used_field_names.removeLast();
									// hide keyboard
									LinearLayout nextLayout_ = layoutList.get(newPosition);
									if(!isSubmitLayout(nextLayout_))
										hideKeyboard(nextLayout_);
								}
								if (errorMsg != null)
									errorMsg.setLayoutParams(new LayoutParams(
											LayoutParams.WRAP_CONTENT, 0));
							}
						}
					} else {
						// not radio
						// no need validation

						if (direction.equals(LEFT_TO_RIGHT)) {
							
							// logic testing
							// reverse
//							LinearLayout nextLayout_ = layoutList.get(newPosition);
//							boolean isInvolvedRef = isFieldInvolvedReference(nextLayout_);
//							if(isInvolvedRef){
//								String field_ref = getReferenceFromParrentLayout(nextLayout_);
//								LinearLayout ref_layout = getRefLayout(field_ref, layoutList);
//								JSONArray next_cond = getNextConditionFromParrentLayout(nextLayout_);
//								String value_from_ref_layout = jsonReader.readValueFromLayout(ref_layout);
//								boolean isNeedToSkip = isNeedToSkip(next_cond, value_from_ref_layout);
//								newPosition++;
//							}
							//----------------------------------------------------------

							newPosition = getNextRoute(newPosition);
							int range = newPosition - currentPosition;
							navigator.addLast(range);
							used_field_names.addLast(currentFieldName);
							
							vPager.setCurrentItem(newPosition);
							currentPosition = newPosition;
							previousIndex = currentPosition;
							// hide keyboard
							LinearLayout nextLayout_ = layoutList.get(newPosition);
							if(!isSubmitLayout(nextLayout_))
								hideKeyboard(nextLayout_);

						} else { // RIGHT_TO_LEFT
							int last_range = navigator.getLast();
							if (currentPosition - last_range > 0) {
								newPosition = currentPosition - last_range;
								navigator.removeLast();
								used_field_names.removeLast();
							}
							vPager.setCurrentItem(newPosition);
							currentPosition = newPosition;
							// hide keyboard
							LinearLayout nextLayout_ = layoutList.get(newPosition);
							if(isSubmitLayout(nextLayout_))
								hideKeyboard(nextLayout_);
						}
						if (errorMsg != null)
							errorMsg.setText("");
					}
				}
			}

			

			private int getNextRoute(int newPosition){
				boolean isNeedToSkip = true;
				
				while(isNeedToSkip){
					LinearLayout nextLayout_ = layoutList.get(newPosition);
					boolean isInvolvedRef = isFieldInvolvedReference(nextLayout_);
					if(isInvolvedRef){
						String field_ref = getReferenceFromParrentLayout(nextLayout_);
						LinearLayout ref_layout = getRefLayout(field_ref, layoutList);
						JSONArray next_cond = getNextConditionFromParrentLayout(nextLayout_);
						String value_from_ref_layout = jsonReader.readValueFromLayout(ref_layout);
						isNeedToSkip = isNeedToSkip(next_cond, value_from_ref_layout);
						newPosition++;
					}
					else{
						isNeedToSkip=false;
					}
				}
				
				return newPosition;
			}
			
			private boolean isFieldInvolvedReference(LinearLayout parrentlayout){
				String field_ref="";
				for(int i=0; i<parrentlayout.getChildCount(); i++){
					if(parrentlayout.getTag(R.id.layout_id)!=null){
						if(parrentlayout.getTag(R.id.layout_id).toString().equals("submitLayout"))
							return false;
					}
					else if(parrentlayout.getChildAt(i).getClass().getName().equals("android.widget.LinearLayout")){
						LinearLayout linearLayout = (LinearLayout) parrentlayout.getChildAt(i);
						LinearLayout innerLayout = null;
						if(linearLayout.getTag(R.id.layout_id)!=null && !linearLayout.getTag(R.id.layout_id).toString().equals("recordingLayout")){
							innerLayout = linearLayout;
							field_ref = innerLayout.getTag(R.id.field_ref).toString();
						}
					}
				}
				if(field_ref.length()>0)
					return true;
				else
					return false;
			}

			private String getReferenceFromParrentLayout(LinearLayout parrentLayout){
				String reference = "";
				for(int i=0; i<parrentLayout.getChildCount(); i++){
					if(parrentLayout.getTag(R.id.layout_id)!=null){
						if(parrentLayout.getTag(R.id.layout_id).toString().equals("submitLayout"))
							return "";
					}
					else if(parrentLayout.getChildAt(i).getClass().getName().equals("android.widget.LinearLayout")){
						LinearLayout linearLayout = (LinearLayout) parrentLayout.getChildAt(i);
						LinearLayout innerLayout = null;
						if(linearLayout.getTag(R.id.layout_id)!=null && !linearLayout.getTag(R.id.layout_id).toString().equals("recordingLayout")){
							innerLayout = linearLayout;
							reference = innerLayout.getTag(R.id.field_ref).toString();
						}
					}
				}
				return reference;
			}
			
			
			
			private boolean isSubmitLayout(LinearLayout linearLayout){
				boolean isSubmitLayout = false;
				if(linearLayout.getTag(R.id.layout_id)!=null){
					if(linearLayout.getTag(R.id.layout_id).toString().equals("submitLayout")){
						isSubmitLayout = true;
					}
				}
				return isSubmitLayout;
			}
			
			private LinearLayout getInnerLayout(LinearLayout parrentLayout){
				LinearLayout innerLayout = null;
				for(int i=0; i<nextLayout.getChildCount(); i++){
					String className = nextLayout.getChildAt(i).getClass().getName();
					if(className.equals("android.widget.LinearLayout")){
						innerLayout = (LinearLayout) nextLayout.getChildAt(i);
						if(innerLayout.getTag(R.id.layout_id)!=null){
							return innerLayout;
						}
						else{
							return null;
						}
					}
				}
				return innerLayout;
			}

			private LinearLayout getRefLayout(String field_ref, ArrayList<LinearLayout> layoutList){
				LinearLayout refLayout = null;
				int field_ref_ = Integer.parseInt(field_ref);
				refLayout = layoutList.get(field_ref_-1);
				return refLayout;
			}
			
			private JSONArray getNextConditionFromParrentLayout(LinearLayout parrentLayout){
				JSONArray nextCondition = null;
				for(int i=0; i<parrentLayout.getChildCount(); i++){
					if(parrentLayout.getTag(R.id.layout_id)!=null){
						if(parrentLayout.getTag(R.id.layout_id).toString().equals("submitLayout"))
							return null;
					}
					else if(parrentLayout.getChildAt(i).getClass().getName().equals("android.widget.LinearLayout")){
						LinearLayout linearLayout = (LinearLayout) parrentLayout.getChildAt(i);
						LinearLayout innerLayout = null;
						if(linearLayout.getTag(R.id.layout_id)!=null && !linearLayout.getTag(R.id.layout_id).toString().equals("recordingLayout")){
							innerLayout = linearLayout;
							nextCondition = (JSONArray) innerLayout.getTag(R.id.next_cond);
						}
					}
				}
				return nextCondition;
			}
			
			private boolean isNeedToSkip(JSONArray next_cond, String value){
				boolean isNeedToSkip = false;
				if(value.contains("|")){
					// for values which contain pipes
					// like checklist and location
					String[] values = value.split("|");
					for(int i=0; i<next_cond.length(); i++){
						try{
							String cond = next_cond.getString(i);
							for(int j=0; j<values.length; j++){
								String value_item = values[j];
								if(cond.equals(value_item)){
									isNeedToSkip = isNeedToSkip||true;
								}
							}
						}
						catch(JSONException e){
							toast.xaveyToast(null, e.getMessage());
						}
					}
				}
				else{
					for(int i=0; i<next_cond.length(); i++){
						String cond="";
						try {
							cond = next_cond.getString(i);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(cond.equals(value)){
							isNeedToSkip = isNeedToSkip||true;
						}
					}
				}
				return isNeedToSkip;
			}

			private void previous(int newPosition) {
				int last_range = navigator.getLast();
				vPager.setCurrentItem(newPosition - last_range);
				currentPosition = newPosition - last_range;
			}

			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int position) {
				// if(currentPosition>position)
				// {
				// Toast.makeText(getApplicationContext(), "Swiped Right",
				// Toast.LENGTH_SHORT).show();
				// }else
				// {
				// Toast.makeText(getApplicationContext(), "Swiped Left",
				// Toast.LENGTH_SHORT).show();
				// }
				// currentPosition = position;
			}

		});

	}

	/*
	 * private int findIndexByFieldID(int field_id){ for(int i=0;
	 * i<layoutList.size(); i++){ LinearLayout theLayout = layoutList.get(i);
	 * int layoutFieldID =
	 * Integer.parseInt(theLayout.getTag(R.id.field_id).toString());
	 * if(layoutFieldID==field_id) return i; } return -1; }
	 */

	private int findIndexByFieldID(int field_id) {

		for (int i = 0; i < layoutList.size(); i++) {
			LinearLayout theLayout = layoutList.get(i);
			int layoutFieldID = Integer.parseInt(theLayout
					.getTag(R.id.field_id).toString());
			if (layoutFieldID == field_id)
				return i;
		}
		return -1;
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ApplicationValues.REQUEST_DRAWING & data != null) {
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

			for (int i = 0; i < layoutList.size() - 1; i++) {
				LinearLayout parentLayout = (LinearLayout) layoutList.get(i);
				LinearLayout childLayout = null;
				for (int z = 0; z < parentLayout.getChildCount(); z++) {
					View view = parentLayout.getChildAt(z);
					if (view.getClass().getName()
							.equals("android.widget.LinearLayout")
							&& view.getTag(R.id.layout_id) != null) {
						childLayout = (LinearLayout) view;
					}
				}
				if (childLayout.getTag(R.id.layout_id).toString()
						.equals("drawingLayout")) {
					for (int j = 0; j < childLayout.getChildCount(); j++) {
						if (childLayout.getChildAt(j).getId() == view_id) {
							ImageView imageView = (ImageView) childLayout
									.getChildAt(j);
							ImageSavingManager.loadImageFromLocal(imagePath,
									imageView);
							imageView.setTag(imagePath);
						}
					}
				}
			}
		}
		// ------------------------- ~DRAWING END~ ---------------

		else if (requestCode == ApplicationValues.REQUEST_CAMERA
				&& resultCode == RESULT_OK) {
			// Bundle bundle = data.getExtras();
			// bundle.getString("photo_path");
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

			for (int i = 0; i < layoutList.size() - 1; i++) {
				LinearLayout parentLayout = (LinearLayout) layoutList.get(i);
				LinearLayout childLayout = null;
				for (int z = 0; z < parentLayout.getChildCount(); z++) {
					View view = parentLayout.getChildAt(z);
					if (view.getClass().getName()
							.equals("android.widget.LinearLayout")
							&& view.getTag(R.id.layout_id) != null) {
						childLayout = (LinearLayout) view;
					}
				}

				if (childLayout.getTag(R.id.layout_id).toString()
						.equals("photoLayout")) {
					for (int j = 0; j < childLayout.getChildCount(); j++) {
						if (childLayout.getChildAt(j).getId() == view_id) {
							ImageView imageView = (ImageView) childLayout
									.getChildAt(j);
							ImageSavingManager.loadImageFromLocal(imagePath,
									imageView);
							imageView.setTag(imagePath);

							// test
							/*
							 * String miniPath =
							 * ImageSavingManager.getMiniPhotoPath(this); String
							 * largePath =
							 * ImageSavingManager.getLargePhotoPath(this);
							 * Toast.makeText(this, "minPath : " + miniPath +
							 * "\nlargePath: " + largePath, 1000).show();
							 * ImageSavingManager .loadImageFromLocal(miniPath,
							 * imageView); imageView.setTag(largePath);
							 */
						}
					}
				}
			}
		} else if (requestCode == ApplicationValues.SELECT_FILE
				&& resultCode == RESULT_OK && data != null) {
			Uri selectedImageUri = data.getData();
			String tempPath = getPath(selectedImageUri, OneQuestionOneView.this);
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

			for (int i = 0; i < layoutList.size() - 1; i++) {
				LinearLayout parentLayout = (LinearLayout) layoutList.get(i);
				LinearLayout childLayout = null;
				for (int z = 0; z < parentLayout.getChildCount(); z++) {
					View view = parentLayout.getChildAt(z);
					if (view.getClass().getName()
							.equals("android.widget.LinearLayout")
							&& view.getTag(R.id.layout_id) != null) {
						childLayout = (LinearLayout) view;
					}
				}
				if (childLayout.getTag(R.id.layout_id).toString()
						.equals("photoLayout")) {
					for (int j = 0; j < childLayout.getChildCount(); j++) {
						if (childLayout.getChildAt(j).getId() == view_id) {
							ImageView imageView = (ImageView) childLayout
									.getChildAt(j);
							ImageSavingManager.loadImageFromLocal(imagePath,
									imageView);
							imageView.setTag(imagePath);
						}
					}
				}
			}
		}
	}

	private LinearLayout produceSubmitLayout() {
		LinearLayout lL = new LinearLayout(this);
		lL.setOrientation(LinearLayout.VERTICAL);
		lL.setTag(R.id.layout_id, "submitLayout");
		lL.setTag(R.id.field_name_id, "blah blah");
		lL.setTag(R.id.field_required_id, "blah blah");

		lL.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		lL.setGravity(Gravity.CENTER);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		lp.setMargins(15, 0, 15, 0);
		final Button btnSubmit = new Button(this);
		btnSubmit.setText("SUBMIT");
		btnSubmit.setTextSize(16);
		btnSubmit.setLayoutParams(lp);
		btnSubmit.setBackgroundResource(R.drawable.submit_button_border);
		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Document document = new Document();
				ArrayList<LinearLayout> completeList = getCompleteList(
						layoutList, used_field_names);
				HashMap<String, Object> incompleteMap = getValuesFromEachLayout(completeList);

				JSONObject document_json = new JSONObject();
				JSONArray jsonArray = new JSONArray();
				// notes...
				// formFieldsList က form တစ်ခုလုံးရဲ့ structure မှာပါတဲ့
				// မှန်သမျှကိုယူထားတာ..
				// အဲဒီ့တော့ user skip လုပ်လိုက်တဲ့ ဖီးလ်တွေပါ ပါလာမှာ...
				// အဲဒါကြောင့် incompleteMap ထဲမှာ မပါရင် မယူဘူးဆိုပြီး filter
				// တစ်ခုခံထားလိုက်တာ...
				for (int i = 0; i < formFieldsList.size(); i++) {
					if (formFieldsList.get(i).size() > 0) {
						HashMap<String, Object> map = formFieldsList.get(i);
						String fieldLabel = map.get("field_label").toString();
						String fieldName = map.get("field_name").toString();
						String userTypedValue = "";
						if (incompleteMap.containsKey(fieldName)) { // <--
																	// filter
							userTypedValue = incompleteMap.get(fieldName)
									.toString();
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
					// document json start here
					document_json.put("document_json", jsonArray);
					document.setDocument_id(UUIDGenerator.getUUIDForDocument());
					document.setDocument_name(currentForm.getForm_title() + "-"
							+ getCurrentDateTime());
					document.setCreated_at(getCurrentDateTime());
					document.setDocument_json(document_json.toString());
					document.setForm_id(currentForm.getForm_id());
					document.setCreated_worker(ApplicationValues.loginUser
							.getUser_id());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// -----
				isInternetAvailable = connectionDetector
						.isConnectingToInternet();
				if (isInternetAvailable) {
					// submit it with API and set true to isSynced
					// document.setSubmitted("1");
					try {
						SyncManager syncManager = new SyncManager(
								OneQuestionOneView.this);
						// syncManager.submitDocument(document,
						// currentForm);
						if (imagesToSubmit.size() > 0) {
							syncManager.submitDocument2(document, currentForm,
									imagesToSubmit); // <--
						} else {
							syncManager.submitDocument(document, currentForm);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else { // offline mode
					document.setSubmitted("0");
					// save image too.
					String docID = document.getDocument_id();
					for (HashMap<String, String> image_map : imagesToSubmit) {
						String image_name = image_map.get("field_name");
						String image_path = image_map.get("imagePath");
						Image image = new Image();
						image.setDoc_id(docID);
						image.setImage_name(image_name);
						image.setImage_path(image_path);
						dbHelper.addNewImage(image);
					}
					SyncManager syncManager = new SyncManager(
							OneQuestionOneView.this);

					// document_json(structure) is needed everytime before
					// upload
					try {
						JSONArray mainArray = jsonReader.getJSONArrayToSubmit(
								document, currentForm);
						// here.. may be shock
						document.setDocument_json_to_submit(mainArray
								.getJSONObject(0).toString());
						// following line won't be needed , but not sure..., fix
						// later
						document.setSubmitted("0");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				dbHelper.addNewDocument(document);
				finish();
			}// onclick
		});
		lL.addView(btnSubmit);
		return lL;
	}

	// this method only select user typed values
	private ArrayList<LinearLayout> getCompleteList(
			ArrayList<LinearLayout> layoutList,
			LinkedList<String> used_field_names) {
		LinkedList<String> noDupField = removeDups(used_field_names);
		ArrayList<LinearLayout> completeList = new ArrayList<LinearLayout>();
		for (LinearLayout linearLayout : layoutList) {
			String field_name = new LinearLayoutManager()
					.getFieldNameFromLayout(linearLayout);
			if (noDupField.contains(field_name))
				completeList.add(linearLayout);
		}
		return completeList;
	}

	// following method remove duplicate rooms
	private LinkedList<String> removeDups(LinkedList<String> linkedList) {
		for (int i = 0; i < linkedList.size(); i++) {
			for (int j = i + 1; j < linkedList.size(); j++) {
				if (linkedList.get(i).equals(linkedList.get(j))) {
					linkedList.remove(j);
				}
			}
		}
		return linkedList;
	}

	private String getCurrentDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	private HashMap<String, Object> getValuesFromEachLayout(
			ArrayList<LinearLayout> layoutList) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < layoutList.size() - 1; i++) {
			// -1 don't care the last room cuz the last room is submitLayout
			LinearLayout parentLayout = (LinearLayout) layoutList.get(i);
			LinearLayout linearLayout = null;
			for (int p = 0; p < parentLayout.getChildCount(); p++) {
				View child = parentLayout.getChildAt(p);
				if (child.getTag(R.id.layout_id) != null
						&& child.getClass().getName()
								.equals("android.widget.LinearLayout")) {
					linearLayout = (LinearLayout) parentLayout.getChildAt(p);
				}
			}
			parentLayout.getChildAt(1);

			if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("textLayout")) {
				String key = linearLayout.getTag(R.id.field_name_id).toString();
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

						key = linearLayout.getTag(R.id.field_name_id)
								.toString();
						field_label = linearLayout.getTag(R.id.field_label_id)
								.toString();

					} else if (subClass.getName().equals(
							"android.widget.EditText")) {
						edt1 = (EditText) linearLayout.getChildAt(j);
						value = edt1.getText().toString();
					}
				}
				map.put(key, Integer.parseInt(value));
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("radioLayout")) {
				String key = linearLayout.getTag(R.id.field_name_id).toString();
				for (int y = 0; y < linearLayout.getChildCount(); y++) {

					Class<?> subClass = (Class<?>) linearLayout.getChildAt(y)
							.getClass();

					if (subClass.getName().equals("android.widget.RadioGroup")) {
						// radio
						RadioGroup radioGroup = (RadioGroup) linearLayout
								.getChildAt(y);
						RadioButton selectedButton = getSelectedRadioButtonMyRadioGroup(radioGroup);
						String value = selectedButton.getTag(R.id.radio_value)
								.toString();
						
						for(int u=0; u<radioGroup.getChildCount(); u++){
							LinearLayout layoutLine = (LinearLayout) radioGroup.getChildAt(u);
							RadioButton childButton = null;
							EditText childEditText = null;
							for(int v=0; v<layoutLine.getChildCount(); v++){
								String childClassName = layoutLine.getChildAt(v).getClass().getName().toString();
								if(childClassName.equals("android.widget.RadioButton"))
								{
									childButton = (RadioButton) layoutLine.getChildAt(v);
								}
								else if(childClassName.equals("android.widget.EditText")){
									childEditText = (EditText) layoutLine.getChildAt(v);
								}								
							}

							if(childButton.getTag(R.id.radio_value).toString().equals(value)){
								value = value + ":" + childEditText.getText().toString();
							}
						}
						map.put(key, value);
					}
				}
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("checkBoxLayout")) {
				//JSONArray checkedValues = new JSONArray();
				String checkedValues = "";
				String key = linearLayout.getTag(R.id.field_name_id).toString();
				String field_label = linearLayout.getTag(R.id.field_label_id)
						.toString();

				for (int z = 0; z < linearLayout.getChildCount(); z++) {
					Class<?> subClass = (Class<?>) linearLayout.getChildAt(z)
							.getClass();
					if (subClass.getName().equals("android.widget.TextView")) {
						TextView textView = (TextView) linearLayout
								.getChildAt(z);

						// following if else is just to categorize the
						// textView

					} else if (subClass.getName().equals(
							"android.widget.CheckBox")) {
						CheckBox checkBox = (CheckBox) linearLayout
								.getChildAt(z);
						if (checkBox.isChecked()) {
							String value = checkBox.getTag().toString();
							//checkedValues.put(value);
							checkedValues += "|"+value;
						}
					}
				}
				if(checkedValues.length()>0)
					checkedValues = checkedValues.substring(1);
				else
					checkedValues = "-";
				map.put(key, checkedValues);
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("locationLayout")) {
				double latitude = 0.0;
				double longitude = 0.0;

				String key = linearLayout.getTag(R.id.field_name_id).toString();
				String field_label = linearLayout.getTag(R.id.field_label_id)
						.toString();

				for (int j = 0; j < linearLayout.getChildCount(); j++) {
					Class<?> subClass = (Class<?>) linearLayout.getChildAt(j)
							.getClass();
					if (subClass.getName().equals("android.widget.EditText")) {
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

				map.put(key, latitude + "|" + longitude);
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("datetimeLayout")) {
				String key = linearLayout.getTag(R.id.field_name_id).toString();
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
				map.put(key, date + "  " + time);
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("drawingLayout")) {
				String key = linearLayout.getTag(R.id.field_name_id).toString();
				ImageView drawingPreview = null;
				String field_label = linearLayout.getTag(R.id.field_label_id)
						.toString();

				for (int j = 0; j < linearLayout.getChildCount(); j++) {
					Class<?> subClass = (Class<?>) linearLayout.getChildAt(j)
							.getClass();
					if (subClass.getName().equals("android.widget.ImageView")) {

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
				// drawing preview dal mhar tag htae htar ya mhar..
				if(drawingPreview.getTag()!=null)
					map.put(key, drawingPreview.getTag().toString());
				else
					map.put(key, "-");
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("photoLayout")) {
				String key = linearLayout.getTag(R.id.field_name_id).toString();
				ImageView photoPreview = null;
				String field_label = linearLayout.getTag(R.id.field_label_id)
						.toString();

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

						}
					} else if (subClass.getName().equals(
							"android.widget.ImageView")) {
						photoPreview = (ImageView) linearLayout.getChildAt(j);
					}
				}
				if(photoPreview.getTag()!=null)
					map.put(key, photoPreview.getTag().toString());
				else
					map.put(key, "-");
			}
		}
		return map;
	}
	
	private RadioButton getSelectedRadioButtonMyRadioGroup(
			RadioGroup radioGroup) {
		RadioButton selectedButton = null;
		for(int i=0; i<radioGroup.getChildCount(); i++){
			LinearLayout radioButtonLine = (LinearLayout) radioGroup.getChildAt(i);
			for(int z=0; z<radioButtonLine.getChildCount(); z++){
				View view = radioButtonLine.getChildAt(z);
				String className = view.getClass().getName().toString();
				if(className.equals("android.widget.RadioButton")){
					RadioButton radioButton = (RadioButton) view;
					if(radioButton.isChecked()){
						return radioButton;
					}
				}
			}
			/*Object test = radioButtonLine.getTag(R.id.is_radiobutton_selected);
			boolean isSelected = Boolean.parseBoolean(radioButtonLine.getTag(R.id.is_radiobutton_selected).toString());
			if(isSelected){
				for(int j=0; j<radioButtonLine.getChildCount(); j++){
					if(radioButtonLine.getChildAt(j).getClass().getName().equals("android.widget.RadioButton")){
						selectedButton = (RadioButton) radioButtonLine.getChildAt(j);
					}
				}
			}*/
		}
		return selectedButton;
	}

	private void loadUI() {
		getScreenInfo();
		TypeFaceManager tfManager = new TypeFaceManager(this);
		zawGyiTypeFace = tfManager.getZawGyiTypeFace();
		myanmarTypeFace = tfManager.getMyanmar3TypeFace();
		getActionBar().setIcon(R.drawable.home);
		getActionBar().setTitle("Home");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		intent = getIntent();
		jsonReader = new JSONReader(this);
		jsonWriter = new JSONWriter(this);
		dbHelper = new XaveyDBHelper(this);
		currentForm = dbHelper.getFormByFormID(intent.getStringExtra("formID"));
		connectionDetector = new ConnectionDetector(getApplicationContext());
		formFieldsList = jsonReader.getFormFields(currentForm.getForm_json());
	}

	private void getScreenInfo() {
		dm = getResources().getDisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}
	
	private void hideKeyboard(LinearLayout parrentLayout) {
		LinearLayout innerLayout = null;
		int childCount = parrentLayout.getChildCount();
		for(int i=0; i<parrentLayout.getChildCount(); i++){
			String className = parrentLayout.getChildAt(i).getClass().getName().toString();
			if(className.equals("android.widget.LinearLayout")){
				innerLayout = (LinearLayout) parrentLayout.getChildAt(i);
			}
		}
		String innerLayoutID = innerLayout.getTag(R.id.layout_id).toString();
		if(innerLayoutID.equals("datetimeLayout") 
				|| innerLayoutID.equals("radioLayout")
				|| innerLayoutID.equals("checkBoxLayout")
				|| innerLayoutID.equals("locationLayout")
				|| innerLayoutID.equals("drawingLayout")
			|| innerLayoutID.equals("photoLayout")
			|| innerLayoutID.equals("martixOptionSingleLayout")){
				InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
			    // check if no view has focus:
			    View view = this.getCurrentFocus();
			    if (view != null) {
			        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			    }
		}
		
	    
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

}
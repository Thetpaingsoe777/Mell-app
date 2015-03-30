package com.xavey.android;

import java.io.File;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.xavey.android.ApplicationValues.LOGIN_TYPE;
import com.xavey.android.adapter.NumberSetAdapter;
import com.xavey.android.adapter.QuestionPagerAdapter;
import com.xavey.android.adapter.RatingSetAdapter;
import com.xavey.android.adapter.TextSetAdapter;
import com.xavey.android.db.XaveyDBHelper;
import com.xavey.android.layout.CheckboxLayout;
import com.xavey.android.layout.MatrixOptionLayout;
import com.xavey.android.layout.RadioGroupLayout;
import com.xavey.android.model.Document;
import com.xavey.android.model.Form;
import com.xavey.android.model.XMedia;
import com.xavey.android.model.MatrixCell;
import com.xavey.android.util.AudioRecordingManager;
import com.xavey.android.util.ConnectionDetector;
import com.xavey.android.util.GPSTracker;
import com.xavey.android.util.ImageSavingManager;
import com.xavey.android.util.JSONHelper;
import com.xavey.android.util.JSONReader;
import com.xavey.android.util.JSONWriter;
import com.xavey.android.util.LinearLayoutManager;
import com.xavey.android.util.MYHorizontalScrollView;
import com.xavey.android.util.SyncManager;
import com.xavey.android.util.ToastManager;
import com.xavey.android.util.TypeFaceManager;
import com.xavey.android.util.UUIDGenerator;

public class OneQuestionOneView extends FragmentActivity {

	private QuestionPagerAdapter qAdapter; // 19-9-2014
	private final String LEFT_TO_RIGHT = "L_R";
	private final String RIGHT_TO_LEFT = "R_L";
	private boolean isValidating = false;
	int currentPosition = 0;
    LinearLayoutManager lLManager=null;
	LinearLayout currentLayout = null;
	ViewPager vPager;
	// int pageTotal;

	Intent intent;
	JSONReader jsonReader;
	JSONWriter jsonWriter;
	XaveyDBHelper dbHelper;
	Boolean isInternetAvailable = false;
	ConnectionDetector connectionDetector;
	Form currentForm;
	ArrayList<HashMap<String, Object>> formFieldsList;
	ArrayList<HashMap<String, Object>> formRefList;
	HashMap<String, String> Refs;
	ArrayList<LinearLayout> layoutList;

	TypeFaceManager typeface;

	boolean isAllRequiredFieldFilled = true;
	ArrayList<HashMap<String, String>> mediaToSubmit = new ArrayList<HashMap<String, String>>();
	// ArrayList<HashMap<String, String>> audiosToSubmit = new
	// ArrayList<HashMap<String, String>>();

	// DM
	DisplayMetrics dm;
	int screenWidth;
	int screenHeight;
	int previousIndex = 0;

	int jump = 0;
	LinkedList<Integer> navigator = new LinkedList<Integer>();
	LinkedList<String> used_field_ids = new LinkedList<String>();

	String currentDocumentID = "";

	AudioRecordingManager recordingManager;

	GPSTracker gps;
	ToastManager toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.screen_slide);
		loadUI();
		gps = new GPSTracker(getApplicationContext());
		toast = new ToastManager(this);
		currentDocumentID = UUIDGenerator.getUUIDForDocument();
		jsonReader.setCurrentDocumentID(currentDocumentID);
		vPager = (ViewPager) findViewById(R.id.pager);
		try {
			layoutList = jsonReader.readForm2(currentForm);
			layoutList.add(produceSubmitLayout());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * adapterViewPager = new QuestionPagerAdapter(
		 * getSupportFragmentManager(), layoutList);
		 * vPager.setAdapter(adapterViewPager);
		 */

		if (layoutList != null) {
			qAdapter = new QuestionPagerAdapter(getSupportFragmentManager(),
					layoutList);
			vPager.setAdapter(qAdapter);

			vPager.setOnPageChangeListener(new OnPageChangeListener() {
				String fieldRequired;
				String layoutID;
				int index = 0;
				LinearLayout nextLayout = null;
				RelativeLayout currentUpperLayout = null;

				// private int field_id;
				int skipID = 0;
				String direction = "";

				// int previousIndex = 0;

				// just for testing
				String currentLayoutID = "";
				String currentFieldID = "";
				String currentNextRef = "";
				JSONArray currentNextCond;

				ToastManager toast = new ToastManager(OneQuestionOneView.this);

				@Override
				public void onPageSelected(int newPosition) {
					direction = "";
					if (newPosition > currentPosition) {
						// left to right
						direction = LEFT_TO_RIGHT;
					} else if (newPosition < currentPosition) {
						// right to left
						direction = RIGHT_TO_LEFT;
					} else {
						String s = "";
						s.length();
					}
					if (direction == RIGHT_TO_LEFT && !isValidating) {
						// isValidating = false;
						navRightToLeft(newPosition);
						// break from this method
					} else if (direction == LEFT_TO_RIGHT) {
						isValidating = true;
						try {
							validateOnPageSelected(newPosition);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				private void validateOnPageSelected(int newPosition)
						throws Exception {
					int lastJump = -1;
					if (navigator.size() != 0) {
						lastJump = navigator.getLast();
					}
					LinearLayout currentParentLayout = layoutList
							.get(currentPosition);
					for (int i = 0; i < currentParentLayout.getChildCount(); i++) {
						View view = currentParentLayout.getChildAt(i);
						if (view.getClass().getName()
								.equals("android.widget.ScrollView")) {
							ScrollView scroll = (ScrollView) view;
							LinearLayout linearLayout = (LinearLayout) scroll
									.getChildAt(0);
							if (linearLayout.getTag(R.id.layout_id) != null
									&& !linearLayout.getTag(R.id.layout_id)
											.toString()
											.equals("recordingLayout"))
								currentLayout = linearLayout;
						}
						if (view.getClass().getName()
								.equals("android.widget.LinearLayout")) {
							LinearLayout linearLayout = (LinearLayout) currentParentLayout
									.getChildAt(i);
							if (linearLayout.getTag(R.id.layout_id) != null
									&& !linearLayout.getTag(R.id.layout_id)
											.toString()
											.equals("recordingLayout"))
								currentLayout = linearLayout;
						}
						if (view.getClass().getName()
								.equals("android.widget.RelativeLayout")) {
							currentUpperLayout = (RelativeLayout) view;
						}
					}

					nextLayout = layoutList.get(currentPosition);
					LinearLayout nextInnerLayout = getInnerLayout(nextLayout);
					// if next layout is submit or not
					boolean isSubmitLayout = isSubmitLayout(nextLayout);
					if (!isSubmitLayout)
						if (nextInnerLayout.getTag(R.id.layout_id) != null) {
							currentLayoutID = nextInnerLayout.getTag(
									R.id.layout_id).toString();
							currentFieldID = nextInnerLayout.getTag(
									R.id.field_id).toString();
							if (nextInnerLayout.getTag(R.id.next_ref) != null)
								currentNextRef = nextInnerLayout.getTag(
										R.id.next_ref).toString();
							if (nextInnerLayout.getTag(R.id.next_ref_cond) != null)
								currentNextCond = (JSONArray) nextInnerLayout
										.getTag(R.id.next_ref_cond);
						} else {
							for (int i = 0; i < currentNextCond.length(); i++) {
								currentNextCond.remove(i);
							}
						}

					/*
					 * String toast_text = "Current Position : " +
					 * currentPosition + "\n" + "New Position : " + newPosition
					 * + "\n" + "CurrentLayout ID : " + currentLayoutID + "\n" +
					 * "Last Jump Range : " + lastJump;
					 * Toast.makeText(getApplicationContext(), toast_text, 4000)
					 * .show();
					 */

					// boolean isNeedToValid = currentLayout
					// .getTag(R.id.layout_id).toString()
					// .equals("radioLayout");
					boolean isNeedToValid = currentLayout
							.getTag(R.id.layout_id).toString()
							.equals("datetimeLayout");
					isNeedToValid = isNeedToValid
							|| currentLayout.getTag(R.id.layout_id).toString()
									.equals("submitLayout");
					// isNeedToValid = isNeedToValid
					// || currentLayout.getTag(R.id.layout_id).toString()
					// .equals("checkBoxLayout");
					// isNeedToValid = isNeedToValid ||
					// currentLayout.getTag(R.id.layout_id).toString().equals("numberSetLayout");
					// isNeedToValid = isNeedToValid ||
					// currentLayout.getTag(R.id.layout_id).toString().equals("textLayout");
					lLManager = new LinearLayoutManager();

					// LinearLayout currentParentLayout = (LinearLayout)
					// currentLayout.getParent();

					LayoutParams errorMsgLayoutHide = new LayoutParams(
							LayoutParams.MATCH_PARENT, 0);
					errorMsgLayoutHide.setMargins(10, 20, 10, 20);

					// boolean isSkipLogicInvolved =
					// currentLayout.getTag(R.id.next_ref)!=null;
					// boolean isRenderLogicInvolved =
					// currentLayout.getTag(R.id.render_ref)!=null;

					if (!isNeedToValid) {
						HashMap<String, Object> test = null;
						try {
							test = lLManager.test(currentLayout);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String value = "";
						if (test.containsKey("value"))
							value = test.get("value").toString();
						String field_required = "false";
						if (test.containsKey("field_required")) {
							field_required = test.get("field_required")
									.toString();
						}
						String field_label = test.get("field_label").toString();
						String field_error_msg = "";
						if (test.containsKey("field_err_msg"))
							field_error_msg = test.get("field_err_msg")
									.toString();
						String dataset_error_msg = "";
						boolean isNotTyped = field_required.equals("true")
								&& value.equals("#no_value#");
						if (!isNotTyped) {
							// user typed values
							if (ApplicationValues.IS_RECORDING_NOW) {
								// still recording...
								// block..
								// there is no direction validation...
								// bcuz it will block both direction if
								// recording is not ending
								// navigator.addLast(0);
								// used_field_ids.addLast(currentFieldID);
								// vPager.setCurrentItem(currentPosition);
								// errorMsg.setText("Audo recording is need to stop.");
								// errorMsg.setTextColor(Color.RED);
								// errorMsg.setLayoutParams(errorMsgLayoutOpen);
								// currentPosition = previousIndex;
								boolean forceStopL_R = true;
								navStayStill(direction, currentFieldID,
										field_error_msg, lLManager,
										newPosition, currentPosition,
										forceStopL_R);
							}

							String tagID = currentLayout.getTag(R.id.layout_id)
									.toString();
							if (tagID.equals("numberLayout")) {
								String value_ = test.get("value").toString();
								String field_max_value = test.get(
										"field_max_value").toString();
								String field_min_value = test.get(
										"field_min_value").toString();
								// String field_default_value =
								// test.get("field_default_value"); // no need
								// yet
								String field_err_msg = test
										.get("field_err_msg").toString();
								int userTypedValue = Integer.parseInt(value_);
								int maxValue = Integer
										.parseInt(field_max_value);
								int minValue = Integer
										.parseInt(field_min_value);
								if (userTypedValue > maxValue
										|| userTypedValue < minValue) {
									// out of range ..
									// block
									// block for only left to right cuz invalid
									/*
									 * if (direction.equals(LEFT_TO_RIGHT)) { //
									 * int range = //
									 * newPosition-currentPosition;
									 * navigator.addLast(0);
									 * used_field_ids.addLast(currentFieldID);
									 * vPager.setCurrentItem(currentPosition);
									 * errorMsg.setText("" + field_err_msg);
									 * errorMsg.setTextColor(Color.RED); //
									 * errorMsg
									 * .setLayoutParams(errorMsgLayoutOpen);
									 * currentPosition = previousIndex; } else {
									 * // RIGHT_TO_LEFT int last_range = 0; if
									 * (navigator.getLast() != null) last_range
									 * = navigator.getLast(); newPosition =
									 * currentPosition - last_range;
									 * vPager.setCurrentItem(newPosition);
									 * currentPosition = newPosition;
									 * navigator.removeLast();
									 * used_field_ids.removeLast(); }
									 */
									navStayStill(direction, currentFieldID,
											field_error_msg, lLManager,
											newPosition, currentPosition, false);
								} else {
									// in range
									// pass
									// logic testing
									// reverse

									// ------------------------------------------------------
									navLeftToRight(newPosition, currentFieldID);

									// if (errorMsg != null)
									// errorMsg.setText("");
								}
							}
							// ----
							else if (tagID.equals("checkBoxLayout")) {
								// navLeftToRight(newPosition, currentFieldID);
								{
									for (int i = 0; i < currentLayout
											.getChildCount(); i++) {
										String className = currentLayout
												.getChildAt(i).getClass()
												.getName().toString();

										boolean isFieldRequired = Boolean
												.parseBoolean(currentLayout
														.getTag(R.id.field_required_id)
														.toString());
										String field_skip = "";
										boolean isSelectedAnyCheckBox = false;
										String errMessage = "";
										ArrayList<CheckBox> selectedButton = new ArrayList<CheckBox>();
										if (className
												.equals("com.xavey.android.layout.CheckboxLayout")) {
											// radio

											boolean extra_value_required = false;
											boolean isExtraValueTRUE = false;
											boolean isExtraValueTyped = false;

											selectedButton = getSelectedCheckBoxMyCheckboxWrapper(currentLayout);

											isSelectedAnyCheckBox = (selectedButton != null && selectedButton
													.size() > 0) ? true : false;

											String fieldID = currentLayout
													.getTag(R.id.field_id)
													.toString();
											Log.i("fieldID", fieldID);

											boolean forceStopL_R = false;

											// start stay still validation
											if (isFieldRequired
													&& !isSelectedAnyCheckBox) {
												// TODO Get error message from
												// Question
												errMessage = "Please select.";
											}

											// end stay still validation
											if (errMessage.length() > 0) { // block
												navStayStill(direction,
														fieldID, errMessage,
														lLManager, newPosition,
														currentPosition,
														forceStopL_R);
											} else {
												// pass
												ArrayList<String> skips = new ArrayList<String>();
												for (CheckBox thisCB : selectedButton) {
													if (thisCB
															.getTag(R.id.field_skip) != null) {
														skips.add(thisCB
																.getTag(R.id.field_skip)
																.toString());
													}
												}
												if (skips.size() > 0) {
													if (skips.indexOf("") > -1) {
														field_skip = "";
													} else if (skips
															.indexOf("submit") > -1) {
														field_skip = "submit";
													} else {
														field_skip = skips
																.get(0);
													}
												}

												if (field_skip.length() > 0) {
													if (field_skip
															.equals("submit")) {
														// skip to submit
														newPosition = layoutList
																.size() - 1;

														int range = newPosition
																- currentPosition;
														navigator
																.addLast(range);
														used_field_ids
																.addLast(currentFieldID);
														vPager.setCurrentItem(newPosition);
														currentPosition = newPosition;
														previousIndex = currentPosition;
														// hide keyboard
														LinearLayout nextLayout_ = layoutList
																.get(newPosition);
														if (!isSubmitLayout(nextLayout_))
															hideKeyboard(nextLayout_);
														navLeftToRight(
																newPosition,
																currentFieldID);
													} else {

														// skip to other
														// questions
														// logic
														skipID = Integer
																.parseInt(field_skip);
														newPosition = skipID - 1;
														navLeftToRight(
																newPosition,
																currentFieldID);
													}
												} else {
													navLeftToRight(newPosition,
															currentFieldID);
												}
											}

										}
									}
								}
							}
							// ----
							else if (tagID.equals("numberSetLayout")
									|| tagID.equals("textSetLayout")) {

								boolean isValid = true;

								boolean isFieldRequired = Boolean
										.parseBoolean(test
												.get("field_required")
												.toString());
								if (isFieldRequired) {
									boolean isAllFieldsFilled = true;
									ArrayList<HashMap<String, String>> data = (ArrayList<HashMap<String, String>>) test
											.get("data");
									String testValue = test.get("value")
											.toString();
									ArrayList<String> missingLabels = (ArrayList<String>) test
											.get("missing_labels");
									if (testValue.equals(null)
											|| testValue.length() == 0) {
										isAllFieldsFilled = false;
										String allMissingLabels = "";
										for (String missingLabel : missingLabels) {
											allMissingLabels += ", "
													+ missingLabel;
										}
										allMissingLabels = allMissingLabels
												.substring(2);
										// toast.xaveyToast(null,
										// allMissingLabels
										// + " are missing.");
									}
									// check here
									if (!isAllFieldsFilled) {
										isValid = false;
									} else {
										// check if within range or not
										// and then check the max and min
										String layoutID = test.get("layout_id")
												.toString();
										if (layoutID.equals("numberSetLayout")) {
											int minValue = 0;
											if (test.containsKey("field_min_value"))
												minValue = Integer
														.parseInt(test
																.get("field_min_value")
																.toString());
											int maxValue = 0;
											if (test.containsKey("field_max_value"))
												maxValue = Integer
														.parseInt(test
																.get("field_max_value")
																.toString());

											int total = Integer.parseInt(test
													.get("total").toString());

											if (maxValue != 0)
												if (total > maxValue
														|| total < minValue) {
													isValid = false;

													toast.xaveyToast(
															null,
															"Total value must be less than "
																	+ maxValue
																	+ "\nand greater than "
																	+ minValue);
													// TODO:Check whether can
													// migrate to navStayStill
													// errorMsg.setText(field_error_msg);
												}

										}
									}
								}

								if (!isValid) {
									navStayStill(direction, currentFieldID,
											field_error_msg, lLManager,
											newPosition, currentPosition, false);
								} else {
									// pass // LEFT to RIGHT
									navLeftToRight(newPosition, currentFieldID);

								}
							}
							// latested updated
							// I moved the code here since it needs validation
							// radio start
							else if (tagID.equals("radioLayout")) {
								for (int i = 0; i < currentLayout
										.getChildCount(); i++) {
									String className = currentLayout
											.getChildAt(i).getClass().getName()
											.toString();

									boolean isFieldRequired = Boolean
											.parseBoolean(currentLayout.getTag(
													R.id.field_required_id)
													.toString());
									boolean isSelectedAnyRadio = false;
									String errMessage = "";
									if (className
											.equals("com.xavey.android.layout.RadioGroupLayout")) {
										// radio

										boolean extra_value_required = false;
										boolean isExtraValueTRUE = false;
										boolean isExtraValueTyped = false;

										RadioGroup radioGroup = (RadioGroup) currentLayout
												.getChildAt(i);

										RadioButton selectedButton = getSelectedRadioButtonMyRadioGroup(radioGroup);

										isSelectedAnyRadio = selectedButton != null ? true
												: false;

										if (isSelectedAnyRadio
												&& selectedButton
														.getTag(R.id.extra) != null) {
											// Extra Value
											isExtraValueTRUE = Boolean
													.parseBoolean(selectedButton
															.getTag(R.id.extra)
															.toString());
											if (selectedButton
													.getTag(R.id.extra_required) != null) {
												extra_value_required = Boolean
														.parseBoolean(selectedButton
																.getTag(R.id.extra_required)
																.toString());
											}
										}

										String fieldID = currentLayout.getTag(
												R.id.field_id).toString();
										Log.i("fieldID", fieldID);

										boolean forceStopL_R = false;

										String field_skip = "";
										if (selectedButton != null
												&& selectedButton
														.getTag(R.id.field_skip) != null)
											field_skip = selectedButton.getTag(
													R.id.field_skip).toString();
										if (extra_value_required) {
											LinearLayout selectedButtonLine = (LinearLayout) selectedButton
													.getParent();
											for (int k = 0; k < selectedButtonLine
													.getChildCount(); k++) {
												View buttonLineChild = selectedButtonLine
														.getChildAt(k);
												if (buttonLineChild
														.getClass()
														.getName()
														.equals("android.widget.EditText")) {
													EditText selectedExtra = (EditText) buttonLineChild;
													if (selectedExtra.getText()
															.toString()
															.length() > 0)
														isExtraValueTyped = true;
												}
											}
										}

										// start stay still validation
										if (isFieldRequired
												&& !isSelectedAnyRadio) {
											// TODO Get error message from
											// Question
											errMessage = "Required Parent.";
										}

										if (isExtraValueTRUE
												&& extra_value_required) {
											if (!isExtraValueTyped) {
												errMessage = "Required Child Extra";
											}
										}
										if (ApplicationValues.IS_RECORDING_NOW) {
											forceStopL_R = true;
											/*
											 * navStayStill( direction,
											 * currentFieldID,
											 */
											errMessage = "Audio recording is needed to stop.";
											/*
											 * lLManager, newPosition,
											 * currentPosition, forceStopL_R);
											 */
										}
										// end stay still validation

										if (errMessage.length() > 0) { // block
											navStayStill(direction, fieldID,
													errMessage, lLManager,
													newPosition,
													currentPosition,
													forceStopL_R);
										} else {
											// pass
											if (field_skip.length() > 0) {
												if (field_skip.equals("submit")) {
													// skip to submit
													newPosition = layoutList
															.size() - 1;

													int range = newPosition
															- currentPosition;
													navigator.addLast(range);
													used_field_ids
															.addLast(currentFieldID);
													vPager.setCurrentItem(newPosition);
													currentPosition = newPosition;
													previousIndex = currentPosition;
													// hide keyboard
													LinearLayout nextLayout_ = layoutList
															.get(newPosition);
													if (!isSubmitLayout(nextLayout_))
														hideKeyboard(nextLayout_);
													navLeftToRight(newPosition,
															currentFieldID);
												} else {

													// skip to other questions
													// logic
													skipID = Integer
															.parseInt(field_skip);
													newPosition = skipID - 1;
													navLeftToRight(newPosition,
															currentFieldID);
												}
											} else {
												navLeftToRight(newPosition,
														currentFieldID);
											}
										}

									}
								}
							}
							// radio checkboxes ends
							else if (tagID.equals("matrixCheckListLayout")
									|| tagID.equals("matrixOptionLayout")) {

								boolean isValid = true;

								boolean isFieldRequired = Boolean
										.parseBoolean(test
												.get("field_required")
												.toString());

								boolean isSelectedCountValid = true;
								boolean whileController = true;

								ArrayList<String> selectedValueList = new ArrayList<String>();

								// if (isFieldRequired) {
								for (int i = 0; i < currentLayout
										.getChildCount(); i++) {
									View child_ = currentLayout.getChildAt(i);
									if (child_.getTag(R.id.layout_id) != null
											&& child_.getTag(R.id.layout_id)
													.toString()
													.equals("theMatrixLayout")) {
										LinearLayout theMatrixLayout = (LinearLayout) child_;
										theMatrixLayout.getChildCount();
										for (int j = 0; j < theMatrixLayout
												.getChildCount(); j++) {
											View matrix_child = theMatrixLayout
													.getChildAt(j);
											if (matrix_child
													.getClass()
													.getName()
													.equals("com.xavey.android.util.MYHorizontalScrollView")) {
												MYHorizontalScrollView scrollView = (MYHorizontalScrollView) matrix_child;
												for (int k = 0; k < scrollView
														.getChildCount(); k++) {
													View scroll_child = scrollView
															.getChildAt(k);
													if (scroll_child
															.getTag(R.id.layout_id)
															.toString()
															.equals("AllColumns")) {
														LinearLayout AllColumns = (LinearLayout) scroll_child;

														while (whileController) {
															for (int l = 0; l < AllColumns
																	.getChildCount(); l++) {
																View AllColumnsChild = AllColumns
																		.getChildAt(l);
																if (AllColumnsChild
																		.getTag(R.id.layout_id)
																		.toString()
																		.equals("columnLayout")) {
																	LinearLayout columnLayout = (LinearLayout) AllColumnsChild;
																	int selectedCount = 0;
																	String maxRangeString = columnLayout
																			.getTag(R.id.dataset_max_range)
																			.toString();
																	int maxRange = 100; // :TODO
																						// to
																						// get
																						// value
																						// from
																						// JSON
																	if (maxRangeString
																			.equals("#no_value#")) {
																		Toast.makeText(
																				getApplicationContext(),
																				"error : ",
																				Toast.LENGTH_LONG);
																	} else
																		maxRange = Integer
																				.parseInt(maxRangeString); // <--
																											// will
																											// get
																											// from
																											// tag
																	String columnErorMsg = columnLayout
																			.getTag(R.id.dataset_error_message)
																			.toString(); // <--
																							// will
																							// get
																							// from
																							// tag

																	selectedValueList
																			.size();
																	for (int m = 0; m < columnLayout
																			.getChildCount(); m++) {
																		View eachColumnChild = columnLayout
																				.getChildAt(m);
																		if (eachColumnChild
																				.getTag(R.id.layout_id)
																				.toString()
																				.equals("cell")) {
																			LinearLayout cell = (LinearLayout) eachColumnChild;
																			for (int n = 0; n < cell
																					.getChildCount(); n++) {
																				View cellChild = cell
																						.getChildAt(n);
																				if (cellChild
																						.getClass()
																						.getName()
																						.equals("android.widget.RadioButton")) {
																					RadioButton radioBtn = (RadioButton) cellChild;
																					if (radioBtn
																							.isChecked()) {
																						selectedCount++;
																						MatrixCell selectedCell = (MatrixCell) radioBtn
																								.getTag(R.id.matrix_cell);
																						selectedValueList
																								.add(selectedCell
																										.getFieldSkip());
																					}
																				}
																			}
																		}
																	}

																	if (selectedCount > maxRange) {
																		isSelectedCountValid = false;
																		dataset_error_msg = columnErorMsg;
																		break;
																	}
																}
															}
															whileController = false;
														}
													}
												}
											}
										}
									}
								}
								// }

								boolean shouldSkip = false;
								if (selectedValueList.indexOf("") <= -1) {
									shouldSkip = true;
								}
								/*
								 * for (String selectedSingleValue :
								 * selectedValueList) { if
								 * (selectedSingleValue.equals("")) { // <- //
								 * blank // string // means // no // skip
								 * shouldSkip &= false; } }
								 */

								if (!isValid || !isSelectedCountValid) {
									// block

									navStayStill(direction, currentFieldID,
											field_error_msg, lLManager,
											newPosition, currentPosition, false);
								} else {
									// pass

									if (shouldSkip) {
										// skip to submit
										newPosition = layoutList.size() - 1;

										int range = newPosition
												- currentPosition;
										navigator.addLast(range);
										used_field_ids.addLast(currentFieldID);
										vPager.setCurrentItem(newPosition);
										currentPosition = newPosition;
										previousIndex = currentPosition;
										// hide keyboard
										LinearLayout nextLayout_ = layoutList
												.get(newPosition);
										if (!isSubmitLayout(nextLayout_))
											hideKeyboard(nextLayout_);
									}
									navLeftToRight(newPosition, currentFieldID);
								}
							}

							else {
								navLeftToRight(newPosition, currentFieldID);
							}
						} else {

							navStayStill(direction, currentFieldID,
									field_error_msg, lLManager, newPosition,
									currentPosition, false);
						}
					} else {
						// pass all
						navLeftToRight(newPosition, currentFieldID);
					}

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

		// here

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
			hashMap.put("media_path", imagePath);
			hashMap.put("media_type", "image");
			hashMap.put("media_id", "");
			mediaToSubmit.add(hashMap);

			for (int i = 0; i < layoutList.size() - 1; i++) {
				LinearLayout parentLayout = (LinearLayout) layoutList.get(i);
				LinearLayout childLayout = null;
				for (int z = 0; z < parentLayout.getChildCount(); z++) {
					View view = parentLayout.getChildAt(z);
					if (view.getClass().getName()
							.equals("android.widget.LinearLayout")
							&& view.getTag(R.id.layout_id) != null) {
						childLayout = (LinearLayout) view;
						break;
					} else if (view.getClass().getName()
							.equals("android.widget.ScrollView")) {
						ScrollView sv = (ScrollView) view;
						childLayout = (LinearLayout) sv.getChildAt(0);
						break;
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
			hashMap.put("media_path", imagePath);
			hashMap.put("media_type", "image");
			mediaToSubmit.add(hashMap);

			for (int i = 0; i < layoutList.size() - 1; i++) {
				LinearLayout parentLayout = (LinearLayout) layoutList.get(i);
				LinearLayout childLayout = null;
				for (int z = 0; z < parentLayout.getChildCount(); z++) {
					View view = parentLayout.getChildAt(z);
					if (view.getClass().getName()
							.equals("android.widget.LinearLayout")
							&& view.getTag(R.id.layout_id) != null) {
						childLayout = (LinearLayout) view;
						break;
					} else if (view.getClass().getName()
							.equals("android.widget.ScrollView")) {
						ScrollView sv = (ScrollView) view;
						childLayout = (LinearLayout) sv.getChildAt(0);
						break;
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
			hashMap.put("media_path", imagePath);
			mediaToSubmit.add(hashMap);

			for (int i = 0; i < layoutList.size() - 1; i++) {
				LinearLayout parentLayout = (LinearLayout) layoutList.get(i);
				LinearLayout childLayout = null;
				for (int z = 0; z < parentLayout.getChildCount(); z++) {
					View view = parentLayout.getChildAt(z);
					if (view.getClass().getName()
							.equals("android.widget.LinearLayout")
							&& view.getTag(R.id.layout_id) != null) {
						childLayout = (LinearLayout) view;
						break;
					} else if (view.getClass().getName()
							.equals("android.widget.ScrollView")) {
						ScrollView sv = (ScrollView) view;
						childLayout = (LinearLayout) sv.getChildAt(0);
						break;
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
		// there is no inner layout in SubmitLayout

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
				// testing...
				// toast.xaveyToast(null, "lat: "+ gps.getLatitude()
				// +"\nlong: "+gps.getLongitude());

				Document document = new Document();
				ArrayList<LinearLayout> completeList = getCompleteList(
						layoutList, used_field_ids);
				HashMap<String, Object> incompleteMap = getValuesFromEachLayout(
						completeList, 0, completeList.size(), false);

				JSONObject document_json = new JSONObject();
				JSONArray jsonArray = new JSONArray();
				// notes...
				// formFieldsList က form �?စ်�?ုလုံးရဲ့ structure မှာပါ�?ဲ့
				// မှန်သမျှကိုယူထား�?ာ..
				// အဲဒီ့�?ော့ user skip လုပ်လိုက်�?ဲ့ ဖီးလ်�?ွေပါ ပါလာမှာ...
				// အဲဒါကြောင့် incompleteMap ထဲမှာ မပါရင် မယူဘူးဆိုပြီး filter
				// �?စ်�?ု�?ံထားလိုက်�?ာ...
				for (int i = 0; i < formFieldsList.size(); i++) {
					if (formFieldsList.get(i).size() > 0) {
						HashMap<String, Object> map = formFieldsList.get(i);
						String fieldID = map.get("field_id").toString();
						String fieldLabel = map.get("field_label").toString();
						String fieldName = map.get("field_name").toString();
						String fieldValueAudio = "";
						boolean is_audio_required = false;
						if (map.containsKey("field_audio_required")) {
							is_audio_required = Boolean.parseBoolean(map.get(
									"field_audio_required").toString());
						}

						// if (is_audio_required) {
						// String audio_file = recordingManager
						// .getFilename(fieldName + " - "
						// + currentDocumentID);
						// File file = new File(audio_file);
						// if (file.exists()) {
						// fieldValueAudio = audio_file;
						// }
						// }

						String userTypedValue = "";
						if (incompleteMap.containsKey(fieldID)) { // <--filter
							userTypedValue = incompleteMap.get(fieldID)
									.toString();
							try {
								JSONObject child = new JSONObject();
								child.put("field_id", fieldID);
								child.put("field_name", fieldName);
								child.put("field_value", userTypedValue);
								child.put("field_label", fieldLabel);

								String audioPath = "";
								String mp4FileName = fieldID + "-"
										+ currentDocumentID + ".mp4";

								File file = new File(
										ApplicationValues.XAVEY_DIRECTORY,
										"AudioRecorder");
								if (file.exists()) {
									audioPath = file.getAbsolutePath() + "/"
											+ mp4FileName;
									File audioFile = new File(audioPath);
									if (audioFile.exists()) {
										child.put("field_audio", audioFile);
									}
								}

								// if (fieldValueAudio.length() > 0) {
								// child.put("field_value_audio",
								// fieldValueAudio);
								// }

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
					document.setDocument_id(currentDocumentID);
					document.setDocument_name(currentForm.getForm_title());
                    String currentDateTime = getCurrentDateTime();
					document.setCreated_at(getCurrentDateTime());
					document.setDocument_json(document_json.toString());
					document.setForm_id(currentForm.getForm_id());
					document.setCreated_worker(ApplicationValues.loginUser.getUser_id());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// just put this outside....
				// whatever internet's avaliable or not.., it will store in
				// local first
				// ---------------------------------------------------------

				// offline mode
				document.setSubmitted("0");

				// fill the audios to audiosToSubmit here
				for (int j = 0; j < jsonArray.length(); j++) {
					try {
						JSONObject obj = jsonArray.getJSONObject(j);
						if (obj.has("field_audio")) {
							String audioPath = obj.getString("field_audio");
							String fieldID = obj.getString("field_id");
							String fieldName = obj.getString("field_name");
							String documentID = document.getDocument_id();
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("media_path", audioPath);
							map.put("field_id", fieldID);
							map.put("field_name", fieldName);
							map.put("document_id", documentID);
							map.put("media_type", "audio");
							mediaToSubmit.add(map);
                        }
					} catch (JSONException e) {
						//
					}
				}

				// save image too.
				String docID = document.getDocument_id();
				for (HashMap<String, String> image_map : mediaToSubmit) {
					String media_name = image_map.get("field_name");
					String media_path = image_map.get("media_path");
					XMedia media = new XMedia();
					media.setMedia_id("media_id");
					media.setDoc_id(docID);
					media.setMedia_name(media_name);
					media.setMedia_path(media_path);
					media.setMedia_type(image_map.get("media_type"));
					dbHelper.addNewMedia(media);
				}

				/*
				 * // save audio for (HashMap<String, String> audio_map :
				 * audiosToSubmit) { String media_name =
				 * audio_map.get("field_name"); String media_path =
				 * audio_map.get("audio_path"); XMedia media = new XMedia();
				 * media.setDoc_id(docID); media.setMedia_name(media_name);
				 * media.setMedia_path(media_path);
				 * media.setMedia_type("audio"); dbHelper.addNewMedia(media); }
				 */

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

				// ---------------------------------------------------------

				// -----
				isInternetAvailable = connectionDetector
						.isConnectingToInternet();
				if (isInternetAvailable
						&& ApplicationValues.CURRENT_LOGIN_MODE
								.equals(LOGIN_TYPE.REGULAR_LOGIN)) {
					try {
						syncManager = new SyncManager(OneQuestionOneView.this);
						if (mediaToSubmit.size() > 0) {
							syncManager.submitDocument2(document, currentForm,
									mediaToSubmit); // <--
						} else {
							syncManager.submitDocument(document, currentForm);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					// -------------------------------------------------------------------
					/*
					 * // offline mode document.setSubmitted("0"); // save image
					 * too. String docID = document.getDocument_id(); for
					 * (HashMap<String, String> image_map : imagesToSubmit) {
					 * String image_name = image_map.get("field_name"); String
					 * image_path = image_map.get("imagePath"); Image image =
					 * new Image(); image.setDoc_id(docID);
					 * image.setImage_name(image_name);
					 * image.setImage_path(image_path);
					 * dbHelper.addNewImage(image); } SyncManager syncManager =
					 * new SyncManager( OneQuestionOneView.this);
					 * 
					 * // document_json(structure) is needed everytime before //
					 * upload try { JSONArray mainArray =
					 * jsonReader.getJSONArrayToSubmit( document, currentForm);
					 * // here.. may be shock
					 * document.setDocument_json_to_submit(mainArray
					 * .getJSONObject(0).toString()); // following line won't be
					 * needed , but not sure..., fix // later
					 * document.setSubmitted("0"); } catch (JSONException e) {
					 * e.printStackTrace(); }
					 */
					// -----------------------------------------------------------
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
			LinkedList<String> used_field_ids) {
		LinkedList<String> noDupField = removeDups(used_field_ids);
		ArrayList<LinearLayout> completeList = new ArrayList<LinearLayout>();
		for (LinearLayout linearLayout : layoutList) {
			String field_id = new LinearLayoutManager()
					.getFieldIDFromLayout(linearLayout);
			if (noDupField.contains(field_id))
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
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
    }

	private HashMap<String, Object> getValuesFromEachLayout(
			ArrayList<LinearLayout> layoutList, int startPoint, int endPoint,
			boolean needRawValue) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (int i = startPoint; i < endPoint; i++) {
			// -1 don't care the last room cuz the last room is submitLayout

			LinearLayout parentLayout = (LinearLayout) layoutList.get(i);
			LinearLayout linearLayout = null;
			for (int p = 0; p < parentLayout.getChildCount(); p++) {
				View child = parentLayout.getChildAt(p);
				if (child.getClass().getName()
						.equals("android.widget.ScrollView")) {
					ScrollView scrollView = (ScrollView) child;
					for (int a = 0; a < scrollView.getChildCount(); a++) {
						View scrollChild = scrollView.getChildAt(a);
						if (scrollChild.getTag(R.id.layout_id) != null
								&& scrollChild.getClass().getName()
										.equals("android.widget.LinearLayout")) {
							if (!scrollChild.getTag(R.id.layout_id).toString()
									.equals("recordingLayout")) {
								linearLayout = (LinearLayout) scrollChild;
							}
						}
					}
				} else if (child.getClass().getName()
						.equals("android.widget.LinearLayout")) {
					LinearLayout linearLayout_ = (LinearLayout) child;
					if (linearLayout_.getTag(R.id.layout_id) != null
							&& !linearLayout_.getTag(R.id.layout_id).toString()
									.equals("recordingLayout")) {
						linearLayout = linearLayout_;
					}
				}
			}
			parentLayout.getChildAt(1);

			if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("textLayout")) {
				String key = linearLayout.getTag(R.id.field_id).toString();
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

						key = linearLayout.getTag(R.id.field_id).toString();
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
				String key = linearLayout.getTag(R.id.field_id).toString();
				for (int y = 0; y < linearLayout.getChildCount(); y++) {

					Class<?> subClass = (Class<?>) linearLayout.getChildAt(y)
							.getClass();

					if (subClass.getName().equals(
							"com.xavey.android.layout.RadioGroupLayout")) {
						// radio
						RadioGroup radioGroup = (RadioGroup) linearLayout
								.getChildAt(y);
						RadioButton selectedButton = getSelectedRadioButtonMyRadioGroup(radioGroup);
						String value = "";
						if (selectedButton != null) {
							value = selectedButton.getTag(R.id.radio_value)
									.toString();
						}

						for (int u = 0; u < radioGroup.getChildCount(); u++) {
							LinearLayout layoutLine = (LinearLayout) radioGroup
									.getChildAt(u);
							RadioButton childButton = null;
							EditText childEditText = null;
							for (int v = 0; v < layoutLine.getChildCount(); v++) {
								String childClassName = layoutLine
										.getChildAt(v).getClass().getName()
										.toString();
								if (childClassName
										.equals("android.widget.RadioButton")) {
									childButton = (RadioButton) layoutLine
											.getChildAt(v);
								} else if (childClassName
										.equals("android.widget.EditText")) {
									childEditText = (EditText) layoutLine
											.getChildAt(v);
								}
							}
							String radio_value = childButton.getTag(
									R.id.radio_value).toString();
							if (radio_value.equals(value)) {
								if (childEditText.getText().toString().length() > 0)
									value = value
											+ ":"
											+ childEditText.getText()
													.toString();
							}
						}
						map.put(key, value);
					}
					// finding recorded wav tag
					/*
					 * LinearLayout radioParentLayout = (LinearLayout)
					 * linearLayout.getParent().getParent(); for(int rp=0;
					 * rp<radioParentLayout.getChildCount(); rp++){ View
					 * radioParentChild = radioParentLayout.getChildAt(rp);
					 * String rpChildTag = "";
					 * if(radioParentChild.getTag(R.id.layout_id)!=null){
					 * rpChildTag =
					 * radioParentChild.getTag(R.id.layout_id).toString(); }
					 * if(rpChildTag.equals("recordingLayout")){ LinearLayout
					 * recordingLayout = (LinearLayout) radioParentChild; String
					 * wav_path =
					 * recordingLayout.getTag(R.id.audio_path).toString(); } }
					 */
				}
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("checkBoxLayout")) {
				// JSONArray checkedValues = new JSONArray();
				String checkedValues = "";
				String key = linearLayout.getTag(R.id.field_id).toString();
				String field_label = linearLayout.getTag(R.id.field_label_id)
						.toString();

				LinearLayout checkBoxLayout = linearLayout;
				for (int c = 0; c < checkBoxLayout.getChildCount(); c++) {
					CheckboxLayout checkBoxWrapper = null;
					View cbLayoutChild = checkBoxLayout.getChildAt(c);
					if (cbLayoutChild.getClass().getName()
							.equals("com.xavey.android.layout.CheckboxLayout")) {

						checkBoxWrapper = (CheckboxLayout) checkBoxLayout
								.getChildAt(c);
						for (int d = 0; d < checkBoxWrapper.getChildCount(); d++) {
							LinearLayout checkBoxLine = null;
							View cbLineLayoutChild = checkBoxWrapper
									.getChildAt(d);
							if (cbLineLayoutChild.getClass().getName()
									.equals("android.widget.LinearLayout")) {
								checkBoxLine = (LinearLayout) checkBoxWrapper
										.getChildAt(d);
								CheckBox cb = getCheckBoxFromCheckBoxLine(checkBoxLine);
								EditText extra = getExtraFromCheckBoxLine(checkBoxLine);
								if (cb.isChecked()) {
									String value = cb.getTag(
											R.id.checkbox_value).toString();
									// checkedValues.put(value);
									if (extra != null
											&& extra.getText().toString()
													.length() > 0) {
										value += ":"
												+ extra.getText().toString();
									}
									checkedValues += "|" + value;
								}

							}

						}

					}

				}

				if (checkedValues.length() > 0)
					checkedValues = checkedValues.substring(1); // <- it deletes
																// the 1st char
																// of the String
																// ( | )
				else
					checkedValues = "-";
				map.put(key, checkedValues);
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("locationLayout")) {
				double latitude = 0.0;
				double longitude = 0.0;

				String key = linearLayout.getTag(R.id.field_id).toString();
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
				String key = linearLayout.getTag(R.id.field_id).toString();
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
				String key = linearLayout.getTag(R.id.field_id).toString();
				ImageView drawingPreview = null;
				String field_label = linearLayout.getTag(R.id.field_label_id)
						.toString();

				for (int j = 0; j < linearLayout.getChildCount(); j++) {
					Class<?> subClass = (Class<?>) linearLayout.getChildAt(j)
							.getClass();
					if (subClass.getName().equals("android.widget.ImageView")) {
						// ဒီ method ထဲမှာဘာမှမလုပ်ဘူး... ဒီ small imageview က
						// preview ပဲပြထား�?ာ... �?ကယ့် path က သယ်လာပြီးသား....
						// validation ပဲလုပ်�?ာ..
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
				if (drawingPreview.getTag() != null)
					map.put(key, drawingPreview.getTag().toString());
				else
					map.put(key, "-");
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("photoLayout")) {
				String key = linearLayout.getTag(R.id.field_id).toString();
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
				if (photoPreview.getTag() != null)
					map.put(key, photoPreview.getTag().toString());
				else
					map.put(key, "-");
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("matrixTextLayout")) {
				String key = linearLayout.getTag(R.id.field_id).toString();
				String inputValues = "";
				// ArrayList<MatrixCell> selectedCellList = new
				// ArrayList<MatrixCell>();
				key.toCharArray();
				for (int x = 0; x < linearLayout.getChildCount(); x++) {
					View view = linearLayout.getChildAt(x);
					if (view.getClass().getName()
							.equals("android.widget.LinearLayout")
							&& view.getTag(R.id.layout_id).toString()
									.equals("theMatrixLayout")) {
						LinearLayout theMatrixLayout = (LinearLayout) view;
						LinearLayout rowLabelColumn = null;
						MYHorizontalScrollView horizontalScrollView = null;
						for (int y = 0; y < theMatrixLayout.getChildCount(); y++) {
							View v = theMatrixLayout.getChildAt(y);

							if (v.getTag(R.id.layout_id).toString()
									.equals("rowLabelColumn")) {
								rowLabelColumn = (LinearLayout) v;
							} else if (v.getTag(R.id.layout_id).toString()
									.equals("horizontalScrollView")) {
								horizontalScrollView = (MYHorizontalScrollView) v;
							}
						}
						// both rowLabelColumn and horizontalScrollView must be
						// already assigned in this line
						if (((LinearLayout) horizontalScrollView.getChildAt(0))
								.getTag(R.id.layout_id).toString()
								.equals("AllColumns")) {
							LinearLayout AllColumns = (LinearLayout) horizontalScrollView
									.getChildAt(0);
							for (int ac = 0; ac < AllColumns.getChildCount(); ac++) {
								LinearLayout singleColumnAt_ac = (LinearLayout) AllColumns
										.getChildAt(ac);
								String columnTitle = "";
								for (int sc = 0; sc < singleColumnAt_ac
										.getChildCount(); sc++) {
									View singleNumberBoxLayout = singleColumnAt_ac
											.getChildAt(sc);
									String cellTag = singleNumberBoxLayout
											.getTag(R.id.layout_id).toString();
									if (cellTag.equals("columnTitle")) {
										columnTitle = ((TextView) singleNumberBoxLayout)
												.getText().toString();
									} else if (cellTag.equals("cell")) {
										LinearLayout cellLayout = (LinearLayout) singleNumberBoxLayout;
										for (int c = 0; c < cellLayout
												.getChildCount(); c++) {
											if (cellLayout
													.getChildAt(c)
													.getClass()
													.getName()
													.toString()
													.equals("android.widget.EditText")) {
												EditText tb = (EditText) cellLayout
														.getChildAt(c);
												if (tb.getText().toString()
														.length() > 0) {
													MatrixCell cell = (MatrixCell) tb
															.getTag(R.id.matrix_cell);
													inputValues += "|"
															/*
															 * + "h" +
															 * cell.getH_index()
															 * + "" + "v" +
															 * cell.getV_index()
															 */
															+ cell.getValue()
															+ ":"
															+ tb.getText()
																	.toString(); // <-h0v0
												}
											}
										}
									}
								}
							}
						}
						// -------------
						if (inputValues.length() > 0)
							inputValues = inputValues.substring(1); // <- it
																	// deletes
																	// the
																	// 1st
																	// char
																	// of
																	// the
																	// String
						else
							inputValues = "-";
					}
				}
				map.put(key, inputValues);
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("matrixNumberLayout")) {
				String key = linearLayout.getTag(R.id.field_id).toString();
				String inputValues = "";
				// ArrayList<MatrixCell> selectedCellList = new
				// ArrayList<MatrixCell>();
				key.toCharArray();
				for (int x = 0; x < linearLayout.getChildCount(); x++) {
					View view = linearLayout.getChildAt(x);
					if (view.getClass().getName()
							.equals("android.widget.LinearLayout")
							&& view.getTag(R.id.layout_id).toString()
									.equals("theMatrixLayout")) {
						LinearLayout theMatrixLayout = (LinearLayout) view;
						LinearLayout rowLabelColumn = null;
						MYHorizontalScrollView horizontalScrollView = null;
						for (int y = 0; y < theMatrixLayout.getChildCount(); y++) {
							View v = theMatrixLayout.getChildAt(y);

							if (v.getTag(R.id.layout_id).toString()
									.equals("rowLabelColumn")) {
								rowLabelColumn = (LinearLayout) v;
							} else if (v.getTag(R.id.layout_id).toString()
									.equals("horizontalScrollView")) {
								horizontalScrollView = (MYHorizontalScrollView) v;
							}
						}
						// both rowLabelColumn and horizontalScrollView must be
						// already assigned in this line
						if (((LinearLayout) horizontalScrollView.getChildAt(0))
								.getTag(R.id.layout_id).toString()
								.equals("AllColumns")) {
							LinearLayout AllColumns = (LinearLayout) horizontalScrollView
									.getChildAt(0);
							for (int ac = 0; ac < AllColumns.getChildCount(); ac++) {
								LinearLayout singleColumnAt_ac = (LinearLayout) AllColumns
										.getChildAt(ac);
								String columnTitle = "";
								for (int sc = 0; sc < singleColumnAt_ac
										.getChildCount(); sc++) {
									View singleNumberBoxLayout = singleColumnAt_ac
											.getChildAt(sc);
									String cellTag = singleNumberBoxLayout
											.getTag(R.id.layout_id).toString();
									if (cellTag.equals("columnTitle")) {
										columnTitle = ((TextView) singleNumberBoxLayout)
												.getText().toString();
									} else if (cellTag.equals("cell")) {
										LinearLayout cellLayout = (LinearLayout) singleNumberBoxLayout;
										for (int c = 0; c < cellLayout
												.getChildCount(); c++) {
											if (cellLayout
													.getChildAt(c)
													.getClass()
													.getName()
													.toString()
													.equals("android.widget.EditText")) {
												EditText tb = (EditText) cellLayout
														.getChildAt(c);
												if (tb.getText().toString()
														.length() > 0) {
													MatrixCell cell = (MatrixCell) tb
															.getTag(R.id.matrix_cell);
													inputValues += "|"
															/*
															 * + "h" +
															 * cell.getH_index()
															 * + "" + "v" +
															 * cell.getV_index()
															 */
															+ cell.getValue()
															+ ":"
															+ tb.getText()
																	.toString();
												}
											}
										}
									}
								}
							}
						}
						// -------------
						if (inputValues.length() > 0)
							inputValues = inputValues.substring(1); // <- it
																	// deletes
																	// the
																	// 1st
																	// char
																	// of
																	// the
																	// String
						else
							inputValues = "-";
					}
				}
				map.put(key, inputValues);
			} else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("matrixCheckListLayout")) {
				String key = linearLayout.getTag(R.id.field_id).toString();
				String checkedValues = "";
				// ArrayList<MatrixCell> selectedCellList = new
				// ArrayList<MatrixCell>();
				key.toCharArray();
				for (int x = 0; x < linearLayout.getChildCount(); x++) {
					View view = linearLayout.getChildAt(x);
					if (view.getClass().getName()
							.equals("android.widget.LinearLayout")
							&& view.getTag(R.id.layout_id).toString()
									.equals("theMatrixLayout")) {
						LinearLayout theMatrixLayout = (LinearLayout) view;
						LinearLayout rowLabelColumn = null;
						MYHorizontalScrollView horizontalScrollView = null;
						for (int y = 0; y < theMatrixLayout.getChildCount(); y++) {
							View v = theMatrixLayout.getChildAt(y);

							if (v.getTag(R.id.layout_id).toString()
									.equals("rowLabelColumn")) {
								rowLabelColumn = (LinearLayout) v;
							} else if (v.getTag(R.id.layout_id).toString()
									.equals("horizontalScrollView")) {
								horizontalScrollView = (MYHorizontalScrollView) v;
							}
						}
						// both rowLabelColumn and horizontalScrollView must be
						// already assigned in this line
						if (((LinearLayout) horizontalScrollView.getChildAt(0))
								.getTag(R.id.layout_id).toString()
								.equals("AllColumns")) {
							LinearLayout AllColumns = (LinearLayout) horizontalScrollView
									.getChildAt(0);
							for (int ac = 0; ac < AllColumns.getChildCount(); ac++) {
								LinearLayout singleColumnAt_ac = (LinearLayout) AllColumns
										.getChildAt(ac);
								String columnTitle = "";
								for (int sc = 0; sc < singleColumnAt_ac
										.getChildCount(); sc++) {
									View singleCheckBoxLayout = singleColumnAt_ac
											.getChildAt(sc);
									String cellTag = singleCheckBoxLayout
											.getTag(R.id.layout_id).toString();
									if (cellTag.equals("columnTitle")) {
										columnTitle = ((TextView) singleCheckBoxLayout)
												.getText().toString();
									} else if (cellTag.equals("cell")) {
										LinearLayout cellLayout = (LinearLayout) singleCheckBoxLayout;
										for (int c = 0; c < cellLayout
												.getChildCount(); c++) {
											if (cellLayout
													.getChildAt(c)
													.getClass()
													.getName()
													.toString()
													.equals("android.widget.CheckBox")) {
												CheckBox cb = (CheckBox) cellLayout
														.getChildAt(c);
												if (cb.isChecked()) {
													MatrixCell cell = (MatrixCell) cb
															.getTag(R.id.matrix_cell);
													checkedValues += "|"
													/*
													 * + "h" + cell.getH_index()
													 * + "" + "v" +
													 * cell.getV_index()
													 */
													+ cell.getValue() + ":"
															+ cell.getValue(); // <-h0v0
												}
											}
										}
									}
								}
							}
						}
						// -------------
						if (checkedValues.length() > 0)
							checkedValues = checkedValues.substring(1); // <- it
																		// deletes
																		// the
																		// 1st
																		// char
																		// of
																		// the
																		// String
						else
							checkedValues = "-";
					}
				}
				map.put(key, checkedValues);
			}

			else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("matrixOptionLayout")) {
				String key = linearLayout.getTag(R.id.field_id).toString();
				String checkedValues = "";
				// ArrayList<MatrixCell> selectedCellList = new
				// ArrayList<MatrixCell>();
				key.toCharArray();
				for (int x = 0; x < linearLayout.getChildCount(); x++) {
					View view = linearLayout.getChildAt(x);
					if (view.getClass()
							.getName()
							.equals("com.xavey.android.layout.MatrixOptionLayout")
							&& view.getTag(R.id.layout_id).toString()
									.equals("theMatrixLayout")) {
						MatrixOptionLayout theMatrixLayout = (MatrixOptionLayout) view;
						LinearLayout rowLabelColumn = null;
						MYHorizontalScrollView horizontalScrollView = null;
						for (int y = 0; y < theMatrixLayout.getChildCount(); y++) {
							View v = theMatrixLayout.getChildAt(y);

							if (v.getTag(R.id.layout_id).toString()
									.equals("rowLabelColumn")) {
								rowLabelColumn = (LinearLayout) v;
							} else if (v.getTag(R.id.layout_id).toString()
									.equals("horizontalScrollView")) {
								horizontalScrollView = (MYHorizontalScrollView) v;
							}
						}
						// both rowLabelColumn and horizontalScrollView must be
						// already assigned in this line
						if (((LinearLayout) horizontalScrollView.getChildAt(0))
								.getTag(R.id.layout_id).toString()
								.equals("AllColumns")) {
							LinearLayout AllColumns = (LinearLayout) horizontalScrollView
									.getChildAt(0);
							for (int ac = 0; ac < AllColumns.getChildCount(); ac++) {
								LinearLayout singleColumnAt_ac = (LinearLayout) AllColumns
										.getChildAt(ac);
								String columnTitle = "";
								for (int sc = 0; sc < singleColumnAt_ac
										.getChildCount(); sc++) {
									View singleRadioButtonLayout = singleColumnAt_ac
											.getChildAt(sc);
									String cellTag = singleRadioButtonLayout
											.getTag(R.id.layout_id).toString();
									if (cellTag.equals("columnTitle")) {
										columnTitle = ((TextView) singleRadioButtonLayout)
												.getText().toString();
									} else if (cellTag.equals("cell")) {
										LinearLayout cellLayout = (LinearLayout) singleRadioButtonLayout;
										for (int c = 0; c < cellLayout
												.getChildCount(); c++) {
											if (cellLayout
													.getChildAt(c)
													.getClass()
													.getName()
													.toString()
													.equals("android.widget.RadioButton")) {
												RadioButton rb = (RadioButton) cellLayout
														.getChildAt(c);
												if (rb.isChecked()) {
													MatrixCell cell = (MatrixCell) rb
															.getTag(R.id.matrix_cell);
													if (needRawValue) {
														checkedValues += "|"
																+ cell.getValue();
													} else {
														checkedValues += "|"
																+ cell.getValue()
																+ ":"
																+ cell.getValue();
													}
												}
											}
										}
									}
								}
							}
						}
						// -------------
						if (checkedValues.length() > 0)
							checkedValues = checkedValues.substring(1); // <- it
																		// deletes
																		// the
																		// 1st
																		// char
																		// of
																		// the
																		// String
						else
							checkedValues = "-";
					}
				}
				map.put(key, checkedValues);
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
				if (checkedValues.length() > 0)
					checkedValues = checkedValues.substring(1); // <- it deletes
																// the 1st char
																// of the String
				else
					checkedValues = "-";
				map.put(key, checkedValues);
			}
			// <image option layout>
			else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("imageOptionLayout")) {
				String key = linearLayout.getTag(R.id.field_id).toString();
				String checkedValues = "";
				String field_label = linearLayout.getTag(R.id.field_label_id)
						.toString();

				for (int l = 0; l < linearLayout.getChildCount(); l++) {
					Class<?> subClass = (Class<?>) linearLayout.getChildAt(l)
							.getClass();
					if (subClass.getName().equals("android.widget.GridView")) {
						GridView gridView = (GridView) linearLayout
								.getChildAt(l);
						// TextView selectedItem = (TextView)
						// gridView.getSelectedItem();
						// ArrayList<E>
						ArrayList<String> selectedValueList = (ArrayList<String>) gridView
								.getTag(R.id.selected_grid_values);
						for (String selectedValue : selectedValueList) {
							checkedValues += "|" + selectedValue;
						}
					}
				}
				if (checkedValues.length() > 0)
					checkedValues = checkedValues.substring(1); // <- it deletes
																// the 1st char
																// of the String
				else
					checkedValues = "-";
				map.put(key, checkedValues);
			}
			// </image option layout>

			// <rating>
			else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("ratingLayout")) {
				String key = linearLayout.getTag(R.id.field_id).toString();
				String startCount = "";

				for (int cc = 0; cc < linearLayout.getChildCount(); cc++) {
					View child = linearLayout.getChildAt(cc);
					if (child.getClass().getName().toString()
							.equals("android.widget.LinearLayout")) {
						if (child.getTag(R.id.layout_id).toString()
								.equals("ratingAndLabelLayout")) {
							LinearLayout ratingAndLabelLayout = (LinearLayout) child;
							for (int z = 0; z < ratingAndLabelLayout
									.getChildCount(); z++) {
								View v = ratingAndLabelLayout.getChildAt(z);
								if (v.getClass().getName().toString()
										.equals("android.widget.RatingBar")) {
									RatingBar ratingBar = (RatingBar) v;
									startCount = (int) ratingBar.getRating()
											+ "";
								}
							}
						}
					}
				}

				map.put(key, startCount);
			}
			// </rating>

			// <rating_set> and <rating_set_image>
			else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("ratingSetLayout")
					|| linearLayout.getTag(R.id.layout_id).toString()
							.equals("ratingSetImageLayout")) {
				String key = linearLayout.getTag(R.id.field_id).toString();
				String checkedValues = "";
				for (int i_ = 0; i_ < linearLayout.getChildCount(); i_++) {
					View view = linearLayout.getChildAt(i_);
					if (view.getClass().getName().toString()
							.equals("android.widget.ListView")) {
						ListView listView = (ListView) view;
						RatingSetAdapter adapter = (RatingSetAdapter) listView
								.getAdapter();
						ArrayList<HashMap<String, String>> data = adapter
								.getData();

						for (int lv = 0; lv < listView.getChildCount(); lv++) {
							HashMap<String, String> dataMap = data.get(lv);
							String value = dataMap.get("value");
							RelativeLayout relativeLayout = (RelativeLayout) listView
									.getChildAt(lv);
							for (int rl = 0; rl < relativeLayout
									.getChildCount(); rl++) {
								View child_ = relativeLayout.getChildAt(rl);
								if (child_.getClass().getName().toString()
										.equals("android.widget.LinearLayout")) {
									if (child_.getTag(R.id.layout_id) != null
											&& child_.getTag(R.id.layout_id)
													.toString()
													.equals("ratingBarLayout")) {
										LinearLayout ratingBarLayout = (LinearLayout) child_;
										for (int rb = 0; rb < ratingBarLayout
												.getChildCount(); rb++) {
											View v = ratingBarLayout
													.getChildAt(rb);
											if (v.getClass()
													.getName()
													.toString()
													.equals("android.widget.RatingBar")) {
												RatingBar ratingBar = (RatingBar) v;
												int rating = (int) ratingBar
														.getRating();
												checkedValues += "|" + value
														+ ":" + rating;
											}
										}
									}
								}
							}
						}
					}
				}
				if (checkedValues.length() > 0)
					checkedValues = checkedValues.substring(1); // <- it deletes
																// the 1st char
																// of the String
				else
					checkedValues = "-";
				map.put(key, checkedValues);
			}
			// </rating set>

			// <textSet>
			else if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("textSetLayout")
					|| linearLayout.getTag(R.id.layout_id).toString()
							.equals("numberSetLayout")) {
				String key = linearLayout.getTag(R.id.field_id).toString();
				String values = "";
				LinearLayoutManager linearLayoutManager = new LinearLayoutManager();

				try {
					HashMap<String, Object> test_ = linearLayoutManager
							.test(linearLayout);
					ArrayList<HashMap<String, String>> data = (ArrayList<HashMap<String, String>>) test_
							.get("data");

					for (HashMap<String, String> singleMap : data) {
						Set<String> keys = singleMap.keySet();
						String singleKey = keys.toArray()[0].toString();
						String singleValue = singleMap.get(singleKey);
						if (needRawValue) {
							values += "|" + singleValue;
						} else {
							values += "|" + singleKey + ":" + singleValue;
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (values.length() > 0)
					values = values.substring(1);
				else
					values = "-";

				map.put(key, values);
			}
			// <textset and numberset/>
		}
		return map;
	}// </getValueFromEachLayout>

	private String getSelectedExtraValueBySelectedButton(
			RadioButton selectedButton) {
		String extraValue = "";
		LinearLayout selectedLayoutLine = (LinearLayout) selectedButton
				.getParent();
		for (int i = 0; i < selectedLayoutLine.getChildCount(); i++) {
			View v = selectedLayoutLine.getChildAt(i);
			if (v.getClass().getName().equals("android.widget.EditText")) {
				EditText selectedExtra = (EditText) v;
				extraValue = selectedExtra.getText().toString();
			}
		}
		return extraValue;
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

	public void renderNextLayout(int newPosition) throws Exception {

		if (newPosition != layoutList.size() - 1) {
			ArrayList<LinearLayout> layoutList_ = layoutList;
			LinearLayout nextLayout = layoutList.get(newPosition);
			LinearLayout nextInnerLayout = getInnerLayout(nextLayout);
			String nextLayoutID = nextInnerLayout.getTag(R.id.layout_id)
					.toString();
			String render_ref = "";
			String render_ref_type = "";

			if (nextInnerLayout.getTag(R.id.render_ref) != null) {
				render_ref = nextInnerLayout.getTag(R.id.render_ref).toString();
				render_ref_type = nextInnerLayout.getTag(R.id.render_ref_type)
						.toString();

				int renderRefID = Integer.parseInt(render_ref) - 1; // -1
																	// to
																	// get
																	// real
																	// id

				if (render_ref_type.equals("extra_equal_no_item")) {
					LinearLayout renderRefLayout = layoutList.get(renderRefID);
					LinearLayout renderInnerLayout = getInnerLayout(renderRefLayout);
					String renderLayoutID = renderInnerLayout.getTag(
							R.id.layout_id).toString();
					// following are all render Layout IDs...
					// <radioLayout>
					JSONArray nextRefcond = (JSONArray) nextInnerLayout
							.getTag(R.id.next_ref_cond);

					if (renderLayoutID.equals("radioLayout")) {
						for (int i = 0; i < renderInnerLayout.getChildCount(); i++) {
							View v = renderInnerLayout.getChildAt(i);
							if (v.getClass().getName()
									.equals("android.widget.RadioGroup")) {
								RadioGroup radioGroup = (RadioGroup) v;
								RadioButton selectedButton = getSelectedRadioButtonMyRadioGroup(radioGroup);
								String selectedButtonValue = selectedButton
										.getTag(R.id.radio_value).toString();

								// to check whether the layout
								// should be skipped or not...
								boolean isRefCorrect = true;

								// calculate that user selected
								// value is equal to one of
								// next ref cond
								for (int n = 0; n < nextRefcond.length(); n++) {
									String condition = "";
									try {
										condition = nextRefcond.getString(n);
										if (selectedButtonValue
												.equals(condition)) {
											isRefCorrect &= false;
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}

								if (!isRefCorrect)
									break;

								boolean extraRequired = false;
								if (selectedButton.getTag(R.id.extra_required) != null) {
									extraRequired = Boolean
											.parseBoolean(selectedButton
													.getTag(R.id.extra_required)
													.toString());
								}

								int extra = Integer
										.parseInt(getSelectedExtraValueBySelectedButton(selectedButton));
								// ^ we got the extra from render
								// layout
								// so let's put it in
								// nextInnerLayout

								// if(nextLayoutID.equals(""))

								for (int a = 0; a < nextInnerLayout
										.getChildCount(); a++) {
									View view = nextInnerLayout.getChildAt(a);
									if (view.getClass().getName()
											.equals("android.widget.ListView")) {
										ListView listView = (ListView) view;
										Adapter adapter = listView.getAdapter();
										if (nextLayoutID
												.equals("numberSetLayout")) {
											NumberSetAdapter numberSetAdapter = (NumberSetAdapter) adapter;
											ArrayList<HashMap<String, String>> data = numberSetAdapter
													.getRefData();
											ArrayList<HashMap<String, String>> newData = new ArrayList<HashMap<String, String>>();

											if (extra > data.size()) {
												extra = data.size();
											}

											for (int k = 0; k < extra; k++) {
												HashMap<String, String> map = data
														.get(k);
												newData.add(map);
											}
											data = newData;
											numberSetAdapter.setData(newData);
											numberSetAdapter
													.notifyDataSetChanged();

										} else if (nextLayoutID
												.equals("textSetLayout")) {
											TextSetAdapter textSetAdapter = (TextSetAdapter) adapter;
											ArrayList<HashMap<String, String>> data = textSetAdapter
													.getRefData();
											ArrayList<HashMap<String, String>> newData = new ArrayList<HashMap<String, String>>();

											if (extra > data.size()) {
												extra = data.size();
											}

											for (int k = 0; k < extra; k++) {
												HashMap<String, String> map = data
														.get(k);
												newData.add(map);
											}
											data = newData;
											textSetAdapter.setData(newData);
											textSetAdapter
													.notifyDataSetChanged();
										}
									}
								}
							}
						}
					}
					// </radioLayout>
				} else if (render_ref_type.equals("display_append_value_set")) {
					LinearLayout renderRefLayout = layoutList.get(renderRefID);
					LinearLayout renderInnerLayout = getInnerLayout(renderRefLayout);
					String renderLayoutID = renderInnerLayout.getTag(
							R.id.layout_id).toString();
					// dismiss newly created labels
					boolean isViewAlreadyExisted = Boolean
							.parseBoolean(nextInnerLayout.getTag(
									R.id.isViewAlreadyExisted).toString());
					// for(int i=0; i<nextInnerLayout.getChildCount(); i++){
					// View viewToRemove = nextInnerLayout.getChildAt(i);
					// if(viewToRemove.getTag(R.id.isViewAlreadyExisted)!=null){
					// isViewAlreadyExisted =
					// Boolean.parseBoolean(viewToRemove.getTag(R.id.isViewAlreadyExisted).toString());
					// if(isViewAlreadyExisted){
					// viewToRemove.setTag(R.id.isViewAlreadyExisted, false);
					// }
					// }
					// }
					// nextInnerLayout.refreshDrawableState();

					renderRefLayout.getChildCount();
					String[] selectedValues = null;
					for (int i = 0; i < renderRefLayout.getChildCount(); i++) {
						View v = renderRefLayout.getChildAt(i);
						if (v.getClass().getName()
								.equals("android.widget.LinearLayout")) {
							String layoutTagKey = v.getTag(R.id.layout_id)
									.toString();
							if (layoutTagKey.equals("textSetLayout")) {
								LinearLayout textSetLayout = (LinearLayout) v;
								for (int j = 0; j < textSetLayout
										.getChildCount(); j++) {
									if (textSetLayout.getChildAt(j).getClass()
											.getName()
											.equals("android.widget.ListView")) {
										ListView textSetListView = (ListView) textSetLayout
												.getChildAt(j);
										TextSetAdapter adapter = (TextSetAdapter) textSetListView
												.getAdapter();
										selectedValues = adapter.CurrentItems;
									}
								}
							}
						}
					}

					// remove blank values from user selected Values
					ArrayList<String> selectedValues_temp = new ArrayList<String>(
							Arrays.asList(selectedValues));
					selectedValues_temp.removeAll(Arrays.asList(null,"null","")); // remove
																		// empty
																		// strings
																		// , for
																		// null
																		// Arrays.asList(null),
																		// for
																		// both
																		// Arrays.asList(null,"")
					selectedValues = selectedValues_temp
							.toArray(new String[selectedValues_temp.size()]); // assign
																				// to
																				// old
																				// String[]

					// append selected values here..
					if (isViewAlreadyExisted) {
						int toBeRemoved = nextInnerLayout.getChildCount() - 2;
						for (int a = toBeRemoved; a > 0; a--) {
							nextInnerLayout.removeViewAt(nextInnerLayout
									.getChildCount() - 1);
						}
					}
					float textSize = 18;
					LayoutParams labelLayoutParams = new LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
					labelLayoutParams.setMargins(20, 10, 10, 0);
					for (int i = 0; i < selectedValues.length; i++) {
						String value = selectedValues[i];
						TextView referencedLabel = new TextView(this);
						referencedLabel.setLayoutParams(labelLayoutParams);
						referencedLabel.setText("-" + value);
						referencedLabel.setTextSize(textSize);
						referencedLabel.setTag(R.id.isViewAlreadyExisted, true);
						nextInnerLayout.addView(referencedLabel);
					}
					nextInnerLayout.setTag(R.id.isViewAlreadyExisted, true);
				} else if (render_ref_type
						.equals("display_dataset_append_set_ref")) {
					// if end with ref
					// read from ref values
					// prepare dataset
					// get target linear layout, remove and add new one
					String refValue = Refs.get(render_ref);
					if (refValue != null && refValue.length() > 0) {

						if (nextLayoutID.equals("checkBoxLayout")) {
							for (int a = 0; a < nextInnerLayout.getChildCount(); a++) {
								View view = nextInnerLayout.getChildAt(a);
								if (view.getClass()
										.getName()
										.equals("com.xavey.android.layout.CheckboxLayout")) {
									JSONHelper jh = new JSONHelper();
									CheckboxLayout checkboxGroup = (CheckboxLayout) view;
									final JSONArray dsArray = jh
											.AppendStringToDataSet(
													checkboxGroup
															.getFinalBaseValueList(),
													refValue.split("\\|"));
									checkboxGroup.initLayout(dsArray);
								}
							}

						} else if (nextLayoutID.equals("checklistLayout")) {

						} else if (nextLayoutID.equals("matrixOptionLayout")) {

						}
					}
				} else if (render_ref_type
						.equals("display_dataset_prepend_set_ref")) {
					// if end with ref
					// read from ref values
					// prepare dataset
					// get target linear layout, remove and add new one
					String refValue = Refs.get(render_ref);
					if (refValue != null && refValue.length() > 0) {

						if (nextLayoutID.equals("checkBoxLayout")) {
							for (int a = 0; a < nextInnerLayout.getChildCount(); a++) {
								View view = nextInnerLayout.getChildAt(a);
								if (view.getClass()
										.getName()
										.equals("com.xavey.android.layout.CheckboxLayout")) {
									JSONHelper jh = new JSONHelper();
									CheckboxLayout checkboxGroup = (CheckboxLayout) view;
									final JSONArray dsArray = jh
											.PrependStringToDataSet(
													checkboxGroup
															.getFinalBaseValueList(),
													refValue.split("\\|"));
									checkboxGroup.initLayout(dsArray);
								}
							}

						} else if (nextLayoutID.equals("checklistLayout")) {

						} else if (nextLayoutID.equals("matrixOptionLayout")) {

						}
					}
				} else if (render_ref_type
						.equals("display_dataset_option_set_ref")) {
					// if end with ref
					// read from ref values
					// prepare dataset
					// get target linear layout, remove and add new one
					String refValue = Refs.get(render_ref);
					if (refValue != null && refValue.length() > 0) {

						if (nextLayoutID.equals("radioLayout")) {
							for (int a = 0; a < nextInnerLayout.getChildCount(); a++) {
								View view = nextInnerLayout.getChildAt(a);
								if (view.getClass()
										.getName()
										.equals("com.xavey.android.layout.RadioGroupLayout")) {
									JSONHelper jh = new JSONHelper();
									RadioGroupLayout rGroup = (RadioGroupLayout) view;
									final JSONArray dsArray = jh
											.StringToJSONDataSet(refValue
													.split("\\|"));
									rGroup.initLayout(dsArray, false);
								}
							}

						} else if (nextLayoutID.equals("checklistLayout")) {

						} else if (nextLayoutID.equals("matrixOptionLayout")) {

						}
					}
				} else if (render_ref_type.equals("display_dataset_v_set_ref")) {
					// if end with ref
					// read from ref values
					// prepare dataset
					// get target linear layout, remove and add new one
					String refValue = Refs.get(render_ref);
					if (refValue != null && refValue.length() > 0) {

						if (nextLayoutID.equals("matrixOptionLayout")) {
							for (int a = 0; a < nextInnerLayout.getChildCount(); a++) {
								View view = nextInnerLayout.getChildAt(a);
								if (view.getClass()
										.getName()
										.equals("com.xavey.android.layout.MatrixOptionLayout")) {
									JSONHelper jh = new JSONHelper();
									MatrixOptionLayout matOpt = (MatrixOptionLayout) view;
									// final JSONArray dsArray =
									// jh.AppendStringToDataSet(radioGroup.getFinalBaseValueList(),refValue.split("\\|"));
									// radioGroup.initLayout(dsArray);
									// hValueList , vValueList, cellValueList
									ArrayList<HashMap<String, String>> hValueList = matOpt
											.getHValueList();
									ArrayList<HashMap<String, String>> vValueList = jh
											.StringToMapDataSet(refValue
													.split("\\|"));
									JSONArray cellValueList = jh
											.generateMatrixValues(hValueList,
													vValueList);
									matOpt.setHValueList(hValueList);
									matOpt.setVValueList(vValueList);
									matOpt.setCellValueList(cellValueList);
									matOpt.initLayout();
								}
							}
						}
					}
				}
				/*
				 * //<numberSetLayout>
				 * if(renderLayoutID.equals("numberSetLayout")){ for(int i=0;
				 * i<nextInnerLayout.getChildCount(); i++){ View child =
				 * nextInnerLayout.getChildAt(0); if(child.getClass
				 * ().getName().equals("android.widget.ListView")){ ListView
				 * listView = (ListView) child; listView.getChildCount(); } } }
				 * //</numberSetLayout>
				 */
			} else {
				// nothing to do here since there is no render_ref
			}
		}
	}

	private LinearLayout getRefLayout(String ref_id,
			ArrayList<LinearLayout> layoutList) {
		LinearLayout refLayout = null;
		int ref_id_ = Integer.parseInt(ref_id);
		refLayout = layoutList.get(ref_id_ - 1);
		return refLayout;
	}

	private String getNextConditionTypeFromParrentLayout(
			LinearLayout parrentLayout) {
		String nextConditionType = null;
		for (int i = 0; i < parrentLayout.getChildCount(); i++) {
			if (parrentLayout.getTag(R.id.layout_id) != null) {
				if (parrentLayout.getTag(R.id.layout_id).toString()
						.equals("submitLayout"))
					return null;
			} else if (parrentLayout.getChildAt(i).getClass().getName()
					.equals("android.widget.LinearLayout")) {
				LinearLayout linearLayout = (LinearLayout) parrentLayout
						.getChildAt(i);
				LinearLayout innerLayout = null;
				if (linearLayout.getTag(R.id.layout_id) != null
						&& !linearLayout.getTag(R.id.layout_id).toString()
								.equals("recordingLayout")) {
					innerLayout = linearLayout;
					nextConditionType = innerLayout.getTag(R.id.next_ref_type)
							.toString();
				}
			} else if (parrentLayout.getChildAt(i).getClass().getName()
					.equals("android.widget.ScrollView")) {
				ScrollView scrollView = (ScrollView) parrentLayout
						.getChildAt(i);
				LinearLayout linearLayout = (LinearLayout) scrollView
						.getChildAt(0);
				LinearLayout innerLayout = null;
				if (linearLayout.getTag(R.id.layout_id) != null
						&& !linearLayout.getTag(R.id.layout_id).toString()
								.equals("recordingLayout")) {
					innerLayout = linearLayout;
					nextConditionType = innerLayout.getTag(R.id.next_ref_type)
							.toString();
					nextConditionType.toString();
				}
			}
		}
		return nextConditionType;
	}

	private JSONArray getNextConditionFromParrentLayout(
			LinearLayout parrentLayout) {
		JSONArray nextCondition = null;
		for (int i = 0; i < parrentLayout.getChildCount(); i++) {
			if (parrentLayout.getTag(R.id.layout_id) != null) {
				if (parrentLayout.getTag(R.id.layout_id).toString()
						.equals("submitLayout"))
					return null;
			} else if (parrentLayout.getChildAt(i).getClass().getName()
					.equals("android.widget.LinearLayout")) {
				LinearLayout linearLayout = (LinearLayout) parrentLayout
						.getChildAt(i);
				LinearLayout innerLayout = null;
				if (linearLayout.getTag(R.id.layout_id) != null
						&& !linearLayout.getTag(R.id.layout_id).toString()
								.equals("recordingLayout")) {
					innerLayout = linearLayout;
					if (innerLayout.getTag(R.id.next_ref_cond) != null) {
						nextCondition = (JSONArray) innerLayout
								.getTag(R.id.next_ref_cond);
						return nextCondition;
					}
				}
			} else if (parrentLayout.getChildAt(i).getClass().getName()
					.equals("android.widget.ScrollView")) {
				ScrollView scrollView = (ScrollView) parrentLayout
						.getChildAt(i);
				LinearLayout linearLayout = (LinearLayout) scrollView
						.getChildAt(0);
				LinearLayout innerLayout = null;
				if (linearLayout.getTag(R.id.layout_id) != null
						&& !linearLayout.getTag(R.id.layout_id).toString()
								.equals("recordingLayout")) {
					innerLayout = linearLayout;
					if (innerLayout.getTag(R.id.next_ref_cond) != null) {
						nextCondition = (JSONArray) innerLayout
								.getTag(R.id.next_ref_cond);
						return nextCondition;
					}
				}
			}
		}
		return nextCondition;
	}

	private boolean isNeedToSkip(JSONArray next_cond, String value) {
		boolean isNeedToSkip = false;
		if (value.contains("|")) {
			// for values which contain pipes
			// like checklist and location
			String[] values = value.split("|");
			for (int i = 0; i < next_cond.length(); i++) {
				try {
					String cond = next_cond.getString(i);
					for (int j = 0; j < values.length; j++) {
						String value_item = values[j];
						if (cond.equals(value_item)) {
							isNeedToSkip = isNeedToSkip || true;
						}
					}
				} catch (JSONException e) {
					toast.xaveyToast(null, e.getMessage());
				}
			}
		} else {
			for (int i = 0; i < next_cond.length(); i++) {
				String cond = "";
				try {
					cond = next_cond.getString(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (cond.equals(value)) {
					// isNeedToSkip = isNeedToSkip || true;
					return true;
				}
			}
		}
		return isNeedToSkip;
	}

	private boolean isNeedToSkipIfLessThan(JSONArray next_cond,
			int valueFromRefLayout) {
		boolean isNeedToSkip = false;
		try {
			int next_condValue = Integer.parseInt(next_cond.getString(0));
			if (valueFromRefLayout <= next_condValue) {
				isNeedToSkip = true;
			}

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return isNeedToSkip;
	}

	private void previous(int newPosition) {
		int last_range = navigator.getLast();
		vPager.setCurrentItem(newPosition - last_range);
		currentPosition = newPosition - last_range;
	}

	private String getReferenceFromParrentLayout(LinearLayout parrentLayout) {
		String reference = "";
		for (int i = 0; i < parrentLayout.getChildCount(); i++) {
			if (parrentLayout.getTag(R.id.layout_id) != null) {
				if (parrentLayout.getTag(R.id.layout_id).toString()
						.equals("submitLayout"))
					return "";
			} else if (parrentLayout.getChildAt(i).getClass().getName()
					.equals("android.widget.LinearLayout")) {
				LinearLayout linearLayout = (LinearLayout) parrentLayout
						.getChildAt(i);
				LinearLayout innerLayout = null;
				if (linearLayout.getTag(R.id.layout_id) != null
						&& !linearLayout.getTag(R.id.layout_id).toString()
								.equals("recordingLayout")) {
					innerLayout = linearLayout;
					if (innerLayout.getTag(R.id.next_ref) != null)
						reference = innerLayout.getTag(R.id.next_ref)
								.toString();
				}
			} else if (parrentLayout.getChildAt(i).getClass().getName()
					.equals("android.widget.ScrollView")) {
				ScrollView scrollView = (ScrollView) parrentLayout
						.getChildAt(i);
				LinearLayout linearLayout = (LinearLayout) scrollView
						.getChildAt(0);
				LinearLayout innerLayout = null;
				if (linearLayout.getTag(R.id.layout_id) != null
						&& !linearLayout.getTag(R.id.layout_id).toString()
								.equals("recordingLayout")) {
					innerLayout = linearLayout;
					if (innerLayout.getTag(R.id.next_ref) != null)
						reference = innerLayout.getTag(R.id.next_ref)
								.toString();
				}
			}
		}
		return reference;
	}

	private boolean isFieldInvolvedNextCondType(LinearLayout parrentlayout) {
		String next_cond_type = "";
		for (int i = 0; i < parrentlayout.getChildCount(); i++) {
			if (parrentlayout.getTag(R.id.layout_id) != null) {
				if (parrentlayout.getTag(R.id.layout_id).toString()
						.equals("submitLayout"))
					return false;
			} else if (parrentlayout.getChildAt(i).getClass().getName()
					.equals("android.widget.LinearLayout")) {
				LinearLayout linearLayout = (LinearLayout) parrentlayout
						.getChildAt(i);
				LinearLayout innerLayout = null;
				if (linearLayout.getTag(R.id.layout_id) != null
						&& !linearLayout.getTag(R.id.layout_id).toString()
								.equals("recordingLayout")) {
					innerLayout = linearLayout;
					if (innerLayout.getTag(R.id.next_ref_cond) != null)
						next_cond_type = innerLayout.getTag(R.id.next_ref_cond)
								.toString();
				}
			} else if (parrentlayout.getChildAt(i).getClass().getName()
					.equals("android.widget.ScrollView")) {
				ScrollView scrollView = (ScrollView) parrentlayout
						.getChildAt(i);
				LinearLayout linearLayout = (LinearLayout) scrollView
						.getChildAt(0);
				LinearLayout innerLayout = null;
				if (linearLayout.getTag(R.id.layout_id) != null
						&& !linearLayout.getTag(R.id.layout_id).toString()
								.equals("recordingLayout")) {
					innerLayout = linearLayout;
					if (innerLayout.getTag(R.id.next_ref_cond) != null)
						next_cond_type = innerLayout.getTag(R.id.next_ref_cond)
								.toString();
				}
			}
		}
		if (next_cond_type.length() > 0)
			return true;
		else
			return false;
	}

	private boolean isFieldInvolvedReference(LinearLayout nextParrentLayout_) {
		String next_ref = "";
		for (int i = 0; i < nextParrentLayout_.getChildCount(); i++) {
			if (nextParrentLayout_.getTag(R.id.layout_id) != null) {
				if (nextParrentLayout_.getTag(R.id.layout_id).toString()
						.equals("submitLayout"))
					return false;
			} else if (nextParrentLayout_.getChildAt(i).getClass().getName()
					.equals("android.widget.LinearLayout")) {
				LinearLayout linearLayout = (LinearLayout) nextParrentLayout_
						.getChildAt(i);
				LinearLayout innerLayout = null;
				if (linearLayout.getTag(R.id.layout_id) != null
						&& !linearLayout.getTag(R.id.layout_id).toString()
								.equals("recordingLayout")) {
					innerLayout = linearLayout;
					if (innerLayout.getTag(R.id.next_ref) != null)
						next_ref = innerLayout.getTag(R.id.next_ref).toString();
				}
			} else if (nextParrentLayout_.getChildAt(i).getClass().getName()
					.equals("android.widget.ScrollView")) {
				ScrollView scrollView = (ScrollView) nextParrentLayout_
						.getChildAt(i);
				LinearLayout linearLayout = (LinearLayout) scrollView
						.getChildAt(0);
				LinearLayout innerLayout = null;
				if (linearLayout.getTag(R.id.layout_id) != null
						&& !linearLayout.getTag(R.id.layout_id).toString()
								.equals("recordingLayout")) {
					innerLayout = linearLayout;
					if (innerLayout.getTag(R.id.next_ref) != null) {
						next_ref = innerLayout.getTag(R.id.next_ref).toString();
						break;
					}
				}
			}
		}
		if (next_ref.length() > 0)
			return true;
		else
			return false;
	}

	private int getNextRoute(int newPosition) {
		boolean isNeedToSkip = true;
		LinearLayout currentLayoutTest = layoutList.get(currentPosition);
		while (isNeedToSkip) {
			LinearLayout nextLayout_ = layoutList.get(newPosition);

			boolean isInvolvedRef = isFieldInvolvedReference(nextLayout_);
			boolean isFieldInvolvedNextCondType = isFieldInvolvedNextCondType(nextLayout_);
			String nextConditionType = "nothing";

			if (isInvolvedRef) {
				String next_ref = getReferenceFromParrentLayout(nextLayout_);
				LinearLayout ref_layout = getRefLayout(next_ref, layoutList);
				JSONArray next_cond = getNextConditionFromParrentLayout(nextLayout_);
				boolean secondCondition = next_cond != null
						&& next_cond.length() > 0;
				if (isFieldInvolvedNextCondType) {
					nextConditionType = getNextConditionTypeFromParrentLayout(nextLayout_);
					LinearLayout ref_inner_layout = getInnerLayout(ref_layout);
					if (nextConditionType.equals("count")) {
						// count is here

						int count = 0;
						if (ref_inner_layout.getTag(R.id.layout_id).toString()
								.equals("checkBoxLayout")) {
							for (int i = 0; i < ref_inner_layout
									.getChildCount(); i++) {
								View v = ref_inner_layout.getChildAt(i);
								String className = v.getClass().getName()
										.toString();
								if (className.equals("android.widget.CheckBox")) {
									CheckBox cb = (CheckBox) v;
									if (cb.isChecked())
										count++;
								}
							}
						}
						// now we got count here
						isNeedToSkip = isNeedToSkip(next_cond, count + "");
						if (isNeedToSkip)
							newPosition++;
						isNeedToSkip = false; // to break from while loop
					} // count end here
					else if (nextConditionType.endsWith("less_or_equal")) {
						String itemValue = nextConditionType.split(":")[0]; // "Used"
						int valueFromRefLayout = 0;

						LinearLayoutManager linearLayoutManager = new LinearLayoutManager();

						try {
							HashMap<String, Object> test_ = linearLayoutManager
									.test(ref_inner_layout);
							ArrayList<HashMap<String, String>> data = (ArrayList<HashMap<String, String>>) test_
									.get("data");

							for (HashMap<String, String> singleMap : data) {
								Set<String> keys = singleMap.keySet();
								String singleKey = keys.toArray()[0].toString();
								if (singleKey.equals(itemValue)) {
									valueFromRefLayout = Integer
											.parseInt(singleMap.get(singleKey));
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

						isNeedToSkip = isNeedToSkipIfLessThan(next_cond,
								valueFromRefLayout);
						if (isNeedToSkip)
							newPosition++;
						// isNeedToSkip = false; // to break from while loop
					} else if (nextConditionType.equals("less_or_equal_ref")) {
						// count is here
						int valueFromRef = 0;
						String refValues = Refs.get(next_ref);
						if (refValues.indexOf("|") > -1) {
							valueFromRef = refValues.split("\\|").length;
						}
						isNeedToSkip = isNeedToSkipIfLessThan(next_cond,
								valueFromRef);
						if (isNeedToSkip)
							newPosition++;
					} // count end here
					else {
						// isNeedToSkip = false;
						String value_from_ref_layout = jsonReader
								.readValueFromLayout(ref_layout);
						isNeedToSkip = isNeedToSkip(next_cond,
								value_from_ref_layout);
						if (isNeedToSkip)
							newPosition++;
						// isNeedToSkip = false; // to break from while loop
					}
				}// nothing to do with it if it doesn't involve
			} else {
				isNeedToSkip = false;
			}
		}
		return newPosition;
	}

	// block navigation control
	private void navStayStill(String direction, String currentFieldID,
			String field_error_msg, LinearLayoutManager lLManager,
			int newPosition, int currentPosition, boolean force_stop_r_l) {

		LinearLayout currentParentLayout = layoutList.get(currentPosition);
		TextView errorMsg = lLManager.getErrorMsgTextView(currentParentLayout);
		LayoutParams errorMsgLayoutOpen = new LayoutParams(
				LayoutParams.MATCH_PARENT, 30);
		errorMsgLayoutOpen.setMargins(10, 20, 10, 20);
		errorMsg.setTextColor(Color.RED);

		if (direction.equals(LEFT_TO_RIGHT)) {
			// navigator.addLast(0);
			used_field_ids.addLast(currentFieldID);
			vPager.setCurrentItem(currentPosition);
			if (field_error_msg.length() > 0)
				errorMsg.setText(field_error_msg);
			else
				errorMsg.setText("Some fields are required..");
			currentPosition = previousIndex;
		} else {
			if (force_stop_r_l) { // RIGHT_TO_LEFT
				used_field_ids.addLast(currentFieldID);
				vPager.setCurrentItem(currentPosition);
				if (field_error_msg.length() > 0)
					errorMsg.setText(field_error_msg);
				else
					errorMsg.setText("Some fields are required..");
				currentPosition = previousIndex;
			} else {
				int last_range = navigator.getLast();
				newPosition = currentPosition - last_range;
				vPager.setCurrentItem(newPosition);
				currentPosition = newPosition;
				// navigator.removeLast();
				used_field_ids.removeLast();
			}
		}
	}

	private void prepRefValues() {
		String setterPointer = "";
		LinearLayout currentParentLayout = layoutList.get(currentPosition);
		LinearLayoutManager llManager = new LinearLayoutManager();
		LinearLayout thisLayout = llManager.getInnerLayout(currentParentLayout);
		if (thisLayout != null && thisLayout.getTag(R.id.ref_setter) != null) {
			setterPointer = thisLayout.getTag(R.id.ref_setter).toString();
		}
		if (setterPointer != null && setterPointer.length() > 0) {
			// tma: question comes with ref_setter ID.. now check
			// the ref object and set the value
			for (int i = 0; i < formRefList.size(); i++) {
				if (formRefList.get(i).size() > 0) {
					HashMap<String, Object> map = formRefList.get(i);
					String refID = map.get("ref_id").toString();
					String refType = map.get("ref_type").toString();
					JSONArray refSetter = (JSONArray) map.get("ref_setter");
					if (refID.equals(setterPointer)) {
						// work only for one ref:ref_setter
						String refValues = "";
						for (int n = 0; n < refSetter.length(); n++) {
							String setterIDRaw = "";
							int setterID = -1;
							int layoutIndex = -1;
							try {
								setterIDRaw = refSetter.getString(n);
								setterID = Integer.valueOf(setterIDRaw);
								layoutIndex = setterID - 1;
								if (layoutIndex <= currentPosition
										&& layoutIndex >= 0) {
									// valid setter ID ready to read
									// the value from layout
									HashMap<String, Object> valueFromLayout = getValuesFromEachLayout(
											layoutList, layoutIndex,
											layoutIndex + 1, true);
									if (valueFromLayout != null) {
										if (refType
												.equals("dataset_row_selected_first_column")) {
											LinearLayout renderRefLayout = layoutList
													.get(layoutIndex);
											LinearLayout renderInnerLayout = getInnerLayout(renderRefLayout);
											String renderLayoutID = renderInnerLayout
													.getTag(R.id.layout_id)
													.toString();
											if (renderLayoutID
													.equals("matrixOptionLayout")) {
												for (int j = 0; j < renderInnerLayout
														.getChildCount(); j++) {
													View v = renderInnerLayout
															.getChildAt(j);
													if (v.getClass()
															.getName()
															.equals("com.xavey.android.layout.MatrixOptionLayout")) {
														MatrixOptionLayout matOpt = (MatrixOptionLayout) v;
														ArrayList<HashMap<String, String>> vValueList = matOpt
																.getVValueList();
														JSONArray cellList = matOpt
																.getCellValueList();

														HashMap.Entry<String, Object> entry = valueFromLayout
																.entrySet()
																.iterator()
																.next();
														String key = entry
																.getKey();
														String value = entry
																.getValue()
																.toString();

														// excepted data format
														// cell_value1|cell_value2
														// or just single
														// cell_value1
														ArrayList<String> valList = new ArrayList<String>();
														if (value.indexOf("|") > -1) {
															valList = new ArrayList<String>(
																	Arrays.asList(value
																			.split("\\|")));
														} else {
															valList.add(value);
														}
														ArrayList<String> colList = new ArrayList<String>();
														for (int k = 0; k < cellList
																.length(); k++) {
															JSONObject jo = cellList
																	.getJSONObject(k);
															String cell_value = jo
																	.getString("value");
															String cell_index = jo
																	.getString("index");
															if (valList
																	.indexOf(cell_value) > -1) {
																if (cell_index
																		.trim()
																		.startsWith(
																				"0")) {
																	String vIndex = cell_index
																			.split(",")[1]
																			.trim();
																	refValues += vValueList
																			.get(Integer
																					.parseInt(vIndex))
																			.get("label")
																			.toString()
																			+ "|";
																	;
																}
															}

														}
													}
												}
											}
										} else if (refType
												.equals("dataset_random")) {
											HashMap.Entry<String, Object> entry = valueFromLayout
													.entrySet().iterator()
													.next();
											String key = entry.getKey();
											String value = entry.getValue()
													.toString();

											if (value.indexOf("|") > -1) {
												String[] valList = value
														.split("\\|");
												value = "";
												ArrayList<String> temp = new ArrayList<String>(
														Arrays.asList(valList));
												Collections.shuffle(temp);
												for (int j = 0; j < temp.size(); j++) {
													if (!temp
															.get(j)
															.toLowerCase()
															.equals("#novalue#")) {
														value += temp.get(j)
																+ "|";
													}
												}
											}

											refValues += value + "|";
										} else {
											HashMap.Entry<String, Object> entry = valueFromLayout
													.entrySet().iterator()
													.next();
											String key = entry.getKey();
											String value = entry.getValue()
													.toString();

											if (value.indexOf("|") > -1) {
												String[] valList = value
														.split("\\|");
												value = "";
												for (int j = 0; j < valList.length; j++) {
													if (!valList[j]
															.toLowerCase()
															.equals("#novalue#")) {
														value += valList[j]
																+ "|";
													}
												}
											}

											refValues += value + "|";
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						// after looping ref:ref_setter values
						Refs.put(refID,
								refValues.substring(0, refValues.length() - 1));
						Log.i("test", Refs.get(refID));
					}
				}
			}
		}
	}

	private void navLeftToRight(int newPosition, String currentFieldID)
			throws Exception {
		prepRefValues();

        LinearLayout currentParentLayout = layoutList.get(currentPosition);
        TextView errorMsg = lLManager.getErrorMsgTextView(currentParentLayout);
        if (errorMsg != null){
            errorMsg.setText("");
        }
		newPosition = getNextRoute(newPosition);
		renderNextLayout(newPosition);
		int range = newPosition - currentPosition;
		if (range != 0)
			navigator.addLast(range);
		used_field_ids.addLast(currentFieldID);
		vPager.setCurrentItem(newPosition);
		currentPosition = newPosition;
		previousIndex = currentPosition;
		// hide keyboard
		LinearLayout nextLayout_ = layoutList.get(newPosition);
		if (!isSubmitLayout(nextLayout_))
			hideKeyboard(nextLayout_);
		isValidating = false;
	}

	private void navRightToLeft(int newPosition) {

		if (ApplicationValues.IS_RECORDING_NOW) {
			LinearLayout thisLayout = layoutList.get(currentPosition);
			LinearLayout recordingLayout = null;
			for (int i = 0; i < thisLayout.getChildCount(); i++) {
				if (thisLayout.getChildAt(i).getTag(R.id.layout_id) != null
						&& thisLayout.getChildAt(i).getTag(R.id.layout_id)
								.toString().equals("recordingLayout")) {
					recordingLayout = (LinearLayout) thisLayout.getChildAt(i);
					break;
				}
			}
			if (recordingLayout != null) {
				AudioRecordingManager currentRecording = (AudioRecordingManager) recordingLayout
						.getTag(R.id.recording_manager);
				currentRecording.triggerStopClick();
			}
		}
		int last_range = 0;
		if (navigator.getLast() != null)
			last_range = navigator.getLast();
		newPosition = currentPosition - last_range;
		vPager.setCurrentItem(newPosition);
		currentPosition = newPosition;
		navigator.removeLast();
		used_field_ids.removeLast();
		// hide keyboard
		LinearLayout nextLayout_ = layoutList.get(newPosition);
		if (!isSubmitLayout(nextLayout_))
			hideKeyboard(nextLayout_);
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

	private ArrayList<CheckBox> getSelectedCheckBoxMyCheckboxWrapper(
			LinearLayout checkBoxLayout) {
		ArrayList<CheckBox> returnList = new ArrayList<CheckBox>();

		for (int c = 0; c < checkBoxLayout.getChildCount(); c++) {
			if (checkBoxLayout.getChildAt(c).getClass().getName()
					.equals("com.xavey.android.layout.CheckboxLayout")) {
				CheckboxLayout checkBoxWrapper = (CheckboxLayout) checkBoxLayout.getChildAt(c);
				for (int d = 0; d < checkBoxWrapper.getChildCount(); d++) {
					LinearLayout checkBoxLine = null;
					View cbLineLayoutChild = checkBoxWrapper.getChildAt(d);
					if (cbLineLayoutChild.getClass().getName()
							.equals("android.widget.LinearLayout")) {
						checkBoxLine = (LinearLayout) checkBoxWrapper
								.getChildAt(d);
						CheckBox cb = getCheckBoxFromCheckBoxLine(checkBoxLine);
						if (cb.isChecked()) {
							returnList.add(cb);
						}
					}
				}
			}
		}
		return returnList;
	}

	private void loadUI() {
		getScreenInfo();
		TypeFaceManager tfManager = new TypeFaceManager(this);
		byte[] logoByteArray = ApplicationValues.loginUser.getLogoImage();
		if (logoByteArray != null) {
			Bitmap logoBitMap = BitmapFactory.decodeByteArray(logoByteArray, 0,
					logoByteArray.length);
			BitmapDrawable bd = new BitmapDrawable(getResources(), logoBitMap);
			getActionBar().setIcon(bd);
		} else {
			getActionBar().setIcon(R.drawable.home);
		}
		getActionBar().setTitle("Home");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		intent = getIntent();
		jsonReader = new JSONReader(this);
		jsonWriter = new JSONWriter(this);
		dbHelper = new XaveyDBHelper(this);
		currentForm = dbHelper.getFormByFormID(intent.getStringExtra("formID"));
		connectionDetector = new ConnectionDetector(getApplicationContext());
		formFieldsList = jsonReader.getFormFields(currentForm.getForm_json());
		recordingManager = new AudioRecordingManager(this);

		formRefList = jsonReader.getFormRefs(currentForm.getForm_json());
		prepareDataSets();
	}

	private void prepareDataSets() {
		Refs = new HashMap<String, String>();
		for (int i = 0; i < formRefList.size(); i++) {
			HashMap<String, Object> map = formRefList.get(i);
			String refID = map.get("ref_id").toString();
			String refType = map.get("ref_type").toString();
			// if(refType=="dataset"){
			Refs.put(refID, null);
			// }
			// else{
			// Refs.put(refID,"");
			// }
		}
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
		for (int i = 0; i < parrentLayout.getChildCount(); i++) {
			String className = parrentLayout.getChildAt(i).getClass().getName()
					.toString();
			if (className.equals("android.widget.ScrollView")) {
				ScrollView scroll = (ScrollView) parrentLayout.getChildAt(i);
				innerLayout = (LinearLayout) scroll.getChildAt(0);
			} else if (className.equals("android.widget.LinearLayout")) {
				LinearLayout linearLayout = (LinearLayout) parrentLayout
						.getChildAt(i);
				if (linearLayout.getTag(R.id.layout_id) != null
						&& linearLayout.getTag(R.id.layout_id).toString() != "recordingLayout")
					innerLayout = linearLayout;
			}
		}
		String innerLayoutID = innerLayout.getTag(R.id.layout_id).toString();
		if (innerLayoutID.equals("datetimeLayout")
				|| innerLayoutID.equals("radioLayout")
				|| innerLayoutID.equals("checkBoxLayout")
				|| innerLayoutID.equals("locationLayout")
				|| innerLayoutID.equals("drawingLayout")
				|| innerLayoutID.equals("photoLayout")
				|| innerLayoutID.equals("martixOptionSingleLayout")
				|| innerLayoutID.equals("imageOptionLayout")
				|| innerLayoutID.equals("imageChecklistLayout")) {
			InputMethodManager inputManager = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			// check if no view has focus:
			View view = this.getCurrentFocus();
			if (view != null) {
				inputManager.hideSoftInputFromWindow(view.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder.setTitle("Confirm");
			alertDialogBuilder.setMessage("Are you sure to end the interview?");
			alertDialogBuilder.setCancelable(false);

			alertDialogBuilder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Intent homeIntent = new
							// Intent(Intent.ACTION_MAIN);
							// homeIntent.addCategory(Intent.CATEGORY_HOME);
							// homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							// startActivity(homeIntent);
							finish();
						}
					});
			alertDialogBuilder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// if this button is clicked, just close
							// the dialog box and do nothing
							dialog.cancel();
						}
					});
			alertDialogBuilder.create().show();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean isSelectedAnyCheckBox(LinearLayout checkBoxLayout) {

		boolean isSelectedAnyCheckBox = false;
		for (int i = 0; i < checkBoxLayout.getChildCount(); i++) {
			LinearLayout checkBoxLine = (LinearLayout) checkBoxLayout
					.getChildAt(i);
			CheckBox cb = (CheckBox) checkBoxLine.getChildAt(0);
			if (cb.isChecked()) {
				isSelectedAnyCheckBox |= true;
			}
		}
		return isSelectedAnyCheckBox;
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

	private EditText getExtraFromCheckBoxLine(LinearLayout checkBoxLine) {
		View view = null;
		for (int i = 0; i < checkBoxLine.getChildCount(); i++) {
			if (checkBoxLine.getChildAt(i).getClass().getName().toString()
					.equals("android.widget.EditText")) {
				view = checkBoxLine.getChildAt(i);
				break;
			}
		}
		return (EditText) view;
	}

	private boolean isSubmitLayout(LinearLayout linearLayout) {
		boolean isSubmitLayout = false;
		if (linearLayout.getTag(R.id.layout_id) != null) {
			if (linearLayout.getTag(R.id.layout_id).toString()
					.equals("submitLayout")) {
				isSubmitLayout = true;
			}
		}
		return isSubmitLayout;
	}

	@Override
	public void onBackPressed() {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Confirm");
		alertDialogBuilder.setMessage("Are you sure to end the interview?");
		alertDialogBuilder.setCancelable(false);

		alertDialogBuilder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Intent homeIntent = new Intent(Intent.ACTION_MAIN);
						// homeIntent.addCategory(Intent.CATEGORY_HOME);
						// homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						// startActivity(homeIntent);
						finish();
					}
				});
		alertDialogBuilder.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});
		alertDialogBuilder.create().show();

	}
}

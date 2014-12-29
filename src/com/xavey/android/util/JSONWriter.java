package com.xavey.android.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.xavey.android.db.XaveyDBHelper;
import com.xavey.android.model.Document;
import com.xavey.android.model.Form;
import com.xavey.android.model.Image;

// for date picker
// http://android-devblog.blogspot.com/2010/05/wheel-ui-contol.html

public class JSONWriter {
	Activity activity;
	JSONObject jsonObject;
	JSONReader jsonReader;
	XaveyDBHelper dbHelper;

	public JSONWriter(Activity activity) {
		this.activity = activity;
		jsonObject = new JSONObject();
		jsonReader = new JSONReader(activity);
		dbHelper = new XaveyDBHelper(activity);
	}
/*
	public LinearLayout writeForm(Form form) throws JSONException {
		String dataJson = form.getForm_json();
		LinearLayout lL = new LinearLayout(activity);
		lL.setBackgroundColor(Color.WHITE);
		lL.setOrientation(LinearLayout.VERTICAL);
		TextView tvFormName = new TextView(activity);
		tvFormName.setText("fn" + form.getForm_title());
		tvFormName.setTextSize(25);
		tvFormName.setTypeface(null, Typeface.BOLD_ITALIC);
		tvFormName.setId(View.getDefaultSize(0, 2000));
		LayoutParams llp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		llp.setMargins(0, 10, 0, 5);
		tvFormName.setLayoutParams(llp);
		lL.addView(tvFormName);

		TextView tvFormTitle = new TextView(activity);
		tvFormTitle.setText("Form subtitle : " + form.getForm_subtitle());
		tvFormTitle.setTextSize(24);
		tvFormTitle.setTypeface(null, Typeface.BOLD_ITALIC);
		llp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		llp.setMargins(0, 5, 0, 10);
		tvFormTitle.setLayoutParams(llp);
		lL.addView(tvFormTitle);

		LinearLayout horizontalLayout;

		ArrayList<HashMap<String, Object>> formFields = jsonReader
				.getFormFields(dataJson);

		for (int i = 0; i < formFields.size(); i++) {
			HashMap<String, Object> fields = formFields.get(i);
			for (Object key : fields.keySet()) {
				if (key.equals("field_type")) {
					if (fields.get(key).equals("text")
							|| fields.get(key).equals("number")) {
						horizontalLayout = new LinearLayout(activity);
						horizontalLayout.setBackgroundColor(Color.WHITE);
						horizontalLayout
								.setOrientation(LinearLayout.HORIZONTAL);
						String fieldName = fields.get("field_name").toString();
						String fieldValue = fields.get("field_value")
								.toString();

						LayoutParams lp;
						lp = new LayoutParams(LayoutParams.WRAP_CONTENT, 40);
						TextView tvFieldName = new TextView(activity);
						tvFieldName.setText(fieldName + "  :");
						tvFieldName.setTextSize(15);
						tvFieldName.setLayoutParams(lp);
						// tvFieldName.setWidth(500);

						// may be change as uneditable Edittext later or may not
						lp = new LayoutParams(LayoutParams.WRAP_CONTENT, 40);
						TextView tvFieldValue = new TextView(activity);
						tvFieldValue.setText(fieldValue);
						tvFieldValue.setTextSize(13);
						tvFieldValue.setLayoutParams(lp);
						// tvFieldValue.setWidth(500);

						horizontalLayout.addView(tvFieldName);
						horizontalLayout.addView(tvFieldValue);
						horizontalLayout.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT));
						lL.addView(horizontalLayout);
					}
				}
			}
		}
		return lL;
	}*/

	public LinearLayout writeDocument(Document document, Form form) {

		JSONArray document_array;
		String documentJSON_To_Submit="";
		try {
			//document_array = jsonReader.getJSONArrayToSubmit(document, form);
			document_array = new JSONArray();
			JSONObject obj = new JSONObject(document.getDocument_json_to_submit());
			document_array.put(obj);
			documentJSON_To_Submit = document_array.getJSONObject(0).toString();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String formJSON = form.getForm_json();

		LinearLayout lL = new LinearLayout(activity);
		lL.setBackgroundColor(Color.WHITE);
		lL.setOrientation(LinearLayout.VERTICAL);
		TextView tvFormName = new TextView(activity);
		tvFormName.setText("" + form.getForm_title());
		tvFormName.setTextSize(15);
		tvFormName.setTypeface(null, Typeface.BOLD_ITALIC);
		tvFormName.setId(View.getDefaultSize(0, 2000));
		LayoutParams llp = new LayoutParams(LayoutParams.MATCH_PARENT,
				50);
		llp.setMargins(5, 10, 5, 20);
		tvFormName.setLayoutParams(llp);
		lL.addView(tvFormName);
//----------------------------------------------------------------

		LinearLayout documentNameLayout = produceVerticalLinearLayout();
		TextView tvDocumentName = new TextView(activity);
		tvDocumentName.setText("Document Name");
		tvDocumentName.setTextSize(15);
		tvDocumentName.setTypeface(null, Typeface.BOLD_ITALIC);
		llp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		llp.setMargins(5, 10, 5, 10);
		tvDocumentName.setLayoutParams(llp);
		documentNameLayout.addView(tvDocumentName);
		TextView documentName = new TextView(activity);
		documentName.setText(document.getDocument_name());
		documentName.setTextSize(15);
		documentName.setTypeface(null, Typeface.BOLD_ITALIC);
		llp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		llp.setMargins(5, 5, 5, 10);
		documentName.setLayoutParams(llp);
		documentNameLayout.addView(documentName);
		documentNameLayout.addView(produceALine(2, Color.BLACK)); //ADAABD
		documentNameLayout.addView(produceALine(40, Color.TRANSPARENT)); //just white spacing

		lL.addView(documentNameLayout);
		LinearLayout horizontalLayout;
		//Typeface zawGyiTypeface = new TypeFaceManager(activity).getZawGyiTypeFace();
		ArrayList<HashMap<String, Object>> formFields = jsonReader
				.getFormFields(formJSON);
		// field_name = cust_name
		// field_type = text
		// field_label = enter customer name
		ArrayList<HashMap<String, String>> documentFields = jsonReader
				.getDocumentFields(documentJSON_To_Submit, "data");
		// field_name = cust_name
		// field_value = zin win htet
		// field_label - enter customer name

		LinkedList<String> used_field_ids = getFieldIDs(documentFields); // collect user typed field_names from document
		formFields = filterFormFieldsByUsedFieldIDs(formFields, used_field_ids); // then filter the form fields by above

		for (int i = 0; i < formFields.size(); i++) {
			HashMap<String, Object> fields = formFields.get(i);
			HashMap<String, String> docFields = documentFields.get(i);
			for (Object key : fields.keySet()) {
				if (key.equals("field_type")) {
					if (fields.get(key).equals("text")
							|| fields.get(key).equals("number")) {
						horizontalLayout = writeHorizontalView(fields, docFields);
						lL.addView(horizontalLayout);
					}
					else if (fields.get(key).equals("date")) {
						horizontalLayout = new LinearLayout(activity);
						horizontalLayout.setBackgroundColor(Color.WHITE);
						horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

						String fieldName = fields.get("field_name").toString();
						// String docName = docFields.get("field_name");
						String fieldValue = docFields.get("field_value");

						LayoutParams lp;
						lp = new LayoutParams(LayoutParams.WRAP_CONTENT, 40);
						lp.setMargins(10, 5, 10, 15);
						TextView tvFieldName = new TextView(activity);
						tvFieldName.setText(fieldName + "  :");
						tvFieldName.setTextSize(15);
						tvFieldName.setLayoutParams(lp);
						// tvFieldName.setWidth(500);

						// may be change as uneditable Edittext later or may not
						lp = new LayoutParams(LayoutParams.WRAP_CONTENT, 40);
						lp.setMargins(10, 5, 10, 15);
						TextView tvFieldValue = new TextView(activity);
						tvFieldValue.setText(fieldValue);
						tvFieldValue.setTextSize(13);
						tvFieldValue.setLayoutParams(lp);
						// tvFieldValue.setWidth(500);

						horizontalLayout.addView(tvFieldName);
						horizontalLayout.addView(tvFieldValue);
						horizontalLayout.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT));
						lL.addView(horizontalLayout);
					}
					else if (fields.get(key).equals("time")) {
						horizontalLayout = new LinearLayout(activity);
						horizontalLayout.setBackgroundColor(Color.WHITE);
						horizontalLayout
								.setOrientation(LinearLayout.HORIZONTAL);

						String fieldName = fields.get("field_name").toString();
						// String docName = docFields.get("field_name");
						String fieldValue = docFields.get("field_value");

						LayoutParams lp;
						lp = new LayoutParams(LayoutParams.WRAP_CONTENT, 40);
						lp.setMargins(10, 5, 10, 15);
						TextView tvFieldName = new TextView(activity);
						tvFieldName.setText(fieldName + "  :");
						tvFieldName.setTextSize(15);
						tvFieldName.setLayoutParams(lp);
						// tvFieldName.setWidth(500);

						// may be change as uneditable Edittext later or may not
						lp = new LayoutParams(LayoutParams.WRAP_CONTENT, 40);
						TextView tvFieldValue = new TextView(activity);
						tvFieldValue.setText(fieldValue);
						tvFieldValue.setTextSize(13);
						tvFieldValue.setLayoutParams(lp);
						// tvFieldValue.setWidth(500);

						horizontalLayout.addView(tvFieldName);
						horizontalLayout.addView(tvFieldValue);
						horizontalLayout.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT));
						lL.addView(horizontalLayout);
					}
					else if(fields.get(key).equals("datetime")){
						horizontalLayout = writeHorizontalView(fields, docFields);
						lL.addView(horizontalLayout);
					}
					else if (fields.get(key).equals("option") || fields.get(key).equals("checklist") || fields.get(key).equals("matrix_option") || fields.get(key).equals("matrix_checklist")) {
						/*horizontalLayout = new LinearLayout(activity);
						horizontalLayout.setBackgroundColor(Color.WHITE);
						horizontalLayout
								.setOrientation(LinearLayout.HORIZONTAL);
						String fieldName = fields.get("field_name").toString();
						String fieldLabel = fields.get("field_label").toString();
						// String docName = docFields.get("field_name");
						String fieldValue = docFields.get("field_value");
						LayoutParams lp;
						lp = new LayoutParams(LayoutParams.WRAP_CONTENT, 40);
						lp.setMargins(10, 5, 10, 15);
						TextView tvFieldName = new TextView(activity);
						tvFieldName.setText(fieldLabel + "  :");
						tvFieldName.setTextSize(15);
						tvFieldName.setLayoutParams(lp);
						// tvFieldName.setWidth(500);
						
						// may be change as uneditable Edittext later or may not
						lp = new LayoutParams(LayoutParams.WRAP_CONTENT, 40);
						TextView tvFieldValue = new TextView(activity);
						tvFieldValue.setText(fieldValue);
						tvFieldValue.setTextSize(15);
						tvFieldValue.setLayoutParams(lp);
						// tvFieldValue.setWidth(500);

						horizontalLayout.addView(tvFieldName);
						horizontalLayout.addView(tvFieldValue);
						horizontalLayout.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT));
						lL.addView(horizontalLayout);*/
						horizontalLayout = writeHorizontalView(fields, docFields);
						lL.addView(horizontalLayout);
					}
					else if(fields.get(key).equals("location")){
						horizontalLayout = writeHorizontalView(fields, docFields);
						lL.addView(horizontalLayout);
					}
					else if(fields.get(key).equals("drawing")){
						// debug docFields , make sure involved path rather than unavailabe
						horizontalLayout = writeHorizontalViewForImage(fields, docFields);
						lL.addView(horizontalLayout);
					}
					else if(fields.get(key).equals("photo")){
						horizontalLayout = writeHorizontalViewForImage(fields, docFields);
						lL.addView(horizontalLayout);
					}
					else if(fields.get(key).equals("")){
						horizontalLayout = writeHorizontalViewForImage(fields, docFields);
						lL.addView(horizontalLayout);
					}
					else if(fields.get(key).equals("")){
						horizontalLayout = writeHorizontalViewForImage(fields, docFields);
						lL.addView(horizontalLayout);
					}
					else if(fields.get(key).equals("")){
						horizontalLayout = writeHorizontalViewForImage(fields, docFields);
						lL.addView(horizontalLayout);
					}
				}
			}
		}

		LayoutParams mainLP = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		
		lL.setLayoutParams(mainLP);
		lL.setGravity(Gravity.CENTER_HORIZONTAL);
		return lL;
	}
	
	// this method remove fields that are not used (skipped)
	private ArrayList<HashMap<String, Object>> filterFormFieldsByUsedFieldIDs(
			ArrayList<HashMap<String, Object>> formFields,
			LinkedList<String> used_field_ids) {
		
		ArrayList<HashMap<String, Object>> filteredFormFields = new ArrayList<HashMap<String,Object>>();;
		
		for(int i=0; i<formFields.size(); i++){
			HashMap<String, Object> fieldMap = formFields.get(i);
			String field_id = fieldMap.get("field_id").toString();
			if(used_field_ids.contains(field_id)){
				filteredFormFields.add(fieldMap);
			}
		}
		return filteredFormFields;
	}
	
	private LinkedList<String> getFieldIDs(
			ArrayList<HashMap<String, String>> documentFields) {
		LinkedList<String> field_ids = new LinkedList<String>();
		for(HashMap<String, String> fieldMap: documentFields){
			String field_id = fieldMap.get("field_id");
			field_ids.addLast(field_id);
		}
		return field_ids;
	}

	private LinkedList<String> getFieldNames(
			ArrayList<HashMap<String, String>> documentFields) {
		LinkedList<String> field_names = new LinkedList<String>();
		for(HashMap<String, String> fieldMap: documentFields){
			String field_name = fieldMap.get("field_name");
			field_names.addLast(field_name);
		}
		return field_names;
	}

	private LinearLayout produceVerticalLinearLayout(){
		LinearLayout linearLayout = new LinearLayout(activity);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setLayoutParams(lp);
		linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		return linearLayout;
	}

	private LinearLayout produceHorizontalLinearLayout(){
		LinearLayout linearLayout = new LinearLayout(activity);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		linearLayout.setLayoutParams(lp);
		return linearLayout;
	}
	
	private View produceALine(int height, String sharpColor){
		View view = new View(activity);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				height);
		lp.setMargins(0, 4, 0, 6);
		int bgColor = Color.parseColor(sharpColor);
		view.setBackgroundColor(bgColor);
		view.setLayoutParams(lp);
		return view;
	}
	
	private View produceALine(int height, int color){
		View view = new View(activity);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				height);
		view.setBackgroundColor(color);
		view.setLayoutParams(lp);
		return view;
	}
	
	private LinearLayout writeHorizontalView(HashMap<String, Object> fields, HashMap<String, String> docFields ){
		// following link may be needed at future
		// http://stackoverflow.com/questions/11111129/android-textview-individual-line-spacing-for-each-line
		
		LinearLayout horizontalLayout = new LinearLayout(activity);
		horizontalLayout.setBackgroundColor(Color.WHITE);
		horizontalLayout
				.setOrientation(LinearLayout.VERTICAL);

		String fieldName = fields.get("field_name").toString();
		String fieldLabel = fields.get("field_label").toString();
		String fieldValue = docFields.get("field_value");
		// String docName = docFields.get("field_name");
		

		LayoutParams lp;
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(10, 5, 10, 15);
		TextView tvFieldName = new TextView(activity);
		tvFieldName.setText(fieldLabel + " ");
		tvFieldName.setTextSize(15);
		tvFieldName.setLayoutParams(lp);
		tvFieldName.setGravity(Gravity.CENTER_VERTICAL);
		tvFieldName.setBackgroundColor(Color.LTGRAY); // just for development test
		tvFieldName.setPadding(0, 10, 0, 10);
		// tvFieldName.setWidth(500);

		// may be change as uneditable Edittext later or may not
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(10, 5, 10, 15);
		TextView tvFieldValue = new TextView(activity);
		//tvFieldValue.setTypeface(zawGyiTypeface);
		tvFieldValue.setText(" - "+fieldValue);
		tvFieldValue.setTextSize(15);
		tvFieldValue.setLayoutParams(lp);
		// tvFieldValue.setWidth(500);
		tvFieldValue.setGravity(Gravity.CENTER_VERTICAL);
		tvFieldValue.setPadding(0, 10, 0, 10);
		
		horizontalLayout.addView(tvFieldName);
		horizontalLayout.addView(tvFieldValue);
		horizontalLayout.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		
		horizontalLayout.addView(produceALine(1, "#ADAABD"));
		
		return horizontalLayout;
	}

	private LinearLayout writeHorizontalViewForImage(HashMap<String, Object> fields, HashMap<String, String> docFields ){
		// following link may be needed at future
		// http://stackoverflow.com/questions/11111129/android-textview-individual-line-spacing-for-each-line
		
		LinearLayout horizontalLayout = new LinearLayout(activity);
		horizontalLayout.setBackgroundColor(Color.WHITE);
		horizontalLayout
				.setOrientation(LinearLayout.VERTICAL);
		String fieldName = fields.get("field_name").toString();
		String fieldLabel = fields.get("field_label").toString();
		String imageID = docFields.get("field_value");
		Image image = new Image();
		if(imageID.equals("unavailable")||imageID.endsWith(".jpeg")||imageID.endsWith(".jpg")){
			image = dbHelper.getImagePathByImageName(fieldName);
		}
		else{
			image = dbHelper.getImageByImageID(imageID);
		}
		String fieldPath = image.getImage_path();
		// String docName = docFields.get("field_name");

		LayoutParams lp;
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(10, 5, 10, 15);
		TextView tvFieldName = new TextView(activity);
		tvFieldName.setText(fieldLabel + " ");
		tvFieldName.setTextSize(15);
		tvFieldName.setLayoutParams(lp);
		tvFieldName.setGravity(Gravity.CENTER_VERTICAL);
		tvFieldName.setBackgroundColor(Color.LTGRAY); // just for development test
		tvFieldName.setPadding(0, 10, 0, 10);
		// tvFieldName.setWidth(500);

		// may be change as uneditable Edittext later or may not
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(10, 5, 10, 15);
		
		
		
		lp = new LayoutParams(200, 200);
		ImageView imageView = new ImageView(activity);
		ImageSavingManager.loadImageFromLocal(fieldPath, imageView);
		imageView.setPadding(0, 10, 0, 10);
		imageView.setLayoutParams(lp);
		
		horizontalLayout.addView(tvFieldName);
		horizontalLayout.addView(imageView);
		horizontalLayout.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		
		horizontalLayout.addView(produceALine(1, "#ADAABD"));
		
		return horizontalLayout;
	}
	
	public Bitmap convertByteArrayToBitmap(byte[] byteArrayImg) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bmp = BitmapFactory.decodeByteArray(byteArrayImg, 0,
				byteArrayImg.length, options);
		return bmp;
	}

	public String toJSONString(String formName, String formTitle,
			ArrayList<HashMap<String, String>> formFieldList) {
		// method's parameters need to be changed later according to the server
		// JSON
		try {
			JSONArray fieldsArray = new JSONArray();
			JSONObject fieldObject = new JSONObject();

			for (int i = 0; i < formFieldList.size(); i++) {
				HashMap<String, String> map = formFieldList.get(i);
				fieldObject.put("field_name", map.get("field_name"));
				fieldObject.put("field_type", map.get("field_type"));
				fieldObject.put("field_hint", map.get("field_hint"));
				fieldObject.put("form_list_title", map.get("form_list_title"));
				fieldObject.put("form_list_subtitle",
						map.get("form_list_subtitle"));
				fieldObject.put("field_required", map.get("field_required"));
				fieldObject.put("field_value", map.get("field_value"));
				fieldsArray.put(fieldObject.toString());
			}
			jsonObject.put("form_fields", fieldsArray);
			jsonObject.put("form_name", formName);
			jsonObject.put("form_title", formTitle);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String s = jsonObject.toString();
		return s;
	}
}

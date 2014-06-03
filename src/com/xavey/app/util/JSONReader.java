package com.xavey.app.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xavey.app.model.Form;

// (1) getFile()
// (2) and getJSON(getFile())
// (3) and that return form
public class JSONReader {

	Activity activity;
	
	public JSONReader(Activity activity){
		this.activity = activity;
	}
	
	public LinearLayout readForm(String fileName) {
		LinearLayout lL = new LinearLayout(activity);
		Form form = getJSON(getFile(fileName));
		TextView tv1 = new TextView(getActivity());
		tv1.setText("Form Name : " + form.getFormName());
		tv1.setTextSize(12);
		tv1.setId(View.getDefaultSize(0, 2000));
		lL.addView(tv1);
		TextView tv2 = new TextView(getActivity());
		tv2.setText("Form Title : " + form.getFormTitle());
		tv2.setTextSize(12);
		lL.addView(tv2);

		for (int i = 0; i < form.getFormFields().size(); i++) {
			HashMap<String, String> fields = form.getFormFields().get(i);
			for (Object key : fields.keySet()) {
				if (key.equals("field_type")) {

					if (fields.get(key).equals("text")) {
						EditText ed1 = new EditText(getActivity());
						for (Object key2 : fields.keySet())
							if (key2.equals("field_hint"))
								ed1.setHint(fields.get(key2));
						ed1.setTextSize(12);
						ed1.setSingleLine(true);
						lL.addView(ed1);
					} else if (fields.get(key).equals("multiline-text")) {
						EditText ed2 = new EditText(getActivity());
						for (Object key2 : fields.keySet())
							if (key2.equals("field_hint"))
								ed2.setHint(fields.get(key2));
						ed2.setTextSize(12);
						ed2.setSingleLine(false);
						lL.addView(ed2);
					}
				}
			}
		}
		return lL;
	}

	public String getFile(String fileName) {
		String jsonStr = "";
		try {
			InputStream is = getActivity().getAssets().open(fileName);
			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			is.close();
			jsonStr = new String(buffer, "UTF-8");
			Log.i("JSON from File", jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return jsonStr;
	}
	
	public Form getJSON(String str) {
		ArrayList<HashMap<String, String>> fieldList = new ArrayList<HashMap<String, String>>();
		Form form = new Form();
		try {
			JSONObject json = new JSONObject(str);
			form.setFormName(json.getString("form_name"));
			form.setFormTitle(json.getString("form_title"));
			JSONArray form_fields = json.getJSONArray("form_fields");
			for (int i = 0; i < form_fields.length(); i++) {

				JSONObject jChild = form_fields.getJSONObject(i);
				HashMap<String, String> fields = new HashMap<String, String>();
				fields.put("field_name", jChild.getString("field_name"));
				fields.put("field_type", jChild.getString("field_type"));
				fields.put("field_hint", jChild.getString("field_hint"));
				fields.put("form_list_title",
						jChild.getString("form_list_title"));
				fields.put("form_list_subtitle",
						jChild.getString("form_list_subtitle"));
				fields.put("field_required", jChild.getString("field_required"));
				fieldList.add(fields);
			}
			form.setFormFields(fieldList);

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		return form;
	}
	
	public Form getFormFromFile(String fileName){
		return getJSON(getFile(fileName));
	}
	
	//getters and setters
	public Activity getActivity() {
		return activity;
	}
	
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
}

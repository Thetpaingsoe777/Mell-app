package com.xavey.android.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.xavey.android.R;
import com.xavey.android.db.XaveyDBHelper;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class TextSetAdapter extends BaseAdapter {

	private Activity activity;
	private static LayoutInflater inflater = null;
	XaveyDBHelper dbHelper;
	private ArrayList<HashMap<String, String>> data;
	private ArrayList<HashMap<String, String>> refData;

	public TextSetAdapter(Activity activity,
			ArrayList<HashMap<String, String>> data) {
		this.activity = activity;
		this.data = data;
		this.refData = data;
		dbHelper = new XaveyDBHelper(this.activity);
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public ArrayList<HashMap<String, String>> getData() {
		return data;
	}

	public void setData(ArrayList<HashMap<String, String>> data) {
		this.data = data;
	}

	public ArrayList<HashMap<String, String>> getRefData() {
		return refData;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	public static class ViewHolder {
		public TextView tvLabel;
		public EditText inputText;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rootView = convertView;
		ViewHolder holder;
		if(convertView==null){
			rootView = inflater.inflate(R.layout.text_set_item, null);
			holder = new ViewHolder();
			holder.tvLabel = (TextView) rootView.findViewById(R.id.label_text_set);
			holder.tvLabel.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			holder.inputText = (EditText) rootView.findViewById(R.id.input_text_set);
			rootView.setTag(holder);
		}
		else{
			holder = (ViewHolder) rootView.getTag();
		}
		HashMap<String, String> map = data.get(position);
		String label=map.get("label");

		holder.tvLabel.setText(label);
		return rootView;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

}

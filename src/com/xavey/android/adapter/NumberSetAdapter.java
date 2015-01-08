package com.xavey.android.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.xavey.android.R;
import com.xavey.android.adapter.TextSetAdapter.ViewHolder;
import com.xavey.android.db.XaveyDBHelper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class NumberSetAdapter extends BaseAdapter {

	private Activity activity;
	private static LayoutInflater inflater = null;
	XaveyDBHelper dbHelper;
	private ArrayList<HashMap<String, String>> data;
	
	public NumberSetAdapter(Activity activity, ArrayList<HashMap<String, String>> data){
		this.activity = activity;
		this.data = data;
		dbHelper = new XaveyDBHelper(this.activity);
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	public ArrayList<HashMap<String, String>> getData() {
		return data;
	}

	public void setData(ArrayList<HashMap<String, String>> data) {
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}
	
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static class ViewHolder {
		public TextView tvLabel;
		public EditText inputNumber;
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rootView = convertView;
		ViewHolder holder;
		if(convertView==null){
			rootView = inflater.inflate(R.layout.number_set_item, null);
			holder = new ViewHolder();
			holder.tvLabel = (TextView) rootView.findViewById(R.id.label_number_set);
			holder.inputNumber = (EditText) rootView.findViewById(R.id.input_number_set);
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

}

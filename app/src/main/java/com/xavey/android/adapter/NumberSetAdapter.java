package com.xavey.android.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.xavey.android.R;
import com.xavey.android.db.XaveyDBHelper;

public class NumberSetAdapter extends BaseAdapter {

	private Activity activity;
	private static LayoutInflater inflater = null;
	XaveyDBHelper dbHelper;
	public String[] CurrentItems;
	private ArrayList<HashMap<String, String>> data;
	private ArrayList<HashMap<String, String>> refData;
	
	public NumberSetAdapter(Activity activity, ArrayList<HashMap<String, String>> data){
		this.activity = activity;
		this.data = data;
		this.refData = data;
		CurrentItems = new String[data.size()];
		dbHelper = new XaveyDBHelper(this.activity);
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		for(int i=0;i<data.size();i++)
//	    {
//	       //myList.put(i,"");
//	    }
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
	
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static class ViewHolder {
		public TextView tvLabel;
		public EditText inputNumber;
		public int ref;
	}

	public void setView(int startPosition, int endPosition){
		
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rootView = convertView;
		final ViewHolder holder;
	    final int pos=position;
		if(convertView==null){
			rootView = inflater.inflate(R.layout.number_set_item, null);
			holder = new ViewHolder();
			holder.tvLabel = (TextView) rootView.findViewById(R.id.label_number_set);
			holder.tvLabel.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			holder.inputNumber = (EditText) rootView.findViewById(R.id.input_number_set);
			rootView.setTag(holder);
		}
		else{
			holder = (ViewHolder) rootView.getTag();
		}

		holder.inputNumber.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,
                    int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start,
                    int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
            	CurrentItems[holder.ref] = s.toString();
               // myList.put(pos,s.toString().trim());
            }
        });
		
		HashMap<String, String> map = data.get(position);
		String label=map.get("label");
		holder.ref=position;
		holder.tvLabel.setText(label);
		holder.inputNumber.setText(CurrentItems[position]);
		
		return rootView;
	}
}

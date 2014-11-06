package com.xavey.app.adapter;

import java.util.ArrayList;

<<<<<<< HEAD
import android.app.Activity;
=======
>>>>>>> ce4c53483e36d66116a944fa419f4f5c31caf09c
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xavey.app.R;
import com.xavey.app.model.Form;
<<<<<<< HEAD
import com.xavey.app.util.TypeFaceManager;
import com.xavey.app.util.XaveyProperties;
=======
>>>>>>> ce4c53483e36d66116a944fa419f4f5c31caf09c

public class FormAdapter extends BaseAdapter {

	Context activity;
	LayoutInflater inflater;
	Intent i ;
	DisplayMetrics metrics;
	int deviceWidth;
	int deviceHeight;
	int formImage = R.drawable.form_icon_1;
<<<<<<< HEAD

	ArrayList<Form> formList;

=======
	
	ArrayList<Form> formList;
	
>>>>>>> ce4c53483e36d66116a944fa419f4f5c31caf09c
	public FormAdapter(Context a, ArrayList<Form> formList){
		activity = a;
		this.formList = formList;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return formList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	public Form getSelectedForm(int position){
		return formList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public static class ViewHolder{
		TextView formTitle;
		String formID;

		public String getFormID() {
			return formID;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		if(convertView==null){
			convertView = inflater.inflate(R.layout.gridview_item, parent, false);
			holder.formTitle = (TextView) convertView.findViewById(R.id.tvFormName);
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}
		Form form = formList.get(position);
		holder.formTitle.setText(form.getForm_title()); // subtitle = name , need to change it after confirmed
<<<<<<< HEAD
		XaveyProperties xaveyProperties = new XaveyProperties();
		String zawGyiFontStatus = xaveyProperties.getZawgyiFontStatus();
		TypeFaceManager tfManager=new TypeFaceManager((Activity) activity);
		if(xaveyProperties.getZawgyiFontStatus().equals("on")){
			holder.formTitle.setTypeface(tfManager.getZawGyiTypeFace());
		}
		holder.formID = form.getForm_id();
		return convertView;
	}
=======
		holder.formID = form.getForm_id();
		return convertView;
	}

>>>>>>> ce4c53483e36d66116a944fa419f4f5c31caf09c
}

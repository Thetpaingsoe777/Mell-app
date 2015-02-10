package com.xavey.android.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xavey.android.R;
import com.xavey.android.model.Form;
import com.xavey.android.util.TypeFaceManager;
import com.xavey.android.util.XaveyProperties;

public class FormAdapter extends BaseAdapter {

	Context activity;
	LayoutInflater inflater;
	Intent i ;
	DisplayMetrics metrics;
	int deviceWidth;
	int deviceHeight;
	int formImage = R.drawable.form_icon_1;
	Drawable availableFormIcon;
	Drawable unavailableFormIcon;
	ArrayList<Form> formList;

	public FormAdapter(Context a, ArrayList<Form> formList){
		activity = a;
		this.formList = formList;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		availableFormIcon = activity.getResources().getDrawable(R.drawable.form_icon_1);
		availableFormIcon.setBounds(0, 0, 200, 290);
		unavailableFormIcon = activity.getResources().getDrawable(R.drawable.form_icon_2);
		unavailableFormIcon.setBounds(0, 0, 200, 290);
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
		if(form.isImageSynced()){
			holder.formTitle.setCompoundDrawables(null, availableFormIcon, null, null);
		}else{
			holder.formTitle.setCompoundDrawables(null, unavailableFormIcon, null, null);
		}
		XaveyProperties xaveyProperties = new XaveyProperties();
		String zawGyiFontStatus = xaveyProperties.getZawgyiFontStatus();
		TypeFaceManager tfManager=new TypeFaceManager((Activity) activity);
		if(xaveyProperties.getZawgyiFontStatus().equals("on")){
			holder.formTitle.setTypeface(tfManager.getTypeFace());
		}
		holder.formID = form.getForm_id();
		return convertView;
	}
}

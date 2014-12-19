package com.xavey.android.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xavey.android.R;
import com.xavey.android.db.XaveyDBHelper;
import com.xavey.android.model.SyncImage;
import com.xavey.android.util.XaveyUtils;

public class ImageAdapter extends BaseAdapter {
	
	Context activity;
	LayoutInflater inflater;
	ArrayList<HashMap<String, String>> data_set_values;
	XaveyDBHelper dbHelper;
	XaveyUtils xaveyUtils;

	public ImageAdapter (Context a, ArrayList<HashMap<String,String>> data_set_values){
		activity = a;
		this.data_set_values = data_set_values;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dbHelper = new XaveyDBHelper(activity);
		xaveyUtils = new XaveyUtils(a);
	}
	
	@Override
	public int getCount() {
		return data_set_values.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static class ViewHolder{
		TextView imageTitle;
		boolean isSelected;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		if(convertView==null){
			convertView = inflater.inflate(R.layout.image_dataset_item, parent, false);
			holder.imageTitle = (TextView) convertView.findViewById(R.id.tvImageData);
			holder.isSelected = false;
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}
		HashMap<String,String> map = data_set_values.get(position);
		// not used the following so far
//		String value = map.get("value");
//		String field_skip = map.get("field_skip");
//		String extra = map.get("extra");
		String label = map.get("label");
		String image = map.get("image");

		SyncImage syncImage = dbHelper.getSyncImageByImageID(image);
		byte[] byteArray = syncImage.getImgByte();
		BitmapDrawable bd = xaveyUtils.convertByteArrayToBitMapDrawable(byteArray);
		bd.setBounds(0, 0, 200, 200);
		holder.imageTitle.setCompoundDrawables(null, bd, null, null);
		holder.imageTitle.setText(label);
		
//		if(holder.isSelected){
//			holder.imageTitle.setBackgroundColor(Color.BLUE);
//			holder.isSelected = false;
//		}else{
//			holder.imageTitle.setBackgroundColor(Color.WHITE);
//			holder.isSelected = true;
//		}
		return convertView;
	}

}

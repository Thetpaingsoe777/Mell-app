package com.xavey.android.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.xavey.android.R;
import com.xavey.android.db.XaveyDBHelper;
import com.xavey.android.model.SyncImage;
import com.xavey.android.util.XaveyUtils;

public class RatingSetAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	XaveyDBHelper dbHelper;
	XaveyUtils xaveyUtils;

	public RatingSetAdapter(Activity a, ArrayList<HashMap<String, String>> data){
		activity = a;
		this.data = data;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dbHelper = new XaveyDBHelper(activity);
		xaveyUtils = new XaveyUtils(activity);
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
	public long getItemId(int position) {
		return position;
	}

	public static class ViewHolder {
		public TextView tvMin;
		public TextView tvMax;
		public ImageView image;
		public TextView tvLabel;
		public RatingBar ratingBar;
		public LinearLayout ratingBarLayout; // <-- just for tag layout_id
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rootView = convertView;
		ViewHolder holder;
		if(convertView==null){
			rootView = inflater.inflate(R.layout.rating_set_item, null);
			holder = new ViewHolder();
			holder.tvMin = (TextView) rootView.findViewById(R.id.tvMin_ratingSet);
			holder.tvMax = (TextView) rootView.findViewById(R.id.tvMax_ratingSet);
			holder.image = (ImageView) rootView.findViewById(R.id.image_ratingSet);
			holder.tvLabel = (TextView) rootView.findViewById(R.id.label_ratingSet);
			holder.ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar_ratingSet);
			holder.ratingBarLayout = (LinearLayout) rootView.findViewById(R.id.ratingBarLayout_ratingSet);
			rootView.setTag(holder);
		}
		else{
			holder = (ViewHolder) rootView.getTag();
		}
		
		HashMap<String, String> map = data.get(position);
		String minLabel = map.get("minLabel");
		String maxLabel = map.get("maxLabel");
		int maxValue = Integer.parseInt(map.get("maxValue"));
		String value = map.get("value");
		String field_skip = map.get("field_skip");
		String image = map.get("image");
		String label = map.get("label");
		String extra = map.get("extra");
		
		holder.tvMin.setText(minLabel);
		holder.tvMax.setText(maxLabel);
		holder.ratingBar.setStepSize(1);
		holder.ratingBar.setRating(1);
		holder.ratingBar.setNumStars(maxValue);
		holder.ratingBar.setTag(R.id.list_item_value, value);
		holder.tvLabel.setText(label);
		
		if(image.equals("#no_value#")){
			holder.image.setVisibility(View.GONE);
		}
		else{
			holder.tvLabel.setVisibility(View.GONE);
			SyncImage syncImage = dbHelper.getSyncImageByImageID(image);
			byte[] byteArray = syncImage.getImgByte();
			Bitmap bitmapImage = xaveyUtils.convertByteArrayToBitmap(byteArray);
			holder.image.setImageBitmap(bitmapImage);
			
			// following code is more beautiful , but setBackgroundDrawable method waas deprecated
//			Drawable drawable = new BitmapDrawable(activity.getResources(), bitmapImage);
//			holder.image.setBackgroundDrawable(drawable);
		}
		holder.ratingBarLayout.setTag(R.id.layout_id, "ratingBarLayout");
		
		return rootView;
	}

}

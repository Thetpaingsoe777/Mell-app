package com.xavey.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.xavey.android.ApplicationValues.LOGIN_TYPE;
import com.xavey.android.adapter.FormAdapter;
import com.xavey.android.adapter.FormAdapter.ViewHolder;
import com.xavey.android.db.XaveyDBHelper;
import com.xavey.android.model.Form;
import com.xavey.android.util.GPSTracker;
import com.xavey.android.util.SessionManager;
import com.xavey.android.util.ToastManager;
import com.xavey.android.util.XaveyUtils;

public class HomeFragment extends Fragment implements OnItemClickListener{

	public static final String ITEM_NAME = "Home_";
	SessionManager session;
	GridView formGridView;
	Activity activity;
	TextView welcomeText;
	FormAdapter formAdapter;
	ArrayList<Form> formList;
	XaveyDBHelper dbHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.home_fragment, container,
				false);

		loadUI(view);

		byte[] logoByteArray = ApplicationValues.loginUser.getLogoImage();
		if(logoByteArray!=null){
//			Bitmap logoBitMap = BitmapFactory.decodeByteArray(logoByteArray , 0, logoByteArray .length);
//			BitmapDrawable bd = new BitmapDrawable(getResources(), logoBitMap);
//			activity.getActionBar().setIcon(bd);
			XaveyUtils xaveyUtils = new XaveyUtils(activity);
			BitmapDrawable bd = xaveyUtils.convertByteArrayToBitmapDrawable(logoByteArray);
			activity.getActionBar().setIcon(bd);
		}else{
			activity.getActionBar().setIcon(R.drawable.home);
		}

		activity.getActionBar().setTitle("Home");
		refresh();
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refresh();
	}

	private void refresh(){
		//formList = dbHelper.getAllForms(); // dbHelper.getUserRelatedForm
		formList = dbHelper.getAssignedFormsByUserID(ApplicationValues.loginUser.getUser_id());
		formAdapter = new FormAdapter(getActivity(), formList);
		formGridView.setAdapter(formAdapter);
		formGridView.setOnItemClickListener(this);
	}

	private void loadUI(View v){
		activity = getActivity();
		welcomeText = (TextView) v.findViewById(R.id.tvWelcomeText);
		formGridView = (GridView) v.findViewById(R.id.gridViewHOME);
		dbHelper = new XaveyDBHelper(getActivity().getApplicationContext());
		if(isTablet(v.getContext()))
			formGridView.setNumColumns(3);
		else
			formGridView.setNumColumns(2);
		setWelcomeText();
	}

	public static boolean isTablet(Context context) {
	    return (context.getResources().getConfiguration().screenLayout
	            & Configuration.SCREENLAYOUT_SIZE_MASK)
	            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	private void setWelcomeText(){
		session = new SessionManager(activity.getApplicationContext());
		HashMap<String, String> user = session.getUserDetails();
		welcomeText.setText(""+user.get(SessionManager.KEY_NAME));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		//Intent i = new Intent(activity, DocumentInputActivity.class);
		GPSTracker gps = GPSTracker.getInstance();
        gps.set_context(view.getContext());
        gps.getLocation(view.getContext()); //just poke the funciton
		ViewHolder holder = (ViewHolder)view.getTag();
		String id_from_holder = holder.getFormID();
		Form clickedForm = dbHelper.getFormByFormID(id_from_holder);

		if(ApplicationValues.CURRENT_LOGIN_MODE==LOGIN_TYPE.DEMO_LOGIN){
			clickedForm.setImageSynced(true);
		}

		if(clickedForm.isImageSynced()){
			// image is synced here
            Intent i = new Intent(activity, OneQuestionOneView.class);
            //either true or false
            i.putExtra("form_location_required",clickedForm.isForm_location_required());

            if(clickedForm.isForm_location_required()) //
			{
				if(gps.canGetLocation()){
					i.putExtra("formID", id_from_holder);
                    //i.putExtra("lat", gps.getLatitude()+"");
                    //i.putExtra("lng", gps.getLongitude()+"");
                    i.putExtra("form_location_required",clickedForm.isForm_location_required());
					startActivity(i);
				}
				else{
                    gps.showSettingsAlert("GPS Setting","This form needs GPS location. Please turn on your GPS to continue.");
				}
			}
			else{
				i.putExtra("formID", id_from_holder);
				startActivity(i);
			}
		}
		else{
			// image synchronizing not finished
			ToastManager toast = new ToastManager(activity);
			toast.xaveyToast(null, "Sorry, some of question images are still loading.");
		}
	}
}

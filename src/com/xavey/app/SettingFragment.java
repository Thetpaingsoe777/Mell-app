package com.xavey.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.xavey.app.util.IPManager;

import android.app.Fragment;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingFragment extends Fragment {

	public static final String ITEM_NAME = "Setting_";
	
	TextView errorMsg;
	EditText etServerIPAddress;
	Button btnSave;
	IPManager ipManager;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setting_fragment, container,
				false);
		loadUI(view);
		getActivity().getActionBar().setIcon(R.drawable.setting);
		getActivity().getActionBar().setTitle("Setting");
		
		etServerIPAddress.setText(ipManager.getServerIPAddress());
		
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String ipAddress = etServerIPAddress.getText().toString();
				if(ipAddress.trim().length()==0){
					errorMsg.setTextColor(Color.parseColor("#E61C1F"));
					errorMsg.setText("IP address could not be blank!!");
				}
				else{
					ipManager.saveServerIPAddress(ipAddress);
					errorMsg.setTextColor(Color.parseColor("#3B7AED"));
					errorMsg.setText("successfully saved");
					Properties prop = new Properties();
					InputStream in = getClass().getResourceAsStream("/com/xavey/app/util/xavey_properties.properties");
					try {
						prop.load(in);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					prop.setProperty("serverIP", ipAddress);
				}
			}
		});
		return view;
	}
	
	private void loadUI(View v){
		etServerIPAddress = (EditText) v.findViewById(R.id.etServerIpAddress);
		btnSave = (Button) v.findViewById(R.id.btnSaveIP_Setting);
		errorMsg = (TextView) v.findViewById(R.id.tvErrorMsg);
		ipManager = new IPManager(getActivity().getApplicationContext());
		MainActivity.optionMenu.getItem(0).setVisible(false);
	}
	

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		MainActivity.optionMenu.getItem(0).setVisible(true);
	}
	
}
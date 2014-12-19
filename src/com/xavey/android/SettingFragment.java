package com.xavey.android;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.xavey.android.model.FONT;

public class SettingFragment extends Fragment {

	public static final String ITEM_NAME = "Setting_";
	
	RadioButton rb_default, rb_zawgyi, rb_myanmar3;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setting_fragment, container,
				false);
		loadUI(view);
		getActivity().getActionBar().setIcon(R.drawable.setting);
		getActivity().getActionBar().setTitle("Setting");
		
		return view;
	}
	
	private void loadUI(View v){
		loadFontSetting(v);
		MainActivity.optionMenu.getItem(0).setVisible(false);
	}
	
	private void loadFontSetting(View v){
		rb_default = (RadioButton) v.findViewById(R.id.rb_default);
		rb_zawgyi = (RadioButton) v.findViewById(R.id.rb_zawgyi);
		rb_myanmar3 = (RadioButton) v.findViewById(R.id.rb_myanmar3);
		
		switch (ApplicationValues.CURRENT_FONT) {
		case DEFAULT_:
			rb_default.setChecked(true);
			break;
		case ZAWGYI:
			rb_zawgyi.setChecked(true);
			break;
		case MYANMAR3:
			rb_myanmar3.setChecked(true);
			break;
		default:
			break;
		}
		
		rb_default.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ApplicationValues.CURRENT_FONT = FONT.DEFAULT_;
			}
		});

		rb_zawgyi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ApplicationValues.CURRENT_FONT = FONT.ZAWGYI;
			}
		});
		rb_myanmar3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ApplicationValues.CURRENT_FONT = FONT.MYANMAR3;
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		MainActivity.optionMenu.getItem(0).setVisible(true);
	}
	
}
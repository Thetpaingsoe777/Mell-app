package com.xavey.android;

import android.app.Fragment;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xavey.android.db.XaveyDBHelper;
import com.xavey.android.model.FONT;
import com.xavey.android.model.SYNC;
import com.xavey.android.util.Utils;
import com.xavey.android.util.XaveyUtils;

public class SettingFragment extends Fragment {

	public static final String ITEM_NAME = "Setting_";
	
	RadioButton rb_default, rb_zawgyi, rb_myanmar3,rb_autoSync,rb_off;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.setting_fragment, container,
				false);
		loadUI(view);
        getActivity().getActionBar().setIcon(R.drawable.setting);
		getActivity().getActionBar().setTitle("Setting");

        RadioGroup rgFont = (RadioGroup) view.findViewById(R.id.fontRadioGroupSetting);
        RadioGroup rgSync = (RadioGroup) view.findViewById(R.id.syncRadioGroup);

        rgFont.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(rb_default.isChecked()){
                    ApplicationValues.CURRENT_FONT = FONT.DEFAULT_;
                }
                if(rb_zawgyi.isChecked()){
                    ApplicationValues.CURRENT_FONT = FONT.ZAWGYI;
                }
                if(rb_myanmar3.isChecked()){
                    ApplicationValues.CURRENT_FONT = FONT.MYANMAR3;
                }
                Utils.setAppPreference(getActivity());
            }
        });

        rgSync.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(rb_autoSync.isChecked()){
                    ApplicationValues.CURRENT_SYNC=SYNC.AUTO_SYNC;
                }
                if(rb_off.isChecked()){
                    ApplicationValues.CURRENT_SYNC=SYNC.OFF;
                }
                Utils.setAppPreference(getActivity());
            }
        });

		return view;
	}
	
	private void loadUI(View v){
        rb_default = (RadioButton) v.findViewById(R.id.rb_default);
        rb_zawgyi = (RadioButton) v.findViewById(R.id.rb_zawgyi);
        rb_myanmar3 = (RadioButton) v.findViewById(R.id.rb_myanmar3);
        rb_autoSync =(RadioButton)v.findViewById(R.id.rb_autoSync);
        rb_off =(RadioButton)v.findViewById(R.id.rb_off);
		loadFontSetting(v);
		MainActivity.optionMenu.getItem(0).setVisible(false);
	}

	private void loadFontSetting(View v){
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

        switch (ApplicationValues.CURRENT_SYNC){
            case OFF:
                rb_off.setChecked(true);
                break;
            case AUTO_SYNC:
                rb_autoSync.setChecked(true);
                break;
            default:break;
        }
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		MainActivity.optionMenu.getItem(0).setVisible(true);
	}
	
}
package com.xavey.android.util;

import com.xavey.android.ApplicationValues;
import com.xavey.android.model.FONT;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class TypeFaceManager {
	Typeface zawgyiTypeFace, myanmar3TypeFace;
	Activity activity_;

    XaveyProperties xaveyProperties;
    String zawGyiFontStatus;

	public TypeFaceManager(Activity act) {
		activity_ = act;
		zawgyiTypeFace = Typeface.createFromAsset(activity_.getAssets(),
				"fonts/zawgyione2008.ttf");
		myanmar3TypeFace = Typeface.createFromAsset(activity_.getAssets(),
				"fonts/myanmar3.ttf");

        xaveyProperties = new XaveyProperties();
        zawGyiFontStatus = xaveyProperties.getZawgyiFontStatus();
	}

    public void setTypeFace(View v) {
        String status = zawGyiFontStatus;
        if (v.getClass().getName().toString().equals("android.widget.TextView")
                && status.equals("on")) {
            TextView tv = (TextView) v;
            if (this.getTypeFace() != null)
                tv.setTypeface(getTypeFace());
        } else if (v.getClass().getName().toString()
                .equals("android.widget.EditText")
                && status.equals("on")) {
            EditText edt = (EditText) v;
            if (getTypeFace() != null)
                edt.setTypeface(getTypeFace());
        } else if (v.getClass().getName().toString()
                .equals("android.widget.RadioButton")
                && status.equals("on")) {
            RadioButton rdButton = (RadioButton) v;
            if (getTypeFace() != null)
                rdButton.setTypeface(getTypeFace());
        } else if (v.getClass().getName().toString()
                .equals("android.widget.CheckBox")
                && status.equals("on")) {
            CheckBox chkBox = (CheckBox) v;
            if (getTypeFace() != null)
                chkBox.setTypeface(getTypeFace());
        } else if (v.getClass().getName().toString()
                .equals("android.widget.Button")
                && status.equals("on")) {
            Button button = (Button) v;
            if (getTypeFace() != null)
                button.setTypeface(getTypeFace());
        }
    }

	public Typeface getTypeFace() {
		switch (ApplicationValues.CURRENT_FONT) {
		case DEFAULT_:
			return null;
		case ZAWGYI:
			return zawgyiTypeFace;
		case MYANMAR3:
			return myanmar3TypeFace;
		default:
			return null;
		}
	}

}

package com.xavey.android.util;

import com.xavey.android.ApplicationValues;
import com.xavey.android.model.FONT;

import android.app.Activity;
import android.graphics.Typeface;

public class TypeFaceManager {
	Typeface zawgyiTypeFace, myanmar3TypeFace;
	Activity activity_;

	public TypeFaceManager(Activity act) {
		activity_ = act;
		zawgyiTypeFace = Typeface.createFromAsset(activity_.getAssets(),
				"fonts/zawgyione2008.ttf");
		myanmar3TypeFace = Typeface.createFromAsset(activity_.getAssets(),
				"fonts/myanmar3.ttf");
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

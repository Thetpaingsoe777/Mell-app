package com.xavey.app.util;

import android.app.Activity;
import android.graphics.Typeface;

public class TypeFaceManager {
	Typeface zawgyiTypeFace, myanmar3TypeFace;
	Activity activity_;
	
	public TypeFaceManager(Activity act){
		activity_ = act;
		zawgyiTypeFace = Typeface.createFromAsset(activity_.getAssets(), "fonts/zawgyi.ttf");
		myanmar3TypeFace = Typeface.createFromAsset(activity_.getAssets(), "fonts/myanmar3.ttf");
	}
	
	public Typeface getZawGyiTypeFace(){
		return zawgyiTypeFace;
	}
	
	public Typeface getMyanmar3TypeFace(){
		return myanmar3TypeFace;
	}
	
}

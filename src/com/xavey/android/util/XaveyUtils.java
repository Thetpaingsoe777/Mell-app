package com.xavey.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

public class XaveyUtils {
	
	Context act;
	
	public XaveyUtils(Context a){
		act = a;
	}
	
	public BitmapDrawable convertByteArrayToBitmapDrawable(byte[] byteArray){
		Bitmap logoBitMap = BitmapFactory.decodeByteArray(byteArray , 0, byteArray.length);
		BitmapDrawable bd = new BitmapDrawable(act.getResources(), logoBitMap);
		return bd;
	}
	
	public Bitmap convertByteArrayToBitmap(byte[] byteArray){
		Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray , 0, byteArray.length);
		return bitmap;
	}
	
}

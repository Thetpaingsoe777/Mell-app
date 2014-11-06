package com.xavey.app;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.xavey.app.model.Form;
import com.xavey.app.model.User;

public class ApplicationValues {
	public static int numberOfForm = 0;
	public static ArrayList<Form> userFormList = new ArrayList<Form>();
	public static User loginUser = new User();

	public static String documentName = "";

	public static int REQUEST_DRAWING = 100;
	public static int REQUEST_CAMERA = 101;
	public static int SELECT_FILE = 102;
	public static String FIELD_NAME_TMP = "";
	public static String FIELD_TYPE_TMP = "";
	public static String FIELD_HELP_TMP = "";
	public static String VIEW_ID_TMP = "";
	public static String IMAGE_PATH_TMP = "";
	public static String PHOTO_NAME_TMP = "";
	

	public static String getDeviceID(Activity activity) {
		TelephonyManager telephonyManager = (TelephonyManager) activity
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}
}

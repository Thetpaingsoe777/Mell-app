package com.xavey.android;

import java.io.File;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;

import com.xavey.android.model.FONT;
import com.xavey.android.model.Form;
import com.xavey.android.model.SYNC;
import com.xavey.android.model.User;

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

    public static final String preferenceKey = "XaveyFONTPref";
    public static final String prefSyncKey = "SYNC";
    public static final String prefSyncOffKey = "SYNC_OFF";
    public static final String prefSyncOnKey = "SYNC_ON";

    public static final String prefFontKey = "font";
    public static final String prefFontDefaultKey = "DEFAULT_";
    public static final String prefFontZawgyiKey = "ZAWGYI";
    public static final String prefFontMyanmarKey = "MYANMAR3";

	public static Context appContext = null;
	public static Activity mainActivity = null;

	public static File XAVEY_DIRECTORY = null; 

	public static FONT CURRENT_FONT = FONT.DEFAULT_;

	public static boolean IS_RECORDING_NOW = false;
	
	public static String UNIQUE_DEVICE_ID = "";
	
	public static LOGIN_TYPE CURRENT_LOGIN_MODE = LOGIN_TYPE.REGULAR_LOGIN;

    public static SYNC CURRENT_SYNC =SYNC.OFF;
	
	public static enum LOGIN_TYPE{
		DEMO_LOGIN, REGULAR_LOGIN
	}



}

package com.xavey.app.db;

import java.util.ArrayList;
import java.util.HashMap;

import com.xavey.app.model.Form;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class XaveyDBHelper extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME="Xavey";
	
	private static final String FORM_TABLE = "form";
	private static final String FORM_ID = "form_id";
	private static final String FORM_NAME = "form_name";
	private static final String FORM_TITLE = "form_title";
	
	private static final String FIELD_TABLE = "field";
	private static final String FIELD_ID = "field_id";
	private static final String FIELD_NAME = "field_name";
	private static final String FIELD_VALUE = "field_value";// not involve from json
	private static final String FIELD_TYPE = "field_type";
	private static final String FIELD_HINT = "field_hint";
	private static final String FIELD_LIST_TITLE = "field_list_title";
	private static final String FIELD_LIST_SUBTITLE = "field_list_subtitle";
	private static final String FIELD_REQUIRED = "field_required";
	private static final String FIELD_FORM_ID = "form_id";
	
	private final ArrayList<Form> formList = new ArrayList<Form>();
	
	public XaveyDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_FORM_TABLE = "CREATE TABLE " + FORM_TABLE
									+ "("
									+ FORM_ID + " INTEGER PRIMARY KEY,"
									+ FORM_NAME + " TEXT,"
									+ FORM_TITLE + " TEXT"
									+ ")";
		
		String CREATE_FIELD_TABLE = "CREATE TABLE " + FIELD_TABLE
									+ "("
									+ FIELD_ID + " INTEGER PRIMARY KEY,"
									+ FIELD_NAME + " TEXT,"
									+ FIELD_VALUE + " TEXT,"
									+ FIELD_HINT + " TEXT,"
									+ FIELD_LIST_TITLE + " FLAG,"
									+ FIELD_LIST_SUBTITLE + " FLAG,"
									+ FIELD_REQUIRED + " FLAG,"
									+ FIELD_FORM_ID + " TEXT"
									+ ")";
		db.execSQL(CREATE_FORM_TABLE);
		db.execSQL(CREATE_FIELD_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + FORM_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + FIELD_TABLE);
		onCreate(db);
	}
	
	//---------- Form CRUD --------------------
	//C
	public void addNewForm(Form form){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values1, values2;
		values1 = values2  = new ContentValues();
		
		ArrayList<HashMap<String, String>> formFields = form.getFormFields();
		values1.put(FORM_NAME, form.getFormName());
		values1.put(FORM_TITLE, form.getFormTitle());
		db.insert(FORM_TABLE, null, values1);
		
		for(HashMap<String,String> field: formFields){
			values2.put(FIELD_NAME, field.get(FIELD_NAME));
			//field values will come from user's input
			values2.put(FIELD_HINT, field.get(FIELD_HINT));
			values2.put(FIELD_LIST_TITLE, field.get(FIELD_LIST_TITLE));
			values2.put(FIELD_LIST_SUBTITLE, field.get(FIELD_LIST_SUBTITLE));
			values2.put(FIELD_REQUIRED, field.get(FIELD_REQUIRED));
			//form_id ?? how ?
		}
		db.close();
	}
	//R
	//U
	//D

}

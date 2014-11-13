package com.xavey.app.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xavey.app.model.Audio;
import com.xavey.app.model.Document;
import com.xavey.app.model.Form;
import com.xavey.app.model.Image;
import com.xavey.app.model.User;
import com.xavey.app.util.JSONReader;

public class XaveyDBHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "Xavey";
	private static final String ID = "ID";
	// Form
	// ID
	private static final String FORM_TABLE = "form";
	private static final String FORM_ID = "form_id";
	private static final String FORM_TITLE = "form_title";
	private static final String FORM_SUBTITLE = "form_subtitle";
	private static final String FORM_DESC = "form_desc";
	private static final String FORM_VERSION = "form_version";
	private static final String FORM_JSON = "form_json";
	private static final String ORG_AUTO_ID = "org_auto_id";
	private static final String ORG_GIVEN_ID = "org_given_id";
	private static final String ORG_NAME = "org_name";
//	private static final String WORKER_ID = "worker_id";
//	private static final String WORKER_EMAIL = "worker_email";
//	private static final String WORKER_NAME = "worker_name";
	private static final String CREATOR_ID = "creator_id";
	private static final String CREATOR_EMAIL = "creator_email";
	private static final String CREATOR_NAME = "creator_name";
	private static final String FORM_FIELDS = "form_fields";
	private final ArrayList<Form> formList = new ArrayList<Form>();

	// ID
	private static final String USER_TABLE = "user";
	private static final String USER_ID = "user_id";
	private static final String USER_NAME = "user_name";
	private static final String PASSWORD = "password";
	private static final String HASH_PWD = "hashPwd";
	private static final String EMAIL = "email";
	private static final String ROLE = "role";
	private static final String ORGANIZATION = "organization";
	private static final String TOKEN="token";

	private static final String WORKER_FORM_TABLE = "worker_form";
	// ID
	// USER_ID
	// FORM_ID
	private static final String ASSIGN = "assign";

	private static final String DOCUMENT_TABLE = "document";
	
	// ID
	private static final String DOCUMENT_ID = "document_id";
	private static final String DOCUMENT_NAME = "document_name";
	private static final String DOCUMENT_JSON = "document_json";
	private static final String DOCUMENT_JSON_TO_SUBMIT = "document_json_to_submit";
	private static final String CREATED_AT = "created_date";
	private static final String CREATED_WORKER = "created_worker";
	private static final String SUBMITTED = "submitted";
	// FORM_ID
	private final ArrayList<Document> documentList = new ArrayList<Document>();

	// IMAGE TABLE
	private static final String IMAGE_TABLE = "image";
	private static final String IMAGE_NAME = "image_name";
	private static final String IMAGE_PATH = "image_path";
	private static final String IMAGE_ID = "image_id";
	private static final String DOC_ID = "doc_id";
	
	// Audio TABLE
		private static final String AUDIO_TABLE = "audio";
		private static final String AUDIO_NAME = "audio_name";
		private static final String AUDIO_PATH = "audio_path";
		private static final String AUDIO_ID = "audio_id";

	public XaveyDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_FORM_TABLE = "CREATE TABLE " + FORM_TABLE + "(" + ID
				+ " INTEGER PRIMARY KEY," + FORM_ID + " INTEGER," + FORM_TITLE
				+ " TEXT," + FORM_SUBTITLE + " TEXT," + FORM_DESC + " TEXT,"
				+ FORM_VERSION + " TEXT," + FORM_JSON + " TEXT," + ORG_AUTO_ID
				+ " TEXT," + ORG_GIVEN_ID + " TEXT," + ORG_NAME + " TEXT,"
				+ CREATOR_ID + " TEXT," + CREATOR_EMAIL + " TEXT," + CREATOR_NAME
				+ " TEXT," + FORM_FIELDS + " TEXT" + ")";

		String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE + "(" + ID
				+ " INTEGER PRIMARY KEY," + USER_ID + " INTEGER," + USER_NAME
				+ " TEXT," + PASSWORD + " TEXT," 
				+ HASH_PWD + " TEXT," + EMAIL + " TEXT," + ROLE
				+ " INTEGER," + ORGANIZATION + " TEXT," 
				+ TOKEN + " TEXT"
				+ ")";

		String CREATE_WORKER_FORM_TABLE = "CREATE TABLE " + WORKER_FORM_TABLE
				+ "(" + ID + " INTEGER PRIMARY KEY," + USER_ID + " INTEGER,"
				+ FORM_ID + " INTEGER," + ASSIGN + " INTEGER" + ")";

		String CREATE_DOCUMENT_TABLE = "CREATE TABLE " + DOCUMENT_TABLE + "("
				+ ID + " INTEGER PRIMARY KEY," 
				+ DOCUMENT_ID + " TEXT,"
				+ DOCUMENT_NAME + " TEXT,"
				+ DOCUMENT_JSON + " TEXT,"
				+ DOCUMENT_JSON_TO_SUBMIT + " TEXT,"
				+ CREATED_AT
				+ " DATETIME DEFAULT CURRENT_TIMESTAMP," + FORM_ID
				+ " INTEGER," + CREATED_WORKER + " TEXT," + SUBMITTED + " INTEGER"
				+ ")";

		String CREATE_IMAGE_TABLE = "CREATE TABLE " + IMAGE_TABLE + "("
				+ ID + " INTEGER PRIMARY KEY," 
				+ IMAGE_NAME + " TEXT,"
				+ IMAGE_PATH + " TEXT," 
				+ IMAGE_ID + " INTEGER," 
				+ DOC_ID + " INTEGER"
				+ ")";
		
		String CREATE_AUDIO_TABLE = "CREATE TABLE " + AUDIO_TABLE +
									"(" + 
									ID + " INTEGER PRIMARY KEY," +
									AUDIO_NAME + " TEXT," +
									AUDIO_PATH + " TEXT," +
									AUDIO_ID + " TEXT," +
									DOC_ID + " TEXT" +
									")";

		db.execSQL(CREATE_FORM_TABLE);
		db.execSQL(CREATE_USER_TABLE);
		db.execSQL(CREATE_WORKER_FORM_TABLE);
		db.execSQL(CREATE_DOCUMENT_TABLE);
		db.execSQL(CREATE_IMAGE_TABLE);
		db.execSQL(CREATE_AUDIO_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + FORM_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DOCUMENT_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + WORKER_FORM_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + IMAGE_TABLE);
		onCreate(db);
	}

	// FORMS
	public void addNewForm(Form form) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(FORM_ID, form.getForm_id());
		values.put(FORM_TITLE, form.getForm_title());
		values.put(FORM_SUBTITLE, form.getForm_subtitle());
		values.put(FORM_DESC, form.getForm_desc());
		values.put(FORM_VERSION, form.getForm_version());
		values.put(FORM_JSON, form.getForm_json());
		values.put(ORG_AUTO_ID, form.getOrg_auto_id());
		values.put(ORG_GIVEN_ID, form.getOrg_given_id());
		values.put(ORG_NAME, form.getOrg_name());
		/*values.put(WORKER_ID, WORKER_ID);
		values.put(WORKER_EMAIL, form.getWorker_email());
		values.put(WORKER_NAME, form.getWorker_name());*/
		values.put(CREATOR_ID, form.getCreator_id());
		values.put(CREATOR_EMAIL, form.getCreator_email());
		values.put(CREATOR_NAME, form.getCreator_name());
		values.put(FORM_FIELDS, form.getForm_fields());
		db.insert(FORM_TABLE, null, values);
		db.close();
	}

	public Form getFormByFormID(String form_id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + FORM_TABLE + " where "
				+ FORM_ID + "=?", new String[] { form_id });
		Form form = new Form();
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			// 0 is id and no need
			form.setForm_id(cursor.getString(1));
			form.setForm_title(cursor.getString(2));
			form.setForm_subtitle(cursor.getString(3));
			form.setForm_desc(cursor.getString(4));
			form.setForm_version(cursor.getString(5));
			String form_json = cursor.getString(6);
			form_json = JSONReader.convertStandardJSONString(form_json);
			form.setForm_json(form_json);
			form.setOrg_auto_id(cursor.getString(7));
			form.setOrg_given_id(cursor.getString(8));
			form.setOrg_name(cursor.getString(9));
			form.setCreator_id(cursor.getString(10));
			form.setCreator_email(cursor.getString(11));
			form.setCreator_name(cursor.getString(12));
			form.setForm_fields(cursor.getString(13));
			cursor.close();
			db.close();
			return form;
		} else
			return null;
	}

	public ArrayList<Form> getAllForms() {
		formList.clear();
		String selectQuery = "SELECT * FROM " + FORM_TABLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				Form form = new Form();
				form.setId(cursor.getString(0));
				form.setForm_id(cursor.getString(1));
				form.setForm_title(cursor.getString(2));
				form.setForm_subtitle(cursor.getString(3));
				form.setForm_desc(cursor.getString(4));
				form.setForm_version(cursor.getString(5));
				String form_json = cursor.getString(6);
				form_json = JSONReader.convertStandardJSONString(form_json);
				form.setForm_json(form_json);
				form.setOrg_auto_id(cursor.getString(7));
				form.setOrg_given_id(cursor.getString(8));
				form.setOrg_name(cursor.getString(9));
				form.setCreator_id(cursor.getString(10));
				form.setCreator_email(cursor.getString(11));
				form.setCreator_name(cursor.getString(12));
				form.setForm_fields(cursor.getString(13));
				formList.add(form);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return formList;
	}

	public ArrayList<Form> getFormsByUserID(String user_id) {
		ArrayList<Form> formList = new ArrayList<Form>();
		ArrayList<String> form_ids = getFormIDsByUserID(user_id);
		for (String form_id : form_ids) {
			formList.add(getFormByFormID(form_id));
		}
		return formList;
	}

	public ArrayList<Form> getAssignedFormsByUserID(String user_id) {
		ArrayList<Form> formList = new ArrayList<Form>();
		ArrayList<String> form_ids = getAssignedFormIDsByUserID(user_id);
		for (String form_id : form_ids) {
			formList.add(getFormByFormID(form_id));
		}
		return formList;
	}

	public boolean isFormAlreadyExistInDB(String form_id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "select * from " + FORM_TABLE + " where " + FORM_ID
				+ "=?";
		Cursor cursor = db.rawQuery(query, new String[] { form_id });
		if (cursor.getCount() == 0)
			return false;
		else
			return true;
	}
	
	public boolean isImageAlreadyExistInDB(String imagePath){
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "select * from " + IMAGE_TABLE + " where " + IMAGE_PATH + "=?";
		Cursor cursor = db.rawQuery(query, new String[]{imagePath});
		if(cursor.getCount()==0)
			return false;
		else
			return true;
	}

	// update
	public int updateForm(Form form) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(FORM_ID, form.getForm_id());
		values.put(FORM_TITLE, form.getForm_title());
		values.put(FORM_SUBTITLE, form.getForm_subtitle());
		values.put(FORM_DESC, form.getForm_desc());
		values.put(FORM_VERSION, form.getForm_version());
		values.put(FORM_JSON, form.getForm_json());
		values.put(ORG_AUTO_ID, form.getOrg_auto_id());
		values.put(ORG_GIVEN_ID, form.getOrg_given_id());
		values.put(ORG_NAME, form.getOrg_name());
		values.put(CREATOR_ID, form.getCreator_id());
		values.put(CREATOR_EMAIL, form.getCreator_email());
		values.put(CREATOR_NAME, form.getCreator_name());
		values.put(FORM_FIELDS, form.getForm_fields());
		return db.update(FORM_TABLE, values, FORM_ID + "=?",
				new String[] { form.getForm_id() + "" });
	}

	// delete

	// USER
	public void addNewUser(User user) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(USER_ID, user.getUser_id());
		values.put(USER_NAME, user.getUser_name());
		values.put(PASSWORD, user.getPwd());
		values.put(HASH_PWD, user.getHashPwd());
		values.put(EMAIL, user.getEmail());
		values.put(ROLE, user.getRole());
		values.put(ORGANIZATION, user.getOrganization());
		values.put(TOKEN, user.getToken());
		db.insert(USER_TABLE, null, values);
		db.close();
	}
	
	public int updateUser(User user) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(USER_ID, user.getUser_id());
		values.put(USER_NAME, user.getUser_name());
		values.put(PASSWORD, user.getPwd());
		values.put(HASH_PWD, user.getHashPwd());
		values.put(EMAIL, user.getEmail());
		values.put(ROLE, user.getRole());
		values.put(ORGANIZATION, user.getOrganization());
		values.put(TOKEN, user.getToken());
		return db.update(USER_TABLE, values, USER_ID+"=?", new String[]{user.getUser_id()});
	}
	
	public int updateTokenByUserID(String user_id, String token){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TOKEN, token);
		return db.update(USER_TABLE, values, USER_ID+"=?", new String[]{token});
	}

	public User getUserByUserID(String user_id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String values = USER_ID + "," + USER_NAME + ", " + PASSWORD + ", "+ HASH_PWD + ", "
				+ EMAIL + ", " + ROLE + ", " + ORGANIZATION + ", " + TOKEN;
		Cursor cursor = db.rawQuery("select " + values + " from " + USER_TABLE
				+ " where " + USER_ID + "=?", new String[] { user_id });
		User user = new User();
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			user.setUser_id(cursor.getString(0));
			user.setUser_name(cursor.getString(1));
			user.setPwd(cursor.getString(2));
			user.setHashPwd(cursor.getString(3));
			user.setEmail(cursor.getString(4));
			user.setRole(cursor.getString(5));
			user.setOrganization(cursor.getString(6));
			user.setToken(cursor.getString(7));
		}
		cursor.close();
		db.close();
		return user;
	}

	public ArrayList<User> getAllUsers() {
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<User> userList = new ArrayList<User>();
		String values = USER_ID + "," + USER_NAME + ", " + PASSWORD + ", " + HASH_PWD + ", "
				+ EMAIL + ", " + ROLE + ", " + ORGANIZATION;
		Cursor cursor = db.rawQuery("select " + values + " from " + USER_TABLE,
				null);
		User user = new User();
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			user.setUser_id(cursor.getString(0));
			user.setUser_name(cursor.getString(1));
			user.setPwd(cursor.getString(2));
			user.setHashPwd(cursor.getString(3));
			user.setEmail(cursor.getString(4));
			user.setRole(cursor.getString(5));
			user.setOrganization(cursor.getString(6));
			userList.add(user);
		}
		cursor.close();
		db.close();
		return userList;
	}

	public boolean isUserAlreadyExistInDB(String user_id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "select * from " + USER_TABLE + " where " + USER_ID
				+ "=?";
		Cursor cursor = db.rawQuery(query, new String[] { user_id });
		int count = cursor.getCount();
		if (cursor.getCount() == 0)
			return false;
		else
			return true;
	}

	public String getUserIDByUserName(String user_name) {
		String userID = "";
		
		if (user_name!=null) {
			user_name = user_name.toLowerCase();
			SQLiteDatabase db = this.getReadableDatabase();
			String query = "select " + USER_ID + " from " + USER_TABLE
					+ " where lower(" + USER_NAME + ")=?";
			String[] parameter = new String[] { user_name };
			Cursor cursor = db.rawQuery(query, parameter);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				userID = cursor.getString(0);// assume that user name is not
												// duplicated
			}
			return userID;
		}else{
			return null;
		}
	}
	
	public String getTokenByUserID(String userID){
		String token="null";
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "select "+ TOKEN + " from " + USER_TABLE
				+ " where " + USER_ID + "=?";
		String[] parameter=new String[]{userID};
		Cursor cursor = db.rawQuery(query, parameter);
		if(cursor !=null && cursor.getCount()>0){
			cursor.moveToFirst();
			token = cursor.getString(0);
		}
		return token;
	}

	// WORKER_FORM
	public void addNewWorkerForm(String user_id, String form_id, String assign) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(USER_ID, user_id);
		values.put(FORM_ID, form_id);
		values.put(ASSIGN, assign);
		db.insert(WORKER_FORM_TABLE, null, values);
		db.close();
	}

	public ArrayList<String> getFormIDsByUserID(String user_id) {
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<String> formIDs = new ArrayList<String>();
		String selectQuery = "select " + FORM_ID + " from " + WORKER_FORM_TABLE
				+ " where " + USER_ID + "=?";
		Cursor cursor = db.rawQuery(selectQuery, new String[] { user_id });
		if (cursor.moveToFirst()) {
			do {
				String form_id = cursor.getString(0);
				formIDs.add(form_id);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return formIDs;
	}

	public ArrayList<String> getAssignedFormIDsByUserID(String user_id) {
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<String> formIDs = new ArrayList<String>();
		String selectQuery = "select " + FORM_ID + " from " + WORKER_FORM_TABLE
				+ " where " + USER_ID + "=?" + " and " + ASSIGN + "=?";
		Cursor cursor = db.rawQuery(selectQuery, new String[]{user_id, "1"});
		int count = cursor.getCount();
		if (cursor.moveToFirst()) {
			do {
				String form_id = cursor.getString(0);
				formIDs.add(form_id);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return formIDs;
	}

	public boolean isUserIDAndFormIDPaired(String user_id, String form_id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "select * from " + WORKER_FORM_TABLE + " where "
				+ USER_ID + "=?" + " and " + FORM_ID + "=?";
		Cursor cursor = db.rawQuery(query, new String[] { user_id, form_id });
		if (cursor.getCount() == 0)
			return false;
		else
			return true;
	}

	public int setAssignByUserIDAndFormID(String userID, String formID,
			String value) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ASSIGN, value);
		return db.update(WORKER_FORM_TABLE, values, USER_ID + "=? AND "
				+ FORM_ID + "=?", new String[] { userID, formID });
	}

	public int setAllAssignZeroByUserID(String userID) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ASSIGN, "0");
		return db.update(WORKER_FORM_TABLE, values, USER_ID + "=?",
				new String[] { userID });
	}

	// ---------------------------------------------------------
	// DOCUMENT
	public void addNewDocument(Document document) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DOCUMENT_ID, document.getDocument_id());
		values.put(DOCUMENT_NAME, document.getDocument_name());
		values.put(DOCUMENT_JSON, document.getDocument_json());
		values.put(DOCUMENT_JSON_TO_SUBMIT, document.getDocument_json_to_submit());
		values.put(CREATED_AT, getCurrentDateTime());
		values.put(FORM_ID, document.getForm_id());
		values.put(CREATED_WORKER, document.getCreated_worker());
		values.put(SUBMITTED, document.getSubmitted());
		db.insert(DOCUMENT_TABLE, null, values);
		db.close();
	}

	private String getCurrentDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd   HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	public Date getDateCreatedAtByDocumentID(String documentID) {
		SQLiteDatabase db = this.getReadableDatabase();
		String rawQuery = "select " + CREATED_AT + " from " + DOCUMENT_TABLE
				+ " where " + DOCUMENT_ID + "=?";
		Date createdAt = null;
		Cursor cursor = db.rawQuery(rawQuery, new String[] { documentID });
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			createdAt = new Date(cursor.getLong(0));
		}
		return createdAt;
	}
	
	

	public Document getDocumentByDocumentID(String documentID) {
		Document document = new Document();
		SQLiteDatabase db = this.getReadableDatabase();
		String rawQuery = "select * from " + DOCUMENT_TABLE + " where " + DOCUMENT_ID
				+ "=?";
		String[] parameters = new String[] { documentID };
		Cursor cursor = db.rawQuery(rawQuery, parameters);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			document.setId(cursor.getString(0));
			document.setDocument_id(cursor.getString(1));
			document.setDocument_name(cursor.getString(2));
			document.setDocument_json(cursor.getString(3));
			document.setDocument_json_to_submit(cursor.getString(4));
			document.setCreated_at(cursor.getString(5));
			document.setForm_id(cursor.getString(6));
			document.setCreated_worker(cursor.getString(7));
			document.setSubmitted(cursor.getString(8));
		}
		cursor.close();
		db.close();
		return document;
	}
	
	public ArrayList<Document> getAllDocumentsByCreaterID(String createdWorker) {
		documentList.clear();
		String selectQuery = "SELECT * FROM " + DOCUMENT_TABLE
				+ " WHERE " + CREATED_WORKER + "=? " 
				+ " ORDER BY " + CREATED_AT + " DESC ";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String[]{createdWorker});
		if (cursor.moveToFirst()) {
			do {
				Document document = new Document();
				document.setId(cursor.getString(0)); // this ID will be useful
														// as index in
														// HistoryFragment
				document.setDocument_id(cursor.getString(1));
				document.setDocument_name(cursor.getString(2));
				document.setDocument_json(cursor.getString(3));
				document.setDocument_json_to_submit(cursor.getString(4));
				document.setCreated_at(cursor.getString(5));
				document.setForm_id(cursor.getString(6));
				document.setCreated_worker(cursor.getString(7));
				document.setSubmitted(cursor.getString(8));
				documentList.add(document);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return documentList;
	}
	
	public ArrayList<Document> getAllDocumentsByFormID(String formID) {
		documentList.clear();
		String selectQuery = "SELECT * FROM " + DOCUMENT_TABLE
				+ " WHERE " + FORM_ID + "=? " 
				+ " ORDER BY " + CREATED_AT + " ASC ";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String[]{formID});
		if (cursor.moveToFirst()) {
			do {
				Document document = new Document();
				document.setId(cursor.getString(0));
				document.setDocument_id(cursor.getString(1));
				document.setDocument_name(cursor.getString(2));
				document.setDocument_json(cursor.getString(3));
				document.setDocument_json_to_submit(cursor.getString(4));
				document.setCreated_at(cursor.getString(5));
				document.setForm_id(cursor.getString(6));
				document.setCreated_worker(cursor.getString(7));
				document.setSubmitted(cursor.getString(8));
				documentList.add(document);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return documentList;
	}

	public ArrayList<Document> getAllDocuments() {
		documentList.clear();
		String selectQuery = "SELECT * FROM " + DOCUMENT_TABLE + " ORDER BY "
				+ CREATED_AT + " DESC ";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				Document document = new Document();
				document.setId(cursor.getString(0)); // this ID will be useful
														// as index in
														// HistoryFragment
				document.setDocument_id(cursor.getString(1));
				document.setDocument_name(cursor.getString(2));
				document.setDocument_json(cursor.getString(3));
				document.setDocument_json_to_submit(cursor.getString(4));
				document.setCreated_at(cursor.getString(5));
				document.setForm_id(cursor.getString(6));
				document.setCreated_worker(cursor.getString(7));
				document.setSubmitted(cursor.getString(8));
				documentList.add(document);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return documentList;
	}

	public ArrayList<Document> getDocumentsBySubmitted(String submitted) {
		// if submitted parameter is zero... it will return unsubmitted docs
		// else return submitted docs
		documentList.clear();
		String selectQuery = "SELECT * FROM " + DOCUMENT_TABLE + " WHERE "
				+ SUBMITTED + "=?" + " ORDER BY " + CREATED_AT + " DESC ";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String[] { submitted });
		if (cursor.moveToFirst()) {
			do {
				Document document = new Document();
				document.setId(cursor.getString(0)); // this ID will be useful
														// as index in
														// HistoryFragment
				document.setDocument_id(cursor.getString(1));
				document.setDocument_name(cursor.getString(2));
				document.setDocument_json(cursor.getString(3));
				document.setDocument_json_to_submit(cursor.getString(4));
				document.setCreated_at(cursor.getString(5));
				document.setForm_id(cursor.getString(6));
				document.setCreated_worker(cursor.getString(7));
				document.setSubmitted(cursor.getString(8));
				documentList.add(document);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return documentList;
	}
	
	public int updateDocumentSubmitted(Document document, String submitted_value) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(SUBMITTED, submitted_value);
		return db.update(DOCUMENT_TABLE, values, DOCUMENT_ID + "=?",
				new String[] { document.getDocument_id() });
	}

	public int updateDocumentSubmittedByJSON(Document document, String submitted_value) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(SUBMITTED, submitted_value);
		return db.update(DOCUMENT_TABLE, values, DOCUMENT_JSON + "=?",
				new String[] { document.getDocument_json() });
	}
	
	public int updateDocument(Document document) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DOCUMENT_NAME, document.getDocument_name());
		values.put(DOCUMENT_JSON, document.getDocument_json());
		values.put(DOCUMENT_JSON_TO_SUBMIT, document.getDocument_json_to_submit());
		values.put(SUBMITTED, document.getSubmitted());
		return db.update(DOCUMENT_TABLE, values, DOCUMENT_ID + "=?",
				new String[] { document.getDocument_id()});
	}

	public int updateDocumentByJSON(Document document, String old_document_json) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DOCUMENT_NAME, document.getDocument_name());
		values.put(DOCUMENT_JSON, document.getDocument_json());
		return db.update(DOCUMENT_TABLE, values, DOCUMENT_JSON + "=?",
				new String[] { old_document_json});
	}

	public void deleteDocumentByID(String document_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(DOCUMENT_TABLE, DOCUMENT_ID + "=?", new String[] { document_id });
		db.close();
	}

	public Form getRelatedFormByDocumentID(String document_id) {
		Document document = getDocumentByDocumentID(document_id);
		Form form = getFormByFormID(document.getForm_id());
		return form;
	}

	public ArrayList<Document> getRelatedDocumentsByFormID(String form_id) {
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<Document> relatedDocumentList = new ArrayList<Document>();
		String query = "select * from " + DOCUMENT_TABLE + " where " + FORM_ID
				+ "=?";
		Cursor cursor = db.rawQuery(query, new String[] { form_id });
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			Document document = new Document();
			document.setId(cursor.getString(0));
			document.setDocument_id(cursor.getString(1));
			document.setDocument_name(cursor.getString(2));
			document.setDocument_json(cursor.getString(3));
			document.setDocument_json_to_submit(cursor.getString(4));
			document.setCreated_at(cursor.getString(5));
			document.setForm_id(cursor.getString(6));
			document.setCreated_worker(cursor.getString(7));
			document.setSubmitted(cursor.getString(8));
			relatedDocumentList.add(document);
		}
		return relatedDocumentList;
	}
	
	// Image
	public void addNewImage(Image image) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(IMAGE_ID, image.getImage_id());
		values.put(IMAGE_NAME, image.getImage_name());
		values.put(IMAGE_PATH, image.getImage_path());
		values.put(DOC_ID, image.getDoc_id());
		db.insert(IMAGE_TABLE, null, values);
		db.close();
	}
	
	public ArrayList<Image> getAllImagesByDocumentID(String documentID){
		ArrayList<Image> imageList = new ArrayList<Image>();
		String selectQuery = "SELECT * FROM " + IMAGE_TABLE + " WHERE "
				+ DOC_ID + "=?";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String[] { documentID });
		if (cursor.moveToFirst()) {
			do {
				Image image = new Image();
				image.setId(cursor.getString(0));
				image.setImage_name(cursor.getString(1));
				image.setImage_path(cursor.getString(2));
				image.setImage_id(cursor.getString(3));
				image.setDoc_id(cursor.getString(4));
				imageList.add(image);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return imageList;
	}
	
	public int updateImageByPath(Image image){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(IMAGE_ID, image.getImage_id());
		return db.update(IMAGE_TABLE, values, IMAGE_PATH+"=?", new String[]{image.getImage_path()});
	}
	
	public Image getImageByImageID(String image_id){
		Image image = new Image();
		SQLiteDatabase db = this.getReadableDatabase();
		String rawQuery = "SELECT * FROM "+IMAGE_TABLE+" WHERE "+IMAGE_ID+"=?";
		String[] parameters = new String[]{image_id};
		Cursor cursor = db.rawQuery(rawQuery, parameters);
		if(cursor!=null && cursor.getCount()>0){
			cursor.moveToFirst();
			image.setId(cursor.getString(0));
			image.setImage_name(cursor.getString(1));
			image.setImage_path(cursor.getString(2));
			image.setImage_id(cursor.getString(3));
			image.setDoc_id(cursor.getString(4));
		}
		cursor.close();
		db.close();
		return image;
	}

	// following method is useful cuz we assumed that the image_name (field_name) is currently unique
	public Image getImagePathByImageName(String image_name){
		Image image = new Image();
		SQLiteDatabase db = this.getReadableDatabase();
		String rawQuery = "SELECT * FROM "+IMAGE_TABLE+" WHERE "+IMAGE_NAME+"=?";
		String[] parameters = new String[]{image_name};
		Cursor cursor = db.rawQuery(rawQuery, parameters);
		if(cursor!=null && cursor.getCount()>0){
			cursor.moveToFirst();
			image.setId(cursor.getString(0));
			image.setImage_name(cursor.getString(1));
			image.setImage_path(cursor.getString(2));
			image.setImage_id(cursor.getString(3));
			image.setDoc_id(cursor.getString(4));
		}
		cursor.close();
		db.close();
		return image;
	}

	public int updateImage(Image image){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(IMAGE_ID	, image.getImage_id());
		values.put(IMAGE_NAME, image.getImage_name());
		values.put(IMAGE_PATH, image.getImage_path());
		values.put(DOC_ID, image.getDoc_id());
		return db.update(IMAGE_TABLE, 	values, IMAGE_ID+"=?", new String[]{image.getImage_id()});
	}

	
	
	
	// AUDIO
	
	public void addNewAudio(Audio audio) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(AUDIO_ID, audio.getAudio_id());
		values.put(AUDIO_NAME, audio.getAudio_name());
		values.put(AUDIO_PATH, audio.getAudio_path());
		values.put(DOC_ID, audio.getDoc_id());
		db.insert(AUDIO_TABLE, null, values);
		db.close();
	}

	public boolean isAudioAlreadyExistInDB(String audioPath){
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "select * from " + AUDIO_TABLE + " where " + AUDIO_PATH + "=?";
		Cursor cursor = db.rawQuery(query, new String[]{audioPath});
		if(cursor.getCount()==0)
			return false;
		else
			return true;
	}

	public int updateAudioByPath(Audio audio){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(IMAGE_ID, audio.getAudio_id());
		return db.update(AUDIO_TABLE, values, AUDIO_PATH+"=?", new String[]{audio.getAudio_path()});
	}
	
}

package com.xavey.android;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.xavey.android.adapter.CustomDrawerAdapter;
import com.xavey.android.db.XaveyDBHelper;
import com.xavey.android.model.Document;
import com.xavey.android.model.Form;
import com.xavey.android.model.Image;
import com.xavey.android.model.RequestMethod;
import com.xavey.android.model.RestClient;
import com.xavey.android.model.SyncImage;
import com.xavey.android.model.User;
import com.xavey.android.util.ConnectionDetector;
import com.xavey.android.util.SessionManager;
import com.xavey.android.util.SyncManager;
import com.xavey.android.util.ToastManager;
import com.xavey.android.util.UUIDGenerator;
import com.xavey.android.util.XaveyProperties;

public class MainActivity extends Activity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	// private CharSequence mDrawerTitle;
	CustomDrawerAdapter adapter_;
	List<DrawerItem> itemList;
	SessionManager session;
	public static Menu optionMenu;
	XaveyDBHelper dbHelper;
	ConnectionDetector connectionDetector;

	public static String LOGIN_USER_ID = "";

	private Handler customHandler = new Handler();;
	ToastManager toastManager;
	
	public static int current_position = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sharedPreferences = getSharedPreferences("XaveyFONTPref",
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		String root = Environment.getExternalStorageDirectory().toString();
		ApplicationValues.XAVEY_DIRECTORY = new File(root, "/Xavey");
		ApplicationValues.appContext = getApplicationContext();
		ApplicationValues.mainActivity = this;
		ApplicationValues.UNIQUE_DEVICE_ID = new SyncManager(this)
				.getDeviceUniqueID(this);
		initializeUI();
		session.checkLogin();
		dbHelper = new XaveyDBHelper(this);
		if (savedInstanceState == null) {
			selectItem(0);
		}
		String current_token = "";
		String userID = session.getUserDetails().get(SessionManager.USER_ID);
		if (userID != null) {
			ApplicationValues.loginUser = dbHelper.getUserByUserID(userID);
			current_token = ApplicationValues.loginUser.getToken();
		}
		downloadForms();
		customHandler.postDelayed(updateTimerThread, 1000 * 30);
	}

	private void downloadForms() {
		HashMap<String, String> nameAndPassword = session.getUserDetails();
		String userName = nameAndPassword.get(SessionManager.KEY_NAME);
		if (userName != null) {
			LOGIN_USER_ID = dbHelper.getUserIDByUserName(userName);
			String password = nameAndPassword.get(SessionManager.PASSWORD);
			String userID = dbHelper.getUserIDByUserName(userName);
			User u = new User();
			u.setUser_id(userID);
			u.setUser_name(userName);
			u.setPwd(password);
			u.setToken(ApplicationValues.loginUser.getToken());
			// online mode
			if (connectionDetector.isConnectingToInternet()) {
				new FormDownloadTask().execute(u);
			}
		}
	}

	private Runnable updateTimerThread = new Runnable() {
		@Override
		public void run() {
			ArrayList<Document> unsubmittedDocList = dbHelper
					.getDocumentsBySubmitted("0");
			if (connectionDetector.isConnectingToInternet()
					&& unsubmittedDocList.size() > 0) {
				Toast.makeText(getApplicationContext(), "Syncing document", 100)
						.show();
				for (Document doc : unsubmittedDocList) {
					SyncManager syncManager = new SyncManager(MainActivity.this);
					String formID = doc.getForm_id();
					Form form = dbHelper.getFormByFormID(formID);
					ArrayList<Image> imageList = dbHelper
							.getAllImagesByDocumentID(doc.getDocument_id());
					try {
						if (imageList.size() > 0) {
							ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
							for (Image image : imageList) {
								String image_path = image.getImage_path();
								String field_name = image.getImage_name();
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("imagePath", image_path);
								map.put("field_name", field_name);
								list.add(map);
							}
							syncManager.submitDocument2(doc, form, list);
						} else {
							syncManager.submitDocument(doc, form);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			customHandler.postDelayed(this, 1000 * 30);
		}
	};

	SharedPreferences sharedPreferences = null;
	Editor editor = null;

	protected void onStop() {
		super.onStop();
		switch (ApplicationValues.CURRENT_FONT) {
		case DEFAULT_:
			editor.putString("font", "DEFAULT_");
			editor.commit();
			break;
		case ZAWGYI:
			editor.putString("font", "ZAWGYI");
			editor.commit();
			break;
		case MYANMAR3:
			editor.putString("font", "MYANMAR3");
			editor.commit();
			break;
		default:
			break;
		}
	};

	private void initializeUI() {
		session = new SessionManager(getApplicationContext());
		itemList = new ArrayList<DrawerItem>();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		addDrawerItem();
		adapter_ = new CustomDrawerAdapter(this, R.layout.custom_drawer_item,
				itemList);
		mDrawerList.setAdapter(adapter_);
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				selectItem(position);
			}
		});
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close);

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		// formFieldList = new ArrayList<HashMap<String, String>>();
		connectionDetector = new ConnectionDetector(getApplicationContext());
		toastManager = new ToastManager(this);
	}

	private void addDrawerItem() {
		itemList.add(new DrawerItem(getString(R.string.str_home),
				R.drawable.orkut));
		itemList.add(new DrawerItem(getString(R.string.str_export_csv),
				R.drawable.orkut));
		itemList.add(new DrawerItem(getString(R.string.str_history),
				R.drawable.orkut));
		itemList.add(new DrawerItem(getString(R.string.action_settings),
				R.drawable.orkut));
		itemList.add(new DrawerItem(getString(R.string.str_about),
				R.drawable.orkut));
		itemList.add(new DrawerItem(getString(R.string.str_logout),
				R.drawable.orkut));
	}

	public void selectItem(int position) {
		Fragment fragment = null;
		Bundle args = new Bundle();
		switch (position) {
		case 0:
			fragment = new HomeFragment();
			args.putString(HomeFragment.ITEM_NAME, itemList.get(position)
					.getItemName());
			setTitle("Xavey Pte Ltd");
			current_position = 0;
			break;
		case 1:
			fragment = new CSVExportFragment();
			args.putString("CSV Export", itemList.get(position).getItemName());
			setTitle("CSV Export");
			current_position = 1;
			break;
		case 2:
			fragment = new HistoryFragment();
			args.putString(HistoryFragment.ITEM_NAME, itemList.get(position)
					.getItemName());
			setTitle(itemList.get(position).getItemName());
			current_position = 2;
			break;
		case 3:
			fragment = new SettingFragment();
			args.putString("Setting", itemList.get(position).getItemName());
			setTitle(itemList.get(position).getItemName());
			current_position = 3;
			break;
		case 4:
			fragment = new AboutFragment();
			args.putString("About", itemList.get(position).getItemName());
			String itemName = itemList.get(position).getItemName();
			setTitle(itemName);
			current_position = 4;
			break;
		case 5:
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder.setTitle("Confirm");
			alertDialogBuilder.setMessage("Are you sure to sign out?");
			alertDialogBuilder.setCancelable(false);
			alertDialogBuilder.setPositiveButton("Yes", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					session.logoutUser();
					startActivity(new Intent(getApplicationContext(),
							LoginActivity.class));
				}
			});
			alertDialogBuilder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// if this button is clicked, just close
							// the dialog box and do nothing
							dialog.cancel();
						}
					});
			alertDialogBuilder.create().show();
			args = null;
			break;
		default:
			break;
		}
		if (args != null) {
			fragment.setArguments(args);
			try {
				FragmentManager frgManager = getFragmentManager();
				frgManager.beginTransaction()
						.replace(R.id.content_frame, fragment).commit();
				mDrawerList.setItemChecked(position, true);
				setTitle(itemList.get(position).getItemName());
				mDrawerLayout.closeDrawer(mDrawerList);
			} catch (IllegalStateException e) {

			}
		}
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		optionMenu = menu;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.app_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		} else {
			switch (item.getItemId()) {
			case R.id.app_menuRefresh:
				setRefreshActionButtonState(true);
				ConnectionDetector detector = new ConnectionDetector(this);
				if (detector.isConnectingToInternet()) {
					ArrayList<Document> unsubmittedDocList = dbHelper
							.getDocumentsBySubmitted("0"); // get all
															// unsubmitted docs

					// document submit...
					if (unsubmittedDocList.size() == 0) {
						Toast.makeText(getApplicationContext(),
								"No documents to submit...", Toast.LENGTH_LONG)
								.show();
					} else {
						for (Document doc : unsubmittedDocList) {
							SyncManager syncManager = new SyncManager(this);
							String formID = doc.getForm_id();
							Form form = dbHelper.getFormByFormID(formID);
							try {
								syncManager.submitDocument(doc, form);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					// here is form download
					// MainActivity.this.onResume();
					downloadForms();
					
					
				} else {
					toastManager.xaveyToast(null,
							"Not connecting with server...");
					setRefreshActionButtonState(false);
				}
				return true;
			default:
				break;
			}
		}
		return false;
	}

	// use it for refresh
	public void setRefreshActionButtonState(final boolean refreshing) {
		if (optionMenu != null) {
			final MenuItem refreshItem = optionMenu
					.findItem(R.id.app_menuRefresh);
			if (refreshItem != null) {
				if (refreshing) {
					refreshItem
							.setActionView(R.layout.actionbar_indeterminate_progress);
				} else {
					refreshItem.setActionView(null);
				}
			}
		}
	}

	private class FormDownloadTask extends
			AsyncTask<User, Void, ArrayList<Form>> {

		private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
		// properties
		XaveyProperties xavey_properties;
		String serverFormDownloadURL = "";
		String localFromDownloadURL = "";

		protected void onPreExecute() {
			Dialog.setMessage("Loading forms from server");
			Dialog.show();
			xavey_properties = new XaveyProperties();
			serverFormDownloadURL = xavey_properties.getServerFormDownloadURL();
			localFromDownloadURL = xavey_properties.getLocalFormDownloadURL();
		}

		@Override
		protected ArrayList<Form> doInBackground(User... params) {
			ArrayList<Form> userFormsList = new ArrayList<Form>();

			// ---------- updating token.. --------------------------------
			String userName = ApplicationValues.loginUser.getUser_name();
			String password = ApplicationValues.loginUser.getPwd();
			String deviceID = ApplicationValues.UNIQUE_DEVICE_ID;
			// updateToken(userName,password);

			String authenticateURL = xavey_properties.getAuthenticateURL();

			RestClient c_ = new RestClient(authenticateURL);
			c_.AddParam("username", userName);
			c_.AddParam("password", password);
			c_.AddParam("device", deviceID);
			try {
				c_.Execute(RequestMethod.POST);
			} catch (Exception e2) {
				toastManager.xaveyToast(null, e2.getMessage());
			}
			int userResponseCode = c_.getResponseCode();
			// -----------------------------------------------------------------

			if (userResponseCode == 200) {
				try {
					User user = params[0];
					// RestClient f = new RestClient(localFromDownloadURL);
					RestClient f = new RestClient(serverFormDownloadURL);
					f.AddParam("id", user.getUser_id());
					f.AddHeader("x-access-token", user.getToken()); // <--token
					f.Execute(RequestMethod.GET);
					int responseCode = f.getResponseCode();
					String response = "";
					// Log.i("response :", f.getResponse());
					// userFormsList = parseJSONForm(response);
					// ApplicationValues.userFormList = userFormsList;
					// ApplicationValues.numberOfForm = userFormsList.size();

					if (responseCode == 200) {
						response = f.getResponse();
						Log.i("response :", f.getResponse());
						userFormsList = parseJSONForm(response);
						ApplicationValues.userFormList = userFormsList;
						ApplicationValues.numberOfForm = userFormsList.size();
					} else if (responseCode == 403) {
						// syncManager.updateToken(userName, password);
						// assumed 403 never come cuz updated the token before
						// sync
						toastManager.xaveyToast(null, "Token expired..!");
					} else {
						toastManager.xaveyToast(null, "Server error..! ");
					}
				} catch (Exception e) {
					Log.e("form error :", e.getMessage());
				}
			} else if (userResponseCode == 401) {
				toastManager.xaveyToast(null,
						"Authenticating failed when downloading form..");
			} else if (userResponseCode == 403) {
				toastManager
						.xaveyToast(null,
								"server error when downloading form.. at Main Activity");
			}

			return userFormsList;
		}

		private ArrayList<Form> parseJSONForm(String response) {
			ArrayList<Form> form_list = new ArrayList<Form>();
			response = response.replace("\n", "");
			if (!response.equals("null")) {
				response = "{" + "\"forms\"" + ":" + response + "}";
				// Log.i("JSON", response.toString());
				JSONObject jsResponse;
				try {
					// 1st level
					jsResponse = new JSONObject(response);
					JSONArray jsMainNode = jsResponse.optJSONArray("forms");
					for (int i = 0; i < jsMainNode.length(); i++) {
						JSONObject jsChildNode1 = jsMainNode.getJSONObject(i);
						Form form = new Form();
						// form.setForm_id(jsChildNode1.getString("form_id"));
						String form_json = jsChildNode1.getString("form_json");
						if (!form_json.equals("null")) {
							// 2nd level
							JSONObject form_content = new JSONObject(form_json);
							form.setForm_id(form_content.getString("_id"));
							// form_meta
							JSONObject form_meta = form_content
									.getJSONObject("form_meta");
							form.setForm_title(form_meta
									.getString("form_title"));
							form.setForm_subtitle(form_meta
									.getString("form_subtitle"));
							form.setForm_desc(form_meta.getString("form_desc"));
							form.setForm_version(form_meta
									.getString("form_version"));
							if (form_meta.has("form_location_required")) {// <--
																			// this
																			// condition
																			// won't
																			// be
																			// needed
																			// in
																			// future
								boolean test = form_meta
										.getBoolean("form_location_required");
								form.setForm_location_required(form_meta
										.getBoolean("form_location_required"));

							}
							// org
							JSONObject org = form_content.getJSONObject("org");
							form.setOrg_auto_id(org.getString("auto_id"));
							form.setOrg_given_id(org.getString("given_id"));
							form.setOrg_name(org.getString("name"));
							// worker
							JSONObject creator = form_content
									.getJSONObject("creator");
							form.setCreator_id(creator.getString("id"));
							form.setCreator_email(creator.getString("email"));
							form.setCreator_name(creator.getString("name"));

							// form_fields
							JSONArray form_fields = form_content
									.getJSONArray("form_fields");
							form.setForm_fields(form_fields.toString());

							form.setForm_json(form_content.toString());
							form_list.add(form);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(), "401", 500).show();
			}
			return form_list;
		}

		@Override
		protected void onPostExecute(ArrayList<Form> result) {
			super.onPostExecute(result);
			String userID = ApplicationValues.loginUser.getUser_id();
			try {
				if (result.size() > 0) {
					dbHelper.setAllAssignZeroByUserID(userID);
					for (Form form : result) {
						String form_id = form.getForm_id();
						if (!dbHelper.isFormAlreadyExistInDB(form_id)) {
							// add if new form
							dbHelper.addNewForm(form);
						} else {
							// update form
							dbHelper.updateForm(form);
						}
						if (!dbHelper.isUserIDAndFormIDPaired(userID, form_id)) {
							dbHelper.addNewWorkerForm(userID, form_id, "1");
						} else {
							dbHelper.setAssignByUserIDAndFormID(userID,
									form_id, "1");
						}
					}

					// assign all forms' imageSynced to false
					for (Form form : result) {
						form.setImageSynced(false);
					}

					// get the forms that involved images
					ArrayList<HashMap<String,String>> imageIncludedFormList = new ArrayList<HashMap<String,String>>();
					
					for (Form form : result) {
						String form_fields_string = form.getForm_fields();
						JSONArray jsonArray = new JSONArray(form_fields_string);

						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject field = jsonArray.getJSONObject(i);
							String fieldType = field.getString("field_type");

							if (fieldType.equals("image_checklist")
									|| fieldType.equals("image_option")) {
								// getting ImageID here
								JSONObject fieldDataset = field
										.getJSONObject("field_dataset");
								JSONArray dataSetValues = fieldDataset
										.getJSONArray("dataset_values");

								for (int j = 0; j < dataSetValues.length(); j++) {
									String imageID = dataSetValues
											.getJSONObject(j)
											.getString("image");
									HashMap<String, String> map = new HashMap<String, String>();
									map.put("form_id", form.getForm_id());
									map.put("image_id", imageID);
									imageIncludedFormList.add(map);
								}
							} else {
								form.setImageSynced(true); 
								dbHelper.updateForm(form);
							}
						}
					}

					// AsyncTask
					SyncImagesDownloadTask syncImageTask = new SyncImagesDownloadTask();
					syncImageTask.execute(imageIncludedFormList);
				}

				if (Dialog != null) {
					Dialog.dismiss();
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Error Loading", 1000)
						.show();
				if (Dialog != null) {
					Dialog.dismiss();
				}
			}

			selectItem(0); // this is like refreshing ;)
			setRefreshActionButtonState(false);
		}
	}

	@Override
	public void onBackPressed() {
		Intent homeIntent = new Intent(Intent.ACTION_MAIN);
		homeIntent.addCategory(Intent.CATEGORY_HOME);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}

	private class SyncImagesDownloadTask extends
			AsyncTask<ArrayList<HashMap<String,String>>, Void, HashMap<String, Object>> {

		private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
		// properties
		XaveyProperties xavey_properties;
		String imageDownloadURL = "";
		ToastManager toastManager;

		// String serverLoginURL = "serverLoginURL";
		// String serverFormURL = "serverFormURL";

		protected void onPreExecute() {
			Dialog.setMessage("Downloading Images..");
			Dialog.show();
			xavey_properties = new XaveyProperties();
			imageDownloadURL = xavey_properties.getSyncImageDownloadURL();
			toastManager = new ToastManager(MainActivity.this);
		}

		protected HashMap<String, Object> doInBackground(
				ArrayList<HashMap<String,String>>... imageIDLists) {

			// ---------- updating token.. --------------------------------
			String userName = ApplicationValues.loginUser.getUser_name();
			String password = ApplicationValues.loginUser.getPwd();
			String deviceID = ApplicationValues.UNIQUE_DEVICE_ID;
			// updateToken(userName,password);

			String authenticateURL = xavey_properties.getAuthenticateURL();

			RestClient c_ = new RestClient(authenticateURL);
			c_.AddParam("username", userName);
			c_.AddParam("password", password);
			c_.AddParam("device", deviceID);
			
			try {
				c_.Execute(RequestMethod.POST);
			} catch (Exception e2) {
				
			}
			int userResponseCode_ = c_.getResponseCode();
			// -----------------------------------------------------------------
			String token = "";

			try {
				token = new JSONObject(c_.getResponse().toString())
						.getString("token");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				toastManager.xaveyToast(null,
						"JSON Exception at MainActivity... response code");
			}

			
			HashMap<String, Object> syncedForms = new HashMap<String, Object>();

			if (userResponseCode_ == 200) {
				ArrayList<HashMap<String,String>> imageIDList = imageIDLists[0];
				String syncID = UUIDGenerator.getUUIDForSyncedID();
				for (HashMap<String, String> hashMap : imageIDList) {
					String imageName = hashMap.get("image_id").toString();
					String formID = hashMap.get("form_id").toString();
					try {
						boolean isImageIDAlreadyExistInSyncImageTable = dbHelper.isImageIDAlreadyExistInSyncImageTable(imageName);
						if(isImageIDAlreadyExistInSyncImageTable){
							boolean  isSyncIDAlreadyExistInSyncImageTable = dbHelper.isSyncIDAlreadyExistInSyncImageTable(imageName, syncID);
							if(isSyncIDAlreadyExistInSyncImageTable){
								// True True Situation... (already synced situation)
								// Do nothing
								// set the form available
								Form form = dbHelper.getFormByFormID(formID);
								form.setImageSynced(true);
								dbHelper.updateForm(form);
								//toastManager.xaveyToast(null, form.getForm_title() + " is now available.");
							}else{
								//True False Situation
								//update sync image
								RestClient c = new RestClient(imageDownloadURL + imageName);
								c.AddHeader("x-access-token", token);
								c.Execute(RequestMethod.GET);
								int imageResponseCode = c.getResponseCode();
								if(imageResponseCode==200){
									byte[] imageByte = c.getResponseImage();
									SyncImage syncImageToUpdate = new SyncImage();
									syncImageToUpdate.setImageID(imageName);
									syncImageToUpdate.setSynceID(syncID);
									syncImageToUpdate.setImgByte(imageByte);
									dbHelper.updateSyncImage(syncImageToUpdate);

									// set the form available
									Form form = dbHelper.getFormByFormID(formID);
									form.setImageSynced(true);
									dbHelper.updateForm(form);
									
									//toastManager.xaveyToast(null, form.getForm_title() + " is now available.");
								}
								else if (imageResponseCode==401){
									toastManager.xaveyToast(null, "401: token expired when downloading image");
								}
								else if (imageResponseCode==403){
									toastManager.xaveyToast(null, "403: server error.");
								}
							}
						}else{
							// false false situation
							// add new sync image here
							RestClient c = new RestClient(imageDownloadURL + imageName);
							c.AddHeader("x-access-token", token);
							c.Execute(RequestMethod.GET);
							int imageResponseCode = c.getResponseCode();
							if(imageResponseCode==200){
								byte[] imageByte = c.getResponseImage();
								SyncImage syncImageToAdd = new SyncImage();
								syncImageToAdd.setImageID(imageName);
								syncImageToAdd.setSynceID(syncID);
								syncImageToAdd.setImgByte(imageByte);
								dbHelper.addNewSyncImage(syncImageToAdd);
								// set the form available
								Form form = dbHelper.getFormByFormID(formID);
								form.setImageSynced(true);
								dbHelper.updateForm(form);
							}
							else if (imageResponseCode==401){
								toastManager.xaveyToast(null, "401: token expired when downloading image");
							}
							else if (imageResponseCode==403){
								toastManager.xaveyToast(null, "403: server error.");
							}
						}
	
					} catch (Exception e) {
						toastManager.xaveyToast(null, "Error Dowonloading image : "+imageName+"\n"+e.getMessage());
					}
					syncedForms.put("formID", formID);
				}
				

			} else if (userResponseCode_ == 401) {
				toastManager.xaveyToast(null,
						"Authenticating failed when downloading form images..");
			} else if (userResponseCode_ == 403) {
				toastManager
						.xaveyToast(null,
								"server error when downloading form images.. at Main Activity");
			}
			return syncedForms;
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> syncedForms) {
			Dialog.dismiss();
			if(current_position==0){
				selectItem(0);
			}
		}
	}

}

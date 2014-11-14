package com.xavey.app;

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
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.Intent;
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

import com.xavey.app.adapter.CustomDrawerAdapter;
import com.xavey.app.db.XaveyDBHelper;
import com.xavey.app.model.Document;
import com.xavey.app.model.Form;
import com.xavey.app.model.Image;
import com.xavey.app.model.RequestMethod;
import com.xavey.app.model.RestClient;
import com.xavey.app.model.User;
import com.xavey.app.util.ConnectionDetector;
import com.xavey.app.util.SessionManager;
import com.xavey.app.util.SyncManager;
import com.xavey.app.util.ToastManager;
import com.xavey.app.util.XaveyProperties;

public class MainActivity extends Activity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	//private CharSequence mDrawerTitle;
	CustomDrawerAdapter adapter;
	List<DrawerItem> itemList;
	SessionManager session;
	public static Menu optionMenu;
	XaveyDBHelper dbHelper;
	ConnectionDetector connectionDetector;

	public static String LOGIN_USER_ID = "";

	private Handler customHandler = new Handler();;
	ToastManager toastManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		String root = Environment.getExternalStorageDirectory().toString();
		ApplicationValues.XAVEY_DIRECTORY = new File(root, "/Xavey");
		ApplicationValues.appContext = getApplicationContext();
		ApplicationValues.mainActivity = this;
		initializeUI();
		session.checkLogin();
		dbHelper = new XaveyDBHelper(this);
		if (savedInstanceState == null) {
			selectItem(0);
		}
		String current_token="";
		String userID = session.getUserDetails().get(SessionManager.USER_ID);
		if(userID!=null){
			ApplicationValues.loginUser = dbHelper.getUserByUserID(userID);
			current_token = ApplicationValues.loginUser.getToken();
		}
		downloadForms();
		customHandler.postDelayed(updateTimerThread, 1000 * 30);
	}

	private void downloadForms(){
		HashMap<String, String> nameAndPassword = session.getUserDetails();
		String userName = nameAndPassword.get(SessionManager.KEY_NAME);
		if (userName != null){
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
					ArrayList<Image> imageList = dbHelper.getAllImagesByDocumentID(doc.getDocument_id());
					try{
						if(imageList.size()>0){
							ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
							for(Image image: imageList){
								String image_path = image.getImage_path();
								String field_name = image.getImage_name();
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("imagePath", image_path);
								map.put("field_name", field_name);
								list.add(map);
							}
							syncManager.submitDocument2(doc, form, list);
						}else{
							syncManager.submitDocument(doc, form);
						}
					}
					catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			customHandler.postDelayed(this, 1000 * 30);
		}
	};

	private void initializeUI() {
		session = new SessionManager(getApplicationContext());
		itemList = new ArrayList<DrawerItem>();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		addDrawerItem();
		adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item,
				itemList);
		mDrawerList.setAdapter(adapter);
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
			break;
		case 1:
			fragment = new CSVExportFragment();
			args.putString("CSV Export", itemList.get(position)
					.getItemName());
			setTitle("CSV Export");
			break;
		case 2:
			fragment = new HistoryFragment();
			args.putString(HistoryFragment.ITEM_NAME, itemList.get(position)
					.getItemName());
			setTitle(itemList.get(position).getItemName());
			break;
		case 3:
			fragment = new AboutFragment();
			args.putString("About", itemList.get(position)
					.getItemName());
			setTitle(itemList.get(position).getItemName());
			break;
		case 4:
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
			try{
				FragmentManager frgManager = getFragmentManager();
				frgManager.beginTransaction().replace(R.id.content_frame, fragment)
						.commit();
				mDrawerList.setItemChecked(position, true);
				setTitle(itemList.get(position).getItemName());
				mDrawerLayout.closeDrawer(mDrawerList);
			}
			catch(IllegalStateException e){
				
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
					toastManager.xaveyToast(null, "Not connecting with server...");
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

	private class FormDownloadTask extends AsyncTask<User, Void, ArrayList<Form>> {

		private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
		// properties
		XaveyProperties xavey_properties;
		String serverFormDownloadURL="";
		String localFromDownloadURL="";

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
			// update token first here
			String userName = ApplicationValues.loginUser.getUser_name();
			String password = ApplicationValues.loginUser.getPwd();
			SyncManager syncManager = new SyncManager(MainActivity.this);
			syncManager.updateToken(userName, password);

			try {
				User user = params[0];
				//RestClient f = new RestClient(localFromDownloadURL);
				RestClient f = new RestClient(serverFormDownloadURL);
				f.AddParam("id", user.getUser_id());
				f.AddHeader("x-access-token", user.getToken()); // <--token
				f.Execute(RequestMethod.GET);
				int responseCode = f.getResponseCode();
				String response = "";
//				Log.i("response :", f.getResponse());
//				userFormsList = parseJSONForm(response);
//				ApplicationValues.userFormList = userFormsList;
//				ApplicationValues.numberOfForm = userFormsList.size();

				if(responseCode==200){
					response = f.getResponse();
					Log.i("response :", f.getResponse());
					userFormsList = parseJSONForm(response);
					ApplicationValues.userFormList = userFormsList;
					ApplicationValues.numberOfForm = userFormsList.size();
				}
				else if(responseCode==403){
//					syncManager.updateToken(userName, password);
					// assumed 403 never come cuz updated the token before sync
				}
				else{
					toastManager.xaveyToast(null, "Server error..! ");
				}
//				else if(responseCode==403){
//					RestClient c = new RestClient("http://192.168.60.103:3000/authenticate/");
//					String deviceID = new SyncManager(MainActivity.this).getDeviceUniqueID(MainActivity.this);
//					c.AddParam("username", ApplicationValues.loginUser.getUser_name());
//					c.AddParam("password", ApplicationValues.loginUser.getPwd());
//					c.AddParam("device", deviceID);
//					c.Execute(RequestMethod.POST);
//				}
			} catch (Exception e) {
				Log.e("form error :", e.getMessage());
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
				if(result.size()>0){
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
							dbHelper.setAssignByUserIDAndFormID(userID, form_id,
									"1");
						}
					}
					
					toastManager.xaveyToast(null, "Successfully Loaded..");
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

}

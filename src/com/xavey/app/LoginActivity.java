package com.xavey.app;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xavey.app.db.XaveyDBHelper;
import com.xavey.app.model.RequestMethod;
import com.xavey.app.model.RestClient;
import com.xavey.app.model.User;
import com.xavey.app.util.ConnectionDetector;
import com.xavey.app.util.SessionManager;
import com.xavey.app.util.StringEncrytDecryManager;
import com.xavey.app.util.SyncManager;
import com.xavey.app.util.XaveyProperties;

public class LoginActivity extends Activity {

	DisplayMetrics dm;
	int width;
	int height;
	ImageView xaveyLogo;
	Button btnLogin;
	EditText edtUserName, edtPassword;
	TextView txtMsg;
	SessionManager session;
	Intent itt;
	XaveyDBHelper dbHelper;

	// private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// boolean noConnectivity =
	// intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
	// String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
	// boolean isFailover =
	// intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
	//
	// NetworkInfo currentNetworkInfo = (NetworkInfo)
	// intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
	// NetworkInfo otherNetworkInfo = (NetworkInfo)
	// intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
	//
	// // do application-specific task(s) based on the current network state,
	// such
	// // as enabling queuing of HTTP requests when currentNetworkInfo is
	// connected etc.
	//
	// }
	// };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
		loadUI();

		dbHelper = new XaveyDBHelper(this);

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ConnectionDetector detector = new ConnectionDetector(
						getApplicationContext());
				boolean isConnecting = detector.isConnectingToInternet();
				boolean b = isConnecting;
				if (detector.isConnectingToInternet()) {
					User u = new User();
					String username, password;
					username = edtUserName.getText().toString();
					password = edtPassword.getText().toString();
					if (username.trim().length() > 0
							&& password.trim().length() > 0) {
						u.setUser_name(username);
						u.setPwd(password);
						new AuthenticateTask().execute(u);
					}
				} else {
					// offline mode
					String userID = dbHelper.getUserIDByUserName(edtUserName
							.getText().toString());
					if (userID.length() != 0) {
						User user = dbHelper.getUserByUserID(userID);
						String userTypedPassword = StringEncrytDecryManager
								.getMD5(edtPassword.getText().toString());
						String realPassword = user.getHashPwd();
						if (realPassword.equals(userTypedPassword)) {
							session = new SessionManager(
									getApplicationContext());
							session.createLoginSession(userID,
									user.getUser_name(), user.getPwd());
							itt = new Intent(getApplicationContext(),
									MainActivity.class);
							startActivity(itt);
							finish();
						} else {
							txtMsg.setText("Invalid login !, try again.");
						}
					} else {
						txtMsg.setText("User name does not exist");
					}
				}
			}
		});
		setOnTouchListeners();
		// IntentFilter filter = new
		// IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		// registerReceiver(mConnReceiver, filter);
	}

	private void setOnTouchListeners() {
		
		edtUserName.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				txtMsg.setText("");
				return false;
			}
		});
		
		edtPassword.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				txtMsg.setText("");
				return false;
			}
		});		
	}

	private void loadUI() {
		session = new SessionManager(getApplicationContext());
		dm = this.getResources().getDisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
<<<<<<< HEAD
=======
//		xaveyLogo = (ImageView) findViewById(R.id.imgView_xaveyLogo);
//		xaveyLogo.setMinimumWidth(width/2-100);
//		xaveyLogo.setMinimumHeight(height/2-50);
>>>>>>> edb6fa1b25de474fb992564fab01ec368919d8aa
		btnLogin = (Button) findViewById(R.id.btnLogin);
		txtMsg = (TextView) findViewById(R.id.tvErrorMsg);
		edtUserName = (EditText) findViewById(R.id.edtName);
		edtUserName.setBackgroundResource(R.drawable.rounded_edittext);
		edtPassword = (EditText) findViewById(R.id.edtPwd);
		edtPassword.setBackgroundResource(R.drawable.rounded_edittext);
	}
<<<<<<< HEAD

// test mode only
//	 public void btnLoginClick(View view) {
//	 String username = edtUserName.getText().toString();
//	 String password = edtPassword.getText().toString();
//	 if (username.trim().length() == 0 && password.trim().length() == 0) {
//	 Toast.makeText(getApplicationContext(),
//	 "username & password required", 1000).show();
//	 }
//	 if (edtUserName.getText().toString().equals("xavey")
//	 && edtPassword.getText().toString().equals("xavey")){
//	 session.createLoginSession(username, password);
//	 Intent i = new Intent(this, MainActivity.class);
//	 startActivity(i);
//	 finish();
//	 }
//	 else {
//	 Toast.makeText(getApplicationContext(),
//	 "Wrong email or password.", Toast.LENGTH_SHORT).show();
//	 }
//	 }

	private class AuthenticateTask extends AsyncTask<User, Void, HashMap<String, String>> {
		// (1) do in background parameter
		// (2) progress
		// (3) post execute parameter
		private ProgressDialog Dialog = new ProgressDialog(LoginActivity.this);
		// properties
		XaveyProperties xavey_properties;
		String authenticateURL="";

		// String serverLoginURL = "serverLoginURL";
		// String serverFormURL = "serverFormURL";

		protected void onPreExecute() {
			Dialog.setMessage("Please wait..");
			Dialog.show();
			xavey_properties = new XaveyProperties();
			authenticateURL = xavey_properties.getAuthenticateURL();
		}

		protected HashMap<String, String> doInBackground(User... params) {
			HashMap<String, String> result = null;
			try {
				RestClient c = new RestClient(authenticateURL);
				String deviceID = new SyncManager(LoginActivity.this).getDeviceUniqueID(LoginActivity.this);
				c.AddParam("username", params[0].getUser_name());
				c.AddParam("password", params[0].getPwd());
				c.AddParam("device", deviceID);
				c.Execute(RequestMethod.POST);
				int userResponseCode = c.getResponseCode();
				result = parseJSON(c.getResponse());
				result.put("password", params[0].getPwd());
				result.put("response_code", userResponseCode+"");
			}
			catch(Exception e){
				Log.e("login server error :", e.getMessage());
			}
			return result;
		}

		private boolean isNumberInvolved(String column, String number_to_check){
			String[] numberArray = column.split(",");
			for(int i=0; i<numberArray.length; i++){
				if(numberArray[i].equals(number_to_check))
					return true;
			}
			return false;
=======
	
	public void btnLoginClick(View view){
		if(edtUserName.getText().toString().length()==0 && edtPassword.getText().toString().length()==0){
			Toast.makeText(getApplicationContext(), "Email and password required.", Toast.LENGTH_SHORT).show();
		}
		else if(edtUserName.getText().toString().equals("x") &&
				edtPassword.getText().toString().equals("x")){
			Intent i = new Intent(this,MainActivity.class);
			startActivity(i);
		}
		else{
			Toast.makeText(getApplicationContext(), "Wrong email or password.", Toast.LENGTH_SHORT).show();
>>>>>>> edb6fa1b25de474fb992564fab01ec368919d8aa
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			Dialog.dismiss();
			
			int responseCode = Integer.parseInt(result.get("response_code").toString());
			String userID = result.get("user_id");
			String username = result.get("username").toString();
			String password = result.get("password").toString();
			String hashPassword = result.get("hashPassword").toString();
			String email = result.get("email").toString();
			String role = result.get("role").toString();
			String organization = result.get("organization").toString();
			String token = result.get("token").toString();
			 
			if(responseCode==200){
				session = new SessionManager(getApplicationContext());
				session.createLoginSession(userID,
						username, hashPassword);

				User loggedInUser = new User();
				loggedInUser.setUser_id(userID);
				loggedInUser.setUser_name(username);
				loggedInUser.setPwd(password);
				loggedInUser.setHashPwd(hashPassword);
				loggedInUser.setEmail(email);
				loggedInUser.setRole(role);
				loggedInUser.setOrganization(organization);
				loggedInUser.setToken(token);
				
				checkUserAndStore(loggedInUser);
				ApplicationValues.loginUser = loggedInUser;
				itt = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(itt);
				finish();
			}
			else if(responseCode==401){
				txtMsg.setText("login info wrong");
			}

		}

		private void checkUserAndStore(User result) {
			// check user
			if (!dbHelper.isUserAlreadyExistInDB(result.getUser_id())) {
				Dialog.setMessage("Saving User");
				/*User user = new User();
				user.setUser_id(result.getUser_id());
				user.setUser_name(result.getUser_name());
				user.setHashPwd(result.getHashPwd());
				user.setEmail(result.getEmail());
				user.setRole(result.getRole());
				user.setOrganization(result.getOrganization());*/
				dbHelper.addNewUser(result);
			} else {
				Toast.makeText(getApplicationContext(), "AlreadyExist", 500)
						.show();
				dbHelper.updateUser(result);
			}
		}

		private User parseJSONLogin(String response) {
			User user = new User();
			if (response != null) {
				Log.i("JSON", response.toString());
				JSONObject jsResponse;
				try {
					jsResponse = new JSONObject(response);
					user.setUser_id(jsResponse.getString("user_id"));
					user.setUser_name(jsResponse.getString("username"));
					user.setHashPwd(jsResponse.getString("password"));
					user.setEmail(jsResponse.getString("email"));
					user.setRole(jsResponse.getString("role"));
					user.setOrganization(jsResponse.getString("organization"));
/*					user.setCode("200");
					user.setMessage("Authorize");*/
					user.setToken(jsResponse.getString("token"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
/*				user.setCode("401");
				user.setMessage("Unathuorize");*/
			}
			return user;
		}

		private HashMap<String, String> parseJSON(String response) {
			HashMap<String, String> responseMap = new HashMap<String, String>();
			if (response != null) {
				Log.i("JSON", response.toString());
				JSONObject jsResponse;
				try {
					jsResponse = new JSONObject(response);
					responseMap.put("user_id", jsResponse.getString("user_id"));
					responseMap.put("username", jsResponse.getString("username"));
					responseMap.put("hashPassword", jsResponse.getString("password"));
					responseMap.put("email", jsResponse.getString("email"));
					responseMap.put("role", jsResponse.getString("role"));
					//responseMap.put("organization", jsResponse.getString("organization"));
					responseMap.put("organization", jsResponse.getInt("organization")+"");
					responseMap.put("response_code", "200");
					//responseMap.put("organization", jsResponse.getString("organization"));
					responseMap.put("message", "Authorize");
					responseMap.put("token", jsResponse.getString("token"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				responseMap.put("response_code", "401");
				responseMap.put("message", "Unauthorize");
			}
			return responseMap;
		}
	}

	// following code is inner class for async
	/*private class CallRIP extends AsyncTask<User, Void, User> {
		// (1) do in background parameter
		// (2) progress
		// (3) post execute parameter
		private ProgressDialog Dialog = new ProgressDialog(LoginActivity.this);
		// properties
		Properties properties;
		String loginURL_ko_atk = "loginURL_ko_atk";
		String formURL_ko_atk = "formURL_ko_atk";
		String serverLoginURL = "serverLoginURL";
		String serverFormURL = "serverFormURL";
		String serverLoginURLToken = "serverLoginURLToken";
		// String serverLoginURL = "serverLoginURL";
		// String serverFormURL = "serverFormURL";

		protected void onPreExecute() {
			Dialog.setMessage("Please wait..");
			Dialog.show();
		}

		protected User doInBackground(User... params) {
			User usr = new User();
			User u;
			// properties stuffs
			loadProperties();

			// here is current works
			try {
				RestClient c = new RestClient(serverLoginURLToken);
				 * c.AddHeader("content type", "application/json");
				c.AddParam("username", params[0].getUser_name());
				c.AddParam("password", params[0].getPwd());
				c.Execute(RequestMethod.POST);
				int userResponseCode = c.getResponseCode();
				String s = c.getResponse();
				u = parseJSONLogin(c.getResponse());
				if(userResponseCode==200){
					
				}
				else if(userResponseCode==401){
					txtMsg.setText("login info wrong");
				}
				RestClient f = new RestClient("http://dev.xavey.com/api/get/forms/worker/");
				f.AddParam("id", u.getUser_id());
				f.Execute(RequestMethod.GET);
				usr.setUser_id(u.getUser_id());
				usr.setUser_name(u.getUser_name());
				usr.setPwd(params[0].getPwd());
				usr.setHashPwd(u.getHashPwd());
				usr.setEmail(u.getEmail());
				usr.setRole(u.getRole());
				usr.setOrganization(u.getOrganization());
				usr.setCode(u.getCode());
				usr.setMessage(u.getMessage());
				usr.setResult((f.getResponseCode() == 200) ? true : false);
				usr.setErr(f.getErrorMessage());
				u.setToken(u.getToken());
			} catch (Exception e) {
				if (usr.getErr() != null)
					usr.setErr(e.getMessage());
			}
			return usr;
		}

		private void loadProperties() {
			properties = new Properties();
			InputStream in = LoginActivity.class
					.getResourceAsStream("/com/xavey/app/util/xavey_properties.properties");
			try {
				properties.load(new InputStreamReader(in, "UTF-8"));
				loginURL_ko_atk = properties.getProperty(loginURL_ko_atk);
				formURL_ko_atk = properties.getProperty(formURL_ko_atk);
				serverLoginURL = properties.getProperty(serverLoginURL);
				serverFormURL = properties.getProperty(serverFormURL);
				serverLoginURLToken = properties.getProperty(serverLoginURLToken);
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Unspported Encoding",
						1000).show();
			}
		}

		@Override
		protected void onPostExecute(User result) {
			Dialog.dismiss();
			try {
				if (!result.getErr().toString().equals("OK")) {
					txtMsg.setText(result.getErr());
				} else if (result.isResult() && result.getCode().equals("200")) {
					session = new SessionManager(getApplicationContext());
					session.createLoginSession(result.getUser_id(),
							result.getUser_name(), result.getPwd());
					checkUserAndStore(result);
					itt = new Intent(getApplicationContext(),
							MainActivity.class);
					startActivity(itt);
					finish();
				} else {
					txtMsg.setText("Code : " + result.getCode() + ", "
							+ "Message : " + result.getMessage());
				}
			} catch (Exception e) {
				txtMsg.setText("Connection Problem.");
			}
		}

		private void checkUserAndStore(User result) {
			// check user
			if (!dbHelper.isUserAlreadyExistInDB(result.getUser_id())) {
				Dialog.setMessage("Saving User");
				User user = new User();
				user.setUser_id(result.getUser_id());
				user.setUser_name(result.getUser_name());
				user.setHashPwd(result.getHashPwd());
				user.setEmail(result.getEmail());
				user.setRole(result.getRole());
				user.setOrganization(result.getOrganization());
				dbHelper.addNewUser(user);
			} else {
				Toast.makeText(getApplicationContext(), "AlreadyExist", 500)
						.show();
			}
		}

		private User parseJSONLogin(String response) {
			User user = new User();
			if (response != null) {
				Log.i("JSON", response.toString());
				JSONObject jsResponse;
				try {
					jsResponse = new JSONObject(response);
					user.setUser_id(jsResponse.getString("user_id"));
					user.setUser_name(jsResponse.getString("username"));
					user.setHashPwd(jsResponse.getString("password"));
					user.setEmail(jsResponse.getString("email"));
					user.setRole(jsResponse.getString("role"));
					user.setOrganization(jsResponse.getString("organization"));
					user.setCode("200");
					user.setMessage("Authorize");
					user.setToken(jsResponse.getString("token"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				user.setCode("401");
				user.setMessage("Unathorize");
			}
			return user;
		}
	}*/
	
	@Override
	public void onBackPressed() {
		Intent homeIntent = new Intent(Intent.ACTION_MAIN);
    	homeIntent.addCategory( Intent.CATEGORY_HOME );
    	homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(homeIntent);		
	}
}

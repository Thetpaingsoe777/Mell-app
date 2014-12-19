package com.xavey.android;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xavey.android.db.XaveyDBHelper;
import com.xavey.android.model.RequestMethod;
import com.xavey.android.model.RestClient;
import com.xavey.android.model.User;
import com.xavey.android.util.ConnectionDetector;
import com.xavey.android.util.SessionManager;
import com.xavey.android.util.StringEncrytDecryManager;
import com.xavey.android.util.SyncManager;
import com.xavey.android.util.ToastManager;
import com.xavey.android.util.XaveyProperties;

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

			private void hideLoginKeyboard() {
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				// check if no view has focus:
				View view = getCurrentFocus();
				if (view != null) {
					inputManager.hideSoftInputFromWindow(view.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}

			@Override
			public void onClick(View v) {

				hideLoginKeyboard();
				txtMsg.setText("");

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
		btnLogin = (Button) findViewById(R.id.btnLogin);
		txtMsg = (TextView) findViewById(R.id.tvErrorMsg);
		edtUserName = (EditText) findViewById(R.id.edtName);
		edtUserName.setBackgroundResource(R.drawable.rounded_edittext);
		edtPassword = (EditText) findViewById(R.id.edtPwd);
		edtPassword.setBackgroundResource(R.drawable.rounded_edittext);
	}

	// test mode only
	// public void btnLoginClick(View view) {
	// String username = edtUserName.getText().toString();
	// String password = edtPassword.getText().toString();
	// if (username.trim().length() == 0 && password.trim().length() == 0) {
	// Toast.makeText(getApplicationContext(),
	// "username & password required", 1000).show();
	// }
	// if (edtUserName.getText().toString().equals("xavey")
	// && edtPassword.getText().toString().equals("xavey")){
	// session.createLoginSession(username, password);
	// Intent i = new Intent(this, MainActivity.class);
	// startActivity(i);
	// finish();
	// }
	// else {
	// Toast.makeText(getApplicationContext(),
	// "Wrong email or password.", Toast.LENGTH_SHORT).show();
	// }
	// }

	private class AuthenticateTask extends
			AsyncTask<User, Void, HashMap<String, String>> {
		// (1) do in background parameter
		// (2) progress
		// (3) post execute parameter
		private ProgressDialog Dialog = new ProgressDialog(LoginActivity.this);
		// properties
		XaveyProperties xavey_properties;
		String authenticateURL = "";
		String localAuthenticate = "";

		// String serverLoginURL = "serverLoginURL";
		// String serverFormURL = "serverFormURL";

		protected void onPreExecute() {
			Dialog.setMessage("Please wait..");
			Dialog.show();
			xavey_properties = new XaveyProperties();
			authenticateURL = xavey_properties.getAuthenticateURL();
			localAuthenticate = xavey_properties.getLocalAuthenticateURL();
		}

		protected HashMap<String, String> doInBackground(User... params) {
			HashMap<String, String> result = new HashMap<String, String>();
			try {
				RestClient c = new RestClient(authenticateURL);
				// RestClient c = new RestClient(localAuthenticate);
				String deviceID = new SyncManager(LoginActivity.this)
						.getDeviceUniqueID(LoginActivity.this);
				c.AddParam("username", params[0].getUser_name());
				c.AddParam("password", params[0].getPwd());
				c.AddParam("device", deviceID);
				c.Execute(RequestMethod.POST);
				int userResponseCode = c.getResponseCode();
				if (userResponseCode == 200) {
					result = parseJSON(c.getResponse());
					result.put("password", params[0].getPwd());
				}
				result.put("response_code", userResponseCode + "");
			} catch (Exception e) {
				Log.e("login server error :", e.getMessage());
			}
			return result;
		}

		private boolean isNumberInvolved(String column, String number_to_check) {
			String[] numberArray = column.split(",");
			for (int i = 0; i < numberArray.length; i++) {
				if (numberArray[i].equals(number_to_check))
					return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			Dialog.dismiss();

			int responseCode = Integer.parseInt(result.get("response_code")
					.toString());

			if (responseCode == 200) {
				String userID = result.get("user_id");
				String username = result.get("username").toString();
				String password = result.get("password").toString();
				String hashPassword = result.get("hashPassword").toString();
				String email = result.get("email").toString();
				String role = result.get("role").toString();
				String organization = result.get("organization").toString();
				String logo = result.get("logo").toString();
				String token = result.get("token").toString();
				session = new SessionManager(getApplicationContext());
				session.createLoginSession(userID, username, hashPassword);
				User loggedInUser = new User();
				loggedInUser.setUser_id(userID);
				loggedInUser.setUser_name(username);
				loggedInUser.setPwd(password);
				loggedInUser.setHashPwd(hashPassword);
				loggedInUser.setEmail(email);
				loggedInUser.setRole(role);
				loggedInUser.setOrganization(organization);
				loggedInUser.setLogoName(logo);
				loggedInUser.setToken(token);

				checkUserAndStore(loggedInUser);
				ApplicationValues.loginUser= loggedInUser;

				if (!logo.equals("null") && logo.length() > 0) { // <- logo involves
					// download logo here and start activity only if the
					// downloadTask() complete
					HashMap<String, String> tokenAndLogo = new HashMap<String, String>();
					tokenAndLogo.put("token", token);
					tokenAndLogo.put("logo", logo);
					tokenAndLogo.put("user_id", userID);
					new LogoDownloadAndTask().execute(tokenAndLogo);
					
				} else { // <- logo doesn't involve
							// do as usual
					
					itt = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(itt);
					finish();
				}
				ApplicationValues.loginUser = loggedInUser; // if logo involved or not whatever, loggedInUser must be assigned
			} else if (responseCode == 403) {
				txtMsg.setText("Token expired....!");
			} else if (responseCode == 401) {
				txtMsg.setText("Login failed, Try again.");
			}
		}

		private void checkUserAndStore(User result) {
			// check user
			if (!dbHelper.isUserAlreadyExistInDB(result.getUser_id())) {
				Dialog.setMessage("Saving User");
				/*
				 * User user = new User(); user.setUser_id(result.getUser_id());
				 * user.setUser_name(result.getUser_name());
				 * user.setHashPwd(result.getHashPwd());
				 * user.setEmail(result.getEmail());
				 * user.setRole(result.getRole());
				 * user.setOrganization(result.getOrganization());
				 */
				dbHelper.addNewUser(result);
			} else {
				Toast.makeText(getApplicationContext(), "AlreadyExist", 500)
						.show();
				dbHelper.updateUser(result);
			}
		}

		
		private HashMap<String, String> parseJSON(String response) {
			HashMap<String, String> responseMap = new HashMap<String, String>();
			if (response != null) {
				Log.i("JSON", response.toString());
				JSONObject jsResponse;
				try {
					jsResponse = new JSONObject(response);
					responseMap.put("user_id", jsResponse.getString("user_id"));
					responseMap.put("username",
							jsResponse.getString("username"));
					responseMap.put("hashPassword",
							jsResponse.getString("password"));
					responseMap.put("email", jsResponse.getString("email"));
					responseMap.put("role", jsResponse.getString("role"));
					// responseMap.put("organization",
					// jsResponse.getString("organization"));
					responseMap.put("organization",
							jsResponse.getInt("organization") + "");
					responseMap.put("logo", jsResponse.getString("logo"));
					responseMap.put("response_code", "200");
					// responseMap.put("organization",
					// jsResponse.getString("organization"));
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

	private class LogoDownloadAndTask extends
			AsyncTask<HashMap<String, String>, Void, HashMap<String, Object>> {

		private ProgressDialog Dialog = new ProgressDialog(LoginActivity.this);
		// properties
		XaveyProperties xavey_properties;
		String logoDownloadURL = "";
		ToastManager toastManager;
		// String serverLoginURL = "serverLoginURL";
		// String serverFormURL = "serverFormURL";

		protected void onPreExecute() {
			Dialog.setMessage("Downloading Company Logo..");
			Dialog.show();
			xavey_properties = new XaveyProperties();
			logoDownloadURL = xavey_properties.getSyncImageDownloadURL();
			toastManager = new ToastManager(LoginActivity.this);
		}

		protected HashMap<String, Object> doInBackground(HashMap<String, String>... params) {

			
			
			// ---------- updating token.. --------------------------------
			String userName =ApplicationValues.loginUser.getUser_name();
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

			HashMap<String, Object> result = new HashMap<String, Object>();
			
			if(userResponseCode_==200){

				HashMap<String, String> logoAndToken = params[0];
				String logo = logoAndToken.get("logo");
				String token = logoAndToken.get("token");
				String userID = logoAndToken.get("user_id");

				try {
					RestClient c = new RestClient(logoDownloadURL+logo); // <-- concat logo here
					// RestClient c = new RestClient(localAuthenticate);
					c.AddHeader("x-access-token", token); // <-- add token here
					c.Execute(RequestMethod.GET);
					int userResponseCode = c.getResponseCode();
					if (userResponseCode == 200) {
						result.put("logo_image", c.getResponseImage());
					}
					result.put("response_code", userResponseCode + "");
					result.put("user_id", userID);
				} catch (Exception e) {
					Log.e("logo server error :", e.getMessage());
				}
			}
			else if(userResponseCode_==401){
				toastManager.xaveyToast(null, "Authenticating failed when downloading form..");
			}
			else if(userResponseCode_==403){
				toastManager.xaveyToast(null, "server error when downloading form.. at Main Activity");
			}
			return result;
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> result) {
			Dialog.dismiss();

			if(result.containsKey("response_code")){
				int responseCode = Integer.parseInt(result.get("response_code")
						.toString());
				if (responseCode == 200) {
					byte[] logoImage = (byte[]) result.get("logo_image");
					String userID = result.get("user_id").toString();
					ApplicationValues.loginUser.setLogoImage(logoImage);
					dbHelper.updateUser(ApplicationValues.loginUser);
					itt = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(itt);
					finish();
				} else if (responseCode == 403) {
					txtMsg.setText("CODE 403, Token expired when downloading company logo....!");
				} else if (responseCode == 401) {
					txtMsg.setText("CODE 401, Login failed when downloading company logo , Try again.");
				}
			}
			else{
				toastManager.xaveyToast(null, "Couldn't get any response from server. (LoginActivity)"); 
			}
		}
	}

	@Override
	public void onBackPressed() {
		Intent homeIntent = new Intent(Intent.ACTION_MAIN);
		homeIntent.addCategory(Intent.CATEGORY_HOME);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
	}
	
	private byte[] getLogoImage(String url){
        try {
                URL imageUrl = new URL(url);
                URLConnection ucon = imageUrl.openConnection();

                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);

                ByteArrayBuffer baf = new ByteArrayBuffer(500);
                int current = 0;
                while ((current = bis.read()) != -1) {
                        baf.append((byte) current);
                }

                return baf.toByteArray();
        } catch (Exception e) {
                Log.d("ImageManager", "Error: " + e.toString());
                Toast.makeText(getApplicationContext(), e.getMessage(), 3000);
                return null;
        }

   }
}

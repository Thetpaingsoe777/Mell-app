package com.xavey.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.xavey.app.R;
import com.xavey.app.model.RequestMethod;
import com.xavey.app.model.RestClient;
import com.xavey.app.model.User;
import com.xavey.app.util.SessionManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	DisplayMetrics dm;
	int width;
	int height;
	ImageView xaveyLogo;
	Button btnLogin;
	EditText edtUserName, edtPassword;
	SessionManager session;
	Intent itt;
//	private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
//            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
//                        boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
//            
//            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
//            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
//                        
//            // do application-specific task(s) based on the current network state, such 
//            // as enabling queuing of HTTP requests when currentNetworkInfo is connected etc.
//            
//        }
//    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
		loadUI();
//		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);        
//		registerReceiver(mConnReceiver, filter);
	}
	
	private void loadUI(){
		session = new SessionManager(getApplicationContext());
		dm = this.getResources().getDisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		xaveyLogo = (ImageView) findViewById(R.id.imgView_xaveyLogo);
		xaveyLogo.setMinimumWidth(width/2-100);
		xaveyLogo.setMinimumHeight(height/2-50);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		edtUserName = (EditText) findViewById(R.id.edtName);
		edtPassword = (EditText) findViewById(R.id.edtPwd);
	}
	
	//test mode only
	public void btnLoginClick(View view){
		String username = edtUserName.getText().toString();
		String password = edtPassword.getText().toString();

		if(username.trim().length()==0 && password.trim().length()==0){
			Toast.makeText(getApplicationContext(), "username & password required", 1000).show();
		}
		if(edtUserName.getText().toString().equals("xavey")&&edtPassword.getText().toString().equals("xavey")){
			session.createLoginSession(username, password);
			Intent i = new Intent(this,MainActivity.class);
			startActivity(i);
			finish();
		}
		else{
			Toast.makeText(getApplicationContext(), "Wrong Information..!", 1000).show();
		}
	}
//	
//	public void btnLoginClick(View view){
//		User u = new User();
//		String username, password = "";
//		username = edtUserName.getText().toString();
//		password = edtPassword.getText().toString();
//		
//		if(username.trim().length() > 0 && password.trim().length()>0){
//			u.setUser_name(username);
//			u.setPwd(password);
//			u.setBtnState("Log in");
//			u.setAction("and");
//			new CallRIP().execute(u);
//		}
//		
//	}
	
	// following code is inner class for async 
	private class CallRIP extends AsyncTask<User, Void, User> {
		private ProgressDialog Dialog = new ProgressDialog(LoginActivity.this);

		protected void onPreExecute() {
			Dialog.setMessage("Please wait..");
			Dialog.show();
		}

		protected User doInBackground(User... params) {
			User usr = new User();
			try {
				if (params[0].getBtnState() == "Log in") {

					RestClient c = new RestClient(
							"http://192.168.60.101:80/dev.xavey.com/login/");
					//c.AddHeader("Authorization","Basic " + 
					//Base64.encodeToString((params[0].getUser_name()+":"+params[0].getPwd()).getBytes(),Base64.DEFAULT));
					//c.AddHeader("Access-Control-Allow-Origin", "*");
					//c.AddHeader("Access-Control-Allow-Headers", "*");
					c.AddParam("username", params[0].getUser_name());
					c.AddParam("password", params[0].getPwd());
					c.AddParam("action", params[0].getAction());
					c.Execute(RequestMethod.POST);
					usr = parseJSON(c.getResponse());
					usr.setUser_name(params[0].getUser_name());
					usr.setPwd(params[0].getPwd());
					usr.setBtnState("Log in");
					usr.setResult((c.getResponseCode() == 200) ? true : false);
					usr.setErr(c.getErrorMessage());

				} /*else if (params[0].getBtnState() == "Log out") {
					RestClient c = new RestClient(
							"http://192.168.60.101:80/xavey-web-app/logout/and/");
					c.Execute(RequestMethod.GET);
					u.setBtnState("Log out");
					u.setResult((c.getResponseCode() == 200) ? true : false);
					u.setErr(c.getErrorMessage());
				}*/
			} catch (Exception e) {
				if (usr.getErr() != null)
					usr.setErr(e.getMessage());
			}

			return usr;
		}

		@Override
		protected void onPostExecute(User result) {
			Dialog.dismiss();

			btnLogin = (Button) findViewById(R.id.btnLogin);

			if (!result.getErr().equals("OK")) {

				//txtMsg.setText(result.getErr());

			} else if (result.getResult() && result.getBtnState().equals("Log in") && result.getCode().equals("200")) {
				session = new SessionManager(getApplicationContext());
				session.createLoginSession(result.getUser_name(), result.getPwd());
				itt = new Intent (getApplicationContext(), MainActivity.class);
				startActivity(itt);
				finish();

			}
			else {
				//txtMsg.setText("Code : " + result.getCode() + ", " + "Message : " + result.getMessage());
				Toast.makeText(getApplicationContext(), "Code: " + result.getCode() + ", " +"Message : "+ result.getMessage(), 1000).show();
			}
		}
		
		
		private User parseJSON(String response) {

			User user = new User();
			response = "{" + "'Ack'" + ":" + response + "}";
			Log.i("JSON", response.toString());
			JSONObject jsResponse;
			try {
				jsResponse = new JSONObject(response);
				JSONArray jsMainNode = jsResponse.optJSONArray("Ack");
				for (int i = 0; i < jsMainNode.length(); i++) {

					JSONObject jsChildNode = jsMainNode.getJSONObject(i);
					user.setCode(jsChildNode.getString("code"));
					user.setMessage(jsChildNode.getString("message"));
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return user;
		}

		

	}
	
}

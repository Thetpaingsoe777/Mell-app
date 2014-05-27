package com.xavey.app;

import com.xavey.app.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	DisplayMetrics dm;
	int width;
	int height;
	ImageView xaveyLogo;
	Button btnLogin;
	EditText edtUserName, edtPassword;
	
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
		dm = this.getResources().getDisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
//		xaveyLogo = (ImageView) findViewById(R.id.imgView_xaveyLogo);
//		xaveyLogo.setMinimumWidth(width/2-100);
//		xaveyLogo.setMinimumHeight(height/2-50);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		edtUserName = (EditText) findViewById(R.id.edtName);
		edtPassword = (EditText) findViewById(R.id.edtPwd);
	}
	
	public void btnLoginClick(View view){
		if(edtUserName.getText().toString().length()==0 && edtPassword.getText().toString().length()==0){
			Toast.makeText(getApplicationContext(), "username & password required", 1000).show();
		}
		if(edtUserName.getText().toString().equals("xavey")&&edtPassword.getText().toString().equals("xavey")){
			Intent i = new Intent(this,MainActivity.class);
			startActivity(i);
		}
		else{
			Toast.makeText(getApplicationContext(), "Wrong Information..!", 1000).show();
		}

	}
	
}

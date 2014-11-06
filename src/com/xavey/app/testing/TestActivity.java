package com.xavey.app.testing;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.xavey.app.R;
import com.xavey.app.util.ConnectionDetector;

public class TestActivity extends Activity {

	Button btnStart;
	private Handler customHandler = new Handler();
	ConnectionDetector detector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_screen);
		detector = new ConnectionDetector(this);
		
		
		
		btnStart = (Button) findViewById(R.id.btnStart);
		btnStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				customHandler.postDelayed(updateTimerThread, 1000*30);
			}
		});
	}
	
	private Runnable updateTimerThread = new Runnable() {
		
		@Override
		public void run() {
			Toast.makeText(getApplicationContext(), "From runnable thread", 100).show();
			customHandler.postDelayed(this, 1000*30);
			
		}
	};
	

}

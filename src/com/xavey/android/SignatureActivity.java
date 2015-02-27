package com.xavey.android;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SignatureActivity extends Activity {

	GestureOverlayView govSignature;
	Button btnOKSignature, btnClearSignature, btnCancelSignature;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signature_view);
		loadUI();
		
		btnOKSignature.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bitmap bmp = Bitmap.createBitmap(govSignature
						.getDrawingCache());
				
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.JPEG, 100,
						stream);
				byte[] byteArrayImage = stream.toByteArray();
				
				/*Bitmap newBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

				Canvas canvas = new Canvas(newBitmap);
				canvas.drawColor(Color.WHITE);
				canvas.drawBitmap(bmp, 0, 0, null);*/
				
				/*boolean isGestureVisible = govSignature.isPressed();
				Toast.makeText(getApplicationContext(), "isPreesed :"+isGestureVisible, 1000).show();*/
				/*Intent toDocumentInput = new Intent(SignatureActivity.this, DocumentInputActivity.class);
				toDocumentInput.putExtra("signature", byteArrayImage);
				startActivity(toDocumentInput);*/
//				DocumentInputActivity.mySignature = byteArrayImage;
//				DocumentInputActivity.signatureBitmap = bmp;
				finish();
			}
		});
		
		btnClearSignature.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				govSignature.cancelClearAnimation();
				govSignature.clear(true);
			}
		});
		
		btnCancelSignature.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void loadUI() {
		govSignature = (GestureOverlayView) findViewById(R.id.govSignature_view);
		govSignature.setDrawingCacheEnabled(true);
		govSignature.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
		btnOKSignature = (Button) findViewById(R.id.btnOKSignature);
		btnClearSignature = (Button) findViewById(R.id.btnClearSignature);
		btnCancelSignature = (Button) findViewById(R.id.btnCancelSignature);
	}
}

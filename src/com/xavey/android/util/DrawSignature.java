package com.xavey.android.util;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xavey.android.ApplicationValues;
import com.xavey.android.R;

public class DrawSignature extends Activity {
	DrawingView dv;
	private Paint mPaint;
	// private DrawingView mDrawingManager=null;

	String root;
	File myDir;
	String fname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dv = new DrawingView(this);
		setContentView(dv);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.BLACK); // <-- pen color
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.BEVEL);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(3); // <-- pen size here

		Intent i = getIntent();
		String field_help = i.getStringExtra("field_help");
/*		Toast toast = Toast.makeText(this, field_help, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0,0);
		toast.show();*/
		ToastManager toastManager = new ToastManager(this);
		toastManager.xaveyToast(null, field_help);

	}

	public class DrawingView extends View {

		public int width;
		public int height;
		private Bitmap mBitmap;
		private Canvas mCanvas;
		private Path mPath;
		private Paint mBitmapPaint;
		Context context;
		private Paint circlePaint;
		private Path circlePath;
		Button btnsave;

		public DrawingView(Context c) {
			super(c);
			context = c;
			mPath = new Path();
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);
			circlePaint = new Paint();
			circlePath = new Path();
			circlePaint.setAntiAlias(true);
			circlePaint.setColor(Color.BLUE);
			circlePaint.setStyle(Paint.Style.STROKE);
			circlePaint.setStrokeJoin(Paint.Join.MITER);
			circlePaint.setStrokeWidth(5);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);

			mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);

		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

			canvas.drawPath(mPath, mPaint);

			canvas.drawPath(circlePath, circlePaint);
		}

		private float mX, mY;
		private static final float TOUCH_TOLERANCE = 4;

		private void touch_start(float x, float y) {
			mPath.reset();
			mPath.moveTo(x, y);
			mX = x;
			mY = y;
		}

		private void touch_move(float x, float y) {
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
				mX = x;
				mY = y;

				circlePath.reset();
				circlePath.addCircle(mX, mY, 25, Path.Direction.CW); // <- radius is circle size
			}
		}

		private void touch_up() {
			mPath.lineTo(mX, mY);
			circlePath.reset();
			// commit the path to our offscreen
			mCanvas.drawPath(mPath, mPaint);
			// kill this so we don't double draw
			mPath.reset();
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				touch_move(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				invalidate();
				break;
			}
			return true;
		}

		public Bitmap getBitmap() {
			// this.measure(100, 100);
			// this.layout(0, 0, 100, 100);
			this.setDrawingCacheEnabled(true);
			this.buildDrawingCache();
			Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
			this.setDrawingCacheEnabled(false);

			return bmp;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.detail_menu, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.cancel:
			Log.i("Cancel", "Cancel");
			// setResult(0);
//			Utils.setImage_Path(this, "");
			finish();
			break;
		case R.id.save:
			Bitmap bm = (Bitmap) dv.getBitmap();
			
			Bitmap newBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), bm.getConfig());

			Canvas canvas = new Canvas(newBitmap);
			canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(bm, 0, 0, null);
			
			String imagePath = saveImageToLocalStore(newBitmap);

			Intent i = getIntent();
			String field_name = i.getStringExtra("field_name");
			String field_type = i.getStringExtra("field_type");
			String view_id = i.getStringExtra("view_id");
			i.putExtra("field_name", field_name);
			i.putExtra("field_type", field_type);
			i.putExtra("view_id", view_id);
			
			
//			Random generator = new Random();
//			int n = 1000000000;
//			n = generator.nextInt(n);
//			String fname = "Signature-"+ n +".jpg";
//			String sdCard = ImageSavingManager.getSDCardPath();
//			//String imgPath = sdCard+"/tmp/images/"+fname;
//			String imgPath = "/mnt/tmp/images/"+fname;
//			ImageSavingManager imageSavingManager = new ImageSavingManager(imgPath, fname);
//			int result_code = imageSavingManager.saveImage(bm);
//			if(result_code==ImageSavingManager.IO_EXCEPTION){
//				Log.e("IO_EXCEPTION", "IO_EXCEPTION occurs :(");
//			}else if(result_code==ImageSavingManager.FILE_NOT_FOUND_EXCEPTION){
//				Log.e("IO_EXCEPTION", "FILE_NOT_EXCEPTION occurs :(");
//			}else{
//				Log.i("SUCCESS", "SUCCESS fully save");
//			}
			

			i.putExtra("signPath", imagePath);
			setResult(2,i);
			finish();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private String saveImageToLocalStore(Bitmap finalBitmap) { 
        root = Environment.getExternalStorageDirectory().toString();
		myDir = new File(ApplicationValues.XAVEY_DIRECTORY + "/Drawing");
        //myDir = new File("mnt/sdcard" + "/temp");
       myDir.mkdirs(); 
        fname = "_image"+ System.currentTimeMillis() +".jpeg";
       File file = new File (myDir, fname);
       if (file.exists()) file.delete(); 
       try {
           FileOutputStream out = new FileOutputStream(file);
           finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
           Toast.makeText(getBaseContext(), "Save"+ fname + "Path " + myDir, Toast.LENGTH_LONG).show();
           Log.i("Save", fname + "Path " + myDir);
         
           out.flush();
           out.close(); 
       } catch (Exception e) {
           e.printStackTrace();
       }
       
       return myDir+"/"+fname;
   }
}

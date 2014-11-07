package com.xavey.app.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

// referenced from
// http://stackoverflow.com/questions/8969072/android-save-images-to-specific-folder-in-the-sd-card
public class ImageSavingManager {
	File myDir;
	String fileName;
	File file;

	public static final int FILE_NOT_FOUND_EXCEPTION = 4949;
	public static final int IO_EXCEPTION = 9494;
	public static final int SUCCESS = 7777;

	public ImageSavingManager(String filePath, String fileName) {
		this.fileName = fileName;
		myDir = new File(filePath);
		myDir.mkdirs();
		file = new File(myDir, fileName);
		if (file.exists())
			file.delete();
	}

	public int saveImage(Bitmap bitmapImage) {
		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmapImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			return SUCCESS;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return FILE_NOT_FOUND_EXCEPTION;
		} catch (IOException e) {
			e.printStackTrace();
			return IO_EXCEPTION;
		}
	}

	public int saveImageInSDCard(Bitmap bitmapImage) {
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath());
		dir.mkdirs();

		File out = new File(dir, fileName);
		try {
			out.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		DataOutputStream fo = null;
		try {
			fo = new DataOutputStream(new FileOutputStream(out));
			fo.close();
			return SUCCESS;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return FILE_NOT_FOUND_EXCEPTION;
		} catch (IOException e) {
			e.printStackTrace();
			return IO_EXCEPTION;
		}
	}

	public static String getSDCardPath() {
		File sdCard = Environment.getExternalStorageDirectory();
		return sdCard.getAbsolutePath();
	}

	public static void loadImageFromLocal(String imgPath, ImageView imageView) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
		imageView.setImageBitmap(bitmap);
	}

	@SuppressWarnings("deprecation")
	public static String getMiniPhotoPath(Activity act) {
		// http://stackoverflow.com/questions/14627900/camera-intent-data-null-in-onactivityresultint-requestcode-int-resultcode-int
		String[] projection = {
				MediaStore.Images.Thumbnails._ID, // The columns we want
				MediaStore.Images.Thumbnails.IMAGE_ID,
				MediaStore.Images.Thumbnails.KIND,
				MediaStore.Images.Thumbnails.DATA };
		String selection = MediaStore.Images.Thumbnails.KIND + "=" + // Select
				// only
				// mini's
				MediaStore.Images.Thumbnails.MINI_KIND;
		String sort = MediaStore.Images.Thumbnails._ID + " DESC";
		Cursor myCursor = act.managedQuery(
				MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection,
				selection, null, sort);
		String thumbnailPath = "";
		try {
			myCursor.moveToFirst();

			thumbnailPath = myCursor.getString(myCursor
					.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
		} finally {
			// do something
			myCursor.close();
		}
		return thumbnailPath;
	}

	public static String getLargePhotoPath(Activity act) {
		// http://stackoverflow.com/questions/14627900/camera-intent-data-null-in-onactivityresultint-requestcode-int-resultcode-int
		String[] largeFileProjection = { MediaStore.Images.ImageColumns._ID,
				MediaStore.Images.ImageColumns.DATA };
		String largeFileSort = MediaStore.Images.ImageColumns._ID + " DESC";
		Cursor myCursor = act.managedQuery(
				MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
				largeFileProjection, null, null, largeFileSort);
		String largeImagePath = "";
		try {
			myCursor.moveToFirst();

			// This will actually give yo uthe file path location of the
			// image.
			largeImagePath = myCursor
					.getString(myCursor
							.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
		} finally {
			// do something
			myCursor.close();
		}
		return largeImagePath;
	}

}

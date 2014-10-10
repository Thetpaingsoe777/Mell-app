package com.xavey.app.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

// referenced from downloaded project named by "ImportExportToCSV"
public class CSVExportManager {

	Context context;
	protected static final File XAVEY_DIRECTORY = new File(
			Environment.getExternalStorageDirectory(), "Xavey CSV");

	protected static final File DATABASE_DIRECTORY = new File(
			Environment.getExternalStorageDirectory(), "ImportExport");

	public CSVExportManager(Context context) {
		this.context = context;
	}

	public Boolean exportDocumentsToCSV(String outFileName,
			ArrayList<String> headerList,
			ArrayList<HashMap<String, String>> dataList) {
		Boolean returnCode = false;
		String csvHeader = "";
		String csvValues = "";
		outFileName += ".csv";
		try {
			Log.e("export fun:file name : ", outFileName);

			if (!XAVEY_DIRECTORY.exists()) {
				XAVEY_DIRECTORY.mkdirs();
			}

			File outFile = new File(XAVEY_DIRECTORY, outFileName);
			FileWriter fileWriter = new FileWriter(outFile);
			BufferedWriter out = new BufferedWriter(fileWriter);
			for (String header : headerList) {
				csvHeader += "\"" + header + "\",";
			}
			csvHeader += "\n";
			out.write(csvHeader);
			/* data list
				{Name=zinwinhtet___!}, 
				{DOB=2014-11-12  10:52}, 
				{Age=24}, 
				{Your Hobby=["gaming","football"]}, 
				{Name=htet htar oo}, 
				{DOB=2016-7-19  10:57}, 
				{Age=25}, 
				{Your Hobby=["gaming"]}
			 */
			for (int i = 0; i < dataList.size(); i++) {
				HashMap<String, String> data = dataList.get(i);
				int count = headerList.size();
				for(int j=0; j<headerList.size(); j++){
					String key = headerList.get(j);
					String value = data.get(key);
					csvValues += value + ", ";
				}
				csvValues += "\n";
				out.write(csvValues);
				/*HashMap<String, String> row = dataList.get(i);
				for (int j = 0; j < row.size(); j++) {
					String key = headerList.get(j);
					String value = row.get(key);
					csvValues += value + ", ";
				}
				csvValues += "\n";
				out.write(csvValues);*/
			}

			out.close();
			returnCode = true;
		} catch (Exception e) {
			returnCode = false;
			Log.e("Exception", e.getMessage());
		}
		return returnCode;
	}

	private String getDownloadPath() {
		String download_folder_path = Environment.getExternalStorageDirectory()
				+ "/Downloads/";
		if (!isSDCardMounted()) {
			// String xaveyCSVExportedPath =
			// Environment.getDataDirectory()+"/XaveyCSVExported/";
			/*
			 * File file = new File(xaveyCSVExportedPath); if(!file.exists()){
			 * file.mkdir(); }
			 */
			File downloadFolder = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			return downloadFolder.getPath();
		} else {
			File file = new File(download_folder_path);
			if (!file.exists())
				file.mkdir();
		}
		return download_folder_path;
	}

	private boolean isSDCardMounted() {
		Boolean isSDPresent = android.os.Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED);
		if (isSDPresent)
			return true;
		else
			return false;
	}
}

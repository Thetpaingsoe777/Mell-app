package com.xavey.android.util;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONHelper {
	
	public JSONObject StringToDataSet(String val) throws JSONException{
		JSONObject jsonO = new JSONObject();
		jsonO.put("label", val);
		jsonO.put("value", val);
		jsonO.put("extra", false);
		jsonO.put("image", "");
		jsonO.put("field_skip", "");
		return jsonO;
	}
	
	public JSONArray StringToDataSet(String[] StringData) throws JSONException {
		JSONArray thisArray = new JSONArray();
		for (int i = 0; i < StringData.length; i++) {
			thisArray.put(StringToDataSet(StringData[i]));
		}
		return thisArray;
	}

	public JSONArray AppendStringToDataSet(JSONArray ParentArray,
			String[] StringData) throws JSONException {

		String[] tempCompare = new String[ParentArray.length()+StringData.length];
		JSONArray returnArray = new JSONArray();
		for(int x=0; x<ParentArray.length();x++){
			JSONObject jsonO = ParentArray.getJSONObject(x);
			tempCompare[x]=jsonO.getString("label").toLowerCase();
			returnArray.put(jsonO);
		}
		
		for (int j = 0; j < StringData.length; j++) {
			String newLabel = StringData[j];
			if(newLabel!=null&&newLabel.length()>0){
				if(Arrays.asList(tempCompare).indexOf(newLabel.toLowerCase())<=-1){
				returnArray.put(StringToDataSet(newLabel));
				tempCompare[ParentArray.length()+j]=newLabel.toLowerCase();
				}
			}
		}
		return returnArray;
	}
}

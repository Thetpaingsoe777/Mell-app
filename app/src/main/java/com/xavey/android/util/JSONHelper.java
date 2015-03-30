package com.xavey.android.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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

	public JSONArray StringToJSONDataSet(String[] StringData) throws Exception {
		JSONArray thisArray = new JSONArray();
		for (int i = 0; i < StringData.length; i++) {
			thisArray.put(StringToDataSet(StringData[i]));
		}
		return thisArray;
	}
	
	public JSONArray StringToJSONDataSet(ArrayList<String> StringData) throws Exception {
		JSONArray thisArray = new JSONArray();
		for (int i = 0; i < StringData.size(); i++) {
			thisArray.put(StringToDataSet(StringData.get(i)));
		}
		return thisArray;
	}

	public ArrayList<HashMap<String, String>> StringToMapDataSet(String[] StringData) throws Exception {
		ArrayList<HashMap<String, String>> values_list = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < StringData.length; i++) {
			HashMap<String,String> thisArray = new HashMap<String,String>();
			thisArray.put("max_range", "");
			thisArray.put("field_skip", "");
			thisArray.put("value", StringData[i]);
			thisArray.put("label", StringData[i]);
			thisArray.put("extra", "");
			thisArray.put("error_message", "");
			values_list.add(thisArray);
		}
		return values_list;
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
	
	public JSONArray PrependStringToDataSet(JSONArray ParentArray,
			String[] StringData) throws JSONException {

		String[] tempCompare = new String[ParentArray.length()+StringData.length];
		JSONArray returnArray = new JSONArray();
		for(int x=0; x<ParentArray.length();x++){
			JSONObject jsonO = ParentArray.getJSONObject(x);
			tempCompare[x]=jsonO.getString("label").toLowerCase();
			//returnArray.put(jsonO);
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
		for(int x=0; x<ParentArray.length();x++){
			JSONObject jsonO = ParentArray.getJSONObject(x);
			returnArray.put(jsonO);
		}
		
		return returnArray;
	}
	
	public JSONArray generateMatrixValues(ArrayList<HashMap<String, String>> hValueList, ArrayList<HashMap<String, String>> vValueList) throws JSONException{

		hValueList.size();
		vValueList.size();
		
		JSONArray matrix_values = new JSONArray();
		
		for(int h=0; h<hValueList.size(); h++){ // outter h loop
			HashMap<String, String> current_h = hValueList.get(h);
			String current_h_value = current_h.get("value");
			//String current_h_label = current_h.get("label");
			
			for(int v=0; v<vValueList.size(); v++){ // inner v loop
				
				HashMap<String, String> current_v = vValueList.get(v);
				String current_v_value = current_v.get("value");
				//String current_v_label = current_v.get("label");
				
				JSONObject obj = new JSONObject();
				obj.put("index", h+","+v);
				obj.put("value", current_h_value+"_"+current_v_value);
				obj.put("field_skip", "");
				obj.put("extra", false);
				matrix_values.put(obj);
			}
		}
		
		return matrix_values;
	}
}
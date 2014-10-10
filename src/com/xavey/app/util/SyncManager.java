package com.xavey.app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import com.xavey.app.ApplicationValues;
import com.xavey.app.db.XaveyDBHelper;
import com.xavey.app.model.Document;
import com.xavey.app.model.Form;
import com.xavey.app.model.Image;
import com.xavey.app.model.RequestMethod;
import com.xavey.app.model.RestClient;
import com.xavey.app.model.User;

public class SyncManager {

	XaveyDBHelper dbHelper;
	Activity activity_;
	Context context;
	JSONReader jsonReader;
	Document documentToSubmit;
	Form formToSubmit;
	// Form formToSubmit;

	JSONArray documentArrayToSubmit;
	HashMap<String, Object> incompleteMap = new HashMap<String, Object>();

	// http://stackoverflow.com/questions/9191288/creating-a-unique-timestamp-in-java
//	private static final AtomicLong UNIQUE_TIMESTAMP = new AtomicLong();

	public SyncManager(Activity activity) {
		this.context = activity.getApplicationContext();
		this.activity_ = activity;
		dbHelper = new XaveyDBHelper(context);
		jsonReader = new JSONReader(activity_);
	}

	// getJSONArrayToSubmit(Document document, Form form)
	// return JSONArray

	// document json pal a loat loat tar...
/*	public JSONArray getJSONArrayToSubmit(Document document, Form form)
			throws JSONException {

		SessionManager session = new SessionManager(activity_);
		HashMap<String, String> userInfo = session.getUserDetails();
		String worker_id = userInfo.get(SessionManager.USER_ID);
		User loginUser = dbHelper.getUserByUserID(worker_id);
		// String timestamp = Calendar.getInstance().get(Calendar.MILLISECOND)
		// + "";

		String timestamp = uniqueCurrentTimeMS() + "";

		// ဒီအောက်ကအတိုင်းလုပ်လို့ရရင် ကောင်းတယ်.. document.getID() ကအလု်ဖြစ်ရင်
		// လုပ်လို့ရပြီ....
		// So the form parameter won't be needed anymore
		// form = dbHelper.getFormByFormID(document.getId());

		JSONObject document_json = new JSONObject();
		// (1) worker child node
		JSONObject workerChildNode = new JSONObject();
		workerChildNode.put("id", loginUser.getUser_id());
		workerChildNode.put("name", loginUser.getUser_name());
		// -------------------------------------------------------------
		// (2) form child node
		JSONObject formChildNode = new JSONObject();
		formChildNode.put("id", form.getForm_id());
		formChildNode.put("title", form.getForm_title());

		// (3) org child node
		JSONObject orgChildNode = new JSONObject();
		orgChildNode.put("id", form.getOrg_auto_id());
		orgChildNode.put("org_name", form.getOrg_name());
		// (4) data child node
		String document_JSON = document.getDocument_json();
		ArrayList<HashMap<String, String>> fieldList = jsonReader
				.getDocumentFields(document_JSON, "document_json");
		ArrayList<HashMap<String, String>> fieldList = jsonReader
				.getDocumentFields(document_JSON, "data");
		JSONArray dataArray = new JSONArray();
		for (int i = 0; i < fieldList.size(); i++) {
			HashMap<String, String> map = fieldList.get(i);
			JSONObject fieldNode = new JSONObject();
			fieldNode.put("field_label", map.get("field_label"));
			fieldNode.put("field_name", map.get("field_name"));
			fieldNode.put("field_value", map.get("field_value"));
			dataArray.put(fieldNode);
		}

		document_json.put("data", dataArray);
		document_json.put("timestamp", timestamp);
		document_json.put("org", orgChildNode);
		document_json.put("form", formChildNode);
		document_json.put("worker", workerChildNode);

		JSONArray mainArray = new JSONArray();
		mainArray.put(document_json);
		return mainArray;
	}*/

	public void submitDocument(Document document, Form form)
			throws JSONException {

		JSONArray mainArray = jsonReader.getJSONArrayToSubmit(document, form);
		// following two lines should be actually written only after submit success
		document.setDocument_json(mainArray.getJSONObject(0).toString());
		document.setSubmitted("1");

		setDocumentToSubmit(document);
		setFormToSubmit(form);

		DocumentSubmitTask jSubmit = new DocumentSubmitTask();
		jSubmit.execute(mainArray);
		// ဒီအောက်ကလိုင်းက post execute ထဲမှာသွားလုပ်သင့်တယ်...
		// dbHelper.updateDocumentSubimitted(document, "1");
		// post execute ထဲမှာလုပ်လို့ရအောင် အပေါ်မှာ setDocumentToSubmit
		// လုပ်ထားတယ်...
		// ဟိုထဲရောက်ရင် getDocumentToSubmit ပြန်ခေါ်ယုံပဲ...
	}

	private class DocumentSubmitTask extends AsyncTask<JSONArray, Void, String> {
		private ProgressDialog Dialog = new ProgressDialog(context);
		XaveyProperties xavey_properties;
		String documentUploadURL = "";
		
		protected void onPreExecute() {
			// Dialog.setMessage("Submitting..");
			// Dialog.show();
			xavey_properties = new XaveyProperties();
			documentUploadURL = xavey_properties.getServerDocumentUploadURL();
		}

		@Override
		protected String doInBackground(JSONArray... params) {
			JSONArray jsonArray = params[0];
			String response = "";
			RestClient c = new RestClient(documentUploadURL, jsonArray);
			try {
				String token = ApplicationValues.loginUser.getToken();
				c.AddHeader("x-access-token", token);
				c.Execute(RequestMethod.POST);
				Log.i("document submit errorMsg : ", c.getErrorMessage());
				Log.i("document submit responseCode : ", c.getResponseCode()
						+ "");
				response = c.getResponse();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			// Dialog.setMessage("Done..");
			// Dialog.dismiss();
			Document document = getDocumentToSubmit();
			dbHelper.updateDocument(document);
			dbHelper.updateDocumentSubmittedByJSON(document, "1");
			// update document
		}
	}

	public Form getFormToSubmit() {
		return formToSubmit;
	}

	public void setFormToSubmit(Form formToSubmit) {
		this.formToSubmit = formToSubmit;
	}

	public Document getDocumentToSubmit() {
		return documentToSubmit;
	}

	public void setDocumentToSubmit(Document documentToSubmit) {
		this.documentToSubmit = documentToSubmit;
	}

	// this method will replace submitDocument method
	// now writing
	public void submitDocument2(Document document, Form form,
			ArrayList<HashMap<String, String>> imagesToSubmit)
			throws JSONException {
		// getJSONArrayToSubmit(); ကိုခေါ်ပြီးတဲ့အချိန်မှာ documentArrayToSubmit
		// ထဲမှာ value ရောက်နေပြီးဖြစ်တယ်...
		documentArrayToSubmit = jsonReader.getJSONArrayToSubmit(document, form);

		setDocumentToSubmit(document);
		setFormToSubmit(form);

		ImageAsyncTask imgAsyncTask = new ImageAsyncTask();
		imgAsyncTask.execute(imagesToSubmit);

	}

	private class ImageAsyncTask
			extends
			AsyncTask<ArrayList<HashMap<String, String>>, ArrayList<HashMap<String, String>>, ArrayList<HashMap<String, String>>> {

		private ProgressDialog Dialog = new ProgressDialog(activity_);
		private ArrayList<HashMap<String, String>> uploadedImages = new ArrayList<HashMap<String, String>>();
		ArrayList<Image> imagesToStoreLocally = new ArrayList<Image>();

		XaveyProperties xavey_properties;
		String imageUploadURL="";
		
		protected void onPreExecute() {
/*			 Dialog.setMessage("Uploading ..");
			 Dialog.show();*/
			xavey_properties = new XaveyProperties();
			imageUploadURL = xavey_properties.getServerImageUploadURL();
		}

		
		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				ArrayList<HashMap<String, String>>... params) {
			
			// update token first here
			String userName = ApplicationValues.loginUser.getUser_name();
			String password = ApplicationValues.loginUser.getPwd();
			updateToken(userName,password, getDeviceUniqueID(activity_));

			if (params[0].size()>0) {
				// upload image and collect ids
				// replace in documentArrayToSubmit
				HashMap<String, String> hashMap = new HashMap<String, String>();
				ArrayList<HashMap<String, String>> imagesToSubmit_ = params[0];
				for (HashMap<String, String> map : imagesToSubmit_) {
					String image_field_name = map.get("field_name");
					String image_path = map.get("imagePath");
					RestClient c = new RestClient(imageUploadURL, image_path);
					c.AddHeader("x-access-token", ApplicationValues.loginUser.getToken());
					try {
						c.Execute(RequestMethod.POST);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String response = c.getResponse();
					String image_id = "";
					String serverError = "";
					try {
						JSONObject jsonResponse = new JSONObject(response);
						image_id = jsonResponse.getString("id");
						serverError = jsonResponse.getString("error");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					hashMap.put(image_field_name, image_id);
					Image image = new Image();
					image.setDoc_id(documentToSubmit.getDocument_id());
					image.setImage_id(image_id);
					image.setImage_name(image_field_name);
					image.setImage_path(image_path);
					image.setServerError(serverError);
					imagesToStoreLocally.add(image);
				}
				uploadedImages.add(hashMap);
			}
			return uploadedImages;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			super.onPostExecute(result);

			JSONObject finalObject = new JSONObject();
			JSONArray completeDataArray = new JSONArray();
			try {
				JSONObject jsonObj = documentArrayToSubmit.getJSONObject(0);
				JSONArray incompleteDataArray = jsonObj.getJSONArray("data");

				for (int i = 0; i < incompleteDataArray.length(); i++) {
					JSONObject child = incompleteDataArray.getJSONObject(i);
					String field_name = child.getString("field_name");
					String field_label = child.getString("field_label");
					String field_value = child.getString("field_value");
					if (field_value.equals("unavailable") || field_value.endsWith(".jpeg") || field_value.endsWith(".jpg")) {
						for (int j = 0; j < result.size(); j++) {
							HashMap<String, String> resultMap = result.get(j);
							JSONObject updatedObject = new JSONObject();
							if (resultMap.containsKey(field_name)) {
								String serverID = resultMap.get(field_name);
								updatedObject.put("field_name", field_name);
								updatedObject.put("field_label", field_label);
								updatedObject.put("field_value", serverID);
								completeDataArray.put(updatedObject);
							}
						}
					} else
						completeDataArray.put(child);
				}
				// (1) replace documentArrayToSubmit
				finalObject = jsonObj;
				finalObject.remove("data");
				finalObject.put("data", completeDataArray);
				JSONArray finalArrayToSync = new JSONArray();
				finalArrayToSync.put(finalObject);
				// (2) submit documentArrayToSubmit
				DocumentSubmitTask documentSubmitTask = new DocumentSubmitTask();
				documentSubmitTask.execute(finalArrayToSync);
				// (3) update local document
				String old_document_json = documentToSubmit.getDocument_json();
				//documentToSubmit.setDocument_json(finalObject.toString());
				documentToSubmit.setDocument_json_to_submit(finalObject.toString());
				documentToSubmit.setSubmitted("1");
				dbHelper.updateDocument(documentToSubmit);
				dbHelper.updateDocumentSubmitted(documentToSubmit, "1");
				for (Image image : imagesToStoreLocally) {
					if(!dbHelper.isImageAlreadyExistInDB(image.getImage_path()))
						dbHelper.addNewImage(image);
					else
						dbHelper.updateImageByPath(image);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String getDeviceUniqueID(Activity activity){
		String device_unique_id = Secure.getString(activity.getContentResolver(),
			    Secure.ANDROID_ID);
		return device_unique_id;
	}

/*	private long uniqueCurrentTimeMS() {
		long now = System.currentTimeMillis();
		while (true) {
			long lastTime = UNIQUE_TIMESTAMP.get();
			if (lastTime >= now)
				now = lastTime + 1;
			if (UNIQUE_TIMESTAMP.compareAndSet(lastTime, now))
				return now;
		}
	}*/

	@SuppressWarnings("unchecked")
	public void updateToken(String userName, String password, String deviceID){
		HashMap<String, String> authenMap = new HashMap<String, String>();
		authenMap.put("username", userName);
		authenMap.put("password", password);
		authenMap.put("device", deviceID);
		RetrieveAndUpdateTokenTask tokenTask = new  RetrieveAndUpdateTokenTask(activity_);
		tokenTask.execute(authenMap);
	}

	public class RetrieveAndUpdateTokenTask extends
			AsyncTask<HashMap<String, String>, Void, HashMap<String, String>> {

		// properties
		Properties properties;

//		private ProgressDialog Dialog;

		public RetrieveAndUpdateTokenTask(Activity activity) {
			activity_ = activity;
//			Dialog = new ProgressDialog(activity_);
		}

		@Override
		protected void onPreExecute() {
//			Dialog = new ProgressDialog(activity_);
//			Dialog.setMessage("starting authentication..");
//			Dialog.show();
			String info = "starting authentication";
			Log.i("updateTokenThread", info);
		}
		
		@Override
		protected HashMap<String, String> doInBackground(
				HashMap<String, String>... params) {

//			Dialog.setMessage("authentication in progress..");
//			Dialog.show();

			HashMap<String, String> result = new HashMap<String, String>();
			String userName = params[0].get("username");
			String password = params[0].get("password");
			String deviceID = params[0].get("device");
			
			XaveyProperties xavey_properties = new XaveyProperties();
			String authenticateURL = xavey_properties.getAuthenticateURL();
			
			try {
				//RestClient c = new RestClient(authenticate);
				RestClient c = new RestClient(authenticateURL);
				c.AddParam("username", userName);
				c.AddParam("password", password);
				c.AddParam("device", deviceID);
				c.Execute(RequestMethod.POST);
				int responseCode = c.getResponseCode();
				String response = c.getResponse();
				JSONObject jsResponse = new JSONObject(response);
				String user_id = jsResponse.getString("user_id");
				String token = jsResponse.getString("token");
				result.put("user_id", user_id);
				result.put("response_code", responseCode+"");
				result.put("token", token);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return result;
		}
		
		@Override
		protected void onPostExecute(HashMap<String, String> result) {
//			Dialog.setMessage("authentication done..");
//			Dialog.show();
			String user_id = result.get("user_id");
			int responseCode = Integer.parseInt(result.get("response_code"));
			String token = result.get("token");
			if(responseCode==200){
//				Dialog.setMessage("Token is now updated..");
//				Dialog.show();
				//Toast.makeText(activity_, "token is now updated", 1000).show();
				
				dbHelper.updateTokenByUserID(user_id, token);
			}else{
//				Dialog.setMessage("Authentication failed..! responseCode : "+responseCode);
//				Dialog.show();
			}
		}

	}
	
	private class AuthenticateTask extends AsyncTask<User, Void, HashMap<String, String>> {
		// (1) do in background parameter
		// (2) progress
		// (3) post execute parameter
		
		User user = new User();
		
		private ProgressDialog Dialog = new ProgressDialog(activity_);
		// properties
		XaveyProperties xavey_properties;
		String authenticateURL = "";

		// String serverLoginURL = "serverLoginURL";
		// String serverFormURL = "serverFormURL";

		protected void onPreExecute() {
			Dialog.setMessage("Please wait..");
			Dialog.show();
			HashMap<String, Object> userAndTask = new HashMap<String, Object>();
			xavey_properties = new XaveyProperties();
			authenticateURL = xavey_properties.getAuthenticateURL();
		}

		protected HashMap<String, String> doInBackground(User... params) {

			// properties stuffs

			// here is current works
			HashMap<String, String> result = null;
			try {
				//RestClient c = new RestClient(serverLoginURLToken);
				RestClient c = new RestClient(authenticateURL);
				/*
				 * c.AddHeader("content type", "application/json");
				 */
				String deviceID = new SyncManager(activity_).getDeviceUniqueID(activity_);
				c.AddParam("username", params[0].getUser_name());
				c.AddParam("password", params[0].getPwd());
				c.AddParam("device", deviceID);
				c.Execute(RequestMethod.POST);
				int userResponseCode = c.getResponseCode();
				String s = c.getResponse();
				result = parseJSON(c.getResponse());
				result.put("password", params[0].getPwd());
				result.put("response_code", userResponseCode+"");
			}
			catch(Exception e){
				Log.e("login server error :", e.getMessage());
			}
			return result;
		}

		private boolean isNumberInvolved(String column, String number_to_check){
			String[] numberArray = column.split(",");
			for(int i=0; i<numberArray.length; i++){
				if(numberArray[i].equals(number_to_check))
					return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			Dialog.dismiss();
			
			int responseCode = Integer.parseInt(result.get("response_code").toString());
			String userID = result.get("user_id");
			String username = result.get("username").toString();
			String password = result.get("password").toString();
			String hashPassword = result.get("hashPassword").toString();
			String email = result.get("email").toString();
			String role = result.get("role").toString();
			String organization = result.get("organization").toString();
			String token = result.get("token").toString();
			 
			if(responseCode==200){
				SessionManager session = new SessionManager(activity_.getApplicationContext());
				session.createLoginSession(userID,
						username, hashPassword);

				User loggedInUser = new User();
				loggedInUser.setUser_id(userID);
				loggedInUser.setUser_name(username);
				loggedInUser.setPwd(password);
				loggedInUser.setHashPwd(hashPassword);
				loggedInUser.setEmail(email);
				loggedInUser.setRole(role);
				loggedInUser.setOrganization(organization);
				loggedInUser.setToken(token);
				
				checkUserAndStore(loggedInUser);
				ApplicationValues.loginUser = loggedInUser;
			}
			else if(responseCode==401){
				Toast.makeText(activity_, "Unauthroized..!", 1000).show();
			}
			else{
				Toast.makeText(activity_, "Server Error..!", 1000).show();
			}

		}
		


		private void checkUserAndStore(User result) {
			// check user
			if (!dbHelper.isUserAlreadyExistInDB(result.getUser_id())) {
				Dialog.setMessage("Saving User");
				/*User user = new User();
				user.setUser_id(result.getUser_id());
				user.setUser_name(result.getUser_name());
				user.setHashPwd(result.getHashPwd());
				user.setEmail(result.getEmail());
				user.setRole(result.getRole());
				user.setOrganization(result.getOrganization());*/
				dbHelper.addNewUser(result);
			} else {
				Toast.makeText(activity_.getApplicationContext(), "AlreadyExist", 500)
						.show();
				dbHelper.updateUser(result);
			}
		}

		private User parseJSONLogin(String response) {

			User user = new User();
			if (response != null) {

				Log.i("JSON", response.toString());
				JSONObject jsResponse;
				try {
					jsResponse = new JSONObject(response);
					user.setUser_id(jsResponse.getString("user_id"));
					user.setUser_name(jsResponse.getString("username"));
					user.setHashPwd(jsResponse.getString("password"));
					user.setEmail(jsResponse.getString("email"));
					user.setRole(jsResponse.getString("role"));
					user.setOrganization(jsResponse.getString("organization"));
/*					user.setCode("200");
					user.setMessage("Authorize");*/
					user.setToken(jsResponse.getString("token"));

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
/*				user.setCode("401");
				user.setMessage("Unathorize");*/
			}
			return user;
		}

		private HashMap<String, String> parseJSON(String response) {
			HashMap<String, String> responseMap = new HashMap<String, String>();
			if (response != null) {
				Log.i("JSON", response.toString());
				JSONObject jsResponse;
				try {
					jsResponse = new JSONObject(response);
					responseMap.put("user_id", jsResponse.getString("user_id"));
					responseMap.put("username", jsResponse.getString("username"));
					responseMap.put("hashPassword", jsResponse.getString("password"));
					responseMap.put("email", jsResponse.getString("email"));
					responseMap.put("role", jsResponse.getString("role"));
					//responseMap.put("organization", jsResponse.getString("organization"));
					responseMap.put("organization", "null org");
					responseMap.put("response_code", "200");
					//responseMap.put("organization", jsResponse.getString("organization"));
					
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

}

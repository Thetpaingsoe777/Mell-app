package com.xavey.android.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.xavey.android.ApplicationValues;
import com.xavey.android.MainActivity;
import com.xavey.android.db.XaveyDBHelper;
import com.xavey.android.model.Document;
import com.xavey.android.model.Form;
import com.xavey.android.model.RequestMethod;
import com.xavey.android.model.RestClient;
import com.xavey.android.model.User;
import com.xavey.android.model.XMedia;

public class SyncManager {

	XaveyDBHelper dbHelper;
	Activity activity_;
	Context context;
	JSONReader jsonReader;
	Document documentToSubmit;
	Form formToSubmit;
	// Form formToSubmit;
	
	ToastManager toast = null;

	JSONArray documentArrayToSubmit;
	HashMap<String, Object> incompleteMap = new HashMap<String, Object>();

	// http://stackoverflow.com/questions/9191288/creating-a-unique-timestamp-in-java
//	private static final AtomicLong UNIQUE_TIMESTAMP = new AtomicLong();

	public SyncManager(Activity activity) {
		this.context = activity.getApplicationContext();
		this.activity_ = activity;
		dbHelper = new XaveyDBHelper(context);
		jsonReader = new JSONReader(activity_);
		toast = new ToastManager(activity);
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
		document.setDocument_json_to_submit(mainArray.getJSONObject(0).toString());
//		document.setSubmitted("1");

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

	private class DocumentSubmitTask extends AsyncTask<JSONArray, Void, RestClient> {
		private ProgressDialog Dialog = new ProgressDialog(context.getApplicationContext());
		XaveyProperties xavey_properties;
		String documentUploadURL = "";

		protected void onPreExecute() {
//			 Dialog.setMessage("Submitting document ...");
//			 Dialog.show();
			xavey_properties = new XaveyProperties();
			documentUploadURL = xavey_properties.getServerDocumentUploadURL();
		}

		@Override
		protected RestClient doInBackground(JSONArray... params) {

			// ---------- updating token.. --------------------------------
			String userName = ApplicationValues.loginUser.getUser_name();
			String password = ApplicationValues.loginUser.getPwd();
			String deviceID = getDeviceUniqueID(activity_);
			//updateToken(userName,password);

			String authenticateURL = xavey_properties.getAuthenticateURL();

			RestClient c_ = new RestClient(authenticateURL);
			c_.AddParam("username", userName);
			c_.AddParam("password", password);
			c_.AddParam("device", deviceID);
			try {
				c_.Execute(RequestMethod.POST);
			} catch (Exception e2) {
				
			}
			int userResponseCode = c_.getResponseCode();
			// -----------------------------------------------------------------
			
			JSONArray jsonArray = params[0];
            try {
                JSONObject tempObj = jsonArray.getJSONObject(0);
                tempObj.accumulate("sync_datetime", getCurrentDateTime());
                jsonArray = new JSONArray();
                jsonArray.put(tempObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RestClient c = new RestClient(documentUploadURL, jsonArray);
			
			if(userResponseCode==200){

				c.setMainActivity(ApplicationValues.mainActivity);
				try {
					String token = ApplicationValues.loginUser.getToken();
					c.AddHeader("x-access-token", token);
					c.Execute(RequestMethod.POST);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			else{
				toast.xaveyToast(null, "token updating failed in DocumentSubmitTask");
			}
			return c;
		}

		@Override
		protected void onPostExecute(RestClient rc) {
			if(rc.getResponseCode()==200){
				String result = rc.getResponse();
				Document document = getDocumentToSubmit();
				if(result.length()>0){
					try {
			            JSONArray jsonDoc = (new JSONArray(result));
			            String _id = jsonDoc.getJSONObject(0).getString("_id"); //the id generated from mongo insert()
			            if(_id.length()>0){
			            	dbHelper.updateDocument(document);
							dbHelper.updateDocumentSubmittedByJSON(document, "1");
							// :TODO 
							//MainActivity.selectItem(MainActivity.current_position);
			            }
			        } catch (JSONException e) {
			            e.printStackTrace();
			        }
				}else{
					dbHelper.updateDocumentSubmittedByJSON(document, "0");
				}
			}
			else if(rc.getResponseCode()==500){
				toast.xaveyToast(null, rc.getErrorMessage());
			}
			else{
				toast.xaveyToast(null, "Error document submiting onPostExecute of DocumentSubmitTask");
			}
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
			ArrayList<HashMap<String, String>> mediaToSubmit)
			throws JSONException {
		// getJSONArrayToSubmit(); ကိုခေါ်ပြီးတဲ့အချိန်မှာ documentArrayToSubmit
		// ထဲမှာ value ရောက်နေပြီးဖြစ်တယ်...
		documentArrayToSubmit = jsonReader.getJSONArrayToSubmit(document, form);
		document.setDocument_json_to_submit(documentArrayToSubmit.toString());

		setDocumentToSubmit(document);
		setFormToSubmit(form);

		MediaAsyncTask imgAsyncTask = new MediaAsyncTask();
		imgAsyncTask.execute(mediaToSubmit);

	}

    private String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

	private class MediaAsyncTask
			extends
			AsyncTask<ArrayList<HashMap<String, String>>, ArrayList<HashMap<String, String>>, ArrayList<HashMap<String, String>>> {

		private ProgressDialog Dialog = new ProgressDialog(activity_);
		private ArrayList<HashMap<String, String>> uploadedImages = new ArrayList<HashMap<String, String>>();
		ArrayList<XMedia> mediaToStoreLocally = new ArrayList<XMedia>();

		XaveyProperties xavey_properties;
		String mediaUploadURL="";
		
		protected void onPreExecute() {
/*			 Dialog.setMessage("Uploading ..");
			 Dialog.show();*/
			xavey_properties = new XaveyProperties();
			mediaUploadURL = xavey_properties.getServerImageUploadURL();
		}

		
		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				ArrayList<HashMap<String, String>>... params) {
			
			// update token first here
			String userName = ApplicationValues.loginUser.getUser_name();
			String password = ApplicationValues.loginUser.getPwd();
			String deviceID = getDeviceUniqueID(activity_);
			//updateToken(userName,password);
			
			String authenticateURL = xavey_properties.getAuthenticateURL();
			
			RestClient c_ = new RestClient(authenticateURL);
			c_.AddParam("username", userName);
			c_.AddParam("password", password);
			c_.AddParam("device", deviceID);
			try {
				c_.Execute(RequestMethod.POST);
			} catch (Exception e2) {
                e2.printStackTrace();
			}
			int userResponseCode = c_.getResponseCode();
			if(userResponseCode==200){
				if (params[0].size()>0) {
					// upload image and collect ids
					// replace in documentArrayToSubmit
					HashMap<String, String> hashMap = new HashMap<String, String>();
					ArrayList<HashMap<String, String>> imagesToSubmit_ = params[0];
					for (HashMap<String, String> map : imagesToSubmit_) {
						String image_field_name = map.get("field_name");
						String media_path = map.get("media_path");
						RestClient c = new RestClient(mediaUploadURL, media_path);
						c.AddHeader("x-access-token", ApplicationValues.loginUser.getToken());
						try {
							c.Execute(RequestMethod.POST);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							//toast.xaveyToast(null, "can't reach to server");
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
						XMedia media = new XMedia();
						media.setDoc_id(documentToSubmit.getDocument_id());
						media.setMedia_id(image_id);
						media.setMedia_name(image_field_name);
						media.setMedia_path(media_path);
						media.setMedia_type(isAudio(media_path)?"audio":(isImage(media_path)?"image":"unknown"));
						media.setServerError(serverError);
						mediaToStoreLocally.add(media);
					}
					uploadedImages.add(hashMap);
				}
			}
			else{
				//toast.xaveyToast(null, "Authenticate occurs error before downloading images.");
                Log.d("auth",String.valueOf(userResponseCode));
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
                    String field_id = child.getString("field_id");
					String field_name = child.getString("field_name");
					String field_label = child.getString("field_label");
					String field_value = child.getString("field_value");
                    String field_audio = child.has("field_audio")==true ? child.getString("field_audio") : "";
					boolean isAudio = false;
					boolean isImage = false;
					//TODO: add other audio extension;
					isAudio = isAudio(field_audio);
					isImage = isImage(field_value);
					//:TODO include PNG extension
					if (isAudio||isImage) {
						for (int j = 0; j < result.size(); j++) {
							HashMap<String, String> resultMap = result.get(j);
							JSONObject updatedObject = new JSONObject();
							if (resultMap.containsKey(field_name)) {
								String serverID = resultMap.get(field_name);
                                updatedObject.put("field_id", field_id);
                                updatedObject.put("field_name", field_name);
								updatedObject.put("field_label", field_label);
								if(isAudio) {
                                    updatedObject.put("field_audio", serverID);
                                    updatedObject.put("field_value", field_value);
                                }
                                else if(isImage) {
                                    updatedObject.put("field_value", serverID);
                                }
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
				for (XMedia image : mediaToStoreLocally) {
					if(!dbHelper.isMediaAlreadyExistInDB(image.getMedia_path()))
						dbHelper.addNewMedia(image);
					else{
						dbHelper.updateMediaByPath(image);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getDeviceUniqueID(Activity activity){
        String device_unique_id = Secure.getString(activity.getContentResolver(),  Secure.ANDROID_ID);
		return device_unique_id;
	}

	private boolean isAudio(String media_name){
		return (media_name.endsWith(".m4a") || media_name.endsWith(".mp4") || media_name.endsWith(".mp3"));
	}

	private boolean isImage(String media_name){
		return (media_name.endsWith(".jpeg") || media_name.endsWith(".jpg") || media_name.endsWith(".png"));
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
	public void updateToken(String userName, String password){
		String deviceID = getDeviceUniqueID(activity_);
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
		//ToastManager xToast;

//		private ProgressDialog Dialog;

		public RetrieveAndUpdateTokenTask(Activity activity) {
			activity_ = activity;
			//xToast = new ToastManager(activity_);
//			Dialog = new ProgressDialog(activity_);
		}

		@Override
		protected void onPreExecute() {
//			Dialog = new ProgressDialog(activity_);
//			Dialog.setMessage("starting authentication..");
//			Dialog.show();
			String info = "starting authentication";
			Log.i("updateTokenThread", info);
			//xToast.xaveyToast(null, info);
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
			String responseCode_ = result.get("response_code");

			if(isNumeric(responseCode_)){
				int responseCode = Integer.parseInt(result.get("response_code"));
				String token = result.get("token");
				if(responseCode==200){
//					Dialog.setMessage("Token is now updated..");
//					Dialog.show();
					//Toast.makeText(activity_, "token is now updated", 1000).show();

					dbHelper.updateTokenByUserID(user_id, token);
				}else{
					toast.xaveyToast(null, "Authentication failed..!" + "\nResponse Code : "+responseCode);
//					Dialog.setMessage("Authentication failed..! responseCode : "+responseCode);
//					Dialog.show();
				}
			}
			else{
				toast.xaveyToast(null, "Authentication failed..!" + "\nServer is not responding.");
			}
		}

	}

	public static boolean isNumeric(String str) {
	    if (str == null) {
	        return false;
	    }
	    int sz = str.length();
	    for (int i = 0; i < sz; i++) {
	        if (Character.isDigit(str.charAt(i)) == false) {
	            return false;
	        }
	    }
	    return true;
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
				Toast.makeText(activity_, "Unauthroized..!", Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(activity_, "Server Error..!", Toast.LENGTH_LONG).show();
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
				Toast.makeText(activity_.getApplicationContext(), "AlreadyExist", Toast.LENGTH_SHORT)
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

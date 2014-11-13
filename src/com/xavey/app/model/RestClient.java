package com.xavey.app.model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;

import android.app.Activity;

import com.xavey.app.ApplicationValues;
import com.xavey.app.util.ToastManager;


public class RestClient {

	private ArrayList<NameValuePair> params;
	private ArrayList<NameValuePair> headers;
	private Object obj;
	private JSONArray jsonArray;
	private String url;
	private int responseCode;
	private String message;
	private String response;
	private String filePath;
	
	private Activity main_activity;
	

	public String getResponse() {
		return response;
	}

	public String getErrorMessage() {
		return message;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public RestClient(String url) {
		this.url = url;
		this.jsonArray = null;
		this.filePath = null;
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
	}

	public RestClient(String url, Object obj) {
		this.url = url;
		this.obj = obj;
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
	}
	
	public RestClient(String url, String filepath) {
		this.url = url;
		this.filePath = filepath;
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
	}

	public RestClient(String url, JSONArray jsonArray) {
		this.url = url;
		this.jsonArray = jsonArray;
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
	}

	public void AddParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
	}

	public void AddHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}

	public void Execute(RequestMethod method) throws Exception {
		switch (method) {
		case GET: {
			// add parameters
			String combinedParams = "";
			if (!params.isEmpty()) {
				//combinedParams += "?";
				for (NameValuePair p : params) {
					String value = p.getValue();
					// server မှလက်ခံသည့် ပုံစံနှင့်ကိုက်ညီအောင်ပြောင်းလိုက်ပါသည်... ၂၆-၉-၂၀၁၄
//					String paramString = p.getName() + "="
//							+ URLEncoder.encode(value, "UTF-8");
					String paramString = URLEncoder.encode(value,"UTF-8");
					if (combinedParams.length() > 1) {
						combinedParams += "&" + paramString;
					} else {
						combinedParams += paramString;
					}
				}
			}

			HttpGet request = new HttpGet(url + combinedParams);

			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}

			executeRequest(request, url);
			break;
		}
		case POST: {
			if( this.filePath != null){ //Checking image			
				upload();				
			} else {
				HttpPost request = new HttpPost(url);
				if (getJsonArray()!= null) { //Checking JSON			

					String json = getJsonArray().toString();
					StringEntity se = new StringEntity(json,"UTF-8");
					request.setEntity(se);
					request.setHeader("Accept", "application/json");
					request.setHeader("Content-type", "application/json");
					
					for(NameValuePair pair: headers){
						request.setHeader(pair.getName(), pair.getValue());
					}

				} else {
					// add headers
					for (NameValuePair h : headers) {
						String name = h.getName();
						String value = h.getValue();
						request.addHeader(name, value);
					}

					if (!params.isEmpty()) {
						request.setEntity(new UrlEncodedFormEntity(params,
							HTTP.UTF_8));
					}
				}
				executeRequest(request, url);
			}
			
			break;
		}
		case PUT: {
			HttpPut request = new HttpPut(url);

			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}

			if (!params.isEmpty()) {
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			}

			executeRequest(request, url);
			break;
		}
		case DELETE: {
			String combinedParams = "";
			if (!params.isEmpty()) {
				combinedParams += "?";
				for (NameValuePair p : params) {
					String paramString = p.getName() + "="
							+ URLEncoder.encode(p.getValue(), "UTF-8");
					if (combinedParams.length() > 1) {
						combinedParams += "&" + paramString;
					} else {
						combinedParams += paramString;
					}
				}
			}

			HttpDelete request = new HttpDelete(url + combinedParams);

			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}

			HttpClient client = new DefaultHttpClient();
			HttpResponse httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			message = httpResponse.getStatusLine().getReasonPhrase();

			break;
		}
		}
	}
	
	private void upload() {
		
		BufferedReader reader = null;
        HttpURLConnection conn = null;
        DataOutputStream dos = null; 
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String filePath = getFilePath();
        String[] str = filePath.split("/");
		String fileName = str[str.length - 1];
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(filePath);
         
        if (!sourceFile.isFile()) {
            
        	message = "Source file is not found!";
             
        }else {        	
            
			try {                  
                   // open a URL connection to the Servlet
                 FileInputStream fileInputStream = new FileInputStream(sourceFile);
                 URL mURL = new URL(url);
                 
                 
                 // Open a HTTP  connection to  the URL
                 conn = (HttpURLConnection) mURL.openConnection();
                 conn.setDoInput(true); // Allow Inputs
                 conn.setDoOutput(true); // Allow Outputs
                 conn.setUseCaches(false); // Don't use a Cached Copy
                 conn.setRequestMethod("POST");
                 conn.setRequestProperty("Connection", "Keep-Alive");
                 conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                 conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                 
                 conn.setRequestProperty("x-access-token", ApplicationValues.loginUser.getToken());

                 conn.setRequestProperty("uploaded_file", fileName );

                 dos = new DataOutputStream(conn.getOutputStream());
        
                 dos.writeBytes(twoHyphens + boundary + lineEnd);
                 dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                                           + fileName + "\"" + lineEnd);
                 dos.writeBytes("Content-Type:image/jpeg"+lineEnd); 
                 dos.writeBytes(lineEnd);

                 // create a buffer of  maximum size
                 bytesAvailable = fileInputStream.available();
        
                 bufferSize = Math.min(bytesAvailable, maxBufferSize);
                 buffer = new byte[bufferSize];
        
                 // read file and write it into form...
                 bytesRead = fileInputStream.read(buffer, 0, bufferSize); 
                    
                 while (bytesRead > 0) {
                      
                   dos.write(buffer, 0, bufferSize);
                   bytesAvailable = fileInputStream.available();
                   bufferSize = Math.min(bytesAvailable, maxBufferSize);
                   bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
                    
                  }
        
                 // send multipart form data necesssary after file data...
                 dos.writeBytes(lineEnd);
                 dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                 fileInputStream.close();
                 dos.flush();
                 dos.close();

			}catch (MalformedURLException ex) {
				
				 message = "MalformedURLException Exception : check url.";
				 
           }catch (IOException ioe) {
        	   message = ioe.getMessage();
           }
                 
			// Get the server response
			try {
				InputStream inputStream = conn.getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				reader = new BufferedReader(inputStreamReader);
				StringBuilder sb = new StringBuilder();
				String line = null;

				// Read Server Response
				while ((line = reader.readLine()) != null) {
					// Append server response in string
					sb.append(line + "");
				}

				// Append Server Response To Content String
				response = sb.toString();
			} catch (Exception ex) {
				message = ex.getMessage();
			}
				finally {
			
				try {

					reader.close();
				}

				catch (Exception ex) {
					message = ex.getMessage();
				}
			}
		}                  
    }

	private void executeRequest(HttpUriRequest request, String url) {
		HttpClient client = new DefaultHttpClient();
		HttpResponse httpResponse;

		try {
			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			message = httpResponse.getStatusLine().getReasonPhrase();

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				InputStream instream = entity.getContent();
				response = convertStreamToString(instream);

				// Closing the input stream will trigger connection release
				instream.close();
			}

		} catch (ClientProtocolException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		} catch (IOException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
			ToastManager toast = new ToastManager(getMainActivity());
			toast.xaveyToast(null, e.getMessage());
		}
	}

	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public JSONArray getJsonArray() {
		return jsonArray;
	}

	public void setJsonArray(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Activity getMainActivity() {
		return main_activity;
	}

	public void setMainActivity(Activity main_activity) {
		this.main_activity = main_activity;
	}
	
	
}

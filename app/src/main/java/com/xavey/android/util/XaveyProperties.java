package com.xavey.android.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import com.xavey.android.ApplicationValues;
import com.xavey.android.MainActivity;

public class XaveyProperties {
	final String AUTHENTICATE = "authenticateURL";
	final String SERVER_FORM_DOWNLOAD = "serverFormDownload";
	final String SERVER_IMAGE_UPLOAD = "serverImageUpload";
	final String SERVER_DOCUMENT_UPLOAD = "serverDocumentUpload";
	final String ZAWGYI_FONT = "zawgyiFont";
	final String IMAGE_DOWNLOAD = "logoDownloadURL";
	final String CURRENTLY_POINTING_URL = "currentlyPointingURL";
	
	final String AUTHEN_USER_NAME = "authen_user";
	final String AUTHEN_PASSWORD = "authen_pw";
	
	private String authenticateURL;
	private String serverFormDownloadURL;
	private String serverImageUploadURL;
	private String serverDocumentUploadURL;
	private String zawgyi_font;
	private String localFormDownload;
	private String localAuthenticate;
	private String syncImageDownloadURL;
	private String authenUserName;
	private String authenPassword;
	private String currentlyPointingURL;
	
	public XaveyProperties() {
		Properties xaveyProperties = new Properties();
//		InputStream in = MainActivity.class.getResourceAsStream("/src/main/java/com/xavey/android/util/xavey_properties_.properties");
//        InputStream in = MainActivity.class.getResourceAsStream("/src/main/res/xavey_properties_.properties");
//        InputStream in = null;
//        try {
//            in = ApplicationValues.appContext.getAssets().open("/com/xavey/android/util/xavey_properties_.properties");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
/*        try {
			xaveyProperties.load(new InputStreamReader(in, "UTF-8"));
			authenticateURL = xaveyProperties.getProperty(AUTHENTICATE);
			serverFormDownloadURL = xaveyProperties.getProperty(SERVER_FORM_DOWNLOAD);
			serverImageUploadURL = xaveyProperties.getProperty(SERVER_IMAGE_UPLOAD);
			serverDocumentUploadURL = xaveyProperties.getProperty(SERVER_DOCUMENT_UPLOAD);
			zawgyi_font = xaveyProperties.getProperty(ZAWGYI_FONT);
			authenUserName = xaveyProperties.getProperty(AUTHEN_USER_NAME);
			authenPassword = xaveyProperties.getProperty(AUTHEN_PASSWORD);
			setCurrentlyPointingURL(xaveyProperties.getProperty(CURRENTLY_POINTING_URL));
			setSyncImageDownloadURL(xaveyProperties.getProperty(IMAGE_DOWNLOAD));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/

        /*/dev
            authenticateURL = "http://dev.xavey.com:3000/authenticate/";
			serverFormDownloadURL = "http://dev.xavey.com:3000/forms/worker/";
			serverImageUploadURL = "http://dev.xavey.com:3000/collections/upload";
			serverDocumentUploadURL = "http://dev.xavey.com:3000/collections/data";
			zawgyi_font = "on";
			setSyncImageDownloadURL("http://dev.xavey.com:3000/forms/image/");
            setCurrentlyPointingURL("dev.xavey.com");

       */
        authenticateURL = "http://api.xavey.com:3000/authenticate/";
        serverFormDownloadURL = "http://api.xavey.com:3000/forms/worker/";
        serverImageUploadURL = "http://api.xavey.com:3000/collections/upload";
        serverDocumentUploadURL = "http://api.xavey.com:3000/collections/data";
        zawgyi_font = "on";
        setSyncImageDownloadURL("http://api.xavey.com:3000/forms/image/");
        setCurrentlyPointingURL("api.xavey.com");
    }

	public String getAuthenticateURL() {
		return authenticateURL;
	}
	
	public String getServerFormDownloadURL() {
		return serverFormDownloadURL;
	}

	public String getServerImageUploadURL() {
		return serverImageUploadURL;
	}

	public String getServerDocumentUploadURL() {
		return serverDocumentUploadURL;
	}

	public String getZawgyiFontStatus(){
		return zawgyi_font;
	}
	
	public String getLocalFormDownloadURL(){
		return localFormDownload;
	}
	
	public String getLocalAuthenticateURL(){
		return localAuthenticate;
	}

	public String getSyncImageDownloadURL() {
		return syncImageDownloadURL;
	}

	public void setSyncImageDownloadURL(String syncImageDownloadURL) {
		this.syncImageDownloadURL = syncImageDownloadURL;
	}

	public String getAuthenUserName() {
		return authenUserName;
	}

	public void setAuthenUserName(String authenUserName) {
		this.authenUserName = authenUserName;
	}

	public String getAuthenPassword() {
		return authenPassword;
	}

	public void setAuthenPassword(String authenPassword) {
		this.authenPassword = authenPassword;
	}

	public String getCurrentlyPointingURL() {
		return currentlyPointingURL;
	}

	public void setCurrentlyPointingURL(String currentlyPointingURL) {
		this.currentlyPointingURL = currentlyPointingURL;
	}



}

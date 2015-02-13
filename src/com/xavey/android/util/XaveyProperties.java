package com.xavey.android.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import com.xavey.android.MainActivity;

public class XaveyProperties {
	final String AUTHENTICATE = "authenticateURL";
	final String SERVER_FORM_DOWNLOAD = "serverFormDownload";
	final String SERVER_IMAGE_UPLOAD = "serverImageUpload";
	final String SERVER_DOCUMENT_UPLOAD = "serverDocumentUpload";
	final String ZAWGYI_FONT = "zawgyiFont";
	final String LOCAL_FORM_DOWNLOAD = "localFormDownload";
	final String LOCAL_AUTHENTICATE = "localAuthenticate";
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
		InputStream in = MainActivity.class.getResourceAsStream("/com/xavey/android/util/xavey_properties.properties");
		try {
			xaveyProperties.load(new InputStreamReader(in, "UTF-8"));
			authenticateURL = xaveyProperties.getProperty(AUTHENTICATE);
			serverFormDownloadURL = xaveyProperties.getProperty(SERVER_FORM_DOWNLOAD);
			serverImageUploadURL = xaveyProperties.getProperty(SERVER_IMAGE_UPLOAD);
			serverDocumentUploadURL = xaveyProperties.getProperty(SERVER_DOCUMENT_UPLOAD);
			zawgyi_font = xaveyProperties.getProperty(ZAWGYI_FONT);
			localFormDownload = xaveyProperties.getProperty(LOCAL_FORM_DOWNLOAD);
			localAuthenticate = xaveyProperties.getProperty(LOCAL_AUTHENTICATE);
			authenUserName = xaveyProperties.getProperty(AUTHEN_USER_NAME);
			authenPassword = xaveyProperties.getProperty(AUTHEN_PASSWORD);
			setCurrentlyPointingURL(xaveyProperties.getProperty(CURRENTLY_POINTING_URL));
			setSyncImageDownloadURL(xaveyProperties.getProperty(IMAGE_DOWNLOAD));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

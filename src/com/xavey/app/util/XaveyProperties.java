package com.xavey.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import com.xavey.app.MainActivity;

public class XaveyProperties {
	final String AUTHENTICATE = "authenticateURL";
	final String SERVER_FORM_DOWNLOAD = "serverFormDownload";
	final String SERVER_IMAGE_UPLOAD = "serverImageUpload";
	final String SERVER_DOCUMENT_UPLOAD = "serverDocumentUpload";
	
	private String authenticateURL;
	private String serverFormDownloadURL;
	private String serverImageUploadURL;
	private String serverDocumentUploadURL;
	
	public XaveyProperties() {
		Properties xaveyProperties = new Properties();
		InputStream in = MainActivity.class.getResourceAsStream("/com/xavey/app/util/xavey_properties.properties");
		try {
			xaveyProperties.load(new InputStreamReader(in, "UTF-8"));
			authenticateURL = xaveyProperties.getProperty(AUTHENTICATE);
			serverFormDownloadURL = xaveyProperties.getProperty(SERVER_FORM_DOWNLOAD);
			serverImageUploadURL = xaveyProperties.getProperty(SERVER_IMAGE_UPLOAD);
			serverDocumentUploadURL = xaveyProperties.getProperty(SERVER_DOCUMENT_UPLOAD);
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

}

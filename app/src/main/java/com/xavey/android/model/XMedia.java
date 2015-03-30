package com.xavey.android.model;

public class XMedia {
	
	private String id; // auto increment id
	private String media_name; // actually that's field_name
	private String media_path;
	private String media_id; // server's image id
	private String doc_id;
	private String media_type;
	private String media_content;
	private String serverError;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMedia_name() {
		return media_name;
	}
	public void setMedia_name(String media_name) {
		this.media_name = media_name;
	}
	public String getMedia_path() {
		return media_path;
	}
	public void setMedia_path(String media_path) {
		this.media_path = media_path;
	}
	public String getMedia_id() {
		return media_id;
	}
	public void setMedia_id(String media_id) {
		this.media_id = media_id;
	}
	public String getDoc_id() {
		return doc_id;
	}
	public void setDoc_id(String doc_id) {
		this.doc_id = doc_id;
	}
	public String getMedia_type() {
		return media_type;
	}
	public void setMedia_type(String media_type) {
		this.media_type = media_type;
	}
	public String getMedia_content() {
		return media_content;
	}
	public void setMedia_content(String media_content) {
		this.media_content = media_content;
	}
	public String getServerError() {
		return serverError;
	}
	public void setServerError(String serverError) {
		this.serverError = serverError;
	}

}

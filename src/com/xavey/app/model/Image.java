package com.xavey.app.model;

public class Image {
	
	private String id; // auto increment id
	private String image_name; // actually that's field_name
	private String image_path;
	private String image_id; // server's image id
	private String doc_id;
	private String image_content;
	private String serverError;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getImage_name() {
		return image_name;
	}
	public void setImage_name(String image_name) {
		this.image_name = image_name;
	}
	public String getImage_path() {
		return image_path;
	}
	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}
	public String getImage_id() {
		return image_id;
	}
	public void setImage_id(String image_id) {
		this.image_id = image_id;
	}
	public String getDoc_id() {
		return doc_id;
	}
	public void setDoc_id(String doc_id) {
		this.doc_id = doc_id;
	}
	public String getImage_content() {
		return image_content;
	}
	public void setImage_content(String image_content) {
		this.image_content = image_content;
	}
	public String getServerError() {
		return serverError;
	}
	public void setServerError(String serverError) {
		this.serverError = serverError;
	}

}

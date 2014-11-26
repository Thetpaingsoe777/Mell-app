package com.xavey.app.model;

public class Form {
	private String id;
	private String form_id;
	private String form_title; // form title
	private String form_subtitle; // form name
	private String form_desc;
	private String form_version;
	private String form_json; // 6 
	private String org_auto_id;
	private String org_given_id;
	private String org_name;
//	private String worker_id;
//	private String worker_email;
//	private String worker_name;
	private String creator_id;
	private String creator_email;
	private String creator_name;
	
	private String form_fields;
	private boolean form_location_required;

	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getForm_id() {
		return form_id;
	}
	public void setForm_id(String form_id) {
		this.form_id = form_id;
	}
	public String getForm_title() {
		return form_title;
	}
	public void setForm_title(String form_title) {
		this.form_title = form_title;
	}
	public String getForm_subtitle() {
		return form_subtitle;
	}
	public void setForm_subtitle(String form_subtitle) {
		this.form_subtitle = form_subtitle;
	}
	public String getForm_desc() {
		return form_desc;
	}
	public void setForm_desc(String form_desc) {
		this.form_desc = form_desc;
	}
	public String getForm_version() {
		return form_version;
	}
	public void setForm_version(String form_version) {
		this.form_version = form_version;
	}
	public String getForm_json() {
		return form_json;
	}
	public void setForm_json(String form_json) {
		this.form_json = form_json;
	}
	public String getOrg_auto_id() {
		return org_auto_id;
	}
	public void setOrg_auto_id(String org_auto_id) {
		this.org_auto_id = org_auto_id;
	}
	public String getOrg_given_id() {
		return org_given_id;
	}
	public void setOrg_given_id(String org_given_id) {
		this.org_given_id = org_given_id;
	}
	public String getOrg_name() {
		return org_name;
	}
	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}
	
	public String getForm_fields() {
		return form_fields;
	}
	public void setForm_fields(String form_fields) {
		this.form_fields = form_fields;
	}
	public String getCreator_id() {
		return creator_id;
	}
	public void setCreator_id(String creator_id) {
		this.creator_id = creator_id;
	}
	public String getCreator_email() {
		return creator_email;
	}
	public void setCreator_email(String creator_email) {
		this.creator_email = creator_email;
	}
	public String getCreator_name() {
		return creator_name;
	}
	public void setCreator_name(String creator_name) {
		this.creator_name = creator_name;
	}
	public boolean isForm_location_required() {
		return form_location_required;
	}
	public void setForm_location_required(boolean form_location_required) {
		this.form_location_required = form_location_required;
	}
}

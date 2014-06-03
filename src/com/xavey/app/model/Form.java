package com.xavey.app.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Form {
	private String form_name;
	private String form_title;
	private ArrayList<HashMap<String,String>> form_fields;
	
	public String getFormName() {
		return form_name;
	}
	public void setFormName(String form_name) {
		this.form_name = form_name;
	}
	public String getFormTitle() {
		return form_title;
	}
	public void setFormTitle(String form_title) {
		this.form_title = form_title;
	}
	public ArrayList<HashMap<String,String>> getFormFields() {
		return form_fields;
	}
	public void setFormFields(ArrayList<HashMap<String,String>> form_fields) {
		this.form_fields = form_fields;
	}
	

}

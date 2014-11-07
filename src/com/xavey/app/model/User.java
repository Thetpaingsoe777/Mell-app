package com.xavey.app.model;

import java.util.ArrayList;

public class User {
	private String user_id; // new added
	private String user_name;
	private String pwd;
	private String hashPwd;// new added
	private String email;// new added
	private String role;
	private String organization;// new added
/*	private String err;
	private boolean result;
	private String code;
	private String message;*/
	private ArrayList<Form> forms;// new added
	private int no_form;	// new added
	private String token;
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getHashPwd() {
		return hashPwd;
	}
	public void setHashPwd(String hashPwd) {
		this.hashPwd = hashPwd;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	/*public String getErr() {
		return err;
	}
	public void setErr(String err) {
		this.err = err;
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}*/
	public ArrayList<Form> getForms() {
		return forms;
	}
	public void setForms(ArrayList<Form> forms) {
		this.forms = forms;
	}
	public int getNo_form() {
		return no_form;
	}
	public void setNo_form(int no_form) {
		this.no_form = no_form;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}

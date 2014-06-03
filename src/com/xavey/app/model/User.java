package com.xavey.app.model;

public class User {
	private String user_name;
	private String pwd;
	private String err;
	private boolean result;
	private String btnState;
	private String action;
	private String code;
	private String message;
	
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
	public String getErr() {
		return err;
	}
	public void setErr(String err) {
		this.err = err;
	}
	public boolean getResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getBtnState() {
		return btnState;
	}
	public void setBtnState(String btnState) {
		this.btnState = btnState;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
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
	}

}

package com.xavey.app.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.widget.Toast;

public class Document implements Comparable {
	private String id;
	private String document_id;
	private String document_name;
	private String document_json;
	private String document_json_to_submit;
	private String created_at;
	private String form_id;
	private String created_worker;
	private String submitted;

	public Document() {

	}

	public Document(String document_id, String document_name,
			String document_json, String document_json_to_submit, String created_at, String form_id) {
		super();
		this.document_id = document_id;
		this.document_name = document_name;
		this.document_json = document_json;
		this.document_json_to_submit = document_json_to_submit;
		this.created_at = created_at;
		this.form_id = form_id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDocument_id() {
		return document_id;
	}

	public void setDocument_id(String document_id) {
		this.document_id = document_id;
	}

	public String getDocument_name() {
		return document_name;
	}

	public void setDocument_name(String document_name) {
		this.document_name = document_name;
	}

	public String getDocument_json() {
		return document_json;
	}

	public void setDocument_json(String document_json) {
		this.document_json = document_json;
	}
	
	public String getDocument_json_to_submit() {
		return document_json_to_submit;
	}

	public void setDocument_json_to_submit(String document_json_to_submit) {
		this.document_json_to_submit = document_json_to_submit;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getForm_id() {
		return form_id;
	}

	public void setForm_id(String form_id) {
		this.form_id = form_id;
	}

	public String getCreated_worker() {
		return created_worker;
	}

	public void setCreated_worker(String created_worker) {
		this.created_worker = created_worker;
	}

	public String getSubmitted() {
		return submitted;
	}

	public void setSubmitted(String submitted) {
		this.submitted = submitted;
	}

	@Override
	public int compareTo(Object another) {
		Date compareDate = ((Document) another).getParsedDate();
		Date myDate = getParsedDate();
		int compareSecond = compareDate.getSeconds();
		int mySecond = myDate.getSeconds();
		return compareSecond - mySecond;
	}

	public Date getParsedDate() {
		String dateInString = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
				.format(getCreated_at());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			return formatter.parse(dateInString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}

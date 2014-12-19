package com.xavey.android.model;

public class Audio {
	private String id; // auto increment id
	private String audio_name; // actually that's field_name
	private String audio_path;
	private String audio_id; // server's audio id
	private String doc_id;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAudio_name() {
		return audio_name;
	}
	public void setAudio_name(String audio_name) {
		this.audio_name = audio_name;
	}
	public String getAudio_path() {
		return audio_path;
	}
	public void setAudio_path(String audio_path) {
		this.audio_path = audio_path;
	}
	public String getAudio_id() {
		return audio_id;
	}
	public void setAudio_id(String audio_id) {
		this.audio_id = audio_id;
	}
	public String getDoc_id() {
		return doc_id;
	}
	public void setDoc_id(String doc_id) {
		this.doc_id = doc_id;
	}
}

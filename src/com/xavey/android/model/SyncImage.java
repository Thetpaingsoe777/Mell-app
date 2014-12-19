package com.xavey.android.model;

public class SyncImage {
	private String id;
	private String imageID;
	private String synceID;
	private byte[] imgByte;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getImageID() {
		return imageID;
	}
	
	public void setImageID(String imageID) {
		this.imageID = imageID;
	}
	
	public String getSynceID() {
		return synceID;
	}
	
	public void setSynceID(String synceID) {
		this.synceID = synceID;
	}
	
	public byte[] getImgByte() {
		return imgByte;
	}
	
	public void setImgByte(byte[] imgByte) {
		this.imgByte = imgByte;
	}
}

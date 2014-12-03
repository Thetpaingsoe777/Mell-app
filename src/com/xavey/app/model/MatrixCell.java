package com.xavey.app.model;

public class MatrixCell {
	
	private int v_index;
	private int h_index;
	private String fieldSkip;
	private String value;

	public int getV_index() {
		return v_index;
	}
	public void setV_index(int v_index) {
		this.v_index = v_index;
	}
	public int getH_index() {
		return h_index;
	}
	public void setH_index(int h_index) {
		this.h_index = h_index;
	}
	public String getFieldSkip() {
		return fieldSkip;
	}
	public void setFieldSkip(String fieldSkip) {
		this.fieldSkip = fieldSkip;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}

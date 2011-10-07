package com.coctelmental.android.project1886.model;

public class ResultBundle {
	
	private int resultCode;
	private String content;

	public ResultBundle() {}

	public ResultBundle(int resultCode) {
		this.resultCode = resultCode;
	}
	
	public ResultBundle(int resultCode, String content) {
		this.resultCode = resultCode;
		this.content = content;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}

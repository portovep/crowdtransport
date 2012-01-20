package com.coctelmental.android.project1886.model;

public class Credentials {
	
	public static final int TYPE_USER=0;
	public static final int TYPE_TAXI=1;
	public static final int TYPE_BUS=2;
	
	private String id;
	private String fullName;
	private String password;
	private int type;
	
	public Credentials(){}
	
	public Credentials(String id, String password, int type) {
		this.id = id;
		this.password = password;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}

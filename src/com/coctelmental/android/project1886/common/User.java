package com.coctelmental.android.project1886.common;

import com.coctelmental.android.project1886.common.util.JsonHandler;


public class User {
	
	private String userName;
	private String fullName;
	private String password;
	private String email;
	
	public User() {}
	
	public User(String userName, String name, String password, String email) {
		this.userName = userName;
		this.fullName = name;
		this.password = password;
		this.email = email;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setName(String name) {
		this.fullName = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}	
	
	public String getFullName() {
		return fullName;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public String toString() {
		return String.format("userName: %s\n" +
							 "fullName: %s\n" +
							 "email: %s", userName, fullName, email );
	}

	public String toJson() {
		return JsonHandler.toJson(this);	
	}
}

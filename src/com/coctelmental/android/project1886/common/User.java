package com.coctelmental.android.project1886.common;

import com.coctelmental.android.project1886.common.util.JsonHandler;


public class User {
	
	private String userName;
	private String name;
	private String password;
	private String email;
	
	public User() {}
	
	public User(String userName, String name, String password, String email) {
		this.userName = userName;
		this.name = name;
		this.password = password;
		this.email = email;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setName(String name) {
		this.name = name;
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
	
	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public String toString() {
		return String.format("userName: %s\n" +
							 "name: %s\n" +
							 "email: %s", userName, name, email );
	}

	public String toJson() {
		return JsonHandler.toJson(this);	
	}
}

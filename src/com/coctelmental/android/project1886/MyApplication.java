package com.coctelmental.android.project1886;

import com.coctelmental.android.project1886.model.Credentials;

import android.app.Application;

public class MyApplication extends Application {
	
	private static MyApplication singleton;
	
	private Credentials activeUser;
	
	public static MyApplication getInstance() {
		return singleton;
	}
	
	public final void onCreate() {
		super.onCreate();
		singleton = this;
	}

	public synchronized Credentials getActiveUser() {
		return activeUser;
	}

	public synchronized void setActiveUser(Credentials activeUser) {
		this.activeUser = activeUser;
	}
		
	
}

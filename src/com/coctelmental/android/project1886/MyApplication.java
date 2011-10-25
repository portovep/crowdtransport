package com.coctelmental.android.project1886;

import java.util.UUID;

import com.coctelmental.android.project1886.model.Credentials;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MyApplication extends Application {
	
	private static MyApplication singleton;
	
	private static String uniqueID = null;
	private static final String PREF_UNIQ_ID = "PREF_UNIQ_ID";
	
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
	
	public synchronized String id() {
		if (uniqueID == null) {
			SharedPreferences appSettings = getSharedPreferences(PREF_UNIQ_ID, Context.MODE_PRIVATE);
			// looking for stored unique id into app preferences
			uniqueID = appSettings.getString(PREF_UNIQ_ID, null);
			if (uniqueID == null) {
				// create the unique id for this installation
				uniqueID = UUID.randomUUID().toString();
				Editor editor = appSettings.edit();
				// store it into app preferences
				editor.putString(PREF_UNIQ_ID, uniqueID);
				editor.commit();
			}
		}
		return uniqueID;
	}		
	
}

package com.coctelmental.android.project1886.main;

import java.util.UUID;

import com.coctelmental.android.project1886.common.ServiceRequestInfo;
import com.coctelmental.android.project1886.model.Credentials;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppData extends Application {
	
	private static AppData singleton;
	
	private static String uniqueID = null;
	private static final String PREF_UNIQ_ID = "PREF_UNIQ_ID";
	
	private String targetCity;
	private String targetLine;
	
	private ServiceRequestInfo serviceRequestInfo;
	
	private Credentials activeUser;
	
	
	
	public static AppData getInstance() {
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
	
	public synchronized void storeTrackingInfo(String targetCity, String targetLine) {
		this.targetCity = targetCity;
		this.targetLine = targetLine;
	}
	
	public synchronized String[] getStoredTrackingInfo() {
		String result[] = {targetCity, targetLine};
		return result;
	}
	
	public synchronized void storeServiceRequestInfo(ServiceRequestInfo serviceRequestInfo) {
		this.serviceRequestInfo = serviceRequestInfo;
	}
	
	public synchronized ServiceRequestInfo getStoredServiceRequestInfo() {
		return serviceRequestInfo;
	}
	
	public synchronized String getInstallationUniqueId() {
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

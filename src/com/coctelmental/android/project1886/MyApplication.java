package com.coctelmental.android.project1886;

import java.util.UUID;

import com.coctelmental.android.project1886.model.Credentials;
import com.coctelmental.android.project1886.model.ServiceRequestInfo;

import android.app.ActivityManager;
import android.app.Application;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;

public class MyApplication extends Application {
	
	private static MyApplication singleton;
	
	private static String uniqueID = null;
	private static final String PREF_UNIQ_ID = "PREF_UNIQ_ID";
	
	private String targetCity;
	private String targetLine;
	
	private ServiceRequestInfo serviceRequestInfo;
	
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
	
	public void storeTrackingInfo(String targetCity, String targetLine) {
		this.targetCity = targetCity;
		this.targetLine = targetLine;
	}
	
	public String[] getStoredTrackingInfo() {
		String result[] = {targetCity, targetLine};
		return result;
	}
	
	public synchronized void storeServiceRequestInfo(ServiceRequestInfo serviceRequestInfo) {
		this.serviceRequestInfo = serviceRequestInfo;
	}
	
	public synchronized ServiceRequestInfo getServiceRequestInfo() {
		return serviceRequestInfo;
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
	
	public boolean isServiceRunning(String serviceName) {
	    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceName.equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public boolean isGPSEnabled() {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return false;
		}
		return true;
	}
	
}

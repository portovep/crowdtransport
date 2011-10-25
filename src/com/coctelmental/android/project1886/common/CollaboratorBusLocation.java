package com.coctelmental.android.project1886.common;

import com.coctelmental.android.project1886.common.util.JsonHandler;


public class CollaboratorBusLocation {

	private String userID;
	private int latitude;
	private int longitude;

	
	public CollaboratorBusLocation (String id) {
		this.userID = id;
	}

	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String toString() {
		return String.format("locationID: %s\n" +
							 "latitude: %s\n" +
							 "longitude: %s", userID, latitude, longitude);
	}

	public String toJson() {
		return JsonHandler.toJson(this);	
	}
	
}

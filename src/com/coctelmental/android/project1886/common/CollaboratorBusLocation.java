package com.coctelmental.android.project1886.common;


public class CollaboratorBusLocation {

	private String userID;
	private GeoPointInfo geopoint;

	public CollaboratorBusLocation (String id) {
		this.userID = id;
	}
	
	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	public String getUserID() {
		return userID;
	}

	public GeoPointInfo getGeopoint() {
		return geopoint;
	}

	public void setGeopoint(GeoPointInfo geopoint) {
		this.geopoint = geopoint;
	}

}

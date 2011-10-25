package com.coctelmental.android.project1886.common;

import com.coctelmental.android.project1886.common.util.JsonHandler;



public class BusLocation {

	private String busLocationID;
	private int latitude;
	private int longitude;
	
	public BusLocation (String id) {
		this.busLocationID = id;
	}

	public String getBusLocationID() {
		return busLocationID;
	}

	public void setBusLocationID(String busLocationID) {
		this.busLocationID = busLocationID;
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
	
	public String toString() {
		return String.format("locationID: %s\n" +
							 "latitude: %s\n" +
							 "longitude: %s", busLocationID, latitude, longitude );
	}

	public String toJson() {
		return JsonHandler.toJson(this);	
	}
	
}

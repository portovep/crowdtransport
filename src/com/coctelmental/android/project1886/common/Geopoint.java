package com.coctelmental.android.project1886.common;

import com.coctelmental.android.project1886.common.util.JsonHandler;



public class Geopoint {

	private String id;
	private int latitude;
	private int longitude;
	
	public Geopoint (String id)
	{
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		return String.format("id: %s\n" +
							 "latitude: %s\n" +
							 "longitude: %s", id, latitude, longitude );
	}

	public String toJson() {
		return JsonHandler.toJson(this);	
	}
	
}

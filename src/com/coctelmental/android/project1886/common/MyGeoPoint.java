package com.coctelmental.android.project1886.common;

public class MyGeoPoint {
	
	private int latitudeE6;
	private int longitudeE6;
	
	public MyGeoPoint(int latitudeE6, int longitudeE6) {
		this.latitudeE6 = latitudeE6;
		this.longitudeE6 = longitudeE6;
	}

	public int getLatitudeE6() {
		return latitudeE6;
	}

	public int getLongitudeE6() {
		return longitudeE6;
	}
		
}

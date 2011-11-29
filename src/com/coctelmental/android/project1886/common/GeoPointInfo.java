package com.coctelmental.android.project1886.common;

import java.io.Serializable;

import com.google.android.maps.GeoPoint;

@SuppressWarnings("serial")
public class GeoPointInfo implements Serializable {
	
	private int latitudeE6;
	private int longitudeE6;
	
	public GeoPointInfo(int latitudeE6, int longitudeE6) {
		this.latitudeE6 = latitudeE6;
		this.longitudeE6 = longitudeE6;
	}
	
	public GeoPointInfo(GeoPoint gp) {
		this.latitudeE6 = gp.getLatitudeE6();
		this.longitudeE6 = gp.getLongitudeE6();
	}	

	public int getLatitudeE6() {
		return latitudeE6;
	}

	public int getLongitudeE6() {
		return longitudeE6;
	}	
		
}

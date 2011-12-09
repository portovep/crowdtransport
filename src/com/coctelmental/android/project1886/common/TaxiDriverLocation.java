package com.coctelmental.android.project1886.common;


public class TaxiDriverLocation {

	private String taxiDriverID;
	private String taxiDriverUUID;
	private GeoPointInfo geopoint;

	public TaxiDriverLocation(String taxiDriverID, String taxiDriverUUID) {
		this.taxiDriverID = taxiDriverID;
		this.taxiDriverUUID = taxiDriverUUID;
	}
	
	public String getTaxiDriverID() {
		return taxiDriverID;
	}

	public void setTaxiDriverID(String taxiDriverID) {
		this.taxiDriverID = taxiDriverID;
	}

	public String getTaxiDriverUUID() {
		return taxiDriverUUID;
	}

	public void setTaxiDriverUUID(String taxiDriverUUID) {
		this.taxiDriverUUID = taxiDriverUUID;
	}

	public GeoPointInfo getGeopoint() {
		return geopoint;
	}

	public void setGeopoint(GeoPointInfo geopoint) {
		this.geopoint = geopoint;
	}
	
}

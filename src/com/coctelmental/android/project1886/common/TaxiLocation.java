package com.coctelmental.android.project1886.common;

public class TaxiLocation {

	private String taxiDriverID;
	private String taxiDriverUUID;
	private LocationInfo location;
	
	public TaxiLocation(String taxiDriverID, String taxiDriverUUID) {
		this.taxiDriverID = taxiDriverID;
		this.taxiDriverUUID = taxiDriverUUID;
	}

	public String getTaxiDriverID() {
		return taxiDriverID;
	}
	
	public void setTaxiDriverID(String taxiDriverID) {
		this.taxiDriverID = taxiDriverID;
	}
	
	public String getUUID() {
		return taxiDriverUUID;
	}

	public void setUUID(String uUID) {
		taxiDriverUUID = uUID;
	}
	
	public LocationInfo getLocation() {
		return location;
	}
	
	public void setLocation(LocationInfo location) {
		this.location = location;
	}
	
}

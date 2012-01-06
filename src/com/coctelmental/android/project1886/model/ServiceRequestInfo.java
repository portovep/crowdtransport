package com.coctelmental.android.project1886.model;

import com.coctelmental.android.project1886.common.GeoPointInfo;

public class ServiceRequestInfo {

	private String userUUID;
	private String userID;
	private GeoPointInfo gpOrigin;
	private GeoPointInfo gpDestination;
	private String taxiDriverID;
	private String taxiDriverUUID;
	private String comment;
	private int requestLifeTime;
	private String addressFrom;
	private String addressTo;
	
	public ServiceRequestInfo(String userUUID) {
		this.userUUID = userUUID;
	}
	
	public String getUserUUID() {
		return userUUID;
	}

	public void setUserUUID(String userUUID) {
		this.userUUID = userUUID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public GeoPointInfo getGpOrigin() {
		return gpOrigin;
	}

	public void setGpOrigin(GeoPointInfo gpOrigin) {
		this.gpOrigin = gpOrigin;
	}

	public GeoPointInfo getGpDestination() {
		return gpDestination;
	}

	public void setGpDestination(GeoPointInfo gpDestination) {
		this.gpDestination = gpDestination;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getRequestLifeTime() {
		return requestLifeTime;
	}

	public void setRequestLifeTime(int requestLifeTime) {
		this.requestLifeTime = requestLifeTime;
	}

	public String getAddressFrom() {
		return addressFrom;
	}

	public void setAddressFrom(String addressFrom) {
		this.addressFrom = addressFrom;
	}

	public String getAddressTo() {
		return addressTo;
	}

	public void setAddressTo(String addressTo) {
		this.addressTo = addressTo;
	}
	
}

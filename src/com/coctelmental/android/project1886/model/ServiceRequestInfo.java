package com.coctelmental.android.project1886.model;

import com.coctelmental.android.project1886.common.GeoPointInfo;

public class ServiceRequestInfo {

	private String userID;
	private GeoPointInfo gpOrigin;
	private GeoPointInfo gpDestination;
	private String taxiDriverID;
	private String clarificationComment;
	private int requestLifeTime;
	
	public ServiceRequestInfo(String userID) {
		this.userID = userID;
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

	public String getClarificationComment() {
		return clarificationComment;
	}

	public void setClarificationComment(String comment) {
		this.clarificationComment = comment;
	}

	public int getRequestLifeTime() {
		return requestLifeTime;
	}

	public void setRequestLifeTime(int requestLifeTime) {
		this.requestLifeTime = requestLifeTime;
	}
	
}

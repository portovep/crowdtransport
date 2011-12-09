package com.coctelmental.android.project1886.common;

public class DeviceInfo {

	private String UUID;
	private String registrationID;
	
	public DeviceInfo(String UUID) {
		this.UUID = UUID;
	}
	
	public String getUUID() {
		return UUID;
	}
	public void setUUID(String uUID) {
		UUID = uUID;
	}
	
	public String getRegistrationID() {
		return registrationID;
	}
	
	public void setRegistrationID(String registrationID) {
		this.registrationID = registrationID;
	}
	
}

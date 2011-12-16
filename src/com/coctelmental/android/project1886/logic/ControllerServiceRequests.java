package com.coctelmental.android.project1886.logic;


import com.coctelmental.android.project1886.MyApplication;
import com.coctelmental.android.project1886.common.DeviceInfo;
import com.coctelmental.android.project1886.common.util.JsonHandler;
import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.model.ServiceRequestInfo;
import com.coctelmental.android.project1886.util.ConnectionsHandler;

public class ControllerServiceRequests {
	
	private static final String C2DM_REGISTRATION_RESOURCE = "/c2dm-registration";
	private static final String SERVICE_REQUEST_RESOURCE = "/service-request";
	
	private ControllerUsers controllerU;
	
	public ControllerServiceRequests() {
		controllerU = new ControllerUsers();
	}
	
	public void createServiceRequest() {
		// get installation unique id
		String userUUID = MyApplication.getInstance().id();
		ServiceRequestInfo serviceRequestInfo = new ServiceRequestInfo(userUUID);
		
		// get logged user ID
		String userID;
		if(controllerU.existActiveUser())
			userID = controllerU.getActiveUser().getId();
		else
			userID = userUUID;
		
		serviceRequestInfo.setUserID(userID);
				
		// store info overriding previous data
		MyApplication.getInstance().storeServiceRequestInfo(serviceRequestInfo);
	}
	
	public ServiceRequestInfo getServiceRequest() {
		return MyApplication.getInstance().getServiceRequestInfo();
	}
	
	public static int sendServiceRequest(){
		int result = -1;

		ServiceRequestInfo serviceRequest = MyApplication.getInstance().getServiceRequestInfo();
		if (serviceRequest != null) {
			// convert to json
			String jsonServiceRequest = JsonHandler.toJson(serviceRequest);
			result = ConnectionsHandler.put(SERVICE_REQUEST_RESOURCE, jsonServiceRequest);
		}
		
		return result;		
	}
	
	public static ResultBundle getNewServiceRequest(String requestID){
		String taxiUUID = MyApplication.getInstance().id();
		String targetURL = SERVICE_REQUEST_RESOURCE + "/" + taxiUUID + "/request/" + requestID;
		return ConnectionsHandler.get(targetURL);	
	}
	
	public static ResultBundle getAllServiceRequest(){
		String taxiUUID = MyApplication.getInstance().id();
		String targetURL = SERVICE_REQUEST_RESOURCE + "/" + taxiUUID;
		return ConnectionsHandler.get(targetURL);	
	}
	
	public static int sendRegistrationIdToServer(String registrationID) {
		int result = -1;
		if (registrationID != null && registrationID != "") {
			// get installation UUID		
			String UUID = MyApplication.getInstance().id();
			
			// setup device info
			DeviceInfo deviceInfo = new DeviceInfo(UUID);
			deviceInfo.setRegistrationID(registrationID);
			
			// send to webservice
			String jsonDeviceInfo = JsonHandler.toJson(deviceInfo);
			result = ConnectionsHandler.post(C2DM_REGISTRATION_RESOURCE, jsonDeviceInfo);
		}
		return result;
	}

}

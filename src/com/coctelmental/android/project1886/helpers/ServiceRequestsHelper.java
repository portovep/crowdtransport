package com.coctelmental.android.project1886.helpers;


import com.coctelmental.android.project1886.common.DeviceInfo;
import com.coctelmental.android.project1886.common.ServiceRequestInfo;
import com.coctelmental.android.project1886.main.AppData;
import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.util.ConnectionsHandler;
import com.coctelmental.android.project1886.util.JsonHandler;
import com.google.android.maps.GeoPoint;

public class ServiceRequestsHelper {
	
	public static final int ERROR_MALFORMED_REQUEST = -1;
	
	private static final String URI_C2DM_REGISTRATION_RESOURCE = "/c2dm-registration";
	private static final String URI_SERVICE_REQUEST_RESOURCE = "/service-request";
	private static final String URI_REQUEST_RESPONSE_RESOURCE = "/request-response";

	
	public static void createNewServiceRequest() {
		// get installation unique id
		String userUUID = AppData.getInstance().getInstallationUniqueId();
		ServiceRequestInfo serviceRequestInfo = new ServiceRequestInfo(userUUID);
		
		// get logged user ID
		String userID;
		if(UsersHelper.existActiveUser())
			userID = UsersHelper.getActiveUser().getId();
		else
			userID = userUUID;
		
		serviceRequestInfo.setUserID(userID);
				
		// store info overriding previous data
		AppData.getInstance().storeServiceRequestInfo(serviceRequestInfo);
	}
	
	public static ServiceRequestInfo getServiceRequest() {
		return AppData.getInstance().getStoredServiceRequestInfo();
	}
	
	public static int sendServiceRequest(){
		int result = ERROR_MALFORMED_REQUEST;

		ServiceRequestInfo serviceRequest = AppData.getInstance().getStoredServiceRequestInfo();
		if (serviceRequest != null) {
			// convert to json
			String jsonServiceRequest = JsonHandler.toJson(serviceRequest);
			String targetURL = ConnectionsHandler.SERVER_ADDRESS + URI_SERVICE_REQUEST_RESOURCE; 
			result = ConnectionsHandler.put(targetURL, jsonServiceRequest);
		}
		
		return result;		
	}
	
	public static ResultBundle obtainAllServiceRequest(){
		String taxiUUID = AppData.getInstance().getInstallationUniqueId();
		String targetURL = ConnectionsHandler.SERVER_ADDRESS + URI_SERVICE_REQUEST_RESOURCE + "/" + taxiUUID;
		return ConnectionsHandler.get(targetURL);	
	}

	public static int acceptServiceRequest(String requestID){
		int result = -1;
	
		if (requestID != null) {
		    String taxiUUID = AppData.getInstance().getInstallationUniqueId();
		    // build data
		    String[] request = {"accept", taxiUUID};
		    String jsonRequest = JsonHandler.toJson(request);
			String targetURL = ConnectionsHandler.SERVER_ADDRESS + URI_REQUEST_RESPONSE_RESOURCE + "/" + requestID;
			result = ConnectionsHandler.post(targetURL, jsonRequest);	
		}
		
		return result;		
	}
	
	public static int cancelSentServiceRequest(){
		int result = ERROR_MALFORMED_REQUEST;
		
		ServiceRequestInfo serviceRequest = AppData.getInstance().getStoredServiceRequestInfo();
		if (serviceRequest != null) {
			String taxiUUID = serviceRequest.getTaxiDriverUUID();
			String requestID = serviceRequest.getUserUUID();
			
			if (taxiUUID != null && requestID != null) {
				String targetURL = ConnectionsHandler.SERVER_ADDRESS + URI_SERVICE_REQUEST_RESOURCE + "/" + taxiUUID + "/request/" + requestID;
				result = ConnectionsHandler.delete(targetURL);
			}
		}
		
		return result;		
	}
	
	public static int cancelAllServiceRequest(){
		String taxiUUID = AppData.getInstance().getInstallationUniqueId();
		String targetURL = ConnectionsHandler.SERVER_ADDRESS + URI_SERVICE_REQUEST_RESOURCE + "/" + taxiUUID;
		return ConnectionsHandler.delete(targetURL);		
	}
	
	public static int sendRegistrationID(String registrationID) {
		int result = -1;
		if (registrationID != null && registrationID != "") {
			// get installation UUID		
			String UUID = AppData.getInstance().getInstallationUniqueId();
			
			// setup device info
			DeviceInfo deviceInfo = new DeviceInfo(UUID);
			deviceInfo.setRegistrationID(registrationID);
			
			// send to webservice
			String jsonDeviceInfo = JsonHandler.toJson(deviceInfo);
			String targetURL = ConnectionsHandler.SERVER_ADDRESS + URI_C2DM_REGISTRATION_RESOURCE;
			result = ConnectionsHandler.post(targetURL, jsonDeviceInfo);
		}
		return result;
	}
	
	public static ResultBundle obtainRouteInfo(GeoPoint gpOrigin, GeoPoint gpDestination){
		// get coordinates
		double oriLatitude = gpOrigin.getLatitudeE6() / 1E6;
		double oriLongitude = gpOrigin.getLongitudeE6() / 1E6;
		
		double destLatitude = gpDestination.getLatitudeE6() / 1E6;
		double destLongitude = gpDestination.getLongitudeE6() / 1E6;
		
		// build URL
		String baseURL = "http://maps.googleapis.com/maps/api/distancematrix/json?";
		StringBuilder stBuilder = new StringBuilder(baseURL);
		stBuilder.append("origins=");
		stBuilder.append(String.valueOf(oriLatitude));
		stBuilder.append(",");
		stBuilder.append(String.valueOf(oriLongitude));
		stBuilder.append("&destinations=");
		stBuilder.append(String.valueOf(destLatitude));
		stBuilder.append(",");
		stBuilder.append(String.valueOf(destLongitude));
		stBuilder.append("&language=es&sensor=true");
		
		String targetURL = stBuilder.toString();
		
		return ConnectionsHandler.get(targetURL);	
	}
	
}

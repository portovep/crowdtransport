package com.coctelmental.android.project1886.logic;


import com.coctelmental.android.project1886.MyApplication;
import com.coctelmental.android.project1886.model.ServiceRequestInfo;

public class ControllerServiceRequests {
	
	private ControllerUsers controllerU;
	
	public ControllerServiceRequests() {
		controllerU = new ControllerUsers();
	}
	
	public void createServiceRequest() {		
		String userID;
		if(controllerU.existActiveUser())
			userID = controllerU.getActiveUser().getId();
		else
			// get installation unique id
			userID = MyApplication.getInstance().id();
		
		ServiceRequestInfo serviceRequestInfo = new ServiceRequestInfo(userID);
		// store info overriding previous data
		MyApplication.getInstance().storeServiceRequestInfo(serviceRequestInfo);
	}
	
	public ServiceRequestInfo getServiceRequest() {
		return MyApplication.getInstance().getServiceRequestInfo();
	}

}

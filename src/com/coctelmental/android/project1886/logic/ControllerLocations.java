package com.coctelmental.android.project1886.logic;

import com.coctelmental.android.project1886.common.Geopoint;
import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.util.ConnectionsHandler;

public class ControllerLocations {
	
	private static final String LOCATION_RESOURCE = "/location";
	
	public ResultBundle obtainLocation(String city, String line) {
		String resourceID = city + line;
		String targetURL = LOCATION_RESOURCE + "/" + resourceID;
		// REST request to the specific resource
		return ConnectionsHandler.get(targetURL);
	}
	
	public int sendLocation(String city, String line, Geopoint gp) {
		String resourceID = city + line;
		String targetURL = LOCATION_RESOURCE + "/" + resourceID;
		return ConnectionsHandler.put(targetURL, gp.toJson());
	}

}

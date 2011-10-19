package com.coctelmental.android.project1886.logic;

import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.util.ConnectionsHandler;

public class ControllerAvailableData {
	
	private static final String CITY_RESOURCE = "/city";
	private static final String LINE_RESOURCE = "/line";
	
	public ResultBundle getAvailableCities() {
		// request to specific resource
		return ConnectionsHandler.get(CITY_RESOURCE);
	}
	
	public ResultBundle getAvailableLines(String targetCity) {
		String targetUrl = CITY_RESOURCE + "/" + targetCity + LINE_RESOURCE;
		return ConnectionsHandler.get(targetUrl);
	}

}

package com.coctelmental.android.project1886.helpers;

import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.util.ConnectionsHandler;

public class AvailableDataHelper {
	
	private static final String URI_CITY_RESOURCE = "/city";
	private static final String URI_LINE_RESOURCE = "/line";
	
	public static ResultBundle getAvailableCities() {
		// request to specific resource
		return ConnectionsHandler.get(URI_CITY_RESOURCE);
	}
	
	public static ResultBundle getAvailableLines(String targetCity) {
		String targetUrl = URI_CITY_RESOURCE + "/" + targetCity + URI_LINE_RESOURCE;
		return ConnectionsHandler.get(targetUrl);
	}

}

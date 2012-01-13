package com.coctelmental.android.project1886.helpers;

import com.coctelmental.android.project1886.common.BusDriver;
import com.coctelmental.android.project1886.common.TaxiDriver;
import com.coctelmental.android.project1886.common.User;
import com.coctelmental.android.project1886.main.AppData;
import com.coctelmental.android.project1886.model.Credentials;
import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.util.ConnectionsHandler;
import com.coctelmental.android.project1886.util.JsonHandler;
import com.coctelmental.android.project1886.util.Tools;

public class UsersHelper {
	
	private static final String URI_USER_RESOURCE = "/user";
	private static final String URI_BUS_RESOURCE = "/bus";
	private static final String URI_TAXI_RESOURCE = "/taxi";

	
	public static ResultBundle getUser(String userID) {
		String targetURL = URI_USER_RESOURCE + "/" + userID;
		return ConnectionsHandler.get(targetURL);
	}

	public static ResultBundle getBusDriver(String busDriverID) {
		String targetURL = URI_BUS_RESOURCE + "/" + busDriverID;
		return ConnectionsHandler.get(targetURL);
	}
	
	public static ResultBundle getTaxiDriver(String taxiDriverID) {
		String targetURL = URI_TAXI_RESOURCE + "/" + taxiDriverID;
		return ConnectionsHandler.get(targetURL);
	}

	public static int registerUser(User user) {
		return ConnectionsHandler.put(URI_USER_RESOURCE, JsonHandler.toJson(user));
	}

	public static int registerBusDriver(BusDriver busDriver) {
		return ConnectionsHandler.put(URI_BUS_RESOURCE, JsonHandler.toJson(busDriver));
	}
	
	public static int registerTaxiDriver(TaxiDriver taxiDriver) {
		return ConnectionsHandler.put(URI_TAXI_RESOURCE, JsonHandler.toJson(taxiDriver));
	}
	
	public static void logIn(Credentials credentials) {
		AppData.getInstance().setActiveUser(credentials);
	}
	
	public static void logOut() {
		AppData.getInstance().setActiveUser(null);
	}
	
	public static boolean existActiveUser() {
		if(getActiveUser() != null)
			return true;
		return false;
	}
	
	public static Credentials getActiveUser() {
		return AppData.getInstance().getActiveUser();
	}
	
	public static String passwordToDigest(String password) {
		return Tools.getPasswordDigest(password);
	}
	
}

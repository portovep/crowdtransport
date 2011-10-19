package com.coctelmental.android.project1886.logic;

import com.coctelmental.android.project1886.MyApplication;
import com.coctelmental.android.project1886.common.BusDriver;
import com.coctelmental.android.project1886.common.TaxiDriver;
import com.coctelmental.android.project1886.common.User;
import com.coctelmental.android.project1886.model.Credentials;
import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.util.ConnectionsHandler;
import com.coctelmental.android.project1886.util.Tools;

public class ControllerUsers {
	
	private static final String USER_RESOURCE = "/user";
	private static final String TAXI_RESOURCE = "/taxi";
	private static final String BUS_RESOURCE = "/bus";
	
	
	public ControllerUsers() {}
	
	public ResultBundle getUser(String userID) {
		String targetURL = USER_RESOURCE + "/" + userID;
		return ConnectionsHandler.get(targetURL);
	}

	public ResultBundle getTaxiDriver(String userID) {
		String targetURL = TAXI_RESOURCE + "/" + userID;
		return ConnectionsHandler.get(targetURL);
	}

	public ResultBundle getBusDriver(String userID) {
		String targetURL = BUS_RESOURCE + "/" + userID;
		return ConnectionsHandler.get(targetURL);
	}

	public int registerUser(User user) {
		return ConnectionsHandler.put(USER_RESOURCE, user.toJson());
	}

	public int registerTaxiDriver(TaxiDriver taxiDriver) {
		return ConnectionsHandler.put(TAXI_RESOURCE, taxiDriver.toJson());
	}
	
	public int registerBusDriver(BusDriver busDriver) {
		return ConnectionsHandler.put(BUS_RESOURCE, busDriver.toJson());
	}
	
	public void logIn(Credentials credentials) {
		MyApplication.getInstance().setActiveUser(credentials);
	}
	
	public void logOut() {
		MyApplication.getInstance().setActiveUser(null);
	}
	
	public boolean existActiveUser() {
		if(getActiveUser() != null)
			return true;
		return false;
	}
	
	public Credentials getActiveUser() {
		return MyApplication.getInstance().getActiveUser();
	}
	
	public String passwordToDigest(String password) {
		return Tools.digestFromPassword(password);
	}
	
}

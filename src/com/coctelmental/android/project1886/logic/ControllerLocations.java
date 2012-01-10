package com.coctelmental.android.project1886.logic;

import android.location.Location;
import android.util.Log;

import com.coctelmental.android.project1886.MyApplication;
import com.coctelmental.android.project1886.common.CollaboratorBusLocation;
import com.coctelmental.android.project1886.common.GeoPointInfo;
import com.coctelmental.android.project1886.common.TaxiDriverLocation;
import com.coctelmental.android.project1886.common.util.JsonHandler;
import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.util.ConnectionsHandler;

public class ControllerLocations {
	
	private static final String LOCATION_RESOURCE = "/location";
	private static final String TAXI_LOCATION_RESOURCE = "/location-taxi";
	
	public ResultBundle obtainLocation(String city, String line) {
		String resourceID = city + line;
		String targetURL = LOCATION_RESOURCE + "/" + resourceID;
		// REST request to specific resource
		return ConnectionsHandler.get(targetURL);
	}
	
	public ResultBundle obtainTaxiLocation(GeoPointInfo gpOrigin) {
		String jsonGpOrigin = JsonHandler.toJson(gpOrigin);
		
		String targetURL = TAXI_LOCATION_RESOURCE + "/" + jsonGpOrigin;
		return ConnectionsHandler.get(targetURL);
	}
	
	public int sendCollaboratorLocation(String userID, Location location) {
		int result = -1;
		
		if (location != null) {			
			// setup location info
			CollaboratorBusLocation cBusLocation = new CollaboratorBusLocation(userID);
			Double latitude = location.getLatitude()*1E6;
			Double longitude = location.getLongitude()*1E6;
			GeoPointInfo gp = new GeoPointInfo(latitude.intValue(), longitude.intValue());
			cBusLocation.setGeopoint(gp);	
			
	        // get targets
	        String[] storedInfo = MyApplication.getInstance().getStoredTrackingInfo();
	        String city = storedInfo[0];
	        String line = storedInfo[1];
			
			String resourceID = city + line;
			String targetURL = LOCATION_RESOURCE + "/" + resourceID;
			result = ConnectionsHandler.put(targetURL, JsonHandler.toJson(cBusLocation));
			
			// Log
			String newLocation = location.getLatitude() + " - " + location.getLongitude();
			Log.e("Collaborator -> New location found", newLocation + " - " + cBusLocation.getUserID());
		}
		
		return result;
	}

	public int sendBusDriverLocation(String busDriverID, Location location) {
		int result = -1;
		
		if (location != null) {			
			// setup location info
			CollaboratorBusLocation cBusLocation = new CollaboratorBusLocation(busDriverID);
			Double latitude = location.getLatitude()*1E6;
			Double longitude = location.getLongitude()*1E6;
			GeoPointInfo gp = new GeoPointInfo(latitude.intValue(), longitude.intValue());
			cBusLocation.setGeopoint(gp);		
			
	        // get targets
	        String[] storedInfo = MyApplication.getInstance().getStoredTrackingInfo();
	        String city = storedInfo[0];
	        String line = storedInfo[1];
			
			String resourceID = city + line;
			String targetURL = LOCATION_RESOURCE + "/" + resourceID;
			result = ConnectionsHandler.put(targetURL, JsonHandler.toJson(cBusLocation));
			
			// Log
			String newLocation = location.getLatitude() + " - " + location.getLongitude();
			Log.e("BusDriver -> New location found", newLocation + " - " + cBusLocation.getUserID());
		}
		
		return result;
	}
	
	public int sendTaxiDriverLocation(String taxiDriverID, Location location) {
		int result = -1;
		
		if (location != null) {		
			// get installation UUID 
			String taxiDriverUUID = MyApplication.getInstance().id();
			// setup location info
			TaxiDriverLocation taxiDriverLocation = new TaxiDriverLocation(taxiDriverID, taxiDriverUUID);
			taxiDriverLocation.setTaxiDriverID(taxiDriverID);
			Double latitude = location.getLatitude()*1E6;
			Double longitude = location.getLongitude()*1E6;
			GeoPointInfo gp = new GeoPointInfo(latitude.intValue(), longitude.intValue());
			taxiDriverLocation.setGeopoint(gp);
			
			
			String targetURL = TAXI_LOCATION_RESOURCE;
			String jsonLocation = JsonHandler.toJson(taxiDriverLocation);
			result = ConnectionsHandler.put(targetURL, jsonLocation);
			
			// Log
			String newLocation = gp.getLatitudeE6() + " - " + gp.getLongitudeE6();
			Log.e("TaxiDriver -> New location found", newLocation + " - " + taxiDriverLocation.getTaxiDriverID());
		}
		
		return result;
	}
}

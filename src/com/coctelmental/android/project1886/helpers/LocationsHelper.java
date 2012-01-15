package com.coctelmental.android.project1886.helpers;

import android.location.Location;
import android.util.Log;

import com.coctelmental.android.project1886.common.CollaboratorBusLocation;
import com.coctelmental.android.project1886.common.GeoPointInfo;
import com.coctelmental.android.project1886.common.TaxiDriverLocation;
import com.coctelmental.android.project1886.main.AppData;
import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.util.ConnectionsHandler;
import com.coctelmental.android.project1886.util.JsonHandler;

public class LocationsHelper {
	
	private static final String URI_BUS_LOCATION_RESOURCE = "/location";
	private static final String URI_TAXI_LOCATION_RESOURCE = "/location-taxi";
	
	public static ResultBundle obtainBusLocations(String city, String line) {
		String resourceID = city + line;
		String targetURL = ConnectionsHandler.SERVER_ADDRESS + URI_BUS_LOCATION_RESOURCE + "/" + resourceID;
		// request to target resource
		return ConnectionsHandler.get(targetURL);
	}
	
	public static ResultBundle obtainTaxiLocations(GeoPointInfo gpOrigin) {
		String jsonGpOrigin = JsonHandler.toJson(gpOrigin);
		
		String targetURL = ConnectionsHandler.SERVER_ADDRESS + URI_TAXI_LOCATION_RESOURCE + "/" + jsonGpOrigin;
		return ConnectionsHandler.get(targetURL);
	}
	
	public static int sendCollaboratorLocation(String userID, Location location) {
		int result = -1;
		
		if (location != null) {			
			// setup location info
			CollaboratorBusLocation cBusLocation = new CollaboratorBusLocation(userID);
			Double latitude = location.getLatitude()*1E6;
			Double longitude = location.getLongitude()*1E6;
			GeoPointInfo gp = new GeoPointInfo(latitude.intValue(), longitude.intValue());
			cBusLocation.setGeopoint(gp);	
			
	        // get targets
	        String[] storedInfo = AppData.getInstance().getStoredTrackingInfo();
	        String city = storedInfo[0];
	        String line = storedInfo[1];
			
			String resourceID = city + line;
			String targetURL = ConnectionsHandler.SERVER_ADDRESS + URI_BUS_LOCATION_RESOURCE + "/" + resourceID;
			result = ConnectionsHandler.put(targetURL, JsonHandler.toJson(cBusLocation));
			
			// Log
			String newLocation = location.getLatitude() + " - " + location.getLongitude();
			Log.e("Collaborator -> New location found", newLocation + " - " + cBusLocation.getUserID());
		}
		
		return result;
	}

	public static int sendBusDriverLocation(String busDriverID, Location location) {
		int result = -1;
		
		if (location != null) {			
			// setup location info
			CollaboratorBusLocation cBusLocation = new CollaboratorBusLocation(busDriverID);
			Double latitude = location.getLatitude()*1E6;
			Double longitude = location.getLongitude()*1E6;
			GeoPointInfo gp = new GeoPointInfo(latitude.intValue(), longitude.intValue());
			cBusLocation.setGeopoint(gp);		
			
	        // get targets
	        String[] storedInfo = AppData.getInstance().getStoredTrackingInfo();
	        String city = storedInfo[0];
	        String line = storedInfo[1];
			
			String resourceID = city + line;
			String targetURL = ConnectionsHandler.SERVER_ADDRESS + URI_BUS_LOCATION_RESOURCE + "/" + resourceID;
			result = ConnectionsHandler.put(targetURL, JsonHandler.toJson(cBusLocation));
			
			// Log
			String newLocation = location.getLatitude() + " - " + location.getLongitude();
			Log.e("BusDriver -> New location found", newLocation + " - " + cBusLocation.getUserID());
		}
		
		return result;
	}
	
	public static int sendTaxiDriverLocation(String taxiDriverID, Location location) {
		int result = -1;
		
		if (location != null) {		
			// get installation UUID 
			String taxiDriverUUID = AppData.getInstance().getInstallationUniqueId();
			// setup location info
			TaxiDriverLocation taxiDriverLocation = new TaxiDriverLocation(taxiDriverID, taxiDriverUUID);
			taxiDriverLocation.setTaxiDriverID(taxiDriverID);
			Double latitude = location.getLatitude()*1E6;
			Double longitude = location.getLongitude()*1E6;
			GeoPointInfo gp = new GeoPointInfo(latitude.intValue(), longitude.intValue());
			taxiDriverLocation.setGeopoint(gp);
			
			
			String targetURL = ConnectionsHandler.SERVER_ADDRESS + URI_TAXI_LOCATION_RESOURCE;
			String jsonLocation = JsonHandler.toJson(taxiDriverLocation);
			result = ConnectionsHandler.put(targetURL, jsonLocation);
			
			// Log
			String newLocation = gp.getLatitudeE6() + " - " + gp.getLongitudeE6();
			Log.e("TaxiDriver -> New location found", newLocation + " - " + taxiDriverLocation.getTaxiDriverID());
		}
		
		return result;
	}
}

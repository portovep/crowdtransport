package com.coctelmental.android.project1886;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class UserLocationHelper {
	
	private LocationManager lm;
	private boolean gpsEnabled = false;
	private boolean netEnabled = false;
	private Location gpsLocation = null;
	private Location netLocation = null;
	
	public UserLocationHelper(Context context) {
		// get location manager
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public boolean setupListeners() {
		gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		netEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		// check whether there is a provider available
		if(gpsEnabled == false && netEnabled == false)
			return false;
		// register listeners for each provider available
		if(gpsEnabled)
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListener);
		if(netEnabled)
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, netLocationListener);		
		return true;
	}
	
	private LocationListener gpsLocationListener = new LocationListener() {		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		
		@Override
		public void onProviderEnabled(String provider) {}
		
		@Override
		public void onProviderDisabled(String provider) {}
		
		@Override
		public void onLocationChanged(Location location) {
			// save location
			gpsLocation = location;
			// remove listeners
			lm.removeUpdates(this);
			lm.removeUpdates(netLocationListener);
		}
	};
	
	private LocationListener netLocationListener = new LocationListener() {		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		
		@Override
		public void onProviderEnabled(String provider) {}
		
		@Override
		public void onProviderDisabled(String provider) {}
		
		@Override
		public void onLocationChanged(Location location) {
			// save location
			netLocation = location;
			// remove listeners
			lm.removeUpdates(this);
			lm.removeUpdates(gpsLocationListener);
		}
	};
	
	public boolean isLocationAvailable() {		
		if(gpsLocation != null || netLocation != null)
			return true;
		return false;
	}
	
	public Location getBestLocation() {
		// return location with best accuracy (GPS > NET)
		if(gpsLocation != null)
			return gpsLocation;
		else
			return netLocation;
	}

}

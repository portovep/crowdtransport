package com.coctelmental.android.project1886.helpers;

import java.util.List;

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

	public Location getValidLastKnownLocation(long maxTime, float minAccuracy) {
		Location bestLocation = null;
		long bestTime = maxTime;
		float bestAccuracy = minAccuracy;
		long now = System.currentTimeMillis();
		
		List<String> providers = lm.getAllProviders();
		for(String provider : providers) {
			Location location = lm.getLastKnownLocation(provider);
			if (location != null) {
				float accuracy = location.getAccuracy();
				long time = now - location.getTime();
				
				if((time > 0 && time <= bestTime && accuracy <= bestAccuracy)) {
					// time > 0 to elude a problem with some GPS time information
					bestLocation = location;
					bestAccuracy = accuracy;
					bestTime = time;
				}
		  }
		}
		return bestLocation;
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
		public void onProviderEnabled(String provider) {
			gpsEnabled = true;
		}
		@Override
		public void onProviderDisabled(String provider) {
			gpsEnabled = false;
		}
		
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
		public void onProviderEnabled(String provider) {
			netEnabled = true;
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			netEnabled = false;
		}
		
		@Override
		public void onLocationChanged(Location location) {
			// save location
			netLocation = location;
			// remove listeners
			lm.removeUpdates(this);
			lm.removeUpdates(gpsLocationListener);
		}
	};
	
	public boolean areProvidersEnabled() {
		if(netEnabled || gpsEnabled)
			return true;
		return false;
	}
	
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

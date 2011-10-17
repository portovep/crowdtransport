package com.coctelmental.android.project1886;

import com.coctelmental.android.project1886.common.Geopoint;
import com.coctelmental.android.project1886.util.ConnectionsHandler;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class CollaborationTrackingService extends Service {
	
	private static final String PROVIDER = LocationManager.GPS_PROVIDER;
	private static final int TIME_BETWEEN_UPDATES = 10000; // milliseconds
	private static final int DISTANCE_BETWEEN_UPDATES = 200; // meters
	
	private LocationManager locationManager;
	private String targetResourceID;
	private String userID;
	
	private Location updatedLocation;
			
	
	@Override
	public void onCreate() {
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    // get data from intent
    	Bundle extras = intent.getExtras();	    
        String targetCity = extras != null ? extras.getString(CollaborationStatusPanel.TARGET_CITY) : "";
        String targetLine = extras != null ? extras.getString(CollaborationStatusPanel.TARGET_LINE) : "";    
        
        targetResourceID = targetCity+targetLine;
        
	    if(MyApplication.getInstance().getActiveUser() != null)
	    	userID = MyApplication.getInstance().getActiveUser().getId();
	    else
	    	userID = "noUser";
        
        locationManager.requestLocationUpdates(PROVIDER, TIME_BETWEEN_UPDATES, DISTANCE_BETWEEN_UPDATES, collaboratorLocationListener);
		return Service.START_STICKY;
	}

	private LocationListener collaboratorLocationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String arg0) {		
		}
		
		@Override
		public void onLocationChanged(Location location) {
			updatedLocation = location;
			Thread workerThread = new Thread(sendNewLocationInBackground);
			workerThread.start();
		}
	};
	
	private Runnable sendNewLocationInBackground = new Runnable() {
		
		@Override
		public void run() {
			sendNewLocation(updatedLocation);
		}
	};
	
	private void sendNewLocation(Location location) {
		if (location != null) {
			String newLocation = location.getLatitude() + " - " + location.getLongitude();
			
			Geopoint gp = new Geopoint(this.userID);
			Double latitude = location.getLatitude()*1E6;
			Double longitude = location.getLongitude()*1E6;
			gp.setLatitude(latitude.intValue());
			gp.setLongitude(longitude.intValue());
			
			ConnectionsHandler.put("/location/"+this.targetResourceID, gp.toJson());
			Log.e("New location", newLocation + " - " + gp.getId());
		}
	}
	
	@Override
	public void onDestroy() {
		locationManager.removeUpdates(collaboratorLocationListener);
		Toast.makeText(this, getString(R.string.collaborationServiceFinished), Toast.LENGTH_SHORT).show(); 
		stopSelf();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

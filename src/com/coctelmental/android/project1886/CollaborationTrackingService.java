package com.coctelmental.android.project1886;

import com.coctelmental.android.project1886.common.Geopoint;
import com.coctelmental.android.project1886.util.ConnectionsHandler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
	
	private static final int NOTIFICATION_ID = 1;
	private NotificationManager notificationManager;
	
	private static final String PROVIDER = LocationManager.GPS_PROVIDER;
	private static final int TIME_BETWEEN_UPDATES = 10000; // milliseconds
	private static final int DISTANCE_BETWEEN_UPDATES = 50; // meters
	
	private LocationManager locationManager;
	private String targetResourceID;
	private String userID;
	
	private Location updatedLocation;
	private PendingIntent pendingIntent;
			
	
	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);	
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    // get data from intent
    	Bundle extras = intent.getExtras();	    
        String targetCity = extras != null ? extras.getString(CollaborationLineSelection.TARGET_CITY) : "";
        String targetLine = extras != null ? extras.getString(CollaborationLineSelection.TARGET_LINE) : "";
        
		// setup pending intent 
		Intent notificationIntent = new Intent(this, CollaborationInformationPanel.class);
		notificationIntent.putExtra(CollaborationLineSelection.TARGET_CITY, targetCity);
		notificationIntent.putExtra(CollaborationLineSelection.TARGET_LINE, targetLine);
		this.pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);		
		// setup new notification
		Notification notification = setupNotification(R.drawable.icon, getString(R.string.collaborationServiceStarted),
				getString(R.string.app_name), getString(R.string.collaborationServiceRunning), pendingIntent);		
		// launch notification
		notificationManager.notify(NOTIFICATION_ID, notification);
		
        // build targetResourceID
        targetResourceID = targetCity+targetLine;
        // get active user if exists
	    if(MyApplication.getInstance().getActiveUser() != null)
	    	userID = MyApplication.getInstance().getActiveUser().getId();
	    else
	    	userID = "noUser";
        
	    // registering location listener with target settings
        locationManager.requestLocationUpdates(PROVIDER, TIME_BETWEEN_UPDATES, DISTANCE_BETWEEN_UPDATES, collaboratorLocationListener);
		return Service.START_STICKY;
	}

	private LocationListener collaboratorLocationListener = new LocationListener() {		
		@Override
		public void onLocationChanged(Location location) {
			// updating class variable instance with new detected location
			updatedLocation = location;
			// setup new thread for sending new location to remote server
			Thread workerThread = new Thread(sendNewLocationInBackground);
			// launch thread
			workerThread.start();
		}
		
		@Override
		public void onProviderEnabled(String arg0) {
			// update notification to alert user
			// setup notification
			Notification notification = setupNotification(R.drawable.icon, getString(R.string.collaborationServiceRunning),
					getString(R.string.app_name), getString(R.string.collaborationServiceRunning), pendingIntent);
			// update current showed notification
			notificationManager.notify(NOTIFICATION_ID, notification);
		}
		
		@Override
		public void onProviderDisabled(String arg0) {			
			// update notification to alert user
			// setup notification
			Notification notification = setupNotification(R.drawable.icon_error, getString(R.string.collaborationServiceGPSDisabled), 
					getString(R.string.app_name), getString(R.string.collaborationServiceGPSDisabled), pendingIntent);
			// update current showed notification
			notificationManager.notify(NOTIFICATION_ID, notification);
		}
		
		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// nothing to do here
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
			// setup new geopoint
			Geopoint gp = new Geopoint(this.userID);
			Double latitude = location.getLatitude()*1E6;
			Double longitude = location.getLongitude()*1E6;
			gp.setLatitude(latitude.intValue());
			gp.setLongitude(longitude.intValue());
			
			// sending new location
			ConnectionsHandler.put("/location/"+this.targetResourceID, gp.toJson());
			
			String newLocation = location.getLatitude() + " - " + location.getLongitude();
			Log.e("New location found", newLocation + " - " + gp.getId());
		}
	}
	
	@Override
	public void onDestroy() {
		// disabling location listener
		locationManager.removeUpdates(collaboratorLocationListener);
		// disabling current notification  
		notificationManager.cancel(NOTIFICATION_ID);
		// notifying the user
		Toast.makeText(this, getString(R.string.collaborationServiceFinished), Toast.LENGTH_SHORT).show();
		// finish service
		stopSelf();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Notification setupNotification(int icon, CharSequence tickerText, CharSequence contentTitle, 
			CharSequence contentText, PendingIntent pendingIntent) {	
		long when = System.currentTimeMillis();		
		Notification notification = new Notification(icon, tickerText, when);
		// default sound when notification is launched
		notification.defaults |= Notification.DEFAULT_SOUND;
		// add flag to unable behavior of clear button for this notification and specify as ongoing event
		notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		notification.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, pendingIntent);		
		return notification;		
	}
}
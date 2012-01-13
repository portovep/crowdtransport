package com.coctelmental.android.project1886.buses;

import com.coctelmental.android.project1886.R;
import com.coctelmental.android.project1886.helpers.LocationsHelper;
import com.coctelmental.android.project1886.helpers.UsersHelper;
import com.coctelmental.android.project1886.main.AppData;
import com.coctelmental.android.project1886.model.Credentials;
import com.coctelmental.android.project1886.util.Tools;

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
import android.view.Gravity;
import android.widget.Toast;

public class BusTrackingService extends Service {
		
	public static final String EXTRA_CALLER_ACTIVITY = "targetActivity";	
	public static final int ID_COLLABORATOR_ACTIVITY = 0;
	public static final int ID_BUSDRIVER_ACTIVITY = 1;
	
	private static final int ID_NOTIFICATION = 1;
	private NotificationManager notificationManager;
	
	private static final String DEFAULT_PROVIDER = LocationManager.GPS_PROVIDER;
	
	private static final int TIME_BETWEEN_UPDATES = 10000; // milliseconds
	private static final int DISTANCE_BETWEEN_UPDATES = 20; // meters
	
	private LocationManager locationManager;
	private String userID;
	private int userType;
	
	private Location updatedLocation;
	private PendingIntent pendingIntent;
			
	
	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}
	
	@Override
	public void onDestroy() {
		// disabling location listener
		locationManager.removeUpdates(trackingLocationListener);
		// disabling current notification  
		notificationManager.cancel(ID_NOTIFICATION);
		// notifying the user
		Tools.buildToast(getApplicationContext(), getString(R.string.trackingServiceFinished), Gravity.CENTER, Toast.LENGTH_SHORT).show();
		// finish service
		stopSelf();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    // get intent data
    	Bundle extras = intent.getExtras();	    
        int callerActivityID = extras != null ? extras.getInt(EXTRA_CALLER_ACTIVITY) : 0;
        
        Intent notificationIntent;
        // check caller activity
        if(callerActivityID == ID_BUSDRIVER_ACTIVITY)
        	notificationIntent = new Intent(this, BusDriverInformationPanel.class);
        else
        	notificationIntent = new Intent(this, CollaboratorInformationPanel.class);
        
        // setup pending intent
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		this.pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);	
		
		// setup new notification
		Notification notification = setupNotification(R.drawable.ic_stat_notify_tracking, getString(R.string.trackingServiceStarted),
				getString(R.string.app_name), getString(R.string.trackingServiceRunning), pendingIntent);		
		// launch notification
		notificationManager.notify(ID_NOTIFICATION, notification);
	
		// get userID
	    if(UsersHelper.existActiveUser()) {
	    	userID = UsersHelper.getActiveUser().getId();
	    	userType = UsersHelper.getActiveUser().getType();
	    }
	    else {
	    	// if no user logged, use unique id as identification of this app installation
	    	userID = AppData.getInstance().getInstallationUniqueId();
	    	userType = Credentials.TYPE_USER;
	    }
        
	    // registering location listener with target settings
        locationManager.requestLocationUpdates(DEFAULT_PROVIDER, TIME_BETWEEN_UPDATES, DISTANCE_BETWEEN_UPDATES, trackingLocationListener);
		return Service.START_STICKY;
	}

	private LocationListener trackingLocationListener = new LocationListener() {		
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
			Notification notification = setupNotification(R.drawable.ic_stat_notify_tracking, getString(R.string.trackingServiceRunning),
					getString(R.string.app_name), getString(R.string.trackingServiceRunning), pendingIntent);
			// update current showed notification
			notificationManager.notify(ID_NOTIFICATION, notification);
		}
		
		@Override
		public void onProviderDisabled(String arg0) {			
			// update notification to alert user
			// setup notification
			Notification notification = setupNotification(R.drawable.ic_stat_notify_tracking_error, getString(R.string.trackingServiceGPSDisabled), 
					getString(R.string.app_name), getString(R.string.trackingServiceGPSDisabled), pendingIntent);
			// update current showed notification
			notificationManager.notify(ID_NOTIFICATION, notification);
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
		switch(userType) {
		case Credentials.TYPE_USER:
			LocationsHelper.sendCollaboratorLocation(userID, location);
			break;
		case Credentials.TYPE_BUS:
			LocationsHelper.sendBusDriverLocation(userID, location);
			break;
		}
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
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
		
}

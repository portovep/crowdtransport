package com.coctelmental.android.project1886;

import com.coctelmental.android.project1886.logic.ControllerLocations;
import com.coctelmental.android.project1886.logic.ControllerUsers;
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
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.widget.Toast;

public class TrackingService extends Service {
	
	public static final String TARGET_CITY="targetCity";
	public static final String TARGET_LINE="targetLine";	
	public static final String CALLER_ACTIVITY = "targetActivity";
	
	public static final int COLLABORATOR_ACTIVITY_ID = 0;
	public static final int BUSDRIVER_ACTIVITY_ID = 1;
	public static final int TAXIDRIVER_ACTIVITY_ID = 2;
	
	private static final int NOTIFICATION_ID = 1;
	private NotificationManager notificationManager;
	
	private static final String PROVIDER = LocationManager.GPS_PROVIDER;
	private static final int TIME_BETWEEN_UPDATES = 10000; // milliseconds
	private static final int DISTANCE_BETWEEN_UPDATES = 50; // meters
	
    private final IBinder serviceBinder = new TrackingServiceBinder(); // Binder given to clients
	
	private LocationManager locationManager;
	private String userID;
	private int userType;
	
	private Location updatedLocation;
	private PendingIntent pendingIntent;
	
	private ControllerUsers controllerU;
	private ControllerLocations controllerL;
			
	
	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		controllerU = new ControllerUsers();
		controllerL = new ControllerLocations();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    // get intent data
    	Bundle extras = intent.getExtras();	    
        int targetActivityID = extras != null ? extras.getInt(CALLER_ACTIVITY) : 0;
        
        Intent notificationIntent;
        // check destiny
        if(targetActivityID == TAXIDRIVER_ACTIVITY_ID)
        	notificationIntent = new Intent(this, TaxiDriverInformationPanel.class);
        else if(targetActivityID == BUSDRIVER_ACTIVITY_ID)
        	notificationIntent = new Intent(this, BusDriverInformationPanel.class);
        else
        	notificationIntent = new Intent(this, CollaboratorInformationPanel.class);
        
        // setup pending intent
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		this.pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);	
		
		// setup new notification
		Notification notification = setupNotification(R.drawable.ic_stat_notify_tracking, getString(R.string.collaborationServiceStarted),
				getString(R.string.app_name), getString(R.string.collaborationServiceRunning), pendingIntent);		
		// launch notification
		notificationManager.notify(NOTIFICATION_ID, notification);
	
		// get userID
	    if(controllerU.existActiveUser()) {
	    	userID = controllerU.getActiveUser().getId();
	    	userType = controllerU.getActiveUser().getType();
	    }
	    else {
	    	// if no user logged, use unique id as identification of this app installation
	    	userID = MyApplication.getInstance().id();
	    	userType = Credentials.TYPE_USER;
	    }
        
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
			Notification notification = setupNotification(R.drawable.ic_stat_notify_tracking, getString(R.string.collaborationServiceRunning),
					getString(R.string.app_name), getString(R.string.collaborationServiceRunning), pendingIntent);
			// update current showed notification
			notificationManager.notify(NOTIFICATION_ID, notification);
		}
		
		@Override
		public void onProviderDisabled(String arg0) {			
			// update notification to alert user
			// setup notification
			Notification notification = setupNotification(R.drawable.ic_stat_notify_tracking_error, getString(R.string.collaborationServiceGPSDisabled), 
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
		switch(userType) {
		case Credentials.TYPE_USER:
			controllerL.sendCollaboratorLocation(userID, location);
			break;
		case Credentials.TYPE_BUS:
			controllerL.sendBusDriverLocation(userID, location);
			break;
		case Credentials.TYPE_TAXI:
			controllerL.sendTaxiDriverLocation(userID, location);
			break;
		}
	}
	
	@Override
	public void onDestroy() {
		// disabling location listener
		locationManager.removeUpdates(collaboratorLocationListener);
		// disabling current notification  
		notificationManager.cancel(NOTIFICATION_ID);
		// notifying the user
		Tools.buildToast(getApplicationContext(), getString(R.string.collaborationServiceFinished), Gravity.CENTER, Toast.LENGTH_SHORT).show();
		// finish service
		stopSelf();
	}
	
    public class TrackingServiceBinder extends Binder {
        public TrackingService getServiceInstance() {
            // Return this instance of LocalService so clients can call public methods
            return TrackingService.this;
        }
    }
	
	@Override
	public IBinder onBind(Intent arg0) {
		return serviceBinder;
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

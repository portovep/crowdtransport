package com.coctelmental.android.project1886;

import com.coctelmental.android.project1886.TrackingService.TrackingServiceBinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BusDriverInformationPanel extends Activity {
	
	private TextView tvCity;
	private TextView tvLine;
	private Button bFinishService;
	
	private String targetCity = null;
	private String targetLine = null;
	
	private TrackingService trackingService = null;
	private boolean isBound = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_driver_information_panel);
				
	    // get data from intent
    	Bundle extras = getIntent().getExtras();
    	if(targetCity == null | targetLine == null) {
	        targetCity = extras != null ? extras.getString(TrackingService.TARGET_CITY) : null;
	        targetLine = extras != null ? extras.getString(TrackingService.TARGET_LINE) : null;
    	}
        
    	// get info labels
        tvCity = (TextView) findViewById(R.id.collaborationInfoCity);
        tvLine = (TextView) findViewById(R.id.collaborationInfoLine);
        
        // setup button to finish service
        bFinishService = (Button) findViewById(R.id.buttonFinishCollaboration);
        bFinishService.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				finishTrackingService();
				goMainMenu();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (!isGPSEnabled())
			showGPSDialog();
		else {
			if(!MyApplication.getInstance().isServiceRunning(TrackingService.class.getName())) {
			    // launch location tracking service
			    Intent i = new Intent(this, TrackingService.class);
			    i.putExtra(TrackingService.TARGET_ACTIVITY, TrackingService.TRACKING_BUS_ID);
			    i.putExtra(TrackingService.TARGET_CITY, targetCity);
			    i.putExtra(TrackingService.TARGET_LINE, targetLine);			    
			    startService(i);
			}
			if(!isBound) {
		        // bind to TrackingService
		        Intent intent = new Intent(this, TrackingService.class);
		        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
			}
		}
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        // unbind from the service
        if(isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }
    
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}
	
	private void goMainMenu() {
		super.onBackPressed();
		finish();
	}

	private boolean isGPSEnabled() {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return false;
		}
		return true;
	}
	
	private void showGPSDialog() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(getString(R.string.failGPSNotFound))
    	       .setCancelable(false)
    	       .setPositiveButton(getString(R.string.enableGPS), new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   dialog.dismiss();
    	        	   Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    	        	   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	        	   startActivity(intent);
	        	   }
    	       })
	           .setNegativeButton(getString(R.string.noEnableGPS), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						finishTrackingService();
						goMainMenu();
	        	   }
	           });	
    	AlertDialog alert = builder.create();
    	alert.show();
	}
	
	private void finishTrackingService() {
		// finish location tracking service
		Intent i = new Intent(getApplicationContext(), TrackingService.class);
		stopService(i);
	}
	
    private ServiceConnection serviceConnection = new ServiceConnection() {
    	// defines callbacks for service binding, passed to bindService()
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// We've bound to trackingService, cast the IBinder and get trackingService instance
            TrackingServiceBinder binder = (TrackingServiceBinder) service;
            trackingService = binder.getServiceInstance();            
            isBound = true;
            
            String city = trackingService.getTargetCity();
            String line = trackingService.getTargetLine();
            if(city != null && line != null) {
        		tvCity.setText(city);
        		tvLine.setText(line);
            }
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			 isBound = false;			
		}
	};
}

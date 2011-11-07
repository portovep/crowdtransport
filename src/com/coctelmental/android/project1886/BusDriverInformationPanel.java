package com.coctelmental.android.project1886;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BusDriverInformationPanel extends Activity {
	
	public static final int BUS_DRIVER_ACTIVITY = 1;
	public static final String BUSDRIVER_SERVICE_EXTRA = "BUSDRIVER_EXTRA";
	
	private TextView tvCity;
	private TextView tvLine;
	private Button bFinishService;
	
	private String targetCity = null;
	private String targetLine = null;	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_driver_information_panel);
				
	    // get data from intent
    	Bundle extras = getIntent().getExtras();
    	if(targetCity == null | targetLine == null) {
	        targetCity = extras != null ? extras.getString(CollaborationLineSelection.TARGET_CITY) : null;
	        targetLine = extras != null ? extras.getString(CollaborationLineSelection.TARGET_LINE) : null;
    	}
        
        if(savedInstanceState != null && (targetCity == null | targetLine == null)) {
        	targetCity = savedInstanceState.getString(CollaborationLineSelection.TARGET_CITY);
        	targetLine = savedInstanceState.getString(CollaborationLineSelection.TARGET_LINE);
        }
        
        // setup collaboration info
        tvCity = (TextView) findViewById(R.id.collaborationInfoCity);
        tvCity.append(" "+targetCity);
        tvLine = (TextView) findViewById(R.id.collaborationInfoLine);
        tvLine.append(" "+targetLine);
        
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
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(CollaborationLineSelection.TARGET_CITY, targetCity);
		outState.putString(CollaborationLineSelection.TARGET_LINE, targetLine);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (!isGPSEnabled())
			showGPSDialog();
		else {
			if(!MyApplication.getInstance().isServiceRunning(CollaborationTrackingService.class.getName())) {
			    // launch location tracking service
			    Intent i = new Intent(this, CollaborationTrackingService.class);
			    i.putExtra(CollaborationLineSelection.TARGET_CITY, targetCity);
			    i.putExtra(CollaborationLineSelection.TARGET_LINE, targetLine);
			    i.putExtra(BUSDRIVER_SERVICE_EXTRA, BUS_DRIVER_ACTIVITY);			    
			    startService(i);
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
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
    	        	   Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    	        	   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	        	   dialog.dismiss();
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
		Intent i = new Intent(getApplicationContext(), CollaborationTrackingService.class);
		stopService(i);
	}
	
	private void goMainMenu() {
		Intent intent = new Intent(this, BusDriverMain.class);
		//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

}

package com.coctelmental.android.project1886.buses;

import com.coctelmental.android.project1886.MyApplication;
import com.coctelmental.android.project1886.R;
import com.coctelmental.android.project1886.TrackingService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BusDriverInformationPanel extends Activity {
	
	private TextView tvCity;
	private TextView tvLine;
	private Button bFinishService;
	private ViewGroup backgroundLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_driver_information_panel);
		
	    // get layout
		backgroundLayout = (LinearLayout) findViewById(R.id.containerBInformationPanel);
				   
    	// get info labels
        tvCity = (TextView) findViewById(R.id.collaborationInfoCity);
        tvLine = (TextView) findViewById(R.id.collaborationInfoLine);

        // get saved info
        String[] storedInfo = MyApplication.getInstance().getStoredTrackingInfo();
        String city = storedInfo[0];
        String line = storedInfo[1];
        // fill info labels
        if(city != null && line != null) {
    		tvCity.setText(city);
    		tvLine.setText(line);
        }
        
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
		
		if (!MyApplication.getInstance().isGPSEnabled()) {
			// hide content
			backgroundLayout.setVisibility(ViewGroup.GONE);
			showGPSDialog();
		}
		else {
			if (backgroundLayout.getVisibility() != ViewGroup.VISIBLE)
				// show content
				backgroundLayout.setVisibility(ViewGroup.VISIBLE);
			if(!MyApplication.getInstance().isServiceRunning(TrackingService.class.getName())) {
			    // launch location tracking service
			    Intent i = new Intent(this, TrackingService.class);
			    i.putExtra(TrackingService.CALLER_ACTIVITY, TrackingService.BUSDRIVER_ACTIVITY_ID);			    
			    startService(i);
			}
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
	
}

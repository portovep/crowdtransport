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

public class CollaborationInformationPanel extends Activity {
	
	private TextView tvCity;
	private TextView tvLine;
	private Button bFinishService;
	
	private String targetCity = null;
	private String targetLine = null;	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collaboration_information_panel);
				
	    // get data from intent
    	Bundle extras = getIntent().getExtras();	    
        targetCity = extras != null ? extras.getString(CollaborationLineSelection.TARGET_CITY) : null;
        targetLine = extras != null ? extras.getString(CollaborationLineSelection.TARGET_LINE) : null;
        
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
	protected void onResume() {
		super.onResume();
		
		if (!isGPSEnabled())
			showGPSDialog();
		else {		
		    // launch location tracking service
		    Intent i = new Intent(this, CollaborationTrackingService.class);
		    i.putExtra(CollaborationLineSelection.TARGET_CITY, targetCity);
		    i.putExtra(CollaborationLineSelection.TARGET_LINE, targetLine);
		    startService(i);
		}
	}

	@Override
	public void onBackPressed() {
		// modify onBackPressed behavior to go directly to main menu
		goMainMenu();
	}
	
	private void goMainMenu() {
		startActivity(new Intent(this, MainActivity.class));
	}
	
	private boolean isGPSEnabled() {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return false;
		}
		return true;
	}
	
	private void showGPSDialog() {
		getWindow().setBackgroundDrawable(null);
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
		Intent i = new Intent(CollaborationInformationPanel.this, CollaborationTrackingService.class);
		stopService(i);
	}

}

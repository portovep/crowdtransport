package com.coctelmental.android.project1886;

import com.coctelmental.android.project1886.c2dm.C2DMRegistrationReceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class TaxiDriverInformationPanel extends Activity{
	
	public static final String ACTION_RECEIVER_REQUEST = "RECEIVER_REQUEST";

	private static final int TTS_CHECK_CODE = 0;
	
	private Button bFinishService;
	private ViewGroup backgroundLayout;
	private TextView tvNumberOfRequest;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taxi_driver_information_panel);
		
		// check TTS (Text to speech) capability
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, TTS_CHECK_CODE);
		
	    // get layout
		backgroundLayout = (LinearLayout) findViewById(R.id.containerBInformationPanel);
		
		tvNumberOfRequest = (TextView) findViewById(R.id.numberOfrequest);
        
        // setup button to finish service
        bFinishService = (Button) findViewById(R.id.buttonFinishCollaboration);
        bFinishService.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// unregister C2DM
				C2DMRegistrationReceiver.register(getApplicationContext());
				
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
			    i.putExtra(TrackingService.CALLER_ACTIVITY, TrackingService.TAXIDRIVER_ACTIVITY_ID);			    
			    startService(i);
			}
			// activate broadcast receiver
			registerReceiver(serviceRequestReceiver, new IntentFilter(ACTION_RECEIVER_REQUEST));
			
		}
	}	
    
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(serviceRequestReceiver);
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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		 *  Check Text to speech capability
		 *  If any language/voice package is needed, send request to install it.
		 */
	    if(requestCode == TTS_CHECK_CODE) {
	        if(resultCode != TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
	        	Log.w("TTS", "Missing TTS data");
	            // missing TTS data, install it
	            Intent installIntent = new Intent();
	            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
	            startActivity(installIntent);
	        }
	    }
	}
	
	private BroadcastReceiver serviceRequestReceiver = new BroadcastReceiver() {
				
		@Override
		public void onReceive(Context context, Intent intent) {
			// get number of request
			int nRequest = Integer.parseInt(tvNumberOfRequest.getText().toString());
			// update it
			nRequest += 1;
			// update in UI
			tvNumberOfRequest.setText(String.valueOf(nRequest));
		}
	};

}

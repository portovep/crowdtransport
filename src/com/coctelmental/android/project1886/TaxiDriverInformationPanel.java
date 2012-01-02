package com.coctelmental.android.project1886;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import com.coctelmental.android.project1886.c2dm.C2DMRegistrationReceiver;
import com.coctelmental.android.project1886.c2dm.C2DMessageReceiver;
import com.coctelmental.android.project1886.common.util.JsonHandler;
import com.coctelmental.android.project1886.logic.ControllerServiceRequests;
import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.model.ServiceRequestInfo;
import com.coctelmental.android.project1886.util.Tools;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class TaxiDriverInformationPanel extends Activity{
	
	public static final String ACTION_RECEIVER_REQUEST = "RECEIVER_REQUEST";

	private static final int TTS_CHECK_CODE = 0;
	
	private Button bFinishService;
	private ViewGroup backgroundLayout;
	private TextView tvNumberOfRequests;
	private AlertDialog gpsAlertDialog;
	
	private ArrayList<ServiceRequestInfo> requests;
		
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
		
		tvNumberOfRequests = (TextView) findViewById(R.id.numberOfrequest);
		
		// setup layout as clickable
		LinearLayout numberOfRequestLayout = (LinearLayout) findViewById(R.id.containerNumberRequests);
		numberOfRequestLayout.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				new GetAllServiceRequestTask().execute();			
			}
		});
        
        // setup button to finish service
        bFinishService = (Button) findViewById(R.id.buttonFinishCollaboration);
        bFinishService.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// launch cancellation async task
				new RejectAllServiceRequestTask().execute();
				// unregister C2DM
				C2DMRegistrationReceiver.unregister(getApplicationContext());
				// stop service
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
				// C2DM register to receive push notifications from web service
				C2DMRegistrationReceiver.register(getApplicationContext());
			    // launch location tracking service
				startTrackingService();
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
		if (gpsAlertDialog == null) {
			// build new alert dialog
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage(getString(R.string.failGPSNotFound))
	    	       .setCancelable(false)
	    	       .setPositiveButton(getString(R.string.enableGPS), new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   dialog.cancel();
	    	        	   Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    	        	   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	        	   startActivity(intent);
		        	   }
	    	       })
		           .setNegativeButton(getString(R.string.noEnableGPS), new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							if(MyApplication.getInstance().isServiceRunning(TrackingService.class.getName())) {
								finishTrackingService();
							}
							goMainMenu();
		        	   }
		           });	
	    	gpsAlertDialog = builder.create();
		}
		gpsAlertDialog.show();
	}
	
	private void startTrackingService() {
	    Intent i = new Intent(this, TrackingService.class);
	    i.putExtra(TrackingService.CALLER_ACTIVITY, TrackingService.TAXIDRIVER_ACTIVITY_ID);			    
	    startService(i);
		// activate broadcast receiver
		registerReceiver(serviceRequestReceiver, new IntentFilter(ACTION_RECEIVER_REQUEST));
	}
	
	private void finishTrackingService() {
		// stop broadcast receiver
		unregisterReceiver(serviceRequestReceiver);
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
	    super.onActivityResult(requestCode, resultCode, data);
	}
	
	private BroadcastReceiver serviceRequestReceiver = new BroadcastReceiver() {
				
		@Override
		public void onReceive(Context context, Intent intent) {
			// get number of requests
			int nRequests = intent.getIntExtra(C2DMessageReceiver.EXTRA_NUMBER_REQUESTS, -1);
			if (nRequests > 0) {
				// update UI
				tvNumberOfRequests.setText(String.valueOf(nRequests));
			}

		}
	};
	
	private class GetAllServiceRequestTask extends AsyncTask<Void, Void, ResultBundle> {
		private ProgressDialog pdDownloadingRequests;
		
		protected void onPreExecute () {
			// show a progress dialog while data is retrieved from server
			pdDownloadingRequests = ProgressDialog.show(TaxiDriverInformationPanel.this, "", getString(R.string.loadingRequests), true);
		}
		
	    protected ResultBundle doInBackground(Void... params) {
	    	// retrieving new request from webservice
	        return ControllerServiceRequests.getAllServiceRequest();
	    }

	    protected void onPostExecute(ResultBundle rb) {    	
	    	pdDownloadingRequests.dismiss();
	        // check result
	        if(rb.getResultCode() == HttpURLConnection.HTTP_OK) {
		    	String jsonRequests = rb.getContent();
				// Obtaining specific object from json codification
				Type listType = new TypeToken<List<ServiceRequestInfo>>() {}.getType();			
		    	requests = JsonHandler.fromJson(jsonRequests, listType);
		    			    	
		    	if (requests != null && !requests.isEmpty()) {
		    		// update UI label
		    		String nRequest = String.valueOf(requests.size());
		    		tvNumberOfRequests.setText(nRequest);
		    		
		    		// setup adapter and dialog
		    		AlertDialog.Builder builder = new AlertDialog.Builder(TaxiDriverInformationPanel.this);
		    		builder.setTitle(R.string.chooseServiceRequest);
		    		int resID = R.layout.service_request_view;
		    		ServiceRequestAdapter adapter = new ServiceRequestAdapter(TaxiDriverInformationPanel.this,
		    				resID, requests);
		    		
		    		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
		    		    public void onClick(DialogInterface dialog, int item) {
		    		    	// parse data to send with intent
		    		    	ServiceRequestInfo selectedRequest = requests.get(item);
		    		    	String jsonServiceRequest = JsonHandler.toJson(selectedRequest);

		    		    	Intent intent = new Intent(getApplicationContext(), TaxiDriverRouteView.class);
		    		    	// attach JSON data to intent
		    		    	intent.putExtra(TaxiDriverRouteView.SERVICE_REQUEST, jsonServiceRequest);
		    		    	startActivity(intent);
		    		    }
		    		});
		    		
		    		AlertDialog alert = builder.create();
		    		alert.show();
		    	}
	        }				
	        else {
				// default message = error server not found
				String message = getString(R.string.failServerNotFound);
				Log.d("SERVICE REQUESTS", "Http error code" + Integer.toString(rb.getResultCode()));
				if (rb.getResultCode() == HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
					// no request available
		    		message = getString(R.string.serviceRequestsNotFound);
		    		// reset label
		    		tvNumberOfRequests.setText("0");
		    		Log.d("SERVICE REQUESTS", "Service requests found -> 0");
				}
				Toast toast = Tools.buildToast(TaxiDriverInformationPanel.this, message, Gravity.CENTER, Toast.LENGTH_SHORT);
				toast.show();				
	        }
	    }
	}
	
	private class RejectAllServiceRequestTask extends AsyncTask<Void, Void, Integer> {
		
	    protected Integer doInBackground(Void... params) {
	        return ControllerServiceRequests.rejectAllServiceRequest();
	    }

	    protected void onPostExecute(Integer result) {    	
	        // check result
	        if(result == HttpURLConnection.HTTP_OK) {
	        	Log.w("TaxiServiceRequests", "All service request cancelled");
	        }				
	        else {
	        	Log.w("TaxiServiceRequests", "Error trying cancel all service request" +
	        			"Error code -> (" + result + ")");
	        }
	    }
	}

}

package com.coctelmental.android.project1886.c2dm;

import java.net.HttpURLConnection;

import com.coctelmental.android.project1886.R;
import com.coctelmental.android.project1886.TaxiDriverInformationPanel;
import com.coctelmental.android.project1886.common.util.JsonHandler;
import com.coctelmental.android.project1886.logic.ControllerServiceRequests;
import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.model.ServiceRequestInfo;
import com.coctelmental.android.project1886.tts.TextToSpeechService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class C2DMessageReceiver extends BroadcastReceiver{
	
	public static final String C2DM_RECEIVE_INTENT = "com.google.android.c2dm.intent.RECEIVE";
	
	private static final String TAXI_NOTIFICATION_PAYLOAD = "notify_taxiDriver";
	private static final String USER_NOTIFICATION_PAYLOAD = "notify_user";
	
	private Context mContext;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if (action.equals(C2DM_RECEIVE_INTENT)) {
			Log.d("C2DM", "Message received");
			
			// get payload data
			String taxiNotificationData = intent.getStringExtra(TAXI_NOTIFICATION_PAYLOAD);
			String userNotificationData = intent.getStringExtra(USER_NOTIFICATION_PAYLOAD);
			
			// check notification type
			if (taxiNotificationData != null) {
				Log.d("C2DM", "Message type -> Taxi Driver notification");
				handleTaxiNotification(context, taxiNotificationData);
			}
			else if (userNotificationData != null) {
				Log.d("C2DM", "Message type -> User notification");
				handleUserNotification(context);
			}
		}		
	}
	
	private void handleTaxiNotification(Context context, String requestID) {
		// TO-DO
		
		// save context to use it in async task
		mContext = context;
		
		// launch async task to retrieve request info
		new GetNewServiceRequestTask().execute(requestID);
		
		// notify activity
		Intent intent = new Intent(TaxiDriverInformationPanel.ACTION_RECEIVER_REQUEST);
		context.sendBroadcast(intent);
	}
	
	private void handleUserNotification(Context context) {
		// TO-DO	
	}
	
	private class GetNewServiceRequestTask extends AsyncTask<String, Void, ResultBundle> {
	
	    protected ResultBundle doInBackground(String... params) {
	    	// retrieving new request from webservice
	        return ControllerServiceRequests.getNewServiceRequest(params[0]);
	    }

	    protected void onPostExecute(ResultBundle rb) {
	        // check result
	        if(rb.getResultCode() == HttpURLConnection.HTTP_OK) {
		    	String jsonServiceRequest = rb.getContent();
		    	Log.d("GETTING SERVICE REQUEST", "Json request ->" + jsonServiceRequest);
				// parse JSON data	
		    	ServiceRequestInfo newServiceRequest = JsonHandler.fromJson(jsonServiceRequest, ServiceRequestInfo.class);
		    			    	
		    	if (newServiceRequest != null) {
					Intent ttsIntent = new Intent(mContext, TextToSpeechService.class);
					
					// setup TTS message
					String ttsMessage = mContext.getString(R.string.newIncomingRequestTTS);
					
					// attach TTS message
					ttsIntent.putExtra(TextToSpeechService.TTS_MESSAGE, ttsMessage);
					// call service to launch TTS (text to speech) task
					mContext.startService(ttsIntent);	
		    	}
		    	else {
		    		Log.w("GETTING SERVICE REQUEST", "Error retrieving service request data");
		    	}
	        }				
	        else {
				Log.w("GETTING SERVICE REQUEST", "Http error code -> " +Integer.toString(rb.getResultCode()));
	        }
	    }
	}
	
}

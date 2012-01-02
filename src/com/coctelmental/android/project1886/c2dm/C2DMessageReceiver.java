package com.coctelmental.android.project1886.c2dm;

import com.coctelmental.android.project1886.R;
import com.coctelmental.android.project1886.TaxiDriverInformationPanel;
import com.coctelmental.android.project1886.UserTaxiWaitingPanel;
import com.coctelmental.android.project1886.tts.TextToSpeechService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class C2DMessageReceiver extends BroadcastReceiver{
	
	public static final String C2DM_RECEIVE_INTENT = "com.google.android.c2dm.intent.RECEIVE";
	
	private static final String TAXI_NOTIFICATION_PAYLOAD = "notify_taxiDriver";
	private static final String TAXI_NUMBER_REQUESTS_PAYLOAD = "taxiDriver_number_requests";
	private static final String USER_NOTIFICATION_PAYLOAD = "notify_user";
	
	public static final String USER_PAYLOAD_ACCEPT = "accept";
	public static final String USER_PAYLOAD_CANCEL = "cancel";
	
	public static final String EXTRA_NUMBER_REQUESTS = "e_number_of_requests";
	public static final String EXTRA_TAXI_RESPONSE = "e_taxi_response";	
	
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
				// get number of request
				int nRequests = 0;
				String sNumberRequests = intent.getStringExtra(TAXI_NUMBER_REQUESTS_PAYLOAD);
				if (sNumberRequests != null && !sNumberRequests.equals(""))
					nRequests = Integer.parseInt(sNumberRequests);
				Log.d("C2DM", "Message type -> Taxi Driver notification");
				Log.d("C2DM", "Number of requests -> " + nRequests);
				handleTaxiNotification(context, taxiNotificationData, nRequests);
			}
			else if (userNotificationData != null) {
				Log.d("C2DM", "Message type -> User notification");
				handleUserNotification(context, userNotificationData);
			}
		}		
	}
	
	private void handleTaxiNotification(Context context, String serviceRequestData, int nRequests) {
		// save context
		mContext = context;
		
		// launch TTS
		textToSpeech(serviceRequestData);
		
		// notify activity
		Intent intent = new Intent(TaxiDriverInformationPanel.ACTION_RECEIVER_REQUEST);
		intent.putExtra(EXTRA_NUMBER_REQUESTS, nRequests);
		context.sendBroadcast(intent);
	}
	
	private void handleUserNotification(Context context, String payloadData) {		
		// check payload data
		if (payloadData.equals(USER_PAYLOAD_ACCEPT)) {
			Log.d("C2DM", "Message: Service request accepted");
			// notify activity
			Intent intent = new Intent(UserTaxiWaitingPanel.ACTION_REQUEST_RESPONSE_RECEIVER);
			intent.putExtra(EXTRA_TAXI_RESPONSE, USER_PAYLOAD_ACCEPT);
			context.sendBroadcast(intent);
		}
		else if (payloadData.equals(USER_PAYLOAD_CANCEL)) {
			Log.d("C2DM", "Message: Service request canceled");
			// notify activity
			Intent intent = new Intent(UserTaxiWaitingPanel.ACTION_REQUEST_RESPONSE_RECEIVER);
			intent.putExtra(EXTRA_TAXI_RESPONSE, USER_PAYLOAD_CANCEL);
			context.sendBroadcast(intent);
		}
		else {
			Log.d("C2DM", "Message: unknow");			
		}
	}
	
	private void textToSpeech(String serviceRequestDATA) {
		// setup TTS message
		String ttsMessage = mContext.getString(R.string.newIncomingRequestTTS);

		Intent ttsIntent = new Intent(mContext, TextToSpeechService.class);
		// attach TTS message
		ttsIntent.putExtra(TextToSpeechService.TTS_MESSAGE, ttsMessage);
		// call service to launch TTS (text to speech) task
		mContext.startService(ttsIntent);
	}
	
}

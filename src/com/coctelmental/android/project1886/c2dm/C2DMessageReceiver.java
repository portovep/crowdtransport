package com.coctelmental.android.project1886.c2dm;

import com.coctelmental.android.project1886.R;
import com.coctelmental.android.project1886.main.Preferences;
import com.coctelmental.android.project1886.taxis.TaxiDriverInformationPanel;
import com.coctelmental.android.project1886.taxis.UserTaxiWaitingPanel;
import com.coctelmental.android.project1886.tts.TextToSpeechMain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class C2DMessageReceiver extends BroadcastReceiver{
	
	private static final String C2DM_RECEIVE_INTENT = "com.google.android.c2dm.intent.RECEIVE";
	
	private static final String PAYLOAD_TAXI_ADDRESS_FROM = "notification_addressFrom_name";
	private static final String PAYLOAD_TAXI_ADDRESS_TO = "notification_addressTo_name";
	private static final String PAYLOAD_TAXI_COMMENT = "notification_commnet";
	private static final String PAYLOAD_TAXI_NUMBER_REQUESTS = "taxiDriver_number_requests";
	
	private static final String PAYLOAD_USER_NOTIFICATION = "notify_user";	
	public static final String PAYLOAD_USER_RESPONSE_ACCEPT = "accept";
	public static final String PAYLOAD_USER_RESPONSE_CANCEL = "cancel";
	
	public static final String EXTRA_NUMBER_REQUESTS = "e_number_of_requests";
	public static final String EXTRA_TAXI_RESPONSE = "e_taxi_response";

	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if (action.equals(C2DM_RECEIVE_INTENT)) {
			Log.d("C2DM", "Message received");
			
			// get payload data
			String taxiNotificationData = intent.getStringExtra(PAYLOAD_TAXI_ADDRESS_FROM);
			String userNotificationData = intent.getStringExtra(PAYLOAD_USER_NOTIFICATION);
			
			// check notification type
			if (taxiNotificationData != null) {
				// get address names
				String addressFrom = taxiNotificationData;
				String addressTo = intent.getStringExtra(PAYLOAD_TAXI_ADDRESS_TO);
				
				// get request comment
				String requestComment = intent.getStringExtra(PAYLOAD_TAXI_COMMENT);
				
				// get number of request
				int nRequests = 0;
				String sNumberRequests = intent.getStringExtra(PAYLOAD_TAXI_NUMBER_REQUESTS);
				if (sNumberRequests != null && !sNumberRequests.equals(""))
					nRequests = Integer.parseInt(sNumberRequests);
				
				Log.d("C2DM", "Message type -> Taxi Driver notification");
				Log.d("C2DM", "AddressFrom -> " + addressFrom);
				Log.d("C2DM", "AddressTo -> " + addressTo);
				Log.d("C2DM", "Comment -> " + requestComment);
				Log.d("C2DM", "Number of requests -> " + nRequests);
				
				handleTaxiNotification(context, addressFrom, addressTo, requestComment, nRequests);
			}
			else if (userNotificationData != null) {
				Log.d("C2DM", "Message type -> User notification");
				handleUserNotification(context, userNotificationData);
			}
		}		
	}
	
	private void handleTaxiNotification(Context context, String addressFrom, String addressTo, String requestComment, int nRequests) {
		
        // check user preferences
	    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
	    String incomingRequestMessage = sp.getString(Preferences.PREF_TAXI_NEW_REQUEST_MESSAGE,
	    		context.getString(R.string.newIncomingRequestTTS));
	    
	    boolean playDestination = sp.getBoolean(Preferences.PREF_TAXI_PLAY_DEST,
	    		Preferences.DEFAULT_TAXI_PLAY_DEST);
	    boolean playComment = sp.getBoolean(Preferences.PREF_TAXI_PLAY_COMMENT,
	    		Preferences.DEFAULT_TAXI_PLAY_COMMENT);
		
		
		// create tts message
		StringBuilder sb = new StringBuilder();
		sb.append(incomingRequestMessage);
		sb.append(".");
		if (addressFrom != null && !addressFrom.equals("")) {
			sb.append(context.getString(R.string.originTTS));
			sb.append(addressFrom);
			sb.append(".");
		}
		if (playDestination && addressTo != null && !addressTo.equals("")) {
			sb.append(context.getString(R.string.destinationTTS));
			sb.append(addressTo);
			sb.append(".");
		}
		if (playComment && requestComment != null && !requestComment.equals("")) {
			sb.append(context.getString(R.string.clarificationCommentTTS));
			sb.append(requestComment);
			sb.append(".");
		}
		String ttsMessage = sb.toString();
		
		// launch TTS
		TextToSpeechMain.playMessage(context, ttsMessage);
		
		// notify activity
		Intent intent = new Intent(TaxiDriverInformationPanel.ACTION_RECEIVE_REQUEST);
		intent.putExtra(EXTRA_NUMBER_REQUESTS, nRequests);
		context.sendBroadcast(intent);
	}
	
	private void handleUserNotification(Context context, String payloadData) {		
		// check payload data
		if (payloadData.equals(PAYLOAD_USER_RESPONSE_ACCEPT)) {
			Log.d("C2DM", "Message: Service request accepted");
			// notify activity
			Intent intent = new Intent(UserTaxiWaitingPanel.ACTION_RECEIVE_REQUEST_RESPONSE);
			intent.putExtra(EXTRA_TAXI_RESPONSE, PAYLOAD_USER_RESPONSE_ACCEPT);
			context.sendBroadcast(intent);
		}
		else if (payloadData.equals(PAYLOAD_USER_RESPONSE_CANCEL)) {
			Log.d("C2DM", "Message: Service request canceled");
			// notify activity
			Intent intent = new Intent(UserTaxiWaitingPanel.ACTION_RECEIVE_REQUEST_RESPONSE);
			intent.putExtra(EXTRA_TAXI_RESPONSE, PAYLOAD_USER_RESPONSE_CANCEL);
			context.sendBroadcast(intent);
		}
		else {
			Log.d("C2DM", "Message: unknown");			
		}
	}
	
	
}

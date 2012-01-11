package com.coctelmental.android.project1886.c2dm;

import com.coctelmental.android.project1886.R;
import com.coctelmental.android.project1886.TaxiDriverInformationPanel;
import com.coctelmental.android.project1886.UserTaxiWaitingPanel;
import com.coctelmental.android.project1886.tts.TextToSpeechMain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class C2DMessageReceiver extends BroadcastReceiver{
	
	public static final String C2DM_RECEIVE_INTENT = "com.google.android.c2dm.intent.RECEIVE";
	
	private static final String TAXI_ADDRESS_FROM_PAYLOAD = "notification_addressFrom_name";
	private static final String TAXI_ADDRESS_TO_PAYLOAD = "notification_addressTo_name";
	private static final String TAXI_COMMENT_PAYLOAD = "notification_commnet";
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
			String taxiNotificationData = intent.getStringExtra(TAXI_ADDRESS_FROM_PAYLOAD);
			String userNotificationData = intent.getStringExtra(USER_NOTIFICATION_PAYLOAD);
			
			// check notification type
			if (taxiNotificationData != null) {
				// get address names
				String addressFrom = taxiNotificationData;
				String addressTo = intent.getStringExtra(TAXI_ADDRESS_TO_PAYLOAD);
				
				// get request comment
				String requestComment = intent.getStringExtra(TAXI_COMMENT_PAYLOAD);
				
				// get number of request
				int nRequests = 0;
				String sNumberRequests = intent.getStringExtra(TAXI_NUMBER_REQUESTS_PAYLOAD);
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
		// save context
		mContext = context;
		
		// create tts message
		StringBuilder sb = new StringBuilder();
		sb.append(mContext.getString(R.string.newIncomingRequestTTS));
		if (addressFrom != null && !addressFrom.equals("")) {
			sb.append(mContext.getString(R.string.originTTS));
			sb.append(addressFrom);
			sb.append(".");
		}
		if (addressTo != null && !addressTo.equals("")) {
			sb.append(mContext.getString(R.string.destinationTTS));
			sb.append(addressTo);
			sb.append(".");
		}
		if (requestComment != null && !requestComment.equals("")) {
			sb.append(mContext.getString(R.string.clarificationCommentTTS));
			sb.append(requestComment);
			sb.append(".");
		}
		String ttsMessage = sb.toString();
		
		// launch TTS
		TextToSpeechMain.playMessage(mContext, ttsMessage);
		
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
			Log.d("C2DM", "Message: unknown");			
		}
	}
	
	
}

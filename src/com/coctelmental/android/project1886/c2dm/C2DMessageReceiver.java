package com.coctelmental.android.project1886.c2dm;

import com.coctelmental.android.project1886.TaxiDriverInformationPanel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class C2DMessageReceiver extends BroadcastReceiver{
	
	public static final String C2DM_RECEIVE_INTENT = "com.google.android.c2dm.intent.RECEIVE";
	
	private static final String TAXI_NOTIFICATION_PAYLOAD = "notify_taxiDriver";
	private static final String USER_NOTIFICATION_PAYLOAD = "notify_user";	
	
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
				Log.d("C2DM", "Message type -> Notify to taxi driver");
				handleTaxiNotification(context);
			}
			else if (userNotificationData != null) {
				Log.d("C2DM", "Message type -> Notify to user");
				handleUserNotification(context);
			}
		}
		
	}
	
	private void handleTaxiNotification(Context context) {
		// TO-DO
		
		// notify activity
		Intent intent = new Intent(TaxiDriverInformationPanel.ACTION_RECEIVER_REQUEST);
		context.sendBroadcast(intent);
	}
	
	private void handleUserNotification(Context context) {
		// TO-DO	
	}
	
 
}

package com.coctelmental.android.project1886.c2dm;

import java.net.HttpURLConnection;

import com.coctelmental.android.project1886.helpers.ServiceRequestsHelper;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;


public class C2DMRegistrationReceiver extends BroadcastReceiver{
	
	private static final String C2DM_REGISTRATION_ACTION = "com.google.android.c2dm.intent.REGISTRATION";
	private static final String C2DM_REGISTRATION_INTENT = "com.google.android.c2dm.intent.REGISTER";
	private static final String C2DM_UNREGISTRATION_INTENT = "com.google.android.c2dm.intent.UNREGISTER";
	
    // extras in registration callback intents.
    private static final String EXTRA_REGISTRATION_ID = "registration_id";
    private static final String EXTRA_UNREGISTERED = "unregistered";
    private static final String EXTRA_ERROR = "error";
    
    // extras in registration intents
    private static final String EXTRA_SENDER = "sender";
    private static final String EXTRA_APPLICATION_PENDING_INTENT = "app";
    private static final String SENDER_ID = "project1886@gmail.com";

	@Override
	public void onReceive(Context context, Intent intent) {
	    if (intent.getAction().equals(C2DM_REGISTRATION_ACTION)) {
	    	handleRegistration(context, intent);
	    }
	}
	
	private void handleRegistration(Context context, Intent intent) {
		 String registrationID = intent.getStringExtra(EXTRA_REGISTRATION_ID);
		 
		 if (intent.getStringExtra(EXTRA_ERROR) != null) {
			 String errorCode = intent.getStringExtra(EXTRA_ERROR);
			 // Registration failed, should try again later.
			 Log.e("C2DM", "Error in C2DM registration process: " + errorCode);
			 if (errorCode.equals("SERVICE_NOT_AVAILABLE"))
				 // try again
				 register(context);
		 }
		 else if (intent.getStringExtra(EXTRA_UNREGISTERED) != null)
			 // unregistration done, new messages from the authorized sender will be rejected
			 Log.d("C2DM", "Device succesful unregistered");
		 else if (registrationID != null) {
			 Log.d("C2DM", "Device succesful registered");
			 // register device in webservice
			 new SendRegistrationIDTask(registrationID).execute(0);
		 }

	}

    public static void register(Context context) {
        Intent registrationIntent = new Intent(C2DM_REGISTRATION_INTENT);
        registrationIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT,
        		 PendingIntent.getBroadcast(context, 0, new Intent(), 0));
        registrationIntent.putExtra(EXTRA_SENDER, SENDER_ID);
        // Initiate c2d messaging registration for the current application
        context.startService(registrationIntent);
    }

    public static void unregister(Context context) {
        Intent unRegistrationIntent = new Intent(C2DM_UNREGISTRATION_INTENT);
        unRegistrationIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT, PendingIntent.getBroadcast(context,
                0, new Intent(), 0));
        // unregister the application. New messages will be blocked by server.
        context.startService(unRegistrationIntent);
        // unregister device in webservice
        new RemoveDeviceInfoTask().execute();
    }
    
	private class SendRegistrationIDTask extends AsyncTask<Integer, Void, Integer> {
	
		private String registrationID;
		private Integer attempts;
		
		public SendRegistrationIDTask(String registrationID) {
			this.registrationID = registrationID;
		}
		
	    protected Integer doInBackground(Integer... params) {
	    	// save number of attempts
	    	attempts = params[0];
		    return ServiceRequestsHelper.sendRegistrationID(registrationID);	    	
	    }

	    protected void onPostExecute(Integer result) {
	        // check result	    	
	        if(result == HttpURLConnection.HTTP_OK) {
	        	Log.w("C2DM", "Succesful device registration in webservice.");
	        }				
	        else {
	        	// check attempts
	        	if (attempts != null && attempts < 6) {
		        	Log.e("C2DM", "Error trying device registration in webservice."
		        			+ "Error code -> (" + result +") "
		        			+ "Attempt -> " + attempts);
		        	// try again
		        	new SendRegistrationIDTask(this.registrationID).execute(attempts+1);
		        	Log.e("C2DM", "Trying device registration again");
	        	}
	        	else {
		        	Log.e("C2DM", "Error trying device registration in webservice after "
		        			+ attempts + " attempts.");
	        	}
	        }
	    }
	}
	
	private static class RemoveDeviceInfoTask extends AsyncTask<Void, Void, Integer> {
		
	    protected Integer doInBackground(Void... params) {
	    	// remove device info stored in server
	        return ServiceRequestsHelper.removeDeviceInfo();
	    }

	    protected void onPostExecute(Integer result) {
	        // check result
	        if(result == HttpURLConnection.HTTP_OK) {
	        	Log.w("C2DM", "Succesful unregistration in webservice.");
	        }				
	        else {
	        	Log.e("C2DM", "Error trying device unregistration in webservice." +
	        			"Error code -> (" + result +")");
	        }
	    }
	}
	
}

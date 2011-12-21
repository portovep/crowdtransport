package com.coctelmental.android.project1886;

import java.net.HttpURLConnection;

import com.coctelmental.android.project1886.c2dm.C2DMRegistrationReceiver;
import com.coctelmental.android.project1886.logic.ControllerServiceRequests;
import com.coctelmental.android.project1886.model.ServiceRequestInfo;
import com.coctelmental.android.project1886.util.Tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class UserTaxiWaitingPanel extends Activity {

	private ProgressBar progressBar;
	private CountDownTimer countDown;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_taxi_waiting_panel);		
		
		// get request info
		ControllerServiceRequests controllerSR = new ControllerServiceRequests();
		ServiceRequestInfo serviceRequest = controllerSR.getServiceRequest();
		String targetTaxiDriverName = serviceRequest.getTaxiDriverID();
        
        // fill taxi driver name label
        TextView tvTaxiDriverName = (TextView) findViewById(R.id.tvTaxiDriverName);
        tvTaxiDriverName.setText(targetTaxiDriverName);
        
        // setup cancel button
        Button bCancelRequest = (Button) findViewById(R.id.bCancelRequest);
        bCancelRequest.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// cancel request in webservice
				new CancelServiceRequestTask().execute();
			}
		});
        
        // setup progress bar
        progressBar = (ProgressBar) findViewById(R.id.waitingProgressBar);
        
        int total = serviceRequest.getRequestLifeTime() * 60 * 1000;
        progressBar.setMax(total);
        progressBar.setProgress(total);
        
        // setup countdown
        countDown = new CountDownTimer(total, 1000) {
        	int total;
        	
            public void onTick(long millisUntilFinished) {
            	total = (int) millisUntilFinished;
            	// update progress in UI
            	progressBar.setProgress(total);
            }

            public void onFinish() {
            	progressBar.setVisibility(View.GONE);
            	Tools.buildToast(getApplicationContext(), "DONE", Gravity.CENTER, Toast.LENGTH_SHORT).show();
            	// TO-DO cancel request?
            }
         };
         
        countDown.start();
	}
	
	
	private class CancelServiceRequestTask extends AsyncTask<Void, Void, Integer> {
		private ProgressDialog pdSendingRequests;
		
		protected void onPreExecute () {
			// show a progress dialog while data is retrieved from server
			pdSendingRequests = ProgressDialog.show(UserTaxiWaitingPanel.this, "", getString(R.string.cancelRequestInProgress), true);
		}
		
	    protected Integer doInBackground(Void... params) {
	        return ControllerServiceRequests.cancelSentServiceRequest();
	    }

	    protected void onPostExecute(Integer result) {
	    	pdSendingRequests.dismiss();
	        // check result
	        if(result == HttpURLConnection.HTTP_OK) {	        	
	        	Log.w("ServiceRequest", "ServiceRequest cancelled");
	        	
				// unregister C2DM
				C2DMRegistrationReceiver.unregister(getApplicationContext());
				// stop countDown
				countDown.cancel();
	        	
				Tools.buildToast(getApplicationContext(), getString(R.string.requestCanceled), Gravity.CENTER, Toast.LENGTH_SHORT).show();
				// go to main
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				finish();
	        }				
	        else {
				// default message = server not found
				String message = getString(R.string.failServerNotFound);
				if (result == HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
					message = getString(R.string.failCancelRequest);
				}
				else if (result == ControllerServiceRequests.ERROR_MALFORMED_REQUEST) {
					message = getString(R.string.failMalformedServiceRequest);
				}
				Toast toast = Tools.buildToast(UserTaxiWaitingPanel.this, message, Gravity.CENTER, Toast.LENGTH_SHORT);
				toast.show();	
	        	Log.w("ServiceRequest", "Error trying cancel serviceRequest in webservice" +
	        			"Error code -> (" + result + ")");
	        }
	    }
	}
	
}

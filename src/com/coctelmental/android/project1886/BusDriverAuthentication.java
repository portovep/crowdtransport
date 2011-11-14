package com.coctelmental.android.project1886;

import java.net.HttpURLConnection;

import com.coctelmental.android.project1886.common.BusDriver;
import com.coctelmental.android.project1886.common.util.JsonHandler;
import com.coctelmental.android.project1886.logic.ControllerUsers;
import com.coctelmental.android.project1886.model.Credentials;
import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.util.Tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BusDriverAuthentication extends Activity {
	
	private EditText etUserID;
	private EditText etPassword;
	private Button bLogin;
	
	private String userID;
	private String password;
	
	private ControllerUsers controllerU;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_driver_authentication);
        
        // get a controller instance
        controllerU = new ControllerUsers();
        
        etUserID = (EditText) findViewById(R.id.userID);
        etPassword = (EditText) findViewById(R.id.password);
        
        bLogin = (Button) findViewById(R.id.buttonLogin);
        bLogin.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {				
				// get written data
				userID = etUserID.getText().toString();
				password = etPassword.getText().toString();				
				// check data
				if(userID.equals("") || password.equals(""))
					Tools.buildToast(getApplicationContext(), getString(R.string.missingFields),
							Gravity.CENTER, Toast.LENGTH_LONG).show();	
				else 
					// launch Async Task to attempt to authenticate the user
					new AuthenticationAsyncTask().execute(userID);
			}
		});        
    }
	
	private class AuthenticationAsyncTask extends AsyncTask<String, Void, ResultBundle> {
		private ProgressDialog pdprocessingAuthentication;
		
		protected void onPreExecute () {
			// show a progress dialog while data is retrieved from the server
			pdprocessingAuthentication = ProgressDialog.show(BusDriverAuthentication.this, "", getString(R.string.processingUserAuthentication), true);
		}
	    /** The system calls this to perform work in a worker thread and
	      * delivers it the parameters given to AsyncTask.execute() */		
	    protected ResultBundle doInBackground(String... params) {
			// send request to the server and return response code
	        return tryAuthentication(params[0]);
	    }	    
	    /** The system calls this to perform work in the UI thread and delivers
	      * the result from doInBackground() */
	    protected void onPostExecute(ResultBundle rb) {
	    	// disable the progress dialog
	        pdprocessingAuthentication.dismiss();
			// calculate password digest
			String passwordDigest = controllerU.passwordToDigest(password);					
			// check result
			if (rb.getResultCode() == HttpURLConnection.HTTP_OK) {
				String jsonUser = rb.getContent();
				BusDriver busDriver = JsonHandler.fromJson(jsonUser, BusDriver.class);
				if (busDriver.getPassword().equals(passwordDigest)) {
					// setup new user credentials
					Credentials credentials=new Credentials(userID, passwordDigest, Credentials.TYPE_BUS);
					// log in
					controllerU.logIn(credentials);
					// information panel
					Tools.buildToast(getApplicationContext(), getString(R.string.correctLogin),
							Gravity.CENTER, Toast.LENGTH_SHORT).show();						
					// go to bus driver main activity
					Intent i = new Intent(getApplicationContext(), BusDriverMain.class);
					startActivity(i);
					finish();
				}
				else
					// error: passwords don't match
					Tools.buildToast(getApplicationContext(), getString(R.string.failLoginInvalidData),
							Gravity.CENTER, Toast.LENGTH_LONG).show();	
			}
			else {
				// default message = error server not found
				String errorMessage = getString(R.string.failServerNotFound);
				Log.e("Http error code", Integer.toString(rb.getResultCode()));
				if (rb.getResultCode() == HttpURLConnection.HTTP_NOT_ACCEPTABLE)
					// error: target user doesn't exist
					errorMessage = getString(R.string.failLoginInvalidData);
				Tools.buildToast(getApplicationContext(), errorMessage,
						Gravity.CENTER, Toast.LENGTH_LONG).show();							
			}					
		}
	}
	
	private ResultBundle tryAuthentication(String userID) {
		ResultBundle rb = null;
		rb = controllerU.getBusDriver(userID);
		return rb;
	}
	
}	

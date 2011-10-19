package com.coctelmental.android.project1886;

import java.net.HttpURLConnection;


import com.coctelmental.android.project1886.common.BusDriver;
import com.coctelmental.android.project1886.logic.ControllerUsers;
import com.coctelmental.android.project1886.model.Credentials;
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

public class RegistrationBus extends Activity {
	
	private EditText etDNI;
	private EditText etFullName;
	private EditText etPassword;
	private EditText etPassword2;
	private EditText etEmail;
	private EditText etCompanyCIF;
	private EditText etCompanyAuthCode;
	private Button bSend;
	
	private String fullName;
	private String dni;
	private String password;
	private String password2;
	private String email;
	private String companyCIF;
	private String companyAuthCode;
	
	private ControllerUsers controllerU;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_bus);
        
        // get a instance of our controller
        controllerU = new ControllerUsers();
        
        // obtain views
        etFullName= (EditText) findViewById(R.id.fullName);
        etDNI= (EditText) findViewById(R.id.dni);
        etPassword= (EditText) findViewById(R.id.password);
        etPassword2= (EditText) findViewById(R.id.password2);
        etEmail= (EditText) findViewById(R.id.email);
        etCompanyCIF = (EditText) findViewById(R.id.cif);
        etCompanyAuthCode= (EditText) findViewById(R.id.authCode);
        
        bSend= (Button) findViewById(R.id.buttonRegisterBus);
        
        bSend.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// get data from views
				fullName= etFullName.getText().toString();
				dni= etDNI.getText().toString();
				password= etPassword.getText().toString();
				password2= etPassword2.getText().toString();
				email= etEmail.getText().toString();
				companyCIF= etCompanyCIF.getText().toString();
				companyAuthCode= etCompanyAuthCode.getText().toString();
				
				// looking for invalid data
				if(fullName.equals("") || dni.equals("") || password.equals("") || password2.equals("") || email.equals("") ||
						companyCIF.equals("") || companyAuthCode.equals(""))
					Tools.buildToast(getApplicationContext(), getString(R.string.missingFields),
							Gravity.CENTER, Toast.LENGTH_SHORT).show();
				else if (!password.equals(password2))
					// passwords doesn't match
					Tools.buildToast(getApplicationContext(), getString(R.string.differentPasswords),
							Gravity.CENTER, Toast.LENGTH_SHORT).show();
				else
					// launch Async Task to try to register user
					new RegistrationAsyncTask().execute();
			}
		});
    }
	
	private class RegistrationAsyncTask extends AsyncTask<Void, Void, Integer> {
		private ProgressDialog pdprocessingRegistration;
		private BusDriver busDriver;
		
		protected void onPreExecute () {
			// show a progress dialog while data is retrieved from the server
			pdprocessingRegistration = ProgressDialog.show(RegistrationBus.this, "", getString(R.string.processingUserRegistration), true);
		}
	    /** The system calls this to perform work in a worker thread and
	      * delivers it the parameters given to AsyncTask.execute() */		
	    protected Integer doInBackground(Void... params) {
			// create a busDriver instance with registration data
			busDriver= new BusDriver(dni, fullName, controllerU.passwordToDigest(password), email,
					companyCIF, companyAuthCode);
	    	// send request to the server and return response code
	        return tryRegistration(busDriver);
	    }	    
	    /** The system calls this to perform work in the UI thread and delivers
	      * the result from doInBackground() */
	    protected void onPostExecute(Integer responseStatus) {
	    	// disable the progress dialog
	        pdprocessingRegistration.dismiss();
			// check response
			if(responseStatus == HttpURLConnection.HTTP_OK) {
				// add registered user as active user (auto log in after registration)
				Credentials credentials = new Credentials(busDriver.getFullName(), busDriver.getPassword(), Credentials.TYPE_BUS);
				controllerU.logIn(credentials);
				// show message to the user
				Tools.buildToast(getApplicationContext(), getString(R.string.correctRegister),
						Gravity.CENTER, Toast.LENGTH_SHORT).show();
				// go to main menu  ---- CAMBIAR -----
				Intent i = new Intent(RegistrationBus.this, MainActivity.class);
				// add flag to clear this activity from the top of Android activity stack
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);	
			}
			// no valid registration
			else {
				String errorMessage = getString(R.string.failServerNotFound);
				Log.e("Http error code", Integer.toString(responseStatus));
				// setup the error message based on the server response state
				if(responseStatus == HttpURLConnection.HTTP_CONFLICT)
					 // there is a user with the same dni in the bbdd
					 errorMessage = getString(R.string.failRegisterInvalidDNI);
				else if(responseStatus == HttpURLConnection.HTTP_PRECON_FAILED)
					// the registration auth code is not valid
					errorMessage = getString(R.string.failRegisterInvalidCompanyAuthCode);
				else if (responseStatus == HttpURLConnection.HTTP_NOT_ACCEPTABLE)
					errorMessage = getString(R.string.failRegister);
				// show message to the user
				Tools.buildToast(getApplicationContext(), errorMessage,
						Gravity.CENTER, Toast.LENGTH_LONG).show();
			}
	    }
	}
	
	private int tryRegistration(BusDriver busDriver) {
		return controllerU.registerBusDriver(busDriver);			
	}

}

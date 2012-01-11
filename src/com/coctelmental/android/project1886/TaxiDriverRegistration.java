package com.coctelmental.android.project1886;

import java.net.HttpURLConnection;


import com.coctelmental.android.project1886.common.TaxiDriver;
import com.coctelmental.android.project1886.helpers.UsersHelper;
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

public class TaxiDriverRegistration extends Activity {
	
	private EditText etDNI;
	private EditText etFullName;
	private EditText etPassword;
	private EditText etPassword2;
	private EditText etEmail;
	private EditText etLicenceNumber;
	private EditText etCarBrand;
	private EditText etCarModel;
	private Button bSend;
	
	private String fullName;
	private String dni;
	private String password;
	private String password2;
	private String email;
	private String licence;
	private String carBrand;
	private String carModel;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_driver_registration);
        
        // obtain views
        etFullName= (EditText) findViewById(R.id.fullName);
        etDNI= (EditText) findViewById(R.id.dni);
        etPassword= (EditText) findViewById(R.id.password);
        etPassword2= (EditText) findViewById(R.id.password2);
        etEmail= (EditText) findViewById(R.id.email);
        etLicenceNumber = (EditText) findViewById(R.id.licenceNumber);
        etCarBrand = (EditText) findViewById(R.id.carBrand);
        etCarModel = (EditText) findViewById(R.id.carModel);
        
        bSend= (Button) findViewById(R.id.buttonRegisterTaxis);
        
        bSend.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// get data from views
				fullName= etFullName.getText().toString();
				dni= etDNI.getText().toString();
				password= etPassword.getText().toString();
				password2= etPassword2.getText().toString();
				email= etEmail.getText().toString();
				licence= etLicenceNumber.getText().toString();
				carBrand= etCarBrand.getText().toString();
				carModel= etCarModel.getText().toString();			
				// looking for invalid data
				if(fullName.equals("") || dni.equals("") || password.equals("") || password2.equals("") || email.equals("") ||
						licence.equals("") || carBrand.equals("") || carModel.equals(""))
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
		private TaxiDriver taxiDriver;
		
		protected void onPreExecute () {
			// show a progress dialog while data is retrieved from the server
			pdprocessingRegistration = ProgressDialog.show(TaxiDriverRegistration.this, "", getString(R.string.processingUserRegistration), true);
		}
	    /** The system calls this to perform work in a worker thread and
	      * delivers it the parameters given to AsyncTask.execute() */		
	    protected Integer doInBackground(Void... params) {
			// create a taxiDriver instance with registration data
			taxiDriver= new TaxiDriver(dni, fullName, UsersHelper.passwordToDigest(password), email,
					licence, carBrand, carModel);
			// send request to the server and return response code
	        return tryRegistration(taxiDriver);
	    }	    
	    /** The system calls this to perform work in the UI thread and delivers
	      * the result from doInBackground() */
	    protected void onPostExecute(Integer responseStatus) {
	    	// disable the progress dialog
	        pdprocessingRegistration.dismiss();
	        // check response
			if(responseStatus == HttpURLConnection.HTTP_OK) {
				// add registered user as active user (auto log in after registration)
				Credentials credentials = new Credentials(taxiDriver.getDni(), taxiDriver.getPassword(), Credentials.TYPE_TAXI);
				UsersHelper.logIn(credentials);
				// show message to the user
				Tools.buildToast(getApplicationContext(), getString(R.string.correctRegister),
						Gravity.CENTER, Toast.LENGTH_SHORT).show();
				// go to taxi driver main activity
				Intent i = new Intent(getApplicationContext(), TaxiDriverMain.class);
				startActivity(i);
				finish();
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
					// invalid licence number
					errorMessage = getString(R.string.failRegisterInvalidLicence);
				else if (responseStatus == HttpURLConnection.HTTP_NOT_ACCEPTABLE)
					// error in DB
					errorMessage = getString(R.string.failRegister);
				// show message to the user
				Tools.buildToast(getApplicationContext(), errorMessage,
						Gravity.CENTER, Toast.LENGTH_LONG).show();
			}
	    }
	}

	private int tryRegistration(TaxiDriver taxiDriver) {
		return UsersHelper.registerTaxiDriver(taxiDriver);			
	}

}

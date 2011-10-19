package com.coctelmental.android.project1886;

import java.net.HttpURLConnection;

import com.coctelmental.android.project1886.common.BusDriver;
import com.coctelmental.android.project1886.common.TaxiDriver;
import com.coctelmental.android.project1886.common.User;
import com.coctelmental.android.project1886.common.util.JsonHandler;
import com.coctelmental.android.project1886.logic.ControllerUsers;
import com.coctelmental.android.project1886.model.Credentials;
import com.coctelmental.android.project1886.model.ResultBundle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Authentication extends Activity {
	
	private EditText etUserName;
	private EditText etPassword;
	private Spinner spUserType;
	private Button bLogin;
	
	private String userID;
	private String password;
	
	private static final String[] userTypes = {"Normal", "Taxi", "Bus"};
	private String targetUserType;
	private int restoredtargetUserType;
	private String restoredUserName;
	private String restoredPassword;
	
	private ControllerUsers controllerU;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication);
        
        // get a instance of our controller
        controllerU = new ControllerUsers();
        
        // setting default values for fields which will be restored onResume() if needed
        restoredUserName = "";
        restoredPassword = "";
        
        etUserName = (EditText) findViewById(R.id.userName);
        etPassword = (EditText) findViewById(R.id.password);
        spUserType = (Spinner) findViewById(R.id.userType);
        bLogin = (Button) findViewById(R.id.buttonLogin);
        
        // fill spinner with user types available
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, userTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // set adapter to spinner
        spUserType.setAdapter(adapter);
        // set our custom listener        
        spUserType.setOnItemSelectedListener(new MySpinnerItemSelectedListener());
                
        bLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// get data written by the user
				userID = etUserName.getText().toString();
				password = etPassword.getText().toString();
				
				// verify data
				if(userID.equals("") || password.equals(""))
					showShortToast(getString(R.string.missingFields));
				else 
					// launch Async Task to try to authenticate user
					new AuthenticationAsyncTask().execute(userID);
			}
		});        
    }
	
	private class AuthenticationAsyncTask extends AsyncTask<String, Void, ResultBundle> {
		private ProgressDialog pdprocessingAuthentication;
		
		protected void onPreExecute () {
			// show a progress dialog while data is retrieved from the server
			pdprocessingAuthentication = ProgressDialog.show(Authentication.this, "", getString(R.string.processingUserAuthentication), true);
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
			// check target user type
			// TYPE = Normal User
			if (targetUserType.equals(userTypes[0])) {
				if (rb.getResultCode() == HttpURLConnection.HTTP_OK) {
					String jsonUser = rb.getContent();
					User user = JsonHandler.fromJson(jsonUser, User.class);
					if (user.getPassword().equals(passwordDigest)) {
						// setup new user credentials
						Credentials credentials=new Credentials(userID, passwordDigest, Credentials.TYPE_USER);
						// log in
						controllerU.logIn(credentials);
						// information panel
						showShortToast(getString(R.string.correctLogin));					
						// go to main menu
						Intent i = new Intent(Authentication.this, MainActivity.class);
						// add flag to clear this activity from the top of Android activity stack
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
					}
					else
						// error: passwords don't match
						showLongToast(getString(R.string.failLoginInvalidData));
				}
				else {
					// default message = error server not found
					String errorMessage = getString(R.string.failServerNotFound);
					Log.e("Http error code", Integer.toString(rb.getResultCode()));
					if (rb.getResultCode() == HttpURLConnection.HTTP_NOT_ACCEPTABLE)
						// if response code = request not acceptable
						errorMessage = getString(R.string.failLoginInvalidData);
					showLongToast(errorMessage);							
				}				
			}
			// TYPE = Taxi
			else if (targetUserType.equals(userTypes[1])) {
				if (rb.getResultCode() == HttpURLConnection.HTTP_OK) {
					String jsonUser = rb.getContent();
					TaxiDriver taxiDriver = JsonHandler.fromJson(jsonUser, TaxiDriver.class);
					if (taxiDriver.getPassword().equals(passwordDigest)) {
						// setup new user credentials
						Credentials credentials=new Credentials(userID, passwordDigest, Credentials.TYPE_TAXI);
						// log in
						controllerU.logIn(credentials);
						// information panel
						showShortToast(getString(R.string.correctLogin));					
						// go to main menu   ----------- CAMBIAR ---------------
						Intent i = new Intent(Authentication.this, MainActivity.class);
						// add flag to clear this activity from the top of Android activity stack
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
					}
					else
						// error: passwords don't match
						showLongToast(getString(R.string.failLoginInvalidData));
				}
				else {
					// default message = error server not found
					String errorMessage = getString(R.string.failServerNotFound);
					Log.e("Http error code", Integer.toString(rb.getResultCode()));
					if (rb.getResultCode() == HttpURLConnection.HTTP_NOT_ACCEPTABLE)
						// error: target user doesn't exist
						errorMessage = getString(R.string.failLoginInvalidData);
					showLongToast(errorMessage);							
				}					
			}
			// TYPE = Bus
			else if (targetUserType.equals(userTypes[2])) {
				if (rb.getResultCode() == HttpURLConnection.HTTP_OK) {
					String jsonUser = rb.getContent();
					BusDriver busDriver = JsonHandler.fromJson(jsonUser, BusDriver.class);
					if (busDriver.getPassword().equals(passwordDigest)) {
						// setup new user credentials
						Credentials credentials=new Credentials(userID, passwordDigest, Credentials.TYPE_BUS);
						// log in
						controllerU.logIn(credentials);
						// information panel
						showShortToast(getString(R.string.correctLogin));					
						// go to main menu   ----------- CAMBIAR ---------------
						Intent i = new Intent(Authentication.this, MainActivity.class);
						// add flag to clear this activity from the top of Android activity stack
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
					}
					else
						// error: passwords don't match
						showLongToast(getString(R.string.failLoginInvalidData));
				}
				else {
					// default message = error server not found
					String errorMessage = getString(R.string.failServerNotFound);
					Log.e("Http error code", Integer.toString(rb.getResultCode()));
					if (rb.getResultCode() == HttpURLConnection.HTTP_NOT_ACCEPTABLE)
						// error: target user doesn't exist
						errorMessage = getString(R.string.failLoginInvalidData);
					showLongToast(errorMessage);							
				}					
			}

	    }
	}
	
	private ResultBundle tryAuthentication(String userID) {
		ResultBundle rb = null;
		// TYPE = Normal User
		if (targetUserType.equals(userTypes[0]))
			rb = controllerU.getUser(userID);
		// TYPE = Taxi
		else if (targetUserType.equals(userTypes[1]))
			rb = controllerU.getTaxiDriver(userID);
		// TYPE = Bus
		else if (targetUserType.equals(userTypes[2]))
			rb = controllerU.getBusDriver(userID);	
		return rb;
	}
	
    public class MySpinnerItemSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			// obtaining user type
			targetUserType = parent.getItemAtPosition(pos).toString();		
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// nothing	to do here
		}		
	}
	
	@Override
	protected void onResume() {
		// restoring form fields values
		etUserName.setText(restoredUserName);
		etPassword.setText(restoredPassword);
		spUserType.setSelection(restoredtargetUserType);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// saving form fields values
		restoredUserName = etUserName.getText().toString();
		restoredPassword = etPassword.getText().toString();
		restoredtargetUserType = spUserType.getSelectedItemPosition();
		super.onPause();
	}

	private void showShortToast(String message) {
		// information panel
		Toast toast= Toast.makeText(getApplicationContext(), message,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	private void showLongToast(String message) {
		// information panel
		Toast toast= Toast.makeText(getApplicationContext(), message,
				Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}

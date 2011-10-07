package com.coctelmental.android.project1886;

import java.net.HttpURLConnection;


import com.coctelmental.android.project1886.common.User;
import com.coctelmental.android.project1886.model.Credentials;
import com.coctelmental.android.project1886.util.ConnectionsHandler;
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

public class RegistrationUser extends Activity {
	
	private EditText etUserName;
	private EditText etName;
	private EditText etPassword;
	private EditText etPassword2;
	private EditText etEmail;
	private Button bSend;
	
	private String name;
	private String userName;
	private String password;
	private String password2;
	private String email;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_user);
        
        // obtain views
        etName= (EditText) findViewById(R.id.fullName);
        etUserName= (EditText) findViewById(R.id.userName);
        etPassword= (EditText) findViewById(R.id.password);
        etPassword2= (EditText) findViewById(R.id.password2);
        etEmail= (EditText) findViewById(R.id.email);
        
        bSend= (Button) findViewById(R.id.buttonRegisterUser);
        
        bSend.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				name= etName.getText().toString();
				userName= etUserName.getText().toString();
				password= etPassword.getText().toString();
				password2= etPassword2.getText().toString();
				email= etEmail.getText().toString();
				
				// looking for invalid data
				if(name.equals("") || userName.equals("") || password.equals("") || password2.equals("") || email.equals(""))
					showShortToast(getString(R.string.missingFields));
				else if (!password.equals(password2))
					// passwords doesn't match
					showShortToast(getString(R.string.differentPasswords));
				else
					// launch Async Task to try to register user
					new RegistrationAsyncTask().execute();
			}
		});
    }
	
	private class RegistrationAsyncTask extends AsyncTask<Void, Void, Integer> {
		private ProgressDialog pdprocessingRegistration;
		private User user;
		
		protected void onPreExecute () {
			// show a progress dialog while data is retrieved from the server
			pdprocessingRegistration = ProgressDialog.show(RegistrationUser.this, "", getString(R.string.processingUserRegistration), true);
		}
	    /** The system calls this to perform work in a worker thread and
	      * delivers it the parameters given to AsyncTask.execute() */		
	    protected Integer doInBackground(Void... params) {
			// create an user instance with registration data
			user= new User(userName, name, Tools.digestFromPassword(password), email);
			// send request to the server and return response code
	        return tryRegistration(user);
	    }	    
	    /** The system calls this to perform work in the UI thread and delivers
	      * the result from doInBackground() */
	    protected void onPostExecute(Integer responseStatus) {
	    	// disable the progress dialog
	        pdprocessingRegistration.dismiss();
			// check response
			if(responseStatus == HttpURLConnection.HTTP_OK) {
				// add registered user as active user (log in)
				Credentials credentials = new Credentials(user.getUserName(), user.getPassword(), Credentials.TYPE_USER);
				MyApplication.getInstance().setActiveUser(credentials);
				// information panel
				showShortToast(getString(R.string.correctRegister));
				// go to main menu
				Intent i = new Intent(RegistrationUser.this, MainActivity.class);
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
					errorMessage = getString(R.string.failRegisterInvalidUserName);
				else if (responseStatus == HttpURLConnection.HTTP_NOT_ACCEPTABLE)
					errorMessage = getString(R.string.failRegister);
				// show message to the user
				showLongToast(errorMessage);
			}
	    }
	
	}
	
	private int tryRegistration(User user) {
		return ConnectionsHandler.put("/user", user.toJson());			
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

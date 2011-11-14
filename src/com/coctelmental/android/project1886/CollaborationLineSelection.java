package com.coctelmental.android.project1886;

import java.net.HttpURLConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.coctelmental.android.project1886.common.util.JsonHandler;
import com.coctelmental.android.project1886.logic.ControllerAvailableData;
import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.util.Tools;

public class CollaborationLineSelection extends Activity {
	
	private Button bStart;
	private Spinner spCities;
	private Spinner spLines;
	
	private String targetCity;
	private String targetLine;
	
	private ControllerAvailableData controllerAD;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collaboration_line_selection);
        
        // get a instance of our controller
        controllerAD = new ControllerAvailableData();
        
        // Setup start button
        bStart = (Button) findViewById(R.id.buttonStart);
        bStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent;
				intent= new Intent(CollaborationLineSelection.this, CollaborationInformationPanel.class);
				intent.putExtra(TrackingService.TARGET_CITY, targetCity);
				intent.putExtra(TrackingService.TARGET_LINE, targetLine);
				startActivity(intent);
				finish();
			}
		});
        
        // Setup city spinner
        spCities = (Spinner) findViewById(R.id.targetCity);
        spCities.setOnItemSelectedListener(new CitiesSpinnerItemSelectedListener());
        // Setup line spinner
        spLines = (Spinner) findViewById(R.id.targetLine);
        spLines.setOnItemSelectedListener(new LinesSpinnerItemSelectedListener());        

        // launch AsyncTask which show a progress dialog while the cities are retrieved from the server
        new GetAvailableCitiesTask().execute();
        
    }

	public class CitiesSpinnerItemSelectedListener implements OnItemSelectedListener
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			// obtaining target city
			targetCity = parent.getItemAtPosition(pos).toString();
	        // launch AsyncTask which show a progress dialog while lines are retrieved from the server
	        new GetAvailableLinesTask().execute(targetCity);			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// nothing	to do here
		}		
	}
	
	public class LinesSpinnerItemSelectedListener implements OnItemSelectedListener
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			// obtaining target line
			targetLine=parent.getItemAtPosition(pos).toString();     
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// nothing to do here	
		}		
	}	
	
	private class GetAvailableCitiesTask extends AsyncTask<Void, Void, ResultBundle> {
		private ProgressDialog pdLoadingCities;
		
		protected void onPreExecute () {
			// show a progress dialog while data is retrieved from the server
			pdLoadingCities = ProgressDialog.show(CollaborationLineSelection.this, "", getString(R.string.loadingCities), true);
		}
	    /** The system calls this to perform work in a worker thread and
	      * delivers it the parameters given to AsyncTask.execute() */		
	    protected ResultBundle doInBackground(Void... params) {
	    	// retrieving available cities form server
	        return controllerAD.getAvailableCities();
	    }	    
	    /** The system calls this to perform work in the UI thread and delivers
	      * the result from doInBackground() */
	    protected void onPostExecute(ResultBundle rb) {
	    	// disable the progress dialog
	        pdLoadingCities.dismiss();
	        // check if data is valid
	        if(rb.getResultCode() == HttpURLConnection.HTTP_OK) {
	        	String jsonCities = rb.getContent();
	        	String[] cities = JsonHandler.fromJson(jsonCities, String[].class);
	        	// setup and add to the spinner a new adapter with available cities
	            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CollaborationLineSelection.this, android.R.layout.simple_spinner_item, cities);
	            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	            spCities.setAdapter(adapter);  	   
	        }				
	        else {
				// default message = error server not found
				String errorMessage = getString(R.string.failServerNotFound);
				Log.e("Http error code", Integer.toString(rb.getResultCode()));
				if (rb.getResultCode() == HttpURLConnection.HTTP_NOT_ACCEPTABLE)
					// if response code = request not acceptable
					errorMessage = getString(R.string.failLoadingCities);
				showBackAlertDialog(errorMessage);
	        }
	    }
	}
	
	private class GetAvailableLinesTask extends AsyncTask<String, Void, ResultBundle> {
		private ProgressDialog pdLoadingLines;
		
		protected void onPreExecute () {
	        // enable spinnner and button if disabled
			spLines.setEnabled(true);
			bStart.setEnabled(true);
			// show a progress dialog while data is retrieved from the server
			pdLoadingLines = ProgressDialog.show(CollaborationLineSelection.this, "", getString(R.string.loadingLines), true);
		}
	
	    protected ResultBundle doInBackground(String... params) {
	    	// retrieving available lines form server
	        return controllerAD.getAvailableLines(params[0]);
	    }

	    protected void onPostExecute(ResultBundle rb) {
	    	// disable the progress dialog
	        pdLoadingLines.dismiss();
	        // check if data is valid
	        if(rb.getResultCode() == HttpURLConnection.HTTP_OK) {
	        	String jsonLines = rb.getContent();
	        	String[] lines = JsonHandler.fromJson(jsonLines, String[].class);
	        	// setup and add to the spinner a new adapter with available cities
	            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CollaborationLineSelection.this, android.R.layout.simple_spinner_item, lines);
	            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	            spLines.setAdapter(adapter);  	   
	        }				
	        else {
				// default message = error server not found
				String errorMessage = getString(R.string.failServerNotFound);
				Log.e("Http error code", Integer.toString(rb.getResultCode()));
				if (rb.getResultCode() == HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
					// if response code = request not acceptable
					errorMessage = getString(R.string.failLoadingLines);
					// disabling search button and cities spinner
					spLines.setEnabled(false);
					bStart.setEnabled(false);
					// show error message
					Tools.buildToast(getApplicationContext(), errorMessage, Gravity.CENTER, Toast.LENGTH_LONG).show();
				}
				else
					showBackAlertDialog(errorMessage);
	        }
	    }
	}
	
	private void showBackAlertDialog(String textToShow) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(textToShow)
    	       .setCancelable(false)
    	       .setPositiveButton(getString(R.string.buttonBack), new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
   	       			// finish activity and go previous activity
   	       			CollaborationLineSelection.super.onBackPressed();
	        	   }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
	}

}

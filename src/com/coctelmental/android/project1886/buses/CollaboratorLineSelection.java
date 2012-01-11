package com.coctelmental.android.project1886.buses;

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

import com.coctelmental.android.project1886.MyApplication;
import com.coctelmental.android.project1886.R;
import com.coctelmental.android.project1886.common.util.JsonHandler;
import com.coctelmental.android.project1886.helpers.AvailableDataHelper;
import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.util.Tools;

public class CollaboratorLineSelection extends Activity {
	
	private Button bStart;
	private Spinner spCities;
	private Spinner spLines;
	
	private String targetCity;
	private String targetLine;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collaborator_line_selection);
        
        // Setup start button
        bStart = (Button) findViewById(R.id.buttonStart);
        bStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// save selected targets
				MyApplication.getInstance().storeTrackingInfo(targetCity, targetLine);
				Intent intent;
				intent= new Intent(CollaboratorLineSelection.this, CollaboratorInformationPanel.class);
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

	private class CitiesSpinnerItemSelectedListener implements OnItemSelectedListener {
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
	
	private class LinesSpinnerItemSelectedListener implements OnItemSelectedListener {
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
			pdLoadingCities = ProgressDialog.show(CollaboratorLineSelection.this, "", getString(R.string.loadingCities), true);
		}
		
	    protected ResultBundle doInBackground(Void... params) {
	    	// retrieving available cities form server
	        return AvailableDataHelper.getAvailableCities();
	    }	    

	    protected void onPostExecute(ResultBundle rb) {
	    	// disable the progress dialog
	        pdLoadingCities.dismiss();
	        // check if data is valid
	        if(rb.getResultCode() == HttpURLConnection.HTTP_OK) {
	        	String jsonCities = rb.getContent();
	        	String[] cities = JsonHandler.fromJson(jsonCities, String[].class);
	        	// setup and add to the spinner a new adapter with available cities
	            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CollaboratorLineSelection.this, android.R.layout.simple_spinner_item, cities);
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
			pdLoadingLines = ProgressDialog.show(CollaboratorLineSelection.this, "", getString(R.string.loadingLines), true);
		}
	
	    protected ResultBundle doInBackground(String... params) {
	    	// retrieving available lines from server
	        return AvailableDataHelper.getAvailableLines(params[0]);
	    }

	    protected void onPostExecute(ResultBundle rb) {
	    	// disable the progress dialog
	        pdLoadingLines.dismiss();
	        // check if data is valid
	        if(rb.getResultCode() == HttpURLConnection.HTTP_OK) {
	        	String jsonLines = rb.getContent();
	        	String[] lines = JsonHandler.fromJson(jsonLines, String[].class);
	        	// setup and add to the spinner a new adapter with available cities
	            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CollaboratorLineSelection.this, android.R.layout.simple_spinner_item, lines);
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
   	       			CollaboratorLineSelection.super.onBackPressed();
	        	   }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
	}

}

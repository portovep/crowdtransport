package com.coctelmental.android.project1886;

import com.coctelmental.android.project1886.common.util.JsonHandler;
import com.coctelmental.android.project1886.util.ConnectionsHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class BusLineSelection extends Activity {
	
	public static final String TARGET_CITY="targetCity";
	public static final String TARGET_LINE="targetLine";
	
	private Button bSearch;
	private Spinner spCities;
	private Spinner spLines;
	
	private String targetCity;
	private String targetLine;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_lines);
        
        // Setup search button
        bSearch = (Button) findViewById(R.id.buttonSearch);
        bSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent;
				intent= new Intent(BusLineSelection.this, BusLocationMap.class);
				intent.putExtra(TARGET_CITY, targetCity);
				intent.putExtra(TARGET_LINE, targetLine);
				startActivity(intent);
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
	
	private String[] getAvailableCities()
	{
		String jsonCities;
		String[] result;
		// request to specific resource
		jsonCities = ConnectionsHandler.get("/city");
		if(jsonCities != null) {
			result = JsonHandler.fromJson(jsonCities, String[].class);
		}
		else {
			// if no cities available, return a empty string array
			result = new String[]{};
		}		
		return result;
	}

	private String[] getAvailableLines(String targetCity)
	{
		String jsonLines;
		String[] result;
		// request to specific resource
		jsonLines = ConnectionsHandler.get("/city/"+targetCity+"/line");
		if (jsonLines != null) {
			result = JsonHandler.fromJson(jsonLines, String[].class);
		}
		else
			// if no lines available, return a empty string array
			result = new String[]{};
		return result;
	}
	
	private void showBackAlertDialog(String textToShow) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(textToShow)
    	       .setCancelable(false)
    	       .setPositiveButton(getString(R.string.buttonBack), new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   //Intent i = new Intent(BusLineSelection.this, MainActivity.class);
    	        	   //startActivity(i);
	        	   }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
	}
	
	private class GetAvailableCitiesTask extends AsyncTask<Void, Void, String[]> {
		private ProgressDialog pdLoadingCities;
		
		protected void onPreExecute () {
			// show a progress dialog while data is retrieved from the server
			pdLoadingCities = ProgressDialog.show(BusLineSelection.this, "", getString(R.string.loadingCities), true);
		}
	    /** The system calls this to perform work in a worker thread and
	      * delivers it the parameters given to AsyncTask.execute() */		
	    protected String[] doInBackground(Void... params) {
	    	// retrieving available cities form server
	        return getAvailableCities();
	    }
	    
	    /** The system calls this to perform work in the UI thread and delivers
	      * the result from doInBackground() */
	    protected void onPostExecute(String[] cities) {
	    	// disable the progress dialog
	        pdLoadingCities.dismiss();
	        // check if data is valid
	        if(cities.length == 0)
				showBackAlertDialog(getString(R.string.failLoadingCities));
	        else {
	        	// setup and add to the spinner a new adapter with available cities
	            ArrayAdapter<String> adapter = new ArrayAdapter<String>(BusLineSelection.this, android.R.layout.simple_spinner_item, cities);
	            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	            spCities.setAdapter(adapter);  	        	
	        }
	    }
	}
	
	private class GetAvailableLinesTask extends AsyncTask<String, Void, String[]> {
		private ProgressDialog pdLoadingLines;
		
		protected void onPreExecute () {
			// show a progress dialog while data is retrieved from the server
			pdLoadingLines = ProgressDialog.show(BusLineSelection.this, "", getString(R.string.loadingLines), true);
		}
	
	    protected String[] doInBackground(String... params) {
	    	// retrieving available lines form server
	        return getAvailableLines(params[0]);
	    }

	    protected void onPostExecute(String[] lines) {
	    	// disable the progress dialog
	        pdLoadingLines.dismiss();
	        // check if data is valid
	        if(lines.length == 0)
				showBackAlertDialog(getString(R.string.failLoadingLines));
	        else {
	        	// setup and add to the spinner a new adapter with available lines
	            ArrayAdapter<String> adapter = new ArrayAdapter<String>(BusLineSelection.this, android.R.layout.simple_spinner_item, lines);
	            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	            spLines.setAdapter(adapter);     	
	        }
	    }
	}
	
}

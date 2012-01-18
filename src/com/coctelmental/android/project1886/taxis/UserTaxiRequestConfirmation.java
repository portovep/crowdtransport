package com.coctelmental.android.project1886.taxis;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Locale;

import com.coctelmental.android.project1886.R;
import com.coctelmental.android.project1886.c2dm.C2DMRegistrationReceiver;
import com.coctelmental.android.project1886.common.GeoPointInfo;
import com.coctelmental.android.project1886.common.ServiceRequestInfo;
import com.coctelmental.android.project1886.helpers.ServiceRequestsHelper;
import com.coctelmental.android.project1886.util.Tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UserTaxiRequestConfirmation extends Activity{
	
	private static final String[] availableLifeTimes = {"1 min", "3 min", "5 min"};
	
	private EditText etServiceRequestComment;
	private Spinner spRequestLifeTime;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_taxi_request_confirmation);
        
        ServiceRequestInfo serviceRequest = ServiceRequestsHelper.getServiceRequest();
		String targetTaxiDriverName = serviceRequest.getTaxiDriverFullName();
		String targetTaxiDriverCarBrand = serviceRequest.getTaxiDriverCarBrand();
		String targetTaxiDriverCarModel = serviceRequest.getTaxiDriverCarModel();
        
        // fill taxi driver name label
        TextView tvTaxiDriverName = (TextView) findViewById(R.id.tvTaxiDriverName);
        tvTaxiDriverName.setText(targetTaxiDriverName);
        
        // fill taxi driver car info label
        if (targetTaxiDriverCarBrand != null && targetTaxiDriverCarModel !=null) {
	        TextView tvTaxiDriverCarInfo = (TextView) findViewById(R.id.tvTaxiDriverCarInfo);
	        tvTaxiDriverCarInfo.setText(targetTaxiDriverCarBrand);
	        tvTaxiDriverCarInfo.append(" ");
	        tvTaxiDriverCarInfo.append(targetTaxiDriverCarModel);
        }
		
        // get edit text view
        etServiceRequestComment = (EditText) findViewById(R.id.etClarificationComment);
        
        // fill spinner with available lifetimes
        spRequestLifeTime = (Spinner) findViewById(R.id.spRequestLifeTime);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        					android.R.layout.simple_spinner_item, availableLifeTimes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRequestLifeTime.setAdapter(adapter);  	
        
        
        Button bConfirmation = (Button) findViewById(R.id.bConfirmRequest);
        bConfirmation.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// get clarification comment if there
				String clarificationComment = etServiceRequestComment.getText().toString();
				// add comment to service request info
				ServiceRequestsHelper.getServiceRequest().setComment(clarificationComment);
				// get request time life
				int selectedPosition = spRequestLifeTime.getSelectedItemPosition();
				// add selected time life to service request info
				// get digit
				String aux = availableLifeTimes[selectedPosition];
				String stSelectedLifeTime = aux.substring(0, aux.indexOf('m')-1);
				int selectedLifeTime = Integer.parseInt(stSelectedLifeTime);
				ServiceRequestsHelper.getServiceRequest().setRequestLifeTime(selectedLifeTime);
		        
				// C2DM register to receive push notifications from web service
				C2DMRegistrationReceiver.register(getApplicationContext());
				// Send service request
		        new SendServiceRequestTask().execute();
			}
		});
        	
    }
    
	private class SendServiceRequestTask extends AsyncTask<Void, Void, Integer> {
		private ProgressDialog pdSendingRequests;
		
		protected void onPreExecute () {
			// show a progress dialog while data is retrieved from server
			pdSendingRequests = ProgressDialog.show(UserTaxiRequestConfirmation.this, "", getString(R.string.sendingRequest), true);
		}
		
	    protected Integer doInBackground(Void... params) {
	        ServiceRequestInfo serviceRequest = ServiceRequestsHelper.getServiceRequest();
			// get address name form origin and destination geopoints.
			String addressName = getAddressNameFromGeoPointInfo(serviceRequest.getGpFrom());
			serviceRequest.setAddressFrom(addressName);
			addressName = getAddressNameFromGeoPointInfo(serviceRequest.getGpTo());
			serviceRequest.setAddressTo(addressName);
	        
	        Log.d("Service request info"," TaxiDriver ID: "
	        		+serviceRequest.getTaxiDriverID()+
	        		"\n TaxiDriver UUID: "
	        		+serviceRequest.getTaxiDriverUUID()+
	        		"\n gpORI: "+
	        		serviceRequest.getGpFrom().getLatitudeE6()+
	        		"\n gpDEST: "+
	        		serviceRequest.getGpTo().getLatitudeE6()+
	        		"\n commnet: "+
	        		serviceRequest.getComment()+
	        		"\n lifetime: "+
	        		serviceRequest.getRequestLifeTime() +
	        		"\n addressFrom: "+
	        		serviceRequest.getAddressFrom() +
	        		"\n addressTo: "+
	        		serviceRequest.getAddressTo());
			
	    	// send service request to server
	        return ServiceRequestsHelper.sendServiceRequest();
	    }

	    protected void onPostExecute(Integer result) {
	    	pdSendingRequests.dismiss();
	        // check result
	        if(result == HttpURLConnection.HTTP_OK) {	        	
	        	Log.w("ServiceRequest", "ServiceRequest sent to server");
	        	
	        	Intent intent = new Intent(getApplicationContext(), UserTaxiWaitingPanel.class);
	        	startActivity(intent);
	        	finish();
	        }				
	        else {
				// default message = server not found
				String message = getString(R.string.failServerNotFound);
				if (result == HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
					message = getString(R.string.failSendingRequest);
				}
				else if (result == ServiceRequestsHelper.ERROR_MALFORMED_REQUEST) {
					message = getString(R.string.failMalformedServiceRequest);
				}
				Toast toast = Tools.buildToast(UserTaxiRequestConfirmation.this, message, Gravity.CENTER, Toast.LENGTH_SHORT);
				toast.show();	
	        	Log.w("ServiceRequest", "Error trying send ServiceRequest to server" +
	        			"Error code -> (" + result + ")");
	        }
	    }
	}
	
	private String getAddressNameFromGeoPointInfo(GeoPointInfo geopoint) {
		String addressName = null;
		
		if (geopoint == null)
			return null;
		
		// setup location
		Location location = new Location("");  
		location.setLatitude(geopoint.getLatitudeE6() / 1E6);  
		location.setLongitude(geopoint.getLongitudeE6() / 1E6);
		
		Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
		try {
			List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			
			if (addressList.size() == 1) {
				Address address = addressList.get(0);
				Log.d("Geocoder", "Address found");
				addressName = address.getAddressLine(0);
				if (addressName != null && !addressName.equals("")) {
					// parse address to get address name only
					int indexOfComma = addressName.indexOf(',');
					if (indexOfComma != -1) {
						addressName = addressName.substring(0, indexOfComma);
						Log.d("Geocoder", addressName);
					}
				}
			}
		}catch (IOException ioe) {
			Log.w("Geocoder", "Error trying get address from location");
		}
		
		return addressName;
	}
}

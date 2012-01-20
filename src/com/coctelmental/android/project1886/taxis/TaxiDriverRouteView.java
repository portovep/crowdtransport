package com.coctelmental.android.project1886.taxis;

import java.net.HttpURLConnection;
import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coctelmental.android.project1886.R;
import com.coctelmental.android.project1886.common.GeoPointInfo;
import com.coctelmental.android.project1886.common.ServiceRequestInfo;
import com.coctelmental.android.project1886.helpers.ServiceRequestsHelper;
import com.coctelmental.android.project1886.main.Preferences;
import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.util.JsonHandler;
import com.coctelmental.android.project1886.util.Tools;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class TaxiDriverRouteView extends MapActivity {

	public static final String EXTRA_SERVICE_REQUEST = "EXTRA_SERVICE_REQUEST";
	
	private static final int INIT_ZOOM_LEVEL = 17;

	private MyLocationOverlay myLocationOverlay;
	
	private GeoPoint gpOrigin = null;
	private GeoPoint gpDestination = null;
	
	private ServiceRequestInfo serviceRequest;
	private String jsonServiceRequest = null;
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.taxi_driver_route_view);
		
        // check user preferences
	    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    boolean satellite = sp.getBoolean(Preferences.PREF_TAXI_MAP_SATELLITE,
	    		Preferences.DEFAULT_TAXI_MAP_SATELLITE);
	    boolean zoomControls = sp.getBoolean(Preferences.PREF_TAXI_MAP_ZOOM_CONTROL,
	    		Preferences.DEFAULT_TAXI_MAP_ZOOM_CONTROL);
		
        // setup map configuration
	    MapView mapView = (MapView) findViewById(R.id.mapTaxiRoute);	    
        mapView.setEnabled(true);
	    mapView.setBuiltInZoomControls(zoomControls);
        mapView.setSatellite(satellite);
        mapView.setTraffic(false);	
        mapView.setStreetView(false);
        
        // get serviceRequestInfo
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        	jsonServiceRequest = extras.getString(EXTRA_SERVICE_REQUEST);
        
        serviceRequest = null;
        if (jsonServiceRequest == null) {
        	Tools.buildToast(this, getString(R.string.errorGettingServiceRequestInfo), Gravity.CENTER, Toast.LENGTH_SHORT).show();
        	finish();
        }
        else {
        	Log.e("re", jsonServiceRequest);
	        serviceRequest = JsonHandler.fromJson(jsonServiceRequest, ServiceRequestInfo.class);
	
	        GeoPointInfo gpOri = serviceRequest.getGpFrom();
	        GeoPointInfo gpDest = serviceRequest.getGpTo();
	        
	        gpOrigin = new GeoPoint(gpOri.getLatitudeE6(), gpOri.getLongitudeE6());
	        gpDestination = new GeoPoint(gpDest.getLatitudeE6(), gpDest.getLongitudeE6());
	       
			// update route info labels
			new GetRouteInfoAsyncTask().execute();
	        
	        // setup overlays
	        OverlayItem overlayOri = new OverlayItem(gpOrigin, "", "");       
	        OverlayItem overlayDest = new OverlayItem(gpDestination, "", "");
	        
	        // add overlays
	        RouteItemizedOverlay itemizedOverlay = new RouteItemizedOverlay(getResources().getDrawable(R.drawable.marker_ori));
	        itemizedOverlay.addOverlay(overlayOri);
	        itemizedOverlay.addOverlay(overlayDest, getResources().getDrawable(R.drawable.marker_dest));
	        itemizedOverlay.populateNow();
	    
	        mapView.getOverlays().add(itemizedOverlay);
	        
	        // setup zoom
	        mapView.getController().setCenter(overlayOri.getPoint());
	        mapView.getController().setZoom(INIT_ZOOM_LEVEL);
	        
	        // redraw map
	        mapView.invalidate();
	    
	        // show taxi driver location
	        myLocationOverlay = new MyLocationOverlay(this, mapView);
	        mapView.getOverlays().add(myLocationOverlay);
	        myLocationOverlay.enableMyLocation(); 
	        
	        Button bAcceptServiceRequest = (Button) findViewById(R.id.bAcceptRequest);
	        bAcceptServiceRequest.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View v) {
					// launch async task
					new AcceptServiceRequestTask().execute();
				}
			});
	        
        }
	}

	@Override
	protected void onResume() {
		myLocationOverlay.enableMyLocation();
		super.onResume();
	}

	@Override
	protected void onPause() {
		myLocationOverlay.disableMyLocation();
		super.onPause();
	}

	private class AcceptServiceRequestTask extends AsyncTask<Void, Void, Integer> {
		private ProgressDialog pdAcceptingRequest;
		
		protected void onPreExecute () {
			// show a progress dialog while data is retrieved from server
			pdAcceptingRequest = ProgressDialog.show(TaxiDriverRouteView.this, "", getString(R.string.acceptingRequest), true);
		}
		
	    protected Integer doInBackground(Void... params) {
	    	// send request to server
	        return ServiceRequestsHelper.acceptServiceRequest(serviceRequest.getUserUUID());
	    }

	    protected void onPostExecute(Integer result) {
	    	pdAcceptingRequest.dismiss();
	        // check result
	        if(result == HttpURLConnection.HTTP_OK) {	        	
	        	Log.d("ServiceRequest", "ServiceRequest accepted");
	        	
				Tools.buildToast(getApplicationContext(), getString(R.string.taxiDriverAcceptRequest), Gravity.CENTER, Toast.LENGTH_SHORT).show();
				
				// finish tracking service
				Intent i = new Intent(getApplicationContext(), TaxiTrackingService.class);
				stopService(i);
				
				// goto new activity
				Intent intent = new Intent(getApplicationContext(), TaxiDriverRouteReminder.class);
		    	// attach JSON data to intent
		    	intent.putExtra(EXTRA_SERVICE_REQUEST, jsonServiceRequest);
	        	startActivity(intent);
	        	finish();
	        }				
	        else {
				// default message = server not found
				String message = getString(R.string.failServerNotFound);
				if (result == HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
					message = getString(R.string.failAcceptingRequest);
				}
				
				Toast toast = Tools.buildToast(TaxiDriverRouteView.this, message, Gravity.CENTER, Toast.LENGTH_SHORT);
				toast.show();	
	        	Log.d("ServiceRequest", "Error trying accept ServiceRequest in server" +
	        			"Error code -> (" + result + ")");
	        }
	    }
	}
	
	private void updateDistanceLabel(double distanceInMeters) {		
		// check user preferences
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String units = sp.getString(Preferences.PREF_TAXI_DISTANCE_UNITS, Preferences.DEFAULT_TAXI_DISTANCE_UNITS);
		
		if (units.equals("km"))
			// parse to kilometers
			distanceInMeters = distanceInMeters / 1000;
		// update label
		DecimalFormat df = new DecimalFormat("#######0.0#");
		TextView tvDistance = (TextView) findViewById(R.id.distance);
		tvDistance.setText(" " + df.format(distanceInMeters) + units);
	}
	
	private void updateRouteTimeLabel(String timeText) {
		TextView tvTime = (TextView) findViewById(R.id.time);
		tvTime.setText("   " + timeText);
	}
	
	private class GetRouteInfoAsyncTask extends AsyncTask<Void, Void, ResultBundle> {
		
	    protected ResultBundle doInBackground(Void... params) {
	        return ServiceRequestsHelper.obtainRouteInfo(gpOrigin, gpDestination);
	    }

	    protected void onPostExecute(ResultBundle rb) {
	    	boolean error = true;
	        // check response code
	        if(rb.getResultCode() == HttpURLConnection.HTTP_OK) {
	        	String jsonRouteInfo = rb.getContent();
	        	
	        	try{
	        		// parse JSON route info data
		        	JSONObject joRouteInfo = new JSONObject(jsonRouteInfo);
		        	
		        	// check response code
		        	String status = joRouteInfo.getString("status");
		        	if (status.equals("OK")) {
		        		// disable error flag
			        	error = false;
			        	JSONArray rows = joRouteInfo.getJSONArray("rows");
			        	// get distance in meters
			        	double distance = (double) rows.getJSONObject(0).getJSONArray("elements")
			        			.getJSONObject(0).getJSONObject("distance").getDouble("value");
			        	// get time text
			        	String timeText = (String) rows.getJSONObject(0).getJSONArray("elements")
			        			.getJSONObject(0).getJSONObject("duration").getString("text");		
			        	
			        	Log.d("ROUTE_INFO", "Route distance: " + distance);
			        	Log.d("ROUTE_INFO", "Route time: " + timeText);
			        	
			        	// update labels
			        	updateDistanceLabel(distance);
			        	updateRouteTimeLabel(timeText);
		        	}
	        	}catch (JSONException je) {
					Log.w("JSON error", je.getMessage());
				}	        	
	        }				
	        
	        if (error) {
	    		// calculate unaccurate distance
	    		Double distanceInMeters = Tools.calculateDistanceInMeters(gpOrigin, gpDestination);
	    		// show unaccurate distance
	    		updateDistanceLabel(distanceInMeters);
	    		updateRouteTimeLabel(getString(R.string.routeTimeLabelDefault));
	        }
	    }
	}
	
}

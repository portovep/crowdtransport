package com.coctelmental.android.project1886;

import java.net.HttpURLConnection;
import java.text.DecimalFormat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coctelmental.android.project1886.common.GeoPointInfo;
import com.coctelmental.android.project1886.common.util.JsonHandler;
import com.coctelmental.android.project1886.logic.ControllerServiceRequests;
import com.coctelmental.android.project1886.model.ServiceRequestInfo;
import com.coctelmental.android.project1886.util.Tools;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class TaxiDriverRouteView extends MapActivity {

	public static final String SERVICE_REQUEST = "SERVICE_REQUEST";
	private static final int INIT_ZOOM_LEVEL = 17;

	private MyLocationOverlay myLocationOverlay;
	
	private ServiceRequestInfo serviceRequest;
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.taxi_driver_route_view);
		
        // setup map configuration
	    MapView mapView = (MapView) findViewById(R.id.mapTaxiRoute);	    
	    mapView.setBuiltInZoomControls(true);
        mapView.setEnabled(true);
        mapView.setSatellite(false);
        mapView.setTraffic(false);	
        mapView.setStreetView(false);
        
        // get serviceRequestInfo
        Bundle extras = getIntent().getExtras();
        String jsonServiceRequest = null;
        if (extras != null)
        	jsonServiceRequest = extras.getString(SERVICE_REQUEST);
        
        serviceRequest = null;
        if (jsonServiceRequest == null) {
        	Tools.buildToast(this, getString(R.string.errorGettingServiceRequestInfo), Gravity.CENTER, Toast.LENGTH_SHORT).show();
        	finish();
        }
        else {
        	Log.e("re", jsonServiceRequest);
	        serviceRequest = JsonHandler.fromJson(jsonServiceRequest, ServiceRequestInfo.class);
	
	        GeoPointInfo gpOri = serviceRequest.getGpOrigin();
	        GeoPointInfo gpDest = serviceRequest.getGpDestination();
	        
	        // setup overlays
	        OverlayItem overlayOri = new OverlayItem(new GeoPoint(gpOri.getLatitudeE6(), gpOri.getLongitudeE6()),
	        			"", "");       
	        OverlayItem overlayDest = new OverlayItem(new GeoPoint(gpDest.getLatitudeE6(), gpDest.getLongitudeE6()),
	    				"", "");
        	
			// calculating distance
			Double distance = Tools.calculateDistanceInMeters(overlayOri.getPoint(), overlayDest.getPoint());
			// update distance label
			TextView tvDistance = (TextView) findViewById(R.id.distance);
			DecimalFormat df = new DecimalFormat("#######0.0#");
			tvDistance.setText(" "+df.format(distance)+"m");
	        
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
			pdAcceptingRequest = ProgressDialog.show(TaxiDriverRouteView.this, "", getString(R.string.sendingRequest), true);
		}
		
	    protected Integer doInBackground(Void... params) {
	    	// send request to server
	        return ControllerServiceRequests.acceptServiceRequest(serviceRequest.getUserUUID());
	    }

	    protected void onPostExecute(Integer result) {
	    	pdAcceptingRequest.dismiss();
	        // check result
	        if(result == HttpURLConnection.HTTP_OK) {	        	
	        	Log.d("ServiceRequest", "ServiceRequest accepted");
	        	
				Tools.buildToast(getApplicationContext(), getString(R.string.taxiDriverAcceptRequest), Gravity.CENTER, Toast.LENGTH_SHORT).show();
				
				// finish tracking service
				Intent i = new Intent(getApplicationContext(), TrackingService.class);
				stopService(i);
				
		    	// parse data to send into intent
		    	String jsonServiceRequest = JsonHandler.toJson(serviceRequest);
				
				// goto new activity
				Intent intent = new Intent(getApplicationContext(), TaxiDriverRouteReminder.class);
		    	// attach JSON data to intent
		    	intent.putExtra(SERVICE_REQUEST, jsonServiceRequest);
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
	
}

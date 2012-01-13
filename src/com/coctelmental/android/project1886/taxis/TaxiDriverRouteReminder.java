package com.coctelmental.android.project1886.taxis;

import com.coctelmental.android.project1886.R;
import com.coctelmental.android.project1886.common.GeoPointInfo;
import com.coctelmental.android.project1886.common.ServiceRequestInfo;
import com.coctelmental.android.project1886.util.JsonHandler;
import com.coctelmental.android.project1886.util.Tools;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

public class TaxiDriverRouteReminder extends MapActivity {

	private static final int INIT_ZOOM_LEVEL = 17;
	
	private Chronometer chrono;
	
	private MyLocationOverlay myLocationOverlay;
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.taxi_driver_route_reminder);
		
        // setup map configuration
	    MapView mapView = (MapView) findViewById(R.id.mapTaxiRoute);	    
        mapView.setEnabled(true);
        mapView.setSatellite(false);
        mapView.setTraffic(false);	
        mapView.setStreetView(false);
        
        // get serviceRequestInfo
        Bundle extras = getIntent().getExtras();
        String jsonServiceRequest = null;
        if (extras != null)
        	jsonServiceRequest = extras.getString(TaxiDriverRouteView.EXTRA_SERVICE_REQUEST);
        
        ServiceRequestInfo serviceRequest = null;
        if (jsonServiceRequest == null) {
        	Tools.buildToast(this, getString(R.string.errorGettingServiceRequestInfo), Gravity.CENTER, Toast.LENGTH_SHORT).show();
        	finish();
        }
        else {
	        serviceRequest = JsonHandler.fromJson(jsonServiceRequest, ServiceRequestInfo.class);
	
	        GeoPointInfo gpOri = serviceRequest.getGpFrom();
	        GeoPointInfo gpDest = serviceRequest.getGpTo();
	        
	        // setup overlays
	        OverlayItem overlayOri = new OverlayItem(new GeoPoint(gpOri.getLatitudeE6(), gpOri.getLongitudeE6()),
	        			"", "");       
	        OverlayItem overlayDest = new OverlayItem(new GeoPoint(gpDest.getLatitudeE6(), gpDest.getLongitudeE6()),
	    				"", "");
	        
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
	        
	        // setup chronometer
	        chrono = (Chronometer) findViewById(R.id.chrono);
	        chrono.start();
	        
	        Button bPickedUp = (Button) findViewById(R.id.bPickedUp);
	        bPickedUp.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View v) {
					// launch next activity
					Intent intent = new Intent(getApplicationContext(), TaxiDriverAttendingServiceRequest.class);
					startActivity(intent);
				}
			});
	        
        }
	}
	
	@Override
	protected void onResume() {
		chrono.start();
		myLocationOverlay.enableMyLocation();
		super.onResume();
	}

	@Override
	protected void onPause() {
		chrono.stop();
		myLocationOverlay.disableMyLocation();
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}
	
}

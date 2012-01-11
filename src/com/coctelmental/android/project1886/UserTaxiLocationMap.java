package com.coctelmental.android.project1886;


import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.coctelmental.android.project1886.common.GeoPointInfo;
import com.coctelmental.android.project1886.common.TaxiLocation;
import com.coctelmental.android.project1886.common.util.JsonHandler;
import com.coctelmental.android.project1886.helpers.LocationsHelper;
import com.coctelmental.android.project1886.helpers.ServiceRequestsHelper;
import com.coctelmental.android.project1886.model.ResultBundle;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.gson.reflect.TypeToken;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class UserTaxiLocationMap extends MapActivity {
	
	private static final int TIME_BETWEEN_UPDATES = 5000;	
	private Timer updaterTimer;
	
	private AlertDialog alertDialogLocationNotFound;
	
	private TaxiItemizedOverlay taxiItemizedOverlays;
	
	private ResultBundle updatedLocation;
    private GeoPointInfo gpOrigin = null;
	
	private MapView mapView;
	private MapController mc;
	private List<Overlay> mapOverlays;	
	private Drawable drawableTaxiMarker;

	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.user_taxi_location_map);
        
	    // get origin geopoint from service request info
    	gpOrigin = ServiceRequestsHelper.getServiceRequest().getGpFrom();        
	    
	    Log.w(getString(R.string.app_name), "User loc: "+gpOrigin.getLatitudeE6()+" : "+gpOrigin.getLongitudeE6());
	    
	    
	    // setup map configuration
	    mapView = (MapView) findViewById(R.id.mapTaxiLocation);
	    mapView.setBuiltInZoomControls(true);
        mapView.setClickable(true);
        mapView.setEnabled(true);
        mapView.setSatellite(true);
        mapView.setTraffic(false);	
        mapView.setStreetView(false);
	        
		// get map controller to control zoom and other stuff
		mc = mapView.getController();
        mc.setZoom(17);
        // get reference to map overlays
        mapOverlays = mapView.getOverlays();
        
        // focus map's center on origin geopoint
        mc.animateTo(new GeoPoint(gpOrigin.getLatitudeE6(), gpOrigin.getLongitudeE6()));
	    
	    // get custom marker icon
        drawableTaxiMarker = this.getResources().getDrawable(R.drawable.marker_taxi);    
		
		// setup custom overlays
		taxiItemizedOverlays = new TaxiItemizedOverlay(drawableTaxiMarker, this);
	    // add our custom overlay to the map
        mapOverlays.add(taxiItemizedOverlays);
	}
	
	@Override
	protected void onResume() {
	    // start a timer witch allow us to obtain the location from the server at regular intervals
		updaterTimer = new Timer();
	    updaterTimer.schedule(new updaterTimerTask(), 0, TIME_BETWEEN_UPDATES);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// dismiss alert dialog if it's needed
		if (this.alertDialogLocationNotFound != null)
			this.alertDialogLocationNotFound.cancel();
		stopUpdater();
		super.onPause();
	}

	private void showUpdatedLocation(ResultBundle rb) {
	    // position available
	    if (rb.getResultCode() == HttpURLConnection.HTTP_OK) {
	    	String jsonLocations = rb.getContent();
			// Obtaining specific object from json codification
			Type listType = new TypeToken<List<TaxiLocation>>() {}.getType();			
	    	ArrayList<TaxiLocation> newLocations = JsonHandler.fromJson(jsonLocations, listType);

	    	Log.w(getString(R.string.app_name), "New taxi location received ("+newLocations.size()+")," +
	    			" lat="+newLocations.get(0).getLocation().getGeopoint().getLatitudeE6()+
	    			" long="+newLocations.get(0).getLocation().getGeopoint().getLatitudeE6());

	    	// remove previous overlays
	    	taxiItemizedOverlays.clear();   
	    	
	    	GeoPoint geopoint = null;
	    	for(TaxiLocation taxiLocation : newLocations) {
		    	// setup a Android GeoPoint with received position and add it to the new overlay item
	    		GeoPointInfo gpInfo = taxiLocation.getLocation().getGeopoint();
			    geopoint = new GeoPoint(gpInfo.getLatitudeE6(), gpInfo.getLongitudeE6());
			    // setup overlay item
			    /* NOTE: 
			     * 		Overlay title = taxiDriverID
			     * 		Overlay comment = taxiDriverUUID
			     */
			    OverlayItem overlayItem = new OverlayItem(geopoint, taxiLocation.getTaxiDriverID(), taxiLocation.getUUID());    
			    // add new overlay to the list
			    taxiItemizedOverlays.addOverlay(overlayItem);
	    	}
	    	taxiItemizedOverlays.populateNow();

	        // re-draw the map with new overlays
	        mapView.invalidate();
	    }
	    // no new locations
		else {
			if (!taxiItemizedOverlays.isOverlayDialogVisible()) {
		    	// stop updaterTimer
		    	stopUpdater();			
		    	
				// default message = error server not found
				String errorMessage = getString(R.string.failServerNotFound);
				
				Log.e("Http error code", Integer.toString(rb.getResultCode()));
				
				if (rb.getResultCode() == HttpURLConnection.HTTP_NOT_ACCEPTABLE)
					// if response code = request not acceptable
					errorMessage = getString(R.string.taxiLocationsNotFound);			
		    	
		    	goPreviousActivity(errorMessage);
			}
	    }
	}

	private class updaterTimerTask extends TimerTask {		
		public void run() {

			if (!taxiItemizedOverlays.isOverlayDialogVisible()) {
				updatedLocation = LocationsHelper.obtainTaxiLocations(gpOrigin);
				// notify the handler
				handler.sendEmptyMessage(0);
			}
		}
	}
	
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {       		
			showUpdatedLocation(updatedLocation);
        }
	};
	
	private void stopUpdater() {
    	// cancel timer
    	updaterTimer.cancel();
		// remove pending messages
		handler.removeCallbacksAndMessages(null);
	}
	
	private void goPreviousActivity(String message){
		if(alertDialogLocationNotFound == null) {
	    	// setup and show a alert dialog
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage(message)
	    	       .setCancelable(false)
	    	       .setPositiveButton(getString(R.string.buttonBack), new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	    dialog.dismiss();
	    	       			// finish activity and go previous activity
	    	       			UserTaxiLocationMap.super.onBackPressed();
	    	           }
	    	       });
	    	// creating the alert dialog
	    	alertDialogLocationNotFound = builder.create();
		}
		if(!alertDialogLocationNotFound.isShowing()) {
			alertDialogLocationNotFound.setMessage(message);
	    	alertDialogLocationNotFound.show();
		}
	}
	
}

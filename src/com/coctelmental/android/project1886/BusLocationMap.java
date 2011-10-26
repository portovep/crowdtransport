package com.coctelmental.android.project1886;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.coctelmental.android.project1886.common.BusLocation;
import com.coctelmental.android.project1886.common.util.JsonHandler;
import com.coctelmental.android.project1886.logic.ControllerLocations;
import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.util.Tools;
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


public class BusLocationMap extends MapActivity {
	
	private static final int TIME_BETWEEN_UPDATES = 5000;	
	private Timer updaterTimer;
	
	private AlertDialog alertDialogLocationNotFound; 
	
	private String targetCity = null;
	private String targetLine = null;	
	private ResultBundle updatedLocation;			
	
	private MapView mapView;
	private MapController mc;
	private List<Overlay> mapOverlays;	
	private Drawable drawableBusMarker;
	
	private ControllerLocations controllerL;
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
        // get a instance of our controller
        controllerL = new ControllerLocations();
        
	    // get data from intent
    	Bundle extras = getIntent().getExtras();	    
        targetCity = extras != null ? extras.getString(BusLineSelection.TARGET_CITY) : null;
        targetLine = extras != null ? extras.getString(BusLineSelection.TARGET_LINE) : null;      
	    
	    Log.w(getString(R.string.app_name), "Request information to city: "+targetCity+" and line: "+targetLine);
	        
	    setContentView(R.layout.bus_location_map);
	    
	    // setup map configuration
	    mapView = (MapView) findViewById(R.id.mapBusLocation);
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
        
	    updaterTimer = new Timer();

	    // get reference for our marker custom icon
        drawableBusMarker = this.getResources().getDrawable(R.drawable.bus_icon);    
	}
	
	@Override
	protected void onResume() {
	    // start a timer witch allow us to obtain the location from the server at regular intervals
	    updaterTimer.schedule(new updaterTimerTask(), 0, TIME_BETWEEN_UPDATES);		
		super.onResume();
	}

	private void showUpdatedLocation(ResultBundle rb)
	{
	    // position available
	    if (rb.getResultCode() == HttpURLConnection.HTTP_OK) {
	    	String jsonLocations = rb.getContent();
			// Obtaining specific object from json codification
			Type listType = new TypeToken<List<BusLocation>>() {}.getType();			
	    	ArrayList<BusLocation> newLocations = JsonHandler.fromJson(jsonLocations, listType);

	    	Log.w(getString(R.string.app_name), "New location received ("+newLocations.size()+")," +
	    			" lat="+newLocations.get(0).getLatitude()+" long="+newLocations.get(0).getLongitude());

	    	// remove previous overlays
	    	CustomItemizedOverlay busItemizedOverlays = new CustomItemizedOverlay(drawableBusMarker, this);    
	    	
	    	GeoPoint geopoint = null;
	    	for(int i=0; i<newLocations.size(); i++) {
		    	// setup a Android GeoPoint with received position and add it to the new overlay item
	    		BusLocation busLocation = newLocations.get(i);
			    geopoint = new GeoPoint(busLocation.getLatitude(), busLocation.getLongitude());
			    // setup overlay item
			    OverlayItem overlayItem = new OverlayItem(geopoint, busLocation.getBusLocationID(),
			    		getString(R.string.whenReceived)+": "+Tools.getTime(busLocation.getWhen())+"\n"+
			    		getString(R.string.city)+": "+targetCity+"\n"+
			    		getString(R.string.line)+": "+targetLine);		    
			    // add new overlay to the list
			    busItemizedOverlays.addOverlay(overlayItem);
	    	}
	    	busItemizedOverlays.populateNow();
	    	
		    // focus map's center on the geopoint
	    	//mc.animateTo(geopoint);
	    	
	    	// clear previous overlays
	    	mapOverlays.clear();
		    // adding our custom overlay to the list of the map
	        mapOverlays.add(busItemizedOverlays);
	        // re-draw the map with new overlays
	        mapView.invalidate();
	    }
	    // no new locations
		else {
	    	// stop updaterTimer
	    	stopUpdater();			
	    	
			// default message = error server not found
			String errorMessage = getString(R.string.failServerNotFound);
			
			Log.e("Http error code", Integer.toString(rb.getResultCode()));
			
			if (rb.getResultCode() == HttpURLConnection.HTTP_NOT_ACCEPTABLE)
				// if response code = request not acceptable
				errorMessage = getString(R.string.locationNotFound);			
	    	
	    	goPreviousActivity(errorMessage);
	    }
	}

	private class updaterTimerTask extends TimerTask {		
		public void run() {
			updatedLocation = controllerL.obtainLocation(targetCity, targetLine);
			// notify the handler
			handler.sendEmptyMessage(0);
		}
	}
	
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {       		
			showUpdatedLocation(updatedLocation);
        }
	};
	
	private void stopUpdater() {
    	// remove pending messages
		handler.removeMessages(0);		
    	// cancel timer
    	updaterTimer.cancel();
	}

	@Override
	protected void onPause() {
		// dismiss alert dialog if it's needed
		if (this.alertDialogLocationNotFound != null)
			this.alertDialogLocationNotFound.dismiss();
		stopUpdater();
		super.onPause();
	}
	
	private void goPreviousActivity(String message){
    	// setup and show a alert dialog
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(message)
    	       .setCancelable(false)
    	       .setPositiveButton(getString(R.string.buttonBack), new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	       			// finish activity and go previous activity
    	       			BusLocationMap.super.onBackPressed();
    	           }
    	       });
    	// creating and showing the alert dialog
    	alertDialogLocationNotFound = builder.create();
    	alertDialogLocationNotFound.show();
	}
	
}	
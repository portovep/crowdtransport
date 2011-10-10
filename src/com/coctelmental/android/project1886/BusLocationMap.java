package com.coctelmental.android.project1886;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import com.coctelmental.android.project1886.common.Geopoint;
import com.coctelmental.android.project1886.common.util.JsonHandler;
import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.util.ConnectionsHandler;
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


public class BusLocationMap extends MapActivity implements Runnable {
	
	private static final int UPDATE_TIME = 5000;
	
	private Thread updaterThread;
	private Boolean flagStopThread;
	
	private AlertDialog alertDialogLocationNotFound; 
	
	private String targetCity = null;
	private String targetLine = null;	
	private ResultBundle updatedLocation;			
	
	private MapView mapView;
	private MapController mc;
	private List<Overlay> mapOverlays;	
	private CustomItemizedOverlay busItemizedOverlay;
	
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
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
        mapView.setSatellite(false);
        mapView.setTraffic(false);	
        mapView.setStreetView(false);
	        
		// get map controller to control zoom and other stuff
		mc = mapView.getController();
        mc.setZoom(18); 
	    
	    // setup custom itemized overlay by adding custom icon
	    Drawable drawableBusIcon = this.getResources().getDrawable(R.drawable.bus_icon);
	    busItemizedOverlay = new CustomItemizedOverlay(drawableBusIcon, this);        
	}
	
	
	
	@Override
	protected void onResume() {
	    // setup and start a worker thread which will obtain the location from the server
	    flagStopThread = false;
	    updaterThread = new Thread(this);
	    updaterThread.start();			
		super.onResume();
	}

	private ResultBundle obtainLocation(){
		// REST request to the specific resource
		ResultBundle rb = ConnectionsHandler.getWithStatus("/location/"+targetCity+targetLine);
		return rb;
	}
	
	private void showUpdatedLocation(ResultBundle rb)
	{
	    // position available
	    if (rb.getResultCode() == HttpURLConnection.HTTP_OK) {
	    	String jsonLocations = rb.getContent();
			// Obtaining specific object from json codification
			Type listType = new TypeToken<List<Geopoint>>() {}.getType();			
	    	ArrayList<Geopoint> newLocations = JsonHandler.fromJson(jsonLocations, listType);

	    	Log.w(getString(R.string.app_name), "New location received, lat="+newLocations.get(0).getLatitude()+" long="+newLocations.get(0).getLongitude());

	    	// remove previous overlays
		    busItemizedOverlay.removeAllOverlays();
	    	GeoPoint geopoint = null;
	    	for(int i=0; i<newLocations.size(); i++) {
		    	// setup a Android GeoPoint with received position and add it to the new overlay item
	    		Geopoint aux = newLocations.get(i);
			    geopoint = new GeoPoint(aux.getLatitude(), aux.getLongitude());
			    // setup overlay item
			    OverlayItem overlayItem = new OverlayItem(geopoint, aux.getId(),
			    		getString(R.string.city)+": "+targetCity+"\n"+
			    		getString(R.string.line)+": "+targetLine);		    
			    // add new overlay to the list
			    busItemizedOverlay.addOverlay(overlayItem);
	    	}
		    // focus map's center on the geopoint
		    mc.animateTo(geopoint);     
		    // adding our custom overlay to the list of the map
	        mapOverlays = mapView.getOverlays();
	        mapOverlays.add(busItemizedOverlay);
	        // re-draw the map with new overlays
	        mapView.invalidate();
	    }
	    // no new locations
		else {
			// default message = error server not found
			String errorMessage = getString(R.string.failServerNotFound);
			
			Log.e("Http error code", Integer.toString(rb.getResultCode()));
			
			if (rb.getResultCode() == HttpURLConnection.HTTP_NOT_ACCEPTABLE)
				// if response code = request not acceptable
				errorMessage = getString(R.string.locationNotFound);			
			
	    	// stopping thread and handler
	    	flagStopThread=true;
	    	stopThreadHandler();
	    	
	    	goPreviousActivity(errorMessage);
	    }
	}
	

	
	@Override
	public void run() {		
		while(!this.flagStopThread)
		{
			updatedLocation = obtainLocation();
			// notify the handler
			handler.sendEmptyMessage(0);
			try{
				Thread.sleep(UPDATE_TIME);
			}catch(Exception e)
			{
				Log.e(getString(R.string.app_name), e.getMessage()+"\n"+e.getCause());
			}
		}		
	}
	
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {       		
			//if(!updatedLocation.equals(location))
			showUpdatedLocation(updatedLocation);
        }
	};
	
	private void stopThreadHandler() {
		handler.removeMessages(0);
        //handler.removeCallbacks(this);
	}

	@Override
	protected void onPause() {
		// dismiss alert dialog if it's needed
		if (this.alertDialogLocationNotFound != null)
			this.alertDialogLocationNotFound.dismiss();
		flagStopThread = true;
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
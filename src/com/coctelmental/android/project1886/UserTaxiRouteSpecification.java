package com.coctelmental.android.project1886;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coctelmental.android.project1886.common.GeoPointInfo;
import com.coctelmental.android.project1886.logic.ControllerServiceRequests;
import com.coctelmental.android.project1886.util.Tools;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class UserTaxiRouteSpecification extends MapActivity {
	
	private static final String ORIGIN_ID = "ORIGIN";
	private static final String DESTINATION_ID = "DESTINATION";
	
	private MapView mapView = null;
	private static final int ZOOM_LEVEL = 17;
	
	private GeoPoint gpOrigin = null;
	private GeoPoint gpDestination = null;
	
	private LinearLayout layout;
	private TextView tvDistance;
	private ProgressDialog pdLookingLocation = null;	
	private Timer timer = null;
	private UserLocationHelper userLocationHelper = null;
	
	private ControllerServiceRequests controllerSR;

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.user_taxi_route_specification);
	 
	    controllerSR = new ControllerServiceRequests();
	    
	    // get layout and set invisible during setup
	    layout = (LinearLayout) findViewById(R.id.container);
	    layout.setVisibility(ViewGroup.GONE);
	    
        // get distance label
        tvDistance = (TextView) findViewById(R.id.distance);
	    
        // setup map configuration
	    mapView = (MapView) findViewById(R.id.mapTaxiRoute);	    
	    mapView.setBuiltInZoomControls(false);
        mapView.setEnabled(true);
        mapView.setSatellite(false);
        mapView.setTraffic(false);	
        mapView.setStreetView(false);
	 
	    // setup button
	    Button bConfirm = (Button) findViewById(R.id.bConfirmRoute);
	    bConfirm.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// create new service request
				controllerSR.createServiceRequest();
				// add route info to service request
				controllerSR.getServiceRequest().setGpOrigin(new GeoPointInfo(gpOrigin));
				controllerSR.getServiceRequest().setGpDestination(new GeoPointInfo(gpDestination));
				
				Intent intent = new Intent(getApplicationContext(), UserTaxiLocationMap.class);
				startActivity(intent);
			}
		});
	    
	    userLocationHelper = new UserLocationHelper(getApplicationContext());
	    if(!Tools.isConnectionAvailable(getApplicationContext()))
	    	showBackAlertDialog(getString(R.string.failInternetConnectionNotFound));
	    else if(!userLocationHelper.setupListeners())
	    	showBackAlertDialog(getString(R.string.failLocationProviders));
	    else{
		    // setup timer to request user location
		    timer = new Timer();
		    timer.schedule(new GetUserLocationTask(), 6000);
		    
		    // show progress dialog
		    pdLookingLocation = ProgressDialog.show(this, "", getString(R.string.obtainingUserLocation), true);	
	    }
	}
	
	private class RouteSpecificationItemizedOverlay extends ItemizedOverlay<OverlayItem> {
		
		private ArrayList<OverlayItem> aOverlays;
		private int offsetX;
		private int offsetY;
		private int imageOffset_X;
		private int imageOffset_Y;
		private ImageView selectedImage;
		private Drawable marker;
		private Drawable destinationMarker;
		private OverlayItem selectedOverlay;
		
		public RouteSpecificationItemizedOverlay(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
			aOverlays= new ArrayList<OverlayItem>();
			
			marker = defaultMarker;
			selectedImage = null;
			
			// create origin overlay
			if(gpOrigin != null) {
				OverlayItem origin = new OverlayItem(gpOrigin, ORIGIN_ID, "");
				aOverlays.add(origin);
			}
			// create destination overlay
			if(gpDestination != null) {				
				OverlayItem destination = new OverlayItem(gpDestination, DESTINATION_ID, "");
				// setup marker of destination overlay
				destinationMarker = (Drawable)getResources().getDrawable(R.drawable.marker_dest);
				destinationMarker.setBounds(0, 0, destinationMarker.getIntrinsicWidth(), destinationMarker.getIntrinsicHeight());
				destination.setMarker(boundCenterBottom(destinationMarker));
				aOverlays.add(destination);
			}			
			populate();
			// update distance between geopoints
			updateDistanceLabel();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return aOverlays.get(i);
		}

		@Override
		public int size() {
			return aOverlays.size();
		}		
		
		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			final int action = event.getAction();
			int x = (int) event.getX();
			int y = (int) event.getY();
			boolean result = false;
			
			// check action type
			if(action == MotionEvent.ACTION_DOWN) {
				// getting touched overlay
				for(OverlayItem item:aOverlays) {
					Point screenPoint = new Point();
					// get screen pixels
					mapView.getProjection().toPixels(item.getPoint(), screenPoint);					
					// select the correct marker for hitTest
					Drawable targetMarker = null;
					if (item.getTitle().equals(ORIGIN_ID))
						targetMarker = marker;
					else
						targetMarker = destinationMarker;						
					// verify if it's the touched overlay
					if(hitTest(item, targetMarker, x-screenPoint.x, y-screenPoint.y)) {
						selectedOverlay = item;
						aOverlays.remove(item);
						populate();						
						// choosing correct image to drag
						if(selectedOverlay.getTitle().equals(ORIGIN_ID))
							selectedImage = (ImageView) findViewById(R.id.originImage);
						else
							selectedImage = (ImageView) findViewById(R.id.destinationImage);
						
						imageOffset_X = selectedImage.getDrawable().getIntrinsicWidth()/2;
						imageOffset_Y = selectedImage.getDrawable().getIntrinsicHeight();
						
						offsetX = 0;
						offsetY = 0;					
						
						// set image visible and update her position
						selectedImage.setVisibility(View.VISIBLE);
						updateImagePosition(screenPoint.x, screenPoint.y);
						
						offsetX = x - screenPoint.x;
						offsetY = y - screenPoint.y;
						
						result = true;
						break;
					}
				}				
			}
			else if (action == MotionEvent.ACTION_MOVE && selectedOverlay != null) {
				// get map boundaries
				int maxHeight = mapView.getHeight();
				int maxWidth = mapView.getWidth();
				// checking whether or not the overlay is off map boundaries
				if(x > maxWidth)
					x = maxWidth;
				if(y > maxHeight)
					y = maxHeight;
				updateImagePosition(x,y);
				result = true;
			}
			else if (action == MotionEvent.ACTION_UP && selectedOverlay != null) {
				// hide image
				selectedImage.setVisibility(View.GONE);
				// setup new geopoint on new map position
				GeoPoint gp = mapView.getProjection().fromPixels(x - offsetX, y - offsetY);
				OverlayItem oi = new OverlayItem(gp, selectedOverlay.getTitle(), selectedOverlay.getSnippet());
				// check whether the new geopoint will be the source or destination
				if(selectedOverlay.getTitle().equals(DESTINATION_ID)) {
					// set correct marker
					oi.setMarker(boundCenterBottom(destinationMarker));
					gpDestination = gp;
				}
				else
					gpOrigin = gp;				
				aOverlays.add(oi);
				populate();
				// no overlay selected
				selectedOverlay = null;
				// calculate and update the distance
				updateDistanceLabel();
				result = true;
			}			
 			return (result || super.onTouchEvent(event, mapView));
		}
		
		private void updateImagePosition(int x, int y) {
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) selectedImage.getLayoutParams();
			lp.setMargins(x - imageOffset_X - offsetX, y - imageOffset_Y - offsetY, 0, 0);
			selectedImage.setLayoutParams(lp);
		}
	}
	
	private class GetUserLocationTask extends TimerTask {		
		public void run() {
			// notify the handler
			handler.sendEmptyMessage(0);
		}
	}
	
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	// cancel progress dialog
        	pdLookingLocation.dismiss();
        	if(userLocationHelper.isLocationAvailable()) {
				// show layout
        		layout.setVisibility(View.VISIBLE);
        		
				Location l = userLocationHelper.getBestLocation();
				Double lat = l.getLatitude() * 1E6;
				Double lng = l.getLongitude() * 1E6;
				// default origin point = current user location
				gpOrigin = new GeoPoint(lat.intValue(), lng.intValue());
				// default destination point = displaced point of origin
			    gpDestination = new GeoPoint(gpOrigin.getLatitudeE6()+1150, gpOrigin.getLongitudeE6()+1150);
			    
			    MapController mc = mapView.getController();
			    // center to origin geopoint
			    mc.setCenter(gpOrigin);
			    mc.setZoom(ZOOM_LEVEL);
			    
			    Drawable defaultMarker = (Drawable) getResources().getDrawable(R.drawable.marker_ori);
			    defaultMarker.setBounds(0, 0, defaultMarker.getIntrinsicWidth(), defaultMarker.getIntrinsicHeight());
			    //add our custom overlays
			    mapView.getOverlays().add(new RouteSpecificationItemizedOverlay(defaultMarker));			    
			}
			else
				showBackAlertDialog(getString(R.string.failUserLocationNotFound));				
        }
	};
	
	private void updateDistanceLabel() {
		// calculating distance
		Double distance = calculateDistanceInMeters(gpOrigin, gpDestination);
		DecimalFormat df = new DecimalFormat("#######0.0#");
		// update label
		tvDistance.setText(" "+df.format(distance)+"m");
	}
	
	private double calculateDistanceInMeters(GeoPoint from, GeoPoint to) {
		// setup aux source location
		Location locationFrom = new Location("");  
		locationFrom.setLatitude(from.getLatitudeE6() / 1E6);  
		locationFrom.setLongitude(from.getLongitudeE6() / 1E6);  
		// setup aux destination location
		Location locationTo = new Location("");  
		locationTo.setLatitude(to.getLatitudeE6() / 1E6);  
		locationTo.setLongitude(to.getLongitudeE6() / 1E6);  
		return locationFrom.distanceTo(locationTo);
	}
	
	private void showBackAlertDialog(String textToShow) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(textToShow)
    	       .setCancelable(false)
    	       .setPositiveButton(getString(R.string.buttonBack), new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
   	       			// finish activity and go previous activity
   	       			UserTaxiRouteSpecification.super.onBackPressed();
	        	   }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
	}
}

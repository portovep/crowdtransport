package com.coctelmental.android.project1886.taxis;

import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coctelmental.android.project1886.R;
import com.coctelmental.android.project1886.common.GeoPointInfo;
import com.coctelmental.android.project1886.helpers.ServiceRequestsHelper;
import com.coctelmental.android.project1886.helpers.UserLocationHelper;
import com.coctelmental.android.project1886.main.Preferences;
import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.util.Tools;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class UserTaxiRouteSpecification extends MapActivity {
	
	private static final long LAST_LOCATION_MAX_TIME = 120000; // get last know location within last two minutes
	private static final float LAST_LOCATION_MIN_ACCURACY = 500; // accuracy in meters
	
	private static final String ORIGIN_ID = "ORIGIN";
	private static final String DESTINATION_ID = "DESTINATION";
	
	private static final int TIME_BETWEEN_EXECUTIONS = 2000; // 2s
	private static final int MAX_TIME_LOOKING_FOR_LOCATION = 60000; // 60s
	
	private static final int INIT_ZOOM_LEVEL = 17;
	private MapView mapView = null;	
	
	private GeoPoint gpOrigin = null;
	private GeoPoint gpDestination = null;
	
	private RelativeLayout backgroundLayout;
	private TextView tvDistance;
	private TextView tvTime;
	private ProgressDialog pdLookingLocation = null;
	
	private Timer timer = null;
	private UserLocationHelper userLocationHelper = null;
	
	private int nExecutions = 0;

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.user_taxi_route_specification);
	    
	    // get backgroundLayout and set invisible during setup
	    backgroundLayout = (RelativeLayout) findViewById(R.id.container);
	    backgroundLayout.setVisibility(ViewGroup.GONE);
	    
        // get distance label
        tvDistance = (TextView) findViewById(R.id.distance);
        // get route time label
        tvTime = (TextView) findViewById(R.id.time);
        
        // check user preferences
	    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    boolean satellite = sp.getBoolean(Preferences.PREF_USER_MAP_SATELLITE,
	    		Preferences.DEFAULT_USER_MAP_SATELLITE);
	    boolean zoomControls = sp.getBoolean(Preferences.PREF_USER_MAP_ZOOM_CONTROL,
	    		Preferences.DEFAULT_USER_MAP_ZOOM_CONTROL);
        
        // setup map configuration
	    mapView = (MapView) findViewById(R.id.mapTaxiRoute);	    
        mapView.setEnabled(true);
	    mapView.setBuiltInZoomControls(zoomControls);
        mapView.setSatellite(satellite);
        mapView.setTraffic(false);	
        mapView.setStreetView(false);
	 
	    // setup button
	    Button bConfirm = (Button) findViewById(R.id.bConfirmRoute);
	    bConfirm.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// create new service request
				ServiceRequestsHelper.createNewServiceRequest();
				// add route info to service request
				ServiceRequestsHelper.getServiceRequest().setGpFrom(new GeoPointInfo(gpOrigin));
				ServiceRequestsHelper.getServiceRequest().setGpTo(new GeoPointInfo(gpDestination));
				
				Intent intent = new Intent(getApplicationContext(), UserTaxiLocationMap.class);
				startActivity(intent);
			}
		});
	    
	    userLocationHelper = new UserLocationHelper(getApplicationContext());
	    // looking for a location which matches with given criteria
	    Location validLastKnowUserLocation = userLocationHelper.getValidLastKnownLocation(LAST_LOCATION_MAX_TIME,
	    		LAST_LOCATION_MIN_ACCURACY);
	    if(validLastKnowUserLocation != null) {
	    	setupOverlays(validLastKnowUserLocation);
	    } 
	    else if(!Tools.isConnectionAvailable(getApplicationContext()))
	    	showBackAlertDialog(getString(R.string.failInternetConnectionNotFound));
	    else if(!userLocationHelper.setupListeners())
	    	showBackAlertDialog(getString(R.string.failLocationProviders));
	    else{
		    // setup timer to request user location
		    timer = new Timer();
		    // check user location every 2 seconds
		    timer.scheduleAtFixedRate(new GetUserLocationTask(), 0, TIME_BETWEEN_EXECUTIONS);
		    
		    // show progress dialog
		    pdLookingLocation = ProgressDialog.show(this, "", getString(R.string.obtainingUserLocation), true);	
	    }
	}
	
	private class RouteDragAndDropItemizedOverlay extends ItemizedOverlay<OverlayItem> {
		
		private ArrayList<OverlayItem> aOverlays;
		private int offsetX;
		private int offsetY;
		private int imageOffset_X;
		private int imageOffset_Y;
		private ImageView selectedImage;
		private Drawable marker;
		private Drawable destinationMarker;
		private OverlayItem selectedOverlay;
		
		public RouteDragAndDropItemizedOverlay(Drawable defaultMarker) {
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
			// update route info labels
			new GetRouteInfoAsyncTask().execute();
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
				
				// update route info labels
				new GetRouteInfoAsyncTask().execute();
				
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
			nExecutions++;
			// notify the handler
			handler.sendEmptyMessage(0);
		}
	}
	
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        	if(userLocationHelper.isLocationAvailable()) {
				// stop timer
				timer.cancel();
				// remove pending messages
				handler.removeMessages(0);
				if (!isFinishing()) {
	            	// hide progress dialog
	            	pdLookingLocation.dismiss();
				}
				
				Location userLocation = userLocationHelper.getBestLocation();
				setupOverlays(userLocation);
							    
			}
			else if ((nExecutions * TIME_BETWEEN_EXECUTIONS) > MAX_TIME_LOOKING_FOR_LOCATION || !userLocationHelper.areProvidersEnabled()) {
				// stop timer
				timer.cancel();
				// remove pending messages
				handler.removeMessages(0);
				if (!isFinishing()) {
	            	// hide progress dialog
	            	pdLookingLocation.dismiss();
				}

            	// get cause
				String cause = "";
            	if (!userLocationHelper.areProvidersEnabled())
            		// providers have been deactivated
            		cause = getString(R.string.failLocationProviders);
            	else
            		// no location found
            		cause = getString(R.string.failUserLocationNotFound);
            	
            	if (!isFinishing())
            		showBackAlertDialog(cause);
			}				
        }
	};
	
	private void setupOverlays(Location userLocation) {
		// show backgroundLayout
		backgroundLayout.setVisibility(View.VISIBLE);
		
		// show toast with help
		Tools.buildToast(UserTaxiRouteSpecification.this, getString(R.string.helpDragAndDrop), 
				Gravity.CENTER, Toast.LENGTH_LONG).show();
		
		Double lat = userLocation.getLatitude() * 1E6;
		Double lng = userLocation.getLongitude() * 1E6;
		// default origin point = current user location
		gpOrigin = new GeoPoint(lat.intValue(), lng.intValue());
		// default destination point = displaced point of origin
	    gpDestination = new GeoPoint(gpOrigin.getLatitudeE6()+1150, gpOrigin.getLongitudeE6()+1150);
	    
	    MapController mc = mapView.getController();
	    // center to origin geopoint
	    mc.setCenter(gpOrigin);
	    mc.setZoom(INIT_ZOOM_LEVEL);
	    
	    Drawable defaultMarker = (Drawable) getResources().getDrawable(R.drawable.marker_ori);
	    defaultMarker.setBounds(0, 0, defaultMarker.getIntrinsicWidth(), defaultMarker.getIntrinsicHeight());
	    //add our custom overlays
	    mapView.getOverlays().add(new RouteDragAndDropItemizedOverlay(defaultMarker));
	}
	
	private void updateDistanceLabel(double distanceInMeters) {		
		// check user preferences
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String units = sp.getString(Preferences.PREF_DISTANCE_UNITS, Preferences.DEFAULT_DISTANCE_UNITS);
		
		if (units.equals("km"))
			// parse to kilometers
			distanceInMeters = distanceInMeters / 1000;
		// update label
		DecimalFormat df = new DecimalFormat("#######0.0#");
		tvDistance.setText(" " + df.format(distanceInMeters) + units);
	}
	
	private void updateRouteTimeLabel(String timeText) {		
		tvTime.setText("   " + timeText);
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

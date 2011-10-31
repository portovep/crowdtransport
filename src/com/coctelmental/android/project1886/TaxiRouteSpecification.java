package com.coctelmental.android.project1886;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class TaxiRouteSpecification extends MapActivity {

	public static final String LAT_ORIGIN = "LAT_ORIGIN";
	public static final String LONG_ORIGIN = "LONG_ORIGIN";
	public static final String LAT_DESTINATION = "LAT_DESTINATION";
	public static final String LONG_DESTINATION = "LONG_DESTINATION";
	
	private static final String ORIGIN_ID = "ORIGIN";
	private static final String DESTINATION_ID = "DESTINATION";
	
	private MapView mapView = null;
	private static final int ZOOM_LEVEL = 17;
	
	private GeoPoint gpOrigin = null;
	private GeoPoint gpDestination = null;

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.taxi_route_specification);
	 
	    
	    mapView = (MapView) findViewById(R.id.mapTaxiRoute);
	    // setup map configuration
	    mapView.setBuiltInZoomControls(false);
        mapView.setEnabled(true);
        mapView.setSatellite(true);
        mapView.setTraffic(false);	
        mapView.setStreetView(false);
	    
	    // create default source and destination points
	    gpOrigin = new GeoPoint(42871514, -8550864);
	    gpDestination = new GeoPoint(42873514, -8551864);
	    
	    MapController mc = mapView.getController();
	    // center to origin geopoint
	    mc.setCenter(gpOrigin);
	    mc.setZoom(ZOOM_LEVEL);
	    
	    Drawable defaultMarker = (Drawable) getResources().getDrawable(R.drawable.marker_ori);
	    defaultMarker.setBounds(0, 0, defaultMarker.getIntrinsicWidth(), defaultMarker.getIntrinsicHeight());
	    // add our custom overlays
	    mapView.getOverlays().add(new RouteSpecificationItemizedOverlay(defaultMarker));
	    
	    MyLocationOverlay mlo = new MyLocationOverlay(this, mapView);
	    mapView.getOverlays().add(mlo); 
	    
	    Button bConfirm = (Button) findViewById(R.id.bConfirmRoute);
	    bConfirm.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), TaxiAvailableMap.class);
				Bundle bundle = new Bundle();
				bundle.putInt(LAT_ORIGIN, gpOrigin.getLatitudeE6());
				bundle.putInt(LONG_ORIGIN, gpOrigin.getLongitudeE6());
				bundle.putInt(LAT_DESTINATION, gpDestination.getLatitudeE6());
				bundle.putInt(LONG_DESTINATION, gpDestination.getLongitudeE6());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
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
			super(boundCenter(defaultMarker));
			aOverlays= new ArrayList<OverlayItem>();
			
			marker = defaultMarker;
			selectedImage = null;
			
			// create origin overlay
			//gpOrigin = mapView.getProjection().fromPixels(673, 480);
			OverlayItem origin = new OverlayItem(gpOrigin, ORIGIN_ID, "");
			aOverlays.add(origin);
			
			// create destination overlay
			OverlayItem destination = new OverlayItem(gpDestination, DESTINATION_ID, "");
			// setup marker of destination overlay
			destinationMarker = (Drawable)getResources().getDrawable(R.drawable.marker_dest);
			destinationMarker.setBounds(0, 0, destinationMarker.getIntrinsicWidth(), destinationMarker.getIntrinsicHeight());
			destination.setMarker(boundCenter(destinationMarker));
			aOverlays.add(destination);
			
			populate();
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
			
			if(action == MotionEvent.ACTION_DOWN) {
				for(OverlayItem item:aOverlays) {
					Point screenPoint = new Point();			
					mapView.getProjection().toPixels(item.getPoint(), screenPoint);
					
					// select the correct marker
					Drawable targetMarker = null;
					if (item.getTitle().equals(ORIGIN_ID))
						targetMarker = marker;
					else
						targetMarker = destinationMarker;
							
					
					if(hitTest(item, targetMarker, x-screenPoint.x, y-screenPoint.y)) {
						selectedOverlay = item;
						aOverlays.remove(item);
						populate();
						
						// choosing correct image
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
				selectedImage.setVisibility(View.GONE);
				
				GeoPoint gp = mapView.getProjection().fromPixels(x - offsetX, y - offsetY);
				OverlayItem oi = new OverlayItem(gp, selectedOverlay.getTitle(), selectedOverlay.getSnippet());
				if(selectedOverlay.getTitle().equals(DESTINATION_ID)) {
					oi.setMarker(boundCenter(destinationMarker));
					gpDestination = gp;
				}
				else
					gpOrigin = gp;
				
				aOverlays.add(oi);
				populate();
				
				selectedOverlay = null;
				
				result = true;
			}
			
 			return (result || super.onTouchEvent(event, mapView));
		}

		/*
		@Override
		protected boolean onTap(int index) {
			OverlayItem item = aOverlays.get(index);
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setTitle(item.getTitle());
			dialog.setMessage(item.getSnippet());
			dialog.show();
			return true;
		} */
		
		private void updateImagePosition(int x, int y) {
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) selectedImage.getLayoutParams();
			lp.setMargins(x - imageOffset_X - offsetX, y - imageOffset_Y - offsetY, 0, 0);
			selectedImage.setLayoutParams(lp);
		}

	}
}

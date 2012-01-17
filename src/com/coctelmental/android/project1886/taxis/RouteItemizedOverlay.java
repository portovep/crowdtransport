package com.coctelmental.android.project1886.taxis;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class RouteItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> overlayList;
	
	public RouteItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		overlayList = new ArrayList<OverlayItem>();
		
		// call populate to fix a bug (Issue 2035)
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlayList.get(i);
	}

	@Override
	public int size() {
		return overlayList.size();
	}
	
	public void addOverlay(OverlayItem overlay) {
		overlayList.add(overlay);
	}
	
	public void addOverlay(OverlayItem overlay, Drawable  marker) {
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		overlay.setMarker(boundCenterBottom(marker));
		overlayList.add(overlay);
	}
	
	public void populateNow() {
		populate();
	}
}

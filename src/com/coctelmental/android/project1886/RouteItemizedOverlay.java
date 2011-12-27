package com.coctelmental.android.project1886;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class RouteItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> overlays;
	
	public RouteItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		overlays = new ArrayList<OverlayItem>();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}
	
	public void addOverlay(OverlayItem overlay) {
		overlays.add(overlay);
	}
	
	public void addOverlay(OverlayItem overlay, Drawable  marker) {
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		overlay.setMarker(boundCenterBottom(marker));
		overlays.add(overlay);
	}
	
	public void populateNow() {
		populate();
	}
}

package com.coctelmental.android.project1886.buses;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;



public class BusItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	
	private Context context;
	private ArrayList<OverlayItem> overlayList;

	
	public BusItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenter(defaultMarker));
		overlayList= new ArrayList<OverlayItem>();
		this.context=context;
	}
	
	public void addOverlay(OverlayItem overlay) {
		overlayList.add(overlay);
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlayList.get(i);
	}

	@Override
	public int size() {
		return overlayList.size();
	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = overlayList.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}
	
	public void populateNow() {
		populate();
	}

}

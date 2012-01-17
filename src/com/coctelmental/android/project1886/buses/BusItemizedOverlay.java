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
	
	private AlertDialog overlayDialog;

	
	public BusItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenter(defaultMarker));
		overlayList= new ArrayList<OverlayItem>();
		this.context=context;
		
		// call populate to fix a bug (Issue 2035)
		populate();
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
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(item.getTitle());
		dialogBuilder.setMessage(item.getSnippet());
		overlayDialog = dialogBuilder.create();
		overlayDialog.show();
		return true;
	}
	
	public void populateNow() {
		populate();
	}

	public void clear() {
		// clear array
		overlayList = new ArrayList<OverlayItem>();
		populate();
	}
	
	public boolean isOverlayDialogVisible() {
		if (overlayDialog != null && overlayDialog.isShowing())
			return true;
		return false;
	}
}

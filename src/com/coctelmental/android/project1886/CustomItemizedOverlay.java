package com.coctelmental.android.project1886;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;



public class CustomItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	
	private Context context;
	private ArrayList<OverlayItem> aOverlays;
	
	public CustomItemizedOverlay(Drawable defaultMarker) {
		super(boundCenter(defaultMarker));
		aOverlays= new ArrayList<OverlayItem>();
	}
	
	public CustomItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenter(defaultMarker));
		aOverlays= new ArrayList<OverlayItem>();
		this.context=context;
	}
	
	public void addOverlay(OverlayItem overlay)
	{
		aOverlays.add(overlay);
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
	protected boolean onTap(int index) {
		OverlayItem item = aOverlays.get(index);
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

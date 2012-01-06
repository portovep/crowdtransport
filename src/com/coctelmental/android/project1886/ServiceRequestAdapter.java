package com.coctelmental.android.project1886;

import java.util.List;

import com.coctelmental.android.project1886.model.ServiceRequestInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ServiceRequestAdapter extends ArrayAdapter<ServiceRequestInfo>{

	private int resource;
	
	public ServiceRequestAdapter(Context context, int resourceId, List<ServiceRequestInfo> items) {
		super(context, resourceId, items);
		resource = resourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout requestView;
		
		ServiceRequestInfo request = getItem(position);
		
		// get data
		String locationInfo = request.getUserID();
		String locationComment = request.getComment();
		
		if (convertView == null){
			// create new view
			requestView = new LinearLayout(getContext());
			String inflaterService = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(inflaterService);
			inflater.inflate(resource, requestView, true);
		}
		else {
			// update view
			requestView = (LinearLayout) convertView; 
		}
			
		// fill list's row
		TextView tvLocationInfo = (TextView) requestView.findViewById(R.id.adapterlocationInfo);
		TextView tvLocationComment= (TextView) requestView.findViewById(R.id.adapterlocationComment);
		
		tvLocationInfo.setText(locationInfo);
		if (locationComment != null)
			tvLocationComment.setText(locationComment);
		
		return requestView;
	}
	
}

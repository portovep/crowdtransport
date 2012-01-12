package com.coctelmental.android.project1886.taxis;

import java.util.List;

import com.coctelmental.android.project1886.R;
import com.coctelmental.android.project1886.common.ServiceRequestInfo;

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
		String requestAddressInfo = request.getAddressFrom();
		String requestComment = request.getComment();
		
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
			
		// fill list rows
		TextView tvRequestInfo = (TextView) requestView.findViewById(R.id.adapterlocationInfo);
		TextView tvRequestComment= (TextView) requestView.findViewById(R.id.adapterlocationComment);
		
		// check if addressFrom name is available
		if (requestAddressInfo != null && !requestAddressInfo.equals(""))
			// show address name
			tvRequestInfo.setText(requestAddressInfo);
		else
			// show default message
			tvRequestInfo.setText(R.string.unknownLocation);
		
		// check if clarification comment is available
		if (requestComment != null && !requestComment.equals(""))
			tvRequestComment.setText(requestComment);
		
		return requestView;
	}
	
}

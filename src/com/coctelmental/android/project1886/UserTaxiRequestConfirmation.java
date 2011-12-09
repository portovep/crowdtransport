package com.coctelmental.android.project1886;

import com.coctelmental.android.project1886.c2dm.C2DMRegistrationReceiver;
import com.coctelmental.android.project1886.logic.ControllerServiceRequests;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class UserTaxiRequestConfirmation extends Activity{
	
	public static final String TAXI_DRIVER_ID = "TAXI_DRIVER_ID";
	public static final String TAXI_DRIVER_UUID = "TAXI_DRIVER_UUID";
	public static final String TAXI_DRIVER_NAME = "TAXI_DRIVER_NAME";
	
	private static final Integer availableLifeTimes[] = {5, 10, 15};
	
	private EditText etClarificationComment;
	private Spinner spRequestLifeTime;
	
	private ControllerServiceRequests controllerSR;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_taxi_request_confirmation);
        
        controllerSR = new ControllerServiceRequests();
        
        String targetTaxiDriverID = null;
        String targetTaxiDriverName = null;
        String targetTaxiDriverUUID = null;
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	targetTaxiDriverID = extras.getString(TAXI_DRIVER_ID);
        	targetTaxiDriverUUID = extras.getString(TAXI_DRIVER_UUID);
        	targetTaxiDriverName = extras.getString(TAXI_DRIVER_NAME);
        }
        
        // fill taxi driver name label
        TextView tvTaxiDriverName = (TextView) findViewById(R.id.tvTaxiDriverName);
        tvTaxiDriverName.setText(targetTaxiDriverName);
        
    	// add taxi driver id to request info
    	controllerSR.getServiceRequest().setTaxiDriverID(targetTaxiDriverID);
    	// add taxi driver UUID to request info
    	controllerSR.getServiceRequest().setTaxiDriverUUID(targetTaxiDriverUUID);
    	
        // get edit text view
        etClarificationComment = (EditText) findViewById(R.id.etClarificationComment);
        
        // fill spinner with available lifetimes
        spRequestLifeTime = (Spinner) findViewById(R.id.spRequestLifeTime);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,
        					android.R.layout.simple_spinner_item, availableLifeTimes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRequestLifeTime.setAdapter(adapter);  	
        
        
        Button bConfirmation = (Button) findViewById(R.id.bConfirmRequest);
        bConfirmation.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// get clarification comment if there
				String clarificationComment = etClarificationComment.getText().toString();
				// add comment to service request info
				controllerSR.getServiceRequest().setClarificationComment(clarificationComment);
				// get request time life
				int selectedPosition = spRequestLifeTime.getSelectedItemPosition();
				// add selected time life to service request info
				controllerSR.getServiceRequest().setRequestLifeTime(availableLifeTimes[selectedPosition]);
				
		        Log.d("Service request info"," TaxiDriver ID: "
		        		+controllerSR.getServiceRequest().getTaxiDriverID()+
		        		"\n TaxiDriver UUID: "
		        		+controllerSR.getServiceRequest().getTaxiDriverUUID()+
		        		"\n gpORI: "+
		        		controllerSR.getServiceRequest().getGpOrigin().getLatitudeE6()+
		        		"\n gpDEST: "+
		        		controllerSR.getServiceRequest().getGpDestination().getLatitudeE6()+
		        		"\n commnet: "+
		        		controllerSR.getServiceRequest().getClarificationComment()+
		        		"\n lifetime: "+
		        		controllerSR.getServiceRequest().getRequestLifeTime());
		        
				// C2DM register to receive push notifications from web service
				C2DMRegistrationReceiver.register(getApplicationContext());
			}
		});
        	
    }
    


}

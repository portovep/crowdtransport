package com.coctelmental.android.project1886;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


public class UserTaxiAvailableMap extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.user_taxi_available_map);
	    
	    Bundle extras = getIntent().getExtras();
	    
	    int latO = extras.getInt(UserTaxiRouteSpecification.LAT_SOURCE);
	    int longO = extras.getInt(UserTaxiRouteSpecification.LONG_SOURCE);
	    int latD = extras.getInt(UserTaxiRouteSpecification.LAT_DESTINATION);
	    int longD = extras.getInt(UserTaxiRouteSpecification.LONG_DESTINATION);
	    
	    TextView tvO = (TextView) findViewById(R.id.originInfo);
	    TextView tvD = (TextView) findViewById(R.id.destinationInfo);
	    
	    tvO.append("Origin -> Lat: "+ latO + ", Long: "+ longO);
	    tvD.append("Destination -> Lat: "+ latD + ", Long: "+ longD);
	}

}

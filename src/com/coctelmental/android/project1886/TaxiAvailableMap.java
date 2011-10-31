package com.coctelmental.android.project1886;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


public class TaxiAvailableMap extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.taxi_available_map);
	    
	    Bundle extras = getIntent().getExtras();
	    
	    int latO = extras.getInt(TaxiRouteSpecification.LAT_ORIGIN);
	    int longO = extras.getInt(TaxiRouteSpecification.LONG_ORIGIN);
	    int latD = extras.getInt(TaxiRouteSpecification.LAT_DESTINATION);
	    int longD = extras.getInt(TaxiRouteSpecification.LONG_DESTINATION);
	    
	    TextView tvO = (TextView) findViewById(R.id.originInfo);
	    TextView tvD = (TextView) findViewById(R.id.destinationInfo);
	    
	    tvO.append("Origin -> Lat: "+ latO + ", Long: "+ longO);
	    tvD.append("Destination -> Lat: "+ latD + ", Long: "+ longD);
	}

}

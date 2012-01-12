package com.coctelmental.android.project1886.taxis;

import com.coctelmental.android.project1886.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TaxiDriverAttendingServiceRequest extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taxi_driver_attending_service_request);
		
		Button bServiceCompleted = (Button) findViewById(R.id.bServiceCompleted);
		bServiceCompleted.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// go taxi driver main activiy
				Intent intent = new Intent(getApplicationContext(), TaxiDriverMain.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});
		
	}

}

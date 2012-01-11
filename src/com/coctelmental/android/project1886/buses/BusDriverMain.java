package com.coctelmental.android.project1886.buses;

import com.coctelmental.android.project1886.MyApplication;
import com.coctelmental.android.project1886.R;
import com.coctelmental.android.project1886.TrackingService;
import com.coctelmental.android.project1886.helpers.UsersHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BusDriverMain extends Activity {
	
	private TextView tvProfileName;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_driver_main);
        
        tvProfileName = (TextView) findViewById(R.id.profileName);
        
        Button bStart = (Button) findViewById(R.id.buttonStart);
        bStart.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent;
				// if collaborationTrackingService is running
				if (MyApplication.getInstance().isServiceRunning(TrackingService.class.getName())) {
					intent = new Intent(getApplicationContext(), BusDriverInformationPanel.class);
				}
				else
					intent = new Intent(getApplicationContext(), BusDriverLineSelection.class);				
				startActivity(intent);
			}
		});
    }
	
	@Override
	public void onResume() {
		super.onResume();		
		// check if exist a user logged into the application 
        if(UsersHelper.existActiveUser()) {
        	String userName = UsersHelper.getActiveUser().getId();
        	// show user's name
        	tvProfileName.setText(getString(R.string.profile_welcome)+" "+userName);
        }
        else
        	// remove profile information from main panel
            tvProfileName.setText("");
	} 
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// set activity menu
		inflater.inflate(R.menu.main_activity_logged, menu);		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {		
		
		switch(item.getItemId()) {
			case R.id.menuExit:
				// logout and exit
				UsersHelper.logOut();
				// remove this activity from history
				finish();
				moveTaskToBack(true);
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}	
}

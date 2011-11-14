package com.coctelmental.android.project1886;

import com.coctelmental.android.project1886.logic.ControllerUsers;

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
	
	private ControllerUsers controllerU;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_driver_main);
        
        // get a instance of our user controller
        controllerU = new ControllerUsers();
        
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
        if(controllerU.existActiveUser()) {
        	String userName = controllerU.getActiveUser().getId();
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
		inflater.inflate(R.menu.main_activity_logged_menu, menu);		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {		
		
		switch(item.getItemId()) {
			case R.id.menuExit:
				// logout and exit
				controllerU.logOut();
				//moveTaskToBack(true);
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

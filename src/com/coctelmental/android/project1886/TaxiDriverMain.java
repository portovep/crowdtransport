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

public class TaxiDriverMain extends Activity {
	
	private TextView tvProfileName;
	
	private ControllerUsers controllerU;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_driver_main);
        
        // get a instance of our user controller
        controllerU = new ControllerUsers();
        
        tvProfileName = (TextView) findViewById(R.id.profileName);
        
        Button bStart = (Button) findViewById(R.id.buttonStart);
        bStart.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {				
				Intent intent = new Intent(getApplicationContext(), TaxiDriverInformationPanel.class);				
				startActivity(intent);
			}
		});
    }
	
	@Override
	public void onResume() {
		super.onResume();		
		// check if exist user logged into the application 
        if(controllerU.existActiveUser()) {
        	String userName = controllerU.getActiveUser().getId();
        	// show user name
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
				controllerU.logOut();
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

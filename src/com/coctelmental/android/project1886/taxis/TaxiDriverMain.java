package com.coctelmental.android.project1886.taxis;

import com.coctelmental.android.project1886.R;
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

public class TaxiDriverMain extends Activity {
	
	private TextView tvProfileName;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_driver_main);

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
	protected void onResume() {
		super.onResume();		
		// check if exist user logged into the application 
        if(UsersHelper.existActiveUser()) {
        	String userName = UsersHelper.getActiveUser().getId();
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

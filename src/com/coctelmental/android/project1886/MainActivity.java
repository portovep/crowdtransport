package com.coctelmental.android.project1886;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity{
	
	private static final int menuLogin = Menu.FIRST;
    private static final int menuRegistration = Menu.FIRST + 1;
    private static final int menuProfile = Menu.FIRST + 2;
    private static final int menuExit = Menu.FIRST + 3;
    
    private Button bBusLocation;
    private Button bCollaboration;
    private TextView tvProfileName;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_panel);
        
        tvProfileName = (TextView) findViewById(R.id.profileName);
        
        bBusLocation = (Button) findViewById(R.id.buttonBus);
        bBusLocation.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent;
				intent = new Intent(getApplicationContext(), BusLineSelection.class);
				startActivity(intent);				
			}
		});
        
        bCollaboration = (Button) findViewById(R.id.buttonCollaborate);
        bCollaboration.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				Intent intent;
				// if collaborationTrackingService is running
				if (isServiceRunning(CollaborationTrackingService.class.getName())) {
					intent = new Intent(getApplicationContext(), CollaborationStatusPanel.class);
					// specify flags for use current instance of target activity
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				}
				else 
					intent = new Intent(getApplicationContext(), CollaborationLineSelection.class);
				startActivity(intent);
			}
		});
    }
      
	@Override
	public void onResume() {
		super.onResume();		
		// check if exist a user logged into the application 
        if(MyApplication.getInstance().getActiveUser() != null)
        {
        	String userName = MyApplication.getInstance().getActiveUser().getId();
        	// show user's name
        	tvProfileName.setText(getString(R.string.profile_welcome)+" "+userName);
        }
        else
        	// remove profile information from main panel
            tvProfileName.setText("");
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// clear previous menu configuration
		menu.clear();

		if(MyApplication.getInstance().getActiveUser() == null)
		{
			menu.add(0, menuLogin, 0, R.string.option_menu_login);
			menu.add(0, menuRegistration, 0, R.string.option_menu_register);
		}
		else
		{
			menu.add(0, menuExit, 1, R.string.option_menu_exit);
			menu.add(0, menuProfile, 0, R.string.option_menu_profile);
		}			
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {		
		Intent intent;
		
		switch(item.getItemId())
		{
		case menuLogin:
			// Login panel launcher
			intent = new Intent(this, Authentication.class);
			startActivity(intent);
			break;
		case menuRegistration:
			// Registration panel launcher
			intent = new Intent(this, Registration.class);
			startActivity(intent);
			break;
		case menuExit:
			// logout and exit
			MyApplication.getInstance().setActiveUser(null);
			moveTaskToBack(true);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private boolean isServiceRunning(String serviceName) {
	    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceName.equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}
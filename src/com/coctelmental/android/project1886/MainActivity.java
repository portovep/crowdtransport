package com.coctelmental.android.project1886;

import com.coctelmental.android.project1886.buses.CollaboratorInformationPanel;
import com.coctelmental.android.project1886.buses.CollaboratorLineSelection;
import com.coctelmental.android.project1886.buses.UserBusLineSelection;
import com.coctelmental.android.project1886.helpers.UsersHelper;
import com.coctelmental.android.project1886.buses.BusTrackingService;
import com.coctelmental.android.project1886.taxis.UserTaxiRouteSpecification;
import com.coctelmental.android.project1886.users.Authentication;
import com.coctelmental.android.project1886.users.Registration;
import com.coctelmental.android.project1886.util.Tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{   

    private TextView tvProfileName;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        
        tvProfileName = (TextView) findViewById(R.id.profileName);        
    }
      
	@Override
	protected void onResume() {
		super.onResume();		
		// check if exist a user logged into the application 
        if(UsersHelper.existActiveUser()) {
        	String userName = UsersHelper.getActiveUser().getId();
        	// show user's name
        	tvProfileName.setText(getString(R.string.profile_welcome)+" "+userName);
        	tvProfileName.setVisibility(View.VISIBLE);
        }
        else {
        	// remove profile information from main panel
            tvProfileName.setText("");
            tvProfileName.setVisibility(View.GONE);
        }
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// clear previous menu configuration
		menu.clear();

		MenuInflater inflater = getMenuInflater();
		if(Tools.isServiceRunning(getApplicationContext(), BusTrackingService.class.getName())) {
			inflater.inflate(R.menu.main_activity_service_started, menu);
			if(!UsersHelper.existActiveUser())
				menu.removeItem(R.id.menuProfile);
		}
		else if(UsersHelper.existActiveUser())
			inflater.inflate(R.menu.main_activity_logged, menu);
		else
			inflater.inflate(R.menu.main_activity, menu);
		
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {		
		Intent intent;
		
		switch(item.getItemId()) {
			case R.id.menuLogin:
				// Login panel launcher
				intent = new Intent(getApplicationContext(), Authentication.class);
				startActivity(intent);
				break;
			case R.id.menuRegistration:
				// Registration panel launcher
				intent = new Intent(getApplicationContext(), Registration.class);
				startActivity(intent);
				break;
			case R.id.menuExit:
				// logout and exit
				UsersHelper.logOut();
				moveTaskToBack(true);
				break;
			case R.id.menuCollaboratorPanel:
				// goto collaborator panel
				intent = new Intent(getApplicationContext(), CollaboratorInformationPanel.class);
				startActivity(intent);
				break;
			case R.id.menufinishService:
				// finish location tracking service
				Intent i = new Intent(getApplicationContext(), BusTrackingService.class);
				stopService(i);
				break;				
			}
		return super.onMenuItemSelected(featureId, item);
	}
	
	/** Dashboard button listeners **/
	
	public void onBusAction(View view) {
		Intent intent = new Intent(getApplicationContext(), UserBusLineSelection.class);
		startActivity(intent);		
	}
	
	public void onCollaborationAction(View view) {
		Intent intent;
		// if collaborationTrackingService is running
		if (Tools.isServiceRunning(getApplicationContext(), BusTrackingService.class.getName())) {
			intent = new Intent(getApplicationContext(), CollaboratorInformationPanel.class);
		}
		else
			intent = new Intent(getApplicationContext(), CollaboratorLineSelection.class);
			
		startActivity(intent);
	}
	
	public void onTaxiAction(View view) {
		Intent intent = new Intent(getApplicationContext(), UserTaxiRouteSpecification.class);
		startActivity(intent);	
	}
	
	public void onPreferencesAction(View view) {
		// TO-DO
		Tools.buildToast(getApplicationContext(), "TO-DO", Gravity.CENTER, Toast.LENGTH_SHORT).show();
	}
	
	public void onAboutAction(View view) {
		Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.about_dialog);
		dialog.setTitle(R.string.titleAboutDialog);
		
        // fill versión label
        try {
        	String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        	TextView tvVersion = (TextView) dialog.findViewById(R.id.versionLabel);
        	tvVersion.append("(");
        	tvVersion.append(getString(R.string.versionLabelText));
        	tvVersion.append(" " + versionName);
        	tvVersion.append(")");
    	}catch (NameNotFoundException e) {
			Log.w("APP_VERSION", "Version name not found");
		}
		
		dialog.show();
		
	}
	
}
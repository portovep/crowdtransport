package com.coctelmental.android.project1886;


import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class Registration extends TabActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        
        Resources res= getResources(); // get resource manager
        TabHost tabHost= getTabHost(); 
        TabSpec	spec;				   
        Intent intent;					   
        
        // setup an intent to launch registration user activity
        intent = new Intent(this, UserRegistration.class);
        // setup a TabSpec to each tab and add text, icon and content
        spec = tabHost.newTabSpec("registrationUser")
		    		.setIndicator(
		        		getString(R.string.tabUser),
		        		res.getDrawable(R.drawable.statelist_tab_user))
		        	.setContent(intent);
        // adding new tab to our TabHost
        tabHost.addTab(spec);
        

        intent = new Intent(this, TaxiDriverRegistration.class);
        spec = tabHost.newTabSpec("registrationTaxi")
					.setIndicator(
			    		getString(R.string.tabTaxiDriver),
			    		res.getDrawable(R.drawable.statelist_tab_taxi))
			    	.setContent(intent);
        tabHost.addTab(spec); 

        
        intent = new Intent(this, BusDriverRegistration.class);
        spec = tabHost.newTabSpec("registrationBus")
					.setIndicator(
			    		getString(R.string.tabBusDriver),
			    		res.getDrawable(R.drawable.statelist_tab_bus))
			    	.setContent(intent);
        tabHost.addTab(spec); 
        
        // establishing default tab
        tabHost.setCurrentTab(0);         
    }		
}

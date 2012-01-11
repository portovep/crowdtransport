package com.coctelmental.android.project1886.users;

import com.coctelmental.android.project1886.R;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class Authentication extends TabActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication);
        
        // get resource manager
        Resources res= getResources(); 
        TabHost tabHost= getTabHost(); 
        TabSpec	spec;				   
        Intent intent;					   
        
        // setup an intent to launch user authentication activity
        // USER TAB
        intent = new Intent(this, UserAuthentication.class);
        // setup TabSpec for each tab adding text, icon and content
        spec = tabHost.newTabSpec("authenticationUser")
		    		.setIndicator(
		        		getString(R.string.tabUser),
		        		res.getDrawable(R.drawable.statelist_tab_user))
		        	.setContent(intent);
        // adding new tab to our TabHost
        tabHost.addTab(spec);
        
        // TAXI TAB
        intent = new Intent(this, TaxiDriverAuthentication.class);
        spec = tabHost.newTabSpec("authenticationTaxi")
					.setIndicator(
			    		getString(R.string.tabTaxiDriver),
			    		res.getDrawable(R.drawable.statelist_tab_taxi))
			    	.setContent(intent);
        tabHost.addTab(spec); 

        // BUS DRIVER TAB
        intent = new Intent(this, BusDriverAuthentication.class);
        spec = tabHost.newTabSpec("authenticationBus")
					.setIndicator(
			    		getString(R.string.tabBusDriver),
			    		res.getDrawable(R.drawable.statelist_tab_bus))
			    	.setContent(intent);
        tabHost.addTab(spec); 
        
        // set default tab
        tabHost.setCurrentTab(0);         
    }		
}

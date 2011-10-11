package com.coctelmental.android.project1886;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class CollaborationStatusPanel extends Activity {
	
	private String targetCity = null;
	private String targetLine = null;	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collaboration_status_panel);
		
	    // get data from intent
    	Bundle extras = getIntent().getExtras();	    
        targetCity = extras != null ? extras.getString(CollaborationLineSelection.TARGET_CITY) : null;
        targetLine = extras != null ? extras.getString(CollaborationLineSelection.TARGET_LINE) : null;
        
        // setup collaboration info
        TextView tvCity = (TextView) findViewById(R.id.collaborationInfoCity);
        tvCity.append(" "+targetCity);
        TextView tvLine = (TextView) findViewById(R.id.collaborationInfoLine);
        tvLine.append(" "+targetLine);
        
        Log.e("info", targetCity+targetLine);
	}
		

}

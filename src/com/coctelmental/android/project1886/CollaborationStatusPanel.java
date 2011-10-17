package com.coctelmental.android.project1886;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CollaborationStatusPanel extends Activity {
	
	public static final String TARGET_CITY = "TARGET_CITY";
	public static final String TARGET_LINE = "TARGET_LINE";
	
	private TextView tvCity;
	private TextView tvLine;
	private Button bFinishService;
	
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
        tvCity = (TextView) findViewById(R.id.collaborationInfoCity);
        tvCity.append(" "+targetCity);
        tvLine = (TextView) findViewById(R.id.collaborationInfoLine);
        tvLine.append(" "+targetLine);
        
        Log.e("info", targetCity+targetLine);
        
        // launch location tracking service
        Intent i = new Intent(this, CollaborationTrackingService.class);
        i.putExtra(TARGET_CITY, targetCity);
        i.putExtra(TARGET_LINE, targetLine);
        startService(i);
        
        // setup button to finish service
        bFinishService = (Button) findViewById(R.id.buttonFinishCollaboration);
        bFinishService.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(CollaborationStatusPanel.this, CollaborationTrackingService.class);
				stopService(i);
			}
		});
	}
		

}

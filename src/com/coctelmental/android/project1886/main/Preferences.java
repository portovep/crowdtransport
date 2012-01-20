package com.coctelmental.android.project1886.main;

import com.coctelmental.android.project1886.R;
import com.coctelmental.android.project1886.helpers.UsersHelper;
import com.coctelmental.android.project1886.model.Credentials;
import com.coctelmental.android.project1886.util.Tools;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.Toast;

public class Preferences extends PreferenceActivity {

	/*************************** GENERAL PREFS ***************************/
	public static final String PREF_CUSTOM_WELCOME_MESSAGE = "custom_welcome_message";
	public static final String PREF_BUTTON_CUSTOM_WELCOME_MESSAGE = "button_custom_welcome_message";
	public static final String PREF_DISTANCE_UNITS = "distance_units";
	public static final String DEFAULT_DISTANCE_UNITS= "km";
	
	/*************************** MAP PREFS ***************************/
	public static final String PREF_USER_MAP_SATELLITE = "user_maps_satellite";
	public static final boolean DEFAULT_USER_MAP_SATELLITE = false;
	public static final String PREF_USER_MAP_ZOOM_CONTROL = "user_maps_zoom_controls";
	public static final boolean DEFAULT_USER_MAP_ZOOM_CONTROL = false;
	public static final String PREF_USER_REFRESH_RATE = "user_refresh_rate";
	public static final String DEFAULT_USER_REFRESH_RATE = "10000"; // milliseconds
	
	/*************************** COLLABORATION PREFS ***************************/
	public static final String PREF_COL_NOTIFICATION_SOUND = "collaboration_notification_sound";
	public static final boolean DEFAULT_COL_NOTIFICATION_SOUND = true;
	public static final String PREF_COL_NOTIFICATION_VIB = "collaboration_notification_vibration";
	public static final boolean DEFAULT_COL_NOTIFICATION_VIB = false;

	/*************************** BUS DRIVER PREFS ***************************/
	public static final String PREF_BUS_NOTIFICATION_SOUND = "bus_driver_notification_sound";
	public static final boolean DEFAULT_BUS_NOTIFICATION_SOUND = true;
	public static final String PREF_BUS_NOTIFICATION_VIB = "bus_driver_notification_vibration";
	public static final boolean DEFAULT_BUS_NOTIFICATION_VIB = false;

	/*************************** TAXI DRIVER PREFS ***************************/
	public static final String PREF_TAXI_DISTANCE_UNITS = "taxi_driver_distance_units";
	public static final String DEFAULT_TAXI_DISTANCE_UNITS= "km";
	public static final String PREF_TAXI_MAP_SATELLITE = "taxi_driver_maps_satellite";
	public static final boolean DEFAULT_TAXI_MAP_SATELLITE = false;
	public static final String PREF_TAXI_MAP_ZOOM_CONTROL = "taxi_driver_maps_zoom_controls";
	public static final boolean DEFAULT_TAXI_MAP_ZOOM_CONTROL = false;
	public static final String PREF_TAXI_NEW_REQUEST_MESSAGE = "taxi_driver_request_message";
	public static final String PREF_TAXI_BUTTON_NEW_REQUEST_MESSAGE = "button_taxi_driver_request_message";
	public static final String PREF_TAXI_PLAY_DEST = "taxi_driver_play_destination";
	public static final boolean DEFAULT_TAXI_PLAY_DEST = true;
	public static final String PREF_TAXI_PLAY_COMMENT = "taxi_driver_play_comment";
	public static final boolean DEFAULT_TAXI_PLAY_COMMENT = true;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int userType = Credentials.TYPE_USER;		
		if (UsersHelper.existActiveUser()) {
			userType = UsersHelper.getActiveUser().getType();
		}

		// show preference panel based on user type
		if (userType == Credentials.TYPE_USER) {
			addPreferencesFromResource(R.layout.preferences);

			Preference prefCustomWelcomeMsg = findPreference(PREF_BUTTON_CUSTOM_WELCOME_MESSAGE);
			prefCustomWelcomeMsg.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				@Override
				public boolean onPreferenceClick(Preference preference) {
					String defaultMessage = getString(R.string.profile_welcome);
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					Editor editor = sp.edit();
					editor.putString(PREF_CUSTOM_WELCOME_MESSAGE, defaultMessage);
					editor.commit();
					Tools.buildToast(Preferences.this, getString(R.string.customWelcomeMsgRemoved), Gravity.CENTER, Toast.LENGTH_SHORT).show();
					return true;
				}
			});
		}
		else if (userType == Credentials.TYPE_BUS) {
			addPreferencesFromResource(R.layout.bus_driver_preferences);
		}
		else if (userType == Credentials.TYPE_TAXI) {
			addPreferencesFromResource(R.layout.taxi_driver_preferences);
			
			Preference prefNewRequestMsg = findPreference(PREF_TAXI_BUTTON_NEW_REQUEST_MESSAGE);
			prefNewRequestMsg.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				@Override
				public boolean onPreferenceClick(Preference preference) {
					String defaultMessage = getString(R.string.newIncomingRequestTTS);
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					Editor editor = sp.edit();
					editor.putString(PREF_TAXI_NEW_REQUEST_MESSAGE, defaultMessage);
					editor.commit();
					Tools.buildToast(Preferences.this, getString(R.string.customWelcomeMsgRemoved), Gravity.CENTER, Toast.LENGTH_SHORT).show();
					return true;
				}
			});
		}
		
		setContentView(R.layout.preferences_panel);
	}
	
}

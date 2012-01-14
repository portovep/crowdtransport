package com.coctelmental.android.project1886.main;

import com.coctelmental.android.project1886.R;
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

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);
		setContentView(R.layout.preferences_panel);
		
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
	
}

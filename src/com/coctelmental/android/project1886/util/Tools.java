package com.coctelmental.android.project1886.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;


public class Tools {
	
	private static final String TARGET_ALGORITHM = "SHA-1";
	private static final String DATE_FORMAT = "HH:mm:ss";
	
	public static String digestFromPassword(String password) {
		String hash = "";
		
		try{
			MessageDigest digester = MessageDigest.getInstance(TARGET_ALGORITHM);
			digester.update(password.getBytes());
			byte[] bytes=digester.digest();			
	        // making hex string from byte array
	        StringBuffer hexString = new StringBuffer();
	        for (int i=0; i<bytes.length; i++){
	            String h=Integer.toHexString(0xFF & bytes[i]);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        hash=hexString.toString();
			
		}catch(NoSuchAlgorithmException nsa)
		{
			Log.e(nsa.getClass().toString(), "Error trying to digest");
		}	
		return hash;
	}
	
	public static Toast buildToast(Context context, String message, int gravity, int duration) {
		// information panel
		Toast toast= Toast.makeText(context, message,
				duration);
		toast.setGravity(gravity, 0, 0);
		return toast;
	}
	
	public static String getTime(long milliseconds) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(new Date(milliseconds)).toString();
	}
	
	public static boolean isConnectionAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		// check connection status
		if(ni == null)
			// no connections available
			return false;
		if(!ni.isAvailable())
			return false;
		if(!ni.isConnected())
			return false;
		return true;
	}
	
	public static double calculateDistanceInMeters(GeoPoint from, GeoPoint to) {
		// setup aux source location
		Location locationFrom = new Location("");  
		locationFrom.setLatitude(from.getLatitudeE6() / 1E6);  
		locationFrom.setLongitude(from.getLongitudeE6() / 1E6);  
		// setup aux destination location
		Location locationTo = new Location("");  
		locationTo.setLatitude(to.getLatitudeE6() / 1E6);  
		locationTo.setLongitude(to.getLongitudeE6() / 1E6);  
		return locationFrom.distanceTo(locationTo);
	}

}

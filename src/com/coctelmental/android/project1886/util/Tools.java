package com.coctelmental.android.project1886.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


public class Tools {
	
	private static final String TARGET_ALGORITHM = "SHA-1";
	private static final String DATE_FORMAT = "hh:mm:ss";
	
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

}

package com.coctelmental.android.project1886.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;


public class Tools {
	
	private static final String TARGET_ALGORITHM = "SHA-1";
	
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


}

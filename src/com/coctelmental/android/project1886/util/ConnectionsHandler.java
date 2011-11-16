package com.coctelmental.android.project1886.util;


import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.coctelmental.android.project1886.model.ResultBundle;


public class ConnectionsHandler {
	
	private static final String SERVER_ADDRESS = "http://192.168.1.140:8085/UserManagerServer";

	public static ResultBundle get(String targetURL) {
		// create new result bundle and add default response code as 404
		ResultBundle result = new ResultBundle();
		ClientResource cr = new ClientResource(SERVER_ADDRESS+targetURL);
		try {
			Representation r = cr.get(MediaType.APPLICATION_JSON);
			// add response content to result bundle
			result.setContent(r.getText());
		}catch(Exception e){
			e.printStackTrace();
			result.setContent("");
		}
		// release connection resources
		cr.release();
		// get response code and add it in the bundle to specify the cause
		result.setResultCode(cr.getResponse().getStatus().getCode());
		return result;
	}
	
	public static int put(String targetURL, String jsonString) {
		ClientResource cr = new ClientResource(SERVER_ADDRESS+targetURL);
		// set default response status as 404
		int responseStatus = Status.CLIENT_ERROR_NOT_FOUND.getCode();
		try{
			JsonRepresentation jsonRepresentation = new JsonRepresentation(jsonString);
			cr.put(jsonRepresentation, MediaType.APPLICATION_JSON);
			responseStatus = cr.getResponse().getStatus().getCode();
		}catch(Exception e){
			e.printStackTrace();			
			responseStatus = cr.getResponse().getStatus().getCode();
		}
		// release connection resources
		cr.release();
		return responseStatus;	
	}
}

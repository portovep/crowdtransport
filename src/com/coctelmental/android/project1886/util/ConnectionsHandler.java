package com.coctelmental.android.project1886.util;


import org.restlet.Client;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.coctelmental.android.project1886.model.ResultBundle;


public class ConnectionsHandler {
	
	public static final String SERVER_ADDRESS = "http://54.228.210.184:8080/crowdtransport-ws/api";
	
	public static ResultBundle get(String targetURL) {
		// create new result bundle
		ResultBundle result = new ResultBundle();
		// setup connector
		Client client = new Client(Protocol.HTTP);
		client.setConnectTimeout(5000); // 5s
		ClientResource cr = new ClientResource(targetURL);
		// attach client connector
		cr.setNext(client);
		try {
			Representation r = cr.get(MediaType.APPLICATION_JSON);
			// add response content to result bundle
			result.setContent(r.getText());
		}catch(Exception e){
			e.printStackTrace();
			result.setContent("");
		}
		
		// close connection
		try {
			client.stop();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		// get response code and add it in the bundle to specify the cause
		result.setResultCode(cr.getResponse().getStatus().getCode());
		// release connection resources
		cr.release();
		return result;
	}
	
	public static int post(String targetURL, String jsonString) {
		// set default response status as 404
		int responseStatus = Status.CLIENT_ERROR_NOT_FOUND.getCode();
		// setup connector
		Client client = new Client(Protocol.HTTP);
		client.setConnectTimeout(10000); // 10s
		ClientResource cr = new ClientResource(targetURL);
		// attach client connector
		cr.setNext(client);
		try{
			JsonRepresentation jsonRepresentation = new JsonRepresentation(jsonString);
			cr.post(jsonRepresentation, MediaType.APPLICATION_JSON);
			
			responseStatus = cr.getResponse().getStatus().getCode();
		}catch(Exception e){
			e.printStackTrace();			
			responseStatus = cr.getResponse().getStatus().getCode();
		}
		
		// close connection
		try {
			client.stop();
		}catch (Exception e) {
			e.printStackTrace();
		}
		// release connection resources
		cr.release();
		return responseStatus;	
	}
	
	public static int put(String targetURL, String jsonString) {
		ClientResource cr = new ClientResource(targetURL);
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
	
	public static int delete(String targetURL) {
		ClientResource cr = new ClientResource(targetURL);
		// set default response status as 404
		int responseStatus = Status.CLIENT_ERROR_NOT_FOUND.getCode();
		try{
			cr.delete(MediaType.APPLICATION_JSON);
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

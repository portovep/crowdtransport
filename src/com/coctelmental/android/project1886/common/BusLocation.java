package com.coctelmental.android.project1886.common;



public class BusLocation {

	private String busLocationID;
	private GeoPointInfo geopoint;
	private long when;
	private int nCollaborators;
	
	public BusLocation (String id) {
		this.busLocationID = id;
	}

	public String getBusLocationID() {
		return busLocationID;
	}

	public void setBusLocationID(String busLocationID) {
		this.busLocationID = busLocationID;
	}
	
	public GeoPointInfo getGeopoint() {
		return geopoint;
	}

	public void setGeopoint(GeoPointInfo geopoint) {
		this.geopoint = geopoint;
	}
	
	public long getWhen() {
		return when;
	}

	public void setWhen(long when) {
		this.when = when;
	}
	
	public int getnCollaborators() {
		return nCollaborators;
	}

	public void setnCollaborators(int nCollaborators) {
		this.nCollaborators = nCollaborators;
	}
	
}

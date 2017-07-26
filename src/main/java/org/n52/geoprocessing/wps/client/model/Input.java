package org.n52.geoprocessing.wps.client.model;

public class Input extends WPSParameter {

	private int minOccurs;
	private int maxOccurs;
	
	public int getMinOccurs() {
		return minOccurs;
	}

	public void setMinOccurs(int minOccurs) {
		this.minOccurs = minOccurs;
	}

	public int getMaxOccurs() {
		return maxOccurs;
	}

	public void setMaxOccurs(int maxOccurs) {
		this.maxOccurs = maxOccurs;
	}
	
}

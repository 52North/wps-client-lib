package org.n52.geoprocessing.wps.client.model;

public class Range {

	private Object minimumValue;
	private Object maximumValue;
	
	public Object getMinimumValue() {
		return minimumValue;
	}
	
	public void setMinimumValue(Object minimumValue) {
		this.minimumValue = minimumValue;
	}
	
	public Object getMaximumValue() {
		return maximumValue;
	}
	
	public void setMaximumValue(Object maximumValue) {
		this.maximumValue = maximumValue;
	}
	
}

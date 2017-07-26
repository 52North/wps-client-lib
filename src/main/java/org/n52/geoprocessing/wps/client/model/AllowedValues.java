package org.n52.geoprocessing.wps.client.model;

public class AllowedValues {

	private Object allowedValue;
	private Range range;
	
	public Object getAllowedValue() {
		return allowedValue;
	}
	
	public void setAllowedValue(Object allowedValue) {
		this.allowedValue = allowedValue;
	}
	
	public Range getRange() {
		return range;
	}
	
	public void setRange(Range range) {
		this.range = range;
	}
	
}

package org.n52.geoprocessing.wps.client.model;

public class LiteralInput extends Input {

	private Object defaultValue;
	private AllowedValues allowedValues;
	private boolean anyValue;
	private String dataType;
	
	public Object getDefaultValue() {
		return defaultValue;
	}
	
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public AllowedValues getAllowedValues() {
		return allowedValues;
	}
	
	public void setAllowedValues(AllowedValues allowedValues) {
		this.allowedValues = allowedValues;
	}

	public boolean isAnyValue() {
		return anyValue;
	}

	public void setAnyValue(boolean anyValue) {
		this.anyValue = anyValue;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	
}

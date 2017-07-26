package org.n52.geoprocessing.wps.client.model;

import java.util.List;

public abstract class WPSParameter {

	private String id;
	private String title;
	private String abstrakt;
	private List<Format> formats;
	private Object value;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAbstract() {
		return abstrakt;
	}
	
	public void setAbstract(String abstrakt) {
		this.abstrakt = abstrakt;
	}
	
	public List<Format> getFormats() {
		return formats;
	}
	
	public void setFormats(List<Format> formats) {
		this.formats = formats;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
}
